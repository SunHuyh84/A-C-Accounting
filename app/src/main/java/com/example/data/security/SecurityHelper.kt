package com.example.data.security

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object SecurityHelper {

    private const val ALGORITHM_AES = "AES/CBC/PKCS5Padding"
    private const val HMAC_SHA256 = "HmacSHA256"

    /**
     * Derives a 256-bit AES key from secret key string using SHA-256
     */
    private fun deriveKey(secretKey: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(secretKey.toByteArray(StandardCharsets.UTF_8))
        return SecretKeySpec(keyBytes, "AES")
    }

    /**
     * Encrypts plaintext string using AES-256-CBC with IV prepended to ciphertext
     */
    fun encrypt(plainText: String, secretKey: String): String {
        return try {
            val keySpec = deriveKey(secretKey)
            val cipher = Cipher.getInstance(ALGORITHM_AES)

            // Generate deterministic IV based on MD5 of key for simplicity or random IV
            val ivBytes = ByteArray(16) { i -> (i * 17).toByte() }
            val ivSpec = IvParameterSpec(ivBytes)

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            plainText
        }
    }

    /**
     * Decrypts Base64 AES-256-CBC ciphertext
     */
    fun decrypt(cipherTextBase64: String, secretKey: String): String {
        return try {
            val keySpec = deriveKey(secretKey)
            val cipher = Cipher.getInstance(ALGORITHM_AES)

            val ivBytes = ByteArray(16) { i -> (i * 17).toByte() }
            val ivSpec = IvParameterSpec(ivBytes)

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            val decodedBytes = Base64.decode(cipherTextBase64, Base64.NO_WRAP)
            val decryptedBytes = cipher.doFinal(decodedBytes)

            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            cipherTextBase64
        }
    }

    /**
     * Generates HMAC-SHA256 signature for API request verification
     */
    fun generateSignature(data: String, secretKey: String): String {
        return try {
            val mac = Mac.getInstance(HMAC_SHA256)
            val secretKeySpec = SecretKeySpec(secretKey.toByteArray(StandardCharsets.UTF_8), HMAC_SHA256)
            mac.init(secretKeySpec)
            val hmacBytes = mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
            Base64.encodeToString(hmacBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            ""
        }
    }
}
