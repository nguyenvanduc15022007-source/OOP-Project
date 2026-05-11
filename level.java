import java.util.Random;

public class level {
    private lane[] lanes;
    private int level;
    private int spawnTimer = 0;
    private int spawnInterval;
    private int zombiesSpawned = 0;
    private int maxZombies;
    private Random random;
    private int prespawnTimer = 0;
    private final int preparetime = 1200; 
    private int wavesSpawned = 0;
    private boolean isWavePending = false;
    private int waveDelayTimer = 0;

    public boolean isAllZombiesSpawned() {
    return zombiesSpawned >= maxZombies && !isWavePending;
}
    public level(lane[] lanes, int level) {
        this.lanes = lanes;
        this.level = level;
        this.random = new Random();
        
        if (this.level == 1) {
            this.maxZombies = 12;
            this.spawnInterval = 500;
        } else if (this.level == 2) {
            this.maxZombies = 20;
            this.spawnInterval = 475;
        } else if (this.level == 3) {
            this.maxZombies = 30;
            this.spawnInterval = 475;
        } else if (this.level == 4) {
            this.maxZombies = 40;
            this.spawnInterval = 450;
        } else if (this.level == 5) {
            this.maxZombies = 60;
            this.spawnInterval = 425;
        }
    }

    public int getLevel() {
        return level;
    }

    public int setLevel(int level) {
        this.level = level;
        return level;
    }
    
    public void update() {
        if (prespawnTimer < preparetime) {
            prespawnTimer++;
            return;
        }

        if (isWavePending) {
            waveDelayTimer++;
            if (waveDelayTimer >= 300) {
                triggerHugeWave();
                isWavePending = false;
                waveDelayTimer = 0;
                wavesSpawned++;
                this.spawnTimer = -900;
            }
            return;
        }

        if (zombiesSpawned < maxZombies) {
            spawnTimer++;
            
            if (spawnTimer >= spawnInterval) {
                spawnSingleZombie();
                zombiesSpawned++;
                spawnTimer = 0;

                if (level == 4 && wavesSpawned == 0 && zombiesSpawned == 20) {
                    spawnFlagZombie();
                    isWavePending = true;
                } else if (level == 5) {
                    if ((wavesSpawned == 0 && zombiesSpawned == 20) || (wavesSpawned == 1 && zombiesSpawned == 45)) {
                        spawnFlagZombie();
                        isWavePending = true;
                    }
                }
            }
        }
    }

    private void spawnSingleZombie() {
    int laneIndex = random.nextInt(5);
    lane targetLane = lanes[laneIndex];
    int type = determineZombieType();
    
    zombie z;
    if (type == 1) z = new coneZombie(1000, targetLane.getY());
    else if (type == 2) z = new bucketZombie(1000, targetLane.getY());
    else z = new basicZombie(1000, targetLane.getY());
    
    targetLane.addzombie(z);
}

    private void spawnFlagZombie() {
    int laneIndex = random.nextInt(5);
    lane targetLane = lanes[laneIndex];
    targetLane.addzombie(new flagZombie(1000, targetLane.getY()));
    this.zombiesSpawned++;
}

    private void triggerHugeWave() {
        int waveSize = (level == 4) ? 12 : 18;
        for (int i = 0; i < waveSize; i++) {
            int laneIndex = random.nextInt(5);
            lane targetLane = lanes[laneIndex];
            int zombieType = determineZombieType();
            int offsetX = 1000 + random.nextInt(300);
            
           zombie z;
            if (zombieType == 1) {
                z = new coneZombie(offsetX, targetLane.getY());
            } else if (zombieType == 2) {
                z = new bucketZombie(offsetX, targetLane.getY());
            } else {
                z = new basicZombie(offsetX, targetLane.getY());
            }
            
            targetLane.addzombie(z);
        }
        this.zombiesSpawned += waveSize;
    }

    private int determineZombieType() {
        int rand = random.nextInt(100);
        
        if (level == 1) return 0;
        if (level == 2) return (rand < 15) ? 1 : 0;
        if (level == 3) {
            if (rand < 25) return 1;
            if (rand < 35) return 2;
            return 0;
        }
        if (level == 4) {
            if (rand < 35) return 1;
            if (rand < 55) return 2;
            return 0;
        }
        if (level == 5) {
            if (rand < 40) return 1;
            if (rand < 80) return 2;
            return 0;
        }
        return 0;
    }
}