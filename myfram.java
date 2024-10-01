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
        if (args.length > 0) {  ///////////////ใช้ในการรับจำนวนอุกกาบาต////////////////
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
        Mytime t1 = new Mytime(); // เรียกใช้งาน Mytime
        myThread gameThread = new myThread(gf.m, t1); // ส่ง panel และ Mytime เข้าไปใน myThread
        gameThread.start();
        t1.start(); // เริ่มจับเวลา
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

        // Play background music when the game frame is opened
        playBackgroundMusic("gramstart.wav");

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                stopBackgroundMusic();
            }
        });
    }
    

    // Function to play background music
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

    // Stop background music
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
    }
}

//////////// คลาส panel ในการวาดจอลงใน frame หลัก
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

        // Randomize the initial positions and velocities of stars
       // สุ่มตำแหน่งและความเร็วเริ่มต้นของดาว
       for (int i = 0; i < n; i++) {
        stars[i] = Toolkit.getDefaultToolkit()
                .createImage(System.getProperty("user.dir") + File.separator + (i % 10 + 1) + ".png");
        isClickStar[i] = true;

        // สุ่มตำแหน่ง ภายในเฟรม
        starPositions[i][0] = random.nextInt(1000);
        starPositions[i][1] = random.nextInt(500);

        ///////สุ่มความเร็ว 8 ทิศทาง/////////////
    int direction = random.nextInt(8); // 0 to 2 for 3 directions
    int speedX = random.nextInt(10) + 1; // Random horizontal speed
    int speedY = random.nextInt(10) + 1; // Random vertical speed

    ///////////////////////การสุ่มการเคลื่อนที่ทิศทาง 8 ทิศทาง///////////////////
    switch (direction) {
        case 0: // Vertical Up
            starVelocities[i][0] = 0;      // No horizontal movement
            starVelocities[i][1] = -speedY; // Move up
            break;
        case 1: // Horizontal Right
            starVelocities[i][0] = speedX;  // Move right
            starVelocities[i][1] = 0;      // No vertical movement
            break;
        case 2: // Diagonal Top-Right
            starVelocities[i][0] = speedX;  // Move right
            starVelocities[i][1] = -speedY; // Move up
            break;
        case 3: // Vertical Down
            starVelocities[i][0] = 0;      // No horizontal movement
            starVelocities[i][1] = speedY; // Move down
            break;
        case 4: // Horizontal Left
            starVelocities[i][0] = -speedX; // Move left
            starVelocities[i][1] = 0;      // No vertical movement
            break;
        case 5: // Diagonal Top-Left
            starVelocities[i][0] = -speedX; // Move left
            starVelocities[i][1] = -speedY; // Move up
            break;
        case 6: // Diagonal Bottom-Right
            starVelocities[i][0] = speedX;  // Move right
            starVelocities[i][1] = speedY;  // Move down
            break;
        case 7: // Diagonal Bottom-Left
            starVelocities[i][0] = -speedX; // Move left
            starVelocities[i][1] = speedY;  // Move down
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

            // คลิกอุกกาบาต ยิงอุกกาบาต
           @Override
           public void mousePressed(MouseEvent e) {
               if (e.getClickCount() == 2) {
                   bombx = e.getX();
                   bomby = e.getY();
                   showBomb = true;
                   repaint();
           
                   // Play bomb sound
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
           
                   // ตรวจสอบว่าดาวถูกคลิกหรือไม่
                   for (int i = 0; i < n; i++) {
                       if (isClickStar[i] && bombx >= starPositions[i][0] && bombx <= starPositions[i][0] + 100
                               && bomby >= starPositions[i][1] && bomby <= starPositions[i][1] + 100) {
                           isClickStar[i] = false;  // ดาวถูกคลิก
                           isClick = true;
                       }
                   }
           
                   // ตรวจสอบว่าผู้เล่นคลิกดาวครบทุกดวงหรือไม่
                   boolean allStarsClicked = true;
                   for (boolean star : isClickStar) {
                       if (star) {
                           allStarsClicked = false;
                           break;
                       }
                   }
           
                   // หากคลิกครบทุกดวงแล้ว ให้หยุดเกม
                   if (allStarsClicked) {
                       gameAnd = true;
                   }
           
                   // ใช้เทรดควบคุมการยิงระเบิด
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
        
        // ซ่อนเมาส์
        BufferedImage Img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor hide = Toolkit.getDefaultToolkit().createCustomCursor(Img, new Point(8, 8), "blank mouse");
        setCursor(hide);
    }

   

// เช็คทิศทางของดวงดาว และการชนดวงดาว
public void checkCollision() {
    for (int i = 0; i < stars.length; i++) { // เมื่อยิงเสร็จจะข้ามตำแหน่งดาวที่ยิงไป
        if (!isClickStar[i]) {
            continue;
        }
        for (int j = i + 1; j < stars.length; j++) { // เมื่อยิงเสร็จจะข้ามตำแหน่งดาวที่ยิงไป
            if (!isClickStar[j]) {
                continue;
            }
            // หาระยะห่างของดวงดาว
            int dx = starPositions[i][0] - starPositions[j][0];
            int dy = starPositions[i][1] - starPositions[j][1];
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < starSize) {
                // ปรับตำแหน่งของดาวเพื่อป้องกันการติดกัน
                double overlap = starSize - distance;
                double moveX = (overlap * dx) / distance / 2;
                double moveY = (overlap * dy) / distance / 2;
                starPositions[i][0] += moveX;
                starPositions[j][0] -= moveX;
                starPositions[i][1] += moveY;
                starPositions[j][1] -= moveY;

                // สุ่มความเร็วใหม่สำหรับดาวที่ชนกัน
                Random random = new Random();
                int newSpeedX_i = random.nextInt(10) + 1; // ความเร็วใหม่สำหรับดาว i
                int newSpeedY_i = random.nextInt(10) + 1; // ความเร็วใหม่สำหรับดาว i
                int newSpeedX_j = random.nextInt(10) + 1; // ความเร็วใหม่สำหรับดาว j
                int newSpeedY_j = random.nextInt(10) + 1; // ความเร็วใหม่สำหรับดาว j
                
                // อัปเดตความเร็วของดาวที่ชนกัน
                starVelocities[i][0] = newSpeedX_i * (random.nextBoolean() ? 1 : -1); // เลือกทิศทางแบบสุ่ม
                starVelocities[i][1] = newSpeedY_i * (random.nextBoolean() ? 1 : -1); // เลือกทิศทางแบบสุ่ม
                starVelocities[j][0] = newSpeedX_j * (random.nextBoolean() ? 1 : -1); // เลือกทิศทางแบบสุ่ม
                starVelocities[j][1] = newSpeedY_j * (random.nextBoolean() ? 1 : -1); // เลือกทิศทางแบบสุ่ม
            }
        }
    }
}


// //////////// อัพเดต ตำแหน่งความเร็วต่างๆ ของดวงดาว
public void updateStars() {
    for (int i = 0; i < stars.length; i++) {
        if (!isClickStar[i]) {
            continue;
        }

        // อัพเดตตำแหน่งของดาว
        starPositions[i][0] += starVelocities[i][0];
        starPositions[i][1] += starVelocities[i][1];

        // Bounce off the edges
        if (starPositions[i][0] < 0) {
            starPositions[i][0] = 0;  // Prevent getting stuck at the edge
            starVelocities[i][0] = -starVelocities[i][0];  // Reflect horizontally
        } else if (starPositions[i][0] > getWidth() - starSize) {
            starPositions[i][0] = getWidth() - starSize;
            starVelocities[i][0] = -starVelocities[i][0];  // Reflect horizontally
        }


        // อัพเดตความเร็ว
        if (starPositions[i][1] < 0) {
            starPositions[i][1] = 0;
            starVelocities[i][1] = -starVelocities[i][1];  // Reflect vertically
        } else if (starPositions[i][1] > getHeight() - starSize) {
            starPositions[i][1] = getHeight() - starSize;
            starVelocities[i][1] = -starVelocities[i][1];  // Reflect vertically
        }
    }
    checkCollision();

    repaint();
}

// ใช้วาด ภาพต่างๆลงใน เฟรม ลงในโปรแกรม
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

    // แสดงข้อความ "Game Over" เมื่อผู้เล่นคลิกดาวครบทุกดวง
    if (gameAnd) {
        g.setFont(new Font("Arial", Font.BOLD, 72));
        g.setColor(Color.ORANGE);
        g.drawString("! Victory ! ", getWidth() / 2 - 150, getHeight() / 2);
    }
}

}
class Mytime extends Thread {
    boolean running = true;
    int milisec = 0;
    int second = 0;
    int minute = 0;
    int hour = 0;
    mypanel p;
  

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1);
                milisec++;

                if (milisec == 1000) {
                    second++;
                    milisec = 0;
                }
                if (second == 60) {
                    minute++;
                    second = 0;
                }
                if (minute == 60) {
                    hour++;
                    minute = 0;
                }

            
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void getStopTime() {
        running = false;
    }

    public String Return() {
        return ("Time : " + hour + " : " + minute + " : " + second + " : " + milisec);
    }
}

    
   

// เทรดควบคุม การทำงานต่างๆ ใน โปรแกรม ควบคุมการเคลื่อนที่ของดาว
class myThread extends Thread {
    mypanel panel;
    Mytime time; // สร้างตัวแปรเพื่อเก็บ instance ของ MyTime

    public myThread(mypanel panel, Mytime time) {
        this.panel = panel;
        this.time = time;
    }

    @Override
    public void run() {
        while (!panel.gameAnd) {  // เกมยังไม่จบ
            try {
                Thread.sleep(20);
                panel.updateStars();  // อัปเดตตำแหน่งของดาว
    
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    
        // เมื่อเกมจบ ให้ repaint เพื่อแสดงข้อความ "Game Over"
        panel.repaint();
    }
    
}
