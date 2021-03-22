package sample.communication;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class responsible for downloading received files.
 * It works in separate thread during life of application
 */
public class FileReceiver implements Runnable{

    private AtomicBoolean running = new AtomicBoolean(true);

    /**
     * Function responsible for waiting for files to download
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(8088)){
            serverSocket.setSoTimeout(1000);
            while (running.get()) {
                try {
                    final Socket socket = serverSocket.accept();
                    downloadFile(socket);
                }catch(SocketTimeoutException e){}
            }
        }catch(IOException e){
            System.err.println(e.toString());
        }
    }

    /**
     * Breaks the waiting loop
     */
    public void shutdown(){
        running.set(false);
    }

    /**
     * Function responsible for downloading file
     * @param socket client socket
     */
    public void downloadFile(Socket socket){
        try(DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()))){
            String name = is.readUTF();
            try(FileOutputStream os = new FileOutputStream(name)) {
                byte[] buffer = new byte[4096];
                int readSize;
                while ((readSize = is.read(buffer)) != -1) {
                    os.write(buffer, 0, readSize);
                }
                System.out.println("Downloaded " + name);
            }
        }catch(IOException e){
            System.err.println(e.toString());
        }
    }
}
