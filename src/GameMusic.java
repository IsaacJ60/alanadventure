import javax.sound.midi.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/*
GameMusic.java
Isaac Jiang
Plays .midi music
https://freemidi.org/download3-16514-a-powerful-friend--pet-shop-boys
https://www.youtube.com/watch?v=DF19YWRo4hQ (https://www.mediafire.com/download/rm7k5kl91p2lsar/HURT_.mid)
 */

public class GameMusic implements ActionListener {

    public static final String GAME0 = "Alan's Adventure", GAME1 = "Alan's Ability", GAME2 = "Alan's Ache";
    public static final String[] GAMEMUSIC = {GAME0, GAME1, GAME2};

    private static Sequencer midiPlayer;

    public void startMidi(String midFilename) {
        try {
            File midiFile = new File(midFilename);
            Sequence song = MidiSystem.getSequence(midiFile);
            midiPlayer = MidiSystem.getSequencer();
            midiPlayer.open();
            midiPlayer.setSequence(song);
            midiPlayer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY); // repeat 0 times (play once)
            midiPlayer.start();
        } catch (MidiUnavailableException | IOException | InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public void endMidi() {
        if (midiPlayer != null) {
            midiPlayer.stop();
        }
    }

    public GameMusic(String s) {
        endMidi();
        startMidi(s);     // start the midi player
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}