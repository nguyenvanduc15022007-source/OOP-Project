import java.util.List;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
abstract class plants {
    protected String type;
    protected int health;
    protected int x,y;
    protected int cost;
    protected boolean alive=true;
    protected Image currentRenderImage;
    public plants(int health, int cost, int x, int y) {
        this.health = health;
        this.cost = cost;
        this.x = x;
        this.y = y;
        loadSpecificImages(); 
    }

    public int getX() { return x;}

    public int getY() { return y;}

    public int getHealth() { return health;}

    public String getType() { return type;}

    public int setHealth(int health) {
        this.health = health;
        return health;
    }

    protected abstract void loadSpecificImages();                   

    public abstract void ability();

    public void takedamage(int damage){
        if (!alive) { return; }
        health-=damage;
        if(health<=0){
            die();
        }
    }
    private void die(){
        this.alive=false;
        this.currentRenderImage=null;
    }
     public void render(Graphics g) {
        if (currentRenderImage != null) {
        g.drawImage(currentRenderImage, x+5, y-10, 75, 75, null);
    }
    }
    public boolean alive() {
        return this.alive;
    }
}

class peashooter extends plants {
    private int cooldown=0;
    private lane currentlane;
    private Image peashooterImage;
    public peashooter(int x, int y, lane Lane) {
        super(300, 100, x, y);
        this.currentlane = Lane;
        this.type = "peashooter";
    }
    @Override
    protected void loadSpecificImages() {
        try {
            peashooterImage = new ImageIcon("assets/pea.gif").getImage();
            currentRenderImage = peashooterImage; 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void ability() {
       if (!alive) { return; }
       if (!currentlane.getzombies().isEmpty()) {
        cooldown++;
        if (cooldown >= 60) {
            bullet b = new bullet(x + 80, y, 20, damagetype.normal);
            currentlane.addBullet(b);
            cooldown = 0; 
        }
    }
        else { cooldown=0;}
}
}

class snowpea extends plants {
    private int cooldown=0;
    private lane currentlane;
    private Image snowpeaImage;
    public snowpea(int x, int y, lane Lane) {
        super(300, 175, x, y);
        this.currentlane = Lane;
        this.type = "snowpea";
    }
    @Override
    protected void loadSpecificImages() {
        try {
            snowpeaImage = new ImageIcon("assets/snowpea.gif").getImage();
            currentRenderImage = snowpeaImage; 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void ability() {
       if (!alive) { return; }
       if (!currentlane.getzombies().isEmpty()) {
        cooldown++;
        if (cooldown >= 60) {
            bullet b = new bullet(x + 80, y, 20, damagetype.ice);
            currentlane.addBullet(b);
            cooldown = 0; 
        }
    }
        else { cooldown=0;}
}
}

class wallnut extends plants {
    private Image wallnutNormalImage;
    private Image wallnutDamagedImage;
    public wallnut(int x, int y, lane Lane) {
        super(4000, 50, x, y);
        this.type = "wallnut";
    }
    @Override
    protected void loadSpecificImages() {
        try {
            wallnutNormalImage = new ImageIcon("assets/walnut_full_life.gif").getImage();
            wallnutDamagedImage = new ImageIcon("assets/walnut_half_life.gif").getImage();
            currentRenderImage = wallnutNormalImage; 
        } catch (Exception e) {
            e.printStackTrace(); }
        }
    @Override
    public void takedamage(int damage){
       super.takedamage(damage);
        if (health <= 2000 && currentRenderImage != wallnutDamagedImage) {
            currentRenderImage = wallnutDamagedImage;
        }
    }
    @Override
    public void ability() {}
}

class cherrybomb extends plants {
    private lane currentlane;
    private int timer=0;
    private boolean exploded=false;
    public cherrybomb(int x, int y, lane Lane) {
        super(1000, 150, x, y);
        this.currentlane = Lane;
        this.type = "cherrybomb";
    }
    @Override
    protected void loadSpecificImages() {
        try {
            Image freshImage = new ImageIcon("assets/cherryBomb.gif").getImage();
            freshImage.flush();
            currentRenderImage = freshImage; 
        } catch (Exception e) {
            e.printStackTrace(); }
        }
    @Override
    public void ability() {
         if (!alive) { return; }
         timer++;
          if (timer == 90 && !exploded) { 
                 explode();
    }
          if(timer >=145) {
            this.alive = false;
            this.currentRenderImage = null;
    }
}
    private void explode() {
        exploded=true;
        List<zombie> targets = currentlane.getzombies();
        int radiusfront=150;
        int radiusback=150;
        for (zombie z : targets) {
            if (z.x >= (this.x - radiusback) && z.x <= (this.x + radiusfront)) { 
                z.takedamage(1800, damagetype.fire);
            }
        }
        Sound.playSound("assets/sound/cherrybomb.wav");
    }
    @Override
    public void render(Graphics g) {
        if(currentRenderImage !=null) {
            g.drawImage(currentRenderImage, x - 40, y - 50, 160, 160, null);
        }
    }
}

class sunflower extends plants {
        private int cooldown=0; 
        private lane currentlane;
        private Image sunflowerImage;
        public sunflower(int x, int y, lane Lane) {
            super(300, 50, x, y);
            this.currentlane = Lane;
            this.type = "sunflower";
        }
        @Override
        protected void loadSpecificImages() {
            try {
                sunflowerImage = new ImageIcon("assets/sunflower.gif").getImage();
                currentRenderImage = sunflowerImage; 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void ability() {
            if (!alive) { return; }
            cooldown++;
            if (cooldown >= 600) {
                sun s = new sun(this.x + 20, this.y, this.y + 35);
                currentlane.addSun(s);
                cooldown = 0;
            }
        }
    }


            


