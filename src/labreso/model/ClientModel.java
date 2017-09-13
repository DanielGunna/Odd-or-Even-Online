package labreso.model;

import java.io.PrintStream;
import java.net.Socket;

public class ClientModel {

    private Socket socket;
    private String name;
    private Thread thread;
    private int port;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    private PrintStream outStream;
    private int clientId;
    private boolean isPlaying;

    public ClientModel() {

    }

    public ClientModel(Socket newClient, PrintStream newClientStream, Thread newClientThread, int id) {
        socket = newClient;
        outStream = newClientStream;
        thread = newClientThread;
        port = newClient.getPort();
        setClientId(id);
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public PrintStream getOutStream() {
        return outStream;
    }

    public void setOutStream(PrintStream outStream) {
        this.outStream = outStream;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

}
