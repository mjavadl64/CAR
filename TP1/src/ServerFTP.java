import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerFTP {
    private static int myPort = 2121, myPortData = 2024;
    private static ServerSocket myServer, myServerData;
    private static Socket myClient, myClientData;
    private static Boolean isConnecte = true;

    private static void start() throws IOException {

        String msgPassive = "229 Entering Extended Passive Mode (|||2024|)\r\n";
        String msgAccepDateConnection = "150 Accept data connection \r\n";
        String msgCloseServerSocket = "226 File successfully transferd\r\n";
        String msgAcctionSucces = "250 Action successful\r\n";
        String msgUnix = "215 UNIX Type: L8\r\n";
        String msgNotLogedin = "430 Try again:\r\n";
        String msgRepertoir = "212 You are inside\r\n";
        String msgWindonw = "200 accepted\r\n";
        String msgTypeAccept = "200 Accepted\r\n";
        String msqLogOut = "231 You are loged out\r\n";
        String msgUsrName = "220 Enter your username:\r\n";
        String msgPassIsValid = "230 Log in successfule\r\n";
        String msgFeatures = "211 feature\r\n";
        String msgUsrValid = "331 User Valid\r\n";
        String passWord = "1";
        String usrName = "javad";
        String str, message, fileName;

        // donner un port à mon server
        myServer = new ServerSocket(myPort);
        System.out.println("Ready to connect...");

        // Connecter mon client à mon server
        myClient = myServer.accept();
        System.out.println("We are connected with port 2121");

        OutputStream out = myClient.getOutputStream();
        InputStream in = myClient.getInputStream();
        Scanner scanner = new Scanner(in);

        // Message sur client pour demander identifiant
        out.write(msgUsrName.getBytes());

        // Verifier identifiant
        str = scanner.nextLine();
        if (str.equals("USER " + usrName)) {
            System.out.println("Welcom " + usrName);
            out.write(msgUsrValid.getBytes());

        } else {
            System.out.println(str);
            System.out.println("Your Username is incorrect, try again:");
            out.write(msgNotLogedin.getBytes());
        }

        // Verifier mot de pass
        str = scanner.nextLine();
        if (str.equals("PASS " + passWord)) {
            System.out.println("You are loge in");
            out.write(msgPassIsValid.getBytes());
            System.out.println(str);
        } else {
            out.write(msgNotLogedin.getBytes());
            System.out.println(str);
            System.out.println("Your password is incorrect");
        }

        while (isConnecte) {

            str = scanner.nextLine();
            System.out.println(str);
            // spliter le message
            message = str.split("\\s+")[0];
            

            if (message.equals("SYST")) {
                out.write(msgUnix.getBytes());
            }

            if (message.equals("FEAT")) {
                out.write(msgFeatures.getBytes());
            }

            if (message.equals("TYPE")) {
                out.write(msgTypeAccept.getBytes());
                System.out.println(str);
            }

            //traiter le message LIST
            if (message.equals("LIST")){
                myClientData = myServerData.accept();
                out.write(msgAccepDateConnection.getBytes());
                System.out.println("connected to new port");

                OutputStream outDate = myClientData.getOutputStream();

                //recupere les fichier sur serveur
                String chemain = System.getProperty("user.dir");
                File repertoirFile = new File(chemain);

                //Verifier si le chemain est un repertoir
                if (repertoirFile.isDirectory()){
                    String[] fichiers = repertoirFile.list();

                    //Afficher la list des fichiers
                    for (String fichier : fichiers){
                        String imp = fichier+"\r\n";
                        outDate.write(imp.getBytes()); 
                    }
                    
                }

                outDate.close();
                myServerData.close();
                myClientData.close();

                out.write(msgCloseServerSocket.getBytes());             
                
            }

            // Traiter le message CD 
            if (message.equals("CWD")){
                fileName = str.split("\\s+")[1];
                System.out.println(str);
                System.out.println(fileName);
                out.write(msgAcctionSucces.getBytes());

            }

            //Traiter message Get, EPSV mode passif 
            if (message.equals("EPSV")) {
                System.out.println(str);
                out.write(msgPassive.getBytes());
                // Cree nouvaue port
                myServerData = new ServerSocket(myPortData);

            }

            if (message.equals("RETR")) {

                fileName = str.split("\\s+")[1];    
                myClientData = myServerData.accept();
                out.write(msgAccepDateConnection.getBytes());
                System.out.println("connected to new port");

                // recupere le fichier:

                OutputStream outPut = myClientData.getOutputStream();
                File file = new File(fileName);
                InputStream filInput = new FileInputStream(file);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = filInput.read(buffer)) != -1) {
                    outPut.write(buffer, 0, bytesRead);
                }

                outPut.close();
                myServerData.close();
                myClientData.close();

                out.write(msgCloseServerSocket.getBytes());

            }

            if (message.equals("QUIT")) {
                System.out.println(message);
                out.write(msqLogOut.getBytes());
                scanner.close();
                isConnecte = false;
            }
        }

    }

    public static void main(String[] args) throws IOException {
        start();
    }
}
