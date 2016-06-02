/**
 * Created by michaeldiamant on 5/31/16.
 */
public class run {

    public static void main(String[] args) {
        byte[] b = new byte[512];
        b[0] = 0;
        b[1] = 5;
        b[2] = 1;
        b[5] = (byte) 0xff;
        b[6] = (byte) 0xff;
        b[3] = (byte) 0x7f;
        b[4] = (byte) 0xff;
        b[7] = 6;
        b[8] = 7;
        b[9] = 8;
        b[10] = 9;
        decoder d = new decoder();
//        circuitObject m = d.circuit(b);
//        System.out.println(m.getCircuitID());
//        System.out.println(m.getType());

        openObject o = d.open(b);
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
