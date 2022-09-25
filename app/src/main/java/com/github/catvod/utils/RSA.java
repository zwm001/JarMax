package com.github.catvod.utils;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSA {
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF8");
    public static final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
    public static final String KEY_ALGORITHM = "RSA";
    private static final int MAX_DECRYPT_BLOCK = 128;
    private static final int MAX_ENCRYPT_BLOCK = 117;

    public static String decryptByPrivateKey(String data, String privateKey) {
        int i = 0;
        try {
            byte[] base64Decode = Base64.decode(data, Base64.DEFAULT);
            Cipher instance = Cipher.getInstance(ECB_PKCS1_PADDING);
            instance.init(2, getPrivateKey(privateKey));
            int length = base64Decode.length;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i2 = 0;
            while (true) {
                int i3 = length - i;
                if (i3 > 0) {
                    byte[] doFinal = i3 > MAX_DECRYPT_BLOCK ? instance.doFinal(base64Decode, i, MAX_DECRYPT_BLOCK) : instance.doFinal(base64Decode, i, i3);
                    byteArrayOutputStream.write(doFinal, 0, doFinal.length);
                    i = i2 + 1;
                    int i4 = i;
                    i *= MAX_DECRYPT_BLOCK;
                    i2 = i4;
                } else {
                    byte[] toByteArray = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    return new String(toByteArray, CHARSET_UTF8);
                }
            }
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public static String encryptByPublicKey(String data, String publicKey) {
        int i = 0;
        try {
            byte[] bytes = data.getBytes(CHARSET_UTF8);
            Cipher instance = Cipher.getInstance(ECB_PKCS1_PADDING);
            instance.init(1, getPublicKey(publicKey));
            int length = bytes.length;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i2 = 0;
            while (true) {
                int i3 = length - i;
                byte[] doFinal;
                if (i3 > 0) {
                    doFinal = i3 > MAX_ENCRYPT_BLOCK ? instance.doFinal(bytes, i, MAX_ENCRYPT_BLOCK) : instance.doFinal(bytes, i, i3);
                    byteArrayOutputStream.write(doFinal, 0, doFinal.length);
                    i = i2 + 1;
                    int i4 = i;
                    i *= MAX_ENCRYPT_BLOCK;
                    i2 = i4;
                } else {
                    doFinal = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    return new String(Base64.encode(doFinal, Base64.DEFAULT));
                }
            }
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public static String decryptByPublicKey(String data, String publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, getPublicKey(publicKey));
        byte[] result = cipher.doFinal(Base64.decode(data, Base64.DEFAULT));
        return new String(result);
    }

    public static PrivateKey getPrivateKey(String str) {
        try {
            return KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(str, Base64.DEFAULT)));
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public static PublicKey getPublicKey(String str) {
        try {
            return KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(Base64.decode(str, Base64.DEFAULT)));
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    private static void handleException(Exception exception) {
        exception.printStackTrace();
    }
}
