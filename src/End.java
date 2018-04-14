import javax.swing.*;
import java.awt.*;

public class End extends JFrame {

    private String msg;

    public End(String msg){

        super("BattleShip");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);

        this.msg = msg;

    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 64));
        if(msg.equals("Won"))
            g.setColor(Color.GREEN);
        g.drawString("You " + msg, 110, 260);
    }
}
