import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

// Enhanced Enemy class with more visually appealing GUI
class Enemy extends Entity {
    private double speed;
    private long lastMoveTime;
    private int personality;
    private Color eyeColor;
    private Color accentColor;
    private float pulsePhase = 0;
    private float glowIntensity = 0;
    private Random random;
    private double animationCounter = 0;

    // Additional visual traits
    private boolean hasGlow;
    private boolean pulsating;
    private int patternType;

    public Enemy(int x, int y, double speed, Color color) {
        super(x, y, color);
        this.speed = speed;
        this.lastMoveTime = System.currentTimeMillis();
        this.personality = (int)(Math.random() * 3); // 0=chaser, 1=wanderer, 2=ambusher

        // Initialize random for animation
        this.random = new Random();

        // Create complementary colors for accents
        this.eyeColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());

        // Create accent color (slightly brighter version of main color)
        int r = Math.min(color.getRed() + 50, 255);
        int g = Math.min(color.getGreen() + 50, 255);
        int b = Math.min(color.getBlue() + 50, 255);
        this.accentColor = new Color(r, g, b);

        // Visual traits
        this.hasGlow = random.nextBoolean();
        this.pulsating = random.nextBoolean();
        this.patternType = random.nextInt(3);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void update(Player player, Maze maze) {
        // Update animations
        animationCounter += 0.05;
        pulsePhase += 0.1;
        glowIntensity = (float)(0.5 + 0.5 * Math.sin(pulsePhase));

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime < 1000 / speed) {
            return;
        }

        lastMoveTime = currentTime;

