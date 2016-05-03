
import java.net.*;
import java.io.*;
import java.sql.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;



public class Server extends Thread
{
       private ServerSocket serverSocket;
       Socket server;

       public Server(int port) throws IOException, SQLException, ClassNotFoundException, Exception
       {
          serverSocket = new ServerSocket(port);
          serverSocket.setSoTimeout(180000);
       }

       public void run()
       {
           while(true)
          { 
               try
               {
                  server = serverSocket.accept();
                  DataInputStream din=new DataInputStream(server.getInputStream());
                  DataOutputStream dout=new DataOutputStream(server.getOutputStream());

                  dout.writeUTF("server: -i am greeting server");
                  dout.writeUTF("server:- hi! hello client");

                  System.out.println(din.readUTF());
                  System.out.println(din.readUTF());

                  BufferedImage img=ImageIO.read(ImageIO.createImageInputStream(server.getInputStream()));

                  System.out.println("Image received!!!!"); 
                  //lblimg.setIcon(img);
              }
             catch(SocketTimeoutException st)
             {
                   System.out.println("Socket timed out!");
                  break;
             }
             catch(IOException e)
             {
                  e.printStackTrace();
                  break;
             }
             catch(Exception ex)
            {
                  System.out.println(ex);
            }
          }
       }
       
       public static void main(String [] args) throws IOException, SQLException, ClassNotFoundException, Exception
       {
              //int port = Integer.parseInt(args[0]);
              Thread t = new Server(6066);
              t.start();
       }
}