/**
 * Created by michaeldiamant on 5/30/16.
 */
import java.io.*;
import java.net.*;

public class Thing {
    private ServerSocket server;


    public Thing(int port) {

        System.out.println("listening on port: " + port);

        server = null;
        try {
            server = new ServerSocket(port);
            //cse461.cs.washington.edu:46101
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void send(String host, int port, byte[] message) {
        try {
            Socket hostSocket = new Socket();
            InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 12345);
            hostSocket.bind(addr);
            hostSocket.connect(new InetSocketAddress(host, port));
            DataOutputStream outToClient = new DataOutputStream(hostSocket.getOutputStream());
            outToClient.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        Thread l = new listen();
        l.start();
    }



    protected void finalize() {
        try{
            server.close();
        } catch (IOException e) {
            System.out.println("Could not close socket");
            System.exit(-1);
        }
    }

    class readThread extends Thread {

        private DataInputStream in;
        private DataOutputStream out;

        readThread(DataInputStream in, DataOutputStream out) {
            this.in = in;
            this.out = out;
        }

        public void run () {
            try {
                do {
                    //String b = in.readLine();
                    byte[] b = new byte[512];
                    in.readFully(b);
                    for(int i = 0; i < b.length; i++) {
                        System.out.print(" " + b[i]);
                    }
                    System.out.println("from:" + out);
                    System.out.println("on:" + in);

                    //out.writeBytes("you sent: " + b + '\n');
                } while (true);
            } catch (IOException e) {
                System.out.println("pipe broken");
            }
        }
    }

    class listen extends Thread {

        public void run () {
            while(true) {
                Socket connectionSocket = null;
                try {
                    connectionSocket = server.accept();
                    String from = "" + connectionSocket.getRemoteSocketAddress();
                    System.out.println(from);

                    Socket hostSocket = new Socket();
                    InetSocketAddress addr = new InetSocketAddress(0);
                    hostSocket.bind(addr);
                    System.out.println("new server address: " + hostSocket.getLocalAddress());
                    String hostname = from.split(":")[0].substring(1);
                    int port = Integer.parseInt(from.split(":")[1]);
                    System.out.println(hostname + ":" + port);
                    //hostSocket.connect(new InetSocketAddress(hostname, port));
                    System.out.println(hostSocket.isConnected());

                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    //DataOutputStream outToClient = new DataOutputStream(hostSocket.getOutputStream());

                    //BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());
                    Thread read = new readThread(inFromClient, outToClient);
                    read.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
