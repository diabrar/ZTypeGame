import javalib.worldcanvas.WorldCanvas;
import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;
import java.util.Random;

// to represent worlds for the ZType game and hold constants.
interface IZTypeWorld {
  int SCREEN_HEIGHT = 500;
  int SCREEN_WIDTH = 500;
  int TEXT_SIZE = 20;
  double TICK = 2;
  Color BACKGROUND_COLOR = Color.BLACK;
  Color ACTIVEWORD_COLOR = Color.BLUE;
  Color INACTIVEWORD_COLOR = Color.GRAY;

  WorldScene makeScene();
}

// to represent a world for this ZType game.
class ZTypeWorld extends World implements IZTypeWorld {
  ILoWord words;
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

  ZTypeWorld(ILoWord words, Random rand) {
    this.rand = rand;
    this.words = words;
  }

  ZTypeWorld(ILoWord words) {
    this(words, new Random());
  }

  // returns the current WorldScene
  public WorldScene makeScene() {
    return this.words.draw(
            new WorldScene(SCREEN_WIDTH, SCREEN_HEIGHT)
                .placeImageXY(new RectangleImage(SCREEN_WIDTH, SCREEN_HEIGHT,
                    "solid", BACKGROUND_COLOR), 250, 250));
  }

  //move the Words on the scene and adds a new word at a random location on every tick.
  public World onTick() {
    ILoWord add = new ConsLoWord(new InactiveWord(new Utils().generateWord(this.rand),
        new Utils().randomNum(50, 450), new Utils().randomNum(50, 200)), this.words);
    return new ZTypeWorld(add.move());
  }

  // move the Words on the scene and adds a new word at a pseudo-random location on every tick
  // JUST for the tests :)
  public World onTickForTesting() {
    ILoWord add = new ConsLoWord(new InactiveWord(new Utils().generateWord(new Random(2)),
    50, 100), this.words);
    return new ZTypeWorld(add.move());
  }

  // Handles key events
  public ZTypeWorld onKeyEvent(String key) {
    if (!key.isEmpty()) {
      return new ZTypeWorld(this.words.checkAndReduce(key).filterOutEmpties());
    } else {
      return this;
    }
  }

  // Check if any word has reached the bottom of the screen
  public boolean gameOver() {
    return this.words.anyWordAtBottom();
  }

  // produce the ending scene.
  public WorldScene finalScene() {
    return new WorldScene(SCREEN_WIDTH, SCREEN_HEIGHT)
               .placeImageXY(new RectangleImage(SCREEN_WIDTH, SCREEN_HEIGHT, "solid", BACKGROUND_COLOR),
                   250, 250)
               .placeImageXY(new TextImage("Game over!", 70, Color.RED), 240, 200)
               .placeImageXY(new TextImage("nice try :)", 40, Color.WHITE), 240, 300);
  }

  // end the game!
  public WorldEnd worldEnds() {
    if (this.gameOver()) {
      return new WorldEnd(true, this.finalScene());
    } else {
      return new WorldEnd(false, this.makeScene());
    }
  }

}

class Utils {
  /* TEMPLATE
   * NO FIELDS *
   * METHODS
   ... this.generateWord(Random) ...          -- String
   ... this.generateWordAcc(Random, String) ..  -- String
   ... this.randomNum(int, int) ...      -- int
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

  public int randomNum(int min, int max) {
    return (int) (Math.random() * (max-min + 1)) + min;
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

  ILoWord activeAndInactive = new ConsLoWord(new InactiveWord("playful", 100, 400),
      new ConsLoWord(new InactiveWord("glasses", 300, 200),
          new ConsLoWord(new InactiveWord("virtual", 50, 320),
              new ConsLoWord(new ActiveWord("mother", 300, 100),
                  new ConsLoWord(new ActiveWord("food", 230, 200),
                      new ConsLoWord(new ActiveWord("popcorn", 180, 400), new MtLoWord()))))));

  ILoWord shortActInact = new ConsLoWord(new InactiveWord("play", 300, 100),
      new ConsLoWord(new ActiveWord("father", 300, 200), new MtLoWord()));

  // world examples
  ZTypeWorld world1 = new ZTypeWorld(this.activeAndInactive);
  ZTypeWorld smallWorld = new ZTypeWorld(this.shortActInact, new Random(1));
  ZTypeWorld smallWorldDiffRand = new ZTypeWorld(this.shortActInact, new Random(2));
  ZTypeWorld emptyWorld = new ZTypeWorld(empty);

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

  // to test the generateWordAcc() method.
  boolean testGenerateWordAcc(Tester t) {
    return t.checkExpect(new Utils().generateWordAcc(new Random(1), ""), "knwnee")
        && t.checkExpect(new Utils().generateWordAcc(new Random(2), "i"), "iiwpro")
        && t.checkExpect(new Utils().generateWordAcc(new Random(3), "hel"), "heljkk");
  }
  boolean testOnTick(Tester t) {
    return t.checkExpect(emptyWorld.onTickForTesting(), new ZTypeWorld(new ConsLoWord(
        new InactiveWord(new Utils().generateWord(new Random(2)), 50, 105), new MtLoWord())));
  }

  boolean testOnKeyEvent(Tester t) {
    return t.checkExpect(smallWorld.onKeyEvent("p"),
        new ZTypeWorld(new ConsLoWord(new ActiveWord("lay", 300, 100),
            new ConsLoWord(new ActiveWord("father", 300, 200), new MtLoWord()))));
  }

  // to actually run and see the results of the makeScene() method.
  boolean testScene(Tester t) {
    WorldCanvas c1 = new WorldCanvas(500, 500);
    return c1.drawScene(world1.makeScene()) && c1.show();
  }

  boolean testBigBang(Tester t) {
    return emptyWorld.bigBang(IZTypeWorld.SCREEN_WIDTH, IZTypeWorld.SCREEN_HEIGHT, IZTypeWorld.TICK);
  }

}