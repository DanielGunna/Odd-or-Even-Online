/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labreso.controller;

/**
 *
 * @author 940437
 */
public class GameSettings {

    public static final int NUM_USERS = 3;
    public static final int ERR_MAX_USERS = 0;
    public static final int ERR_MIN_USERS = 1;
    public static final int ERR_FULL_USERS = 2;
    private int isConnected;

    public GameSettings() {
        isConnected = 0;
    }

    public int getIsConnected() {
        return isConnected;
    }

    public boolean closeConnection() {
        boolean conn = false;
        if (isConnected >= 0) {
            conn = true;
            isConnected--;
        } else {
            handleConnectionStatus(ERR_MIN_USERS);
        }
        return conn;
    }

    public boolean tryConnect() {
        boolean connectionStatus = false;
        if (isConnected < NUM_USERS) {
            connectionStatus = true;
            this.isConnected++;
        } else if (isConnected == NUM_USERS + 1) {
            connectionStatus = false;
            isConnected++;
            handleConnectionStatus(ERR_FULL_USERS);
        } else {
            handleConnectionStatus(ERR_MAX_USERS);
        }
        return connectionStatus;
    }

    public void handleConnectionStatus(int errorStatus) {
        switch (errorStatus) {
            case 0:
                System.out.println("Nao foi possivel entrar no jogo.Numero maximo atingido ");
                break;
            case 1:
                System.out.println("Erro ao encerrar conexao");
                break;
            case 2:
                System.out.println("O jogo ja esta com o numero maximo de players !");
                break;
            case 3:
                System.out.println("Esperando por outros  jogadores...");
                break;
        }
    }
}
