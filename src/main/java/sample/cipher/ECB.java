package sample.cipher;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;

public class ECB implements CipherFile{

    @Override
    public void encrypt(File file, Data data) throws IOException{
        //TODO: MAKE READING AND CIPHERING BUFFERED
        byte[] bytes = Files.readAllBytes(file.toPath());

        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, data.getSecretKey());
            data.setFileName(file.getName());
            data.setDataBytes(cipher.doFinal(bytes));

        }catch(Exception e)
        {
            System.out.println("Error with ECB mode encryption");
        }
    }

    @Override
    public void decrypt(Data data) {
        try {
            //TODO: MAKE DECIPHERING BUFFERED
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, data.getSecretKey());
            try(FileOutputStream os = new FileOutputStream(data.getFileName())) {
                os.write(cipher.doFinal(data.getDataBytes()));
            }

        }catch(Exception e){
            System.out.println("Error with ECB mode decryption");
        }
    }
}
