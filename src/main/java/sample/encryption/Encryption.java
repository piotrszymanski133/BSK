package sample.encryption;

import javax.crypto.*;
import java.io.File;
import java.io.FileOutputStream;

public class Encryption {

    private byte[] bytes;

    public Encryption(byte[] bytes){
        this.bytes = bytes;
    }

    public void encryptionECB(byte[] bytes, File file){
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();

            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedFile = cipher.doFinal(bytes);

            String pathToProject = "D:\\Studia- informatyka\\6 semestr\\Bezpieczeństwo systemów komputerowych\\Projekt\\BSK\\";

            File outputFile = new File( pathToProject+ file.getName());

            try (FileOutputStream stream = new FileOutputStream(outputFile)) {
                stream.write(encryptedFile);
            }

        }catch(Exception e)
        {
            System.out.println("Error with ECB mode encryption");
        }

    }
}
