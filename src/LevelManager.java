import java.util.ArrayList;
import java.util.List;

// LevelManager class to create and manage all levels
class LevelManager {
    private List<Level> beginnerLevels;
    private List<Level> intermediateLevels;
    private List<Level> advancedLevels;

    public LevelManager() {
        beginnerLevels = new ArrayList<>();
        intermediateLevels = new ArrayList<>();
        advancedLevels = new ArrayList<>();

        // Create levels
        initializeLevels();
    }

    private void initializeLevels() {
        // Create beginner levels (1-4)
        for (int i = 1; i <= 4; i++) {
            beginnerLevels.add(new BeginnerLevel(i));
        }

        // Create intermediate levels (5-7)
        for (int i = 5; i <= 7; i++) {
            intermediateLevels.add(new IntermediateLevel(i));
        }

        // Create advanced levels (8-10)
        for (int i = 8; i <= 10; i++) {
            advancedLevels.add(new AdvancedLevel(i));
        }
    }

    public List<Level> getBeginnerLevels() {
        return beginnerLevels;
    }

    public List<Level> getIntermediateLevels() {
        return intermediateLevels;
    }

    public List<Level> getAdvancedLevels() {
        return advancedLevels;
    }

    public List<Level> getAllLevels() {
        List<Level> allLevels = new ArrayList<>();
        allLevels.addAll(beginnerLevels);
        allLevels.addAll(intermediateLevels);
        allLevels.addAll(advancedLevels);
        return allLevels;
    }

    public Level getLevelByNumber(int number) {
        if (number >= 1 && number <= 10) {
            return getAllLevels().get(number - 1);
        }
        return null;
    }
}