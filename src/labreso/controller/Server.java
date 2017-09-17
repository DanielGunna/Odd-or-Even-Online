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
import java.util.Timer;
import java.util.TimerTask;
 
/**
 * Classe do servidor
 * @author lipec
 */
public class Server {

    //Porta servidor
    private int port;
    //Socket servidor
    private ServerSocket server;
    //Estrtutra que armazena stream dos clientes 
    private HashMap<Integer, PrintStream> clientsHash;
    //Estrtutra que armazena  informaao dos clientes indexados por porta
    private HashMap<Integer, ClientModel> users;
    //Estrutura que armazena  informaao dos clientes indexados por id
    private HashMap<Integer, ClientModel> clientsList;
    //Estrtutra que pode ser usada para armazenar salas (nao foi implementado)
    private List<HashMap<Integer, Integer>> rooms;
    //Campo de classe que contem algumas constantes do jogo
    private GameSettings gameSettings;
    //Estrutura para armazenar acoes dos jogaodres
    private HashMap<Integer, String> userActions;
    //ID    incremental dos jogadores
    private int contId;

    
    //Construtor que recebe a porta onde o server ira se conectar
    public Server(int port) {
        this.port = port;
        users = new HashMap<>();
        userActions = new HashMap<>();
        clientsHash = new HashMap<>();
        clientsList = new HashMap<>();
        rooms = new ArrayList<>();
    }

    //Metodo que inicia socket do servidor na porta definida
    private void initServerSocket() {
        try {
            server = new ServerSocket(port);
            System.out.println("Porta "+port+" aberta! Aguardando conexoes...");
        } catch (Exception e) {
            System.out.println("Erro ao criar servidor na porta " + port + ". Detalhes:\n");
            e.printStackTrace();
        }

    }
    //Metodo que aceita a conexao de um socket de um  novo cliente
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

    //Metodo para manipular a conexao de um novo cliente e armazenamento de suas
    //informacoes e  configuracao para troca de mensagens
    private void handleClient(Socket newClient) throws Exception {
        //Cria um novo stream  de saida para comunicao com cliente 
        PrintStream newClientStream = new PrintStream(newClient.getOutputStream());
        //armazena na estrutura de streams
        clientsHash.put(contId, newClientStream);
        //Cria a nova thread do cliente
        Thread newClientThread = new Thread(getClientRun(newClient));
        //Cria objeto com infos do cliente  e armazena por id e por porta 
        ClientModel newClientModel
                = new ClientModel(newClient, newClientStream, newClientThread, contId++);
        
        users.put(newClientModel.getPort(), newClientModel);
        clientsList.put(newClientModel.getPort(), newClientModel);
        //Inica a thread do cliente
        newClientThread.start();
    }

    //Metodo que retorna o runnable que contem a logica de execucao da thread do cliente
    private Runnable getClientRun(Socket client) throws IOException {
        return () -> {
            try {
                //Cria um novo stream de entrada  para receber msgs do cliente
                Scanner scanner = new Scanner(client.getInputStream());
                //enquanto houver conteudo neste stream
                while (scanner.hasNextLine()) {
                    //Leia o conteudo
                    String msg = scanner.nextLine();
                    System.out.println(msg);
                    //As msgs seguem o paadrao FLAG_ConteudoDaMensagem,
                    //onde o campo FLAG determina um acao para o server ou clientes portanto 
                    //splita-se pero carcter `_` p/ obter a msg
                    String[] line = msg.split("_");
                    //Se a FLAG for nick o add nome do client 
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

    //Metodo que seta o nickname de um cliente em determinada porta
    private void setNickClient(int port, String name) {
        users.get(port).setName(name);
        verifyCanStart();
    }
    //Metdo que retorna quantidade de clientes conectados
    private int getNumberUser() {
        return contId;
    }
    //Metodo que retorna numero de comandos enviados pelos clientes
    private int getNumberCommands() {
        return userActions.size();
    }
    //Metodo que verfica se o resultado da partida pode ser verficado
    //verficando se todos clientes conectados fizeram sua jogada
    private boolean canPlay() {
        System.out.println("Let go!!!!");
        return (getNumberUser() == getNumberCommands());
    }
    //Metodo que registra acao de um cliente
    private void addUserAction(int port, String action) {
        userActions.put(port, action);
    }
    //Metodo que envia um comando para um cliente conectado em determinada porta
    private void sendCommand(int port, String command) {
        //Se a quantidade de jogadores for suficente para uma  partida
        if (getNumberUser() == gameSettings.NUM_USERS) {
            //Registra acao do jogador
            addUserAction(port, command);
            //Envia acao pra todos os outros jogadores
            sendAll("Action_" + users.get(port).getName() + "_" + command);
            //Verifica se pode mostrar o resultado
            if (canPlay()) {
                //armazena porta do vencedor 
                int result = verifyResult();
                //Se houve empate envia a flag de empate para todos
                if (result == -1) {
                    sendAll("Draw");
                } else {
                    //senao envia para todos o nome do vencedor
                    sendAll("Winner_" + users.get(result).getName());
                }
                //Limpe as acoes do jogadores para uma nova rodada
                userActions.clear();
            }
        }
    }
    //Metodo que verfica o resultado da partida e retorna a porta onde 
    // o cliente vencedor esta conectado ou -1 em caso de empate
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
    //Metodo que fica escutando a porta do server por novos clientes
    private void listenClients() {
        while (true) {
            acceptConnection();
        }

    }
    //Metodo que inicia  o servidor
    public void startServer() {
        initServerSocket();
        listenClients();
    }
    //Metodo que realiza um broadcast para todos os clientes
    public void sendAll(String msg) {
        for (Map.Entry<Integer, ClientModel> cliente : clientsList.entrySet()) {
            cliente.getValue().getOutStream().println(msg);
        }
    }

    
    //Metodo que verfica se existem clientes suficientes para que  o jogo comece
    private void verifyCanStart() {
        //Quando houver tres jogadores
        if (contId == 3) {
            //Envie que o jogo ira comecar
            sendAll("Start");
            String mensagem = "";
            Set<Integer> keys = users.keySet();
            //Concatene os nomes de todos os jogadores separados por um caracter separador defindo
            for (int key : keys) {
                mensagem += users.get(key).getName() + "&";
            }
            
            final String msg  = mensagem;
            //Por problemas com a conexao do ultimo jogador a se 
            //conectar aguarde um segundo antes de enviar para todos os nomes
            new Timer().schedule( new  TimerTask() {
                
                @Override
                public void run() {
                    sendAll("Players_"+msg);
                }
            },1000);
            
        }
    }

}
