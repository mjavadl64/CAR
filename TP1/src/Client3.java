import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client3 {


    private static Socket myClient,myClientData;


    private static void start () throws IOException{

        String msg;
        myClient = new Socket("localhost", 2121);
        
        InputStream in = myClient.getInputStream();
        Scanner scanner = new Scanner(in);
        OutputStream out = myClient.getOutputStream();

        
        out.write(("ESPV").getBytes());

        myClientData = new Socket("localhost",2024);
        OutputStream outData = myClientData.getOutputStream();

        
        
        outData.write(("LINE C3M.txt 42").getBytes());

       
        
        out.write(("QUIT\r\n").getBytes());
        msg = scanner.nextLine();
        out.close();
        myClient.close();
           
    }

    public static void main(String[] args) throws IOException {
        start();
    }
}