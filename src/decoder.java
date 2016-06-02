import java.nio.ByteBuffer;

/**
 * Created by michaeldiamant on 5/31/16.
 */
public class Decoder {

    public OpenObject open(byte[] message) {
        ByteBuffer temp = ByteBuffer.wrap(message);
        temp.position(3);
        int openerID = temp.getInt();
        int openedID = temp.getInt();
        return new OpenObject(openerID, openedID, message[2]);
    }

    public circuitObject circuit(byte[] message) {
        ByteBuffer temp = ByteBuffer.wrap(message);
        int circID = temp.getShort();
        return new circuitObject(circID, message[2]);
    }

    public RelayObject relay(byte[] message) {
        ByteBuffer temp = ByteBuffer.wrap(message);
        int circID = temp.getShort();
        temp.position(3);
        int streamID = temp.getShort();
        temp.position(10);
        int bodyLength = temp.getShort();
        temp.position(14);
        if(message[13] == 1 || message[13] == 6) {
            String body = "";
            for(int i = 0; i < bodyLength; i++) {
                body += temp.getChar();
            }
            return new RelayObject(circID, streamID, bodyLength, message[13], body);
        } else if(message[13] == 2) {
            return new RelayObject(circID, streamID, bodyLength, message[13], temp.slice().array());
        }
        return new RelayObject(circID, streamID, bodyLength, message[13]);
    }
}
