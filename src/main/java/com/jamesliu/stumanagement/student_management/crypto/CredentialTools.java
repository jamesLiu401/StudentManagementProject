package com.jamesliu.stumanagement.student_management.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CredentialTools {
    private static final Logger log = LoggerFactory.getLogger(CredentialTools.class);

    private static final int KEY_SIZE = 256;
    private static final int ITERATION_COUNT = 65536;
    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 16;
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_DERIVE_ALGORITHM = "PBKDF2WithHmacSHA256";

    /** 加密 */
    public String encrypt(byte[] data, String envKeyName) {
        try {
            String password = getKeyFromEnv(envKeyName);
            byte[] salt = generateRandomBytes(SALT_LENGTH);
            byte[] iv = generateRandomBytes(IV_LENGTH);

            SecretKey aesKey = deriveAesKey(password, salt);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));
            byte[] encryptedData = cipher.doFinal(data);

            // 拼接 salt + iv + ciphertext
            byte[] result = new byte[salt.length + iv.length + encryptedData.length];
            System.arraycopy(salt, 0, result, 0, salt.length);
            System.arraycopy(iv, 0, result, salt.length, iv.length);
            System.arraycopy(encryptedData, 0, result, salt.length + iv.length, encryptedData.length);

            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            log.error("加密失败", e);
            throw new RuntimeException("加密失败", e);
        }
    }

    /** 解密 */
    public byte[] decrypt(String cipherTextBase64, String envKeyName) {
        try {
            String password = getKeyFromEnv(envKeyName);
            byte[] allBytes = Base64.getDecoder().decode(cipherTextBase64);

            byte[] salt = new byte[SALT_LENGTH];
            byte[] iv = new byte[IV_LENGTH];
            byte[] encryptedData = new byte[allBytes.length - SALT_LENGTH - IV_LENGTH];

            System.arraycopy(allBytes, 0, salt, 0, SALT_LENGTH);
            System.arraycopy(allBytes, SALT_LENGTH, iv, 0, IV_LENGTH);
            System.arraycopy(allBytes, SALT_LENGTH + IV_LENGTH, encryptedData, 0, encryptedData.length);

            SecretKey aesKey = deriveAesKey(password, salt);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            log.error("解密失败", e);
            throw new RuntimeException("解密失败", e);
        }
    }

    // ============ 工具方法 ============

    private String getKeyFromEnv(String envKeyName) {
        String value = System.getenv(envKeyName);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("环境变量未配置: " + envKeyName);
        }
        return value;
    }

    private byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    private SecretKey deriveAesKey(String password, byte[] salt) throws Exception {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_SIZE);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_DERIVE_ALGORITHM);
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }
}
