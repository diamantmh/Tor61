import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by josephkesting on 6/2/16.
 */
public class BufferPair {
    private BlockingQueue proxyToTor;
    private BlockingQueue torToProxy;

    public BufferPair() {
        this.proxyToTor = new LinkedBlockingQueue<>();
        this.torToProxy = new LinkedBlockingQueue<>();
    }

    public BlockingQueue getProxyToTor() {
        return proxyToTor;
    }

    public BlockingQueue getTorToProxy() {
        return torToProxy;
    }
}
