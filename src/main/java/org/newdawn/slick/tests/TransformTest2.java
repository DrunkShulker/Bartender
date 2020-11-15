package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;


public class TransformTest2 extends BasicGame {
   
   private float scale = 1;
   
   private boolean scaleUp;
   
   private boolean scaleDown;

   
   private float camX = 320;
   
   private float camY = 240;   
   
   private boolean moveLeft;
   
   private boolean moveUp;
   
   private boolean moveRight;
   
   private boolean moveDown;
   
   
   
   public TransformTest2() {
      super("Transform Test");
   }
   
   
   public void init(GameContainer container) throws SlickException {
      container.setTargetFrameRate(100);
   }

   
   public void render(GameContainer contiainer, Graphics g) {
      g.translate(320,240);
      
      g.translate( -camX * scale, -camY * scale);
      
            
      g.scale(scale, scale);

      g.setColor(Color.red);
      for (int x=0;x<10;x++) {
         for (int y=0;y<10;y++) {
            g.fillRect(-500+(x*100), -500+(y*100), 80, 80);
         }
      }
      
      g.setColor(new Color(1,1,1,0.5f));
      g.fillRect(-320,-240,640,480);
      g.setColor(Color.white);
      g.drawRect(-320,-240,640,480);
   }

   
   public void update(GameContainer container, int delta) {
      if (scaleUp) {
         scale += delta * 0.001f;
      }
      if (scaleDown) {
         scale -= delta * 0.001f;
      }
      
      float moveSpeed = delta * 0.4f * (1/scale);
      
      if( moveLeft ) {
         camX -= moveSpeed;
      }
      if( moveUp ) {
         camY -= moveSpeed;
      }
      if( moveRight) {
         camX += moveSpeed;
      }
      if( moveDown ) {
         camY += moveSpeed;
      }
   }

   
   public void keyPressed(int key, char c) {
      if (key == Input.KEY_ESCAPE) {
         System.exit(0);
      }
      if (key == Input.KEY_Q) {
         scaleUp = true;
      }
      if (key == Input.KEY_A) {
         scaleDown = true;
      }

      if( key == Input.KEY_LEFT) {
         moveLeft = true;
      }
      if( key == Input.KEY_UP ) {
         moveUp = true;
      }
      if( key == Input.KEY_RIGHT ) {
         moveRight = true;
      }
      if( key == Input.KEY_DOWN ) {
         moveDown = true;
      }
   }

   
   public void keyReleased(int key, char c) {
      if (key == Input.KEY_Q) {
         scaleUp = false;
      }
      if (key == Input.KEY_A) {
         scaleDown = false;
      }
      
      if( key == Input.KEY_LEFT) {
         moveLeft = false;
      }
      if( key == Input.KEY_UP ) {
         moveUp = false;
      }
      if( key == Input.KEY_RIGHT ) {
         moveRight = false;
      }
      if( key == Input.KEY_DOWN ) {
         moveDown = false;
      }
   }
   
   
   public static void main(String[] argv) {
      try {
         AppGameContainer container = new AppGameContainer(new TransformTest2());
         container.setDisplayMode(640,480,false);
         container.start();
      } catch (SlickException e) {
         e.printStackTrace();
      }
   }
} 
