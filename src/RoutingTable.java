
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

    public synchronized void addEntry(Pair outPair) {
        add(outPair, new Pair(-1, 0));
    }

    public synchronized void add(Pair inPair, Pair outPair) {
        mappings.put(inPair, outPair);
        mappings.put(outPair, inPair);
    }

    public synchronized void add(Pair inPair, int outID, boolean owned) {
        int circuitNum = getCircuitNum(owned);
        Pair outPair = new Pair(circuitNum, outID);
        add(inPair, outPair);
    }

    public synchronized void remove(Pair pair) {
        Pair opp = mappings.remove(pair);
        if (!opp.isExit()) {
            mappings.remove(opp);
        }
    }

    public synchronized Pair getBeginPair() {
        return mappings.get(new Pair(-1, 0));
    }

    public synchronized int stage (int extendTargetID, int extenderCircuitID, int extenderSocketID, boolean owned) {
        int extendTargetCircuitID = getCircuitNum(owned);
        Pair a = new Pair(extendTargetCircuitID, extendTargetID);
        Pair b = new Pair(extenderCircuitID, extenderSocketID);
        mappings.put(a, b);
        return extendTargetCircuitID;
    }

    public synchronized Pair unstage (int circuitID, int socketID) {
        Pair staged = new Pair(circuitID, socketID);
        Pair opp = mappings.get(staged);
        if (opp != null) {
            mappings.put(opp, staged);
        }
        return opp;
    }

    public synchronized Pair removeFromStage (int circuitID, int socketID) {
        return mappings.remove(new Pair(circuitID, socketID));
    }

    public synchronized boolean isIncomingPair(Pair pair) {
        return mappings.containsKey(pair);
    }

    public synchronized Pair get(Pair pair) {
        return mappings.get(pair);
    }

    private synchronized int getCircuitNum(boolean owned) {
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
