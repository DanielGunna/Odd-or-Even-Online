package labreso.controller;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
 
/**
 * Classe para controlar conexao do cliente com server
 * @author lipec
 */
public class Client implements DataReceiver.ClientListener {
    //Nome do cliente
    private String clientName;
    //Host do servidor
    private String host;
    //Porta do servidor
    private int port;
    //Listener que recebera infos do datareceiver e passara para a view do jogo
    private DataReceiver.ClientListener listener;
    //socket do cliente
    private Socket clientSocket;
    //Campo objeto que controlar recebimento de mensagens do servidor
    private DataReceiver dataReceiver;
    //Thread do cliente
    private Thread clientThread;
    //Entrada de dados cliente
    private Scanner userInput;
    //Saida de dados do cliente
    private PrintStream userOutput;

    //Construtor
    public Client(String host, int port, String clientName, DataReceiver.ClientListener listener) {
        this.host = host;
        this.port = port;
        this.clientName = clientName;
        this.listener = listener;
        userInput = new Scanner(System.in);
    }

    
    //Metodo que cria conexao do socket do cliente com o servidor na porta e host
    //setados
    private void createClientSocket() {
        try {
            clientSocket = new Socket(host, port);
            System.out.println("O cliente se conectou ao servidor!");
        } catch (Exception e) {
            System.out.println("Erro ao conectar com o servidor na porta: " + port + ", Host: " + host);
        }
    }

    
    //Metodo que inicia a thread que ira receber os dados do server
    private void initDataReceiver() {
        try {
            //cria um novo objeto que manipula a recepcao de mensagem 
            //passando o stream  do server
            dataReceiver = new DataReceiver(clientSocket.getInputStream(), this);
            //Cria a thread da comunicao
            clientThread = new Thread(dataReceiver);
            //Inicia a thread do cliente
            clientThread.start();
        } catch (Exception e) {
            System.out.println("Problemas ao  obter stream do servidor.Detalhes:\n");
            e.printStackTrace();
        }
    }

    
    //Metodo para enviar o nick do cliente para o servidor para o servidor
    private void readInput() {
        try {
            //Cria stream de saida a partir do socket conectado ao server
            userOutput = new PrintStream(clientSocket.getOutputStream());
            //Envia a mensagerm com a flad de nick e nome do cliente
            userOutput.println("Nick_" + clientName.trim());
        } catch (Exception e) {
            System.out.println("Problemas ao  ler dados .Detalhes:\n");
        }
    }

    
    //Metodo que envia uma msg  para o servidor
    public void sendMessageToServer(String msg) {
        try {
            userOutput.println(msg);
        } catch (Exception e) {
            System.out.println("Problemas ao enviar  dados .Detalhes:\n");
            e.printStackTrace();
        }
    }

    
    //Metodo para fechar a conexao  com o servidor
    public void closeConnection() {
        try {
            
            //Fecha stram de saida
            userOutput.close();
            //Fecha stream de entrada
            userInput.close();
            //fecha conexao do socket
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("Problemas ao  encerrar conexao.Detalhes:\n");
            e.printStackTrace();
        }

    }
    //inicia cliente
    public void initClient() {
        createClientSocket();
        initDataReceiver();
        readInput();
    }

    
    //Implementacao dos metodos que enviam mensagems do listener
    //basicamente eles so repassam o conteudo para  o listener(no caso a view do jogo)
    //exterior  a  implementacao do cliente
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
