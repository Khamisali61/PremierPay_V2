package com.topwise.toptool.api.comm;

public interface ICommHelper {
    ISslKeyStore createSslKeyStore();
    ICommSslClient createSslClient(String paramString, int paramInt, ISslKeyStore paramISslKeyStore);
    ICommTcpClient createTcpClient(String paramString, int paramInt);
}
