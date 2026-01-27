package com.topwise.toptool.impl;



import com.topwise.toptool.api.utils.AppLog;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


/**
 * used for SSL client, and HHTPS client
 *
 */
class CommSslSocketFactoryExt extends SSLSocketFactory {
    private static final String TAG = CommSslSocketFactoryExt.class.getSimpleName();
    SSLContext sslContext = SSLContext.getInstance("TLS");

    // public SslSocketFactoryExt(KeyStore truststore) throws NoSuchAlgorithmException,
    // KeyManagementException,
    // KeyStoreException,
    // UnrecoverableKeyException {
    // super(truststore);
    //
    // TrustManagerFactory trustManager = TrustManagerFactory.getInstance("X509");
    //
    // if (truststore == null) {
    // sslContext.init(null, trustAllCerts(), null);
    // } else {
    // trustManager.init(truststore);
    // sslContext.init(null, trustManager.getTrustManagers(), null);
    // }
    // }
    //
    // public SslSocketFactoryExt(KeyStore keyStore, String strKeyStorePassWord) throws NoSuchAlgorithmException,
    // KeyManagementException,
    // KeyStoreException,
    // UnrecoverableKeyException {
    // super(keyStore, strKeyStorePassWord);
    //
    // if (keyStore == null) {
    // sslContext.init(null, trustAllCerts(), null);
    // } else {
    // KeyManagerFactory keyManager = KeyManagerFactory.getInstance("X509");
    // keyManager.init(keyStore, (strKeyStorePassWord != null) ? strKeyStorePassWord.toCharArray() : null);
    // sslContext.init(keyManager.getKeyManagers(), null, null);
    // }
    // }

    public CommSslSocketFactoryExt(KeyStore keyStore, String strKeyStorePassWord, KeyStore truststore) throws NoSuchAlgorithmException,
            KeyManagementException,
            KeyStoreException,
            UnrecoverableKeyException {
        super(keyStore, strKeyStorePassWord, truststore);

        /**
         * ignore CN (Common Name)
         */
        setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        KeyManagerFactory keyManager = KeyManagerFactory.getInstance("X509");
        TrustManagerFactory trustManager = TrustManagerFactory.getInstance("X509");

        boolean isKm = false;
        boolean isTm = false;
        if ((keyStore == null) && (truststore == null)) {
            AppLog.w(TAG, "key store is null, assuming trust all!");
            // trust all certs!!!
            sslContext.init(null, trustAllCerts(), null);
        } else {
            if (keyStore != null) {
                keyManager.init(keyStore, (strKeyStorePassWord != null) ? strKeyStorePassWord.toCharArray() : null);
                isKm = true;
            }
            if (truststore != null) {
                trustManager.init(truststore);
                isTm = true;
            }
            sslContext.init(isKm ? keyManager.getKeyManagers() : null, isTm ? trustManager.getTrustManagers() : null,
                    null);
        }
    }

    // @Override
    // public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
    // UnknownHostException {
    // return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    // }
    //
    // @Override
    // public Socket createSocket() throws IOException {
    // return sslContext.getSocketFactory().createSocket();
    // }

    /**
     * this is special for SSL client
     * @return
     * @throws IOException
     */
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }

    private TrustManager[] trustAllCerts() {
        return new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        } };
    }
}
