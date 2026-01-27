package com.topwise.toptool.impl;

import com.topwise.toptool.api.algo.IAlgo;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

public class AlgoRsa implements IAlgo.IRsa {

    private static AlgoRsa instance;


    private AlgoRsa() {
    }


    public synchronized static AlgoRsa getInstance() {
        if (instance == null) {
            instance = new AlgoRsa();
        }

        return instance;
    }

    @Override
    public KeyPair genKeyPair(int bits) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = new SecureRandom();
            kpg.initialize(bits, random);
            KeyPair kp = kpg.generateKeyPair();
            return kp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RSAPublicKey genPublicKey(byte[] modulus, byte[] exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RSAPrivateKey genPrivateKey(byte[] modulus, byte[] exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private byte[][] splitArray(byte[] data, int blockSize) {
        int x = data.length / blockSize;
        int y = data.length % blockSize;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];
        byte[] arr;
        for (int i = 0; i < x + z; i++) {
            arr = new byte[blockSize];
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(data, i * blockSize, arr, 0, y);
            } else {
                System.arraycopy(data, i * blockSize, arr, 0, blockSize);
            }
            arrays[i] = arr;
        }
        return arrays;
    }

    @Override
    public byte[] encryptWithPublicKey(RSAPublicKey key, byte[] data, PaddingOption paddingOption) {
        Cipher cipher;

        try {
            if (paddingOption == PaddingOption.NO_PADDING) {
                cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            } else {
                cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            }
            cipher.init(Cipher.ENCRYPT_MODE, key);

            int keyLen = key.getModulus().bitLength() / 8;
            byte[][] blocks = null;
            int blockSize = keyLen;
            if (paddingOption == PaddingOption.NO_PADDING) {
                blocks = splitArray(data, blockSize);
            } else if (paddingOption == PaddingOption.PKCS1_PADDING) {
                blockSize = keyLen - 11;
                blocks = splitArray(data, blockSize);
            }

            ByteBuffer bb = ByteBuffer.allocate(blocks.length * keyLen);
            for (byte[] block : blocks) {
                byte[] encryptedBlock = cipher.doFinal(block);
                bb.put(encryptedBlock);
            }
            bb.flip();
            int totalLen = bb.limit();

            byte[] ret = new byte[totalLen];
            bb.position(0);
            bb.get(ret);

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] encryptWithPrivateKey(RSAPrivateKey key, byte[] data, PaddingOption paddingOption) {
        Cipher cipher;

        try {
            if (paddingOption == PaddingOption.NO_PADDING) {
                cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            } else {
                cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            }
            cipher.init(Cipher.ENCRYPT_MODE, key);

            int keyLen = key.getModulus().bitLength() / 8;
            byte[][] blocks = null;
            int blockSize = keyLen;
            if (paddingOption == PaddingOption.NO_PADDING) {
                blocks = splitArray(data, blockSize);
            } else if (paddingOption == PaddingOption.PKCS1_PADDING) {
                blockSize = keyLen - 11;
                blocks = splitArray(data, blockSize);
            }

            ByteBuffer bb = ByteBuffer.allocate(blocks.length * keyLen);
            for (byte[] block : blocks) {
                byte[] encryptedBlock = cipher.doFinal(block);
                bb.put(encryptedBlock);
            }
            bb.flip();
            int totalLen = bb.limit();

            byte[] ret = new byte[totalLen];
            bb.position(0);
            bb.get(ret);

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] decryptWithPrivateKey(RSAPrivateKey key, byte[] data, PaddingOption paddingOption) {
        Cipher cipher;

        try {
            if (paddingOption == PaddingOption.NO_PADDING) {
                cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            } else {
                cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            }
            cipher.init(Cipher.DECRYPT_MODE, key);

            int keyLen = key.getModulus().bitLength() / 8;
            byte[][] blocks = null;
            blocks = splitArray(data, keyLen);

            ByteBuffer bb = ByteBuffer.allocate(blocks.length * keyLen);
            for (byte[] block : blocks) {
                byte[] decryptedBlock = cipher.doFinal(block);
                bb.put(decryptedBlock);
            }
            bb.flip();
            int totalLen = bb.limit();

            byte[] ret = new byte[totalLen];
            bb.position(0);
            bb.get(ret);

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] decryptWithPublicKey(RSAPublicKey key, byte[] data, PaddingOption paddingOption) {
        Cipher cipher;

        try {
            if (paddingOption == PaddingOption.NO_PADDING) {
                cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            } else {
                cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            }
            cipher.init(Cipher.DECRYPT_MODE, key);

            int keyLen = key.getModulus().bitLength() / 8;
            byte[][] blocks = null;
            blocks = splitArray(data, keyLen);

            ByteBuffer bb = ByteBuffer.allocate(blocks.length * keyLen);
            for (byte[] block : blocks) {
                byte[] decryptedBlock = cipher.doFinal(block);
                bb.put(decryptedBlock);
            }
            bb.flip();
            int totalLen = bb.limit();

            byte[] ret = new byte[totalLen];
            bb.position(0);
            bb.get(ret);

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] sign(byte[] data, RSAPrivateKey privateKey, SignAlgorithm signAlgo) {
        try {
            String algoString = "SHA1withRSA";
            if (signAlgo == SignAlgorithm.MD5_WITH_RSA) {
                algoString = "MD5withRSA";
            } else if (signAlgo == SignAlgorithm.SHA256_WITH_RSA) {
                algoString = "SHA256withRSA";
            }

            Signature signatrue = Signature.getInstance(algoString);
            signatrue.initSign(privateKey);
            signatrue.update(data);

            byte[] sign = signatrue.sign();
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean verifySign(byte[] data, RSAPublicKey publicKey, byte[] sign, SignAlgorithm signAlgo) {
        try {
            String algoString = "SHA1withRSA";
            if (signAlgo == SignAlgorithm.MD5_WITH_RSA) {
                algoString = "MD5withRSA";
            } else if (signAlgo == SignAlgorithm.SHA256_WITH_RSA) {
                algoString = "SHA256withRSA";
            }

            Signature signatrue = Signature.getInstance(algoString);
            signatrue.initVerify(publicKey);
            signatrue.update(data);

            return signatrue.verify(sign);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
