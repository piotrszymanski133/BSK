package sample.cipher;

import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface CipherFile {

    File encrypt(File file) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException;

    File decrypt(File file) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException;
}
