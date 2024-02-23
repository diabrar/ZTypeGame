import javalib.funworld.WorldScene;
import javalib.worldimages.*;
import tester.*;

import java.awt.*;

//represents a list of words
interface ILoWord {
  // checkAndReduce : ILoWord String -> ILoWord
  // returns a new ILoWord where any active words in ILoWord that start with the given letter
  // are reduced.
  ILoWord checkAndReduce(String letter);

  // addToEnd : ILoWord IWord -> ILoWord
  // returns a new ILoWord like this one but with word added to the end.
  ILoWord addToEnd(IWord word);

  // filterOutEmpties : ILoWord -> ILoWord
  // returns a new ILoWord with any empty strings filtered out
  ILoWord filterOutEmpties();

  // draw : ILoWord WorldScene -> WorldScene
  // returns a WorldScene with all words in this ILoWord drawn onto the given
  // WorldScene.
  WorldScene draw(WorldScene world);

  // moves each word in this ILoWord.
  ILoWord move();

  // anyWordAtBottom : ILoWord -> boolean
  // returns true if any word in this list is at the bottom of the screen
  boolean anyWordAtBottom();

}

//represents an empty list of words
class MtLoWord implements ILoWord {
  MtLoWord() {}

  /* TEMPLATE
   * NO FIELDS *
   * METHODS
   ... this.checkAndReduce(String) ...       -- ILoWord
   ... this.addToEnd(IWord) ...              -- ILoWord
   ... this.filterOutEmpties() ...           -- ILoWord
   ... this.draw(WorldScene) ...             -- WorldScene
   ... this.move() ...                       -- ILoWord
   */

  // returns this empty list.
  public ILoWord checkAndReduce(String letter) {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     */
    return this;
  }

  // returns a list with the given IWord.
  public ILoWord addToEnd(IWord word) {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     * NO METHODS ON FIELDS *
     */
    return new ConsLoWord(word, this);
  }

  // returns the original empty list.
  public ILoWord filterOutEmpties() {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     */
    return this;
  }

  // returns the given WorldScene
  public WorldScene draw(WorldScene world) {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     * METHODS ON FIELDS
     ... world.placeImageXY(WorldImage, int, int) ... -- WorldScene
     */
    return world;
  }

  // empty list has no words to move.
  public ILoWord move() {
    return this;
  }

  // empty list has no words.
  public boolean anyWordAtBottom() {
    return false;
  }

}

class ConsLoWord implements ILoWord {
  IWord first;
  ILoWord rest;

  ConsLoWord(IWord first, ILoWord rest) {
    this.first = first;
    this.rest = rest;
  }

  /* TEMPLATE
   * FIELDS
   ... this.first ...  -- IWord
   ... this.rest ...   -- ILoWord
   * METHODS
   ... this.checkAndReduce() ...       -- ILoWord
   ... this.addToEnd(IWord) ...        -- ILoWord
   ... this.filterOutEmpties() ...     -- ILoWord
   ... this.draw(WorldScene) ...       -- WorldScene
   * METHODS ON FIELDS
   ... this.first.getWord() ...                   -- String
   ... this.rest.checkAndReduce(String) ...       -- ILoWord
   ... this.rest.addToEnd(IWord) ...              -- ILoWord
   ... this.rest.filterOutEmpties() ...           -- ILoWord
   ... this.rest.draw(WorldScene) ...             -- WorldScene
   */

  // returns a new list where active words that begin with letter are reduced
  // by removing that first letter.
  public ILoWord checkAndReduce(String letter) {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     */
    if (this.first.firstWithLet(letter)) {
      return new ConsLoWord(this.first.checkLetter(letter), this.rest);
    }
    return new ConsLoWord(this.first, this.rest.checkAndReduce(letter));
  }

  // returns a new ILoWord like this one but with word added to the end.
  public ILoWord addToEnd(IWord word) {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     * METHODS ON FIELDS
     ... word.getWord() ...             -- String
     ... word.checkLetter(String) ...   -- IWord
     ... word.checkEmpty() ...          -- boolean
     */
    return new ConsLoWord(this.first, this.rest.addToEnd(word));
  }

