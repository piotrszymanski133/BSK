package sample.communication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;

public class KeySender implements Runnable{

    private PublicKey publicKey;
    private AtomicBoolean running = new AtomicBoolean(true);
    private int senderPort;
    private int receiverPort;

    public KeySender(PublicKey publicKey, int senderPort, int receiverPort){
        this.publicKey = publicKey;
        this.senderPort = senderPort;
        this.receiverPort = receiverPort;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(senderPort)){
            serverSocket.setSoTimeout(1000);
            while (running.get()) {
                try {
                    final Socket socket = serverSocket.accept();
                    String ip;
                    try(DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {
                        ip = is.readUTF();
                    }
                    sendKey(ip);
                }catch(SocketTimeoutException e){
                    System.err.println(e.toString());
                }
            }
        }catch(IOException e){
            System.err.println(e.toString());
        }
    }

    private void sendKey(String ip){
        try{
            Socket socket = new Socket("192.168.56.102", receiverPort);
            try(DataOutputStream os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
                os.writeUTF(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
            }
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
}
