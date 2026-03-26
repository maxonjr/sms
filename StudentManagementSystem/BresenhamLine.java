import java.awt.*;
import javax.swing.*;
import java.util.Scanner;

public class BresenhamLine extends JPanel {
    private int x1, y1, x2, y2;

    public BresenhamLine(int x1, int y1, int x2, int y2) {
        this.x1 = x1; this.y1 = y1;
        this.x2 = x2; this.y2 = y2;
    }

    @Override
    public void paint(Graphics g) {
        // Draw axes (origin at 300,300)
        g.setColor(Color.GRAY);
        g.drawLine(300, 0, 300, 600); // Y-axis
        g.drawLine(0, 300, 600, 300); // X-axis

        // Tick marks and labels
        for (int i = -30; i <= 30; i += 5) {
            g.drawLine(300 + i*10, 295, 300 + i*10, 305);
            g.drawString(Integer.toString(i), 300 + i*10 - 10, 315);
            g.drawLine(295, 300 - i*10, 305, 300 - i*10);
            g.drawString(Integer.toString(i), 310, 300 - i*10 + 5);
        }

        // Axis labels
        g.drawString("X - Axis", 550, 315);
        g.drawString("Y - Axis", 310, 20);

        // Ideal line (blue)
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2));
        int sx1 = 300 + x1*10, sy1 = 300 - y1*10;
        int sx2 = 300 + x2*10, sy2 = 300 - y2*10;
        g2.drawLine(sx1, sy1, sx2, sy2);

        // Bresenham pixels (red dots)
        g.setColor(Color.RED);
        drawLine(g, x1, y1, x2, y2);

        // Legend
        g.setColor(Color.BLACK);
        g.drawString("Red dots = Bresenham pixels", 20, 20);
        g.drawString("Blue line = Ideal line", 20, 40);
    }

    void drawLine(Graphics g, int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1, sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;
        while (true) {
            int screenX = 300 + x1*10, screenY = 300 - y1*10;
            g.fillOval(screenX - 3, screenY - 3, 6, 6);
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2*err;
            if (e2 > -dy) { err -= dy; x1 += sx; }
            if (e2 < dx) { err += dx; y1 += sy; }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter x1 y1 x2 y2: ");
        int x1 = sc.nextInt(), y1 = sc.nextInt();
        int x2 = sc.nextInt(), y2 = sc.nextInt();
        sc.close();
        JFrame f = new JFrame("Bresenham Line");
        f.add(new BresenhamLine(x1, y1, x2, y2));
        f.setSize(650, 650);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}