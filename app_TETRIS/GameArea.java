package app_TETRIS;

import java.awt.Color;

public class GameArea {
    private int fieldHight = 21;
    private int fieldWidth = 12;
    private int grandHight = 30;
    private int grandWidth = 20;
    private int[][] field;
    private Color[][] fieldColors;
    private int[][] bufferField;
    private Color[][] bufferFieldColors;
    private int score = 0;
    private int linecount = 0;
    private String name;
    private int lastClearedLines = 0; 

    public GameArea() {
        this.field = new int[grandHight][grandWidth];
        this.fieldColors = new Color[grandHight][grandWidth];
        this.bufferField = new int[grandHight][grandWidth];
        this.bufferFieldColors = new Color[grandHight][grandWidth];
        initBufferField();
        initField();
    }

    public int getScore() {
        return this.score;
    }

    public int getCount() {
        return this.linecount;
    }

    public int resetCount() {
        this.linecount = 0;
        return this.linecount;
    }

    public int getFieldHight() {
        return this.fieldHight;
    }

    public int getFieldWidth() {
        return this.fieldWidth;
    }

    public int getGrandHight() {
        return this.grandHight;
    }

    public int getGrandWidth() {
        return this.grandWidth;
    }

    public int[][] getBufferField() {
        return this.bufferField;
    }

    public int[][] getField() {
        return this.field;
    }

    public Color[][] getFieldColors() {
        return this.fieldColors;
    }

    public Color[][] getBufferFieldColors() {
        return this.bufferFieldColors;
    }

    public GameArea(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void initField() {
        for (int y = 0; y < getFieldHight(); y++) {
            for (int x = 0; x < getFieldWidth(); x++) {
                field[y][x] = bufferField[y][x];
                fieldColors[y][x] = bufferFieldColors[y][x];
            }
        }
    }

    public void initBufferField() {
        for (int y = 0; y < getFieldHight(); y++) {
            for (int x = 0; x < getFieldWidth(); x++) {
                bufferField[y][x] = 0;
                bufferFieldColors[y][x] = null;
            }
        }
        for (int y = 0; y < getFieldHight(); y++) {
            bufferField[y][0] = bufferField[y][getFieldWidth() - 1] = 1;
        }
        for (int x = 0; x < getFieldWidth(); x++) {
            bufferField[getFieldHight() - 1][x] = 1;
        }
    }

    public void drawField() {
        for (int y = 0; y < getFieldHight(); y++) {
            for (int x = 0; x < getFieldWidth(); x++) {
                System.out.printf("%s", (field[y][x] == 1 ? "��" : "��"));
            }
            System.out.println();
        }
        System.out.println("Lines cleared: " + linecount);
        System.out.print("Name: " + name + "   ");
        System.out.println("Score: " + score);
    }

    public void drawNextMino(Mino nextMino) {
        int[][][] m = nextMino.getMino();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                System.out.printf("%s", (m[0][y][x] == 1 ? "o" : "x"));
            }
            System.out.println();
        }
    }

    public void drawFieldAndMino(Mino mino, Mino nextMino) {
        if (isCollison(mino)) {
            bufferFieldAddMino(mino);
            initField();
            mino.initMino();
        } else {
            initField();
            fieldAddMino(mino);
        }
        drawField();
        System.out.println();
    }

    public void fieldAddMino(Mino mino) {
        for (int y = 0; y < mino.getMinoSize(); y++) {
            for (int x = 0; x < mino.getMinoSize(); x++) {
                if (mino.getMino()[mino.getMinoAngle()][y][x] == 1) {
                    this.field[mino.getMinoY() + y][mino.getMinoX() + x] = 1;
                    this.fieldColors[mino.getMinoY() + y][mino.getMinoX() + x] = mino.getColor();
                }
            }
        }
    }

    public void bufferFieldAddMino(Mino mino) {
        for (int y = 0; y < mino.getMinoSize(); y++) {
            for (int x = 0; x < mino.getMinoSize(); x++) {
                if (mino.getMino()[mino.getMinoAngle()][y][x] == 1) {
                    this.bufferField[mino.getMinoY() + y][mino.getMinoX() + x] = 1;
                    this.bufferFieldColors[mino.getMinoY() + y][mino.getMinoX() + x] = mino.getColor();
                }
            }
        }
    }

    public boolean isCollison(Mino mino) {
        for (int r = 0; r < mino.getMinoSize(); r++) {
            for (int c = 0; c < mino.getMinoSize(); c++) {
                if (mino.getMino()[mino.getMinoAngle()][r][c] == 1) {
                    int newY = mino.getMinoY() + r + 1;
                    int newX = mino.getMinoX() + c;
                    if (newY < 0 || newY >= getFieldHight() || newX < 0 || newX >= getFieldWidth()) {
                        return true;
                    }
                    if (this.bufferField[newY][newX] == 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCollison(Mino mino, int _x, int _y, int _angle) {
        for (int r = 0; r < mino.getMinoSize(); r++) {
            for (int c = 0; c < mino.getMinoSize(); c++) {
                if (mino.getMino()[_angle][r][c] == 1) {
                    int checkX = _x + c;
                    int checkY = _y + r;
                    if (checkX < 0 || checkX >= getFieldWidth() || checkY < 0 || checkY >= getFieldHight()) {
                        return true;
                    }
                    if (checkX < 0 || checkX >= getFieldWidth() || checkY < 0 || checkY >= getFieldHight()) {
                        continue;
                    }
                    if (getBufferField()[checkY][checkX] == 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void eraseLine() {
        boolean isFill;
        int linesCleared = 0;

        for (int y = getFieldHight() - 2; y > 0; y--) {
            isFill = true;
            for (int x = 1; x < getFieldWidth() - 1; x++) {
                if (bufferField[y][x] == 0) {
                    isFill = false;
                    break;
                }
            }
            if (isFill) {
                linesCleared++;
                for (int _y = y; _y > 0; _y--) {
                    for (int x = 1; x < getFieldWidth() - 1; x++) {
                        bufferField[_y][x] = bufferField[_y - 1][x];
                        bufferFieldColors[_y][x] = bufferFieldColors[_y - 1][x];
                    }
                }
                y++;
            }
        }

        linecount += linesCleared;
        lastClearedLines = linesCleared;
        addScore(linesCleared);
    }

    public void addScore(int linesCleared) {
        int intMax = 21_4748_3647;

        switch (linesCleared) {
            case 1:
                score = Math.min(score + 40, intMax);
                break;
            case 2:
                score = Math.min(score + 100, intMax);
                break;
            case 3:
                score = Math.min(score + 300, intMax);
                break;
            case 4:
                                score = Math.min(score + 1200, intMax);
                break;
            default:
                score += 0;
                break;
        }
    }

    public int getLastClearedLines() {
        return lastClearedLines;
    }

    public void moveDown(Mino mino) {
        mino.addMinoY();
    }

    public void moveRight(Mino mino) {
        mino.addMinoX();
    }

    public void moveLeft(Mino mino) {
        if (!isCollison(mino, mino.getMinoX() - 1, mino.getMinoY(), mino.getMinoAngle())) {
            mino.decMinoX();
        }
    }

    public void rotation(Mino mino) {
        if (!isCollison(mino, mino.getMinoX(), mino.getMinoY(), (mino.getMinoAngle() + 1) % mino.getMinoAngleSize())) {
            mino.setMinoAngle((mino.getMinoAngle() + 1) % mino.getMinoAngleSize());
        }
    }
}
