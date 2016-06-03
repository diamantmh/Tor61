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
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;


        while(true) {

            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Thread opened = new SocketOpenedThread(clientSocket);
            opened.start();
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
            OpenObject open = (OpenObject) Decoder.decode(openMessage);
            int agentID = open.getOpenerID();
            SocketData data = manager.connectionOpened(s, agentID);
            OpenObject opened = new OpenObject(open.getOpenerID(), open.getOpenedID(), 6);
            try {
                data.getBuffer().put(opened.getBytes());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
