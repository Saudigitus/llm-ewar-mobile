package org.saudigitus.climasaude.ai

import android.content.Context
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.saudigitus.climasaude.domain.ai.TriageModel
import java.io.File

class AndroidTriageModel(private val context: Context) : TriageModel {
    private val mutex = Mutex()

    override suspend fun complete(prompt: String): String? = mutex.withLock {
        withContext(Dispatchers.IO) {
            val model = modelFile() ?: return@withContext null
            Engine(
                EngineConfig(
                    modelPath = model.absolutePath,
                    backend = Backend.CPU()
                )
            ).use { engine ->
                engine.initialize()
                engine.createConversation().use { conversation ->
                    conversation.sendMessage(prompt).contents.contents
                        .filterIsInstance<Content.Text>()
                        .joinToString("") { it.text }
                }
            }
        }
    }

    private fun modelFile(): File? {
        val asset = "models/climasaude.litertlm"
        val destination = File(context.filesDir, asset)
        val versionFile = File(destination.parentFile, "asset-version")
        val installedVersion = context.packageManager
            .getPackageInfo(context.packageName, 0).lastUpdateTime.toString()
        if (destination.isFile && destination.length() > 0 &&
            versionFile.takeIf { it.isFile }?.readText() == installedVersion
        ) return destination
        val available = context.assets.list("models")?.contains("climasaude.litertlm") == true
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
}
