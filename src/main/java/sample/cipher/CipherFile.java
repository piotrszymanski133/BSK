package sample.cipher;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface CipherFile {

    byte[] encrypt(Data data, byte[] bytes) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException;

    byte[] decrypt(Data data, byte[] bytes) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException;
}
