package app_TETRIS;

import javax.swing.JFrame;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.util.Scanner;

public class App extends JFrame {
    private GameArea ga;
    private Mino mino;
    private Mino nextMino;
    private String playerName;
    private boolean isPaused = false; // ?í‚èÛ?

    public App(String playerName) {
        this.mino = new Mino();
        this.ga = new GameArea();
        this.nextMino = new Mino();
        this.playerName = playerName;  // ï€ë∂äﬂâ∆ñºèÃ
        new GameThread(mino, ga, nextMino, this).start();
        initControls();

        setTitle("Tetris");
        setSize(500, 640); // ˙ùâ¡?ìxà»?é¶â∫àÍò¢É~Ém
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Tetris");
        System.out.print("Please enter your name:");

        Scanner sc = new Scanner(System.in);
        String name = sc.next();

        int l = name.length();
        if (0 < l && l <= 16) {
            System.out.println("Welcome " + name + "!");
        } else {
            name = "Guest";
            System.out.println("Guest");
        }

        System.out.println("Press Enter to start!!");
        while ((System.in.read()) != '\n');

        String finalName = name;
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new App(finalName).setVisible(true);
            }
        });
        sc.close();
    }

    public void updateMino(Mino mino) {
        this.mino = mino;
        initControls();
    }

    public void updateNextMino(Mino nextMino) {
        this.nextMino = nextMino;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void togglePause() {
        isPaused = !isPaused;
    }

    private void initControls() {
        InputMap im = this.getRootPane().getInputMap();
        ActionMap am = this.getRootPane().getActionMap();

        im.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        am.put("right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!ga.isCollison(mino, mino.getMinoX() + 1, mino.getMinoY(), mino.getMinoAngle())) {
                    ga.moveRight(mino);
                    repaint();
                }
            }
        });

        im.put(KeyStroke.getKeyStroke("LEFT"), "left");
        am.put("left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!ga.isCollison(mino, mino.getMinoX() - 1, mino.getMinoY(), mino.getMinoAngle())) {
                    ga.moveLeft(mino);
                    repaint();
                }
            }
        });

        im.put(KeyStroke.getKeyStroke("DOWN"), "down");
        am.put("down", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!ga.isCollison(mino, mino.getMinoX(), mino.getMinoY() + 1, mino.getMinoAngle())) {
                    ga.moveDown(mino);
                    repaint();
                }
            }
        });

        im.put(KeyStroke.getKeyStroke("UP"), "up");
        am.put("up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!ga.isCollison(mino, mino.getMinoX(), mino.getMinoY(), (mino.getMinoAngle() + 1) % mino.getMinoAngleSize())) {
                    ga.rotation(mino);
                    repaint();
                }
            }
        });

        // ìYâ¡?í‚?
        im.put(KeyStroke.getKeyStroke("P"), "pause");
        am.put("pause", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g; // è´ Graphics ?è€??? Graphics2D ?è€

        // ?êßîíêFîwåi
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // ?êßï™êî
        g2d.setColor(Color.BLACK);
        g2d.drawString("Score: " + ga.getScore(), (ga.getFieldWidth() + 1) * 30, 50);

        // ?êßäﬂâ∆ñºèÃ
        g2d.drawString("Player: " + playerName, (ga.getFieldWidth() + 1) * 30, 70);

        // ?êßü‡?ãÊàÊ
        for (int y = 0; y < ga.getFieldHight(); y++) {
            for (int x = 0; x < ga.getFieldWidth(); x++) {
                if (ga.getField()[y][x] == 1) {
                    g2d.setColor(ga.getFieldColors()[y][x]); // égópë∂?ìI?êF
                    g2d.fillRect(x * 30, y * 30, 30, 30);
                    g2d.setColor(Color.DARK_GRAY); // ?íu?ûy?êF?¸KêF
                    g2d.setStroke(new BasicStroke(3)); // ?íu?ûy?ìx
                    g2d.drawRect(x * 30, y * 30, 30, 30); // ?êß?ûy
                } else {
                    g2d.setColor(Color.BLACK); // ?íu?îíêFîwåi
                    g2d.fillRect(x * 30, y * 30, 30, 30);
                    g2d.setColor(Color.DARK_GRAY); // ?íu?¸KêF?ûy
                    g2d.setStroke(new BasicStroke(1)); // ?íuîwåiäiéqìI?ûy?ìx
                    g2d.drawRect(x * 30, y * 30, 30, 30); // ?êß?ûy
                }
            }
        }

        // ?êßìñëOÉ~Ém
        for (int y = 0; y < mino.getMinoSize(); y++) {
            for (int x = 0; x < mino.getMinoSize(); x++) {
                if (mino.getMino()[mino.getMinoAngle()][y][x] == 1) {
                                       g2d.setColor(mino.getColor());
                    g2d.fillRect((mino.getMinoX() + x) * 30, (mino.getMinoY() + y) * 30, 30, 30);
                    g2d.setColor(Color.BLACK); // ?íu?ûy?êF?¸KêF
                    g2d.setStroke(new BasicStroke(3)); // ?íu?ûy?ìx
                    g2d.drawRect((mino.getMinoX() + x) * 30, (mino.getMinoY() + y) * 30, 30, 30); // ?êß?ûy
                }
            }
        }

        // ?êßâ∫àÍò¢É~Ém
        drawNextMino(g2d, nextMino);

        // ?êßÅgTCS_B groupÅhâ¡¸Kéö
        g2d.setColor(Color.BLACK);
        g2d.setFont(g2d.getFont().deriveFont(java.awt.Font.BOLD));
        g2d.drawString("TCS_B group", getWidth() - 100, getHeight() - 30);
    }

    private void drawNextMino(Graphics2D g2d, Mino nextMino) {
        int offsetX = 390; // ?êÆ?ò¢?à»å¸âEà⁄?
        int offsetY = 150; // ?êÆ?ò¢?à»å¸â∫à⁄?

        g2d.setColor(Color.BLACK);
        g2d.drawString("Next Mino:", offsetX, offsetY - 20); // ??à íu

        for (int y = 0; y < nextMino.getMinoSize(); y++) {
            for (int x = 0; x < nextMino.getMinoSize(); x++) {
                if (nextMino.getMino()[0][y][x] == 1) {
                    g2d.setColor(nextMino.getColor());
                    g2d.fillRect(offsetX + x * 30, offsetY + y * 30, 30, 30);
                    g2d.setColor(Color.BLACK); // ?íu?ûy?êF?¸KêF
                    g2d.setStroke(new BasicStroke(3)); // ?íu?ûy?ìx
                    g2d.drawRect(offsetX + x * 30, offsetY + y * 30, 30, 30); // ?êß?ûy
                }
            }
        }
    }
}
