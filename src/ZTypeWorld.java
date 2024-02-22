import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
import javalib.worldcanvas.WorldCanvas;

import java.awt.Color;
import java.util.Random;

// to represent worlds for the ZType game and hold constants.
interface IZTypeWorld {
  int SCREEN_HEIGHT = 500;
  int SCREEN_WIDTH = 500;
  int TEXT_SIZE = 20;
  Color BACKGROUND_COLOR = Color.BLACK;
  Color ACTIVEWORD_COLOR = Color.BLUE;
  Color INACTIVEWORD_COLOR = Color.GRAY;

  WorldScene makeScene();
}

// to represent a world for this ZType game.
class ZTypeWorld extends World implements IZTypeWorld {
  ILoWord activeWords;
  ILoWord inactiveWords;

  Random rand;

  /* TEMPLATE
   * FIELDS
   ... this.activeWords ...    -- ILoWord
   ... this.inactiveWords ...  -- ILoWord
   * METHODS
   ... this.makeScene() ...    -- WorldScene
   * METHODS ON FIELDS
   ... this.activeWords.checkAndReduce() ...       -- ILoWord
   ... this.activeWords.addToEnd(IWord) ...        -- ILoWord
   ... this.activeWords.filterOutEmpties() ...      -- ILoWord
   ... this.activeWords.draw(WorldScene) ...       -- WorldScene
   ... this.inactiveWords.checkAndReduce() ...     -- ILoWord
   ... this.inactiveWords.addToEnd(IWord) ...      -- ILoWord
   ... this.inactiveWords.filterOutEmpties() ...    -- ILoWord
   ... this.inactiveWords.draw(WorldScene) ...     -- WorldScene
   */

  ZTypeWorld(ILoWord activeWords, ILoWord inactiveWords) {
    this.rand = new Random();
    this.activeWords = activeWords;
    this.inactiveWords = inactiveWords;
  }

  ZTypeWorld(ILoWord activeWords, ILoWord inactiveWords, Random rand) {
    this.rand = rand;
    this.activeWords = activeWords;
    this.inactiveWords = inactiveWords;
  }

  // returns the current WorldScene
  public WorldScene makeScene() {
    return this.activeWords.draw(
        this.inactiveWords.draw(
            new WorldScene(SCREEN_WIDTH, SCREEN_HEIGHT)
            .placeImageXY(new RectangleImage(SCREEN_WIDTH, SCREEN_HEIGHT, "solid", BACKGROUND_COLOR)
                , 250, 250)));
  }

  //Handles key events
  public ZTypeWorld onKeyEvent(String key) {
    if (!key.isEmpty()) {
      return new ZTypeWorld(this.activeWords.checkAndReduce(key), this.inactiveWords.checkAndReduce(key));
    } else {
      return this;
    }
  }

  //Handles tick events
  public ZTypeWorld onTick() {
    Utils utils = new Utils();
    return new ZTypeWorld(this.activeWords.addToEnd(new ActiveWord(utils.generateWord(new Random()),
        250, 0)), this.inactiveWords);
  }

  public boolean gameOver() {
    // Check if any word has reached the bottom of the screen
    return this.activeWords.anyWordAtBottom();
  }

}



class Utils {
  /* TEMPLATE
   * NO FIELDS *
   * METHODS
   ... this.generateWord(Random) ...          -- String
   ... this.generateWordAcc(Random, String) ..  -- String
   */

  // returns a random word of 6 characters.
  // to test : pass a seeded rand.
  public String generateWord(Random rand) {
    return generateWordAcc(rand, "");
  }

  // accumulator for generateWord()
  public String generateWordAcc(Random rand, String start) {
    String alphabet = "abcdefghijklmnopqrstuvwxyz";
    if (start.length() == 6) {
      return start;
    } else {
      char randLetter = alphabet.charAt(rand.nextInt(25));
      String next = start + randLetter;
      return generateWordAcc(rand, next);
    }
  }
}

