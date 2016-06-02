/**
 * Created by michaeldiamant on 5/31/16.
 */
public class relayObject <T> {
    private int circuitID;
    private int streamID;
    private int bodyLength;
    private int command;
    private T body;

    public relayObject(int circuitID, int streamID, int bodyLength, int command, T body) {
        this.circuitID = circuitID;
        this.streamID = streamID;
        this.bodyLength = bodyLength;
        this.command = command;
        this.body = body;
    }

    public relayObject(int circuitID, int streamID, int bodyLength, int command) {
        this.circuitID = circuitID;
        this.streamID = streamID;
        this.bodyLength = bodyLength;
        this.command = command;
        this.body = null;
    }

    public int getCircuitID() {
        return circuitID;
    }

    public int getStreamID() {
        return streamID;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public int getCommand() {
        return command;
    }

    public T getBody() {
        return body;
    }


}
