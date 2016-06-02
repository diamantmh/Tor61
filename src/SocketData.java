import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by josephkesting on 5/31/16.
 */
public class SocketData {

    private boolean owned;
    private DataOutputStream out;
    private InputStream in;
    private BlockingQueue buffer;
    private int port;
    private int agentID;


    public SocketData(Socket socket, boolean owned, int agentID) throws IOException {
        this.owned = owned;
        out = new DataOutputStream(socket.getOutputStream());
        in = socket.getInputStream();
        buffer = new LinkedBlockingQueue<>();
        this.port = socket.getPort();
        this.agentID = agentID;

    }

    public boolean isOwned() {
        return owned;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public InputStream getIn() {
        return in;
    }

    public BlockingQueue getBuffer() {
        return buffer;
    }

    public int getPort() {
        return port;
    }

    public int getAgentID() {
        return agentID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SocketData that = (SocketData) o;

        if (isOwned() != that.isOwned()) return false;
        if (getOut() != null ? !getOut().equals(that.getOut()) : that.getOut() != null) return false;
        if (getIn() != null ? !getIn().equals(that.getIn()) : that.getIn() != null) return false;
        return getBuffer() != null ? getBuffer().equals(that.getBuffer()) : that.getBuffer() == null;

    }

    @Override
    public int hashCode() {
        int result = (isOwned() ? 1 : 0);
        result = 31 * result + (getOut() != null ? getOut().hashCode() : 0);
        result = 31 * result + (getIn() != null ? getIn().hashCode() : 0);
        result = 31 * result + (getBuffer() != null ? getBuffer().hashCode() : 0);
        result = 31 * result + getPort();
        result = 31 * result + getAgentID();
        return result;
    }
}
