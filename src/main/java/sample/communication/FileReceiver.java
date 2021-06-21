package sample.communication;

import javafx.application.Platform;
import javafx.scene.control.Label;
import sample.asymmetrical_cipher.*;
import sample.cipher.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.KeyPair;
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
    private Label receivingProgressLabel;

    public FileReceiver(KeyPair keyPair, int port, Label receivingProgressLabel) {
        this.keyPair = keyPair;
        this.port = port;
        this.receivingProgressLabel = receivingProgressLabel;
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
                }catch(SocketTimeoutException ignored){
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
            final String finalName = name;
            Platform.runLater(() -> receivingProgressLabel.setText("Downloading file " + finalName));
            int bytes_read = 0;
            int counter = 0;
            CipherFile cipherFile = new CipherFile();
            try (FileOutputStream os = new FileOutputStream(name)) {
                byte[] buffer = new byte[16400];
                byte[] decryptedBuffer;
                while (true) {
                    while(is.available() < 16400) {
                        Thread.sleep(10);
                        counter++;
                        if(counter > 100)
                        {
                            break;
                        }
                    }
                    counter = 0;
                    bytes_read = is.read(buffer);
                    if(bytes_read == -1) {
                        break;
                    }
                    decryptedBuffer = cipherFile.decrypt(data, Arrays.copyOfRange(buffer, 0, bytes_read), mode);
                    os.write(decryptedBuffer);
                }
                Platform.runLater(() -> receivingProgressLabel.setText("Downloaded " + finalName));
                System.out.println("Downloaded " + name);
            }
        } catch (Exception e) {
            System.err.println(e.toString());
            Platform.runLater(() -> receivingProgressLabel.setText("Download error"));
        }
    }
}
