import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class Home extends JFrame{
    private JPanel basePanel;
    private JLabel backLabel;
    private JButton adminLoginButton;
    private JButton librarianLoginButton;
    private JButton userPortalButton;
    private JLabel titleLabel;

    public Home() {

        setContentPane(basePanel);
        setTitle("BookShare: Library Management Application");
        setLocation(250,50);
        setMinimumSize(new Dimension(900, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        adminLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminLogin adminLogin = new AdminLogin();
            }
        });
        userPortalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserBookWindow userBookWindow = new UserBookWindow();
            }
        });
        librarianLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LibrarianLogin librarianLogin = new LibrarianLogin();
            }
        });
    }

    public static void main(String[] args) {



        Loading loading = new Loading();
        loading.start();
        new Home();

    }
}
