/*
 * Copyright © Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package at.bitfire.cert4android

import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.MessageDigest
import java.security.cert.X509Certificate
import java.util.*
import java.util.logging.Level
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object CertUtils {

    fun fingerprint(cert: X509Certificate, algorithm: String): String {
        val md = MessageDigest.getInstance(algorithm)
        return hexString(md.digest(cert.encoded))
    }

    fun getTrustManager(keyStore: KeyStore?): X509TrustManager? {
        try {
            val tmf = TrustManagerFactory.getInstance("X509")
            tmf.init(keyStore)
            tmf.trustManagers
                    .filterIsInstance<X509TrustManager>()
                    .forEach { return it }
        } catch(e: GeneralSecurityException) {
            Constants.log.log(Level.SEVERE, "Couldn't initialize trust manager", e)
        }
        return null
    }

    fun getTag(cert: X509Certificate): String {
        val str = StringBuilder()
        for (b in cert.signature)
            str.append(String.format(Locale.ROOT, "%02x", b))
        return str.toString()
    }

    fun hexString(data: ByteArray): String {
        val str = StringBuilder()
        for ((idx, b) in data.withIndex()) {
            if (idx != 0)
                str.append(':')
            str.append(String.format("%02x", b).uppercase(Locale.ROOT))
        }
        return str.toString()
    }

}
