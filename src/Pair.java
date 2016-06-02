/**
 * Created by josephkesting on 5/31/16.
 */
public class Pair {

    private int circuit;
    private int socket;

    public Pair(int circuit, int socket) {
        if(circuit == -1) {
            socket = 0;
        } else if (socket == -1) {
            circuit = 0;
        }
        this.circuit = circuit;
        this.socket = socket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        if (getCircuit() != pair.getCircuit()) return false;
        return getSocket() == pair.getSocket();

    }

    @Override
    public int hashCode() {
        int result = getCircuit();
        result = 31 * result + getSocket();
        return result;
    }

    public int getCircuit() {
        return circuit;
    }

    public int getSocket() {
        return socket;
    }

    public boolean isExit() {
        return socket == -1;
    }

    public boolean isEntry() { return circuit == -1; }


}
