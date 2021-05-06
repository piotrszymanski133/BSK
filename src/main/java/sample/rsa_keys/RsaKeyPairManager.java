package sample.rsa_keys;

import java.io.File;
import java.security.*;

public class RsaKeyPairManager {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    /**
     * Class for managing user's RSA key pair. Generates new one if user's directory is not found.
     * Otherwise reads existing one from files.
     * @param login user's login, which will be used for naming and searching for files
     * @param password user's password, for private key encryption
     * @param dir directory in which users' directories with keys are stored
     */
    public RsaKeyPairManager(String login, String password, String dir) {
        File userDir = new File(dir, login);
        if(!userDir.exists()) {
            try {
                KeyPair keyPair = generateKeys();
                this.publicKey = keyPair.getPublic();
                this.privateKey = keyPair.getPrivate();
                RsaKeyPairFileManager fileManager = new RsaKeyPairFileManager(userDir.getAbsolutePath());
                fileManager.write(keyPair, password);
            } catch(NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            }
        } else {
            RsaKeyPairFileManager fileManager = new RsaKeyPairFileManager(userDir.getAbsolutePath());
            KeyPair keyPair = fileManager.read(password);
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
    public KeyPair getKeyPair() {
        return new KeyPair(this.publicKey, this.privateKey);
    }

    /**
     * Generates new RSA key pair
     * @return generated key pair
     * @throws NoSuchAlgorithmException if RSA was not detected
     */
    private KeyPair generateKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        return generator.generateKeyPair();
    }
}
