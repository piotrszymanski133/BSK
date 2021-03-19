package sample;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileToBytes {
    private File file;

    public FileToBytes(File file){
        this.file = file;
    }

    public byte[] convertFileToBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

}