  // returns a new ILoWord with any empty strings removed.
  public ILoWord filterOutEmpties() {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     */
    if (this.first.checkEmpty()) {
      return this.rest.filterOutEmpties();
    }
    return new ConsLoWord(this.first, this.rest.filterOutEmpties());
  }

  // returns world with all of the words in this drawn onto it.
  public WorldScene draw(WorldScene world) {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     * METHODS ON FIELDS
     ... world.placeImageXY(WorldImage, int, int) ... -- WorldScene
     */
    return this.rest.draw(this.first.createWorld(world));
  }

  // empty list has no words to move.
  public ILoWord move() {
    return new ConsLoWord(this.first.moveWord(), this.rest.move());
  }

  public boolean anyWordAtBottom() {
    return this.first.atBottom() || this.rest.anyWordAtBottom();
  }

}

//represents a word in the ZType game
interface IWord {
  // getWord : IWord -> String
  // gets the word
  String getWord();

  // checkLetter : IWord String -> IWord
  // returns either the same IWord if the word does not start with the given letter,
  // or a new IWord with the first letter cut off.
  IWord checkLetter(String letter);

  // checkEmpty : IWord -> boolean
  // returns true if this word is empty, i.e, ""
  boolean checkEmpty();

  // createWorld : IWord WorldScene -> WorldScene
  // adds this IWord to the given WorldScene.
  WorldScene createWorld(WorldScene initWorld);

  // moves this word down the y-axis.
  IWord moveWord();

  // returns true if this word is at the bottom of the screen.
  boolean atBottom();

  // returns true if the word starts with the given letter.
  boolean firstWithLet(String letter);
}

//represents an active word in the ZType game
class ActiveWord implements IWord {
  String word;
  int x;
  int y;

  /* TEMPLATE
   * FIELDS
   ... this.word ...    -- String
   ... this.x ...       -- int
   ... this.y ...       -- int
   * METHODS
   ... this.getWord() ...                         -- String
   ... this.checkLetter(String) ...               -- IWord
   ... this.checkEmpty() ...                      -- boolean
   ... this.createWorld(WorldScene) ...           -- WorldScene
   ... this.becomeActive(String) ...              -- boolean
   */

  ActiveWord(String word, int x, int y) {
    this.word = word;
    this.x = x;
    this.y = y;
  }

  public String getWord() {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     */
    return this.word;
  }

  public IWord checkLetter(String letter) {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     */
    if (!this.word.isEmpty() && this.firstWithLet(letter)) {
      return new ActiveWord(this.word.substring(1), this.x, this.y);
    }
    return this;
  }

  public boolean firstWithLet(String letter) {
    if (!this.word.isEmpty()) {
      return this.word.substring(0,1).equals(letter);
    }
    return false;
  }

  public boolean checkEmpty() {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     */
    return this.word.isEmpty();
  }

  public WorldScene createWorld(WorldScene initWorld) {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     * METHODS ON FIELDS
     ... initWorld.placeImageXY(WorldImage, int, int) ... -- WorldScene
     */
    return initWorld.placeImageXY(new TextImage(this.word, 20, IZTypeWorld.ACTIVEWORD_COLOR),
        this.x, this.y);
  }

  // returns an ActiveWord like this one, but shifted down 5.
  public IWord moveWord() {
    return new ActiveWord(this.word, this.x, this.y + 10);
  }

  // returns true if this word is at the bottom of the screen.
  public boolean atBottom() {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     */
    return this.y >= IZTypeWorld.SCREEN_HEIGHT;
  }

}

//represents an inactive word in the ZType game
class InactiveWord implements IWord {
  String word;
  int x;
  int y;

  /* TEMPLATE
   * FIELDS
   ... this.word ...    -- String
   ... this.x ...       -- int
   ... this.y ...       -- int
   * METHODS
   ... this.getWord() ...                         -- String
   ... this.checkLetter(String) ...               -- IWord
   ... this.checkEmpty() ...                      -- boolean
   ... this.createWorld(WorldScene) ...           -- WorldScene
   */

  InactiveWord(String word, int x, int y) {
    this.word = word;
    this.x = x;
    this.y = y;
  }

  public String getWord() {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     */
    return this.word;
  }

