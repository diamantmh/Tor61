import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by josephkesting on 5/31/16.
 */
public class SocketList {

    private Map<Integer, SocketData> sockets;

    public SocketList() {
        sockets = new HashMap<>();
    }

    //Wait until open is received?
    public SocketData addSocket (Socket s, boolean owned, int agentID) throws IOException {
        SocketData data = new SocketData(s, owned, agentID);
        sockets.put(agentID, data);
        return data;
    }

    public void addSocket (SocketData data, int agentID) throws IOException {
        sockets.put(agentID, data);
    }

    public SocketData remove (int id) {
        return sockets.remove(id);
    }

    public SocketData get (int id) {
        return sockets.get(id);
    }

    public boolean contains (int id) {
        return sockets.containsKey(id);
    }
}
