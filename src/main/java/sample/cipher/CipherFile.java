package sample.cipher;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface CipherFile {

    void encrypt(File file, Data data) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException;

    void decrypt( Data data) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException;
}
