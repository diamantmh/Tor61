import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
        List<FetchResult> viableRouters = new ArrayList<>(1);
        viableRouters.add(new FetchResult("127.0.0.1", LISTEN_PORT, data));
        createCircuit(viableRouters);

    }

    private static void createCircuit(List<FetchResult> viableRouters) {
        int count = 3;
        while(count > 0) {
            int n = rand.nextInt(viableRouters.size());
            String body = viableRouters.get(n).getIp() + ":" + viableRouters.get(n).getIp() + "\0" + viableRouters.get(n).getData();
            manager.extend(-1, -1, viableRouters.get(n).getData(), body);
            try {
                String result = manager.getCommandQ().take();
                if(result.equals("created") || result.equals("extended")) {
                    count--;
                } else {
                    viableRouters.remove(n);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class NewCircuit extends Thread {

        public NewCircuit() {

        }

        public void run() {

        }
    }
}
