import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminLogin extends JFrame {
    private JLabel welcomeLabel;
    private JLabel usernameLabel;
    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JButton backButton;
    private JButton signInButton;
    private JPanel basePanel;
    private JLabel passwordLabel;
    private JButton homeButton;

    public AdminLogin() {
        // idk

        setTitle("Admin Login");
        setContentPane(basePanel);
        setLocation(150,150);
        setMinimumSize(new Dimension(550, 250));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //new HomeWindow();
                dispose();
            }
        });
        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String username = usernameTextField.getText();
                // made a String object from char[] using wrapper
                String password = String.valueOf(passwordField.getPassword());


                if (password.equals("admin123") && username.equals("admin")) {
                    JOptionPane.showMessageDialog(AdminLogin.this, "Signed In Successfully", "**Success**", JOptionPane.INFORMATION_MESSAGE);
                    new Admin();
                    dispose();

                } else {
                    JOptionPane.showMessageDialog(AdminLogin.this, "Bad Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                    usernameTextField.setText("");
                    passwordField.setText("");
                }
            }
        });

        setVisible(true);

    }



}
