package in.Kaulk.NetLib.examples;///////
//An example of how to use the in.Kaulk.NetLib client side
///////

import in.Kaulk.NetLib.*;
import in.Kaulk.NetLib.Client;
import in.Kaulk.NetLib.Message;

import java.util.Scanner;

public class ClientTest {

    public static void main(String args[]){

        //Create a client object to interact with your server
        Client c = new Client("localhost", 5555, false);
        System.out.println("Connected");
        //Create a message object (You can use any object that is serializable as the parameter)
        Message msg = new Message("Hello m8");
        //Send the message object through client.send
        c.send(msg);
        System.out.println("Sent");
        //get the next message from the client
        Message recv = c.getNextMessage();
        System.out.println("Recv");
        //cast the recieved object to string (Or whatever you expect it to be)
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
