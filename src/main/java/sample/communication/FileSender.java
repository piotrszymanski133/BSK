package sample.communication;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import sample.asymmetrical_cipher.AsymmetricalCipher;
import sample.asymmetrical_cipher.RsaCipher;
import sample.cipher.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.ConcurrentModificationException;
import java.util.function.DoubleConsumer;

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

    public FileSender(Data data, ProgressBar progressBar, Label progressLabel, KeyPair keyPair, int port){
        this.data = data;
        this.progressBar = progressBar;
        this.progressLabel = progressLabel;
        this.keyPair = keyPair;
        this.port = port;
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
                    outputStream.writeUTF(data.getCipherMode().toString());
                    outputStream.writeUTF(Base64.getEncoder().encodeToString(encryptedKey));
                    outputStream.writeUTF(Base64.getEncoder().encodeToString(encryptedIv));
                    outputStream.writeUTF(data.getFile().getName());
                    try {
                        while ((inputStream.read(buffer)) != -1) {
                            readed += buffer.length;
                            final double c = readed;
                            encryptedBuffer = cipherFile.encrypt(data, buffer);
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
