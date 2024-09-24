import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Gram extends JFrame {
    Gram() {
        setSize(1000, 563);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        Gram gram = new Gram();
        MypPanel mPanel = new MypPanel();
        mPanel.addMouseMotionListener(mPanel);
        mPanel.addMouseListener(mPanel);
        gram.add(mPanel);
        gram.setVisible(true);
    }
}

class MypPanel extends JPanel implements MouseMotionListener, MouseListener {

    Image imagebg = Toolkit.getDefaultToolkit()
            .createImage(System.getProperty("user.dir") + File.separator + "Back\\background.jpg");
    Image imageghost = Toolkit.getDefaultToolkit()
            .createImage(System.getProperty("user.dir") + File.separator + "Back\\ghost.png");
    Image imagegun = Toolkit.getDefaultToolkit()
            .createImage(System.getProperty("user.dir") + File.separator + "Back\\sight.gif");

    Random random = new Random();
    int x = 0;
    int y = 0;
    int getclickX = 0;
    int getclickY = 0;
    int n = 10;
    boolean isclick = true;
    boolean[] isclickghost = new boolean[n];
    int[] gx = new int[n];
    int[] gy = new int[n];
    boolean isgrame = false;

    Mytime tMytime;

    MypPanel() {
        setBounds(0, 0, 1000, 563);
        for (int i = 0; i < n; i++) {
            gx[i] = random.nextInt(800);
            gy[i] = random.nextInt(400);
            isclickghost[i] = true;
        }

        tMytime = new Mytime();
        tMytime.start();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < n; i++) {
                    gx[i] = gx[i] + random.nextInt(21) - 10; // Update x-coordinate
                    gy[i] = gy[i] + random.nextInt(21) - 10; // Update y-coordinate independently

                    if (gx[i] < 0) {
                        gx[i] = 0;
                    }
                    if (gx[i] > getWidth() - 100) {
                        gx[i] = getWidth() - 100;
                    }
                    if (gy[i] < 0) {
                        gy[i] = 0;
                    }
                    if (gy[i] > getHeight() - 100) {
                        gy[i] = getHeight() - 100;
                    }
                }
                repaint();
            }
        }, 0, 20);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(imagebg, 0, 0, this);
        g.setColor(Color.WHITE);
        Font font = new Font("Bodoni MT Black", Font.BOLD, 30);
        g.setFont(font);
        g.drawString("Gun Ghost", 770, 50);
        g.drawRect(735, 15, 230, 50);

        for (int i = 0; i < n; i++) {
            if (isclickghost[i]) {
                g.drawImage(imageghost, gx[i], gy[i], this);
            }
        }
        if (!isclick) {
            g.setColor(Color.GREEN);
            g.drawLine(getclickX, getclickY, 500, 563);
        }

        g.drawImage(imagegun, x, y, this);
        if (isgrame) {
            g.setColor(Color.RED);
            g.drawString("Time" + tMytime.getTime(), 450, 250);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX() - 50;
        y = e.getY() - 50;
        isclick = true;
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        getclickX = e.getX();
        getclickY = e.getY();
        if (isclick) {
            isclick = false;
        }
        Autojun();

        for (int i = 0; i < n; i++) {
            if (isclickghost[i] && getclickX >= gx[i] && getclickX <= gx[i] + 100 && getclickY >= gy[i]
                    && getclickY <= gy[i] + 100) {
                isclickghost[i] = false;
            }
        }

        boolean allgohst = true;
        for (boolean click : isclickghost) {
            if (click) {
                allgohst = false;
                break;
            }
        }
        if (allgohst && !isgrame) {
            isgrame = true;
            tMytime.stopWatch();
            repaint();
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

    public void Autojun() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File gun;
                    AudioInputStream stream;
                    AudioFormat format;
                    DataLine.Info info;
                    Clip clip;
                    File file = new File(System.getProperty("user.dir") + File.separator + "Back\\gun.wav");
                    stream = AudioSystem.getAudioInputStream(file);
                    format = stream.getFormat();
                    info = new DataLine.Info(Clip.class, format);
                    clip = (Clip) AudioSystem.getLine(info);
                    clip.open(stream);
                    clip.start();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();
    }
}

class Mytime extends Thread {
    int mis = 0;
    int se = 0;
    int min = 0;
    int hr = 0;
    boolean running = true;

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1);
                mis++;
                if (mis == 1000) {
                    se++;
                    mis = 0;
                }
                if (se == 60) {
                    min++;
                    se = 0;
                }
                if (min == 60) {
                    hr++;
                    min = 0;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopWatch() {
        running = false;
    }

    public String getTime() {
        return ("H : " + hr + " M : " + min + " S : " + se + " Ms : " + mis);
    }
}
