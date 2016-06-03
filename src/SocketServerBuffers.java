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
public class SocketServerBuffers {
    private static SocketServerBuffers instance = null;
    private static Map<Pair, BufferPair> buffers = null;
    private static final Lock l = new ReentrantLock();

    private SocketServerBuffers() {
        buffers = new HashMap<>();
    }

    public static SocketServerBuffers getInstance() {
        if (instance == null) {
            instance = new SocketServerBuffers();
        }
        return instance;
    }

    public BufferPair create(int circuitID, int streamID) {
        Pair p = new Pair(circuitID, streamID);
        BufferPair bp = new BufferPair();
        buffers.put(p, bp);
        return bp;
    }

    public BufferPair get(int circuitID, int streamID) {
        Pair p = new Pair(circuitID, streamID);
        return buffers.get(p);
    }

    public void close(int circuitID, int streamID) {
        Pair p = new Pair(circuitID, streamID);
        buffers.remove(p);
    }
}
