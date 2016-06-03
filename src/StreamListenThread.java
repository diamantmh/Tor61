import java.util.concurrent.BlockingQueue;

/**
 * Created by josephkesting on 6/2/16.
 */
public class StreamListenThread extends Thread {

    private int streamID;

    public StreamListenThread(int streamID) {
        this.streamID = streamID;

    }

    public void run() {
        SocketClientBuffers buffers = SocketClientBuffers.getInstance();
        BlockingQueue proxyToTor = buffers.get(streamID).getProxyToTor();
        SocketManager manager = SocketManager.getInstance(0);
        while(true) {
            byte[] data = new byte[512];
            try {
                data = (byte[])proxyToTor.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Pair start = manager.getRoutingTable().getBeginPair();
            RelayObject dataMessage = new RelayObject(start.getCircuit(), streamID, data.length, 2, data);
            try {
                manager.getSocketList().get(start.getSocket()).getBuffer().put(dataMessage.getBytes());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
