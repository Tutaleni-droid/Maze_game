import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class GamePanel extends JPanel implements ActionListener {
    // Constants
    private static final int CELL_SIZE = 30;
    private static final int ROWS = 15;
    private static final int COLS = 20;
    private static final int PANEL_WIDTH = COLS * CELL_SIZE;
    private static final int PANEL_HEIGHT = ROWS * CELL_SIZE;
    private static final int TIMER_DELAY = 1000; // 1 second for timer updates
    private static final int GAME_SPEED = 16; // ~60 FPS

    // Game objects
    private Maze maze;
    private Player player;
    private ArrayList<Enemy> enemies;
    private Level currentLevel;

    // Game state
    private int timeRemaining;
    private boolean gameOver = false;
    private boolean levelComplete = false;
    private Timer gameTimer;
    private Timer countdownTimer;

    public GamePanel(Level level) {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        // Set current level
        this.currentLevel = level;

        // Initialize game objects
        initializeGame();

        // Set up timers
        gameTimer = new Timer(GAME_SPEED, e -> updateGame());
        countdownTimer = new Timer(TIMER_DELAY, this);

        // Add key listener for player control
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver || levelComplete) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        if (gameOver) {
                            restartLevel();
                        } else if (levelComplete) {
                            Window window = SwingUtilities.getWindowAncestor(GamePanel.this);
                            if (window != null) {
                                window.dispose();
                            }
                        }
                    }
                    return;
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        player.move(0, -1, maze);
                        break;
                    case KeyEvent.VK_DOWN:
                        player.move(0, 1, maze);
                        break;
                    case KeyEvent.VK_LEFT:
                        player.move(-1, 0, maze);
                        break;
                    case KeyEvent.VK_RIGHT:
                        player.move(1, 0, maze);
                        break;
                }
            }
        });

        // Start the game
        startGame();
    }

    private void initializeGame() {
        timeRemaining = currentLevel.getTimeLimit();

        // Create maze and apply level-specific generation
        maze = new Maze(ROWS, COLS);
        maze.setSeed(currentLevel.getNumber() * 1000); // Consistent maze generation per level
        currentLevel.generateMaze(maze);

        player = new Player(1, 1);
        enemies = new ArrayList<>();

        // Create enemies based on the level
        int enemyCount = currentLevel.getEnemyCount();
        double enemySpeed = currentLevel.getEnemySpeed();

        if (enemyCount > 0) {
            createEnemies(enemyCount, enemySpeed);
        }

        gameOver = false;
        levelComplete = false;
    }

    private void createEnemies(int count, double speed) {
        java.util.Random random = new java.util.Random(currentLevel.getNumber() * 2000);

        for (int i = 0; i < count; i++) {
            int enemyX, enemyY;
            boolean validPosition;

            // Ensure enemies don't spawn too close to player or other enemies
            do {
                validPosition = true;
                enemyX = random.nextInt(COLS - 4) + 2;
                enemyY = random.nextInt(ROWS - 4) + 2;

                // Check if position is valid (not a wall and not too close to player)
                if (maze.isWall(enemyX, enemyY) ||
                        (Math.abs(enemyX - player.getX()) < 5 && Math.abs(enemyY - player.getY()) < 5)) {
                    validPosition = false;
                    continue;
                }

                // Check if too close to other enemies
                for (Enemy otherEnemy : enemies) {
                    if (Math.abs(enemyX - otherEnemy.getX()) < 3 &&
                            Math.abs(enemyY - otherEnemy.getY()) < 3) {
                        validPosition = false;
                        break;
                    }
                }
            } while (!validPosition);

            Color enemyColor = new Color(
                    200 + random.nextInt(55),  // More red
                    random.nextInt(100),       // Less green
                    random.nextInt(100)        // Less blue
            );

            enemies.add(new Enemy(enemyX, enemyY, speed, enemyColor));
        }
    }

    private void startGame() {
        gameTimer.start();
        countdownTimer.start();
        requestFocus();
    }

    private void updateGame() {
        if (!gameOver && !levelComplete) {
            // Update enemies
            for (Enemy enemy : enemies) {
                enemy.update(player, maze);
            }

            // Check collisions
            checkCollisions();
        }
        repaint();
    }

    private void checkCollisions() {
        // Check if player reached the exit
        if (player.getX() == COLS - 2 && player.getY() == ROWS - 2) {
            levelComplete = true;
            gameTimer.stop();
            countdownTimer.stop();
        }

        // Check if player touched any enemy
        for (Enemy enemy : enemies) {
            if (player.getX() == enemy.getX() && player.getY() == enemy.getY()) {
                gameOver = true;
                gameTimer.stop();
                countdownTimer.stop();
                break;
            }
        }
    }

    private void restartLevel() {
        initializeGame();
        gameTimer.start();
        countdownTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Countdown timer
        if (timeRemaining > 0) {
            timeRemaining--;
        } else {
            gameOver = true;
            gameTimer.stop();
            countdownTimer.stop();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw maze with level-specific background
        maze.draw(g2d, currentLevel.getBackgroundColor());

        // Draw player
        player.draw(g2d);

        // Draw enemies
        for (Enemy enemy : enemies) {
            enemy.draw(g2d);
        }

        // Draw HUD
        drawHUD(g2d);

        // Draw game over message
        if (gameOver) {
            drawGameOver(g2d);
        }

        // Draw level complete message
        if (levelComplete) {
            drawLevelComplete(g2d);
        }
    }

    private void drawHUD(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Level: " + currentLevel.getNumber() + " - " + currentLevel.getName(), 10, 20);
        g.drawString("Time: " + timeRemaining, PANEL_WIDTH - 100, 20);
        g.drawString("Enemies: " + enemies.size(), 10, 40);

        // Draw mini-map or instructions as needed
        if (currentLevel.getNumber() == 1) {
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString("Use arrow keys to move", PANEL_WIDTH / 2 - 70, 20);
            g.drawString("Reach the green exit", PANEL_WIDTH / 2 - 70, 40);
        }
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String message = "Game Over!";
        int messageWidth = g.getFontMetrics().stringWidth(message);
        g.drawString(message, (PANEL_WIDTH - messageWidth) / 2, PANEL_HEIGHT / 2 - 20);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String instruction = "Press SPACE to try again";
        int instructionWidth = g.getFontMetrics().stringWidth(instruction);
        g.drawString(instruction, (PANEL_WIDTH - instructionWidth) / 2, PANEL_HEIGHT / 2 + 20);
    }

    private void drawLevelComplete(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String message = "Level " + currentLevel.getNumber() + " Complete!";
        int messageWidth = g.getFontMetrics().stringWidth(message);
        g.drawString(message, (PANEL_WIDTH - messageWidth) / 2, PANEL_HEIGHT / 2 - 20);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String instruction = "Press SPACE to return to menu";
        int instructionWidth = g.getFontMetrics().stringWidth(instruction);
        g.drawString(instruction, (PANEL_WIDTH - instructionWidth) / 2, PANEL_HEIGHT / 2 + 20);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        String message2 = "Congratulations on completing this level!";
        int message2Width = g.getFontMetrics().stringWidth(message2);
        g.drawString(message2, (PANEL_WIDTH - message2Width) / 2, PANEL_HEIGHT / 2 + 60);
    }
}