package com.github.catvod.utils;

import android.util.Base64;

import com.github.catvod.crawler.SpiderDebug;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CBC {
    public static byte[] CBC(byte[] data, byte[] key, byte[] iv) {
        try {

            IvParameterSpec ivParameterSpec = new IvParameterSpec (iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(2, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            SpiderDebug.log(e);
            return null;
        }
    }

    public static String  encrypt (byte[] data, byte[] key, byte[] iv) {
        try {

            IvParameterSpec ivParameterSpec = new IvParameterSpec (iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(data);
            return new String(Base64.encode(encrypted, Base64.DEFAULT));
        } catch (Exception e) {
            SpiderDebug.log(e);
            return null;
        }
    }
    public static String  DECRYPT (byte[] data, byte[] key, byte[] iv) {
        try {

            IvParameterSpec ivParameterSpec = new IvParameterSpec (iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(data);
           // return new String(Base64.encode(encrypted, Base64.DEFAULT));
            return new String(encrypted);
        } catch (Exception e) {
            SpiderDebug.log(e);
            return null;
        }
    }
   
    
    public static String AES_decode(String src, String key, String IV)throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("ASCII"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes()));
        //return new String(cipher.doFinal(str, "UTF-8");
      //  String  decodestr = new String(cipher.doFinal(Base64.decode(src.getBytes("UTF-8"))), "utf-8");
        String  decodestr = new String(cipher.doFinal(Base64.decode(src.getBytes(), 0)));
        return decodestr;
    }

    public static byte[]  encryptbyte (byte[] data, byte[] key, byte[] iv) {
        try {

            IvParameterSpec ivParameterSpec = new IvParameterSpec (iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(1, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(data);
        } catch (Exception e)
        {
            SpiderDebug.log(e);
            return null;
        }
    }



}
