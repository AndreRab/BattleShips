import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server {
    private static final String MODECLIENT = "client";
    private static final String MODESERVER = "server";
    private static PlayBoard playerBoard;
    private static PlayBoard opponentBoard;
    private static String lastShot = null;
    private static String lastShotWithFind = null;
    private static String lastResult = null;
    private static boolean findAndKill = true;
    private static int errorsNum = 0;
    public static void main(String[] args) {
        try {
            int splitter = 0;
            String serverToConnect = "";
            String mode = args[splitter++];
            if (mode.equals(MODECLIENT)) {
                serverToConnect = args[splitter++];
            }
            String port = args[splitter++];
            String mapFile = args[splitter];

            playerBoard = new PlayBoard(mapFile);
            opponentBoard = new PlayBoard();
            playerBoard.printBoard();

            if (mode.equals(MODESERVER)) {
                startServer(port);
            } else if (mode.equals(MODECLIENT)) {
                connectToServer(serverToConnect, port);
            } else {
                System.out.println("Incorrect mode, please try again");
            }
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Write mode, server, port and map-file to start the game");
            e.printStackTrace();
        }
    }

    private static String applyCommunicat(String command){
        Message mes;
        try{
            mes = new Message(command);
        } catch (ArrayIndexOutOfBoundsException e){
            return Communicators.ERROR;
        }
        switch (mes.communicate()){
            case Communicators.OSTATNI_ZATOPIONY:
                opponentBoard.setField('#', lastShot);
                return Communicators.WINNER;
            case Communicators.PUDLO:
                opponentBoard.setField('~', lastShot);
                break;
            case Communicators.TRAFIONY:
                findAndKill = false;
                lastShotWithFind = lastShot;
                opponentBoard.setField('#', lastShot);
                break;
            case Communicators.TRAFIONY_ZATOPIONY:
                findAndKill = true;
                opponentBoard.setField('#', lastShot);
                break;
            case Communicators.START:
                break;
            default:
                return Communicators.ERROR;
        }
        lastResult = mes.communicate();
        return playerBoard.applyShot(mes.field());
    }
    
    private static String makeDecision(){
        if(lastResult == null || lastResult.equals(Communicators.START)){
            lastShot = opponentBoard.getRandomCoordinates();
        }
        if(lastResult != null && lastResult.equals(Communicators.TRAFIONY_ZATOPIONY)){
            opponentBoard.surroundShip(lastShot);
            lastShot = opponentBoard.getRandomCoordinates();
        }
        else if(!findAndKill){
            lastShot = opponentBoard.guessWhereShip(lastShotWithFind);
        }
        else{
            lastShot = opponentBoard.getRandomCoordinates();
        }
        return lastShot;
    }

    private static void endGameYouLost(){
        System.out.println("Przegrana\n");
        opponentBoard.printBoardWithReplace();
        System.out.println();
        playerBoard.printBoard();
    }

    private static void endGameYouWon(){
        System.out.println("Wygrana\n");
        opponentBoard.printBoardWithReplace();
        System.out.println();
        playerBoard.printBoard();
    }

    private static void startServer(String port){
        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port));
            Socket clientSocket = serverSocket.accept();
            clientSocket.setSoTimeout(1000);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while (playerBoard.getShipsNumber() != 0){
                String command = in.readLine();
                System.out.println("Get message: " + command);
                String result = applyCommunicat(command);
                if (result.equals(Communicators.OSTATNI_ZATOPIONY)){
                    endGameYouLost();
                    out.println(new Message(Communicators.OSTATNI_ZATOPIONY, lastShot));
                    break;
                }
                else if(result.equals(Communicators.WINNER)){
                    endGameYouWon();
                    break;
                }
                else if(result.equals(Communicators.ERROR)){
                    out.println(new Message(lastResult, lastShot));
                    errorsNum++;
                    if(errorsNum == 2){
                        System.out.println("Błąd komunikacji");
                        break;
                    }
                }
                String field = makeDecision();
                out.println(new Message(result, field));
                System.out.println("Send message: " + new Message(result, field));
            }
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Błąd komunikacji");
        }
    }

    private static void connectToServer(String serverToConnect, String port){
        try {
            Socket clientSocket = new Socket(serverToConnect, Integer.parseInt(port));
            clientSocket.setSoTimeout(1000);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String field = makeDecision();
            out.println(new Message(Communicators.START, field));
            System.out.println("Send message: " + new Message(Communicators.START, field));
            while (playerBoard.getShipsNumber() != 0){
                String command = in.readLine();
                System.out.println("Get message: " + command);
                String result = applyCommunicat(command);
                if (result.equals(Communicators.OSTATNI_ZATOPIONY)){
                    endGameYouLost();
                    out.println(new Message(Communicators.OSTATNI_ZATOPIONY, lastShot));
                    break;
                }
                else if(result.equals(Communicators.WINNER)){
                    endGameYouWon();
                    break;
                }
                else if(result.equals(Communicators.ERROR)){
                    out.println(new Message(lastResult, lastShot));
                    errorsNum++;
                    if(errorsNum == 2){
                        System.out.println("Błąd komunikacji");
                        break;
                    }
                }
                field = makeDecision();
                out.println(new Message(result, field));
                System.out.println("Send message: " + new Message(result, field));
            }
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Błąd komunikacji");
        }
    }
}
