/**
 * Created by josephkesting on 6/2/16.
 */
public class StreamListenThread extends Thread {

    private int streamID;

    public StreamListenThread(int streamID) {
        this.streamID = streamID;

    }

    public void run() {
        SocketBuffers buffers = SocketBuffers.getInstance();
        SocketManager manager = SocketManager.getInstance(0);
        while(true) {
            byte[] data = buffers.take(streamID);
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
