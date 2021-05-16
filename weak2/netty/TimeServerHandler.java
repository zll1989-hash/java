package net.xdclass.bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class TimeServerHandler implements  Runnable{

    private Socket socket;

    public  TimeServerHandler(Socket socket){
        this.socket = socket;
    }


    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter  out = null;

        try {
            in = new BufferedReader( new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(),true);

            String body = null;

            while (( body = in.readLine())!= null && body.length()!=0){
                System.out.println("the time server receive msg :"+body);
                out.println(new Date().toString());
            }

        } catch (Exception e){

            e.printStackTrace();

        } finally {
            if(in != null){
                try {
                    in.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if(out != null){
                try {
                    out.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(this.socket != null){
                try{
                    this.socket.close();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }

    }

}
