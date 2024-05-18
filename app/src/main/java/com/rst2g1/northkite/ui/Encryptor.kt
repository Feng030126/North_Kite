package com.rst2g1.northkite.ui

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import android.util.Base64

object Encryptor {
    private const val algorithm = "AES"
    private const val transformation = "AES/CBC/PKCS5Padding"
    private const val key = "ThisIsASecretKey" // 16-byte key for AES-128
    private const val iv = "ThisIsAnIVVector"  // 16-byte IV for AES

    private fun getKey(): Key {
        return SecretKeySpec(key.toByteArray(), algorithm)
    }

    private fun getIvSpec(): IvParameterSpec {
        return IvParameterSpec(iv.toByteArray())
    }

    fun encrypt(input: String): String {
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, getKey(), getIvSpec())
        val encryptedBytes = cipher.doFinal(input.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decrypt(encrypted: String): String {
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), getIvSpec())
        val decodedBytes = Base64.decode(encrypted, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes)
    }
}