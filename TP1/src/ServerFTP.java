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
    private static ServerSocket myServer,myServerData;
    private static Socket myClient,myClientData;
    private static Boolean chek = true; 

    private static void start () throws IOException
    {

        String msgPassive = "229 Entering Extended Passive Mode (|||2024|)\r\n";
        String msgAccepDateConnection = "150 Accept data connection \r\n";
        String msgCloseServerSocket = "226 File successfully transferd\r\n";
        String msgUnix = "215 UNIX Type: L8\r\n";
        String msgNotLogedin = "430 Try again:\r\n";
        String msgErrore = "501 Error, try again:\r\n";
        String msgTypeAccept = "200 Type accepted\r\n";
        String msqLogOut = "231 You are loged out\r\n";
        String msgUsrName = "220 Enter your username:\r\n";
        String msgPassIsValid = "230 Log in successfule\r\n";
        String msgFeatures = "211 feature\r\n";
        String msgUsrValid = "331 User Valid\r\n";
        String passWord = "123456";
        String usrName = "javad";
        String pass,usr,quite,str,input,fileName;

        //donner un port à mon server 
        myServer = new ServerSocket(myPort);
        System.out.println("Ready to connect...");

        // Connecter mon client à mon server
        myClient = myServer.accept();
        System.out.println("We are connected with port 2121");

        while (chek) {
           
        //Message sur client pour demander identifiant
        OutputStream out = myClient.getOutputStream();
        out.write(msgUsrName.getBytes());

        //Recevoir la reponce d'identifiant de client 
        InputStream in = myClient.getInputStream();
        Scanner scanner = new Scanner(in);              
        usr = scanner.nextLine();

        //Verifier identifiant
            if (usr.equals("USER "+usrName)){
                System.out.println("Welcom "+ usrName );
                out.write(msgUsrValid.getBytes());  
                      
            }
            else {
                System.out.println(usr);
                System.out.println("Your Username is incorrect, try again:");
                out.write(msgNotLogedin.getBytes());
            }
        //Recupere mot de pass
            pass = scanner.nextLine();
            
        //Verifier mot de pass
            if (pass.equals("PASS "+passWord)) {
                System.out.println("You are loge in");
                out.write(msgPassIsValid.getBytes());
                System.out.println(pass);              
            }             
            else {
                out.write(msgNotLogedin.getBytes());
                System.out.println(pass);
                System.out.println("Your password is incorrect");
            }
            
            str = scanner.nextLine();
            if (str.equals("SYST")){
                out.write(msgUnix.getBytes());
            }
            
            str = scanner.nextLine();
            if (str.equals("FEAT")){
                out.write(msgFeatures.getBytes());
            }

            str = scanner.nextLine();
            if (str.equals("TYPE I")){
                out.write(msgTypeAccept.getBytes());
                System.out.println(str);
            }

            
            str = scanner.nextLine();
            if (str.equals("EPSV")){
                System.out.println(str);
                out.write(msgPassive.getBytes());
                // Cree nouvaue port
                myServerData = new ServerSocket(myPortData);
                
              
            }
            //split le message de client
            str = scanner.nextLine();
            input = str.split("\\s+")[0];
            fileName = str.split("\\s+")[1];


            if (input.equals("RETR")){

                myClientData = new Socket();
                myClientData = myServerData.accept();

                out.write(msgAccepDateConnection.getBytes());
                System.out.println("connected to new port");
                
                // recupere le fichier 
                OutputStream outPut = myClientData.getOutputStream() ;

                //File file = new File("image/"+fileName);
                InputStream filInput = new FileInputStream("src/image/proprioter.jpg");
      

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = filInput.read(buffer)) != -1){
                    outPut.write(buffer,0,bytesRead);
                }

                outPut.close();
                myServerData.close();
                myClientData.close();

                out.write(msgCloseServerSocket.getBytes());

                
                
                
            }

            str = scanner.nextLine();
            if (str.equals("QUIT")){
                System.out.println(str);
                out.write(msqLogOut.getBytes());
                scanner.close();
                chek = false;
            }
            
            //
        }
        

        /*//Bouqule d'attendre de tapper quite    
        while(chek) {
            quite = scanner.nextLine();
            if (quite.equals("QUIT")){
                out.write(msqLogOut.getBytes());
                chek = false;
            }
        */

        
    
        }   

    

     public static void main(String[] args) throws IOException{
    start();
   }
}
