package org.newdawn.slick;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.CursorLoader;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.util.Log;


public class AppletGameContainer extends Applet {
   
   protected ContainerPanel canvas;
   
   protected Container container;
   
   protected Canvas displayParent;
   
   protected Thread gameThread;
   
   protected boolean alphaSupport = true;
   
   
   public void destroy() {
      if (displayParent != null) {
         remove(displayParent);
      }
      super.destroy();
      
      Log.info("Clear up");
   }

   
   private void destroyLWJGL() {
      container.stopApplet();
      
      try {
         gameThread.join();
      } catch (InterruptedException e) {
         Log.error(e);
      }
   }

   
   public void start() {
      
   }
   
   
   public void startLWJGL() {
      if (gameThread != null) {
         return;
      }
      
      gameThread = new Thread() {
         public void run() {
            try {
               canvas.start();
            }
            catch (Exception e) {
               e.printStackTrace();
               if (Display.isCreated()) {
                  Display.destroy();
               }
               displayParent.setVisible(false);
               add(new ConsolePanel(e));
               validate();
            }
         }
      };
      
      gameThread.start();
   }

   
   public void stop() {
   }

   
   public void init() {
      removeAll();
      setLayout(new BorderLayout());
      setIgnoreRepaint(true);

      try {
         Game game = (Game) Class.forName(getParameter("game")).newInstance();
         
         container = new Container(game);
         canvas = new ContainerPanel(container);
         displayParent = new Canvas() {
            public final void addNotify() {
               super.addNotify();
               startLWJGL();
            }
            public final void removeNotify() {
               destroyLWJGL();
               super.removeNotify();
            }

         };

         displayParent.setSize(getWidth(), getHeight());
         add(displayParent);
         displayParent.setFocusable(true);
         displayParent.requestFocus();
         displayParent.setIgnoreRepaint(true);
         setVisible(true);
      } catch (Exception e) {
         Log.error(e);
         throw new RuntimeException("Unable to create game container");
      }
   }

   
   public GameContainer getContainer() {
      return container;
   }

   
   public class ContainerPanel {
      
      private Container container;

      
      public ContainerPanel(Container container) {
         this.container = container;
      }

      
      private void createDisplay() throws Exception {
         try {
            
            Display.create(new PixelFormat(8,8,GameContainer.stencil ? 8 : 0));
            alphaSupport = true;
         } catch (Exception e) {
            
            alphaSupport = false;
             Display.destroy();
             
            Display.create();
         }
      }
      
      
      public void start() throws Exception {
         Display.setParent(displayParent);
         Display.setVSyncEnabled(true);
         
         try {
            createDisplay();
         } catch (LWJGLException e) {
            e.printStackTrace();
            
            Thread.sleep(1000);
            createDisplay();
         }
         
         initGL();
         displayParent.requestFocus();
         container.runloop();
      }

      
      protected void initGL() {
         try {
            InternalTextureLoader.get().clear();
            SoundStore.get().clear();

            container.initApplet();
         } catch (Exception e) {
            Log.error(e);
            container.stopApplet();
         }
      }
   }

   
   public class Container extends GameContainer {
      
      public Container(Game game) {
         super(game);

         width = AppletGameContainer.this.getWidth();
         height = AppletGameContainer.this.getHeight();
      }

      
      public void initApplet() throws SlickException {
         initSystem();
         enterOrtho();

         try {
            getInput().initControllers();
         } catch (SlickException e) {
            Log.info("Controllers not available");
         } catch (Throwable e) {
            Log.info("Controllers not available");
         }

         game.init(this);
         getDelta();
      }

      
      public boolean isRunning() {
         return running;
      }

      
      public void stopApplet() {
         running = false;
      }

      
      public int getScreenHeight() {
         return 0;
      }

      
      public int getScreenWidth() {
         return 0;
      }
      
      
      public boolean supportsAlphaInBackBuffer() {
         return alphaSupport;
      }
      
      
      public boolean hasFocus() {
         return true;
      }
      
      
      public Applet getApplet() {
         return AppletGameContainer.this;
      }
      
      
      public void setIcon(String ref) throws SlickException {
         
      }

      
      public void setMouseGrabbed(boolean grabbed) {
         Mouse.setGrabbed(grabbed);
      }

	  
      public boolean isMouseGrabbed() {
    	  return Mouse.isGrabbed();
	  }
      
      
      public void setMouseCursor(String ref, int hotSpotX, int hotSpotY) throws SlickException {
         try {
            Cursor cursor = CursorLoader.get().getCursor(ref, hotSpotX, hotSpotY);
            Mouse.setNativeCursor(cursor);
         } catch (Throwable e) {
            Log.error("Failed to load and apply cursor.", e);
			throw new SlickException("Failed to set mouse cursor", e);
         }
      }

      
      private int get2Fold(int fold) {
          int ret = 2;
          while (ret < fold) {
              ret *= 2;
          }
          return ret;
      }
      
      
      public void setMouseCursor(Image image, int hotSpotX, int hotSpotY) throws SlickException {
          try {
             Image temp = new Image(get2Fold(image.getWidth()), get2Fold(image.getHeight()));
             Graphics g = temp.getGraphics();
             
             ByteBuffer buffer = BufferUtils.createByteBuffer(temp.getWidth() * temp.getHeight() * 4);
             g.drawImage(image.getFlippedCopy(false, true), 0, 0);
             g.flush();
             g.getArea(0,0,temp.getWidth(),temp.getHeight(),buffer);
             
             Cursor cursor = CursorLoader.get().getCursor(buffer, hotSpotX, hotSpotY,temp.getWidth(),temp.getHeight());
             Mouse.setNativeCursor(cursor);
          } catch (Throwable e) {
             Log.error("Failed to load and apply cursor.", e);
 			 throw new SlickException("Failed to set mouse cursor", e);
          }
       }
      
      
      public void setIcons(String[] refs) throws SlickException {
         
      }

      
      public void setMouseCursor(ImageData data, int hotSpotX, int hotSpotY) throws SlickException {
         try {
            Cursor cursor = CursorLoader.get().getCursor(data, hotSpotX, hotSpotY);
            Mouse.setNativeCursor(cursor);
         } catch (Throwable e) {
            Log.error("Failed to load and apply cursor.", e);
			throw new SlickException("Failed to set mouse cursor", e);
         }
      }

      
      public void setMouseCursor(Cursor cursor, int hotSpotX, int hotSpotY) throws SlickException {
         try {
            Mouse.setNativeCursor(cursor);
         } catch (Throwable e) {
            Log.error("Failed to load and apply cursor.", e);
			throw new SlickException("Failed to set mouse cursor", e);
         }
      }

      
      public void setDefaultMouseCursor() {
      }

