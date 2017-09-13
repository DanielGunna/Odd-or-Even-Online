package labreso.controller;

import java.io.IOException;
import labreso.model.ClientModel;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Server {

    private int port;
    private ServerSocket server;
    private HashMap<Integer, PrintStream> clientsHash;
    private HashMap<Integer, ClientModel> users;
    private HashMap<Integer, ClientModel> clientsList;
    private List<HashMap<Integer, Integer>> rooms;
    private GameSettings gameSettings;
    private HashMap<Integer, String> userActions;
    private int contId;

    public Server(int port) {
        this.port = port;
        users = new HashMap<>();
        userActions = new HashMap<>();
        clientsHash = new HashMap<>();
        clientsList = new HashMap<>();
        rooms = new ArrayList<>();
    }

    private void initServerSocket() {
        try {
            server = new ServerSocket(port);
            System.out.println("Porta 12345 aberta!");
        } catch (Exception e) {
            System.out.println("Erro ao criar servidor na porta " + port + ". Detalhes:\n");
            e.printStackTrace();
        }

    }

    private void acceptConnection() {
        try {
            Socket newClient = server.accept();
            System.out.println("Nova conexão com o cliente " + newClient.getPort());
            handleClient(newClient);
        } catch (Exception e) {
            System.out.println("Erro ao aceitar conexao. Detalhes:\n");
            e.printStackTrace();
        }
    }

    private void handleClient(Socket newClient) throws Exception {
        PrintStream newClientStream = new PrintStream(newClient.getOutputStream());
        clientsHash.put(contId, newClientStream);
        Thread newClientThread = new Thread(getClientRun(newClient));
        ClientModel newClientModel
                = new ClientModel(newClient, newClientStream, newClientThread, contId++);
        users.put(newClientModel.getPort(), newClientModel);
        clientsList.put(newClientModel.getPort(), newClientModel);
        newClientThread.start();
    }

    private Runnable getClientRun(Socket client) throws IOException {
        return () -> {
            try {
                Scanner scanner = new Scanner(client.getInputStream());
                while (scanner.hasNextLine()) {
                    String msg = scanner.nextLine();
                    System.out.println(msg);
                    String[] line = msg.split("_");
                    if (line[0].equals("Nick")) {
                        setNickClient(client.getPort(), line[1]);
                    } else {
                        sendCommand(client.getPort(), msg);
                    }
                }
                scanner.close();
            } catch (Exception e) {
                System.out.println("Erro ao criar run do client");
                e.printStackTrace();
            }
        };
    }

    private void setNickClient(int port, String name) {
        users.get(port).setName(name);
        verifyCanStart();
    }

    private int getNumberUser() {
        return contId;
    }

    private int getNumberCommands() {
        return userActions.size();
    }

    private boolean canPlay() {
        System.out.println("Let go!!!!");
        return (getNumberUser() == getNumberCommands());
    }

    private void addUserAction(int port, String action) {
        userActions.put(port, action);
    }

    private void sendCommand(int port, String command) {
        if (getNumberUser() == gameSettings.NUM_USERS) {
            addUserAction(port, command);
            sendAll("Action_" + users.get(port).getName() + "_" + command);
            if (canPlay()) {
                int result = verifyResult();
                if (result == -1) {
                    sendAll("Draw");
                } else {
                    sendAll("Winner_" + users.get(result).getName());
                }
                userActions.clear();
            }
        }
    }

    private int verifyResult() {
        int champs = -1;
        for (Map.Entry<Integer, String> i : userActions.entrySet()) {
            int count = 0;
            for (Map.Entry<Integer, String> j : userActions.entrySet()) {
                if (!(j.getKey().equals(i.getKey())) && (!j.getValue().equals(i.getValue()))) {
                    count++;
                }
            }
            if (count == users.size() - 1) {
                champs = i.getKey();
            }
        }

        return champs;
    }

    private void listenClients() {
        while (true) {
            acceptConnection();
        }

    }

    public void startServer() {
        initServerSocket();
        listenClients();
    }

    public void sendAll(String msg) {
        for (Map.Entry<Integer, ClientModel> cliente : clientsList.entrySet()) {
            cliente.getValue().getOutStream().println(msg);
        }
    }

    private void verifyCanStart() {
        if (contId == 3) {
            sendAll("Start");
            String mensagem = "";
            Set<Integer> keys = users.keySet();
            for (int key : keys) {
                mensagem += users.get(key).getName() + "&";
            }
            sendAll("Players_" + mensagem);
        }
    }

}