        // Different behaviors based on personality
        switch (personality) {
            case 0: // Chaser - direct pursuit
                chasePlayer(player, maze);
                break;
            case 1: // Wanderer - mostly random movement
                if (Math.random() < 0.2) { // 20% chance to chase
                    chasePlayer(player, maze);
                } else {
                    moveRandomly(maze);
                }
                break;
            case 2: // Ambusher - tries to predict player movement
                ambushPlayer(player, maze);
                break;
        }
    }

    private void chasePlayer(Player player, Maze maze) {
        // Simple AI: try to move toward the player
        int dx = 0, dy = 0;

        // Determine direction toward player
        if (player.getX() < x && !maze.isWall(x - 1, y)) dx = -1;
        else if (player.getX() > x && !maze.isWall(x + 1, y)) dx = 1;
        else if (player.getY() < y && !maze.isWall(x, y - 1)) dy = -1;
        else if (player.getY() > y && !maze.isWall(x, y + 1)) dy = 1;

        // If can't move toward player, try random move
        if (dx == 0 && dy == 0) {
            moveRandomly(maze);
        } else {
            x += dx;
            y += dy;
        }
    }

    private void ambushPlayer(Player player, Maze maze) {
        // Try to predict where the player is going and intercept
        int playerX = player.getX();
        int playerY = player.getY();

        // Calculate player direction (very simple prediction)
        int targetX = playerX + (playerX - x) / 2;
        int targetY = playerY + (playerY - y) / 2;

        // Move toward the predicted position
        int dx = 0, dy = 0;

        if (targetX < x && !maze.isWall(x - 1, y)) dx = -1;
        else if (targetX > x && !maze.isWall(x + 1, y)) dx = 1;
        else if (targetY < y && !maze.isWall(x, y - 1)) dy = -1;
        else if (targetY > y && !maze.isWall(x, y + 1)) dy = 1;

        if (dx == 0 && dy == 0) {
            moveRandomly(maze);
        } else {
            x += dx;
            y += dy;
        }
    }

    private void moveRandomly(Maze maze) {
        // Try up to 4 times to find a valid move
        for (int i = 0; i < 4; i++) {
            int direction = (int)(Math.random() * 4);
            int dx = 0, dy = 0;

            switch (direction) {
                case 0: dx = -1; break; // Left
                case 1: dx = 1; break;  // Right
                case 2: dy = -1; break; // Up
                case 3: dy = 1; break;  // Down
            }

            if (!maze.isWall(x + dx, y + dy)) {
                x += dx;
                y += dy;
                return;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        int cellSize = 30;
        int baseX = x * cellSize;
        int baseY = y * cellSize;

        // Save original graphics settings
        Composite originalComposite = g.getComposite();
        Stroke originalStroke = g.getStroke();
        RenderingHints originalHints = g.getRenderingHints();

        // Enable antialiasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw glow effect if enabled
        if (hasGlow) {
            drawGlowEffect(g, baseX, baseY, cellSize);
        }

        // Draw pulsating effect if enabled
        float alpha = pulsating ? 0.7f + 0.3f * (float)Math.sin(pulsePhase) : 1.0f;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Draw patterns based on patternType
        drawPattern(g, baseX, baseY, cellSize);

        // Different shapes based on personality with enhanced visuals
        switch (personality) {
            case 0: // Chaser - Evolved Square
                drawChaser(g, baseX, baseY, cellSize);
                break;
            case 1: // Wanderer - Evolved Diamond
                drawWanderer(g, baseX, baseY, cellSize);
                break;
            case 2: // Ambusher - Evolved Triangle
                drawAmbusher(g, baseX, baseY, cellSize);
                break;
        }

        // Restore original graphics settings
        g.setComposite(originalComposite);
        g.setStroke(originalStroke);
        g.setRenderingHints(originalHints);
    }

    private void drawGlowEffect(Graphics2D g, int baseX, int baseY, int cellSize) {
        int padding = 5;
        int glowSize = cellSize + 10;
        int glowX = baseX - 5;
        int glowY = baseY - 5;

        // Create gradient paint for glow
        RadialGradientPaint glow = new RadialGradientPaint(
                baseX + cellSize/2,
                baseY + cellSize/2,
                glowSize/2,
                new float[] {0.0f, 1.0f},
                new Color[] {
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 150),
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)
                }
        );

        g.setPaint(glow);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glowIntensity * 0.7f));
        g.fillOval(glowX, glowY, glowSize, glowSize);
    }

    private void drawPattern(Graphics2D g, int baseX, int baseY, int cellSize) {
        g.setColor(accentColor);

        switch (patternType) {
            case 0: // Spiral pattern
                g.setStroke(new BasicStroke(1.5f));
                for (int i = 0; i < 4; i++) {
                    double angle = animationCounter + i * Math.PI / 2;
                    int centerX = baseX + cellSize / 2;
                    int centerY = baseY + cellSize / 2;
                    int radius = cellSize / 6;
                    int x1 = centerX + (int)(radius * Math.cos(angle));
                    int y1 = centerY + (int)(radius * Math.sin(angle));
                    int x2 = centerX + (int)(radius * Math.cos(angle + Math.PI));
                    int y2 = centerY + (int)(radius * Math.sin(angle + Math.PI));
                    g.drawLine(x1, y1, x2, y2);
                }
                break;

            case 1: // Dots pattern
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if ((i + j) % 2 == 0) continue;
                        int dotX = baseX + i * cellSize / 3 + cellSize / 6;
                        int dotY = baseY + j * cellSize / 3 + cellSize / 6;
                        int dotSize = 3;
                        g.fillOval(dotX - dotSize/2, dotY - dotSize/2, dotSize, dotSize);
                    }
                }
                break;

            case 2: // Stripe pattern
                g.setStroke(new BasicStroke(1.0f));
                for (int i = 0; i < 3; i++) {
                    int startX = baseX + 5;
                    int startY = baseY + 5 + i * (cellSize - 10) / 2;
                    int endX = baseX + cellSize - 5;
                    int endY = startY;
                    g.drawLine(startX, startY, endX, endY);
                }
                break;
        }
    }

    private void drawChaser(Graphics2D g, int baseX, int baseY, int cellSize) {
        int padding = 5;
        int innerPadding = 3;

        // Main body
        g.setColor(color);
        RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
                baseX + padding,
                baseY + padding,
                cellSize - padding * 2,
                cellSize - padding * 2,
                8, 8
        );
        g.fill(roundedRect);

        // Accent border
        g.setColor(accentColor);
        g.setStroke(new BasicStroke(2.0f));
        g.draw(roundedRect);

        // Eyes
        g.setColor(eyeColor);
        int eyeSize = cellSize / 5;
        g.fillOval(baseX + cellSize/3 - eyeSize/2, baseY + cellSize/3 - eyeSize/2, eyeSize, eyeSize);
        g.fillOval(baseX + cellSize*2/3 - eyeSize/2, baseY + cellSize/3 - eyeSize/2, eyeSize, eyeSize);

        // Moving mouth
        int mouthWidth = cellSize / 3;
        int mouthHeight = cellSize / 6;
        int mouthY = baseY + cellSize * 2/3;
        int mouthX = baseX + cellSize/2 - mouthWidth/2;

        // Animate mouth
        double mouthPhase = Math.sin(animationCounter * 2) * mouthHeight / 3;
        QuadCurve2D mouth = new QuadCurve2D.Float(
                mouthX, mouthY,
                mouthX + mouthWidth/2, (float)(mouthY + mouthPhase),
                mouthX + mouthWidth, mouthY
        );
        g.setStroke(new BasicStroke(2.0f));
        g.draw(mouth);
    }

    private void drawWanderer(Graphics2D g, int baseX, int baseY, int cellSize) {
        int padding = 5;

        // Main body (diamond)
        int[] xPoints = {baseX + cellSize/2, baseX + cellSize - padding, baseX + cellSize/2, baseX + padding};
        int[] yPoints = {baseY + padding, baseY + cellSize/2, baseY + cellSize - padding, baseY + cellSize/2};

        // Animate diamond slightly
        double wobble = Math.sin(animationCounter) * 2;
        xPoints[0] += wobble;
        xPoints[2] -= wobble;
        yPoints[1] += wobble;
        yPoints[3] -= wobble;

        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, 4);

        // Accent border
        g.setColor(accentColor);
        g.setStroke(new BasicStroke(2.0f));
        g.drawPolygon(xPoints, yPoints, 4);

        // Eyes
        g.setColor(eyeColor);
        int eyeSize = cellSize / 6;
        g.fillOval(baseX + cellSize/3 - eyeSize/2, baseY + cellSize/3 - eyeSize/2, eyeSize, eyeSize);
        g.fillOval(baseX + cellSize*2/3 - eyeSize/2, baseY + cellSize/3 - eyeSize/2, eyeSize, eyeSize);

        // Antenna
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(baseX + cellSize/2, baseY + padding,
                baseX + cellSize/2, baseY + padding - cellSize/4);
        g.fillOval(baseX + cellSize/2 - 2, baseY + padding - cellSize/4 - 2, 4, 4);
    }

    private void drawAmbusher(Graphics2D g, int baseX, int baseY, int cellSize) {
        int padding = 5;

        // Main body (triangle)
        int[] xPoints = {
                baseX + cellSize/2,
                baseX + cellSize - padding,
                baseX + padding
        };
        int[] yPoints = {
                baseY + padding,
                baseY + cellSize - padding,
                baseY + cellSize - padding
        };

        // Animate triangle slightly
        double wobble = Math.sin(animationCounter) * 2;
        xPoints[0] += wobble;
        yPoints[0] -= wobble;

        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, 3);

        // Accent border
        g.setColor(accentColor);
        g.setStroke(new BasicStroke(2.0f));
        g.drawPolygon(xPoints, yPoints, 3);

        // Eyes
        g.setColor(eyeColor);
        int eyeSize = cellSize / 6;
        g.fillOval(baseX + cellSize/3 - eyeSize/2, baseY + cellSize/2 - eyeSize/2, eyeSize, eyeSize);
        g.fillOval(baseX + cellSize*2/3 - eyeSize/2, baseY + cellSize/2 - eyeSize/2, eyeSize, eyeSize);

        // Spikes
        int spikeLength = cellSize / 6;
        double spikeWobble = Math.sin(animationCounter * 2) * 2;

        g.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < 3; i++) {
            int dx = (int)(Math.cos(i * 2 * Math.PI / 3 + animationCounter) * spikeLength);
            int dy = (int)(Math.sin(i * 2 * Math.PI / 3 + animationCounter) * spikeLength);
            g.drawLine(baseX + cellSize/2, baseY + cellSize/2,
                    baseX + cellSize/2 + dx, baseY + cellSize/2 + dy);
        }
    }
}