import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.*;
import javax.sound.sampled.Clip;

public class board extends JPanel {
    private Main mainFrame;
    private lane[] lanes;
    private level levels;
    private Timer gameTimer;
    private List<sun> suns;
    private Random random;
    private int playerSun = 100;
    private String selectedPlant = "none";
    private boolean isShovelActive = false;
    private int sunSpawnTimer = 0;
    private Image lawnImage;
    private Image SeedBankImage;
    private Image cardPeaImage;
    private Image cardSunImage;
    private Image cardCherryImage;
    private Image cardWallnutImage;
    private Image cardSnowPeaImage;
    private Image shovelIcon;
    private boolean isLose = false;
    private boolean isWin = false;
    private boolean isPaused = false;
    private Clip IngameMusic;

    public static final int GRID_START_X = 250;
    public static final int GRID_START_Y = 130;
    public static final int CELL_WIDTH = 80;    
    public static final int CELL_HEIGHT = 100;

    public static final int PLANT_WIDTH = 70;
    public static final int PLANT_HEIGHT = 70;
    public static final int ZOMBIE_WIDTH = 100;
    public static final int ZOMBIE_HEIGHT = 120;

    public board(int levelselected, Main Frame) {
        this.IngameMusic = Sound.playLoop("assets/sound/ingame sound.wav");
        this.mainFrame = Frame;
        lanes = new lane[5];
        for (int i = 0; i < 5; i++) {
            lanes[i] = new lane(i);
        }

        suns = new ArrayList<>();
        random = new Random();
        levels = new level(lanes, levelselected);

        try {
            this.lawnImage = new ImageIcon("assets/lawn.png").getImage();
            this.SeedBankImage = new ImageIcon("assets/SeedBank.png").getImage();
            this.cardPeaImage = new ImageIcon("assets/Peashooter.png").getImage();
            this.cardSunImage = new ImageIcon("assets/Sunflower.png").getImage();
            this.cardCherryImage = new ImageIcon("assets/CherryBomb.png").getImage();
            this.cardWallnutImage = new ImageIcon("assets/Wall-nut.png").getImage();
            this.cardSnowPeaImage = new ImageIcon("assets/SnowPea.png").getImage();
            this.shovelIcon = new ImageIcon("assets/Shovel.jpg").getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }                                                                                                   

        gameTimer = new Timer(16, e -> {
            updateGame();
            repaint();
        });
        gameTimer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseInteraction(e.getX(), e.getY());
                repaint();
            }
        });
    }

    public void setPlayersun(int sun) {
        if (sun >= 0) {
        this.playerSun = sun;
        }
    }

    public int getPlayersun() {
        return this.playerSun; 
    }

    public void returntomenu() {
        if (this.mainFrame != null) {
            this.mainFrame.showMenu();
        }
        mainFrame.showMenu();
    }

    public void resumeTimer() {
    if (gameTimer != null && !gameTimer.isRunning()) {
        gameTimer.start();
    if (IngameMusic != null && !IngameMusic.isRunning()) {
        IngameMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }
    }
}

    public void pauseTimer() {
    if (gameTimer != null && gameTimer.isRunning()) {
        gameTimer.stop();
    }
}
    public boolean isGameOver() {
        return isWin || isLose;
    }
    private void updateGame() {
        if (isLose || isWin || isPaused) return;
        boolean anyZombieLeft = false;
        sunSpawnTimer++;
        if (sunSpawnTimer >= 600) {
            int startX = 100 + random.nextInt(700);
            int targetY = 150 + random.nextInt(350);
            suns.add(new sun(startX, -50, targetY));
            sunSpawnTimer = 0;
        }

        for (int i = 0; i < suns.size(); i++) {
            sun s = suns.get(i);
            s.update();
            if (s.Isremoved()) {
                suns.remove(i);
                i--;
            }
        }

        for (lane l : lanes) {
            l.update();
            for (zombie z : l.getzombies()) {
            if (z.alive() && z.x < 0) { 
                isLose = true;
            }
            if (z.alive()) anyZombieLeft = true;
        }
            suns.addAll(l.getsuns());
            collision.checklawnmowercollision(l);
            collision.checkcollision(l.getBullets(), l.getzombies());
            collision.checkzombieeat(l);
        }

        levels.update();
        if (levels.isAllZombiesSpawned() && !anyZombieLeft) {
        isWin = true;
    }
        if (isWin && IngameMusic != null && IngameMusic.isRunning()) {
            IngameMusic.stop();
            Sound.playSound("assets/sound/win.wav");
        }
            if (isLose && IngameMusic != null && IngameMusic.isRunning()) {
                IngameMusic.stop();
                Sound.playSound("assets/sound/lose.wav");
            }
    }

    public void returnToMenu() {
        if (IngameMusic != null && IngameMusic.isRunning()) {
            IngameMusic.stop();
        }
        for (lane l : lanes) {
            for(zombie z : l.getzombies()) {
                z.seteating(false);
            }
        }
        if (!isWin && !isLose) {
        saveGame();
        } else {
            File saveFile = new File("savegame.dat");
            if (saveFile.exists()) {
                saveFile.delete();
        }
    }
        if (this.mainFrame != null) {
            this.mainFrame.showMenu();
        }
    }

    private void handleMouseInteraction(int mouseX, int mouseY) {
        if (isWin || isLose) {
            int btnWidth = 300;
            int btnHeight = 60;
            int btnX = (getWidth() - btnWidth) / 2;
            int btnY = getHeight() / 2 + 30;
            if (mouseX >= btnX && mouseX <= btnX + btnWidth && mouseY >= btnY && mouseY <= btnY + btnHeight) {
                returnToMenu(); 
            }
            return; 
        }
         if (mouseX >= 900 && mouseX <= 980 && mouseY >= 10 && mouseY <= 50) {
        isPaused = !isPaused;
        if (isPaused){
            if (gameTimer != null) gameTimer.stop();
            for (lane l : lanes) {
                for (zombie z : l.getzombies()) {
                    z.seteating(false);
                }
            }
        }
            else {
                if (gameTimer != null) gameTimer.start(); 
            }
            return;
        }
        if(isPaused) {
            if (mouseX >= 400 && mouseX <= 600 && mouseY >= 450 && mouseY <= 510) {
                isPaused = false; 
                if (gameTimer != null) {
                    gameTimer.start(); 
                }   
            }
            else if (mouseX >= 350 && mouseX <= 650 && mouseY >= 530 && mouseY <= 590) {
                isPaused = false;
                returnToMenu(); 
            }
            return; 
        }
    
    if (isPaused) return;
        if (mouseY >= 0 && mouseY <= 95) {
            if (mouseX >= 75 && mouseX <= 135) {
                selectedPlant = "peashooter";
                isShovelActive = false;
            } else if (mouseX >= 135 && mouseX <= 190) {
                selectedPlant = "sunflower";
                isShovelActive = false;
            } else if (mouseX >= 190 && mouseX <= 245) {
                selectedPlant = "cherrybomb";
                isShovelActive = false;
            } else if (mouseX >= 245 && mouseX <= 300) {
                selectedPlant = "wallnut";
                isShovelActive = false;
            } else if (mouseX >= 300 && mouseX <= 365) {
                selectedPlant = "snowpea";
                isShovelActive = false;
            }
            else if (mouseX >= 600 && mouseX <= 680) {
                isShovelActive = true;
                selectedPlant = "none";
            return;
            }
            return; 
        }

        if (mouseX >= 600 && mouseX <= 670 && mouseY >= 10 && mouseY <= 80) {
            isShovelActive = true;  
            selectedPlant = "none";
            return;
        }

        for (sun s : suns) {
            if (!s.Isremoved() && s.isclicked(mouseX, mouseY)) {
                s.collect();
                Sound.playSound("assets/sound/sunpickup.wav");
                playerSun += 25;
                return;
            }
        }
        int laneIndex = (mouseY - GRID_START_Y) / CELL_HEIGHT;
        int colIndex = (mouseX - GRID_START_X) / CELL_WIDTH;
        if (laneIndex >= 0 && laneIndex < 5 && colIndex >= 0 && colIndex < 9) {
            lane targetLane = lanes[laneIndex];
            int snapX = GRID_START_X + (colIndex * CELL_WIDTH);

            if (isShovelActive) {
                targetLane.removePlantAt(colIndex);
                isShovelActive = false;
                this.repaint();
            } else if (!selectedPlant.equals("none")) {
                planting(targetLane, snapX, targetLane.getY());
                selectedPlant = "none";
            }
        }
        else { if(!selectedPlant.equals("none" ) && isShovelActive ) {
            selectedPlant = "none";
            isShovelActive = false; 
        }
    }
    }

    private void planting(lane targetLane, int x, int y) {
        int colIndex = (x - GRID_START_X) / CELL_WIDTH;
        plants p = null;
        switch (selectedPlant) {
            case "peashooter":
                p = new peashooter(x, targetLane.getY(), targetLane);
                break;
            case "sunflower":
                p = new sunflower(x, targetLane.getY(), targetLane);
                break;
            case "cherrybomb":
                p = new cherrybomb(x, targetLane.getY(), targetLane);
                break;
            case "wallnut":
                p = new wallnut(x, targetLane.getY(), targetLane);
                break;
            case "snowpea":
                p = new snowpea(x, targetLane.getY(), targetLane);
                break;
            default:
                return;
        }

        if (p != null && playerSun >= p.cost) { 
            if(targetLane.addPlant(p, colIndex)) {
                playerSun -= p.cost;
                selectedPlant = "none";
            }  
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (lawnImage != null) {
            g.drawImage(lawnImage, 0, 0, getWidth(), getHeight(), this);
        }

        for (lane l : lanes) {
            l.render(g);
        }

        for (sun s : suns) {
            s.render(g);
        }
        if (isLose) {
        g.setColor(new Color(0, 0, 0, 150)); 
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.RED);
        Font loseFont = new Font("Arial", Font.BOLD, 50);
        g.setFont(loseFont);
        String msg = "THE ZOMBIES ATE YOUR BRAINS!";
        FontMetrics metrics = g.getFontMetrics(loseFont);
        int x = (getWidth() - metrics.stringWidth(msg)) / 2;
        int y = getHeight() / 2;
        
        g.drawString(msg, x, y);
        drawReturnButton(g);
    }

    if (isWin) {
        g.setColor(new Color(255, 255, 255, 100));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.drawString("LEVEL COMPLETE!", 250, getHeight()/2);
        drawReturnButton(g);
    }
        renderUI(g);
    }

    private void drawReturnButton(Graphics g) {
        int btnWidth = 300;
        int btnHeight = 60;
        int btnX = (getWidth() - btnWidth) / 2;
        int btnY = getHeight() / 2 + 30;
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(btnX, btnY, btnWidth, btnHeight);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("RETURN TO MENU", btnX + 35, btnY + 40); 
    }

    private void renderUI(Graphics g) {
        if (SeedBankImage != null) {
            g.drawImage(SeedBankImage, 10, 10, 450, 80, null);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("" + playerSun, 35, 82);

        if (cardPeaImage != null) g.drawImage(cardPeaImage, 85, 15, 50, 70, null);
        if (cardSunImage != null) g.drawImage(cardSunImage, 140, 15, 50, 70, null);
        if (cardCherryImage != null) g.drawImage(cardCherryImage, 195, 15, 50, 70, null);
        if (cardWallnutImage != null) g.drawImage(cardWallnutImage, 250, 15, 50, 70, null);
        if (cardSnowPeaImage != null) g.drawImage(cardSnowPeaImage, 305, 15, 50, 70, null);

        if (shovelIcon != null) g.drawImage(shovelIcon, 600, 10, 70, 70, null);

        g.setColor(new Color(0, 0, 0, 100));
        switch (selectedPlant) {
            case "peashooter":
                g.fillRect(85, 15, 50, 70);
                break;
            case "sunflower":
                g.fillRect(140, 15, 50, 70);
                break;
            case "cherrybomb":
                g.fillRect(195, 15, 50, 70);
                break;
            case "wallnut":
                g.fillRect(250, 15, 50, 70);
                break;
            case "snowpea":
                g.fillRect(305, 15, 50, 70);
                break;
        }
        
        if (isShovelActive) {
            g.fillRect(600, 10, 70, 70);
        }
    g.setColor(Color.YELLOW);
    g.fillRect(900, 10, 80, 40);
    g.setColor(Color.BLACK);
    g.setFont(new Font("Arial", Font.BOLD, 18));
    g.drawString("PAUSE", 910, 35);

    if (isPaused) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("GAME PAUSED", 300, getHeight() / 2);

        g.setColor(Color.GREEN);
        g.fillRect(400, 450, 200, 60);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("CONTINUE", 420, 490);

        int returnBtnX = 350; 
        int returnBtnY = 530; 
        int returnBtnWidth = 300;
        int returnBtnHeight = 60;

        g.setColor(Color.RED);
        g.fillRect(returnBtnX, returnBtnY, returnBtnWidth, returnBtnHeight);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("RETURN TO MENU", returnBtnX + 30, returnBtnY + 40);
    }
}
    public void saveGame() {
        if (isWin || isLose) {
            File saveFile = new File("savegame.dat");
            if (saveFile.exists()) {
                saveFile.delete();
            }
            return; 
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("savegame.dat"))) {
            Gamesave data = new Gamesave();
            data.currentLevel = this.levels.getLevel(); 
            data.playerSun = this.playerSun;
            for (int i = 0; i < lanes.length; i++) {
                lane currentLane = lanes[i];
                for (plants p : currentLane.getplant()) {
                    Gamesave.PlantData pd = new Gamesave.PlantData(p.getType(), p.getX(), p.getY(), p.getHealth(), i);
                    data.plantsSaved.add(pd);
                }

                for (zombie z : currentLane.getzombies()) {
                    Gamesave.ZombieData zd = new Gamesave.ZombieData(z.getType(), z.getRealX(), z.getY(), z.getHealth(), i);
                    data.zombiesSaved.add(zd);
                }
            }
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }

    public static Gamesave loadGame() {
        File file = new File("savegame.dat");
        if (!file.exists()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Gamesave) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadStateFromSave(Gamesave savedData) {
        this.playerSun = savedData.playerSun;
        
        if (this.levels != null) {
            this.levels.setLevel(savedData.currentLevel);
        }

        for (Gamesave.PlantData pd : savedData.plantsSaved) {
            plants p = null;
            
            switch (pd.type.toLowerCase()) {
                case "peashooter":
                    p = new peashooter(pd.x, pd.y, lanes[pd.laneIndex]);
                    break;
                case "sunflower":
                    p = new sunflower(pd.x, pd.y, lanes[pd.laneIndex]);
                    break;
                case "cherrybomb":
                    p = new cherrybomb(pd.x, pd.y, lanes[pd.laneIndex]);
                    break;
                case "wallnut":
                    p = new wallnut(pd.x, pd.y, lanes[pd.laneIndex]);
                    break;
                case "snowpea":
                    p = new snowpea(pd.x, pd.y, lanes[pd.laneIndex]);
                    break;
                default:
                    
            }

            if (p != null) {
                p.setHealth(pd.health);
                int colIndex = (pd.x - GRID_START_X) / CELL_WIDTH;
                lanes[pd.laneIndex].addPlant(p, colIndex);
            }
        }

        for (Gamesave.ZombieData zd : savedData.zombiesSaved) {
            zombie z = null;
            
            switch (zd.type.toLowerCase()) {
                case "basic":
                    z = new basicZombie((int)zd.realX, zd.y);
                    break;
                case "cone": 
                    z = new coneZombie((int)zd.realX, zd.y);
                    break;
                case "bucket":
                    z = new bucketZombie((int)zd.realX, zd.y);
                    break;
                case "flag":
                    z = new flagZombie((int)zd.realX, zd.y);
                    break;
                default:
                    
            }

            if (z != null) {
                z.setHealth(zd.health);
                lanes[zd.laneIndex].addzombie(z);
            }
        }
    }
}