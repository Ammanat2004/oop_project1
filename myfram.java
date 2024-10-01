import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

// คลาส  main หลัก
public class myfram {
    public static void main(String[] args) {
        int N_Stars = 10;
        if (args.length > 0) {  
            try {
                N_Stars = Integer.parseInt(args[0]);
                if (N_Stars < 0) {
                    System.out.println("Input yourStars > 0");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: please input Numeric");
                return;
            }
        }
        gameframe gf = new gameframe(N_Stars);
        myThread t1 = new myThread(gf.m); 
        t1.start(); 
        gf.setVisible(true);
    }
}


//////// คลาส เฟรมหลัก ///////////
class gameframe extends JFrame {
    mypanel m;
    Clip backgroundMusic;
   
    public gameframe(int N_Stars) {
       
        m = new mypanel(N_Stars);

        setBounds(0, 0, 1536, 863);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setTitle("Gunner Stars");
        add(m);

      
        playBackgroundMusic("gramstart.wav");

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                stopBackgroundMusic();
            }
        });
    }
    

  
    public void playBackgroundMusic(String filepath) {
        try {
            File file = new File(filepath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
    }
}


class mypanel extends JPanel {
    int n;
    Image[] stars;
    int[][] starPositions;
    int[][] starVelocities;

    int gunnerX = 0;
    int gunnerY = 0;

    int bombx = 0;
    int bomby = 0;
    Random random = new Random();

    boolean isClick = false;
    boolean[] isClickStar;

    Image img = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") + File.separator + "bgk.jpg");
    Image bomb = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") + File.separator + "bomb.gif");
    Image gunner = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") + File.separator + "sight.gif");

    int starSize = 80;
    boolean showBomb = false;
    Timer bombTimer;
    boolean gameAnd = false;

   
    public mypanel(int N_Stars) {
        n = N_Stars;
        stars = new Image[n];
        starPositions = new int[n][2];
        starVelocities = new int[n][2];
        isClickStar = new boolean[n];
        setBounds(0, 0, 1536, 863);
        setLayout(new BorderLayout());

        
        for (int i = 0; i < n; i++) {
            stars[i] = Toolkit.getDefaultToolkit()
                .createImage(System.getProperty("user.dir") + File.separator + (i % 10 + 1) + ".png");
            isClickStar[i] = true;
            starPositions[i][0] = random.nextInt(1000);
            starPositions[i][1] = random.nextInt(500);

            int direction = random.nextInt(8);
            int speedX = random.nextInt(10) + 1;
            int speedY = random.nextInt(10) + 1;

            switch (direction) {
                case 0: starVelocities[i][0] = 0; 
                starVelocities[i][1] = -speedY; 
                break;
                case 1: starVelocities[i][0] = speedX;
                 starVelocities[i][1] = 0; 
                 break;
                case 2: starVelocities[i][0] = speedX; 
                starVelocities[i][1] = -speedY;
                 break;
                case 3: starVelocities[i][0] = 0; 
                starVelocities[i][1] = speedY; 
                break;
                case 4: starVelocities[i][0] = -speedX; 
                starVelocities[i][1] = 0; 
                break;
                case 5: starVelocities[i][0] = -speedX; 
                starVelocities[i][1] = -speedY; 
                break;
                case 6: starVelocities[i][0] = speedX; 
                starVelocities[i][1] = speedY;
                 break;
                case 7: starVelocities[i][0] = -speedX; 
                starVelocities[i][1] = speedY; 
                break;
            }
        

    
    }
     // เป้าเกมส์
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                gunnerX = e.getX();
                gunnerY = e.getY();
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                
            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

       
           @Override
           public void mousePressed(MouseEvent e) {
               if (e.getClickCount() == 2) {
                   bombx = e.getX();
                   bomby = e.getY();
                   showBomb = true;
                   repaint();
           

                   try {
                       String BlastSter = "meteorite.wav";
                       File file = new File(BlastSter);
                       AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                       Clip blast = (Clip) AudioSystem.getClip();
                       blast.open(audioStream);
                       blast.start();
                   } catch (Exception ex) {
                       ex.printStackTrace();
                   }
        
                   for (int i = 0; i < n; i++) {
                       if (isClickStar[i] && bombx >= starPositions[i][0] && bombx <= starPositions[i][0] + 100
                               && bomby >= starPositions[i][1] && bomby <= starPositions[i][1] + 100) {
                           isClickStar[i] = false;  
                           isClick = true;
                       }
                   }
        
                   boolean allStarsClicked = true;
                   for (boolean star : isClickStar) {
                       if (star) {
                           allStarsClicked = false;
                           break;
                       }
                   }
           
                   if (allStarsClicked && !gameAnd) {
                       gameAnd = true;
                       
                   }
        
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           try {
                               Thread.sleep(700);
                           } catch (InterruptedException ex) {
                               ex.printStackTrace();
                           }
                           showBomb = false;
                           isClick = false;
                           repaint();
                       }
                   }).start();
               }
           }
           
            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        
        BufferedImage Img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor hide = Toolkit.getDefaultToolkit().createCustomCursor(Img, new Point(8, 8), "blank mouse");
        setCursor(hide);
    }