      public boolean isFullscreen() {
         return Display.isFullscreen();
      }

      public void setFullscreen(boolean fullscreen) throws SlickException {
         if (fullscreen == isFullscreen()) {
            return;
         }

         try {
            if (fullscreen) {
               
               int screenWidth = Display.getDisplayMode().getWidth();
               int screenHeight = Display.getDisplayMode().getHeight();

               
               float gameAspectRatio = (float) width / height;
               float screenAspectRatio = (float) screenWidth
                     / screenHeight;

               int newWidth;
               int newHeight;

               

               if (gameAspectRatio >= screenAspectRatio) {
                  newWidth = screenWidth;
                  newHeight = (int) (height / ((float) width / screenWidth));
               } else {
                  newWidth = (int) (width / ((float) height / screenHeight));
                  newHeight = screenHeight;
               }

               
               int xoffset = (screenWidth - newWidth) / 2;
               int yoffset = (screenHeight - newHeight) / 2;

               
               GL11.glViewport(xoffset, yoffset, newWidth, newHeight);

               enterOrtho();

               
               this.getInput().setOffset(
                     -xoffset * (float) width / newWidth,
                     -yoffset * (float) height / newHeight);

               this.getInput().setScale((float) width / newWidth,
                     (float) height / newHeight);

               width = screenWidth;
               height = screenHeight;
               Display.setFullscreen(true);
            } else {
               
               this.getInput().setOffset(0, 0);
               this.getInput().setScale(1, 1);
               width = AppletGameContainer.this.getWidth();
               height = AppletGameContainer.this.getHeight();
               GL11.glViewport(0, 0, width, height);

               enterOrtho();

               Display.setFullscreen(false);
            }
         } catch (LWJGLException e) {
            Log.error(e);
         }

      }

      
      public void runloop() throws Exception {
         while (running) {
            int delta = getDelta();

            updateAndRender(delta);

            updateFPS();
            Display.update();
         }

         Display.destroy();
      }
   }
   
   
   public class ConsolePanel extends Panel {
      
      TextArea textArea = new TextArea();
      
      
      public ConsolePanel(Exception e) {
         setLayout(new BorderLayout());
         setBackground(Color.black);
         setForeground(Color.white);
         
         Font consoleFont = new Font("Arial", Font.BOLD, 14);
         
         Label slickLabel = new Label("SLICK CONSOLE", Label.CENTER);
         slickLabel.setFont(consoleFont);
         add(slickLabel, BorderLayout.PAGE_START);
         
         StringWriter sw = new StringWriter();
         e.printStackTrace(new PrintWriter(sw));
         
         textArea.setText(sw.toString());
         textArea.setEditable(false);
         add(textArea, BorderLayout.CENTER);
         
         
         add(new Panel(), BorderLayout.LINE_START);
         add(new Panel(), BorderLayout.LINE_END);
         
         Panel bottomPanel = new Panel();
         bottomPanel.setLayout(new GridLayout(0, 1));
         Label infoLabel1 = new Label("An error occured while running the applet.", Label.CENTER);
         Label infoLabel2 = new Label("Plese contact support to resolve this issue.", Label.CENTER);
         infoLabel1.setFont(consoleFont);
         infoLabel2.setFont(consoleFont);
         bottomPanel.add(infoLabel1);
         bottomPanel.add(infoLabel2);
         add(bottomPanel, BorderLayout.PAGE_END);
      }
   }
}