package sample.communication;

import sample.cipher.CipherFile;
import sample.cipher.CipherMode;
import sample.cipher.Data;
import sample.cipher.ECB;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
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
            CipherMode mode = CipherMode.valueOf(is.readUTF());
            String key = is.readUTF();
            byte[] decodedKey = Base64.getDecoder().decode(key);
            String iv = is.readUTF();
            byte[] decodedIv = Base64.getDecoder().decode(iv);
            String name = is.readUTF();
            int size = is.read();
            try(ByteArrayOutputStream os = new ByteArrayOutputStream(size)) {
                byte[] buffer = new byte[4096];
                int readSize;
                while ((readSize = is.read(buffer)) != -1) {
                    os.write(buffer, 0, readSize);
                }
                Data data = new Data();
                data.setDataBytes(os.toByteArray());
                data.setFileName(name);
                data.setSecretKey(new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"));
                data.setIvParameterSpec(new IvParameterSpec(decodedIv));
                System.out.println("Downloaded " + name);
                System.out.println("Key " + key);
                System.out.println("IV " + iv);
                CipherFile decryptor;
                if(mode == CipherMode.ECB)
                {
                    decryptor = new ECB();
                    decryptor.decrypt(data);
                }
                //TODO: DIFFERENT MODES
            }
        }catch(Exception e){
            System.err.println(e.toString());
        }
    }
}
