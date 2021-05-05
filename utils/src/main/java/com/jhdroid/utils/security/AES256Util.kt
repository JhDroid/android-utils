package com.jhdroid.utils.security

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.jhdroid.utils.BuildConfig
import java.io.ByteArrayOutputStream
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/*
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

object AES256Util {
    private const val AndroidKeyStore = "AndroidKeyStore"
    private const val AES_MODE_GCM = "AES/GCM/NoPadding"
    private const val AES_MODE_CBC = "AES/CBC/PKCS5Padding"
    private const val RSA_MODE = "RSA/ECB/PKCS1Padding"
    private const val KEY_ALIAS = BuildConfig.LIBRARY_PACKAGE_NAME
    private var iv = "D64F5141A133"
    private var keySpec: Key? = null
    private lateinit var keyStore: KeyStore

    private lateinit var context: Context

    fun initialize(context: Context) {
        try {
            this.context = context

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    keyStore = KeyStore.getInstance(AndroidKeyStore)
                    keyStore.load(null)

                    if (!keyStore.containsAlias(KEY_ALIAS)) {
                        val keyGenerator = KeyGenerator.getInstance(
                            KeyProperties.KEY_ALGORITHM_AES,
                            AndroidKeyStore
                        )
                        keyGenerator.init(
                            KeyGenParameterSpec.Builder(
                                KEY_ALIAS,
                                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                            )
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                .setRandomizedEncryptionRequired(false)
                                .build()
                        )
                        keyGenerator.generateKey()
                    }
                }

                else -> {
                    val keyGenerator = KeyGenerator.getInstance("AES")
                    keyGenerator.init(256)
                    val secretKey = keyGenerator.generateKey().toString()

                    iv = secretKey.substring(0, 16)

                    val keyBytes = ByteArray(16)
                    val b = secretKey.toByteArray(charset("UTF-8"))
                    var len = b.size
                    if (len > keyBytes.size) len = keyBytes.size
                    System.arraycopy(b, 0, keyBytes, 0, len)

                    this.keySpec = SecretKeySpec(keyBytes, "AES")
                }
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
    }

    // 암호화
    @Throws(Exception::class)
    fun aesEncode(str: String): String {
        if (!::context.isInitialized) throw RuntimeException("AES256Util Uninitialized")

        val c: Cipher

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                c = Cipher.getInstance(AES_MODE_GCM)
                c.init(
                    Cipher.ENCRYPT_MODE, keyStore.getKey(KEY_ALIAS, null),
                    GCMParameterSpec(128, iv.toByteArray())
                )
            }

            else -> {
                c = Cipher.getInstance(AES_MODE_CBC)
                c.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(iv.toByteArray()))
            }
        }

        val encrypted = c.doFinal(str.toByteArray(charset("UTF-8")))
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    //복호화
    @Throws(java.lang.Exception::class)
    fun aesDecode(str: String): String {
        if (!::context.isInitialized) throw RuntimeException("AES256Util Uninitialized")

        val c: Cipher

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                c = Cipher.getInstance(AES_MODE_GCM)
                c.init(
                    Cipher.DECRYPT_MODE, keyStore.getKey(KEY_ALIAS, null),
                    GCMParameterSpec(128, iv.toByteArray(charset("UTF-8")))
                )
            }

            else -> {
                c = Cipher.getInstance(AES_MODE_CBC)
                c.init(
                    Cipher.DECRYPT_MODE,
                    keySpec,
                    IvParameterSpec(iv.toByteArray(charset("UTF-8")))
                )
            }
        }

        val byteStr = Base64.decode(str.toByteArray(), Base64.DEFAULT)

        return String(c.doFinal(byteStr), charset("UTF-8"))
    }

    @Throws(Exception::class)
    private fun rsaEncrypt(secret: ByteArray): ByteArray {
        if (!::context.isInitialized) throw RuntimeException("AES256Util Uninitialized")

        val privateKeyEntry =
            keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry

        val inputCipher = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL")
        inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.certificate.publicKey)

        val outputStream = ByteArrayOutputStream()
        val cipherOutputStream = CipherOutputStream(outputStream, inputCipher)
        cipherOutputStream.write(secret)
        cipherOutputStream.close()

        return outputStream.toByteArray()
    }
}
