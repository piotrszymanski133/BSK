package sample.communication;

import sample.asymmetrical_cipher.*;
import sample.cipher.*;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class responsible for downloading received files.
 * It works in separate thread during life of application
 */
public class FileReceiver implements Runnable{
    private KeyPair keyPair;
    private int port;

    public FileReceiver(KeyPair keyPair, int port) {
        this.keyPair = keyPair;
        this.port = port;
    }

    private AtomicBoolean running = new AtomicBoolean(true);

    /**
     * Function responsible for waiting for files to download
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)){
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
        try (DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {
            String mode, encryptedKey, encryptedIv, name;

            mode = is.readUTF();
            encryptedKey = is.readUTF();
            encryptedIv = is.readUTF();
            name = is.readUTF();

            byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKey);
            byte[] encryptedIvBytes = Base64.getDecoder().decode(encryptedIv);

            AsymmetricalCipher cipher = new RsaCipher(this.keyPair);
            byte[] decryptedKeyBytes = cipher.decrypt(encryptedKeyBytes);
            byte[] decryptedIvBytes = cipher.decrypt(encryptedIvBytes);

            Data data = new Data();
            data.setSecretKey(new SecretKeySpec(Arrays.copyOfRange(decryptedKeyBytes, 112, 128), 0, 16, "AES"));
            data.setIvParameterSpec(new IvParameterSpec(Arrays.copyOfRange(decryptedIvBytes, 112, 128)));
            name = name.replaceAll(String.valueOf((char) 0), "");

 /*       CipherMode mode = CipherMode.valueOf(is.readUTF());
        String key = is.readUTF();
        byte[] decodedKey = Base64.getDecoder().decode(key);
        String iv = is.readUTF();
        byte[] decodedIv = Base64.getDecoder().decode(iv);
        String name = is.readUTF();

        Data data = new Data();
        data.setSecretKey(new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"));
        data.setIvParameterSpec(new IvParameterSpec(decodedIv));
        data.setCipherMode(mode);
  */
            CipherFile cipherFile = new CipherFile();
            try (FileOutputStream os = new FileOutputStream(name)) {
                byte[] buffer = new byte[16400];
                byte[] decryptedBuffer;
                while (is.read(buffer) != -1) {
                    decryptedBuffer = cipherFile.decrypt(data, buffer, mode);
                    os.write(decryptedBuffer);
                }
                //DEBUGOWANIE
                System.out.println("Downloaded " + name);
                System.out.println("Key " + data.getSecretKey());
                System.out.println("IV " + data.getIvParameterSpec());
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
