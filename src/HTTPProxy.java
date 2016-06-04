/**
 * Created by michaeldiamant on 6/1/16.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class HTTPProxy {

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    Map<Integer, BlockingQueue<char[]>> buffers;
    private final Lock l = new ReentrantLock();
    private final Lock lockID = new ReentrantLock();
    private ServerSocket serverSocket;
    private static SocketManager manager;
    private int streamID;
    private SocketClientBuffers buffer;
    private static HTTPProxy instance = null;

    public static HTTPProxy getInstance(int port) {
        if (instance == null) {
            instance = new HTTPProxy(port);
        }
        return instance;
    }

    private HTTPProxy(int port) {
        buffers = new HashMap<>();
        manager = SocketManager.getInstance(0);
        streamID = 0;
        buffer = SocketClientBuffers.getInstance();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void run(){
        Thread accept = new AcceptThread();
        accept.start();
    }

    public class AcceptThread extends Thread {
        public void run() {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    Thread setupThread = new SetupThread(clientSocket);
                    setupThread.start();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
    }

    private INetAddress getAddressFromMessage(String message) {
        String host = "";
        int port = -1;
        Scanner s = new Scanner(message);
        String requestLine = "";
        while (s.hasNextLine()) {

            String line = s.nextLine();
            if(requestLine.equals("")) {
                requestLine = line;
            }
            if(line.toLowerCase().startsWith("host")) {
                String[] splitLine = line.split(":", 2);
                String url = splitLine[1].trim().split(" ")[0];
                String[] split = url.split(":");
                host = split[0];
                if(split.length > 1) {
                    port = Integer.parseInt(split[1].split("/")[0]);
                }

            }
        }
        if (port == -1) {
            String[] splitLine = requestLine.split(" ");

            try {
                URI uri = new URI(splitLine[1]);
                port = uri.getPort();
                if (port == -1) {
                    if (splitLine[1].startsWith("https://") || splitLine[0].equals("CONNECT")) {
                        port = 443;
                    } else {
                        port = 80;
                    }
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("CANT BE SPLIT: ");
                System.out.println(requestLine);
            }

        }

        if (host.equals("")) {
            System.out.println("STILL NO HOST");
        }
        if (port == -1) {
            System.out.println("STILL NO PORT");
        }

        return new INetAddress(host, port);
    }

    private String getModifiedRequest(BufferedReader in) {
        String request = "";
        while(true) {
            String line;
            try {
                line = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            if (line == null || line.equals("")) {
                break;
            }
            if (request.equals("")) {
                line = line.replace("HTTP/1.1", "HTTP/1.0");
                System.out.println(">>> " + line);
            } else if (line.toLowerCase().startsWith("connection") || line.toLowerCase().startsWith("proxy-connection")) {
                line = line.replace("keep-alive", "close");
            }
            request = request + line + "\r\n";
        }
        request += "\r\n";
        return request;
    }

    class SetupThread extends Thread {

        Socket clientSocket;

        public SetupThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            BufferedReader clientIn = null;
            String request;
            try {
                System.out.println(3);
                clientIn = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                System.out.println(e);
            }
            request = getModifiedRequest(clientIn);
            System.out.println(4);
            if (request.trim().length() > 0) {
                INetAddress address = getAddressFromMessage(request);
                try {
                    int currentStream =  getStreamNumber();
                    buffer.create(currentStream);
                    Thread listenForResponse = new ProxySocketWriteThread(clientSocket, buffer.get(currentStream).getTorToProxy());
                    listenForResponse.start();
                    manager.getCommandQ().put("begin " + address.getHost() + " " +  address.getPort() + " " + currentStream);
                    List<byte[]> b = pack(request);
                    for(int i = 0; i < b.size(); i++) {
                        BlockingQueue proxyToTor = buffer.get(currentStream).getProxyToTor();
                        proxyToTor.put(b.get(i));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int getStreamNumber() {
        lockID.lock();
        try {
            streamID += 1;
        } finally {
            int temp = streamID;
            lockID.unlock();
            return temp;
        }
    }

    private List<byte[]> pack(String request) {
        List<byte[]> result = new ArrayList<>();

        byte[] requestBytes = request.getBytes(CHARSET);
        byte[] temp = new byte[498];
        int i;
        for(i = 0; i < requestBytes.length; i++) {
            if(i % 498 == 0 && i != 0) {
                result.add(temp);
                temp = new byte[498];
            }

            temp[i % 498] = requestBytes[i];
        }
        int size = i % 498;
        if (size != 0) {
            byte[] tempTemp = new byte[size];
            for(int j = 0; j < size; j++) {
                tempTemp[j] = temp[j];
            }
            temp = tempTemp;
        }
        result.add(temp);

        return result;

    }

    public void begin(int streamID, int circuitID, String host, int port) {
        Thread setup = new ServerSocketStartupThread(streamID, circuitID, host, port);
        setup.start();
    }
}
