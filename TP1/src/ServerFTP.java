import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
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
        String msgError = "550 I dident find it\r\n";
        String msgPingOk = "200 PING command ok\r\n";
        String msgErroreDir = "500 Please do dir first\r\n";
        String msgTypeAccept = "200 Accepted\r\n";
        String msqLogOut = "231 You are loged out\r\n";
        String msgUsrName = "220 Enter your username:\r\n";
        String msgPassIsValid = "230 Log in successfule\r\n";
        String msgFeatures = "211 feature\r\n";
        String msgUsrValid = "331 User Valid\r\n";
        String passWord = "car";
        String usrName = "miage";
        String str, message, fileName, chemainFile = "", nomFishier;
        String[] fichiers = null;
        int nombreLine;

        // donner un port à mon server
        myServer = new ServerSocket(myPort);
        System.out.println("Ready to connect...");

        // Connecte mon client à mon server
        myClient = myServer.accept();
        System.out.println("We are connected with port 2121");

        OutputStream out = myClient.getOutputStream();
        InputStream in = myClient.getInputStream();
        Scanner scanner = new Scanner(in);

        // Message sur client pour demander identifiant

        while (isConnecte) {

            str = scanner.nextLine();
            System.out.println(str);
            // spliter le message
            message = str.split("\\s+")[0];

            // Verifie Nom d'utilisateur
            if (str.equals("USER " + usrName)) {
                System.out.println("Welcom " + usrName);
                out.write(msgUsrValid.getBytes());
            }

            // Verifiez mot de pass
            if (str.equals("PASS " + passWord)) {
                out.write(msgPassIsValid.getBytes());
                System.out.println("Login successful");
            }

            // Traiter le message du client
            switch (message) {

                case "SYST":
                    out.write(msgUnix.getBytes());
                    break;

                case "FEAT":
                    out.write(msgFeatures.getBytes());
                    break;

                case "TYPE":
                    out.write(msgTypeAccept.getBytes());
                    break;

                case "LIST":
                    myClientData = myServerData.accept();
                    out.write(msgAccepDateConnection.getBytes());
                    System.out.println("connected to new port");
                    OutputStream outDate = myClientData.getOutputStream();

                    if (chemainFile == "") {
                        // recupere les fichier sur serveur
                        chemainFile = System.getProperty("user.dir");
                    }
                    System.out.println(chemainFile);
                    File repertoirFile = new File(chemainFile);
                    // Verifier si le chemain est un repertoir
                    if (repertoirFile.isDirectory()) {
                        fichiers = repertoirFile.list();
                        // Afficher la list des fichiers
                        for (String fichier : fichiers) {
                            String imp = fichier + "\r\n";
                            outDate.write(imp.getBytes());
                        }

                    }
                    // Fermez les port data
                    outDate.close();
                    myServerData.close();
                    myClientData.close();
                    out.write(msgCloseServerSocket.getBytes());
                    break;

                // Traiter le message CD
                case "CWD":
                    // recuperez le nom de dossier
                    fileName = str.split("\\s+")[1];

                    // verifiez que user deja fait un dir
                    if (fichiers != null) {
                        chemainFile = chemainFile + "/" + fileName;
                        System.out.println(fileName);
                        out.write(msgAcctionSucces.getBytes());
                    } else {
                        out.write(msgErroreDir.getBytes());
                    }
                    break;

                // Traiter message Get, EPSV mode passif
                case "EPSV":
                    out.write(msgPassive.getBytes());
                    // Cree nouvaue port
                    myServerData = new ServerSocket(myPortData);
                    break;

                case "RETR":
                    fileName = str.split("\\s+")[1];
                    myClientData = myServerData.accept();
                    out.write(msgAccepDateConnection.getBytes());
                    System.out.println("connected to new port");

                    // recupere le fichier:
                    String filePath = chemainFile + "/" + fileName;
                    File file = new File(filePath);
                    InputStream filInput = new FileInputStream(file);
                    try (OutputStream outPut = myClientData.getOutputStream()) {
                        if (file.exists()) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = filInput.read(buffer)) != -1) {
                                outPut.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                    myServerData.close();
                    out.write(msgCloseServerSocket.getBytes());
                    break;

                case "PING":
                    out.write(msgPingOk.getBytes());
                    out.write(("PONG\r\n").getBytes());
                    break;

                case "LINE":
                    nomFishier = str.split("\\s+")[1];
                    nombreLine = Integer.parseInt(str.split("\\s+")[2]);
                    myClientData = myServerData.accept();
                    out.write(msgAccepDateConnection.getBytes());
                    OutputStream outData = myClientData.getOutputStream();
                    File file = new File(nomFishier);
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        int lineCounter = 1;
                        while ((line = reader.readLine()) != null) {
                            if (nombreLine == lineCounter) {
                                outData.write(line.getBytes());
                                break;
                            }
                            lineCounter++;
                        }
                        // Si il ne trouve pas le ligne
                        outData.write(msgError.getBytes());
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    break;

                case "QUIT":
                    out.write(msqLogOut.getBytes());
                    scanner.close();
                    isConnecte = false;
                    break;
            }
        }

    }

    public static void main(String[] args) throws IOException {
        start();
    }
}
