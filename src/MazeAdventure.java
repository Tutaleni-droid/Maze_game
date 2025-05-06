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
        setVisible(true);
    }

    private void initializeUI() {
        tabbedPane = new JTabbedPane();
        createBeginnerTab();
        createIntermediateTab();
        createAdvancedTab();
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
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
        tabbedPane.setBackgroundAt(0, new Color(200, 230, 255));
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
        tabbedPane.setBackgroundAt(1, new Color(255, 240, 200));
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
        card.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        card.setBackground(level.getBackgroundColor());

        JLabel nameLabel = new JLabel("Level " + level.getNumber() + ": " + level.getName(), JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(nameLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setOpaque(false);
        infoPanel.add(new JLabel("Time: " + level.getTimeLimit() + " seconds"));
        infoPanel.add(new JLabel("Enemies: " + level.getEnemyCount()));
        infoPanel.add(new JLabel(level.getDifficultyDescription()));
        card.add(infoPanel, BorderLayout.CENTER);

        JButton playButton = new JButton("Play Level");
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