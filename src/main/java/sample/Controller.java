package sample;

import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import sample.asymmetrical_cipher.RsaCipher;
import sample.cipher.Data;
import sample.communication.FileReceiver;
import sample.communication.FileSender;
import sample.rsa_keys.RsaKeyPairManager;
import sample.user.User;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
    private Button selectFileButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;

    private FileReceiver fileReceiver;
    private ExecutorService sendingExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService receivingExecutor = Executors.newSingleThreadExecutor();
    private File file = null;
    private User user;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fileReceiver = new FileReceiver();
        receivingExecutor.submit(fileReceiver);
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
        // Get cipher mode from choicebox
        String cipherMode = modeChoiceBox.getValue();
        sendingExecutor.submit(new FileSender(data, progressBar, progressLabel, cipherMode));
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
        String test = "test testowy testom testowym testuje test";
        RsaCipher rsaCipher = new RsaCipher(user.getKeyPair());
        byte[] encryptedTest = rsaCipher.encrypt(test);
        String decryptedTest = rsaCipher.decrypt(encryptedTest);
        System.out.println(decryptedTest);
    }
}
