import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by josephkesting on 6/1/16.
 */
public class ListenThread extends Thread {

    private int port;
    SocketManager manager;

    public ListenThread(int port) {
        this.port = port;
        manager = SocketManager.getInstance(0);

    }

    public void run () {
        ServerSocket serverSocket = null;


        while(true) {
            try {
                serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                Thread opened = new SocketOpenedThread(clientSocket);
                opened.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class SocketOpenedThread extends Thread {
        private Socket s;

        public SocketOpenedThread (Socket s) {
            this.s = s;
        }

        public void run() {
            byte[] openMessage = new byte[512];
            try {
                s.getInputStream().read(openMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Process message
            int agentID = 0;//TODO
            SocketData data = manager.connectionOpened(s, agentID);
            //Send back opened message
        }
    }
}
