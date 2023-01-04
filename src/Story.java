// idea behind story is: in the intro screen, there is a story that is shown and it's very cool

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Story {
    private final ArrayList<String> sentences;

    public Story() {
        sentences = new ArrayList<>();

        try {
            Scanner f = new Scanner(new BufferedReader(new FileReader("src/assets/story/story.txt")));
            while (f.hasNextLine()) {sentences.add(f.nextLine());}
        } catch(FileNotFoundException ex) {System.out.println(ex + " file not found lol");}
    }

    public void display(Graphics g) {
        ;
    }
}
