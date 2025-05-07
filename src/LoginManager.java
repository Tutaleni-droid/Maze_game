import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.io.*;

public class LoginManager {
    private HashMap<String, String> users; // username -> password
    private static final String USER_FILE = "users.dat";
    private boolean isLoggedIn = false;
    private String currentUser = "";

    public LoginManager() {
        users = new HashMap<>();
        loadUsers();
    }

    // Load existing users from file
    @SuppressWarnings("unchecked")
    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            users = (HashMap<String, String>) ois.readObject();
            System.out.println("Loaded " + users.size() + " users");
        } catch (FileNotFoundException e) {
            System.out.println("No existing user file found. Creating new user database.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    // Save users to file
    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(users);
            System.out.println("Saved " + users.size() + " users");
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    // Register a new user
    public boolean registerUser(String username, String password) {
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }

        if (users.containsKey(username)) {
            return false; // User already exists
        }

        users.put(username, password);
        saveUsers();
        return true;
    }

    // Authenticate user
    public boolean loginUser(String username, String password) {
        if (users.containsKey(username) && users.get(username).equals(password)) {
            isLoggedIn = true;
            currentUser = username;
            return true;
        }
        return false;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        isLoggedIn = false;
        currentUser = "";
    }
}

class AuthenticationFrame extends JFrame {
    private LoginManager loginManager;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public AuthenticationFrame() {
        super("Maze Adventure - Authentication");
        loginManager = new LoginManager();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        setContentPane(new JPanel() {
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
        });
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false);

        createLoginPanel();
        createSignupPanel();

        add(mainPanel);
        cardLayout.show(mainPanel, "login");

        setVisible(true);
    }

    private void createLoginPanel() {
        JPanel loginPanel = new JPanel(new BorderLayout(10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        loginPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("Login to Maze Adventure", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        loginPanel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setOpaque(false);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameField = new JTextField();
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordField = new JPasswordField();

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(50, 205, 50));
        loginButton.setForeground(Color.black);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> attemptLogin());

        JButton registerButton = new JButton("Need an account? Sign up");
        registerButton.setBackground(new Color(81, 180, 70));
        registerButton.setForeground(Color.black);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "signup"));

        formPanel.add(loginButton);
        formPanel.add(registerButton);

        loginPanel.add(formPanel, BorderLayout.CENTER);

        // Status label
        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setForeground(Color.RED);
        loginPanel.add(statusLabel, BorderLayout.SOUTH);

        mainPanel.add(loginPanel, "login");
    }

    private void createSignupPanel() {
        JPanel signupPanel = new JPanel(new BorderLayout(10, 10));
        signupPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        signupPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("Sign Up for Maze Adventure", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        signupPanel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setOpaque(false);

        JLabel usernameLabel = new JLabel("Choose Username:");
        usernameLabel.setForeground(Color.WHITE);
        JTextField newUsernameField = new JTextField();
        
        JLabel passwordLabel = new JLabel("Choose Password:");
        passwordLabel.setForeground(Color.WHITE);
        JPasswordField newPasswordField = new JPasswordField();
        
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setForeground(Color.WHITE);
        JPasswordField confirmPasswordField = new JPasswordField();

        formPanel.add(usernameLabel);
        formPanel.add(newUsernameField);
        formPanel.add(passwordLabel);
        formPanel.add(newPasswordField);
        formPanel.add(confirmLabel);
        formPanel.add(confirmPasswordField);

        // Register button
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(50, 205, 50));
        registerButton.setForeground(Color.black);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> {
            String username = newUsernameField.getText();
            String password = new String(newPasswordField.getPassword());
            String confirm = new String(confirmPasswordField.getPassword());

            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (loginManager.registerUser(username, password)) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                newUsernameField.setText("");
                newPasswordField.setText("");
                confirmPasswordField.setText("");
                cardLayout.show(mainPanel, "login");
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists or invalid input!", "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton backButton = new JButton("Back to Login");
        backButton.setBackground(new Color(50, 205, 50));
        backButton.setForeground(Color.black);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        formPanel.add(registerButton);
        formPanel.add(backButton);

        signupPanel.add(formPanel, BorderLayout.CENTER);

        JLabel signupStatusLabel = new JLabel(" ", JLabel.CENTER);
        signupStatusLabel.setForeground(Color.RED);
        signupPanel.add(signupStatusLabel, BorderLayout.SOUTH);

        mainPanel.add(signupPanel, "signup");
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (loginManager.loginUser(username, password)) {
            statusLabel.setText("Login successful!");
            statusLabel.setForeground(new Color(0, 150, 0));

            // Launch the game
            SwingUtilities.invokeLater(() -> {
                dispose(); // Close login window
                new MazeAdventure();
            });
        } else {
            statusLabel.setText("Invalid username or password");
            statusLabel.setForeground(Color.RED);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new AuthenticationFrame());
    }
}