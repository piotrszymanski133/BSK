package sample.communication;

import sample.cipher.*;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class responsible for downloading received files.
 * It works in separate thread during life of application
 */
public class FileReceiver implements Runnable{

    private AtomicBoolean running = new AtomicBoolean(true);

    /**
     * Function responsible for waiting for files to download
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(8088)){
            serverSocket.setSoTimeout(1000);
            while (running.get()) {
                try {
                    final Socket socket = serverSocket.accept();
                    downloadFile(socket);
                }catch(SocketTimeoutException e){
                    System.err.println(e.toString());
                }
            }
        }catch(IOException e){
            System.err.println(e.toString());
        }
    }

    /**
     * Breaks the waiting loop
     */
    public void shutdown(){
        running.set(false);
    }

    /**
     * Function responsible for downloading file
     * @param socket client socket
     */
    public void downloadFile(Socket socket){
        try(DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()))){
            String cipherMode = is.readUTF();
            String key = is.readUTF();
            byte[] decodedKey = Base64.getDecoder().decode(key);
            String iv = is.readUTF();
            byte[] decodedIv = Base64.getDecoder().decode(iv);
            String name = is.readUTF();

            Data data = new Data();
            data.setSecretKey(new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"));
            data.setIvParameterSpec(new IvParameterSpec(decodedIv));
            CipherFile cipherFile = new CipherFile();
            try(FileOutputStream os = new FileOutputStream(name)) {
                byte[] buffer = new byte[16400];
                byte[] decryptedBuffer;
                while (is.read(buffer) != -1) {
                    decryptedBuffer = cipherFile.decrypt(data, buffer, cipherMode);
                    os.write(decryptedBuffer);
                }
                System.out.println("Downloaded " + name);
                System.out.println("Key " + key);
                System.out.println("IV " + iv);

            }
        }catch(Exception e){
            System.err.println(e.toString());
        }
    }
}
