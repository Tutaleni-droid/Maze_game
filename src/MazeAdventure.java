import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MazeAdventure extends JFrame {
    private final LevelManager levelManager;
    private JTabbedPane tabbedPane;

    public MazeAdventure() {
        super("Maze Adventure Quest");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        levelManager = new LevelManager();
        initializeUI();

        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(25, 25, 112),
                        0, getHeight(), new Color(70, 130, 180)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        setContentPane(backgroundPanel);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel gameTitle = new JLabel("MAZE ADVENTURE QUEST", JLabel.CENTER);
        gameTitle.setFont(new Font("Arial", Font.BOLD, 28));
        gameTitle.setForeground(Color.red);


        titlePanel.add(gameTitle, BorderLayout.CENTER);

        getContentPane().add(titlePanel, BorderLayout.NORTH);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);


        setVisible(true);
    }

    private void initializeUI() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.setBackground(new Color(0, 0, 0, 80));
        tabbedPane.setForeground(Color.WHITE);

        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        createBeginnerTab();
        createIntermediateTab();
        createAdvancedTab();
        tabbedPane.setForeground(new Color(13, 141, 230));

    }

    private void createBeginnerTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Beginner Levels", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel levelsPanel = createLevelSelectionPanel(levelManager.getBeginnerLevels());

        panel.add(levelsPanel, BorderLayout.CENTER);

        JLabel descriptionLabel = new JLabel("Perfect for new players - start your maze adventure here!");
        panel.add(descriptionLabel, BorderLayout.SOUTH);

        tabbedPane.addTab("Beginner", new ImageIcon(), panel, "Beginner");

        tabbedPane.setBackgroundAt(0, new Color(218, 100, 185));
    }

    private void createIntermediateTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Intermediate Levels", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel levelsPanel = createLevelSelectionPanel(levelManager.getIntermediateLevels());
        panel.add(levelsPanel, BorderLayout.CENTER);

        JLabel descriptionLabel = new JLabel("For experienced players - test your skills with more complex mazes!");
        panel.add(descriptionLabel, BorderLayout.SOUTH);

        tabbedPane.addTab("Intermediate", new ImageIcon(), panel, "Intermediate");

        tabbedPane.setBackgroundAt(1, new Color(218, 176, 63));
    }

    private void createAdvancedTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Advanced Levels", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel levelsPanel = createLevelSelectionPanel(levelManager.getAdvancedLevels());
        panel.add(levelsPanel, BorderLayout.CENTER);

        JLabel descriptionLabel = new JLabel("For maze masters only - challenging mazes with aggressive enemies!");
        panel.add(descriptionLabel, BorderLayout.SOUTH);

        tabbedPane.addTab("Advanced", new ImageIcon(), panel, "Advanced");

        tabbedPane.setBackgroundAt(2, new Color(255, 200, 200));
    }

    private JPanel createLevelSelectionPanel(java.util.List<Level> levels) {
        JPanel panel = new JPanel(new GridLayout(0, 3, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        for (Level level : levels) {
            JPanel levelCard = createLevelCard(level);
            panel.add(levelCard);
        }

        return panel;
    }

    private JPanel createLevelCard(Level level) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        Color baseColor = level.getBackgroundColor();
        Color cardColor = new Color(
                Math.max(baseColor.getRed() - 20, 0),
                Math.max(baseColor.getGreen() - 20, 0),
                Math.max(baseColor.getBlue() - 20, 0)
        );
        card.setBackground(cardColor);

        JLabel nameLabel = new JLabel("Level " + level.getNumber() + ": " + level.getName(), JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBackground(new Color(255, 200, 200));
        card.add(nameLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setOpaque(false);

        JLabel timeLabel = new JLabel("Time: " + level.getTimeLimit() + " seconds");
        JLabel enemiesLabel = new JLabel("Enemies: " + level.getEnemyCount());
        JLabel difficultyLabel = new JLabel(level.getDifficultyDescription());

        timeLabel.setForeground(Color.WHITE);
        enemiesLabel.setForeground(Color.WHITE);
        difficultyLabel.setForeground(Color.WHITE);

        infoPanel.add(timeLabel);
        infoPanel.add(enemiesLabel);
        infoPanel.add(difficultyLabel);
        card.add(infoPanel, BorderLayout.CENTER);

        JButton playButton = new JButton("Play Level");
        playButton.setBackground(new Color(26, 28, 113)); // Lime Green
        playButton.setForeground(Color.black);
        playButton.setFont(new Font("Arial", Font.BOLD, 14));
        playButton.setFocusPainted(false);
        playButton.setBorder(BorderFactory.createRaisedBevelBorder());

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(level);
            }
        });
        card.add(playButton, BorderLayout.SOUTH);

        return card;
    }

    private void startGame(Level level) {
        setVisible(false);
        JFrame gameFrame = new JFrame("Maze Adventure - Level " + level.getNumber());
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                setVisible(true);
            }
        });

        GamePanel gamePanel = new GamePanel(level);
        gameFrame.add(gamePanel);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start with authentication instead of directly launching the game
        SwingUtilities.invokeLater(() -> new AuthenticationFrame());
    }
}