package labreso.controller;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import labreso.model.MessageEnum;

public class Client implements DataReceiver.ClientListener {

    private String clientName;
    private String host;
    private int port;
    private DataReceiver.ClientListener listener;
    private Socket clientSocket;
    private DataReceiver dataReceiver;
    private Thread clientThread;
    private Scanner userInput;
    private PrintStream userOutput;

    public Client(String host, int port, String clientName, DataReceiver.ClientListener listener) {
        this.host = host;
        this.port = port;
        this.clientName = clientName;
        this.listener = listener;
        userInput = new Scanner(System.in);
    }

    private void createClientSocket() {
        try {
            clientSocket = new Socket(host, port);
            System.out.println("O cliente se conectou ao servidor!");
        } catch (Exception e) {
            System.out.println("Erro ao conectar com o servidor na porta: " + port + ", Host: " + host);
        }
    }

    private void initDataReceiver() {
        try {
            dataReceiver = new DataReceiver(clientSocket.getInputStream(), this);
            clientThread = new Thread(dataReceiver);
            clientThread.start();
        } catch (Exception e) {
            System.out.println("Problemas ao  obter stream do servidor.Detalhes:\n");
            e.printStackTrace();
        }
    }

    private void readInput() {
        try {
            userOutput = new PrintStream(clientSocket.getOutputStream());
            userOutput.println("Nick_" + clientName.trim());
            while (userInput.hasNextLine()) {

            }

        } catch (Exception e) {
            System.out.println("Problemas ao  ler dados .Detalhes:\n");
        }
    }

    public void sendMessageToServer(String msg) {
        try {
            userOutput.println(msg);
        } catch (Exception e) {
            System.out.println("Problemas ao enviar  dados .Detalhes:\n");
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            userOutput.close();
            userInput.close();
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("Problemas ao  encerrar conexao.Detalhes:\n");
            e.printStackTrace();
        }

    }

    public void initClient() {
        createClientSocket();
        initDataReceiver();
        readInput();
    }

    @Override
    public void onGameStarted() {
        listener.onGameStarted();
    }

    @Override
    public void onDraw() {
        listener.onDraw();
    }

    @Override
    public void onGetPlayers(String[] players) {
        listener.onGetPlayers(players);
    }

    @Override
    public void onActionReceived(String player, String action) {
        listener.onActionReceived(player, action);
    }

    @Override
    public void onResultWinner(String winner) {
        listener.onResultWinner(winner);
    }

}
