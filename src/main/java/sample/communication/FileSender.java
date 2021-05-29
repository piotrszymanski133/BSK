package sample.communication;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import sample.asymmetrical_cipher.AsymmetricalCipher;
import sample.asymmetrical_cipher.RsaCipher;
import sample.cipher.*;
import java.io.*;
import java.net.Socket;
import java.security.KeyPair;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * Thread responsible for sending encrypted file
 */
public class FileSender implements Runnable{

    private Data data;
    private ProgressBar progressBar;
    private Label progressLabel;
    private CipherFile cipherFile;
    private KeyPair keyPair;
    private int port;
    private String mode;

    public FileSender(Data data, ProgressBar progressBar, Label progressLabel, KeyPair keyPair, int port, String mode){
        this.data = data;
        this.progressBar = progressBar;
        this.progressLabel = progressLabel;
        this.keyPair = keyPair;
        this.port = port;
        this.mode = mode;
    }

    @Override
    public void run() {
        while(true) {
            try (FileInputStream inputStream = new FileInputStream(data.getFile().getAbsolutePath())) {
                Socket socket = new Socket("127.0.0.1", port);
                try (DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
                    byte[] buffer = new byte[16384];
                    byte[] encryptedBuffer;
                    long readed = 0;

                    AsymmetricalCipher cipher = new RsaCipher(keyPair);
                    byte[] encryptedKey = cipher.encrypt(data.getSecretKey().getEncoded());
                    byte[] encryptedIv = cipher.encrypt(data.getIvParameterSpec().getIV());
                    outputStream.writeUTF(mode);
                    outputStream.writeUTF(Base64.getEncoder().encodeToString(encryptedKey));
                    outputStream.writeUTF(Base64.getEncoder().encodeToString(encryptedIv));
                    outputStream.writeUTF(data.getFile().getName());
                    cipherFile = new CipherFile();
                    try {
                        while ((inputStream.read(buffer)) != -1) {
                            readed += buffer.length;
                            final double c = readed;
                            encryptedBuffer = cipherFile.encrypt(data, buffer, mode);
                            TimeUnit.MICROSECONDS.sleep(100);
                            outputStream.write(encryptedBuffer);
                            Platform.runLater(() -> progressBar.setProgress(c / data.getFile().length()));
                        }
                        Platform.runLater(() -> progressLabel.setText("File was send successfully!"));
                        break;
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        Platform.runLater(() -> progressLabel.setText("Problem with sending file!"));
                    }
                }
            } catch (IOException e) {
                System.err.println(e.toString());
            }
        }
    }
}
