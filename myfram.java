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

public class myfram {
    public static void main(String[] args) {
        int N_Stars = 4;
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
    boolean gameWon = false;  
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

        // สุ่มตำแหน่ง
        starPositions[i][0] = random.nextInt(1000);
        starPositions[i][1] = random.nextInt(500);

    int direction = random.nextInt(3); // 0 to 2 for 3 directions
    int speedX = random.nextInt(10) + 1; // Random horizontal speed
    int speedY = random.nextInt(10) + 1; // Random vertical speed

    switch (direction) {
        case 0: // Vertical
            starVelocities[i][0] = 0; // No horizontal movement
            starVelocities[i][1] = speedY; // Vertical movement
            break;
        case 1: // Horizontal
            starVelocities[i][0] = speedX;  // Horizontal movement
            starVelocities[i][1] = 0; // No vertical movement
            break;
        case 2: // Diagonal
            starVelocities[i][0] = speedX;  // Horizontal movement
            starVelocities[i][1] = speedY;  // Vertical movement
            break;
    }
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
    if (gameWon) return;  // Do nothing if the game is already won

    if (e.getClickCount() == 2) {
        bombx = e.getX();
        bomby = e.getY();
        showBomb = true;  // Show bomb
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

        // Check if bomb hits a star
        for (int i = 0; i < n; i++) {
            if (isClickStar[i] && bombx >= starPositions[i][0] && bombx <= starPositions[i][0] + 100
                    && bomby >= starPositions[i][1] && bomby <= starPositions[i][1] + 100) {
                isClickStar[i] = false;
                isClick = true;
            }
        }

        // Use a thread to hide the bomb after 700 milliseconds
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(700);  // Wait for 700 milliseconds
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                showBomb = false;
                isClick = false;
                repaint();
            }
        }).start();
        
        // Check if the game is won after each click
        checkWinCondition();
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
        
        // Hide cursor
        BufferedImage Img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor hide = Toolkit.getDefaultToolkit().createCustomCursor(Img, new Point(8, 8), "blank mouse");
        setCursor(hide);
    }

    // Method to check if the game is won
    public void checkWinCondition() {
        for (boolean clicked : isClickStar) {
            if (clicked) {
                return;  // If any star is still not clicked, return
            }
        }
        gameWon = true;  // If all stars are clicked, game is won
        stopGame();
    }

    // Stop the game and show a "You Win!" screen
    public void stopGame () {
        // Optionally stop the background music here
        repaint();
        
        // Delay and exit game or reset
        new Timer(700, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);  // Exit the game after 3 seconds
            }
        }).start();
    }

    // Collision detection between stars
   // Collision detection between stars
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
                // ปรับตำแหน่งของดาวเพื่อป้องกันการติดกัน
                double overlap = starSize - distance;
                double moveX = (overlap * dx) / distance / 2;
                double moveY = (overlap * dy) / distance / 2;
                starPositions[i][0] += moveX;
                starPositions[j][0] -= moveX;
                starPositions[i][1] += moveY;
                starPositions[j][1] -= moveY;

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
    checkCollision();
    
    if (gameWon) {
        return;  // Stop updating stars if the game is won
    }

    for (int i = 0; i < stars.length; i++) {
        if (!isClickStar[i]) {
            continue;
        }

        // Update the position of each star
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

        if (starPositions[i][1] < 0) {
            starPositions[i][1] = 0;
            starVelocities[i][1] = -starVelocities[i][1];  // Reflect vertically
        } else if (starPositions[i][1] > getHeight() - starSize) {
            starPositions[i][1] = getHeight() - starSize;
            starVelocities[i][1] = -starVelocities[i][1];  // Reflect vertically
        }
    }

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

        g.drawImage(gunner, gunnerX - 20, gunnerY - 20, 40, 40, this);

        // Display "You Win" message if game is won
        if (gameWon) {
            g.setFont(new Font("Mali", Font.BOLD, 72));
            g.setColor(Color.orange);
            g.drawString("YOU WIN!", getWidth() / 2 - 125 , getHeight() / 2);
            
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
                Thread.sleep(20);
                panel.updateStars();

            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
