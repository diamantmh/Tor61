import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * Created by josephkesting on 6/2/16.
 */
public class ProxySocketReadThread extends Thread {

    private InputStream socketIn;
    private BlockingQueue buffer;

    public ProxySocketReadThread(Socket s, BlockingQueue buffer) {
        try {
            this.socketIn = s.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.buffer = buffer;
    }

    public void run() {
        while (true) {
            byte[] data = new byte[498];
            try {
                socketIn.read(data);
                buffer.put(data);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