  public IWord checkLetter(String letter) {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     */
    if (!this.word.isEmpty() && this.firstWithLet(letter)) {
      return new ActiveWord(this.word.substring(1), this.x, this.y);
    }
    return this;
  }

  public boolean firstWithLet(String letter) {
    return this.word.substring(0,1).equals(letter);
  }

  public boolean checkEmpty() {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     */
    return this.word.isEmpty();
  }

  public WorldScene createWorld(WorldScene initWorld) {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     * METHODS ON FIELDS
     ... initWorld.placeImageXY(WorldImage, int, int) ... -- WorldScene
     */
    return initWorld.placeImageXY(new TextImage(this.word, 20, IZTypeWorld.INACTIVEWORD_COLOR),
        this.x, this.y);
  }

  // returns a new InactiveWord like this one, but shifted down 5.
  public IWord moveWord() {
    return new InactiveWord(this.word, this.x, this.y + 10);
  }

  // returns true if this word is at the bottom of the screen.
  public boolean atBottom() {
    /* TEMPLATE
     * EVERYTHING IN THE CLASS TEMPLATE *
     */
    return this.y >= IZTypeWorld.SCREEN_HEIGHT;
  }

}

//all examples and tests for ILoWord
class ExamplesWordLists {
  ExamplesWordLists(){}

  // active words
  IWord plant = new ActiveWord("plant", 10, 10);
  IWord marine = new ActiveWord("marine", 20, 10);
  IWord shop = new ActiveWord("shop", 15, 5);

  // inactive words
  IWord palace = new InactiveWord("palace", 30, 0);
  IWord landscape = new InactiveWord("landscape", 0, 0);
  IWord sandwich = new InactiveWord("sandwich", 50, 50);
  IWord emptyWord = new InactiveWord("", 0, 0);

  // list examples
  ILoWord empty = new MtLoWord();
  ILoWord list1 = new ConsLoWord(plant, empty);
  ILoWord list2 = new ConsLoWord(marine, list1);
  ILoWord activeWords = new ConsLoWord(shop, list2);

  ILoWord iList1 = new ConsLoWord(palace, empty);
  ILoWord iList2 = new ConsLoWord(landscape, iList1);
  ILoWord inactiveWords = new ConsLoWord(sandwich, iList2);

  ILoWord mList1 = new ConsLoWord(palace, activeWords);
  ILoWord mixedWords = new ConsLoWord(sandwich, mList1);

  ILoWord listWithEmpty = new ConsLoWord(emptyWord, mList1);
  ILoWord multipleEmpty = new ConsLoWord(emptyWord, listWithEmpty);

  ILoWord seemsSorted = new ConsLoWord(marine, activeWords);

  // starting WorldScene
  WorldScene start = new WorldScene(500, 500)
                         .placeImageXY(new RectangleImage(
                             500, 500, "solid", Color.BLACK), 250, 250);


  // to test the checkAndReduce() method
  boolean testCheckAndReduce(Tester t) {
    return t.checkExpect(empty.checkAndReduce("a"), empty)
               && t.checkExpect(list2.checkAndReduce("p"),
        new ConsLoWord(marine, new ConsLoWord(new ActiveWord("lant", 10, 10), empty)))
               && t.checkExpect(list2.checkAndReduce("a"), list2)
               && t.checkExpect(inactiveWords.checkAndReduce("b"), inactiveWords)
               && t.checkExpect(mixedWords.checkAndReduce("a"), mixedWords)
               && t.checkExpect(mixedWords.checkAndReduce("s"),
        new ConsLoWord(new ActiveWord("andwich", 50, 50), new ConsLoWord(palace,
            new ConsLoWord(new ActiveWord("shop", 15, 5), list2))));
  }

  // to test the addToEnd() method
  boolean testAddToEnd(Tester t) {
    return t.checkExpect(empty.addToEnd(landscape), new ConsLoWord(landscape, empty))
               && t.checkExpect(list1.addToEnd(landscape), new ConsLoWord(plant,
        new ConsLoWord(landscape, empty)))
               && t.checkExpect(list2.addToEnd(shop), new ConsLoWord(marine,
        new ConsLoWord(plant, new ConsLoWord(shop, empty))))
               && t.checkExpect(activeWords.addToEnd(sandwich),
        new ConsLoWord(shop, new ConsLoWord(marine, new ConsLoWord(plant,
            new ConsLoWord(sandwich, empty)))));
  }

