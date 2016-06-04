import java.nio.ByteBuffer;


public class OpenObject implements MessageObject {
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
        if(type == 5) {
            messageType = MessageType.OPEN;
        } else if(type == 6) {
            messageType = MessageType.OPENED;
        } else {
            messageType = MessageType.OPEN_FAILED;
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

    @Override
    public MessageType getMessageType() {
        return messageType;
    }



    public byte[] getBytes() {
        ByteBuffer b = ByteBuffer.allocate(512);
        b.position(2);
        b.put((byte) type);
        b.putInt(openerID);
        b.putInt(openedID);
        return b.array();
    }

    @Override
    public int getCircuitID() {
        return 0;
    }
}
