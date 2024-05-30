package app_TETRIS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;

public class App extends JFrame {
    private GameArea ga;
    private Mino mino;
    private Mino nextMino;
    private String playerName;
    private boolean isPaused = false;
    private ArrayList<LeaderboardEntry> leaderboard;
    private static final String LEADERBOARD_FILE = "leaderboard.dat";

    private static final String[] ENCOURAGEMENT_MESSAGES = {
        "Great job today!", "Great job!", 
        "Good job today!", "You're awesome!", "Keep it up for tomorrow!",
    };

    public App(String playerName) {
        this.mino = new Mino();
        this.ga = new GameArea();
        this.nextMino = new Mino();
        this.playerName = playerName;
        this.leaderboard = loadLeaderboard();
        new GameThread(mino, ga, nextMino, this).start();
        initControls();

        setTitle("Tetris");
        setSize(770, 640);
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

    public String getPlayerName() {
        return playerName;
    }

    public String getRandomEncouragementMessage() {
        Random random = new Random();
        int index = random.nextInt(ENCOURAGEMENT_MESSAGES.length);
        return ENCOURAGEMENT_MESSAGES[index];
    }

    private void initControls() {
        InputMap im = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getRootPane().getActionMap();

        im.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        am.put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ga.isCollison(mino, mino.getMinoX() + 1, mino.getMinoY(), mino.getMinoAngle())) {
                    ga.moveRight(mino);
                    repaint();
                }
            }
        });

        im.put(KeyStroke.getKeyStroke("LEFT"), "left");
        am.put("left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ga.isCollison(mino, mino.getMinoX() - 1, mino.getMinoY(), mino.getMinoAngle())) {
                    ga.moveLeft(mino);
                    repaint();
                }
            }
        });

        im.put(KeyStroke.getKeyStroke("DOWN"), "down");
        am.put("down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ga.isCollison(mino, mino.getMinoX(), mino.getMinoY() + 1, mino.getMinoAngle())) {
                    ga.moveDown(mino);
                    repaint();
                }
            }
        });

        im.put(KeyStroke.getKeyStroke("UP"), "up");
        am.put("up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ga.isCollison(mino, mino.getMinoX(), mino.getMinoY(), (mino.getMinoAngle() + 1) % mino.getMinoAngleSize())) {
                    ga.rotation(mino);
                    repaint();
                }
            }
        });

        im.put(KeyStroke.getKeyStroke("P"), "pause");
        am.put("pause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        java.awt.Font multiLangFont = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 18);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.BLACK);
        g2d.setFont(multiLangFont);
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
        drawClearLineMessage(g2d, ga.getLastClearedLines());
        drawTop3Leaderboard(g2d);
        g2d.setColor(Color.BLACK);
        g2d.setFont(multiLangFont.deriveFont(java.awt.Font.BOLD));
        g2d.drawString("TCS_B group", getWidth() - 300, getHeight() - 30);
        g2d.setFont(multiLangFont);
        int leaderboardSize = Math.min(leaderboard.size(), 3);
        int offsetY = 300 + (leaderboardSize * 20) + 40;
        g2d.drawString("Stop button: P", 400, offsetY);
        g2d.drawString("Tetrimino rotation: \u2191", 400, offsetY + 20);
        g2d.drawString("Move the tetrimino downward: \u2193", 400, offsetY + 40);
        g2d.drawString("Move the tetrimino one square to the left: \u2190", 400, offsetY + 60);
        g2d.drawString("Move the tetrimino one square to the right: \u2192", 400, offsetY + 80);
    }

    private void drawNextMino(Graphics2D g2d, Mino nextMino) {
        int offsetX = 460;
        int offsetY = 135;

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
               message =playerName +","+ "Stay Rocks!";
               color = Color.RED;
                break;
            case 2:
                 message =playerName +","+ "Who cares?";
                 color = Color.WHITE;
                break;
            case 3:
                message =playerName +","+"Stay gold!";
                color = new Color(255, 215, 0); 
                break;
            default:
                return;
        }

        java.awt.Font originalFont = g2d.getFont();
        g2d.setColor(color);
        g2d.setFont(originalFont.deriveFont(20f));
        //g2d.drawString(message, getWidth() / 2 -400, getHeight() / 2);
        g2d.drawString(message, getWidth()-630 , getHeight() / 2);
        g2d.setFont(originalFont);
    }

    private void drawTop3Leaderboard(Graphics2D g2d) {
        int offsetX = 400;
        int offsetY = 300;

        java.awt.Font originalFont = g2d.getFont();

        g2d.setColor(Color.BLACK);
        g2d.drawString("Top 3:", offsetX, offsetY - 20);

        int leaderboardSize = Math.min(leaderboard.size(), 3);
        for (int i = 0; i < leaderboardSize; i++) {
            LeaderboardEntry entry = leaderboard.get(i);
            String entryText = String.format("%d. Player:%s       Score:%d", i + 1, entry.getPlayerName(), entry.getScore());
            g2d.drawString(entryText, offsetX, offsetY + (i * 20));
        }

        g2d.setFont(originalFont);
    }

    public void gameOver() {
        leaderboard.add(new LeaderboardEntry(playerName, ga.getScore()));
        Collections.sort(leaderboard, new Comparator<LeaderboardEntry>() {
            public int compare(LeaderboardEntry e1, LeaderboardEntry e2) {
                return Integer.compare(e2.getScore(), e1.getScore());
            }
        });

        saveLeaderboard();
        displayLeaderboard();
        
        JOptionPane.showMessageDialog(this, "Game Over\nPlayer: " + playerName + "\nScore: " + ga.getScore() + "\n" + getRandomEncouragementMessage(), "Game Over", JOptionPane.INFORMATION_MESSAGE, null);
    }

    private void displayLeaderboard() {
        System.out.println("Leaderboard:");
        for (int i = 0; i < Math.min(leaderboard.size(), 10); i++) {
            LeaderboardEntry entry = leaderboard.get(i);
            System.out.printf("%d. %s - %d%n", i + 1, entry.getPlayerName(), entry.getScore());
        }
    }

    private void saveLeaderboard() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE))) {
            oos.writeObject(leaderboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<LeaderboardEntry> loadLeaderboard() {
        ArrayList<LeaderboardEntry> leaderboard = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(LEADERBOARD_FILE))) {
            leaderboard = (ArrayList<LeaderboardEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
           
        }
        return leaderboard;
    }
}
