import java.awt.BorderLayout;
import java.awt.Cursor;
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

public class myfram {
    public static void main(String[] args) {
        int numberOfStars = 10;
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

    mypanel m;
    Clip backgroundMusic;

    public gameframe(int numberOfStars) {
        m = new mypanel(numberOfStars);
        setBounds(0, 0, 1536, 863);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
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

    Image img = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") + File.separator + "bg2.jpg");
    Image bomb = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") + File.separator + "bomb.gif");
    Image gunner = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") + File.separator + "sight.gif");

    int starSize = 80;
    boolean showBomb = false;
    Timer bombTimer;

    public mypanel(int numberOfStars) {
        this.n = numberOfStars;
        stars = new Image[n];
        starPositions = new int[n][2];
        starVelocities = new int[n][2];
        isClickStar = new boolean[n];
        setBounds(0, 0, 1536, 863);
        setLayout(new BorderLayout());

        // Randomize the initial positions and velocities of stars
        for (int i = 0; i < n; i++) {
            stars[i] = Toolkit.getDefaultToolkit()
                    .createImage(System.getProperty("user.dir") + File.separator + (i % 10 + 1) + ".png");
            isClickStar[i] = true;

            //สุ่มตำแหน่ง
            starPositions[i][0] = random.nextInt(1200);
            starPositions[i][1] = random.nextInt(600);

            //สุ่มความเร็ว
            starVelocities[i][0] = random.nextInt(20) + 1;
            starVelocities[i][1] = random.nextInt(20) + 1;
        }

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                gunnerX = e.getX();
                gunnerY = e.getY();
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // Optional: Implement dragging functionality
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
                    showBomb = true;  // แสดง bomb
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

                for (int i = 0; i < n; i++) {
                    if (isClickStar[i] && bombx >= starPositions[i][0] && bombx <= starPositions[i][0] + 100
                            && bomby >= starPositions[i][1] && bomby <= starPositions[i][1] + 100) {
                        isClickStar[i] = false;
                        isClick = true;
                    }
                }

                // Show bomb effect and set a timer to hide it
                showBomb = true;

                if (bombTimer != null && bombTimer.isRunning()) {
                    bombTimer.stop();
                }

                bombTimer = new Timer(700, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showBomb = false;
                        isClick = false;
                    }
                });

                bombTimer.setRepeats(false);
                bombTimer.start();
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

    

    // Collision detection between stars
    public void checkCollision() {
        for (int i = 0; i < stars.length; i++) {
            for (int j = i + 1; j < stars.length; j++) {
                int dx = starPositions[i][0] - starPositions[j][0];
                int dy = starPositions[i][1] - starPositions[j][1];
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < starSize) {
                    // Swap velocities for elastic collision
                    int[] tempVelocity = starVelocities[i];
                    starVelocities[i] = starVelocities[j];
                    starVelocities[j] = tempVelocity;
                }
            }
        }
    }

    // Update the stars' positions and handle boundary collisions
    public void updateStars() {
        for (int i = 0; i < stars.length; i++) {
            starPositions[i][0] += starVelocities[i][0];
            starPositions[i][1] += starVelocities[i][1];

            // Bounce off the edges
            if (starPositions[i][0] <= 0 || starPositions[i][0] >= getWidth() - starSize) {
                starVelocities[i][0] = -starVelocities[i][0];
            }

            if (starPositions[i][1] <= 0 || starPositions[i][1] >= getHeight() - starSize) {
                starVelocities[i][1] = -starVelocities[i][1];
            }
        }
        checkCollision();
        repaint();
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

        if (isClick) {
            g.drawImage(bomb, bombx - 40, bomby - 40, 80, 80, this);
        }

        g.drawImage(gunner, gunnerX - 10, gunnerY - 10, 20, 20, this);
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
                Thread.sleep(80);
                panel.updateStars();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
