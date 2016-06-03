/**
 * Created by michaeldiamant on 6/1/16.
 */
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by josephkesting on 5/31/16.
 */
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

    public BufferPair create(int ID) {
        BufferPair bp = new BufferPair();
        buffers.put(ID, bp);
        return bp;
    }

    public BufferPair get(int id) {
        return buffers.get(id);
    }

    public void close(int id) {
        buffers.remove(id);
    }
}
