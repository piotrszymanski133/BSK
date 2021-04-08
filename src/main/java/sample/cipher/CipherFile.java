package sample.cipher;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface CipherFile {

    byte[] encrypt(File file, SecretKey secretKey, IvParameterSpec ivParameterSpec) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException;

    File decrypt(byte[] encryptedBytes, SecretKey secretKey, IvParameterSpec ivParameterSpec) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException;
}