// examples and tests
class ExamplesZTypeWorld {
  ExamplesZTypeWorld() {}

  // examples of ILoWord
  ILoWord active = new ConsLoWord(new ActiveWord("mother", 300, 100),
      new ConsLoWord(new ActiveWord("food", 230, 200),
          new ConsLoWord(new ActiveWord("popcorn", 180, 400), new MtLoWord())));
  ILoWord shortActive = new ConsLoWord(new ActiveWord("father", 300, 200),
      new MtLoWord());
  ILoWord inactive = new ConsLoWord(new InactiveWord("playful", 100, 400),
      new ConsLoWord(new InactiveWord("glasses", 300, 200),
          new ConsLoWord(new InactiveWord("virtual", 50, 320), new MtLoWord())));
  ILoWord shortInactive = new ConsLoWord(new InactiveWord("play", 300, 100),
      new MtLoWord());
  ILoWord empty = new MtLoWord();

  // world examples
  ZTypeWorld world1 = new ZTypeWorld(active, inactive);
  ZTypeWorld smallWorld = new ZTypeWorld(shortActive, shortInactive, new Random(1));
  ZTypeWorld smallWorldDiffRand = new ZTypeWorld(shortActive, shortInactive, new Random(2));
  ZTypeWorld emptyWorld = new ZTypeWorld(empty, empty);

  // to test the MakeScene() method.
  boolean testMakeScene(Tester t) {
    return t.checkExpect(smallWorld.makeScene(),
        new WorldScene(IZTypeWorld.SCREEN_WIDTH, IZTypeWorld.SCREEN_HEIGHT)
        .placeImageXY(new RectangleImage(IZTypeWorld.SCREEN_WIDTH, IZTypeWorld.SCREEN_HEIGHT,
            "solid", IZTypeWorld.BACKGROUND_COLOR), 250, 250)
        .placeImageXY(
            new TextImage("play", IZTypeWorld.TEXT_SIZE, IZTypeWorld.INACTIVEWORD_COLOR),
            300, 100)
        .placeImageXY(
            new TextImage("father", IZTypeWorld.TEXT_SIZE, IZTypeWorld.ACTIVEWORD_COLOR),
            300, 200))
        && t.checkExpect(world1.makeScene(),
            new WorldScene(IZTypeWorld.SCREEN_WIDTH, IZTypeWorld.SCREEN_HEIGHT)
            .placeImageXY(new RectangleImage(IZTypeWorld.SCREEN_WIDTH, IZTypeWorld.SCREEN_HEIGHT,
                "solid", IZTypeWorld.BACKGROUND_COLOR), 250, 250)
            .placeImageXY(
                new TextImage("playful", IZTypeWorld.TEXT_SIZE, IZTypeWorld.INACTIVEWORD_COLOR),
                100, 400)
            .placeImageXY(
                new TextImage("glasses", IZTypeWorld.TEXT_SIZE, IZTypeWorld.INACTIVEWORD_COLOR),
                300, 200)
            .placeImageXY(
                new TextImage("virtual", IZTypeWorld.TEXT_SIZE, IZTypeWorld.INACTIVEWORD_COLOR),
                50, 320)
            .placeImageXY(
                new TextImage("mother", IZTypeWorld.TEXT_SIZE, IZTypeWorld.ACTIVEWORD_COLOR),
                300, 100)
            .placeImageXY(
                new TextImage("food", IZTypeWorld.TEXT_SIZE, IZTypeWorld.ACTIVEWORD_COLOR),
                230, 200)
            .placeImageXY(
                new TextImage("popcorn", IZTypeWorld.TEXT_SIZE, IZTypeWorld.ACTIVEWORD_COLOR),
                180, 400))
        && t.checkExpect(emptyWorld.makeScene(),
            new WorldScene(IZTypeWorld.SCREEN_WIDTH, IZTypeWorld.SCREEN_HEIGHT)
            .placeImageXY(new RectangleImage(IZTypeWorld.SCREEN_WIDTH, IZTypeWorld.SCREEN_HEIGHT,
                "solid", IZTypeWorld.BACKGROUND_COLOR), 250, 250));
  }

