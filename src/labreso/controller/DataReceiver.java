package labreso.controller;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Classe que manipula mensagens recebidas pelo cliente
 * @author lipec
 */
public class DataReceiver implements Runnable {

    //Listener que informara  os status ao cliente
    private ClientListener listener;
    private InputStream serverInputstream;

    
    //Interface do listener
    public interface ClientListener {
        
        //informa qunado um jogo comecar
        public void onGameStarted();
        //informa quando um acao for recebida
        public void onActionReceived(String player, String action);
        //informa resultado da rodada
        public void onResultWinner(String winner);
        //informa empate
        public void onDraw();
        // informa quando receber o nome dos players
        public void onGetPlayers(String[] players);
    }

    
    //Construtor
    public DataReceiver(InputStream serverInputStream, ClientListener listener) {
        this.serverInputstream = serverInputStream;
        this.listener = listener;
    }

    
    //Metodo com o comportamento da thread do DataReceiver do cliente
    @Override
    public void run() {
        //Cria um stream de leitura com o stream do socket do servidor
        Scanner dataReader = new Scanner(serverInputstream);
        //Enqaunto houver dados
        while (dataReader.hasNextLine()) {
            //Leia conteudo
            String msg  = dataReader.nextLine();
            //Separe a msg da flag de acao
            String[] line = msg.split("_");
            System.out.println(msg);
            switch (line[0]) {
                //Flag comecar o jogo
                case "Start":
                    listener.onGameStarted();
                    break;
                //Flag lance de algum jogador
                case "Action":
                    listener.onActionReceived(line[1],line[2]);
                    break;
                //Flag que informa o vencedor
                case "Winner":
                    listener.onResultWinner(line[1]);
                    break;
                //Flag que informa o empate
                case "Draw":
                    listener.onDraw();
                    break;
                 //Flag que informa o nome dos playes
                case "Players":
                    String[] players = line[1].split("&");
                    listener.onGetPlayers(players);
                    break;
            }
        }
        dataReader.close();
    }
}
