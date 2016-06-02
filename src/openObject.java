import java.nio.ByteBuffer;

/**
 * Created by michaeldiamant on 5/31/16.
 */
public class OpenObject {
    private int openerID;
    private int openedID;
    private int type;
    private MessageType messageType;
    // 5 = open
    // 6 = open success
    // 7 = open failed


    public OpenObject(int openerID, int openedID, int type) {
        this.openerID = openerID;
        this.openedID = openedID;
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

    public int getOpenerID() {
        return openerID;
    }

    public int getOpenedID() {
        return openedID;
    }

    public int getType() {
        return type;
    }

    public byte[] getBytes() {
        ByteBuffer b = ByteBuffer.allocate(512);
        b.position(2);
        b.put((byte) type);
        b.putInt(openerID);
        b.putInt(openedID);
        return b.array();
    }
}
