import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class myfram {
    public static void main(String[] args) {
        int numberOfStars = 20; 
        if (args.length > 0) {
            try {
                
                numberOfStars = Integer.parseInt(args[0]);
                if (numberOfStars < 0) {
                    System.out.println("Input yourStars > 0");
                    return; 
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: please input Numeric");
                return; 
            }
        }
        gameframe gf = new gameframe(numberOfStars);
        myThread t1 = new myThread(gf.m);
        t1.start();
        gf.setVisible(true);
    }
}

class gameframe extends JFrame {
   
     mypanel m ; 
    public gameframe(int numberOfStars) {
       m = new mypanel(numberOfStars);
        setBounds(0, 0, 1536, 863);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        add(m);
    }
}

class mypanel extends JPanel {
    int n ;
    Image[] stars ; // Array สำหรับเก็บรูปดาว
    int[][] starPositions ; // เก็บตำแหน่ง x, y ของดาวแต่ละดวง
    int[][] starVelocities ; // เก็บความเร็ว x, y ของดาวแต่ละดวง

    // เป้าเล็ง
       int bombx = 0;
    int bomby = 0;
    Random random = new Random();

    boolean isClick = true;
    boolean[] isClickStar ;

    Image img = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") + File.separator + "bg2.jpg");
    Image bomb = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") + File.separator + "bomb.gif");
    
    Image gunner = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir")+ File.separator + "Gunleng.png");

    int starSize = 80;
    boolean showBomb = false; // สถานะการแสดง bomb
    Timer bombTimer; // Timer สำหรับจัดการเวลา
    
    public mypanel(int numberOfStars) {
        this.n = numberOfStars;
        stars = new Image[n];
        starPositions = new int[n][2];
        starVelocities = new int[n][2];
        isClickStar = new boolean[n];
        setBounds(0, 0, 1536, 863);
        setLayout(new BorderLayout());

        // สุ่มตำแหน่งและความเร็วของดาว
        for (int i = 0; i < n; i++) {
            stars[i] = Toolkit.getDefaultToolkit()
                    .createImage(System.getProperty("user.dir") + File.separator + (i % 10 + 1) + ".png");
            isClickStar[i] = true;
            // สุ่มตำแหน่ง x และ y ของรูปดาว
            starPositions[i][0] = random.nextInt(1200); // ค่าระหว่าง 0 ถึง 1200
            starPositions[i][1] = random.nextInt(600); // ค่าระหว่าง 0 ถึง 600

            // สุ่มความเร็วในการขยับ x และ y (ค่าบวกหรือลบ)
            starVelocities[i][0] = random.nextInt(10) + 1;
            starVelocities[i][1] = random.nextInt(10) + 1; 
        }

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // Optional: Implement dragging functionality
            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                bombx = e.getX();
                bomby = e.getY();

                
                isClick = false;
                

                // เสียงระเบิดระเบิด
                try {
                    String BlastSter = "meteorite.wav";
                    File file = new File(BlastSter);
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);

                    Clip blast =(Clip) AudioSystem.getClip();
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

                // เริ่มแสดง bomb
                showBomb = true;

                // หยุด Timer ถ้ามีอยู่แล้ว
                if (bombTimer != null && bombTimer.isRunning()) {
                    bombTimer.stop();
                }

                // ตั้งค่า Timer ให้ซ่อน bomb หลังจาก 1 วินาที
                bombTimer= new Timer(500, new ActionListener() {
                    
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        isClick = false;
                        repaint();
                    }
                });

                bombTimer.setRepeats(false); // ทำให้ Timer ไม่ทำงานซ้ำ
                bombTimer.start(); // เริ่ม Timer

                // ตรวจสอบว่าดาวทั้งหมดถูกคลิกแล้วหรือไม่
                boolean AllStarsClicked = true;

                for (boolean clicked : isClickStar) {
                    if (clicked) {
                        AllStarsClicked = false;
                        break;
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
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
    }


    private void panelMouseMove(MouseEvent e) {
        bombx = e.getX() - 40;
        bomby = e.getY() - 40;
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

        for (int i = 0; i < n; i++) {
            if (isClickStar[i]) {
                int x = starPositions[i][0];
                int y = starPositions[i][1];
                g.drawImage(stars[i], x, y, starSize, starSize, this);
            }
        }
        if (isClick ) {
             g.drawImage(bomb, bombx - 40, bomby - 40,80,80, this);
        }
       
    }
}

class playsound extends Thread {

    @Override
    public void run() {
        try {
            String FileSound = "sword.wav";
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(FileSound));
            Clip clip = ( Clip) AudioSystem.getClip();
            clip.open(audio);
       
            clip.start();
        } catch (Exception e) {
            System.out.println(e);
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
                Thread.sleep(100);
                panel.updateStarPositions();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
