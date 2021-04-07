package sample.rsa_keys;

import java.security.*;


public class PrivateKeyFileEncryptor {
    /**
     * Encrypts and saves private key to file (salt is also saved to file)
     * @param password user's password used for encryption
     * @param file path to the file in which private key data will be stored
     * @param priv private key
     */
    public void encryptAndSaveKey(String password, String file, PrivateKey priv) {
        try {
            //create salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[8];
            random.nextBytes(salt);
            //encrypt keys
            PrivateKeyEncryptor passwordEncryptor = new PrivateKeyEncryptor(password, salt);
            //save keys and salt
            EncryptedPrivateKey byteData = passwordEncryptor.encryptPrivateKey(priv);
            PrivateKeyFileManager fileManager = new PrivateKeyFileManager(file);
            fileManager.write(byteData);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Reads private key from file and decrypts it
     * @param password user's password used for decryption
     * @param file path to the file in which encrypted key data is stored
     * @return decrypted private key
     */
    public PrivateKey decryptAndReadKeys(String password, String file) {
        PrivateKeyFileManager fileManager = new PrivateKeyFileManager(file);
        EncryptedPrivateKey byteData = fileManager.read();
        PrivateKeyEncryptor passwordEncryptor = new PrivateKeyEncryptor(password, byteData.getSalt());
        return passwordEncryptor.decryptPrivateKey(byteData);
    }
}
