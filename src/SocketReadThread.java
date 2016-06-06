import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

public class SocketReadThread extends Thread {

    private final SocketData data;
    private final SocketManager socketManager;

    public SocketReadThread(SocketData data) {
        this.data = data;
        this.socketManager = SocketManager.getInstance(0);
    }

    public void run() {
        byte[] messageIn = new byte[512];
        InputStream inputStream = data.getIn();
        try {
            int bytesRead = inputStream.read(messageIn);
            while (bytesRead > 0) {
                try {
                    processMessage(messageIn);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bytesRead = inputStream.read(messageIn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMessage(byte[] messageBytes) throws InterruptedException {
        int inID = data.getPort();
        MessageObject message = Decoder.decode(messageBytes);
        int inCircuitID = message.getCircuitID();
        MessageType type = message.getMessageType();
        RelayObject relayMessage;
        CircuitObject circuitMessage;
        OpenObject openMessage;

        Pair outPair = socketManager.getRoutingTable().get(new Pair(inCircuitID, inID));

        switch(type) {
            case BEGIN: {
                relayMessage = (RelayObject) message;
                String body = relayMessage.getBody();
                String[] splitBody = body.split(":");
                String host = splitBody[0];
                int port = Integer.parseInt(splitBody[1].split("\0")[0]);
                if (outPair.isExit()) {
                    BlockingQueue bufferToTorSocket = socketManager.getSocketList().get(inID).getBuffer();
                    Thread readFromProxyBuffer = new WriteToTorFromServerBufferThread(inCircuitID, relayMessage.getStreamID(), bufferToTorSocket);
                    readFromProxyBuffer.start();
                    HTTPProxy.getInstance(0).begin(relayMessage.getStreamID(), inCircuitID, host, port);
                } else {
                    relayMessage.setCircuitID(outPair.getCircuit());
                    socketManager.getSocketList().get(outPair.getSocket()).getBuffer().put(relayMessage.getBytes());
                }
                break;
            } case BEGIN_FAILED: {
                relayMessage = (RelayObject) message;
                if (outPair.isEntry()) {
                    socketManager.getCommandQ().put("failed begin");
                } else {
                    relayMessage.setCircuitID(outPair.getCircuit());
                    socketManager.getSocketList().get(outPair.getSocket()).getBuffer().put(relayMessage.getBytes());
                }
                break;
            } case CONNECTED: {
                relayMessage = (RelayObject) message;
                if (outPair.isEntry()) {
                    socketManager.getCommandQ().put("connected");
                } else {
                    relayMessage.setCircuitID(outPair.getCircuit());
                    socketManager.getSocketList().get(outPair.getSocket()).getBuffer().put(relayMessage.getBytes());
                }
                break;
            } case CREATE: {
                socketManager.createReceived(inID, inCircuitID);
                break;
            } case CREATED: {
                socketManager.created(inID, inCircuitID);
                break;
            } case CREATE_FAILED: {
                if (outPair.isEntry()) {
                    socketManager.getCommandQ().put("failed create");
                } else {
                    RelayObject extendFailed = new RelayObject(outPair.getCircuit(), 0, 0, 11);
                    socketManager.getSocketList().get(outPair.getSocket()).getBuffer().put(extendFailed.getBytes());
                }
                break;
            } case OPEN: {
                openMessage = (OpenObject) message;
                OpenObject opened = new OpenObject(openMessage.getOpenerID(), openMessage.getOpenedID(), 6);
                socketManager.getSocketList().get(outPair.getSocket()).getBuffer().put(opened.getBytes());
                break;
            } case OPENED: {
                //SHOULD NEVER HAPPEN
                break;
            } case OPEN_FAILED: {
                //SHOULD NEVER HAPPEN
                break;
            } case EXTEND: {
                relayMessage = (RelayObject) message;
                if (outPair.isExit()) {
                    socketManager.extend(inID, inCircuitID, relayMessage.getBody());
                } else {
                    relayMessage.setCircuitID(outPair.getCircuit());
                    socketManager.getSocketList().get(outPair.getSocket()).getBuffer().put(relayMessage.getBytes());
                }
                break;
            } case EXTENDED: {
                relayMessage = (RelayObject) message;
                if (outPair.isEntry()) {
                    socketManager.getCommandQ().put("extended");
                } else {
                    relayMessage.setCircuitID(outPair.getCircuit());
                    socketManager.getSocketList().get(outPair.getSocket()).getBuffer().put(relayMessage.getBytes());
                }
                break;
            } case EXTEND_FAILED: {
                relayMessage = (RelayObject) message;
                if (outPair.isEntry()) {
                    socketManager.getCommandQ().put("failed extend");
                } else {
                    relayMessage.setCircuitID(outPair.getCircuit());
                    socketManager.getSocketList().get(outPair.getSocket()).getBuffer().put(relayMessage.getBytes());
                }
                break;
            } case DATA: {
                relayMessage = (RelayObject) message;
                if (outPair.isExit()) {
                    BlockingQueue buff = SocketServerBuffers.getInstance().get(relayMessage.getCircuitID(), relayMessage.getStreamID()).getTorToProxy();
                    buff.put(relayMessage.getData());
                } else if (outPair.isEntry()) {
                    BlockingQueue buff = SocketClientBuffers.getInstance().get(relayMessage.getStreamID()).getTorToProxy();
                    buff.put(relayMessage.getData());
                } else {
                    relayMessage.setCircuitID(outPair.getCircuit());
                    socketManager.getSocketList().get(outPair.getSocket()).getBuffer().put(relayMessage.getBytes());
                }
                break;
            } case END: {
                relayMessage = (RelayObject) message;
                if (outPair.isExit()) {
                    SocketServerBuffers.getInstance().close(relayMessage.getCircuitID(), relayMessage.getStreamID());
                } else if (outPair.isEntry()) {
                    SocketClientBuffers.getInstance().close(relayMessage.getStreamID());
                } else {
                    relayMessage.setCircuitID(outPair.getCircuit());
                    socketManager.getSocketList().get(outPair.getSocket()).getBuffer().put(relayMessage.getBytes());
                }
                break;
            } case DESTROY: {
                circuitMessage = (CircuitObject) message;
                if (!outPair.isExit() && !outPair.isEntry()) {
                    circuitMessage.setCircuitID(outPair.getCircuit());
                    socketManager.getSocketList().get(outPair.getSocket()).getBuffer().put(circuitMessage.getBytes());
                }
                socketManager.destroy(inCircuitID, inID);

                break;
            }
        }
    }
}
