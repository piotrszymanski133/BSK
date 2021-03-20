package sample.rsa_keys;

import java.io.File;
import java.security.*;

public class RsaKeyPairManager {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    public RsaKeyPairManager(String login, String password, String dir) {
        File userDir = new File(dir, login);
        //temporary txt target file
        File keysFile = new File(userDir, "keys.txt");
        if(!keysFile.exists()) {
            try {
                KeyPair keyPair = generateKeys();
                RsaKeyPairFileEncryptor fileEncryptor = new RsaKeyPairFileEncryptor();
                fileEncryptor.encryptAndSaveKeys(password, keysFile.getAbsolutePath(), keyPair);
                this.publicKey = keyPair.getPublic();
                this.privateKey = keyPair.getPrivate();
            } catch(NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            }
        } else {
            RsaKeyPairFileEncryptor fileEncryptor = new RsaKeyPairFileEncryptor();
            KeyPair keyPair = fileEncryptor.decryptAndReadKeys(password, keysFile.getAbsolutePath());
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
        }
    }
    public PublicKey getPublicKey() {
        return this.publicKey;
    }
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
    private KeyPair generateKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        return generator.generateKeyPair();
    }
}
