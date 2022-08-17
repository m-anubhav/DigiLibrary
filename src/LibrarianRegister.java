import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class LibrarianRegister extends JFrame{
    private JTextField aadharIdTextField;
    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton signUpButton;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel confirmPasswordLabel;
    private JPanel basePanel;
    private JLabel aadharIdLabel;

    public LibrarianRegister() {

        setContentPane(basePanel);
        setTitle("Librarian Register");
        setLocation(250,200);
        setMinimumSize(new Dimension(330, 250));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(!AdminLibrarianWindow.isValidAadharId(aadharIdTextField.getText())) {
                    JOptionPane.showMessageDialog(LibrarianRegister.this, "It should be a 12 digit number", "Invalid Aadhar!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String query = "SELECT aadhar_number FROM librarian WHERE aadhar_number = '" + aadharIdTextField.getText() + "'";
                try(Connection con = ConnectToDB.getConnectToDB();
                    Statement st = con.createStatement()) {
                    ResultSet rs = st.executeQuery(query);
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(LibrarianRegister.this, "You are not registered by the ADMIN", "Login Denied", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }catch (SQLException exception) {
                    JOptionPane.showMessageDialog(LibrarianRegister.this, exception.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                String pass1 = String.valueOf(passwordField.getPassword());
                String pass2 = String.valueOf(confirmPasswordField.getPassword());
                if(pass1.equals("") || pass2.equals("") || Objects.equals(usernameTextField.getText(), "")) {
                    JOptionPane.showMessageDialog(LibrarianRegister.this, "Fields marked with (*) are required!");
                    return;
                }

                if(!pass1.equals(pass2)) {
                    JOptionPane.showMessageDialog(LibrarianRegister.this, "the two passwords do not match!");
                    return;
                }

                int option = JOptionPane.showConfirmDialog(LibrarianRegister.this, "Remember these credentials as they can't be changed if you aren't logged in\n Are you sure to proceed??", "Warning!!", JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.NO_OPTION)
                    return;

                 query = "INSERT INTO librarian_login VALUES( '" + aadharIdTextField.getText() + "' , '" + usernameTextField.getText() + "' , '" + pass1 + "' )";
                try(Connection con = ConnectToDB.getConnectToDB();
                    Statement st = con.createStatement()) {
                    st.executeUpdate(query);
                    JOptionPane.showMessageDialog(LibrarianRegister.this, "You are registered successfully!");

                    dispose();
                }catch (SQLException exception) {
                    JOptionPane.showMessageDialog(LibrarianRegister.this, exception.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        setVisible(true);
    }
}
