package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.File;
import javafx.scene.control.Label;

public class Controller implements Initializable {
    @FXML
    private Button selectFileButton;
    @FXML
    private ChoiceBox<String> modeChoiceBox;
    @FXML
    private Label selectFileLabel;

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


}
