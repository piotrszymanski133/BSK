package sample.cipher;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;

public class CBC implements CipherFile{

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    @Override
    public File encrypt(File file) throws IOException{
        byte[] bytes = Files.readAllBytes(file.toPath());

        try{
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
            IvParameterSpec ivParameterSpec = generateIv(); // vector IV

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            byte[] encryptedFile = cipher.doFinal(bytes);

            String pathToProject = "D:\\Studia- informatyka\\6 semestr\\Bezpieczeństwo systemów komputerowych\\Projekt\\BSK\\files\\encryption\\";

            File outputFile = new File( pathToProject + "CBC_mode_" + file.getName() );

            try (FileOutputStream stream = new FileOutputStream(outputFile)) {
                stream.write(encryptedFile);
            }
            return outputFile;


        }catch(Exception e)
        {
            System.out.println("Error with CBC mode encryption");
            return null;
        }
    }

    @Override
    public File decrypt(File file) {
        return null;
    }
}
