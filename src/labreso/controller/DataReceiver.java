package labreso.controller;

import java.io.InputStream;
import java.util.Scanner;

public class DataReceiver implements Runnable {

    private ClientListener listener;
    private InputStream serverInputstream;

    public interface ClientListener {

        public void onGameStarted();

        public void onActionReceived(String player, String action);

        public void onResultWinner(String winner);

        public void onDraw();

        public void onGetPlayers(String[] players);
    }

    public DataReceiver(InputStream serverInputStream, ClientListener listener) {
        this.serverInputstream = serverInputStream;
        this.listener = listener;
    }

    @Override
    public void run() {
        Scanner dataReader = new Scanner(serverInputstream);
        while (dataReader.hasNextLine()) {
            String msg  = dataReader.nextLine();
            String[] line = msg.split("_");
            System.out.println(msg);
            switch (line[0]) {
                case "Start":
                    listener.onGameStarted();
                    break;
                case "Action":
                    listener.onActionReceived(line[1],line[2]);
                    break;
                case "Winner":
                    listener.onResultWinner(line[1]);
                    break;
                case "Draw":
                    listener.onDraw();
                    break;
                case "Players":
                    String[] players = line[1].split("&");
                    listener.onGetPlayers(players);
                    break;
            }
        }
        dataReader.close();
    }
}
