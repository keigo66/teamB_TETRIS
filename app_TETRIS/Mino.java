package app_TETRIS;

import java.util.Random;
import java.awt.Color;

public class Mino {
    private Random rand;
    private int minoSize = 4;
    private int[][][] minoTypes;
    private int x, y;
    private int minoType;
    private int minoAngle;
    private int minoAngleSize = 4;
    private Color color;

    private int[][][] mino_I = {
        { { 1, 0, 0, 0 }, { 1, 0, 0, 0 }, { 1, 0, 0, 0 }, { 1, 0, 0, 0 } },
        { { 1, 1, 1, 1 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
        { { 0, 0, 0, 1 }, { 0, 0, 0, 1 }, { 0, 0, 0, 1 }, { 0, 0, 0, 1 } },
        { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 1, 1, 1, 1 } }
    };

    private int[][][] mino_O = {
        { { 1, 1, 0, 0 }, { 1, 1, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
        { { 1, 1, 0, 0 }, { 1, 1, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
        { { 1, 1, 0, 0 }, { 1, 1, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
        { { 1, 1, 0, 0 }, { 1, 1, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } }
    };

    private int[][][] mino_S = {
        { { 0, 1, 1, 0 }, { 1, 1, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
        { { 0, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 0 } },
        { { 0, 1, 1, 0 }, { 1, 1, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
        { { 0, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 0 } }
    };

    private int[][][] mino_Z = {
        { { 1, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
        { { 0, 1, 0, 0 }, { 1, 1, 0, 0 }, { 1, 0, 0, 0 }, { 0, 0, 0, 0 } },
        { { 1, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
        { { 0, 1, 0, 0 }, { 1, 1, 0, 0 }, { 1, 0, 0, 0 }, { 0, 0, 0, 0 } }
    };

    private int[][][] mino_J = {
        { { 0, 1, 0, 0 }, { 0, 1, 0, 0 }, { 1, 1, 0, 0 }, { 0, 0, 0, 0 } },
        { { 1, 0, 0, 0 }, { 1, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
        { { 1, 1, 0, 0 }, { 1, 0, 0, 0 }, { 1, 0, 0, 0 }, { 0, 0, 0, 0 } },
        { { 1, 1, 1, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } }
    };

    private int[][][] mino_L = {
        { { 1, 0, 0, 0 }, { 1, 0, 0, 0 }, { 1, 1, 0, 0 }, { 0, 0, 0, 0 } },
        { { 0, 0, 1, 0 }, { 1, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
        { { 1, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 } },
        { { 1, 1, 1, 0 }, { 1, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } }
    };

    private int[][][] mino_T = {
        { { 0, 1, 0, 0 }, { 1, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
              { { 0, 1, 0, 0 }, { 1, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 } },
        { { 1, 1, 1, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
        { { 1, 0, 0, 0 }, { 1, 1, 0, 0 }, { 1, 0, 0, 0 }, { 0, 0, 0, 0 } }
    };

    public Mino() {
        this.rand = new Random();
        this.y = 0;
        this.x = 5;
        setMinoType();
        setMinoAngle();
        randSet();
        setColor();
    }

    public void initMino() {
        this.y = 0;
        this.x = 5;
        setMinoType();
        setMinoAngle();
        randSet();
        setColor();
    }

    private void setColor() {
        switch (getMinoType()) {
            case 1:
                this.color = new Color(166, 196, 228);
                break;
            case 2:
                this.color = new Color(250, 217, 120);
                break;
            case 3:
                this.color = new Color(157, 194, 132);
                break;
            case 4:
                this.color = new Color(210, 109, 106);
                break;
            case 5:
                this.color = new Color(0, 0, 139);
                break;
            case 6:
                this.color = new Color(247, 180, 118);
                break;
            case 7:
                this.color = new Color(138, 126, 190);
                break;
        }
    }

    public Color getColor() {
        return this.color;
    }

    public int[][][] getMino() {
        return minoTypes;
    }

    public int getMinoSize() {
        return this.minoSize;
    }

    public int getMinoAngleSize() {
        return this.minoAngleSize;
    }

    public int getMinoX() {
        return this.x;
    }

    public int getMinoY() {
        return this.y;
    }

    public void setMinoX(int x) {
        this.x = x;
    }

    public void setMinoY(int y) {
        this.y = y;
    }

    public void addMinoX() {
        this.x++;
    }

    public void addMinoY() {
        this.y++;
    }

    public void decMinoX() {
        this.x--;
    }

    public int getMinoAngle() {
        return this.minoAngle;
    }

    public int getMinoType() {
        return this.minoType;
    }

    public void setMinoType() {
        this.minoType = rand.nextInt(7) + 1;
    }

    public void setMinoAngle() {
        this.minoAngle = rand.nextInt(4);
    }

    public void setMinoAngle(int minoAngle) {
        this.minoAngle = minoAngle;
    }

    private void randSet() {
        switch (getMinoType()) {
            case 1:
                this.minoTypes = this.mino_I;
                break;
            case 2:
                this.minoTypes = this.mino_O;
                break;
            case 3:
                this.minoTypes = this.mino_S;
                break;
            case 4:
                this.minoTypes = this.mino_Z;
                break;
            case 5:
                this.minoTypes = this.mino_J;
                break;
            case 6:
                this.minoTypes = this.mino_L;
                break;
            case 7:
                this.minoTypes = this.mino_T;
                break;
        }
    }
}
