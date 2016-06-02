import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by josephkesting on 6/1/16.
 */
public class SocketWriteThread extends Thread {

    BlockingQueue buffer;
    DataOutputStream out;

    public SocketWriteThread(BlockingQueue buffer, DataOutputStream out) {
        this.buffer = buffer;
        this.out = out;
    }

    public void run() {
        while(true) {
            try {
                byte[] message = (byte[]) buffer.take();
                if (message == null || message.length != 512) {
                    //Do something?
                    break;
                }
                out.write(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
