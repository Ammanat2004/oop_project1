import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.Random;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class myfram {
    public static void main(String[] args) {
        gameframe gf = new gameframe();
        myThread t1 = new myThread(gf.m);
        t1.start();
        gf.setVisible(true);
    }
}

class gameframe extends JFrame {
    mypanel m = new mypanel();

    public gameframe() {
        setBounds(0, 0, 1536, 863);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        add(m);
    }
}

class mypanel extends JPanel {

    Image[] stars = new Image[10]; // Array สำหรับเก็บรูปดาว
    int[][] starPositions = new int[10][4]; // เก็บตำแหน่ง x, y ของดาวแต่ละดวง
    int[][] starVelocities = new int[10][4]; // เก็บความเร็ว x, y ของดาวแต่ละดวง

    // เป้าเล็ง
    int bbx = 0;
    int bby = 0;
    Random random = new Random();

    Image img = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") + File.separator + "bg2.jpg");
    Image bomb = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") + File.separator + "bomb.gif");

    int starSize = 60; // ขนาดของดาวแต่ละดวง

    public mypanel() {
        setBounds(0, 0, 1536, 863);
        setLayout(new BorderLayout());

        // สุ่มตำแหน่งและความเร็วของดาว
        for (int i = 0; i < stars.length; i++) {
            stars[i] = Toolkit.getDefaultToolkit()
                    .createImage(System.getProperty("user.dir") + File.separator + (i + 1) + ".png");

            // สุ่มตำแหน่ง x และ y ของรูปดาว
            starPositions[i][0] = random.nextInt(1200); // ค่าระหว่าง 0 ถึง 1200
            starPositions[i][1] = random.nextInt(600); // ค่าระหว่าง 0 ถึง 600

            // สุ่มความเร็วในการขยับ x และ y (ค่าบวกหรือลบ)
            starVelocities[i][0] = random.nextInt(20) - 10;
            starVelocities[i][1] = random.nextInt(20) - 5; 
        }

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                panelMouseMove(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // Optional: Implement dragging functionality
            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Optional: Implement functionality for click
            }

            @Override
            public void mousePressed(MouseEvent e) {
                playsound play = new playsound();
                play.start();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Optional: Implement if needed
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // Optional: Implement if needed
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Optional: Implement if needed
            }
        });
    }

    private void panelMouseMove(MouseEvent e) {
        bbx = e.getX() - 40;
        bby = e.getY() - 40;
        repaint();
    }

    // ตรวจสอบการชนของดาว
    public void checkCollision() {
        for (int i = 0; i < stars.length; i++) {
            for (int j = i + 1; j < stars.length; j++) {
                int dx = starPositions[i][0] - starPositions[j][0];
                int dy = starPositions[i][1] - starPositions[j][1];
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < starSize) {
                    // เด้งกลับโดยการสลับความเร็วในแกน x และ y
                    int[] tempVelocity = starVelocities[i];
                    starVelocities[i] = starVelocities[j];
                    starVelocities[j] = tempVelocity;
                }
            }
        }
    }

    // อัปเดตตำแหน่งของดาว
    public void updateStarPositions() {
        checkCollision(); // ตรวจสอบการชนก่อนอัปเดตตำแหน่ง

        for (int i = 0; i < stars.length; i++) {
            starPositions[i][0] += starVelocities[i][0]; // ขยับตำแหน่ง x
            starPositions[i][1] += starVelocities[i][1]; // ขยับตำแหน่ง y

            // ตรวจสอบว่าดาวอยู่ภายในขอบเขตหน้าจอ ถ้าออกนอกให้เด้งกลับ
            if (starPositions[i][0] < 0 || starPositions[i][0] > getWidth() - starSize) {
                starVelocities[i][0] = -starVelocities[i][0]; // เปลี่ยนทิศทางแกน x
            }
            if (starPositions[i][1] < 0 || starPositions[i][1] > getHeight() - starSize) {
                starVelocities[i][1] = -starVelocities[i][1]; // เปลี่ยนทิศทางแกน y
            }
        }
        repaint(); // วาดหน้าจอใหม่หลังจากอัปเดตตำแหน่ง
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);

        for (int i = 0; i < stars.length; i++) {
            if (stars[i] != null) {
                int x = starPositions[i][0];
                int y = starPositions[i][1];
                g.drawImage(stars[i], x, y, starSize, starSize, this);
            }
        }
        g.drawImage(bomb, bbx, bby, this);
    }
}

class playsound extends Thread {
    @Override
    public void run() {
        while (true) { 
             try {
            File stars = new File(System.getProperty("user.dir") +
             File.separator + "stars.wav");
    
                AudioInputStream stream = AudioSystem.getAudioInputStream(stars);
                AudioFormat format = stream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(stream);
                clip.start();
                Thread.sleep(1000); // เล่นเสียง 1 วินาที
                clip.close();
          
        } catch (Exception exx) {
            exx.printStackTrace();
        }
        
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
        while (true) {
            try {
                Thread.sleep(60);
            } catch (InterruptedException exx) {
                exx.printStackTrace();
            }
            panel.updateStarPositions();
        }
    }
}
