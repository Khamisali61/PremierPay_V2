package com.topwise.toptool.api.comm;

import java.io.InputStream;
import java.security.KeyStore;

public interface ISslKeyStore {
    boolean setTrustStore(InputStream paramInputStream);

    boolean setTrustStore(InputStream paramInputStream, String paramString);

    boolean setKeyStore(InputStream paramInputStream, String paramString);

    boolean setKeyStore(InputStream paramInputStream, String paramString1, String paramString2);

    KeyStore getTrustStore();

    KeyStore getKeyStore();

    String getKeyStorePassword();
}
