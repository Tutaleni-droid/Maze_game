import java.awt.*;

// Base abstract Level class (abstraction principle)
abstract class Level {
    private int number;
    private int timeLimit;
    private String name;
    private String description;

    // Constructor
    public Level(int number, String name, String description) {
        this.number = number;
        this.name = name;
        this.description = description;
        this.timeLimit = calculateTimeLimit();
    }

    // Encapsulation: private fields with getters/setters
    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    protected void setTimeLimit(int timeLimit) {
        this.timeLimit = Math.max(20, timeLimit); // Ensure at least 20 seconds
    }

    // Abstract methods that derived classes must implement (polymorphism)
    public abstract int getEnemyCount();
    public abstract double getEnemySpeed();
    public abstract Color getBackgroundColor();
    public abstract void generateMaze(Maze maze);
    public abstract String getDifficultyDescription();

    // Template method - same for all levels but uses polymorphic methods
    protected int calculateTimeLimit() {
        return 60 - (number * 3);
    }
}

// Beginner level implementation
class BeginnerLevel extends Level {
    public BeginnerLevel(int number) {
        super(number, "Beginner", "Easy maze with few enemies");
    }

    @Override
    public int getEnemyCount() {
        return getNumber() - 1;
    }

    @Override
    public double getEnemySpeed() {
        return 0.5 + (getNumber() * 0.15);
    }

    @Override
    public Color getBackgroundColor() {
        return new Color(200, 230, 255); // Light blue
    }

    @Override
    public void generateMaze(Maze maze) {
        maze.setPathDensity(40 + (getNumber() * 2));
        maze.generate();
    }

    @Override
    public String getDifficultyDescription() {
        return "Easy - Perfect for new players";
    }
}

// Intermediate level implementation
class IntermediateLevel extends Level {
    public IntermediateLevel(int number) {
        super(number, "Intermediate", "Challenging maze with more enemies");
        // Override the base time limit
        setTimeLimit(55 - (number * 3));
    }

    @Override
    public int getEnemyCount() {
        return getNumber(); // One more enemy than beginner levels
    }

    @Override
    public double getEnemySpeed() {
        return 0.6 + (getNumber() * 0.2);
    }

    @Override
    public Color getBackgroundColor() {
        return new Color(255, 240, 200); // Light orange
    }

    @Override
    public void generateMaze(Maze maze) {
        maze.setPathDensity(35 + (getNumber() * 3));
        maze.generate();
    }

    @Override
    public String getDifficultyDescription() {
        return "Medium - For experienced players";
    }
}

// Advanced level implementation
class AdvancedLevel extends Level {
    public AdvancedLevel(int number) {
        super(number, "Advanced", "Complex maze with aggressive enemies");
        setTimeLimit(50 - (number * 3));
    }

    @Override
    public int getEnemyCount() {
        return getNumber() + 1; // More enemies than intermediate levels
    }

    @Override
    public double getEnemySpeed() {
        return 0.7 + (getNumber() * 0.25);
    }

    @Override
    public Color getBackgroundColor() {
        return new Color(255, 200, 200); // Light red
    }

    @Override
    public void generateMaze(Maze maze) {
        maze.setPathDensity(30 + (getNumber() * 4));
        maze.generate();
    }

    @Override
    public String getDifficultyDescription() {
        return "Hard - For maze masters only!";
    }
}