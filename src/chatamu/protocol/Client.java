package chatamu.protocol;

import chatamu.exception.LoginException;
import chatamu.exception.MessageException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {

    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private SocketChannel socket;

    public Client(String server,int port)
    {
        try {
            this.clientSocket = new Socket();
            this.clientSocket.connect(new InetSocketAddress(server, port));
            this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
        }

        catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    public void init()
    {
        try
        {
            System.out.println("Veuillez choisir un pseudo...");
            Scanner scanner = new Scanner(System.in);
            String pseudo = scanner.nextLine();
            this.out.write(pseudo);
            String response = this.in.readLine();
            if (Integer.parseInt(response) == Protocol.PREFIX.ERR_LOG.ordinal()) throw new LoginException();
        }
        catch(LoginException exception)
        {
            System.out.println(exception.getMessage());
        }
        catch (IOException exception)
        {
         exception.printStackTrace();
        }
    }

    public void process() {
        try {

//            Scanner scanner = new Scanner(System.in);
//            String init_message = this.in.readLine();
//            System.out.println(init_message);
//            String pseudo = scanner.nextLine();
//            this.out.write(pseudo);
//            this.out.newLine();
//            this.out.flush();

            Thread thread_rcv = new Thread(new HandleReceive(this, this.in));
            thread_rcv.start();



            //todo Faire un booléen propre (Gérer la terminaison du while proprement)
            boolean bool = true;
            while(bool)
            {


                Scanner scanner = new Scanner(System.in);
                String message = scanner.nextLine();
                this.out.write(Protocol.PREFIX.MESSAGE.toString() + message);
                this.out.newLine();
                this.out.flush();
                if (message.equals("STOP")) {
                    this.in.close();
                    this.out.close();
                    scanner.close();
                    this.clientSocket.close();
                    bool = false;
                }
            }
        }
        catch(Exception exception) {
            exception.printStackTrace();
        }
    }


    private final class HandleReceive implements Runnable {
        private BufferedReader in;
        private Client client;

        HandleReceive(Client client, BufferedReader in)
        {
            this.client = client;
            this.in = in;
        }


        @Override
        public void run() {
            try
            {
                while (true)
                {
                    String response = in.readLine();
                    if (response != null)
                        System.out.println(response);
                }

            }
            catch (IOException e)
            {
                System.out.println("Vous avez quitté le salon.");
            }

//            catch (MessageException exception) {
//                System.out.println(exception.getMessage());
//            }
        }
    }

    public void setClientSocket(SocketChannel socket) { this.socket = socket; }
    public SocketChannel getSocket() { return this.socket; }
}
