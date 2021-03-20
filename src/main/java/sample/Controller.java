package sample;

import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import sample.encryption.Encryption;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.File;


public class Controller implements Initializable {
    @FXML
    private Button selectFileButton;
    @FXML
    private ChoiceBox<String> modeChoiceBox;
    @FXML
    private Label selectFileLabel;
    @FXML
    private Button encryptButton;

    private File file = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> availableChoices = FXCollections.observableArrayList("ECB", "CBC", "CTR", "OFB", "CFB");
        modeChoiceBox.setItems(availableChoices);
        modeChoiceBox.setValue("ECB");
        selectFileLabel.setText("File not selected");
    }

    /*
     * Load file and display it's name
     */
    public void selectFile(){
        FileSelector fileSelector = new FileSelector();
        file = fileSelector.selectFile();
        selectFileLabel.setText(file.getName());
    }

    public void encryptFile() throws IOException {
        FileToBytes fileToBytes = new FileToBytes(file);
        byte[] bytes = fileToBytes.convertFileToBytes(file);
        Encryption encryption = new Encryption(bytes);
        encryption.encryptionECB(bytes, file);
    }
}
