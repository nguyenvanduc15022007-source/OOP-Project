import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.sound.sampled.Clip;
public class lawnmower {
    private int x,y;
    private int speed=10;
    private boolean destroyed=false;
    private boolean moving=false;
    private Image mowerImage;
    private Image mowermovingImage;
    private Image currentImage;
    private Clip movingSound;
    public lawnmower(int x, int y) {
        this.x = x;
        this.y = y;
        try {
        mowerImage = new ImageIcon("assets/lawnmower.gif").getImage();
        mowermovingImage = new ImageIcon("assets/lawnmowerActivated.gif").getImage();
        currentImage = mowerImage;
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    public void stopSound() {
        if (movingSound != null && movingSound.isRunning()) {
            movingSound.stop();
        }
    }

    public void resumeSound() {
        if (this.moving && !this.destroyed) {
            if (movingSound != null && !movingSound.isRunning()) {
                movingSound.loop(Clip.LOOP_CONTINUOUSLY); 
            }
        }
    }
    public void move() {
        if (moving) {
            x += speed;
            if (x>800) {
                destroyed=true;
                if (movingSound != null) {
                    movingSound.stop();
                }
            }
        }
    }
    public void activate() {
        if(!this.moving) {
        this.moving=true;
        this.currentImage=this.mowermovingImage;
        this.movingSound = Sound.playLoop("assets/sound/lawnmower.wav");
        }
    }
    public void render(Graphics g) {
        if (!destroyed && currentImage != null) {
            g.drawImage(currentImage, x+100, y, 100, 100, null);
        }
    }
    public boolean isDestroyed() {
        return destroyed;
    }
    public int getX() {
        return x;
    }
    public boolean isMoving() {
        return this.moving;
    }
}
