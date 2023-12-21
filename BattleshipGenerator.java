import java.util.Random;

public interface BattleshipGenerator {

    int BOARD_SIZE = 10;
    char SHIP = '#';
    char WATER = '.';

    int[] shipsSizes = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
    char[][] generateMap();

    static BattleshipGenerator defaultInstance() {
        return new BattleshipGenerator() {
            @Override
            public char[][] generateMap() {
                char[][] board = new char[BOARD_SIZE][BOARD_SIZE];
                setWater(board);
                placeShips(board);
                return board;
            }

            private void setWater(char[][] arr){
                for (int i = 0; i < BOARD_SIZE; i++){
                    for(int j = 0; j < BOARD_SIZE; j++){
                        arr[i][j] = WATER;
                    }
                }
            }

            private void placeShips(char[][] board){
                Random random = new Random();

                for(int ship: shipsSizes){
                    int direction = random.nextInt(2);
                    int row = random.nextInt(BOARD_SIZE);
                    int column = random.nextInt(BOARD_SIZE);
                    while(!canPlaceShip(board, row, column, direction, ship)){
                        direction = random.nextInt(2);
                        row = random.nextInt(BOARD_SIZE);
                        column = random.nextInt(BOARD_SIZE);
                    }
                    placeShip(board, row, column, ship, direction);
                }
            }

            private void placeShip(char[][] board, int row, int column, int length, int direction){
                if (direction == 0) {
                    for (int i = 0; i < length; i++) {
                        board[row][column + i] = SHIP;
                    }
                } else {
                    for (int i = 0; i < length; i++) {
                        board[row + i][column] = SHIP;
                    }
                }
            }


            private boolean canPlaceShip(char[][] board, int row, int col, int direction, int length) {
                if ((direction == 0 && col + length >= BOARD_SIZE) || (direction == 1 && row + length >= BOARD_SIZE)) return false;

                int checkColumn = direction == 0? length : 1;
                int checkRow = direction == 1? length : 1;

                for (int i = -1; i < checkRow + 1; i++){
                    for (int j = -1; j < checkColumn + 1; j++){
                        if(row + i >= 0 && row + i < BOARD_SIZE && col + j >= 0 && col + j < BOARD_SIZE){
                            if(board[row + i][col + j] == SHIP) return false;
                        }
                    }
                }
                return true;
            }
        };
    }
}
