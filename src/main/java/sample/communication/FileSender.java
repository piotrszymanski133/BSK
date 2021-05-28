package sample.communication;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import sample.cipher.*;

import javax.crypto.Cipher;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private String cipherMode;

    public FileSender(Data data, ProgressBar progressBar, Label progressLabel, String cipherMode){
        this.data = data;
        this.progressBar = progressBar;
        this.progressLabel = progressLabel;
        this.cipherMode = cipherMode;
    }

    @Override
    public void run() {
        try(FileInputStream inputStream = new FileInputStream(data.getFile().getAbsolutePath())) {
            // CipherFile for all modes
            CipherFile cipherFile = new CipherFile();
            Socket socket = new Socket("127.0.0.1", 8085);
            try(DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
                byte[] buffer = new byte[16384];
                byte[] encryptedBuffer;
                long readed = 0;
                outputStream.writeUTF(cipherMode);
                outputStream.writeUTF(Base64.getEncoder().encodeToString(data.getSecretKey().getEncoded()));
                outputStream.writeUTF(Base64.getEncoder().encodeToString(data.getIvParameterSpec().getIV()));
                outputStream.writeUTF(data.getFile().getName());
                try {
                    while ((inputStream.read(buffer)) != -1) {
                        readed += buffer.length;
                        final double c = readed;
                        encryptedBuffer = cipherFile.encrypt(data, buffer, cipherMode);
                        outputStream.write(encryptedBuffer);
                        Platform.runLater(() -> progressBar.setProgress(c/data.getFile().length()));
                    }
                    Platform.runLater(() -> progressLabel.setText("File was send successfully!"));
                }catch(Exception e){
                    System.err.println("Problem with sending file!");
                    Platform.runLater(() -> progressLabel.setText("Problem with sending file!"));
                }
            }
        } catch (IOException e){
            System.err.println(e.toString());
        }
    }
}
