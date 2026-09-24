package org.saudigitus.climasaude.ai

import android.content.Context
import android.util.Log
import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.Message
import com.google.ai.edge.litertlm.MessageCallback
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.saudigitus.climasaude.domain.ai.TriageModel
import java.io.File
import kotlin.time.Duration.Companion.milliseconds

class AndroidTriageModel(private val context: Context) : TriageModel {
    private val initMutex = Mutex()
    private val generationMutex = Mutex()

    @Volatile
    private var engine: Engine? = null

    suspend fun init(): Boolean = withContext(Dispatchers.IO) {
        if (engine != null) return@withContext true
        initMutex.withLock {
            if (engine != null) return@withLock true
            try {
                val model = modelFile() ?: run {
                    Log.w(TAG, "Model asset not found")
                    return@withLock false
                }
                val created = Engine(EngineConfig(modelPath = model.absolutePath))
                try {
                    created.initialize()
                } catch (error: Throwable) {
                    runCatching { created.close() }
                    throw error
                }
                engine = created
                Log.i(TAG, "Local model ready")
                true
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Throwable) {
                Log.w(TAG, "Local model failed to initialise", error)
                false
            }
        }
    }

    override suspend fun complete(prompt: String): String? {
        var text = ""
        withTimeoutOrNull(GENERATION_TIMEOUT_MS.milliseconds) {
            stream(prompt).collect { text = it }
        } ?: Log.w(TAG, "Local model stopped after the time limit")
        return text.takeIf { it.isNotBlank() }
    }

    override fun stream(prompt: String): Flow<String> = callbackFlow {
        val engine = if (init()) engine else null
        if (engine == null) {
            close()
            return@callbackFlow
        }
        generationMutex.withLock {
            val conversation = engine.createConversation()
            try {
                val text = StringBuilder()
                conversation.sendMessageAsync(
                    prompt,
                    object : MessageCallback {
                        override fun onMessage(message: Message) {
                            val chunk = message.contents.contents
                                .filterIsInstance<Content.Text>()
                                .joinToString("") { it.text }
                            if (chunk.isEmpty()) return
                            text.append(chunk)
                            trySend(text.toString())
                        }

                        override fun onDone() {
                            close()
                        }

                        override fun onError(throwable: Throwable) {
                            close(throwable)
                        }
                    },
                    emptyMap()
                )
                awaitClose { runCatching { conversation.cancelProcess() } }
            } finally {
                runCatching { conversation.close() }
            }
        }
    }
        .conflate()
        .catch { error -> Log.w(TAG, "Local model failed", error) }
        .flowOn(Dispatchers.IO)

    private fun modelFile(): File? {
        val asset = "models/climasaude.litertlm"
        val destination = File(context.filesDir, asset)
        val versionFile = File(destination.parentFile, "asset-version")
        val installedVersion = context.packageManager
            .getPackageInfo(context.packageName, 0).lastUpdateTime.toString()
        if (destination.isFile && destination.length() > 0 &&
            versionFile.takeIf { it.isFile }?.readText() == installedVersion
        ) return destination
        val available = context.assets.list("models")?.contains("models/climasaude.litertlm") == true
        if (!available) return null
        destination.parentFile?.mkdirs()
        val temporary = File(destination.parentFile, "climasaude.litertlm.part")
        try {
            context.assets.open(asset).use { input ->
                temporary.outputStream().use { output -> input.copyTo(output) }
            }
            if (temporary.length() == 0L) return null
            if (destination.exists()) destination.delete()
            if (!temporary.renameTo(destination)) return null
            versionFile.writeText(installedVersion)
            return destination
        } finally {
            temporary.delete()
        }
    }

    private companion object {
        const val TAG = "AndroidTriageModel"
        const val GENERATION_TIMEOUT_MS = 90_000L
    }
}
