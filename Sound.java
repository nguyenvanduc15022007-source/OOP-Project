import javax.sound.sampled.*;
import java.io.File;
public class Sound {
    public static void playSound(String filePath) {
        new Thread(() -> {
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
} 
        }).start();
    }
    public static Clip playLoop(String filePath) {
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                return null;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}