/**
 * Created by michaeldiamant on 6/1/16.
 */
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by josephkesting on 5/31/16.
 */
public class SocketBuffers {
    private static SocketBuffers instance = null;
    private static Map<Integer, BlockingQueue<byte[]>> buffers = null;
    private static final Lock l = new ReentrantLock();
    private static int connectionID;

    private SocketBuffers() {
        buffers = new HashMap<>();
    }

    public static SocketBuffers getInstance() {
        if (instance == null) {
            instance = new SocketBuffers();
        }
        return instance;
    }

    public void create(int ID) {
        buffers.put(ID, new LinkedBlockingQueue<>());
    }

    public byte[] take(int id) {
        byte[] retVal = null;
        try {
            retVal = buffers.get(id).take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public void put(int id, byte[] payload) {
        try {
            buffers.get(id).put(payload);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close(int id) {
        buffers.remove(id);
    }
}
