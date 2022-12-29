/*
Sound.java
Isaac Jiang
Sound loads and contains methods to play music and sound effects
 */

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;

public class Sound extends JFrame implements ActionListener {
    SoundEffect sound;
    // all soundeffects and music
    private static SoundEffect intromusic;
    public Sound() {
        // loading all music and sounds
        intromusic = new SoundEffect("src/assets/sounds/bg/intro.wav");
    }
    public void actionPerformed(ActionEvent ae){
        sound.play();
    }

    public static void main(String args[]){new Sound().setVisible(true);}

    // looping or stopping game music
    public static void introMusic(String s) {
        if (s.equals("STOP")) {
            intromusic.stop();
        } else {
            intromusic.stop();
            intromusic.loop();
        }
    }
}

class SoundEffect{
    private Clip c;
    public SoundEffect(String filename){
        setClip(filename);
    }
    public void setClip(String filename){
        try{
            File f = new File(filename);
            c = AudioSystem.getClip();
            c.open(AudioSystem.getAudioInputStream(f));
        } catch(Exception e) {System.out.println("file not found");}
    }
    // play sound one time
    public void play() {
        c.setFramePosition(0);
        c.start();
    }
    // looping music instead of just playing it once
    public void loop() {
        c.setFramePosition(0);
        c.loop(Clip.LOOP_CONTINUOUSLY);
    }
    // stopping music
    public void stop() {c.stop();}
}