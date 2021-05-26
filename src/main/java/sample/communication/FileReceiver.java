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
        try(DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()))){
           String encryptedMode, encryptedKey, encryptedIv, encryptedName;

            encryptedMode = is.readUTF();
            encryptedKey = is.readUTF();
            encryptedIv = is.readUTF();
            encryptedName = is.readUTF();

            byte[] encryptedModeBytes = Base64.getDecoder().decode(encryptedMode);
            byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKey);
            byte[] encryptedIvBytes = Base64.getDecoder().decode(encryptedIv);
            byte[] encryptedNameBytes = Base64.getDecoder().decode(encryptedName);

            AsymmetricalCipher cipher = new RsaCipher(this.keyPair);
            byte[] decryptedModeBytes = cipher.decrypt(encryptedModeBytes);
            byte[] decryptedKeyBytes = cipher.decrypt(encryptedKeyBytes);
            byte[] decryptedIvBytes = cipher.decrypt(encryptedIvBytes);
            byte[] decryptedNameBytes = cipher.decrypt(encryptedNameBytes);

            Data data = new Data();
            String x = new String(decryptedModeBytes);
            x = x.substring(125);
            data.setCipherMode(CipherMode.valueOf(x));
            data.setSecretKey(new SecretKeySpec((new String(decryptedKeyBytes)).substring(112)
                    .getBytes(), 0, 16, "AES"));
            data.setIvParameterSpec(new IvParameterSpec((new String(decryptedIvBytes)).substring(112)
                    .getBytes()));
            String name = new String(decryptedNameBytes);
            name = name.replaceAll(String.valueOf((char)0), "");

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
            CipherFile cipherFile;
            switch (data.getCipherMode()) {
                case CBC:
                    cipherFile = new CBC();
                    break;
                case CTR:
                    cipherFile = new CTR();
                    break;
                case CFB:
                    cipherFile = new CFB();
                    break;
                case OFB:
                    cipherFile = new OFB();
                    break;
                case ECB:
                default:
                    cipherFile = new ECB();
                    break;
            }

            try(FileOutputStream os = new FileOutputStream(name)) {
                byte[] buffer = new byte[16400];
                byte[] decryptedBuffer;
                while (is.read(buffer) != -1) {
                    decryptedBuffer = cipherFile.decrypt(data, buffer);
                    os.write(decryptedBuffer);
                }
                //DEBUGOWANIE
                System.out.println("Downloaded " + name);
                System.out.println("Key " + data.getSecretKey());
                System.out.println("IV " + data.getIvParameterSpec());

            }
        }catch(Exception e){
            System.err.println(e.toString());
        }
    }
}
