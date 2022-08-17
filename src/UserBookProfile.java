import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserBookProfile extends JFrame{
    private JTextField priceTextField;
    private JTextField pubYearTextField;
    private JTextField subjectTextField;
    private JTextField pubTextField;
    private JTextField authorTextField;
    private JTextField nameTextField;
    private JTextField isbnTextField;
    private JLabel isbnLabel;
    private JLabel nameLabel;
    private JLabel label;
    private JCheckBox availableCheckBox;
    private JPanel basePanel;

    public UserBookProfile() {
        setContentPane(basePanel);
        setTitle("User's Book Profile");
        setLocation(250,200);
        setMinimumSize(new Dimension(600, 450));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

    }
    void setBookDetails(ResultSet rs) {
        try {
            isbnTextField.setText(rs.getString(1));
            nameTextField.setText(rs.getString(2));
            authorTextField.setText(rs.getString(3));
            pubTextField.setText(rs.getString(4));
            pubYearTextField.setText(rs.getString(5));
            subjectTextField.setText(rs.getString(6));
            priceTextField.setText(String.valueOf(rs.getDouble(7)));

            String available = (rs.getString(8));

            // if book is available we can't receive and if unavailable ,so we can't assign,
            // hence disabling accordingly
            if(available.equals("yes"))
                availableCheckBox.setSelected(true);
            else
                availableCheckBox.setSelected(false);

            availableCheckBox.setEnabled(false);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(UserBookProfile.this, e.toString(), "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }
}
