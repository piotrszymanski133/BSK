package sample;

import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
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

import java.io.IOException;
import java.net.URL;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
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

    private int keySenderPort = 8086;
    private int keyReceiverPort = 8085;
    //private int fileSenderPort = 8088;
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
        KeyRequester keyRequester = new KeyRequester(keySenderPort, keyReceiverPort);
        keyReceiveExecutor.submit(keyRequester);
        try {
            keyReceiveExecutor.awaitTermination(10, TimeUnit.SECONDS);
        }catch(InterruptedException e){
            System.err.println(e.getMessage());
        }
        PublicKey publicKey = keyRequester.getKey();
        sendingExecutor.submit(new FileSender(data, progressBar, progressLabel, new KeyPair(publicKey, null), fileReceiverPort));
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
        keySender = new KeySender(user.getKeyPair().getPublic(), keySenderPort, keyReceiverPort);
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
    }
}
