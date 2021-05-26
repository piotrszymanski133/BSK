package sample.communication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyRequester  implements Runnable{
    private PublicKey publicKey;
    private String ip = "127.0.0.1";
    private int port;

    public KeyRequester(int port) {
        this.port = port;
    }

    public PublicKey getKey() {
        return this.publicKey;
    }

    @Override
    public void run() {

        try{
            Socket socket = new Socket("127.0.0.1", port);
            try(DataOutputStream os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
                os.writeUTF(ip);
            }
        }catch(Exception e){
            System.err.println(e.getMessage());
        }


        try (ServerSocket serverSocket = new ServerSocket(port)){
            serverSocket.setSoTimeout(1000);
            while (true) {
                try {
                    final Socket socket = serverSocket.accept();
                    try(DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {
                        String keyString = is.readUTF();
                        KeyFactory kf = KeyFactory.getInstance("RSA");
                        publicKey =  kf.generatePublic(new X509EncodedKeySpec(keyString.getBytes()));
                        break;
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }catch(SocketTimeoutException e){
                    System.err.println(e.toString());
                }
            }
        }catch(IOException e){
            System.err.println(e.toString());
        }
    }
}
