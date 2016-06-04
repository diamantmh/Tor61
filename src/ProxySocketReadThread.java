import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

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
        int read = 1;
        while (read > 0) {
            byte[] data = new byte[498];
            try {
                read = socketIn.read(data);
                buffer.put(data);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

        }
    }
}
