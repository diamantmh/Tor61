/**
 * Created by josephkesting on 5/31/16.
 */
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

public class RoutingTable {
    private static final Lock l = new ReentrantLock();
    private int circuitCounter;

    private Map<Pair, Pair> mappings;

    public RoutingTable() {
        this.mappings = new HashMap<>();
        circuitCounter = 1;
    }

    public void addExit(Pair inPair) {
        mappings.put(inPair, new Pair(0, -1));
    }

    public void addEntry(Pair outPair) {
        add(outPair, new Pair(-1, 0));
    }

    public void add(Pair inPair, Pair outPair) {
        mappings.put(inPair, outPair);
        mappings.put(outPair, inPair);
    }

    public void add(Pair inPair, int outID, boolean owned) {
        int circuitNum = getCircuitNum(owned);
        Pair outPair = new Pair(circuitNum, outID);
        add(inPair, outPair);
    }

    public void remove(Pair pair) {
        Pair opp = mappings.remove(pair);
        if (!opp.isExit()) {
            mappings.remove(opp);
        }

    }

    public int stage (int extendTargetID, int extenderCircuitID, int extenderSocketID, boolean owned) {
        int extendTargetCircuitID = getCircuitNum(owned);
        mappings.put(new Pair(extendTargetCircuitID, extendTargetID), new Pair(extenderCircuitID, extenderSocketID));
        return extendTargetCircuitID;
    }

    public Pair unstage (int circuitID, int socketID) {
        Pair staged = new Pair(circuitID, socketID);
        Pair opp = mappings.get(staged);
        if (opp != null) {
            mappings.put(opp, staged);
        }
        return opp;
    }

    public Pair removeFromStage (int circuitID, int socketID) {
        return mappings.remove(new Pair(circuitID, socketID));
    }

    public boolean isIncomingPair(Pair pair) {
        return mappings.containsKey(pair);
    }

    public Pair get(Pair pair) {
        return mappings.get(pair);
    }

    private int getCircuitNum(boolean owned) {
        l.lock();
        int num = -1;
        try {
            if (owned) {
                num = circuitCounter;
            } else {
                num = circuitCounter + 1;
            }
            circuitCounter += 2;
        } finally {
            l.unlock();
            return num;
        }
    }
}
