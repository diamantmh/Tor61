/**
 * Created by michaeldiamant on 6/1/16.
 */
public class INetAddress {
    private String host;
    private int port;

    public INetAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
