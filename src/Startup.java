import java.io.IOException;
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
    private static final int PROXY_PORT = 5555;
    private static final String FETCH_PREFIX = "Tor61Router-1453";
    private static SocketManager manager;
    private static Random rand;


    public static void main(String[] args) {
        int data = GROUP_NUM * 1000 + INSTANCE_NUM;
        manager = SocketManager.getInstance(data);
        rand = new Random();
        RegistrationAgent agent;
        try {
            Thread listen = new ListenThread(LISTEN_PORT);
            listen.start();
            agent = new RegistrationAgent(REG_HOST, REG_PORT);
            String groupName = "Tor61Router-"+GROUP_NUM+"-"+INSTANCE_NUM;
            agent.register(LISTEN_PORT, data, groupName);
            List<FetchResult> viableRouters = agent.fetch(FETCH_PREFIX);
            createCircuit(viableRouters);
            startupProxy();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        List<FetchResult> viableRouters = new ArrayList<>(1);
//        viableRouters.add(new FetchResult("127.0.0.1", LISTEN_PORT, data));
        //createCircuit(viableRouters);
    }

    private static void createCircuit(List<FetchResult> viableRouters) {
        int count = 3;
        while(count > 0) {
            int n = rand.nextInt(viableRouters.size());
            String body = viableRouters.get(n).getIp() + ":" + viableRouters.get(n).getPort() + "\0" + viableRouters.get(n).getData();
            manager.extend(0, -1, body);
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

    private static void startupProxy() {
        HTTPProxy p = HTTPProxy.getInstance(PROXY_PORT);
        p.run();
        while(true) {
            try {
                System.out.println("Startup pretake");
                String result = manager.getCommandQ().take();
                System.out.println("Result: " + result);
                if (result.startsWith("begin")) {

                    Pair start = manager.getRoutingTable().getBeginPair();
                    RelayObject beginMessage = createBeginMessage(start, result);
//                    SocketClientBuffers.getInstance().create(beginMessage.getStreamID());
                    manager.getSocketList().get(start.getSocket()).getBuffer().put(beginMessage.getBytes());
                    String reply = manager.getCommandQ().take();
                    if(reply.equals("connected")) {
                        Thread streamListen = new StreamListenThread(beginMessage.getStreamID());
                        streamListen.start();
                    } else {
                        SocketClientBuffers.getInstance().close(beginMessage.getStreamID());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static RelayObject createBeginMessage(Pair start, String bodyInfo) {
        String[] split = bodyInfo.split(" ");
        String body = split[1]+":"+split[2]+"\0";
        RelayObject begin = new RelayObject(start.getCircuit(), Integer.parseInt(split[3]), body.length(), 1, body);
        return begin;
    }
}
