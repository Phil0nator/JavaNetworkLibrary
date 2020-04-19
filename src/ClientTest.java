import com.NetLib.Client;
import com.NetLib.Message;

import java.util.Scanner;

import static java.lang.System.exit;

public class ClientTest {

    public static void main(String args[]){


        Client c = new Client("localhost", 5555, false);
        System.out.println("Connected");
        Message msg = new Message("Hello m8");
        c.send(msg);
        System.out.println("Sent");
        Message recv = c.getNextMessage();
        System.out.println("Recv");
        //recv.printSerialized();
        String message = (String) recv.getObject();
        System.out.println(message);
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.print("Message: ");
            c.send(new Message(scanner.nextLine()));
            System.out.println("Recieved: "+(String)c.getNextMessage().getObject());

        }


    }


}
