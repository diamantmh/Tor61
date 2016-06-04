import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

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
