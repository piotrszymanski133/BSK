package sample.cipher;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CBC implements CipherFile{

    @Override
    public void encrypt(File file, Data data) throws IOException{
        byte[] bytes = Files.readAllBytes(file.toPath());

        try{
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, data.getSecretKey(), data.getIvParameterSpec());
            data.setFileName(file.getName());
            data.setDataBytes(cipher.doFinal(bytes));

        }catch(Exception e)
        {
            System.out.println("Error with CBC mode encryption");

        }
    }

    @Override
    public void decrypt( Data data){
    }
}
