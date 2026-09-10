package org.saudigitus.climasaude.platform

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import org.saudigitus.climasaude.domain.model.Credentials
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class AndroidSecureCredentials(context: Context) : SecureCredentials {
    private val preferences =
        context.getSharedPreferences("climasaude_secure", Context.MODE_PRIVATE)
    private val alias = "climasaude_dhis2_credentials"

    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val existing = store.getKey(alias, null)
        if (existing is SecretKey) return existing
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        return generator.generateKey()
    }

    override fun save(credentials: Credentials) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        val plaintext =
            (credentials.username + "\n" + credentials.password).toByteArray(Charsets.UTF_8)
        val encrypted = cipher.doFinal(plaintext)
        preferences.edit()
            .putString("iv", Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .putString("data", Base64.encodeToString(encrypted, Base64.NO_WRAP))
            .apply()
    }

    override fun read(): Credentials? = runCatching {
        val iv = Base64.decode(preferences.getString("iv", null) ?: return null, Base64.NO_WRAP)
        val data = Base64.decode(preferences.getString("data", null) ?: return null, Base64.NO_WRAP)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, iv))
        val decoded = cipher.doFinal(data).toString(Charsets.UTF_8)
        val separator = decoded.indexOf('\n')
        if (separator < 1) null else Credentials(
            decoded.substring(0, separator),
            decoded.substring(separator + 1)
        )
    }.getOrNull()

    override fun clear() {
        preferences.edit().clear().apply()
        KeyStore.getInstance("AndroidKeyStore").apply { load(null); deleteEntry(alias) }
    }
}
