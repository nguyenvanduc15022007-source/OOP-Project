import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics;
class lane {
    private plants[] cells;
    private int y;
    private List<plants> plant;
    private List<zombie> zombies;
    private List<bullet> bullets;
    private List<sun> suns;
    private lawnmower mower;

public lane(int index) {
    this.cells = new plants[9];
    this.y = board.GRID_START_Y + (index * board.CELL_HEIGHT);
    this.mower = new lawnmower(50, this.y);
    this.plant = new ArrayList<>();
    this.zombies = new ArrayList<>();
    this.bullets = new ArrayList<>();
    this.suns = new ArrayList<>();
}
public void addzombie(zombie newzombie) {
    this.zombies.add(newzombie);
}

public void addPlant(plants newplant) {
    this.plant.add(newplant);
}

public void addBullet(bullet newbullet) {
    this.bullets.add(newbullet);
}

public List<zombie> getzombies() {
    return this.zombies;
}

public int getY() {
    return this.y;
}

public lawnmower getMower() {
    return this.mower;
}

public void addSun(sun newsun) {
    this.suns.add(newsun);
}

public List<sun> getsuns() {
    List<sun> currentSuns = new ArrayList<>(this.suns);
    this.suns.clear();
    return currentSuns;
}

public List<bullet> getBullets() {
    return this.bullets;
}

public List<plants> getplant() {
        List<plants> activePlants = new ArrayList<>();
        for (int i = 0; i < cells.length; i++) {
            plants p = cells[i];
            if (p != null && p.alive()) {
                activePlants.add(p);
            }
        }
        return activePlants;
    }
public boolean addPlant(plants newPlant, int colIndex) {
        if (colIndex >= 0 && colIndex < 9 && cells[colIndex] == null) {
            cells[colIndex] = newPlant;
            return true;
        }
        return false;
    }

    public void removePlantAt(int colIndex) {
        if (colIndex >= 0 && colIndex < 9) {
            cells[colIndex] = null;
        }
    }

public void update() {
    checkCollisions();
        zombies.forEach(zombie::move);
        zombies.removeIf(zombie::remove);

        for (int i = 0; i < cells.length; i++) {
            if (cells[i] != null) {
                cells[i].ability();
                if (!cells[i].alive()) cells[i] = null;
            }
        }

        bullets.forEach(bullet::move);
        bullets.removeIf(bullet::destroyed);

        if (mower != null && !mower.isDestroyed()) {
            mower.move();
        }
    }

    private void checkCollisions() {
        for (zombie z : zombies) {
            int zombieCol = (z.getX() - board.GRID_START_X) / board.CELL_WIDTH;
            if (zombieCol >= 0 && zombieCol < 9) {
                plants targetPlant = cells[zombieCol];
                if (targetPlant != null && targetPlant.alive()) {
                    if (z.getX() <= targetPlant.getX()+10 && z.getX() >= targetPlant.getX()) {
                        z.attack(targetPlant); 
                    }
                }
            }
        }
    }

    public void render(Graphics g) {
        for (plants p : cells) {
            if (p != null) {
                p.render(g);
            }
        }
        zombies.forEach(z -> z.render(g, null));
        bullets.forEach(b -> b.render(g));
        
        if (mower != null && !mower.isDestroyed()) {
            mower.render(g);
        }
    }
}