public void checkCollision() {
    for (int i = 0; i < stars.length; i++) { 
        if (!isClickStar[i]) {
            continue;
        }
        for (int j = i + 1; j < stars.length; j++) { 
            if (!isClickStar[j]) {
                continue;
            }
   
            int dx = starPositions[i][0] - starPositions[j][0];
            int dy = starPositions[i][1] - starPositions[j][1];
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < starSize) {
                double overlap = starSize - distance;
                double moveX = (overlap * dx) / distance / 2;
                double moveY = (overlap * dy) / distance / 2;
                starPositions[i][0] += moveX;
                starPositions[j][0] -= moveX;
                starPositions[i][1] += moveY;
                starPositions[j][1] -= moveY;

                Random random = new Random();
                int newSpeedX_i = random.nextInt(10) + 1; 
                int newSpeedY_i = random.nextInt(10) + 1; 
                int newSpeedX_j = random.nextInt(10) + 1; 
                int newSpeedY_j = random.nextInt(10) + 1; 

            
                if (random.nextBoolean()) {
                    starVelocities[i][0] = newSpeedX_i; 
                } else {
                    starVelocities[i][0] = -newSpeedX_i;
                }
                if (random.nextBoolean()) {
                    starVelocities[i][1] = newSpeedY_i; 
                } else {
                    starVelocities[i][1] = -newSpeedY_i;
                }

                if (random.nextBoolean()) {
                    starVelocities[j][0] = newSpeedX_j; 
                } else {
                    starVelocities[j][0] = -newSpeedX_j;
                }
                if (random.nextBoolean()) {
                    starVelocities[j][1] = newSpeedY_j; 
                } else {
                    starVelocities[j][1] = -newSpeedY_j;
                }
            }
        }
    }
}
public void updateStars() {
    checkCollision();
    for (int i = 0; i < stars.length; i++) {
        if (!isClickStar[i]) {
            continue;
        }

        starPositions[i][0] += starVelocities[i][0];
        starPositions[i][1] += starVelocities[i][1];

       
        if (starPositions[i][0] < 0) {
            starPositions[i][0] = 0;  
            starVelocities[i][0] = -starVelocities[i][0];  
        } else if (starPositions[i][0] > getWidth() - starSize) {
            starPositions[i][0] = getWidth() - starSize;
            starVelocities[i][0] = -starVelocities[i][0]; 
        }

        if (starPositions[i][1] < 0) {
            starPositions[i][1] = 0;
            starVelocities[i][1] = -starVelocities[i][1]; 
        } else if (starPositions[i][1] > getHeight() - starSize) {
            starPositions[i][1] = getHeight() - starSize;
            starVelocities[i][1] = -starVelocities[i][1];  
        }
    }
    checkCollision();

    repaint();
}


@Override
protected void paintComponent(Graphics g) {
    
    g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
    
    for (int i = 0; i < n; i++) {
        if (isClickStar[i]) {
            int x = starPositions[i][0];
            int y = starPositions[i][1];
            g.drawImage(stars[i], x, y, starSize, starSize, this);
        }
    }

    if (isClick) {
        g.drawImage(bomb, bombx - 40, bomby - 40, 80, 80, this);
    }

    g.drawImage(gunner, gunnerX - 20, gunnerY - 20, 40, 40, this);

    if (gameAnd) {
        g.setFont(new Font("Arial", Font.BOLD, 72));
        g.setColor(Color.ORANGE);
        g.drawString("You Win !", getWidth() / 2 - 150, getHeight() / 2 - 50);
        
        
      
         }
    }
}

class myThread extends Thread {
    mypanel panel;
    
    public myThread(mypanel panel) {
        this.panel = panel;
    }

    @Override
    public void run() {
        while (!panel.gameAnd) { 
            try {
                Thread.sleep(25);
                panel.updateStars(); 
    
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        panel.repaint();
    }
    
}






