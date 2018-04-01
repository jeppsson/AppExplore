package com.jeppsson.appexplore;

import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.security.auth.x500.X500Principal;

final class Utils {

    private static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateTimeInstance();

    static String arrayToLines(String[] strings, String defaultString) {
        return arrayToLines(strings, null, defaultString);
    }

    static String arrayToLines(String[] strings, String prefix, String defaultString) {
        if (prefix == null) {
            prefix = "";
        }

        if (strings == null) {
            return defaultString;
        }

        StringBuilder sb = new StringBuilder();

        for (String s : strings) {
            sb.append(prefix).append(s).append('\n');
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        } else {
            return defaultString;
        }

        return sb.toString();
    }

    static String getCertificateStart(Signature[] signatures, String defaultString) {
        X509Certificate certificate = getCertificate(signatures);
        if (certificate != null) {
            return DATE_FORMAT.format(certificate.getNotBefore());
        } else {
            return defaultString;
        }
    }

    static String getCertificateEnd(Signature[] signatures, String defaultString) {
        X509Certificate certificate = getCertificate(signatures);
        if (certificate != null) {
            return DATE_FORMAT.format(certificate.getNotAfter());
        } else {
            return defaultString;
        }
    }

    private static X509Certificate getCertificate(Signature[] signatures) {
        for (Signature signature : signatures) {
            /*
             * Get the X.509 certificate.
             */
            byte[] rawCert = signature.toByteArray();
            InputStream certStream = new ByteArrayInputStream(rawCert);

            try {
                CertificateFactory certFactory = CertificateFactory.getInstance("X509");
                return (X509Certificate) certFactory.generateCertificate(certStream);
            } catch (CertificateException ignored) {
            }
        }

        return null;
    }

    static String getSignature(Signature[] signatures, String defaultString) {
        X509Certificate certificate = getCertificate(signatures);
        if (certificate != null) {
            X500Principal principal = certificate.getIssuerX500Principal();
            return principal.getName().replace(',', '\n');
        } else {
            return defaultString;
        }
    }
}
