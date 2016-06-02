import java.math.BigInteger;

/**
 * Created by michaeldiamant on 5/31/16.
 */
public class openObject {
    private int openerID;
    private int openedID;
    private int type;
    // 5 = open
    // 6 = open success
    // 7 = open failed


    public openObject(int openerID, int openedID, int type) {
        this.openerID = openerID;
        this.openedID = openedID;
        this.type = type;
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
        byte[] b = new byte[512];
        b[2] = (byte) type;
        //System.out.println(openedID);
        byte[] tempOpener = BigInteger.valueOf(openerID).toByteArray();
        for(int i = tempOpener.length - 1; i >= 0; i--) {
            b[3+i+1] = tempOpener[i];
            System.out.println(tempOpener[i]);
        }
        byte[] tempOpened = BigInteger.valueOf(openedID).toByteArray();
        for(int i = 0; i < tempOpened.length; i++) {
            b[7+i] = tempOpened[i];
        }
        //return Integer.toHexString(openerID).getBytes();
        //System.out.println("" + ((byte) 0xff) + ((byte) 0xff));
        //System.out.println(Integer.toHexString(openerID));
        return b;
    }
}
