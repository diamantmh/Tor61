import java.nio.ByteBuffer;

/**
 * Created by michaeldiamant on 5/31/16.
 */
public class run {

    public static void main(String[] args) {
        ByteBuffer b = ByteBuffer.allocate(512);
        b.put((byte)0);
        b.put((byte)0);
        b.put((byte)0x05);
        b.putInt(1234);
        b.putInt(6789);
        decoder d = new decoder();
        circuitObject m = d.circuit(b.array());
        System.out.println(m.getCircuitID());
        System.out.println(m.getType());

        openObject o = d.open(b.array());
        System.out.println(o.getOpenerID());
        byte[] by = o.getBytes();
        o = d.open(by);
        System.out.println(o.getOpenerID());



//        thing a = new thing(33333);
//        a.run();
//        try {
//            Thread.sleep(1000);                 //1000 milliseconds is one second.
//        } catch(InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }
//        a.send("127.0.0.1", 33333, b);

        //thing b = new thing(33433);
        //b.run();
    }
}
