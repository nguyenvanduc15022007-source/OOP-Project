import java.util.List;
public class collision {
    public static void checkcollision(List<bullet> bullet, List<zombie> zombie) {
        for (bullet b : bullet) {
            if (b.destroyed()) { continue; }
                for (zombie z : zombie ) {
                    if (!z.alive() || z.remove()) { continue; }
                    if (b.getx() >= z.x && b.getx() <= z.x + 80) {
                        z.takedamage(b.getdamage(), b.getType());
                        Sound.playSound("assets/sound/hitzombie.wav");
                        b.destroy();
                        break; 
                    }
                }
            
                }
            }
    public static void checklawnmowercollision(lane currentlane) {
        lawnmower mower = currentlane.getMower();
        if (mower == null || mower.isDestroyed()) { return; }
        List<zombie> zombies = currentlane.getzombies();
            for (zombie z : zombies) {
                if (!z.alive() || z.remove()) { continue; }
                if (z.x <= mower.getX() + 80 && z.x >= mower.getX()-40) {
                    if (!mower.isMoving()) {
                        mower.activate();
                    }
                    z.takedamage(1000, damagetype.normal );
                }
            }
        }
    public static void checkzombieeat(lane currentlane) {
        List<zombie> zombies = currentlane.getzombies();
        List<plants> plant = currentlane.getplant();
        for (zombie z : zombies) {
            if (!z.alive()) { continue; }
            boolean isEating = false;
            for (plants p : plant) {
                if (p==null || !p.alive()) { continue; }
                if (z.getX() >= p.getX()-40 && z.getX() <= p.getX()+10) {
                    z.attack(p);
                    isEating = true;
                    break; 
                }
            }
            if (!isEating) {
                z.seteating(false);
            }
        }
    }
}
