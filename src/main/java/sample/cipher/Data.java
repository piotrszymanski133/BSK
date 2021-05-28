package sample.cipher;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Data {

    private SecretKey secretKey;
    private File file;
    private IvParameterSpec ivParameterSpec;


    // CONSTRUCTOR FOR DATA
    public Data () throws NoSuchAlgorithmException {
        secretKey = KeyGenerator.getInstance("AES").generateKey();
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        ivParameterSpec = new IvParameterSpec(iv);
        file = null;
    }

    // GETTER FOR SECRETKEY
    public SecretKey getSecretKey(){
        return secretKey;
    }

    public void setSecretKey(SecretKey k){
        secretKey = k;
    }

    // GETTER FOR IV
    public IvParameterSpec getIvParameterSpec(){
        return ivParameterSpec;
    }

    public void setIvParameterSpec(IvParameterSpec i){
        ivParameterSpec = i;
    }

    public File getFile(){
        return file;
    }

    public void setFile(File s){
        file = s;
    }

}

