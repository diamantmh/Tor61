import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ProxySocketWriteThread extends Thread {

    private DataOutputStream socketOut;
    private BlockingQueue buffer;

    public ProxySocketWriteThread(Socket s, BlockingQueue buffer) {
        try {
            this.socketOut = new DataOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.buffer = buffer;
    }

    public void run() {
        while(true) {
            try {

                byte[] data = (byte[]) buffer.take();
                socketOut.write(data);
                socketOut.flush();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
