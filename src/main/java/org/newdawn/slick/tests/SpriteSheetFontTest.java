package org.newdawn.slick.tests;

import org.newdawn.slick.*;
import org.newdawn.slick.util.Log;


public class SpriteSheetFontTest extends BasicGame {
   
   private Font font;

   
   public SpriteSheetFontTest() {
      super("SpriteSheetFont Test");
   }

   
   public void init(GameContainer container) throws SlickException {
      SpriteSheet sheet = new SpriteSheet("testdata/spriteSheetFont.png", 32, 32);
      font = new SpriteSheetFont(sheet, ' ');
   }

   
   public void render(GameContainer container, Graphics g) {
      g.setBackground(Color.gray);
      font.drawString(80, 5, "A FONT EXAMPLE", Color.red);
      font.drawString(100, 50, "A MORE COMPLETE LINE");
   }

   
   public void update(GameContainer container, int delta) throws SlickException {
   }

   
   public void keyPressed(int key, char c) {
      if (key == Input.KEY_ESCAPE) {
         System.exit(0);
      }
      if (key == Input.KEY_SPACE) {
         try {
            container.setDisplayMode(640, 480, false);
         } catch (SlickException e) {
            Log.error(e);
         }
      }
   }

   
   private static AppGameContainer container;

   
   public static void main(String[] argv) {
      try {
         container = new AppGameContainer(new SpriteSheetFontTest());
         container.setDisplayMode(800, 600, false);
         container.start();
      } catch (SlickException e) {
         e.printStackTrace();
      }
   }
}
