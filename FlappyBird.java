/* 
Name: Rohit Yadav
Team Member: Prince Gupta
Class: CSCI 282
Professor: Prithvi Raj Singh
Date: 12/07/2025
Project Name: Flappy Bird Game
*/


import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    // Constants
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private static final int PIPE_WIDTH = 80;
    private static final int PIPE_GAP = 190;
    private static final int PIPE_DISTANCE = 260;
    private static final int TIMER_DELAY = 20;
    private static final int FLOOR_LIMIT = 550;

    // Game-over overlay box
    private static final int OVERLAY_WIDTH = 600;
    private static final int OVERLAY_HEIGHT = 320;
    private static final int OVERLAY_BOX_Y = (HEIGHT - OVERLAY_HEIGHT) / 2;

    // Game state
    private int ticks = 0;
    private int yMotion = 0;
    private int score = 0;
    private static int highScore = 0;
    private boolean newHighScoreThisRound = false;

    private boolean started = false;
    private boolean gameOver = false;
    private boolean introScreen = true;
    private boolean gameOverButtonsShown = false;

    private String gameOverReason = "";

    private Rectangle bird;
    private ArrayList<Rectangle> pipes;
    private Random rand;

    // Images
    private Image bgImg = new ImageIcon("flappybirdbg.png").getImage();
    private Image flappyImg = new ImageIcon("flappybird.png").getImage();
    private Image topPipeImg = new ImageIcon("toppipe.png").getImage();
    private Image bottomPipeImg = new ImageIcon("bottompipe.png").getImage();

    private Timer timer;

    // Buttons
    private JButton startBtn;
    private JButton exitBtn;
    private JButton retryBtn;

    // Constructor
    public FlappyBird() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        setLayout(null);

        rand = new Random();
        pipes = new ArrayList<>();
        bird = new Rectangle(350, 250, 40, 40);

        timer = new Timer(TIMER_DELAY, this);
        timer.start();

        showIntroButtons();
    }

    // Intro screen Buttons
    private void showIntroButtons() {
        removeAll();
        setLayout(null);

        int btnWidth = 200;
        int btnHeight = 40;
        int x = (WIDTH - btnWidth) / 2;
        int yStart = HEIGHT / 2 + 20;

        startBtn = new JButton("Start Game");
        startBtn.setBounds(x, yStart, btnWidth, btnHeight);
        startBtn.addActionListener(e -> {
            introScreen = false;
            started = true;
            resetGame();
            requestFocusInWindow();
            removeAll();
            revalidate();
            repaint();
        });

        exitBtn = new JButton("Exit");
        exitBtn.setBounds(x, yStart + 60, btnWidth, btnHeight);
        exitBtn.addActionListener(e -> System.exit(0));

        add(startBtn);
        add(exitBtn);

        revalidate();
        repaint();
    }

    // Game-over Buttons
    private void showGameOverButtons() {
        removeAll();
        setLayout(null);

        int btnWidth = 200;
        int btnHeight = 40;
        int x = (WIDTH - btnWidth) / 2;

        int firstY = OVERLAY_BOX_Y + OVERLAY_HEIGHT + 40;
        int secondY = firstY + 60;

        retryBtn = new JButton("Play Again");
        retryBtn.setBounds(x, firstY, btnWidth, btnHeight);
        retryBtn.addActionListener(e -> {
            gameOver = false;
            introScreen = false;
            started = true;
            resetGame();
            requestFocusInWindow();
            removeAll();
            gameOverButtonsShown = false;
            revalidate();
            repaint();
        });

        exitBtn = new JButton("Exit");
        exitBtn.setBounds(x, secondY, btnWidth, btnHeight);
        exitBtn.addActionListener(e -> System.exit(0));

        add(retryBtn);
        add(exitBtn);

        revalidate();
        repaint();
    }

    // Reset game
    private void resetGame() {
        bird = new Rectangle(350, 250, 40, 40);
        pipes.clear();
        yMotion = 0;
        score = 0;
        newHighScoreThisRound = false;
        gameOverReason = "";
        ticks = 0;

        addPipe(true);
        addPipe(true);
        addPipe(true);
        addPipe(true);
    }

    // Pipe creation
    private void addPipe(boolean start) {
        int minPipeHeight = 50;
        int maxPipeHeight = HEIGHT - PIPE_GAP - 150;
        if (maxPipeHeight <= minPipeHeight) {
            maxPipeHeight = minPipeHeight + 1;
        }

        int topHeight = minPipeHeight + rand.nextInt(maxPipeHeight - minPipeHeight);
        int x;

        if (start && pipes.isEmpty()) {
            x = WIDTH + 100;
        } else {
            Rectangle lastPipe = pipes.get(pipes.size() - 1);
            x = lastPipe.x + PIPE_DISTANCE;
        }

        Rectangle topPipe = new Rectangle(x, 0, PIPE_WIDTH, topHeight);
        int bottomHeight = HEIGHT - topHeight - PIPE_GAP;
        Rectangle bottomPipe = new Rectangle(x, topHeight + PIPE_GAP, PIPE_WIDTH, bottomHeight);

        pipes.add(topPipe);
        pipes.add(bottomPipe);
    }

    // Game loop
    @Override
    public void actionPerformed(ActionEvent e) {
        if (introScreen) {
            repaint();
            return;
        }

        int speed = 5;

        if (started && !gameOver) {
            for (Rectangle pipe : pipes) {
                pipe.x -= speed;
            }

            if (ticks % 2 == 0 && yMotion < 15) {
                yMotion += 2;
            }

            bird.y += yMotion;

            if (ticks % 90 == 0) {
                addPipe(false);
            }

            Iterator<Rectangle> it = pipes.iterator();
            while (it.hasNext()) {
                Rectangle pipe = it.next();
                if (pipe.x + pipe.width < 0) {
                    it.remove();
                }
            }

            for (Rectangle pipe : pipes) {
                if (pipe.intersects(bird)) {
                    gameOver = true;
                    started = false;
                    gameOverReason = "pipe";
                }

                if (pipe.y == 0 &&
                        bird.x > pipe.x + pipe.width &&
                        bird.x <= pipe.x + pipe.width + speed) {
                    score++;
                    if (score > highScore) {
                        highScore = score;
                        newHighScoreThisRound = true;
                    }
                }
            }

            if (bird.y > FLOOR_LIMIT) {
                gameOver = true;
                started = false;
                gameOverReason = "bottom";
            } else if (bird.y < 0) {
                gameOver = true;
                started = false;
                gameOverReason = "top";
            }
        }

        if (gameOver && !gameOverButtonsShown) {
            showGameOverButtons();
            gameOverButtonsShown = true;
        }

        ticks++;
        repaint();
    }

    // Jump logic
    private void jump() {
        if (gameOver) {
            gameOver = false;
            introScreen = false;
            started = true;
            gameOverButtonsShown = false;
            resetGame();
            removeAll();
            revalidate();
            repaint();
            return;
        }

        if (introScreen) {
            introScreen = false;
            started = true;
            resetGame();
            removeAll();
            revalidate();
        }

        if (!started) {
            started = true;
        }

        if (yMotion > 0) {
            yMotion = 0;
        }

        yMotion -= 10;
    }

    // Painting
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), null);

        if (introScreen) {
            drawIntroScreen(g);
            return;
        }

        g.drawImage(flappyImg, bird.x, bird.y, bird.width, bird.height, null);

        for (Rectangle pipe : pipes) {
            if (pipe.y == 0) {
                g.drawImage(topPipeImg, pipe.x, pipe.y, pipe.width, pipe.height, null);
            } else {
                g.drawImage(bottomPipeImg, pipe.x, pipe.y, pipe.width, pipe.height, null);
            }
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("Score: " + score, 20, 40);
        g.drawString("High Score: " + highScore, 20, 70);

        if (gameOver) {
            drawGameOverScreen(g);
        }
    }

    private void drawIntroScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Welcome to Flappy Bird Game!", 140, 200);

        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Press SPACE to start the game,", 230, 240);
        g.drawString("or use the Start / Exit buttons below.", 210, 270);
    }

    private void drawGameOverScreen(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        int boxX = (WIDTH - OVERLAY_WIDTH) / 2;
        int boxY = OVERLAY_BOX_Y;

        g2.setColor(new Color(255, 255, 255, 235));
        g2.fillRoundRect(boxX, boxY, OVERLAY_WIDTH, OVERLAY_HEIGHT, 30, 30);

        g2.setColor(Color.BLACK);

        g2.setFont(new Font("Arial", Font.BOLD, 40));
        String title = "Game Over!!!";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, boxX + (OVERLAY_WIDTH - titleWidth) / 2, boxY + 70);

        g2.setFont(new Font("Arial", Font.BOLD, 24));
        String mainLine;

        if ("pipe".equals(gameOverReason)) {
            mainLine = "Oops! You hit a pipe!";
        } else if ("top".equals(gameOverReason)) {
            mainLine = "You lost, bird hit the top of the window.";
        } else if ("bottom".equals(gameOverReason)) {
            mainLine = "You lost, bird hit the bottom of the window.";
        } else {
            mainLine = "You lost!";
        }

        g2.drawString(mainLine, boxX + 40, boxY + 110);

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        g2.drawString("Your Score: " + score, boxX + 40, boxY + 150);
        g2.drawString("High Score: " + highScore, boxX + 40, boxY + 185);

        if (newHighScoreThisRound && score > 0) {
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            g2.drawString("Congrats!! You made a New High Score!", boxX + 40, boxY + 220);
        }

        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Press SPACE to play again.", boxX + 40, boxY + 260);
    }

    // Keyboard input
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            jump();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    // Main method
    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird --By Rohit & Prince");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        FlappyBird game = new FlappyBird();
        frame.add(game);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
