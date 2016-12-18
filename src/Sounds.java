import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/*

AUDIO SUPPORT CLASS

 */
public enum Sounds {
    DISABLED("gui/sounds/disabled.wav"), // Added
    INCOMING("gui/sounds/incoming.wav"), // Added
    SEND("gui/sounds/send.wav"),         // Added
    RECEIVE("gui/sounds/receive.wav"),   // Added
    ERROR("gui/sounds/error.wav");       // Added

    private Clip clip;

    Sounds(String fileName) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource(fileName));
            clip = AudioSystem.getClip();
            clip.open(audioIn);
        } catch (Exception e) {
        }
    }

    public void play() {
        if (clip.isRunning())
            clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    static void init() {
        values();
    }
}
