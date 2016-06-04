import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
            if (!socketList.contains(s.getPort())) {
                socketList.addSocket(s, false, agentID);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        SocketData data = socketList.get(agentID);
        Thread write = new SocketWriteThread(data.getBuffer(), data.getOut());
        Thread read = new SocketReadThread(data);
        write.start();
        read.start();
        return data;
    }

    public void extend(int extenderID, int circuitNum, String body) {
        ExtendTarget target = new ExtendTarget(body);

        if (socketList.contains(extenderID)) {
            create(extenderID, circuitNum, target.id);
        } else {
            Thread open = new OpenThread(target.host, target.port, target.id, extenderID, circuitNum);
            open.start();

        }
    }

    public void createReceived(int inSocketID, int circuitID) {
        routingTable.addExit(new Pair(circuitID, inSocketID));
        CircuitObject created = new CircuitObject(circuitID, 2);
        try {
            socketList.get(inSocketID).getBuffer().put(created.getBytes());
        } catch (InterruptedException e) {
            e.printStackTrace();
            //TODO: HANDLE ERROR?
        }
    }

    public void create(int extenderID, int extenderCircuitID, int extendTargetID) {
        SocketData outData = socketList.get(extendTargetID);
        int newCircuitID = routingTable.stage(extendTargetID, extenderCircuitID, extenderID, outData.isOwned());
        CircuitObject c = new CircuitObject(newCircuitID, 1);
        try {
            outData.getBuffer().put(c.getBytes());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean created(int extendTargetID, int extendedCircuitID) {
        Pair outSocketInfo = routingTable.unstage(extendedCircuitID, extendTargetID);
        if (!outSocketInfo.isEntry()) {
            SocketData data = socketList.get(outSocketInfo.getSocket());
            RelayObject r = new RelayObject(extendedCircuitID, 0, 0, 7);
            try {
                data.getBuffer().put(r.getBytes());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                commandQ.put("created");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public class ExtendTarget {

        public String host;
        public int port;
        public int id;

        public ExtendTarget(String body) {
            String[] split = body.split("\0");
            String[] hostPort = split[0].split(":");
            host = hostPort[0];
            port = Integer.parseInt(hostPort[1]);
            id = Integer.parseInt(split[1]);
        }
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
                OpenObject open = new OpenObject(myID, targetSocketID, 5);
                data.getOut().write(open.getBytes());
                byte[] response = new byte[512];
                data.getIn().read(response);
                OpenObject opened = (OpenObject)Decoder.decode(response);
                if (opened.getMessageType() == MessageType.OPENED) {
                    socketList.addSocket(data, data.getPort());
                    Thread write = new SocketWriteThread(data.getBuffer(), data.getOut());
                    Thread read = new SocketReadThread(data);
                    write.start();
                    read.start();
                    create(inID, inCircuitID, data.getPort());
                } else {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
