package sample.cipher;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CBC implements CipherFile{

    @Override
    public byte[] encrypt(File file, SecretKey secretKey, IvParameterSpec ivParameterSpec) throws IOException{
        byte[] bytes = Files.readAllBytes(file.toPath());

        try{
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            byte[] encryptedFile = cipher.doFinal(bytes);

            return encryptedFile;


        }catch(Exception e)
        {
            System.out.println("Error with CBC mode encryption");
            return null;
        }
    }

    @Override
    public File decrypt(byte[] encryptedFile, SecretKey secretKey, IvParameterSpec ivParameterSpec){
        return null;
    }
}
