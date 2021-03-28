package sample.cipher;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class ECB implements CipherFile{

    @Override
    public File encrypt(File file) throws IOException{
        byte[] bytes = Files.readAllBytes(file.toPath());

        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();

            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedFile = cipher.doFinal(bytes);

            String pathToProject = "D:\\Studia- informatyka\\6 semestr\\Bezpieczeństwo systemów komputerowych\\Projekt\\BSK\\files\\encryption\\";

            File outputFile = new File( pathToProject+ "ECB_mode_" +file.getName());

            try (FileOutputStream stream = new FileOutputStream(outputFile)) {
                stream.write(encryptedFile);
            }
            return outputFile;


        }catch(Exception e)
        {
            System.out.println("Error with ECB mode encryption");
            return null;
        }
    }

    @Override
    public File decrypt(File file){
        return null;
    }
}
