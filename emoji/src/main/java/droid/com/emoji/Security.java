package droid.com.emoji;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Security {
    private static String TAG = "#Security";
    private static Security security = null;
    private String KEY = "zzxxccvvbbnnmmllkkjjhhggffddssaa";
    private String INITIALIZATION_VECTOR = "INITIALIZATION_VECTOR";
    private MessageDigest messageDigest = null;
    private byte[] digest = new byte[16];

    public static Security getInstance() {
        if (security == null) security = new Security();
        return security;
    }

    private String encoderfun(byte[] decval) {
        return Base64.encodeToString(decval, Base64.DEFAULT);
    }

    public String getEncryptedText(Context context, String text) {
        final String IV = Long.toString(System.currentTimeMillis());
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(INITIALIZATION_VECTOR, IV).apply();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(IV.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "MD5 error" + e.getLocalizedMessage());
        }
        SecretKey secretKey = new SecretKeySpec(KEY.getBytes(), "AES");
        try {
            return encoderfun(encrypt(text.getBytes(), secretKey, digest));
        } catch (Exception e) {
            Log.e(TAG, "encrypt: error", e);
            e.printStackTrace();
            return "";
        }
    }

    private byte[] encrypt(byte[] plainText, SecretKey key, byte[] IV) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        return cipher.doFinal(plainText);
    }

    public String getDecryptedText(Context context, String text) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String IV = pref.getString(INITIALIZATION_VECTOR, "");
        if (IV.isEmpty()) return text;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(IV.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getLocalizedMessage());
            return text;
        }
        try {
            byte[] encText = decoderfun(text);
            String decryptedText = decrypt(encText, KEY, digest);
            return decryptedText == null ? text : decryptedText;
        } catch (Exception e) {
            Log.e("tag", e.getLocalizedMessage());
            e.printStackTrace();
            return text;
        }
    }

    private byte[] decoderfun(String enval) {
        return Base64.decode(enval, Base64.DEFAULT);
    }

    private String decrypt(byte[] cipherText, String key, byte[] IV) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return new String(cipher.doFinal(cipherText));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
