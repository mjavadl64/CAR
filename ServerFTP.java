import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerFTP {
    private static int myPort = 2020;
    private static ServerSocket myServer;
    private static Socket myClient;
    private static Boolean chek = true;

    private static void start() throws IOException {

        String msqDisconnect = "426 You are loged out\r\n";
        String msgUsrName = "220 Enter your username:\r\n";
        String msgPassValid = "230 Log in successfule\r\n";
        String msgErrore = "500 Try again:";
        String msgUsrValid = "331 User Valid\r\n";
        String passWord = "123456";
        String usrName = "javad";
        String pass, usr, quite;

        // donner un port à mon server
        myServer = new ServerSocket(myPort);
        System.out.println("Ready to connect...");

        // Connecter mon client à mon server
        myClient = myServer.accept();
        System.out.println("We are connected with port 2020");

        // Message sur client pour demander identifiant
        OutputStream out = myClient.getOutputStream();
        out.write(msgUsrName.getBytes());

        // Recevoir la reponce d'identifiant de client
        InputStream in = myClient.getInputStream();
        Scanner scanner = new Scanner(in);
        usr = scanner.nextLine();

        // Verifier identifiant
        if (usr.equals("USER " + usrName)) {
            System.out.println("Welcom " + usrName);
            out.write(msgUsrValid.getBytes());
            chek = false;
        } else {
            System.out.println("Your Username is incorrect, try again:");
            out.write(msgErrore.getBytes());
        }
        // Recupere mot de pass
        pass = scanner.nextLine();

        // Verifier mot de pass
        if (pass.equals("PASS " + passWord)) {
            System.out.println("You are loge in");
            out.write(msgPassValid.getBytes());

        } else {
            System.out.println("Your password is incorrect");
            out.write(msgErrore.getBytes());
        }

        // Bouqule d'attendre de tapper quite
        while (chek) {
            quite = scanner.nextLine();
            if (quite.equals("QUIT")) {
                out.write(msqDisconnect.getBytes());
                chek = false;
            } else {
                out.write(msgErrore.getBytes());
            }

        }

        scanner.close();

    }

    public static void main(String[] args) throws IOException {
        start();
    }
}