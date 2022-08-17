import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class Loading extends JFrame {
    private JProgressBar progressBar;
    private JPanel basePanel;
    private JLabel loadLabel;
    private JLabel label;
    private JButton exitButton;

    public Loading() {

        setContentPane(basePanel);
        setLocation(250,150);
        setSize(820, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);//setTitle("WELCOME!");
        setVisible(true);





       // dispose();
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    public void start() {
        try{Thread.sleep(10000);}catch (InterruptedException e){}
        label.setText("Starting");
        try{Thread.sleep(2000);}catch (InterruptedException e){}
        this.dispose();
    }


}

