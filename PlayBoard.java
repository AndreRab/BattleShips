import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class PlayBoard {
    private char[][] board;
    private int shipsNumber;
    private final int boardSize = 10;

    PlayBoard(){
        shipsNumber = 10;
        board = new char[boardSize][boardSize];
        for (int i = 0 ; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++){
                board[i][j] = '.';
            }
        }
    }

    public int getShipsNumber(){
        return shipsNumber;
    }

    PlayBoard(String mapFile){
        shipsNumber = 10;
        Path mapFilePath = Paths.get(mapFile);
        board = new char[boardSize][boardSize];
        if(Files.exists(mapFilePath)){
            try {
                var lines = Files.readAllLines(mapFilePath);
                for(int i = 0; i < lines.size(); i++){
                    board[i] = lines.get(i).toCharArray();
                }
            } catch (IOException e) {
                board = BattleshipGenerator.defaultInstance().generateMap();
            }
        } else{
            board = BattleshipGenerator.defaultInstance().generateMap();
        }
    }

    public void printBoard(){
        for (int i = 0 ; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++){
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

    public void printBoardWithReplace(){
        for (char[] chars : board) {
            for (char aChar : chars) {
                if(aChar == 'm' || aChar == '~') aChar = '.' ;
                else if(aChar == '.') aChar = '?';
                System.out.print(aChar);
            }
            System.out.print("\n");
        }
    }

    public String getRandomCoordinates(){
        Random random = new Random();
        int x = random.nextInt(boardSize);
        int y = random.nextInt(boardSize);
        while(board[y][x] != '.'){
            x = random.nextInt(boardSize);
            y = random.nextInt(boardSize);
        }
        return new Coordinats(y, x).toString();
    }

    private boolean checkShip(int i, int j){
        //check left
        int cur = j - 1;
        while(cur != -1){
            if(board[i][cur] == '#') return true;
            else if(board[i][cur] != '@') break;
            cur --;
        }

        //check right
        cur = j + 1;
        while(cur != boardSize){
            if(board[i][cur] == '#') return true;
            else if(board[i][cur] != '@') break;
            cur++;
        }

        //check bottom
        cur = i + 1;
        while(cur != boardSize){
            if(board[cur][j] == '#') return true;
            else if(board[cur][j] != '@') break;
            cur++;
        }

        //check top
        cur = i - 1;
        while(cur != -1){
            if(board[cur][j] == '#') return true;
            else if(board[cur][j] != '@') break;
            cur--;
        }

        return false;
    }

    public void setField(char c, String coordinates){
        board[translateToCoordinates(coordinates).y()][translateToCoordinates(coordinates).x()] = c;
    }

    public void surroundShip(String coordinates) {
        Coordinats c = translateToCoordinates(coordinates);
        int row = c.y();
        int col = c.x();
        markAdjacentCells(row, col);
        // Left of the hit
        for (int j = col - 1; j >= 0 && board[row][j] == '#'; j--) {
            markAdjacentCells(row, j);
        }

        // Right of the hit
        for (int j = col + 1; j < boardSize && board[row][j] == '#'; j++) {
            markAdjacentCells(row, j);
        }

        // Above the hit
        for (int i = row - 1; i >= 0 && board[i][col] == '#'; i--) {
            markAdjacentCells(i, col);
        }

        // Below the hit
        for (int i = row + 1; i < boardSize && board[i][col] == '#'; i++) {
            markAdjacentCells(i, col);
        }
    }

    private void markAdjacentCells(int row, int col) {
        // Mark left cell
        if (col > 0 && board[row][col - 1] != '#') {
            board[row][col - 1] = 'm';
        }
        // Mark right cell
        if (col < boardSize - 1 && board[row][col + 1] != '#') {
            board[row][col + 1] = 'm';
        }
        // Mark top cell
        if (row > 0 && board[row - 1][col] != '#') {
            board[row - 1][col] = 'm';
        }
        // Mark bottom cell
        if (row < boardSize - 1 && board[row + 1][col] != '#') {
            board[row + 1][col] = 'm';
        }
    }

    public String guessWhereShip(String coordinates){
        Coordinats c = translateToCoordinates(coordinates);
        if((c.x() > 0) && (board[c.y()][c.x() - 1] == '.')){
            return new Coordinats(c.y(), c.x() - 1).toString();
        }
        if((c.x() < boardSize - 1) && (board[c.y()][c.x() + 1] == '.')){
            return new Coordinats(c.y(), c.x() + 1).toString();
        }
        if((c.y() > 0) && (board[c.y() - 1][c.x()] == '.')){
            return new Coordinats(c.y() - 1, c.x()).toString();
        }
        if((c.y() < boardSize - 1) && (board[c.y() + 1][c.x()] == '.')){
            return new Coordinats(c.y() + 1, c.x()).toString();
        }
        return getRandomCoordinates();
    }


    public String applyShot(String command){
        Coordinats coordinats = translateToCoordinates(command);
        int x = coordinats.x();
        int y = coordinats.y();
        if(y < 0 || y > boardSize - 1 || x < 0 || x > boardSize - 1) return Communicators.ERROR;
        switch (board[y][x]){
            case '.':
                board[y][x] = '~';
            case '~':
                return Communicators.PUDLO;
            case '#':
                board[y][x] = '@';
            case '@':
                if(checkShip(y, x)){
                    return Communicators.TRAFIONY;
                }
                else{
                    shipsNumber--;
                    if(shipsNumber == 0){
                        return Communicators.OSTATNI_ZATOPIONY;
                    }
                    return Communicators.TRAFIONY_ZATOPIONY;
                }
        }
        return null;
    }

    private Coordinats translateToCoordinates(String command){
        int x = command.charAt(0) - 'A';
        int y = Integer.parseInt(command.substring(1)) - 1;
        return new Coordinats(y, x);
    }
}

record Coordinats(int y, int x){
    @Override
    public String toString() {
        char xC = (char) (x + 'A');
        return xC + String.valueOf(y + 1);
    }
}
