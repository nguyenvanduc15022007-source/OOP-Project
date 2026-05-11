import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.image.ImageObserver;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

enum damagetype {
    normal,fire,ice
}
abstract class zombie {
    protected static boolean isCommonLoaded=false;
    protected int attackdamage;
    protected int cooldown=0;
    protected int damage;
    protected int health;
    protected double speed;
    protected int x,y;
    protected String type;
    protected double realx;
    protected boolean alive=true;   
    protected boolean eat=false;
    protected boolean remove=false;
    protected boolean chilled = false;
    protected long chilluntil=0;
    protected Image eating;
    protected Image walking;
    protected static Image dyingfire;
    protected int deathTimer=0;
    protected Image currentRenderImage;
    protected Clip eatingSound;
    private static long lastGroanTime = 0;

    public zombie(int health, double speed,int attackdamage, int x, int y, boolean playgroan) {
        if(playgroan) {
        long currentTime = System.currentTimeMillis(); 
            if (currentTime - lastGroanTime > 2000){
                Sound.playSound("assets/sound/zombiegroan.wav");
                lastGroanTime = currentTime;
             }
            }
        try {
                File eatingSoundFile = new File("assets/sound/zombie eating.wav");
            if (eatingSoundFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(eatingSoundFile);
                this.eatingSound = AudioSystem.getClip();
                this.eatingSound.open(audioStream); 
        } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        this.health = health;
        this.speed = speed;
        this.attackdamage = attackdamage;
        this.x = x;
        this.y = y;
        this.realx = x;
        if (!isCommonLoaded) {
            loadCommonImages();
        }
        loadSpecificImages(); 
    }

    private static void loadCommonImages() {
        try {
            dyingfire = new ImageIcon("assets/burntZombie.gif").getImage();
            isCommonLoaded = true;  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void loadSpecificImages();   

    public String getType() {
        return type;
    }

    public void seteating(boolean state) { 
        this.eat = state;
        if (!state && eatingSound != null) {
            eatingSound.stop();
        }
    }

    public int getX() { return x;}

    public int getY() { return y;}

    public double getRealX() { return realx;}

    public int getHealth() { return health;}

    public int setHealth(int health) {
        this.health = health;
        return health;
    }

    public void move() {
        if (chilled && System.currentTimeMillis() > chilluntil) {
            speed = speed / 0.75;
            chilled = false;    
        }
        if (alive&&!eat) {
            realx -= speed;
            x = (int) realx;
            if (currentRenderImage != walking) {
                currentRenderImage = walking;
            }
        }
    }

    public void timer(){
       if(currentRenderImage==dyingfire){
        deathTimer++;
        if(deathTimer>=120){
          remove=true;
          currentRenderImage=null;
        }
       }
    }

   public void attack(plants target) {
        if (!alive || target == null || !target.alive()) {
            seteating(false);
            return;
        }
        eat = true;
        if (eatingSound!=null && !eatingSound.isRunning()) {
            eatingSound.setFramePosition(0);
            eatingSound.loop(Clip.LOOP_CONTINUOUSLY);
        }
        if (currentRenderImage != eating) {
            currentRenderImage = eating;
        }
        cooldown++;
        if (cooldown >= 60) {
            target.takedamage(this.attackdamage);
            cooldown = 0; 
        }
    }
    public void takedamage(int damage, damagetype type) {
        if(!alive){
            return;
        }

        if (type == damagetype.fire) {
            health -= damage * 1.5; 
        } else if (type == damagetype.ice) {
            health -= damage * 0.75;
            chilluntil = System.currentTimeMillis() + 2000;
            if(!chilled) {
            speed = speed *0.75;
            chilled = true;
        }} else {
            health -= damage;
        }
        if (health <= 0) {
            die(type);
        }
    }

    public void die(damagetype type) {
        alive=false;
        speed=0;
        if (eatingSound != null) {
            eatingSound.stop();
            eatingSound.close();
        }
        switch (type) {
            case fire:
                if(dyingfire != null){
                   dyingfire.flush(); 
                }
                currentRenderImage = dyingfire;
                break;
            default:
                currentRenderImage = null;
                remove=true;
                break;
        }
    }

    public void render(Graphics g, ImageObserver observer) {
        timer();
        if (currentRenderImage != null) {
            if(currentRenderImage == dyingfire) {
            g.drawImage(currentRenderImage, x, y- 100, observer);
        }
        else {
            g.drawImage(currentRenderImage, x, y-55, observer);
        }
    }
}
    public boolean remove(){
        return this.remove;
    }
    public boolean alive() {
        return this.alive;
    }
}

class basicZombie extends zombie {
    public basicZombie(int x, int y) {
        super(200, 0.6, 50, x, y, true);
        this.type = "basic";
        this.currentRenderImage = walking;
    }
    @Override
    protected void loadSpecificImages() {
        this.eating = new ImageIcon("assets/basic eating.gif").getImage();
        this.walking = new ImageIcon("assets/basic moving.gif").getImage();
    }
    }


class bucketZombie extends zombie {
    public bucketZombie(int x, int y) {
        super(450, 0.6, 50, x, y, true);
        this.type = "bucket";
        this.currentRenderImage = walking;
    }
    @Override
    protected void loadSpecificImages() {
        this.eating = new ImageIcon("assets/Buckethead eating.gif").getImage();
        this.walking = new ImageIcon("assets/Buckethead moving.gif").getImage();
        }
    }

class coneZombie extends zombie {
    public coneZombie(int x, int y) {
        super( 400, 0.6, 50, x, y, true);
        this.currentRenderImage = walking;
        this.type = "cone";
    }
    @Override
    protected void loadSpecificImages() {
        this.eating = new ImageIcon("assets/Conehead eating.gif").getImage();
        this.walking = new ImageIcon("assets/Conehead moving.gif").getImage();
    }
    }

class flagZombie extends zombie {
    public flagZombie(int x, int y) {
        super(130, 0.56, 50, x, y, false);
        this.currentRenderImage = walking;
        this.type = "flag";
        Sound.playSound("assets/sound/Zombies are coming.wav");
    }
    @Override
    protected void loadSpecificImages() {
        this.eating = new ImageIcon("assets/Flag eating.gif").getImage();
        this.walking = new ImageIcon("assets/Flag moving.gif").getImage();
    }
    }



