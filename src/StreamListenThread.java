import java.util.concurrent.BlockingQueue;

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
            try {
                byte[] data = (byte[])proxyToTor.take();
                Pair start = manager.getRoutingTable().getBeginPair();

                RelayObject dataMessage = new RelayObject(start.getCircuit(), streamID, data.length, 2, data);
                manager.getSocketList().get(start.getSocket()).getBuffer().put(dataMessage.getBytes());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
