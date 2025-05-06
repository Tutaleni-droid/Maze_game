import java.awt.*;
import java.util.Random;

// Maze class
class Maze {
    private int rows, cols;
    private int[][] grid;
    private int pathDensity;
    private Random random;

    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new int[rows][cols];
        this.pathDensity = 50; // Default path density
        this.random = new Random();
    }

    public void setPathDensity(int pathDensity) {
        this.pathDensity = pathDensity;
    }

    public void setSeed(long seed) {
        this.random = new Random(seed);
    }

    public void generate() {
        // Initialize all walls
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = 1; // 1 = wall
            }
        }

        // Create basic maze structure
        for (int i = 1; i < rows - 1; i += 2) {
            for (int j = 1; j < cols - 1; j += 2) {
                grid[i][j] = 0; // 0 = path
            }
        }

        // Create paths based on path density
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < cols - 1; j++) {
                if (random.nextInt(100) < pathDensity) {
                    grid[i][j] = 0;
                }
            }
        }

        // Ensure start and end are clear
        grid[1][1] = 0; // Start
        grid[rows - 2][cols - 2] = 0; // End

        // Make sure there's a path from start to end
        ensurePath();
    }

    private void ensurePath() {
        // Ensure path from start to end
        int x = 1, y = 1;
        int endX = cols - 2, endY = rows - 2;

        while (x < endX || y < endY) {
            if (x < endX && random.nextInt(2) == 0) {
                x++;
                grid[y][x] = 0;
            } else if (y < endY) {
                y++;
                grid[y][x] = 0;
            } else if (x < endX) {
                x++;
                grid[y][x] = 0;
            }
        }
    }

    public boolean isWall(int x, int y) {
        if (x < 0 || x >= cols || y < 0 || y >= rows) {
            return true;
        }
        return grid[y][x] == 1;
    }

    public void draw(Graphics2D g, Color backgroundColor) {
        int cellSize = 30;

        // Draw background
        g.setColor(backgroundColor);
        g.fillRect(0, 0, cols * cellSize, rows * cellSize);

        // Draw walls
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                }
            }
        }

        // Mark the exit
        g.setColor(Color.GREEN);
        g.fillRect((cols - 2) * cellSize, (rows - 2) * cellSize, cellSize, cellSize);
    }
}