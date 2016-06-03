import java.util.concurrent.BlockingQueue;

/**
 * Created by josephkesting on 6/2/16.
 */
public class WriteToTorFromServerBufferThread extends Thread {

    private BlockingQueue proxyBuffer;
    private BlockingQueue torBuffer;
    private int circuitID;
    private int streamID;


    public WriteToTorFromServerBufferThread(int circuitID, int streamID, BlockingQueue torBuffer) {
        proxyBuffer = SocketServerBuffers.getInstance().get(circuitID, streamID).getProxyToTor();
        this.torBuffer = torBuffer;
        this.streamID = streamID;
        this.circuitID = circuitID;
    }

    public void run() {
        SocketManager manager = SocketManager.getInstance(0);
        while(true) {
            byte[] data = new byte[512];
            try {
                data = (byte[])proxyBuffer.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RelayObject dataMessage = new RelayObject(circuitID, streamID, data.length, 2, data);
            try {
                torBuffer.put(dataMessage.getBytes());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
