package com.livk.sso.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * <p>
 * RSAUtils
 * </p>
 *
 * @author livk
 * @date 2022/4/11
 */
@UtilityClass
public class RSAUtils {

    private static final int DEFAULT_SIZE = 2048;

    private static KeyFactory factory;

    static {
        try {
            factory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public PublicKey getPublicKey(String filename) {
        byte[] bytes = readFile(filename);
        bytes = Base64.getDecoder().decode(bytes);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        try {
            return factory.generatePublic(spec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PrivateKey getPrivateKey(String filename) {
        byte[] bytes = readFile(filename);
        bytes = Base64.getDecoder().decode(bytes);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        try {
            return factory.generatePrivate(spec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void generateKey(String publicKeyFilename, String privateKeyFilename, String secret, int keySize) {
        KeyPair keyPair = generateKeyPair(secret, keySize);

        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        publicKeyBytes = Base64.getEncoder().encode(publicKeyBytes);
        writeFile(publicKeyFilename, publicKeyBytes);

        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        privateKeyBytes = Base64.getEncoder().encode(privateKeyBytes);
        writeFile(privateKeyFilename, privateKeyBytes);
    }

    public void generatePublicKey(String publicKeyFilename, String secret, int keySize) {
        KeyPair keyPair = generateKeyPair(secret, keySize);

        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        publicKeyBytes = Base64.getEncoder().encode(publicKeyBytes);
        writeFile(publicKeyFilename, publicKeyBytes);
    }

    private KeyPair generateKeyPair(String secret, int keySize) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            SecureRandom secureRandom = new SecureRandom(secret.getBytes());
            keyPairGenerator.initialize(Math.max(keySize, DEFAULT_SIZE), secureRandom);
            return keyPairGenerator.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void writeFile(String destPath, byte[] bytes) {
        destPath = URLDecoder.decode(destPath, StandardCharsets.UTF_8);
        File file = new File(destPath);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Files.write(file.toPath(), bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] readFile(String filename) {
        try {
            File file;
            if (filename.startsWith("classpath:")) {
                filename = filename.replaceFirst("classpath:", "");
                file = new ClassPathResource(filename).getFile();
            } else {
                file = new File(filename);
            }
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * 生成公钥私钥
     *
     * @param location 存放位置
     */
    public static void generateFile(String location, String secret, int keySize) {
        String privateKey = location + "/id_key_rsa";
        String publicKey = location + "/id_key_rsa.pub";
        generateKey(publicKey, privateKey, secret, keySize);
    }
}
