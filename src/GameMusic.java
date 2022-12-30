import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.sound.midi.*;

public class GameMusic implements ActionListener {

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