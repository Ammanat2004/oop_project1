import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

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

    int n = 10;
    Image[] stars = new Image[n];
    int[][] starPositions = new int[n][2];
    int[][] starVelocities = new int[n][2];

    int bbx = 0;
    int bby = 0;
    Random random = new Random();

    boolean isClick = true;
    boolean[] isClickStar = new boolean[n];

    Image img = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") + File.separator + "bg2.jpg");
    ImageIcon bombIcon = new ImageIcon(System.getProperty("user.dir") + File.separator + "bomb.gif");

    int starSize = 60;
    boolean showBomb = false; // สถานะการแสดง bomb
    Timer bombTimer; // Timer สำหรับจัดการเวลา

    public mypanel() {
        setBounds(0, 0, 1536, 863);
        setLayout(new BorderLayout());

        // สุ่มตำแหน่งและความเร็วของดาว
        for (int i = 0; i < stars.length; i++) {
            stars[i] = Toolkit.getDefaultToolkit()
                    .createImage(System.getProperty("user.dir") + File.separator + (i + 1) + ".png");

            starPositions[i][0] = random.nextInt(1200);
            starPositions[i][1] = random.nextInt(600);

            starVelocities[i][0] = random.nextInt(20) - 10;
            starVelocities[i][1] = random.nextInt(20) - 5;

            isClickStar[i] = true;
        }

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                panelMouseMove(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                bbx = e.getX();
                bby = e.getY();

                if (isClick) {
                    isClick = false;
                }

                // เสียงระเบิดระเบิด
                try {
                    String BlastSter = "meteorite.wav";
                    File file = new File(BlastSter);
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);

                    Clip blast = AudioSystem.getClip();
                    blast.open(audioStream);
                    blast.start();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                for (int i = 0; i < n; i++) {
                    if (isClickStar[i] && bbx >= starPositions[i][0] && bbx <= starPositions[i][0] + 100
                            && bby >= starPositions[i][1] && bby <= starPositions[i][1] + 100) {
                        isClickStar[i] = false;
                    }
                }

                // เริ่มแสดง bomb
                showBomb = true;

                // หยุด Timer ถ้ามีอยู่แล้ว
                if (bombTimer != null && bombTimer.isRunning()) {
                    bombTimer.stop();
                }

                // ตั้งค่า Timer ให้ซ่อน bomb หลังจาก 3 วินาที
                bombTimer = new Timer(3000, e1 -> {

                    showBomb = false; // ซ่อน bomb หลังจาก 3 วินาที

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
        bbx = e.getX() - 40;
        bby = e.getY() - 40;
        repaint();
    }

    public void checkCollision() {
        for (int i = 0; i < stars.length; i++) {
            for (int j = i + 1; j < stars.length; j++) {
                int dx = starPositions[i][0] - starPositions[j][0];
                int dy = starPositions[i][1] - starPositions[j][1];
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < starSize) {
                    playsound playSword = new playsound();
                    playSword.start();

                    int[] tempVelocity = starVelocities[i];
                    starVelocities[i] = starVelocities[j];
                    starVelocities[j] = tempVelocity;
                }
            }
        }
    }

    public void updateStarPositions() {
        checkCollision();

        for (int i = 0; i < stars.length; i++) {
            starPositions[i][0] += starVelocities[i][0];
            starPositions[i][1] += starVelocities[i][1];

            if (starPositions[i][0] < 0 || starPositions[i][0] > getWidth() - starSize) {
                starVelocities[i][0] = -starVelocities[i][0];
            }
            if (starPositions[i][1] < 0 || starPositions[i][1] > getHeight() - starSize) {
                starVelocities[i][1] = -starVelocities[i][1];
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);

        for (int i = 0; i < stars.length; i++) {
            if (isClickStar[i] && stars[i] != null) {
                int x = starPositions[i][0];
                int y = starPositions[i][1];
                g.drawImage(stars[i], x, y, starSize, starSize, this);
            }
        }

        if (showBomb) { // แสดง Bomb ถ้าต้องการ
            g.drawImage(bombIcon.getImage(), bbx - 40, bby - 40, 80, 80, this);
        }
    }
}

class playsound extends Thread {

    @Override
    public void run() {
        try {
            String FileSound = "sword.wav";
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(FileSound));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

class myThread extends Thread {
    mypanel m;

    public myThread(mypanel m) {
        this.m = m;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(20);
                m.updateStarPositions();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
