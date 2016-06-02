import org.python.core.*;
import org.python.util.PythonInterpreter;

import java.io.*;
import java.math.BigInteger;

/**
 * Created by josephkesting on 5/31/16.
 */
public class RegistrationAgent {

    public static void main(String[] args) {
        try {
            RegistrationAgent reg = new RegistrationAgent("cse461.cs.washington.edu", 46101);
            System.out.println(reg.probe());
            reg.register(1234, 14000, "hermanos");
            System.out.println(reg.fetch("")[0].getData());
            reg.quit();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("HERE");
        }
    }

    Process process;
    BufferedWriter out;
    BufferedReader in;
    PythonInterpreter interpreter;
    PyObject agent;

    public RegistrationAgent(String host, int port) throws IOException {
        host = "\"" + host + "\"";
        ProcessBuilder processBuilder = new ProcessBuilder("python", "client.py", host, "" + port);
        processBuilder.redirectErrorStream(true);
        process = processBuilder.start();
        out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        System.out.println(in);
        System.out.println(out);
        interpreter = new PythonInterpreter();
        interpreter.execfile("agent.py");
        agent = interpreter.eval("RegistrationAgent("+host+", "+port+")");
    }

    public void register(int port, int data, String name) throws IOException {
        PyObject[] args = {new PyInteger(port), new PyInteger(data), new PyString(name)};
        agent.invoke("register", args);

    }

    public FetchResult[] fetch(String prefix) throws IOException {
        PyList res = (PyList)agent.invoke("fetch", new PyString(prefix));
        FetchResult[] results = new FetchResult[res.size()];
        for (int i = 0; i < res.size(); i++) {
            PyList itemLine = (PyList)res.get(i);
            String ip = (String) itemLine.get(0);
            int port = (int) itemLine.get(1);
            int data = ((BigInteger) itemLine.get(2)).intValue();
            FetchResult item = new FetchResult(ip, port, data);
            results[i] = item;
        }
        return results;
    }

    public boolean probe() throws IOException {
        PyBoolean res = (PyBoolean)agent.invoke("probe");

        return res.getBooleanValue();
    }

    public void unregister(int port) throws IOException {
        agent.invoke("unregister", new PyInteger(port));
    }

    public void quit() throws IOException {
        agent.invoke("close");
    }

    public class FetchResult {
        private String ip;
        private int port;
        private int data;

        public FetchResult (String input) {
            parseInput(input);
        }

        public FetchResult (String ip, int port, int data) {
            this.ip = ip;
            this.port = port;
            this.data = data;
        }

        private void parseInput(String input) {
            String[] splitInput = input.split(" ");
            ip = splitInput[1];
            port = Integer.parseInt(splitInput[2]);
            data = Integer.parseInt(splitInput[3]);
        }

        public String getIp() {
            return ip;
        }

        public int getPort() {
            return port;
        }

        public int getData() {
            return data;
        }
    }
}
