import java.nio.ByteBuffer;

/**
 * Created by michaeldiamant on 5/31/16.
 */
public class RelayObject {
    private int circuitID;
    private int streamID;
    private int bodyLength;
    private int command;
    // 1 = begin
    // 2 = data
    // 3 = end
    // 4 = connected
    // 6 = extend
    // 7 = extended
    // 11 = begin failed
    // 12 = extend failed
    private byte[] data;
    private String body;

    public RelayObject(int circuitID, int streamID, int bodyLength, int command, byte[] data) {
        this.circuitID = circuitID;
        this.streamID = streamID;
        this.bodyLength = bodyLength;
        this.command = command;
        this.data = data;
        this.body = null;
    }

    public RelayObject(int circuitID, int streamID, int bodyLength, int command, String body) {
        this.circuitID = circuitID;
        this.streamID = streamID;
        this.bodyLength = bodyLength;
        this.command = command;
        this.body = body;
        this.data = null;
    }

    public RelayObject(int circuitID, int streamID, int bodyLength, int command) {
        this.circuitID = circuitID;
        this.streamID = streamID;
        this.bodyLength = bodyLength;
        this.command = command;
        this.body = null;
        this.data = null;
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

    public String getBody() {
        return body;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getBytes() {
        ByteBuffer b = ByteBuffer.allocate(512);
        b.putShort((short) circuitID);
        b.put((byte) 0x03);
        b.putShort((short) streamID);
        b.position(11);
        b.putShort((short) bodyLength);
        b.put((byte) command);

        return b.array();
    }


}
