import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
