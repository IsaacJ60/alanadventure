/*
Sound.java
Isaac Jiang
Sound loads and contains methods to play music and sound effects

equip gun
https://www.videvo.net/sound-effect/gun-cock-single-pe1096303/246057/

// land
Sound Effect from <a href="https://pixabay.com/?utm_source=link-attribution&amp;utm_medium=referral&amp;utm_campaign=music&amp;utm_content=43790">Pixabay</a>
 */

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class Sound implements ActionListener {
    SoundEffect sound;
    // all soundeffects and music
    private static SoundEffect equipBlaster, equipGeneral, purchaseItem, alanLand, alanJump,
            noBullets, alanShoot;
    private static ArrayList<SoundEffect> allSounds;
    public Sound() {
        allSounds = new ArrayList<>();
        // loading all music and sounds
        String path = "src/assets/sounds/effects/";
        equipGeneral = new SoundEffect(path + "equipBg" + ".wav");
        equipBlaster = new SoundEffect(path + "equipBlaster" + ".wav");
        purchaseItem = new SoundEffect(path + "purchase" + ".wav");
        alanLand = new SoundEffect(path + "alanLand" + ".wav");
        alanJump = new SoundEffect(path + "alanJump" + ".wav");
        noBullets = new SoundEffect(path + "noBullets" + ".wav");
        alanShoot = new SoundEffect(path + "alanShoot" + ".wav");
        allSounds.add(equipGeneral);
        allSounds.add(equipBlaster);
        allSounds.add(purchaseItem);
        allSounds.add(alanLand);
        allSounds.add(alanJump);
        allSounds.add(noBullets);
        allSounds.add(alanShoot);
    }
    public void actionPerformed(ActionEvent ae){
        sound.play();
    }

    public static void stopAll() {
        for (SoundEffect sound : allSounds) {
            sound.stop();
        }
    }

    public static void equipBlaster() {equipBlaster.play();}
    public static void equipGeneral() {equipGeneral.play();}
    public static void purchaseItem() {purchaseItem.play();}
    public static void alanLand() {alanLand.play();}
    public static void alanJump() {alanJump.play();}
    public static void noBullets() {noBullets.play();}
    public static void alanShoot() {alanShoot.play();}
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