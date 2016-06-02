import java.nio.ByteBuffer;

/**
 * Created by michaeldiamant on 5/31/16.
 */
public class circuitObject {
    private int circuitID;
    private int type;
    // 1 = new circuit
    // 2 = success
    // 8 = failed
    // 4 = destroy

    public circuitObject(int circuitID, int type) {
        this.circuitID = circuitID;
        this.type = type;
    }

    public int getCircuitID() {
        return circuitID;
    }

    public int getType() {
        return type;
    }

    public byte[] getBytes() {
        ByteBuffer b = ByteBuffer.allocate(512);
        b.putShort((short) circuitID);
        b.put((byte) type);
        return b.array();
    }
}
