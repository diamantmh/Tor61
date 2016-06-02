import java.io.IOException;
import java.util.Random;

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
    private static SocketManager manager;
    private static Random rand;


    public static void main(String[] args) {
        int data = GROUP_NUM * 1000 + INSTANCE_NUM;
        manager = SocketManager.getInstance(data);
        rand = new Random();
        RegistrationAgent agent = null;
//        try {
            Thread listen = new ListenThread(LISTEN_PORT);
            listen.start();
//            agent = new RegistrationAgent(REG_HOST, REG_PORT);
//            String groupName = "Tor61Router-"+GROUP_NUM+"-"+INSTANCE_NUM;
//            agent.register(LISTEN_PORT, data, groupName);
//            FetchResult[] viableRouters = agent.fetch(FETCH_PREFIX);


//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        FetchResult[] viableRouters = new FetchResult[1];
        viableRouters[0] = new FetchResult("127.0.0.1", LISTEN_PORT, data);
        createCircuit(viableRouters);

    }

    private static void createCircuit(FetchResult[] viableRouters) {
        int n = rand.nextInt(viableRouters.length);
        String body = viableRouters[n].getIp() + ":" + viableRouters[n] + "\0" + viableRouters[n].getData();
        manager.extend(-1, -1, viableRouters[n].getData(), body);
    }
}
