import java.io.IOException;

/**
 * Created by josephkesting on 6/1/16.
 */
public class Startup {

    private static final String REG_HOST = "cse461.cs.washington.edu";
    private static final int REG_PORT = 46101;
    private static final int GROUP_NUM = 1453;
    private static final int INSTANCE_NUM = 1914;
    private static final int LISTEN_PORT = 1993;
    private static final String FETCH_PREFIX = "Tor61Router";


    public static void main(String[] args) {
        int data = GROUP_NUM * 10000 + INSTANCE_NUM;
        SocketManager manager = SocketManager.getInstance(data);
        RegistrationAgent agent = null;
        try {
            Thread listen = new ListenThread(LISTEN_PORT);
            listen.start();
            agent = new RegistrationAgent(REG_HOST, REG_PORT);
            String groupName = "Tor61Router-"+GROUP_NUM+"-"+INSTANCE_NUM;
            agent.register(LISTEN_PORT, data, groupName);
            FetchResult[] viableRouters = agent.fetch(FETCH_PREFIX);
            createCircuit(viableRouters);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void createCircuit(FetchResult[] viableRouters) {
        for(int i = 0; i < viableRouters.length; i++) {
            System.out.println("" + viableRouters[i].getIp() + " " + viableRouters[i].getData() + " " + viableRouters[i].getData());
        }
    }
}
