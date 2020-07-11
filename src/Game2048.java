import com.javarush.engine.cell.*;


public class Game2048 extends Game {

    private static final int SIDE = 4;
    private int[][] gameField = new int[SIDE][SIDE];
    private  boolean isGameStopped = false;
    private int score =0;

    private void setCellColoredNumber(int x, int y, int value) {
        Color curr = getColorByValue(value);
        if (value !=0) {
            setCellValueEx(x, y, curr, Integer.toString(value));
        } else {
            setCellValueEx(x, y, curr, "");
        }
    }

    private boolean compressRow(int[] row) {
        boolean flag = false;
        int param = 0;
        for(int i = 0; i <row.length-1; i++) {
            for (int j =i+1; j <row.length; j ++) {
                if (row[i] == 0 && row[j] != 0) {
                    param = row[i];
                    row[i] = row[j];
                    row[j] = param;
                    flag = true;
                }
            }
        }
        return flag;
    }

    private void rotateClockwise() {
        int N = gameField.length;
        for (int i = 0; i < N / 2; i++) {
            for (int j = i; j < N - i - 1; j++) {
                int temp = gameField[i][j];
                gameField[i][j] = gameField[N - 1 - j][i];
                gameField[N - 1 - j][i] = gameField[N - 1 - i][N - 1 - j];
                gameField[N - 1 - i][N - 1 - j] = gameField[j][N - 1 - i];
                gameField[j][N - 1 - i] = temp;
            }
        }
    }

    public void onKeyPress(Key key) {
        if (isGameStopped) {
            if (key == Key.SPACE) {
                isGameStopped = false;
                score = 0;
                setScore(score);
                createGame();
                drawScene();
            }
        } else {
            if (!canUserMove()) {
                gameOver();
            } else {
                if (key == Key.LEFT) {
                    moveLeft();
                } else if (key == Key.RIGHT) {
                    moveRight();
                } else if (key == Key.DOWN) {
                    moveDown();
                } else if (key == Key.UP) {
                    moveUp();
                }
                if (key == Key.LEFT || key == Key.RIGHT || key == Key.UP || key == Key.DOWN) {
                    drawScene();
                }
            }
        }
    }

    private void moveLeft() {
        boolean[] flag = new boolean[gameField.length];
        boolean[] flag1 = new boolean[gameField.length];
        boolean[] flag2 = new boolean[gameField.length];
        for (int i = 0; i <gameField.length; i++) {
            flag[i] = compressRow(gameField[i]);
            flag1[i] = mergeRow(gameField[i]);
            flag2[i] = compressRow(gameField[i]);
        }
        for (int i =0; i <flag.length; i++) {
            if (flag[i] == true || flag1[i] == true || flag2[i] == true ) {
                createNewNumber();
                return;
            }
        }
    }

    private void moveRight() {
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }
    private void moveDown() {
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }
    private void moveUp() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }

    private int getMaxTileValue() {
        int max = gameField[0][0];
        for (int y = 0; y < gameField.length; y++) {
            for (int x = 0; x < gameField[y].length; x++) {
                if (gameField[y][x] > max) {
                    max = gameField[y][x];
                }
            }
        }
        return max;
    }

    private boolean mergeRow(int[] row) {
        boolean flag = false;
        int param = 0;
        for (int i =0; i < row.length-1; i++) {
            if (row[i] == row[i+1] && row[i] !=0) {
                row[i] = row[i] + row[i+1];
                row[i+1] = 0;
                score= score + row[i];
                setScore(score);
                flag = true;
            }
        }
        return flag;
    }

    private Color getColorByValue(int value) {
        switch (value) {
            case 0: return Color.WHITE;
            case 2: return Color.ALICEBLUE;
            case 4: return Color.AQUA;
            case 8: return Color.BISQUE;
            case 16:return Color.BEIGE;
            case 32:return Color.CHOCOLATE;
            case 64: return Color.GAINSBORO;
            case 128: return Color.MEDIUMSPRINGGREEN;
            case 256: return Color.SILVER;
            case 512: return Color.PINK;
            case 1024: return Color.SLATEGRAY;
            case 2048: return Color.VIOLET;
            default: return Color.ORANGE;
        }
    }

    private void createGame() {
        gameField = new int[SIDE][SIDE];
        createNewNumber();
        createNewNumber();

    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BISQUE, "YOU WIN!!!!!", Color.AQUA, 10);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BISQUE, "YOU LOOOOSER!!!!!", Color.AQUA, 10);
    }

    private boolean canUserMove(){
        boolean flag = false;
        int count = 0;
        for (int y = 0; y < gameField.length; y++) {
            for (int x = 0; x < gameField[y].length; x++) {
                if (gameField[y][x] == 0) {
                    count++;
                }
            }
        }
        if (count > 0) {
            flag = true;
        } else {
            for (int y = 0; y < gameField.length; y++) {
                for (int x = 0; x < gameField[y].length-1; x++) {
                    if (gameField[y][x] == gameField[y][x+1]) {
                        flag = true;
                        break;
                    }
                }
            }
            for (int y = 0; y < gameField.length-1; y++) {
                for (int x = 0; x < gameField[y].length; x++) {
                    if (gameField[y][x] == gameField[y+1][x]) {
                        flag = true;
                        break;
                    }
                }
            }

        }
        return flag;
    }

    private void createNewNumber() {
        int win = getMaxTileValue();
        if (win == 2048) {
            win();
        }
        boolean flag = true;
        int x =0;
        int y =0;
        while (flag) {
            x = getRandomNumber(SIDE);
            y = getRandomNumber(SIDE);
            int cur = gameField[x][y];
            if (cur==0) {
                flag = false;
            }
        }
        int t = getRandomNumber(10);
        if (t == 9) {
            gameField[x][y] = 4;
        } else {
            gameField[x][y] = 2;
        }

    }

    private void drawScene() {
        for (int x = 0; x < gameField.length; x++) {
            for (int y = 0; y < gameField[0].length; y++) {
                setCellColoredNumber(y, x, gameField[x][y]);
            }
        }
    }

    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
        drawScene();
    }
}
