package sample.communication;

import sample.cipher.CipherFile;
import sample.cipher.CipherMode;
import sample.cipher.Data;
import sample.cipher.ECB;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.ConcurrentModificationException;

/**
 * Thread responsible for sending encrypted file
 */
public class FileSender implements Runnable{

    private Data data;
    private CipherFile cipherFile;

    public FileSender(Data data){
        this.data = data;
        if(data.getCipherMode() == CipherMode.ECB)
            this.cipherFile = new ECB();
        //TODO: OTHER MODES
    }
    @Override
    public void run() {
        try(FileInputStream inputStream = new FileInputStream(data.getFile().getAbsolutePath())) {
            Socket socket = new Socket("127.0.0.1", 8085);
            try(DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
                byte[] buffer = new byte[16384];
                byte[] encryptedBuffer;

                outputStream.writeUTF(data.getCipherMode().toString());
                outputStream.writeUTF(Base64.getEncoder().encodeToString(data.getSecretKey().getEncoded()));
                outputStream.writeUTF(Base64.getEncoder().encodeToString(data.getIvParameterSpec().getIV()));
                outputStream.writeUTF(data.getFile().getName());
                try {
                    while ((inputStream.read(buffer)) != -1) {
                        encryptedBuffer = cipherFile.encrypt(data, buffer);
                        outputStream.write(encryptedBuffer);
                    }
                }catch(Exception e){
                    System.err.println("Problem with sending file!");
                }
            }
        } catch (IOException e){
            System.err.println(e.toString());
        }
    }
}
