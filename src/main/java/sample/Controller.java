package sample;

import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import sample.cipher.Data;
import sample.communication.FileReceiver;
import sample.communication.FileSender;
import sample.communication.KeyRequester;
import sample.communication.KeySender;
import sample.rsa_keys.RsaKeyPairManager;
import sample.user.User;
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
    @FXML
    private Label receivingProgressLabel;

    // when on two different machines keySenderPort and keyReceiverTargetPort should be equal
    // when on two different machines keyReceiverPort and keySenderTargetPort should be equal
    // I think at least, haven't tested it
    private int keySenderPort = 8085;
    private int keyReceiverPort = 8086;
    private int keySenderTargetPort = 8086;
    private int keyReceiverTargetPort = 8085;

    // when on two different machines fileSenderPort and fileReceiverPort should be equal
    private int fileSenderPort = 8088;
    private int fileReceiverPort = 8088;

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
        ObservableList<String> availableChoices = FXCollections.observableArrayList("ECB", "CBC", "OFB", "CFB");
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
        KeyRequester keyRequester = new KeyRequester(keyReceiverTargetPort, keyReceiverPort);
        keyReceiveExecutor.submit(keyRequester);
        try {
            keyReceiveExecutor.awaitTermination(1, TimeUnit.SECONDS);
        }catch(InterruptedException e){
            System.err.println(e.getMessage());
        }
        PublicKey publicKey = keyRequester.getKey();
        sendingExecutor.submit(new FileSender(data, progressBar, progressLabel, new KeyPair(publicKey, null), fileSenderPort, modeChoiceBox.getValue()));
    }

    /**
     * Shut down all threads when closing window
     */
    public void shutdown(){
       fileReceiver.shutdown();
       receivingExecutor.shutdown();
       sendingExecutor.shutdown();
       keySender.shutdown();
       keySendExecutor.shutdown();
       keyReceiveExecutor.shutdown();
    }

    public void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        RsaKeyPairManager manager = new RsaKeyPairManager(username, password, "./users");
        user = new User(username, password, manager.getKeyPair());

        fileReceiver = new FileReceiver(user.getKeyPair(), fileReceiverPort, receivingProgressLabel);
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
    }
}
