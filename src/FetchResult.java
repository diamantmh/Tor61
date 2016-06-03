/**
 * Created by josephkesting on 6/2/16.
 */
public class FetchResult {
    private String ip;
    private int port;
    private int data;

    public FetchResult (String input) {
        parseInput(input);
    }

    public FetchResult (String ip, int port, int data) {
        this.ip = ip;
        this.port = port;
        this.data = data;
    }

    private void parseInput(String input) {
        String[] splitInput = input.split(" ");
        ip = splitInput[1];
        port = Integer.parseInt(splitInput[2]);
        data = Integer.parseInt(splitInput[3]);
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getData() {
        return data;
    }
}
