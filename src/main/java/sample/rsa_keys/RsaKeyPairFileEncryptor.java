package sample.rsa_keys;

import java.security.*;


public class RsaKeyPairFileEncryptor {
    public void encryptAndSaveKeys(String password, String file, KeyPair keyPair) {
        PublicKey pub = keyPair.getPublic();
        PrivateKey priv = keyPair.getPrivate();
        try {
            //create salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[8];
            random.nextBytes(salt);
            //encrypt keys
            RsaKeyPairPasswordEncryptor passwordEncryptor = new RsaKeyPairPasswordEncryptor(password, salt);
            //save keys and salt
            RsaKeyPairEncryptedByteData byteData = passwordEncryptor.encryptKeys(pub, priv);
            RsaKeyPairFileManager fileManager = new RsaKeyPairFileManager(file);
            fileManager.write(byteData);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    public KeyPair decryptAndReadKeys(String password, String file) {
        RsaKeyPairFileManager fileManager = new RsaKeyPairFileManager(file);
        RsaKeyPairEncryptedByteData byteData = fileManager.read();
        RsaKeyPairPasswordEncryptor passwordEncryptor = new RsaKeyPairPasswordEncryptor(password, byteData.getSalt());
        return passwordEncryptor.decryptKeyPair(byteData);
    }
}
