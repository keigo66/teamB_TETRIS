package app_TETRIS;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GameThread extends Thread {
    private GameArea ga;
    private Mino mino;
    private Mino nextMino;
    private App app;
    private int difficultyLevel = 1;
    private int sleepTime = 1000;

    public GameThread(Mino mino, GameArea ga, Mino nextMino, App app) {
        this.mino = mino;
        this.ga = ga;
        this.nextMino = nextMino;
        this.app = app;
    }

    public GameThread(Mino mino, GameArea ga, App app) {
        this.mino = mino;
        this.ga = ga;
        this.nextMino = new Mino();
        this.app = app;
    }

    public void run() {
        while (true) {
            if (app.isPaused()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                continue;
            }

            if (!ga.isCollison(mino, mino.getMinoX(), mino.getMinoY() + 1, mino.getMinoAngle())) {
                ga.moveDown(mino);
            }

            if (ga.isCollison(mino)) {
                if (mino.getMinoY() <= 1) {
                    System.out.println("GameOver");
                    System.out.println(ga.getName() + "  Your score: " + ga.getScore());
                    app.gameOver(); // ゲームオーバー時にリーダーボードを表示
                    System.exit(0);
                }

                ga.bufferFieldAddMino(mino);
                ga.eraseLine();
                ga.initField();
                this.mino = nextMino;
                this.nextMino = new Mino();
                app.updateMino(this.mino);
                app.updateNextMino(this.nextMino);
            } else {
                ga.initField();
                ga.fieldAddMino(mino);
            }
            app.repaint();
            System.out.println("NextMino");
            ga.drawNextMino(nextMino);

            increaseDifficulty();

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void increaseDifficulty() {
        int score = ga.getScore();
        int newLevel = score / 1000 + 1;
        if (newLevel > difficultyLevel) {
            difficultyLevel = newLevel;
            sleepTime = Math.max(1000 - (difficultyLevel - 1) * 100, 100);
        }
    }
}