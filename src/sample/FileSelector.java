package sample;

import java.io.File;
import javafx.stage.FileChooser;

public class FileSelector {
    private FileChooser fileChooser;
    public FileSelector() {
        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("Select a file to cipher");
    }
    public File selectFile() {
        File selectedFile = fileChooser.showOpenDialog(null);
        return selectedFile;
    }
}
