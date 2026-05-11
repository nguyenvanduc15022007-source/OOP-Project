import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
class bullet {
    private int x,y;
    private int damage;
    private int speed=5;
    private boolean destroyed=false;
    private damagetype type;
    private static Image peabullet;
    private static Image icebullet;
    private static boolean imageloaded=false;
    private Image currentImage;
    public bullet(int x, int y, int damage, damagetype type) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.type = type;
        if (!imageloaded) {
            loadImages();
        }  
    switch (type) {
        case normal:
            currentImage = peabullet;
                break;
        case ice:
            currentImage = icebullet;
                break;
        default:
            currentImage = peabullet; 
        }   
}
    private static void loadImages() {
        try {
            peabullet = new ImageIcon("assets/bullet.png").getImage();
            icebullet = new ImageIcon("assets/snowbullet.png").getImage();
            imageloaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void move() {
        if (!destroyed) {
            x += speed;
        if (x > 900) { 
            this.destroy();
        }
    }
}
    public int getx() { return x;}
    public int getdamage() { return damage;}
    public damagetype getType() { return type;}
    public boolean destroyed() { return destroyed;}

    public void destroy() {
        this.destroyed = true;
    }
     public void render(Graphics g) {
        if (!destroyed && currentImage != null) {
            g.drawImage(currentImage, x, y, null);
        }
    }
}