  // to test the generateWord() method.
  boolean testGenerateWord(Tester t) {
    return t.checkExpect(new Utils().generateWord(new Random(1)), "knwnee")
        && t.checkExpect(new Utils().generateWord(new Random(2)), "iwproa")
        && t.checkExpect(new Utils().generateWord(new Random(3)), "jkkgdc");
  }

  //to actually run and see the results of the makeScene() method.
  boolean testScene(Tester t) {
    WorldCanvas c1 = new WorldCanvas(500, 500);
    return c1.drawScene(world1.makeScene()) && c1.show();
  }

  //Test method to simulate key press events
  boolean testKeyEventM(Tester t) {
    ZTypeWorld newWorld = world1.onKeyEvent("m"); 
    WorldCanvas canvas = new WorldCanvas(500, 500);
    return canvas.drawScene(newWorld.makeScene()) && canvas.show();
  }


  // Test method to simulate tick events
  boolean testTickEvent(Tester t) {
    ZTypeWorld newWorld = world1.onTick(); 
    WorldCanvas canvas = new WorldCanvas(500, 500);
    return canvas.drawScene(newWorld.makeScene()) && canvas.show();
  }


  //Test for the gameOver() method 
  boolean testGameOverWithActiveWords(Tester t) {
    return t.checkExpect(world1.gameOver(), false)
        && t.checkExpect(emptyWorld.gameOver(), false)
        && t.checkExpect(smallWorld.gameOver(), false);
  }

  //Test for the gameOver() method when there are no active words at the bottom
  boolean testGameOverNoActiveWords(Tester t) {
    return t.checkExpect(world1.onTick().onTick().gameOver(), false)
        && t.checkExpect(emptyWorld.onTick().gameOver(), false)
        && t.checkExpect(smallWorld.onTick().onTick().onTick().onTick().onTick().gameOver(), false);
  }

  //Test for the onKeyEvent() method when a key matches an active word
  boolean testOnKeyEventMatch(Tester t) {
    return t.checkExpect(world1.onKeyEvent("m"), new ZTypeWorld(
        new ConsLoWord(new ActiveWord("other", 300, 100),
            new ConsLoWord(new ActiveWord("food", 230, 200),
                new ConsLoWord(new ActiveWord("popcorn", 180, 400), new MtLoWord()))),
        new ConsLoWord(new InactiveWord("playful", 100, 400),
            new ConsLoWord(new InactiveWord("glasses", 300, 200),
                new ConsLoWord(new InactiveWord("virtual", 50, 320), new MtLoWord())))));
  }

  //Test for the onKeyEvent() method when a key does not match any active word
  boolean testOnKeyEventNoMatch(Tester t) {
    return t.checkExpect(world1.onKeyEvent("x"), world1)
        && t.checkExpect(emptyWorld.onKeyEvent("a"), emptyWorld);
  }

  //Test for the onTick() method to ensure a new active word is added
  boolean testOnTick(Tester t) {
    return t.checkExpect(world1.onTick(), new ZTypeWorld(
        new ConsLoWord(new ActiveWord("mother", 300, 100),
            new ConsLoWord(new ActiveWord("food", 230, 200),
                new ConsLoWord(new ActiveWord("popcorn", 180, 400),
                    new ConsLoWord(new ActiveWord("iwproa", 250, 0), new MtLoWord())))),
        new ConsLoWord(new InactiveWord("playful", 100, 400),
            new ConsLoWord(new InactiveWord("glasses", 300, 200),
                new ConsLoWord(new InactiveWord("virtual", 50, 320), new MtLoWord())))));
  }
}