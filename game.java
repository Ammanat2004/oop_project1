import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class game extends JFrame {

    public static void main(String[] args) {

        game jfame = new game();
        jpanel panel = new jpanel();
        jfame.add(panel);
        jfame.setVisible(true);

        // สร้าง Timer เพื่อขยับรูปดาวทุกๆ 50 มิลลิวินาที
        Timer timer = new Timer();
        timer.schedule(new MyTimer(panel), 0, 50); // ขยับทุก 50 มิลลิวินาที
    }

    public game() {

        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class jpanel extends JPanel {

    Image[] stars = new Image[10]; // อาเรย์สำหรับเก็บรูปดาว
    Image imageBg;
    int[][] starPositions = new int[10][2]; // เก็บตำแหน่ง x, y ของดาวแต่ละดวง
    int[][] starVelocities = new int[10][2]; // เก็บความเร็ว x, y ของดาวแต่ละดวง
    Random random = new Random();

    jpanel() {
        // โหลดภาพพื้นหลัง
        imageBg = Toolkit.getDefaultToolkit()
                .createImage(System.getProperty("user.dir") + File.separator + "bg2.jpg");

        // รูปดาวทั้ง 10 รูป สุ่มตำแหน่งและความเร็ว x, y
        for (int i = 0; i < stars.length; i++) {
            stars[i] = Toolkit.getDefaultToolkit()
                    .createImage(System.getProperty("user.dir") + File.separator + (i + 1) + ".png");

            // สุ่มตำแหน่ง x และ y ของรูปดาว
            starPositions[i][0] = random.nextInt(900); // ค่าระหว่าง 0 ถึง 950
            starPositions[i][1] = random.nextInt(500); // ค่าระหว่าง 0 ถึง 550

            // สุ่มความเร็วในการขยับ x และ y (ค่าบวกหรือลบ)
            starVelocities[i][0] = random.nextInt(7) - 3; // ค่าความเร็วระหว่าง -3 ถึง 3 สำหรับแกน x
            starVelocities[i][1] = random.nextInt(7) - 3; // ค่าความเร็วระหว่าง -3 ถึง 3 สำหรับแกน y
        }
    }

    // อัปเดตตำแหน่งดาวโดยให้ขยับตามความเร็วที่กำหนดไว้
    public void updateStarPositions() {
        for (int i = 0; i < stars.length; i++) {
            starPositions[i][0] += starVelocities[i][0]; // ขยับตำแหน่ง x
            starPositions[i][1] += starVelocities[i][1]; // ขยับตำแหน่ง y

            // ตรวจสอบว่าดาวอยู่ภายในขอบเขตหน้าจอ ถ้าออกนอกให้เด้งกลับ
            if (starPositions[i][0] < 0 || starPositions[i][0] > getWidth() - 50) {
                starVelocities[i][0] = -starVelocities[i][0]; // เปลี่ยนทิศทางแกน x
            }
            if (starPositions[i][1] < 0 || starPositions[i][1] > getHeight() - 50) {
                starVelocities[i][1] = -starVelocities[i][1]; // เปลี่ยนทิศทางแกน y
            }
        }
        repaint(); // วาดหน้าจอใหม่หลังจากอัปเดตตำแหน่ง
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // วาดภาพพื้นหลัง
        if (imageBg != null) {
            g.drawImage(imageBg, 0, 0, this.getWidth(), this.getHeight(), this);
        }

        // วาดรูปดาวทั้ง 10 รูปที่ตำแหน่งปัจจุบัน
        for (int i = 0; i < stars.length; i++) {
            if (stars[i] != null) {
                int x = starPositions[i][0];
                int y = starPositions[i][1];
                g.drawImage(stars[i], x, y, 50, 50, this);
            }
        }
    }
}

class MyTimer extends TimerTask {

    jpanel panel;

    MyTimer(jpanel panel) {
        this.panel = panel;
    }

    @Override
    public void run() {
        // อัปเดตตำแหน่งดาวทุกครั้งที่ Timer ทำงาน
        panel.updateStarPositions();
    }
}
