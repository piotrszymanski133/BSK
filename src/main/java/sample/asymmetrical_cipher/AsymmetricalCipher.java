package sample.asymmetrical_cipher;

public interface AsymmetricalCipher {
    byte[] encrypt(byte[] data);
    byte[] decrypt(byte[] data);
}
