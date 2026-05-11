import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
public class sun {
    private int x,y;
    private int targety;
    private int speed=2;
    private int lifetime=0;
    private boolean collected=false;
    private boolean expired=false;
    private Image sunImage;
    public sun(int x, int y, int targety) {
        this.x = x;
        this.y = y;
        this.targety = targety; 
        try {
            sunImage = new ImageIcon("assets/sun.png").getImage();
        } catch (Exception e) {
            e.printStackTrace();
    }
}

public void update() {
    if (y < targety) {
        y += speed;
        }
    else {
        lifetime++;
        if (lifetime > 300) { 
            expired = true;
        }
    }
}
public void render(Graphics g) {
    if (!Isremoved() && sunImage != null) {
        g.drawImage(sunImage, x, y, null);
    }
}

public boolean isclicked(int mouseX, int mouseY) {
    int sunsize = 50;
    int buffer = 20;
    return (mouseX >= x-buffer && mouseX <= x+sunsize+buffer) &&
            (mouseY >= y-buffer && mouseY <= y+sunsize+buffer);
}

public void collect() {
    this.collected = true;
}

public boolean Isremoved() {
    return expired || collected;
}
}