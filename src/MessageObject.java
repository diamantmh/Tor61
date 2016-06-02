/**
 * Created by josephkesting on 6/1/16.
 */
public interface MessageObject {

    public MessageType getMessageType();
    public byte[] getBytes();
    public int getCircuitID();
}
