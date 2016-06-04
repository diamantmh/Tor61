import java.util.concurrent.BlockingQueue;

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
        while(true) {
            try {
                byte[] data = (byte[])proxyBuffer.take();
                if (data.length == 512) {
                    torBuffer.put(data);
                } else {
                    RelayObject dataMessage = new RelayObject(circuitID, streamID, data.length, 2, data);
                    torBuffer.put(dataMessage.getBytes());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }


}
