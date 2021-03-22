package sample.communication;

import java.io.*;
import java.net.Socket;

/**
 * Thread responsible for sending encrypted file
 */
public class FileSender implements Runnable{

    private File file;

    public FileSender(File file){
        this.file = file;
    }
    @Override
    public void run() {
        try(FileInputStream inputStream = new FileInputStream(file)) {
            Socket socket = new Socket("127.0.0.1", 8085);
            try(DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
                byte[] buffer = new byte[4096];
                int readed;
                outputStream.writeUTF(file.getName());
                while ((readed = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readed);
                }
            }
        } catch (IOException e){
            System.err.println(e.toString());
        }
    }
}
