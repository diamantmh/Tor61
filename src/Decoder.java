import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class Decoder {

    public static MessageObject decode(byte[] message) {
        int num = message[2];
        if (num == 3) {
            return relay(message);
        } else if (num >= 5 && num <= 7) {
            return open(message);
        } else {
            return circuit(message);
        }
    }

    public static OpenObject open(byte[] message) {

        ByteBuffer temp = ByteBuffer.wrap(message);
        temp.position(3);
        int openerID = temp.getInt();
        int openedID = temp.getInt();
        return new OpenObject(openerID, openedID, message[2]);
    }

    public static CircuitObject circuit(byte[] message) {
        ByteBuffer temp = ByteBuffer.wrap(message);
        int circID = temp.getShort();
        return new CircuitObject(circID, message[2]);
    }

    public static RelayObject relay(byte[] message) {
        ByteBuffer temp = ByteBuffer.wrap(message);
        int circID = temp.getShort();
        temp.position(3);
        int streamID = temp.getShort();
        temp.position(11);
        int bodyLength = temp.getShort();
        temp.position(14);
        if(message[13] == (byte)0x01 || message[13] == (byte)0x06) {

            CharsetDecoder decode = Charset.forName("UTF8").newDecoder();
            try {
                CharBuffer chars = decode.decode(temp.slice());
                System.out.println(chars);
                byte[] bytes = temp.slice().array();
                String body = new String(bytes, 0, bodyLength * 2, HTTPProxy.CHARSET);

                return new RelayObject(circID, streamID, bodyLength, message[13], body);
            } catch (CharacterCodingException e) {
                e.printStackTrace();
            }
        } else if(message[13] == (byte)0x02) {
            byte[] sliceWorkaround = new byte[bodyLength];
            for (int i = 0; i < sliceWorkaround.length; i++) {
                sliceWorkaround[i] = temp.get();
            }
            return new RelayObject(circID, streamID, bodyLength, message[13], sliceWorkaround);
        }
        return new RelayObject(circID, streamID, bodyLength, message[13]);
    }
}
