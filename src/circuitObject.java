import java.nio.ByteBuffer;

/**
 * Created by michaeldiamant on 5/31/16.
 */
public class CircuitObject implements MessageObject {
    private int circuitID;
    private int type;
    private MessageType messageType;
    // 1 = new circuit
    // 2 = success
    // 8 = failed
    // 4 = destroy

    public CircuitObject(int circuitID, int type) {
        this.circuitID = circuitID;
        this.type = type;
        if(type == 1) {
            messageType = MessageType.CREATE;
        } else if(type == 2) {
            messageType = MessageType.CREATED;
        } else if(type == 4) {
            messageType = MessageType.DESTROY;
        } else {
            messageType = MessageType.CREATE_FAILED;
        }
    }

    public int getCircuitID() {
        return circuitID;
    }

    public int getType() {
        return type;
    }

    @Override
    public MessageType getMessageType() {
        return messageType;
    }

    public byte[] getBytes() {
        ByteBuffer b = ByteBuffer.allocate(512);
        b.putShort((short) circuitID);
        b.put((byte) type);
        return b.array();
    }
}
