import javax.swing.*;
import javax.swing.plaf.nimbus.State;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LibrarianProfile extends JFrame {
    private JPanel basePanel;
    private JPanel buttonPanel;
    private JTextField aadharIdTextField;
    private JTextField nameTextField;
    private JTextField dobTextField;
    private JTextField genderTextField;
    private JTextField contactNumberTextField;
    private JTextField emailTextField;
    private JTextField pinTextField;
    private JTextField cityTextField;
    private JTextField stateTextField;
    private JTextField addressTextField;
    private JTextField joinDateTextField;
    private JTextField salaryTextField;
    private JButton editButton;
    private JButton saveButton;
    private JButton cancelButton;
    private JTextField usernameTextField;
    private JTextField passwordTextField;

    public LibrarianProfile(){

        setContentPane(basePanel);
        setTitle("Librarian Profile");
        setLocation(250,200);
        setMinimumSize(new Dimension(600, 450));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usernameTextField.setEditable(true);
                passwordTextField.setEditable(true);
            }
        });
        setVisible(true);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usernameTextField.setEditable(false);
                passwordTextField.setEditable(false);

                String username = usernameTextField.getText();
                String password = passwordTextField.getText();

                String query = "UPDATE librarian_login " +
                                    "SET username = '" + username + "' ,password = '" + password +
                                     "' where aadhar_number = '" + aadharIdTextField.getText() + "'";

                try(Connection con = ConnectToDB.getConnectToDB();
                    Statement st = con.createStatement()) {
                    st.executeUpdate(query);
                    JOptionPane.showMessageDialog(LibrarianProfile.this, "Credentials updated!");
                }catch (SQLException exception) {
                    JOptionPane.showMessageDialog(LibrarianProfile.this, exception.toString(), "Error!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }


    void setLibrarianDetails(ResultSet rs) {

        try {
            aadharIdTextField.setText(rs.getString(1));
            nameTextField.setText(rs.getString(2));
            dobTextField.setText(rs.getDate(3).toString());
            genderTextField.setText(rs.getString(4));
            contactNumberTextField.setText(rs.getString(5));
            emailTextField.setText(rs.getString(6));
            pinTextField.setText(rs.getString(7));
            cityTextField.setText(rs.getString(8));
            stateTextField.setText(rs.getString(9));
            addressTextField.setText(rs.getString(10));
            joinDateTextField.setText(rs.getDate(11).toString());
            salaryTextField.setText(String.valueOf(rs.getDouble(12)));
            usernameTextField.setText(rs.getString(14));
            passwordTextField.setText(rs.getString(15));
            usernameTextField.setEditable(false);
            passwordTextField.setEditable(false);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(LibrarianProfile.this, e.toString(), "Error!", JOptionPane.ERROR_MESSAGE);
        }

    }
}
