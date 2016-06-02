/**
 * Created by michaeldiamant on 5/31/16.
 */
public class decoder {

    public int byteToDecimal(byte[] b, int start, int end) {
        String result = "";
        for(int i = start; i < end; i++) {
            result += (Integer.toHexString(0xFF & b[i]));
        }
        return Integer.parseInt(result, 16);
    }

    public openObject open(byte[] message) {
        return new openObject(byteToDecimal(message, 3, 7), byteToDecimal(message, 7, 11), message[2]);
    }

    public circuitObject circuit(byte[] message) {
        return new circuitObject(byteToDecimal(message, 0, 2), byteToDecimal(message, 2, 3));
    }

    public relayObject relay(byte[] message) {
        int circID = byteToDecimal(message, 0, 2);
        int streamID = byteToDecimal(message, 3, 5);
        int bodyLength = byteToDecimal(message, 10, 12);
        byte[] b = new byte[bodyLength];
        for(int i = 14; i < 14 + bodyLength; i++) {
            b[i] = message[i];
        }
        if(message[13] == 1 || message[13] == 6) {
            return new relayObject<>(circID, streamID, bodyLength, message[13], new String(b));
        } else if(message[13] == 2) {
            return new relayObject<>(circID, streamID, bodyLength, message[13], b);
        }
        return new relayObject<>(circID, streamID, bodyLength, message[13], b);
    }
}
