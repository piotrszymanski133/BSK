package sample.cipher;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class CTR implements  CipherFile{
    @Override
    public byte[] encrypt(Data data, byte[] bytes) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException {
        return new byte[0];
    }

    @Override
    public byte[] decrypt(Data data, byte[] bytes) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException {
        return new byte[0];
    }
}