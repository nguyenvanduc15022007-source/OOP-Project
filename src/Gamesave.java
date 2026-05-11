import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Gamesave implements Serializable {
    public int playerSun;
    public int currentLevel;
    public List<PlantData> plantsSaved;
    public List<ZombieData> zombiesSaved;
    public List<LawnmowerData> mowersSaved;

    public Gamesave() {
       plantsSaved = new ArrayList<>();
       zombiesSaved = new ArrayList<>();
       mowersSaved = new ArrayList<>();
    }

public static class PlantData implements Serializable {
        public String type; 
        public int x;
        public int y;
        public int health;
        public int laneIndex; 

        public PlantData(String type, int x, int y, int health, int laneIndex) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.health = health;
            this.laneIndex = laneIndex;
        }
    }

    public static class ZombieData implements Serializable {
        public String type; 
        public double realX; 
        public int y;
        public int health;
        public int laneIndex;

        public ZombieData(String type, double realX, int y, int health, int laneIndex) {
            this.type = type;
            this.realX = realX;
            this.y = y;
            this.health = health;
            this.laneIndex = laneIndex;
        }
    }

    public static class LawnmowerData implements Serializable {
        public int x;
        public int y;
        public boolean isMoving;
        public boolean isDestroyed;
        public int laneIndex;

        public LawnmowerData(int x, int y, boolean isMoving, boolean isDestroyed, int laneIndex) {
            this.x = x;
            this.y = y;
            this.isMoving = isMoving;
            this.isDestroyed = isDestroyed;
            this.laneIndex = laneIndex;
        }
    }
}
