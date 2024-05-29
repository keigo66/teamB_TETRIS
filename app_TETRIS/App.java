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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.*;

public class App extends JFrame {
    private GameArea ga;
    private Mino mino;
    private Mino nextMino;
    private String playerName;
    private boolean isPaused = false;
    private ArrayList<LeaderboardEntry> leaderboard;
    private static final String LEADERBOARD_FILE = "leaderboard.dat";

    public App(String playerName) {
        this.mino = new Mino();
        this.ga = new GameArea();
        this.nextMino = new Mino();
        this.playerName = playerName;
        this.leaderboard = loadLeaderboard();
        new GameThread(mino, ga, nextMino, this).start();
        initControls();

        setTitle("Tetris");
        setSize(500, 640);
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
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.BLACK);
        g2d.drawString("Score: " + ga.getScore(), (ga.getFieldWidth() + 1) * 30, 50);
        g2d.drawString("Line: " + ga.getCount(), (ga.getFieldWidth() + 1) * 30, 70);
        g2d.drawString("Player: " + playerName, (ga.getFieldWidth() + 1) * 30, 90);

        for (int y = 0; y < ga.getFieldHight(); y++) {
            for (int x = 0; x < ga.getFieldWidth(); x++) {
                if (ga.getField()[y][x] == 1) {
                    g2d.setColor(ga.getFieldColors()[y][x]);
                    g2d.fillRect(x * 30, y * 30, 30, 30);
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawRect(x * 30, y * 30, 30, 30);
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(x * 30, y * 30, 30, 30);
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawRect(x * 30, y * 30, 30, 30);
                }
            }
        }

        for (int y = 0; y < mino.getMinoSize(); y++) {
            for (int x = 0; x < mino.getMinoSize(); x++) {
                if (mino.getMino()[mino.getMinoAngle()][y][x] == 1) {
                    g2d.setColor(mino.getColor());
                    g2d.fillRect((mino.getMinoX() + x) * 30, (mino.getMinoY() + y) * 30, 30, 30);
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawRect((mino.getMinoX() + x) * 30, (mino.getMinoY() + y) * 30, 30, 30);
                }
            }
        }

        drawNextMino(g2d, nextMino);

        // ライン消去のメッセージを描画
        drawClearLineMessage(g2d, ga.getLastClearedLines());

        g2d.setColor(Color.BLACK);
        g2d.setFont(g2d.getFont().deriveFont(java.awt.Font.BOLD));
        g2d.drawString("TCS_B group", getWidth() - 100, getHeight() - 30);
    }

    private void drawNextMino(Graphics2D g2d, Mino nextMino) {
        int offsetX = 390;
        int offsetY = 150;

        g2d.setColor(Color.BLACK);
        g2d.drawString("Next Mino:", offsetX, offsetY - 20);

        for (int y = 0; y < nextMino.getMinoSize(); y++) {
            for (int x = 0; x < nextMino.getMinoSize(); x++) {
                if (nextMino.getMino()[0][y][x] == 1) {
                    g2d.setColor(nextMino.getColor());
                    g2d.fillRect(offsetX + x * 30, offsetY + y * 30, 30, 30);
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawRect(offsetX + x * 30, offsetY + y * 30, 30, 30);
                }
            }
        }
    }

    private void drawClearLineMessage(Graphics2D g2d, int linesCleared) {
        String message = "";
        Color color = Color.BLACK;
        switch (linesCleared) {
            case 1:
                message = "Nice";
                color = Color.WHITE;
                break;
            case 2:
                message = "Very Good";
                color = Color.RED;
                break;
            case 3:
                message = "Perfect";
                color = new Color(255, 215, 0); // ゴールド
                break;
            default:
                return;
        }
        g2d.setColor(color);
        g2d.setFont(g2d.getFont().deriveFont(30f));
                g2d.drawString(message, getWidth() / 2 - 50, getHeight() / 2);
    }

    // ゲーム終了時の処理
    public void gameOver() {
        // リーダーボードにスコアを追加
        leaderboard.add(new LeaderboardEntry(playerName, ga.getScore()));
        // スコアの降順にソート
        Collections.sort(leaderboard, new Comparator<LeaderboardEntry>() {
            public int compare(LeaderboardEntry e1, LeaderboardEntry e2) {
                return Integer.compare(e2.getScore(), e1.getScore());
            }
        });

        // リーダーボードをファイルに保存
        saveLeaderboard();

        // リーダーボードの表示
        displayLeaderboard();
    }

    // リーダーボードの表示
    private void displayLeaderboard() {
        System.out.println("Leaderboard:");
        for (int i = 0; i < Math.min(leaderboard.size(), 10); i++) {
            LeaderboardEntry entry = leaderboard.get(i);
            System.out.printf("%d. %s - %d%n", i + 1, entry.getPlayerName(), entry.getScore());
        }
    }

    // リーダーボードをファイルに保存
    private void saveLeaderboard() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE))) {
            oos.writeObject(leaderboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // リーダーボードをファイルから読み込み
    @SuppressWarnings("unchecked")
    private ArrayList<LeaderboardEntry> loadLeaderboard() {
        ArrayList<LeaderboardEntry> leaderboard = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(LEADERBOARD_FILE))) {
            leaderboard = (ArrayList<LeaderboardEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // ファイルが存在しない場合、または読み込めない場合は空のリストを返す
        }
        return leaderboard;
    }
}