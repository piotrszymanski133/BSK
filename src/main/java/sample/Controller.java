package sample;

import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import sample.cipher.CBC;
import sample.cipher.CipherMode;
import sample.cipher.Data;
import sample.cipher.ECB;
import sample.communication.FileReceiver;
import sample.communication.FileSender;

import java.io.IOException;
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

    private FileReceiver fileReceiver;
    private ExecutorService sendingExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService receivingExecutor = Executors.newSingleThreadExecutor();
    private File file = null;
    
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
    public void sendFile() throws NoSuchAlgorithmException, IOException {
        Data data = new Data();
        if(modeChoiceBox.getValue().equals("ECB")){
            data.setCipherMode(CipherMode.ECB);
            ECB ecb = new ECB();
            ecb.encrypt(file, data);
        }
        else if(modeChoiceBox.getValue().equals("CBC")){
            data.setCipherMode(CipherMode.CBC);
            CBC cbc = new CBC();
            cbc.encrypt(file, data);
        }
        sendingExecutor.submit(new FileSender(data));
    }

    /**
     * Shut down all threads when closing window
     */
    public void shutdown(){
       fileReceiver.shutdown();
       receivingExecutor.shutdown();
       sendingExecutor.shutdown();
    }
}
