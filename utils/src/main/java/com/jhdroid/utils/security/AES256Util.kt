package com.jhdroid.utils.security

//import android.content.res.Resources
//import android.security.KeyPairGeneratorSpec
//import java.io.ByteArrayInputStream
//import java.math.BigInteger
//import java.security.KeyPairGenerator
//import java.security.SecureRandom
//import java.util.*
//import javax.crypto.CipherInputStream
//import javax.security.auth.x500.X500Principal
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

//                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> {
//                    keyStore = KeyStore.getInstance(AndroidKeyStore)
//                    keyStore.load(null)
//
//                    if (!keyStore.containsAlias(KEY_ALIAS)) {
//                        val defaultLocale = Locale.getDefault()
//                        setLocale(Locale.ENGLISH)
//
//                        val start = Calendar.getInstance()
//                        val end = Calendar.getInstance()
//                        end.add(Calendar.YEAR, 30)
//
//                        val spec = KeyPairGeneratorSpec.Builder(context)
//                            .setAlias(KEY_ALIAS)
//                            .setSubject(X500Principal("CN=$KEY_ALIAS"))
//                            .setSerialNumber(BigInteger.TEN)
//                            .setStartDate(start.time)
//                            .setEndDate(end.time)
//                            .build()
//
//                        val kpg = KeyPairGenerator.getInstance("RSA", AndroidKeyStore)
//                        kpg.initialize(spec)
//                        kpg.generateKeyPair()
//
//                        setLocale(defaultLocale)
//                    }
//                }

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

//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> {
//                c = Cipher.getInstance(AES_MODE_GCM, "BC")
//                c.init(Cipher.ENCRYPT_MODE, getSecretKey(), IvParameterSpec(iv.toByteArray()))
//            }

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

//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> {
//                c = Cipher.getInstance(AES_MODE_GCM, "BC")
//                c.init(Cipher.DECRYPT_MODE, getSecretKey(), IvParameterSpec(iv.toByteArray()))
//            }

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

//    @Throws(Exception::class)
//    private fun getSecretKey(): Key? {
//        val prefs = context.getSharedPreferences(
//            context.packageName, Context.MODE_PRIVATE
//        )
//
//        var encryptedKeyB64 = prefs.getString("keyAesKey", null)
//        if (encryptedKeyB64 == null) {
//            val key = ByteArray(16)
//            val secureRandom = SecureRandom()
//            secureRandom.nextBytes(key)
//            val encryptedKey: ByteArray = rsaEncrypt(key)
//            encryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT)
//            if (encryptedKeyB64 != null && encryptedKeyB64.isNotEmpty()) {
//                val editor = prefs.edit()
//                editor.putString("keyAesKey", encryptedKeyB64)
//                editor.apply()
//            }
//        }
//
//        val encryptedKey = Base64.decode(encryptedKeyB64, Base64.DEFAULT)
//        val key = rsaDecrypt(encryptedKey)
//        return SecretKeySpec(key, "AES")
//    }
//
//    @Throws(Exception::class)
//    private fun rsaDecrypt(encrypted: ByteArray): ByteArray {
//        val privateKeyEntry =
//            keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
//
//        val output = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL")
//        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)
//
//        val cipherInputStream = CipherInputStream(
//            ByteArrayInputStream(encrypted), output
//        )
//        val values = ArrayList<Byte>()
//        var nextByte: Int
//        while (cipherInputStream.read().also { nextByte = it } != -1) {
//            values.add(nextByte.toByte())
//        }
//        val bytes = ByteArray(values.size)
//        for (i in bytes.indices) {
//            bytes[i] = values[i]
//        }
//
//        return bytes
//    }

//    private fun setLocale(locale: Locale) {
//        Locale.setDefault(locale)
//        val resources: Resources = OnAvApplication.getContext().resources
//        val config = resources.configuration
//        config.locale = locale
//        resources.updateConfiguration(config, resources.displayMetrics)
//    }
}
