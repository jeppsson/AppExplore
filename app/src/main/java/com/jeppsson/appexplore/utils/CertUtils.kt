package com.jeppsson.appexplore.utils

import android.content.pm.Signature
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat

internal object CertUtils {

    private val DATE_FORMAT = SimpleDateFormat.getDateTimeInstance()

    fun getCertificateStart(signatures: Array<Signature>): String {
        val certificate = getCertificate(signatures[0])
        return if (certificate != null) {
            DATE_FORMAT.format(certificate.notBefore)
        } else {
            ""
        }
    }

    fun getCertificateEnd(signatures: Array<Signature>): String {
        val certificate = getCertificate(signatures[0])
        return if (certificate != null) {
            DATE_FORMAT.format(certificate.notAfter)
        } else {
            ""
        }
    }

    private fun getCertificate(signature: Signature): X509Certificate? {
        /*
         * Get the X.509 certificate.
         */
        val rawCert = signature.toByteArray()
        val certStream = ByteArrayInputStream(rawCert)

        try {
            val certFactory = CertificateFactory.getInstance("X509")
            return certFactory.generateCertificate(certStream) as X509Certificate
        } catch (ignored: CertificateException) {
        }

        return null
    }

    fun getSignature(signatures: Array<Signature>): String {
        val sb = StringBuilder()

        for (signature in signatures) {
            val certificate = getCertificate(signature)
            if (certificate != null) {
                val principal = certificate.issuerX500Principal
                sb.append(principal.name.replace("(?<!\\\\),".toRegex(), "\n"))
                        .append('\n')
            }

            sb.append("Sha1Fingerprint=")
                    .append(computeFingerprint(signature.toByteArray(), "SHA1"))
                    .append('\n')
                    .append("Sha256Fingerprint=")
                    .append(computeFingerprint(signature.toByteArray(), "SHA256"))
                    .append('\n')
        }

        return sb.toString().trim { it <= ' ' }
    }

    private fun computeFingerprint(certRaw: ByteArray, algorithm: String): String {
        val strResult = StringBuilder()

        try {
            val md = MessageDigest.getInstance(algorithm)
            md.update(certRaw)
            for (b in md.digest()) {
                strResult.append(String.format("%02X:", b))
            }
        } catch (ex: NoSuchAlgorithmException) {
            ex.printStackTrace()
        }

        return strResult.toString().replace(":$".toRegex(), "")
    }
}