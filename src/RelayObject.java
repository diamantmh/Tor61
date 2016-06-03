import java.nio.ByteBuffer;

/**
 * Created by michaeldiamant on 5/31/16.
 */
public class RelayObject implements MessageObject {
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
    private MessageType messageType;

    public RelayObject(int circuitID, int streamID, int bodyLength, int command, byte[] data) {
        this(circuitID, streamID, bodyLength, command, data, null);
    }

    public RelayObject(int circuitID, int streamID, int bodyLength, int command, byte[] data, String body) {
        this.circuitID = circuitID;
        this.streamID = streamID;
        this.bodyLength = bodyLength;
        this.command = command;
        this.body = body;
        this.data = data;
        if(command == 1) {
            messageType = MessageType.BEGIN;
        } else if(command == 2) {
            messageType = MessageType.DATA;
        } else if(command == 3) {
            messageType = MessageType.END;
        } else if(command == 4) {
            messageType = MessageType.CONNECTED;
        } else if(command == 6) {
            messageType = MessageType.EXTEND;
        } else if(command == 7) {
            messageType = MessageType.EXTENDED;
        } else if(command == 11) {
            messageType = MessageType.BEGIN_FAILED;
        } else {
            messageType = MessageType.EXTEND_FAILED;
        }
    }

    public RelayObject(int circuitID, int streamID, int bodyLength, int command, String body) {
        this(circuitID, streamID, bodyLength, command, null, body);
    }

    public RelayObject(int circuitID, int streamID, int bodyLength, int command) {
        this(circuitID, streamID, bodyLength, command, null, null);
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

    @Override
    public MessageType getMessageType() {
        return messageType;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public void setCircuitID(int circuitID) {
        this.circuitID = circuitID;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setData(byte[] data) {
        this.data = data;
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
