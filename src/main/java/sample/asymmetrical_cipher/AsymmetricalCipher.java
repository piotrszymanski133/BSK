package sample.asymmetrical_cipher;

public interface AsymmetricalCipher {
    byte[] encrypt(String data);
    String decrypt(byte[] data);
}