  // to test the filterOutEmpties() method
  boolean testFilterOutEmpties(Tester t) {
    return t.checkExpect(empty.filterOutEmpties(), empty)
               && t.checkExpect(mixedWords.filterOutEmpties(), mixedWords)
               && t.checkExpect(listWithEmpty.filterOutEmpties(), mList1)
               && t.checkExpect(multipleEmpty.filterOutEmpties(), mList1);
  }

  // to test the draw() method
  boolean testDraw(Tester t) {
    return t.checkExpect(empty.draw(start), start)
               && t.checkExpect(list1.draw(start),
        start.placeImageXY(new TextImage("plant", 20, Color.BLUE), 10, 10))
               && t.checkExpect(mList1.draw(start),
        start.placeImageXY(new TextImage("palace", 20, Color.GRAY), 30, 0)
            .placeImageXY(new TextImage("shop", 20, Color.BLUE), 15, 5)
            .placeImageXY(new TextImage("marine", 20, Color.BLUE), 20, 10)
            .placeImageXY(new TextImage("plant", 20, Color.BLUE), 10, 10));
  }

  // TESTING HELPERS

  // to test the getWord() method.
  // * note : i know we aren't allowed to use getters. i tried to implement the methods
  // without one, and i couldn't figure out an implementation that was more efficient
  // than just using the getter. however, i'd rly like to know how. :(
  boolean testGetWord(Tester t) {
    return t.checkExpect(palace.getWord(), "palace")
               && t.checkExpect(emptyWord.getWord(), "")
               && t.checkExpect(plant.getWord(), "plant");
  }

  // to test the checkLetter() method.
  boolean testCheckLetter(Tester t) {
    return t.checkExpect(marine.checkLetter("m"), new ActiveWord("arine", 20, 10))
               && t.checkExpect(landscape.checkLetter("l"), new ActiveWord("andscape", 0, 0))
               && t.checkExpect(emptyWord.checkLetter("a"), emptyWord)
               && t.checkExpect(palace.checkLetter("m"), palace);
  }

  // to test the checkEmpty() method.
  boolean testCheckEmpty(Tester t) {
    return t.checkExpect(marine.checkEmpty(), false)
               && t.checkExpect(emptyWord.checkEmpty(), true)
               && t.checkExpect(landscape.checkEmpty(), false);
  }

  // to test the createWorld() method.
  boolean testCreateWorld(Tester t) {
    return t.checkExpect(marine.createWorld(start),
        start.placeImageXY(new TextImage("marine", 20, Color.BLUE), 20, 10))
               && t.checkExpect(landscape.createWorld(start),
        start.placeImageXY(new TextImage("landscape", 20, Color.GRAY), 0, 0))
               && t.checkExpect(palace.createWorld(landscape.createWorld(start)),
        landscape.createWorld(start).placeImageXY(new TextImage("palace", 20, Color.GRAY),
            30, 0))
               && t.checkExpect(emptyWord.createWorld(start), start);
  }

  boolean testMove(Tester t) {
    return t.checkExpect(list2.move(), new ConsLoWord(new ActiveWord("marine", 20, 20),
        new ConsLoWord(new ActiveWord("plant", 10, 20), new MtLoWord())));
  }

  boolean testMoveWord(Tester t) {
    return t.checkExpect(plant.moveWord(), new ActiveWord("plant", 10, 20))
        && t.checkExpect(marine.moveWord(), new ActiveWord("marine", 20, 20));
  }

  boolean testAnyWordAtBottom(Tester t) {
    return t.checkExpect(list1.anyWordAtBottom(), false)
        && t.checkExpect(new ConsLoWord(
            new ActiveWord("word", 10, 500), list1).anyWordAtBottom(), true);
  }

  boolean testAtBottom(Tester t) {
    return t.checkExpect(new ActiveWord("word", 10, 0).atBottom(), false)
        && t.checkExpect(new ActiveWord("word", 10, 500).atBottom(), true)
        && t.checkExpect(new InactiveWord("word", 10, 0).atBottom(), false)
        && t.checkExpect(new InactiveWord("word", 10, 500).atBottom(), true);

  }

}