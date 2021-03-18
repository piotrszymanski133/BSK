package sample;
import java.io.File;

public interface FileEncryptor {
    File cipher(File file);
    File decipher(File file);
}