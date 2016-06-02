import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by josephkesting on 5/31/16.
 */
public class SocketManager {
    private static SocketManager instance = null;
    private SocketList socketList;
    private RoutingTable routingTable;
    private int myID;
    private BlockingQueue<String> commandQ;

    private SocketManager(int myID) {
        this.myID = myID;
        socketList = new SocketList();
        routingTable = new RoutingTable();
        commandQ = new LinkedBlockingQueue<>();
    }

    public static SocketManager getInstance(int myID) {
        if (instance == null) {
            instance = new SocketManager(myID);
        }
        return instance;
    }

    public BlockingQueue<String> getCommandQ() {
        return commandQ;
    }

    public SocketList getSocketList() {
        return socketList;
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    public int getMyID() {
        return myID;
    }

    public void destroy(int circuitNum, int id) {
        routingTable.remove(new Pair(circuitNum, id));
    }

    public SocketData connectionOpened(Socket s, int agentID) {
        try {
            socketList.addSocket(s, false, agentID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socketList.get(agentID);
    }

    public void extend(int extenderID, int circuitNum, int extendTargetID, String body) {
        Pair current = new Pair(circuitNum, extenderID);
        Pair endpoint = routingTable.get(current);
        if(endpoint.isExit()) {
            if (socketList.contains(extenderID)) {
                create(extenderID, circuitNum, extendTargetID);
            } else {
                Thread open = new OpenThread("", 0, extendTargetID, extenderID, circuitNum);
                open.start();
            }
        } else {
            DataOutputStream s = socketList.get(extenderID).getOut();
            RelayObject r = new RelayObject(endpoint.getCircuit(), 0, body.length(), 6, body);
            try {
                s.write(r.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void create(int extenderID, int extenderCircuitID, int extendTargetID) {
        SocketData outData = socketList.get(extendTargetID);
        int newCircuitID = routingTable.stage(extendTargetID, extenderCircuitID, extenderID, outData.isOwned());
        //Send create Message to appropriate socket
        CircuitObject c = new CircuitObject(newCircuitID, 1);
        try {
            outData.getOut().write(c.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean created(int extendTargetID, int extendedCircuitID) {
        Pair outSocketInfo = routingTable.unstage(extendTargetID, extendedCircuitID);
        SocketData data = socketList.get(outSocketInfo.getSocket());
        RelayObject r = new RelayObject(extendedCircuitID, 0, 0, 7);
        //TODO send EXTENDED message to appropriate socket
        try {
            data.getOut().write(r.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public class OpenThread extends Thread {

        private String host;
        private int port;
        private int targetSocketID;
        private int inID;
        private int inCircuitID;

        public OpenThread(String h, int p, int extendTargetID, int extenderID, int extenderCircuitID) {
            host = h;
            port = p;
            targetSocketID = extendTargetID;
            inID = extenderID;
            inCircuitID = extenderCircuitID;
        }

        public void run() {
            Socket socket = new Socket();
            SocketData data;
            try {
                socket.bind(new InetSocketAddress(0));
                socket.connect(new InetSocketAddress(host, port));
                data = new SocketData(socket, true, targetSocketID);
                byte[] openMessage = new byte[10]; //REPLACE W/ MICHA's method
                data.getOut().write(openMessage);
                byte[] response = new byte[512];
                data.getIn().read(response);
                //Parse response
                boolean success = true; //
                if (success) {
                    socketList.addSocket(data, targetSocketID);
                    //TODO: Create listener threads
                    create(inID, inCircuitID, targetSocketID);
                } else {
                    socket.close();
                }
            } catch (IOException e) {
                //socket.close();
                e.printStackTrace();
            }
        }
    }
}
