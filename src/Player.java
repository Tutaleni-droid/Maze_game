import java.awt.*;

// Player class
class Player extends Entity {
    public Player(int x, int y) {
        super(x, y, Color.BLUE);
    }

    public void move(int dx, int dy, Maze maze) {
        int newX = x + dx;
        int newY = y + dy;

        if (!maze.isWall(newX, newY)) {
            x = newX;
            y = newY;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        int cellSize = 30;
        g.setColor(color);
        g.fillOval(x * cellSize + 5, y * cellSize + 5, cellSize - 10, cellSize - 10);
    }
}