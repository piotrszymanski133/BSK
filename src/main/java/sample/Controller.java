package sample;

import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import sample.asymmetrical_cipher.AsymmetricalCipher;
import sample.asymmetrical_cipher.RsaCipher;
import sample.cipher.CBC;
import sample.cipher.CipherMode;
import sample.cipher.Data;
import sample.cipher.ECB;
import sample.communication.FileReceiver;
import sample.communication.FileSender;
import sample.communication.KeyRequester;
import sample.communication.KeySender;
import sample.rsa_keys.RsaKeyPairManager;
import sample.user.User;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.ResourceBundle;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Controller implements Initializable {
    @FXML
    private ChoiceBox<String> modeChoiceBox;
    @FXML
    private Label selectFileLabel;
    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button sendFileButton;
    @FXML
    private Button selectFileButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;
    @FXML
    private Label loginLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label encryptionModeLabel;
    @FXML
    private Button testKeysButton;

    // when on two different machines keySenderPort and keyReceiverTargetPort should be equal
    // when on two different machines keyReceiverPort and keySenderTargetPort should be equal
    // I think at least, haven't tested it
    private int keySenderPort = 8084;
    private int keyReceiverPort = 8083;
    private int keySenderTargetPort = 8086;
    private int keyReceiverTargetPort = 8085;

    // when on two different machines fileSenderPort and fileReceiverPort should be equal
    private int fileSenderPort = 8088;
    private int fileReceiverPort = 8087;

    private FileReceiver fileReceiver;
    private KeySender keySender;
    private ExecutorService sendingExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService receivingExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService keySendExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService keyReceiveExecutor = Executors.newSingleThreadExecutor();
    private File file = null;
    private User user;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> availableChoices = FXCollections.observableArrayList("ECB", "CBC", "CTR", "OFB", "CFB");
        modeChoiceBox.setItems(availableChoices);
        modeChoiceBox.setValue("ECB");
        selectFileLabel.setText("File not selected");
    }

    /**
     * Load file and display it's name
     */
    public void selectFile(){
        FileSelector fileSelector = new FileSelector();
        file = fileSelector.selectFile();
        selectFileLabel.setText(file.getName());
    }

    /**
     * Send loaded file (for testing purposes file is not encrypted yet)
     */
    public void sendFile() throws NoSuchAlgorithmException{
        Data data = new Data();
        data.setFile(file);
        progressLabel.setText("Sending...");
        progressBar.setVisible(true);
        progressBar.setProgress(0);
        if(modeChoiceBox.getValue().equals("ECB")){
            data.setCipherMode(CipherMode.ECB);
        }
        else if(modeChoiceBox.getValue().equals("CBC")){
            data.setCipherMode(CipherMode.CBC);
        }
        KeyRequester keyRequester = new KeyRequester(keyReceiverTargetPort, keyReceiverPort);
        keyReceiveExecutor.submit(keyRequester);
        try {
            keyReceiveExecutor.awaitTermination(1, TimeUnit.SECONDS);
        }catch(InterruptedException e){
            System.err.println(e.getMessage());
        }
        PublicKey publicKey = keyRequester.getKey();
        sendingExecutor.submit(new FileSender(data, progressBar, progressLabel, new KeyPair(publicKey, null), fileSenderPort));
    }

    /**
     * Shut down all threads when closing window
     */
    public void shutdown(){
       fileReceiver.shutdown();
       receivingExecutor.shutdown();
       sendingExecutor.shutdown();
    }

    public void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        RsaKeyPairManager manager = new RsaKeyPairManager(username, password, "./users");
        user = new User(username, password, manager.getKeyPair());

        fileReceiver = new FileReceiver(user.getKeyPair(), fileReceiverPort);
        receivingExecutor.submit(fileReceiver);
        keySender = new KeySender(user.getKeyPair().getPublic(), keySenderPort, keySenderTargetPort);
        keySendExecutor.submit(keySender);

        loginLabel.setVisible(false);
        passwordLabel.setVisible(false);
        usernameField.setVisible(false);
        passwordField.setVisible(false);
        loginButton.setVisible(false);

        sendFileButton.setVisible(true);
        encryptionModeLabel.setVisible(true);
        modeChoiceBox.setVisible(true);
        selectFileButton.setVisible(true);
        selectFileLabel.setVisible(true);
        testKeysButton.setVisible(true);

    }

    public void testKeys() {
        try {
            SecretKey firstKey = KeyGenerator.getInstance("AES").generateKey();
            AsymmetricalCipher cipher = new RsaCipher(user.getKeyPair());
            byte[] encryptedKeyBytes = cipher.encrypt(firstKey.getEncoded());
            byte[] sentEncryptedKeyBytes = Base64.getDecoder().decode(Base64.getEncoder().encodeToString(encryptedKeyBytes));
            byte[] decryptedKeyBytes = cipher.decrypt(sentEncryptedKeyBytes);
            SecretKey testKey = new SecretKeySpec(Arrays.copyOfRange(decryptedKeyBytes, 112, 128), 0, 16, "AES");
            if(firstKey.equals(testKey)) {
                System.out.println("RÓWNE");
            } else {
                System.out.println("BRZYDKO");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
