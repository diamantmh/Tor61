/**
 * Created by michaeldiamant on 6/1/16.
 */
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SocketClientBuffers {
    private static SocketClientBuffers instance = null;
    private static Map<Integer, BufferPair> buffers = null;
    private static final Lock l = new ReentrantLock();

    private SocketClientBuffers() {
        buffers = new HashMap<>();
    }

    public static SocketClientBuffers getInstance() {
        if (instance == null) {
            instance = new SocketClientBuffers();
        }
        return instance;
    }

    public synchronized BufferPair create(int ID) {
        System.out.println("Created: " + ID);
        BufferPair bp = new BufferPair();
        buffers.put(ID, bp);
        return bp;
    }

    public synchronized BufferPair get(int id) {
        return buffers.get(id);
    }

    public synchronized void close(int id) {
        System.out.println("Destroyed: " + id);
//        buffers.remove(id);
    }
}
