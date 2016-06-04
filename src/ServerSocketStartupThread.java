import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerSocketStartupThread extends Thread {

    private Socket s;
    private BufferPair bp;

    public ServerSocketStartupThread(int streamID, int circuitID, String host, int port) {
        this.bp = SocketServerBuffers.getInstance().get(circuitID, streamID);
        this.s = new Socket();
        try {
            s.bind(new InetSocketAddress(0));
            host = host.trim();
            s.connect(new InetSocketAddress(host, port));
            if (!s.isConnected()) {
                throw new IOException();
            } else {
                RelayObject connected = new RelayObject(circuitID, streamID, 0, 4);
                bp.getProxyToTor().put(connected.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();

            RelayObject beginFailed = new RelayObject(circuitID, streamID, 0, 11);
            try {
                bp.getProxyToTor().put(beginFailed.getBytes());
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Thread read = new ProxySocketReadThread(s, bp.getProxyToTor());
        Thread write = new ProxySocketWriteThread(s, bp.getTorToProxy());
        read.start();
        write.start();
    }
}
