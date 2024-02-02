import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client2 {


    private static Socket myClient;


    private static void start () throws IOException{

        String msg;
        myClient = new Socket("localhost", 2121);
        
        InputStream in = myClient.getInputStream();
        Scanner scanner = new Scanner(in);
        OutputStream out = myClient.getOutputStream();

        //Envoyez message PING au serveur
        out.write(("PING\r\n").getBytes());

        //recuperez la reponse du serveur 
        msg = scanner.nextLine();
        if (msg.equals("200 PING command ok")){
            System.out.println("Always active");
        }

        msg = scanner.nextLine();
        if (msg.equals("PONG")){
            out.write(("200 PONG command ok\r\n").getBytes());
        }
        else {
            out.write(("502 Unknown command\r\n").getBytes());
        }
        
        out.write(("QUIT\r\n").getBytes());
        msg = scanner.nextLine();
        out.close();
        myClient.close();
           
    }

    public static void main(String[] args) throws IOException {
        start();
    }
}