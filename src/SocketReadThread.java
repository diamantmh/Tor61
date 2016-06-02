import java.io.IOException;
import java.io.InputStream;

/**
 * Created by josephkesting on 6/1/16.
 */
public class SocketReadThread extends Thread {

    private final SocketData data;
    private final SocketManager socketManager;

    public SocketReadThread(SocketData data) {
        this.data = data;
        this.socketManager = SocketManager.getInstance(0);
    }

    public void run() {
        byte[] messageIn = new byte[512];
        InputStream inputStream = data.getIn();
        try {
            int bytesRead = inputStream.read(messageIn);
            while (bytesRead > 0) {
                processMessage(messageIn);
                bytesRead = inputStream.read(messageIn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void processMessage(byte[] messageBytes) {
        int inID = 0;
        int inCircuitID = 0;
        int outID = 0;
        int outCircuitID = 0;
        //Create

        socketManager.create(inID, inCircuitID, outID);
        //Created - silent
        socketManager.create(inID, inCircuitID, outID);
        //Create Failed

        //Destroy
        socketManager.destroy(inCircuitID, inID);
        //Begin

        //Data

        //End

        //Connected

        //Extend

        //Extended - relay back

        //Begin Failed

        //Extend Failed
        
    }
}
