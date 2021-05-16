

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class BioServer {

    private static final int PORT = 8080;

    public static void main(String[] args)throws IOException {

        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT);
            System.out.println("the time server is start in port :"+PORT);

            Socket socket = null;

            while (true){
                socket  =  server.accept();
                new Thread(new TimeServerHandler(socket)).start();
            }

        }catch (Exception e){
            e.printStackTrace();

        }finally {
            if(server != null){
                System.out.println("the time server close");
                server.close();
            }
        }


    }
}
