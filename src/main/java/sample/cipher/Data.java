package sample.cipher;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Data {

    private CipherMode cipherMode;
    private SecretKey secretKey;
    private String fileName;
    private byte[] dataBytes;
    private IvParameterSpec ivParameterSpec;


    // CONSTRUCTOR FOR DATA
    public Data () throws NoSuchAlgorithmException {
        secretKey = KeyGenerator.getInstance("AES").generateKey();
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        ivParameterSpec = new IvParameterSpec(iv);

        cipherMode = null;
        dataBytes = null;
        fileName = null;
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

    public byte[] getDataBytes(){
        return dataBytes;
    }

    public void setDataBytes(byte[] d){
        dataBytes = d;
    }

    public String getFileName(){
        return fileName;
    }

    public void setFileName(String s){
        fileName = s;
    }

    public CipherMode getCipherMode(){
        return  cipherMode;
    }

    public void setCipherMode(CipherMode c){
        cipherMode = c;
    }
}
