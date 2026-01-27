package com.topwise.toptool.impl;

import com.topwise.toptool.api.comm.ISslKeyStore;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class CommSslKeyStore implements ISslKeyStore {

    private KeyStore trustKeyStore = null;
    private KeyStore keyStore = null;
    private String keyStorePassword = null;


    @Override
    public boolean setTrustStore(InputStream x509CertIs) {
        try {
            CertificateFactory certificatefactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificatefactory.generateCertificate(x509CertIs);
            trustKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustKeyStore.load(null, null);
            trustKeyStore.setCertificateEntry("trust", certificate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean setTrustStore(InputStream ksIs, String ksType) {
        try {
            trustKeyStore = KeyStore.getInstance(ksType);
            trustKeyStore.load(ksIs, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean setKeyStore(InputStream x509CertIs, String password) {
        try {
            CertificateFactory certificatefactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificatefactory.generateCertificate(x509CertIs);
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, (password != null) ? password.toCharArray() : null);
            keyStore.setCertificateEntry("trust", certificate);
            this.keyStorePassword = password;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean setKeyStore(InputStream ksIs, String ksType, String password) {
        try {
            keyStore = KeyStore.getInstance(ksType);
            keyStore.load(ksIs, (password != null) ? password.toCharArray() : null);
            this.keyStorePassword = password;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public KeyStore getTrustStore() {
        return trustKeyStore;
    }

    @Override
    public KeyStore getKeyStore() {
        return keyStore;
    }

    @Override
    public String getKeyStorePassword() {
        return keyStorePassword;
    }

}
