package sample.communication;

import sample.cipher.Data;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

/**
 * Thread responsible for sending encrypted file
 */
public class FileSender implements Runnable{

    private Data data;

    public FileSender(Data data){
        this.data = data;
    }
    @Override
    public void run() {
        //TODO: CHANGE TO BUFFERED SENDING
        try(ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getDataBytes())) {
            Socket socket = new Socket("127.0.0.1", 8085);
            try(DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
                byte[] buffer = new byte[4096];
                int readed;
                outputStream.writeUTF(data.getCipherMode().toString());
                outputStream.writeUTF(Base64.getEncoder().encodeToString(data.getSecretKey().getEncoded()));
                outputStream.writeUTF(Base64.getEncoder().encodeToString(data.getIvParameterSpec().getIV()));
                outputStream.writeUTF(data.getFileName());
                outputStream.write(data.getDataBytes().length);
                while ((readed = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readed);
                }
            }
        } catch (IOException e){
            System.err.println(e.toString());
        }
    }
}
