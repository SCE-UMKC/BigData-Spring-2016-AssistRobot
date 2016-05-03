import java.net.*;
import java.io.*;
import java.awt.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;


public class Client
{
    Image newimg;
    BufferedImage bimg;
    byte[] bytes;

   public static void main(String [] args)
   {
      String serverName = "localhost";
      int port = 6066;
      try
      {
         System.out.println("Connecting to " + serverName
                             + " on port " + port);
         Socket client = new Socket(serverName, port);

         System.out.println("Just connected to "
                      + client.getRemoteSocketAddress());

        DataInputStream in=new DataInputStream(client.getInputStream());
        System.out.println(in.readUTF());
        System.out.println(in.readUTF());

         DataOutputStream out =
                       new DataOutputStream(client.getOutPutStream());

         out.writeUTF("Hello from "
                      + client.getLocalSocketAddress());
         out.writeUTF("client: hello to server");

         ImageIcon img1=new ImageIcon("C:\\Users\\npdar\\Desktop\\Practise\\Priya.jpg");
         Image img = img1.getImage();
         Image newimg = img.getScaledInstance(100, 120,  java.awt.Image.SCALE_SMOOTH);
         ImageIcon newIcon = new ImageIcon(newimg);

         bimg = ImageIO.read(new File("C:\\Users\\npdar\\Desktop\\Practise\\IMG_2654.PNG"));

         ImageIO.write(bimg,"JPG",client.getOutputStream());
         System.out.println("Image sent!!!!");
         client.close();
      }catch(IOException e)
      {
         e.printStackTrace();
      }
   }
}
