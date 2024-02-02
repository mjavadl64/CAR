import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client1 {


    private static Socket myClient;


    private static void start () throws IOException{

        String str;
        myClient = new Socket("localhost", 2121);
        
    
        InputStream in = myClient.getInputStream();
        Scanner scanner = new Scanner(in);
        OutputStream out = myClient.getOutputStream();

        out.write(("USER miage\r\n").getBytes());
        str = scanner.nextLine();
        //System.out.println(str);
        
        out.write(("PASS car\r\n").getBytes());
        str = scanner.nextLine();

        out.write(("QUIT\r\n").getBytes());
        str = scanner.nextLine();
        out.close();
        myClient.close();
           
    }

    public static void main(String[] args) throws IOException {
        start();
    }
}