import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserBookWindow extends JFrame{
    private JPanel basePanel;
    private JTabbedPane tabbedPane1;
    private JPanel topPanel;
    private JScrollPane leftPanel;
    private JPanel middlePanel;
    private JRadioButton ISBNRadioButton;
    private JRadioButton nameRadioButton;
    private JTextField searchTextField;
    private JButton viewAllButton;
    private JButton searchButton;
    private JLabel iconLabel;
    private JButton backButton;
    private JButton filterSearchButton;
    private JPanel filterPanel;
    private JTextField subjectTextField;
    private JLabel subjectLabel;
    private JTextField pubTextField;
    private JTextField authorTextField;
    private JCheckBox availableCheckBox;
    private JLabel authorLabel;
    private JLabel publicationLabel;

    public UserBookWindow() {

        setContentPane(basePanel);
        setTitle(" User's Books Section");
        setLocation(150,150);
        setMinimumSize(new Dimension(747, 517));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        viewAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = "select * from books ORDER BY name";
                try(Connection con = ConnectToDB.getConnectToDB();
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(query)) {
                    // display in tabular format

                    BookTable bookTable = new BookTable();
                    bookTable.showTable(rs);

                }catch (SQLException exception) {
                    JOptionPane.showMessageDialog(UserBookWindow.this ,exception.toString(),"Error!",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(ISBNRadioButton.isSelected()) {
                    if(BookWindow.isValidISBN(searchTextField.getText())) {
                        getBookDetails(searchTextField.getText());
                    }
                    else
                        JOptionPane.showMessageDialog(UserBookWindow.this, "ISBN should be a 13 digit number");
                }
                else if(nameRadioButton.isSelected()) {
                    if(!searchByName(searchTextField.getText()))
                        JOptionPane.showMessageDialog(UserBookWindow.this, "No record found having name as " + searchTextField.getText());
                }
                else
                    JOptionPane.showMessageDialog(UserBookWindow.this, "Please select one the radio buttons");
            }

        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        filterSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String author = authorTextField.getText(), pub = pubTextField.getText(), subject = subjectTextField.getText();

                String query;
                if(!author.equals(""))
                    author = "LOWER('" + author + "')";
                if(!pub.equals(""))
                    pub = "LOWER('" + pub + "')";
                if(!subject.equals(""))
                    subject = "LOWER('" + subject + "')";

                if(author.equals("") && pub.equals("") && subject.equals(""))
                    query = "SELECT * FROM books";
                else if(author.equals("")) {
                    if(pub.equals(""))
                        query = "SELECT * FROM books WHERE LOWER(subject) = " + subject;
                    else if(subject.equals(""))
                        query = "SELECT * FROM books WHERE LOWER(publication) = " +  pub;
                    else {
                        query = "SELECT * FROM books WHERE LOWER(subject) = " + subject +  " AND LOWER(publication) = " + pub;
                    }
                }
                else  if(pub.equals("")){
                    if(subject.equals(""))
                        query = "SELECT * FROM books WHERE LOWER(author) = " + author;
                    else
                        query = "SELECT * FROM books WHERE LOWER(author) = " + author + " AND LOWER(subject) = " + subject;
                }
                else {
                    if(subject.equals(""))
                        query = "SELECT * FROM books WHERE LOWER(publication) = " + pub + " AND LOWER(author) = " + author;
                    else
                        query = "SELECT * FROM books WHERE LOWER(publication) = " + pub + " AND LOWER(author) = " + author + " AND subject = " + subject;
                }

                if(availableCheckBox.isSelected())
                    query += " AND available = 'yes'";

                try(Connection con = ConnectToDB.getConnectToDB();
                    Statement st = con.createStatement()) {
                    ResultSet rs = st.executeQuery(query + "ORDER BY name");
                    BookTable bookTable = new BookTable();
                    bookTable.setFilter(true);
                    bookTable.setQuery(query);
                    bookTable.showTable(rs);

                }catch (SQLException exception) {
                    JOptionPane.showMessageDialog(UserBookWindow.this ,exception.toString(),"Error!",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private boolean searchByName(String name) {
        String query = "SELECT * FROM books WHERE LOWER(name) = LOWER('" + name  + "') ORDER BY name";
        try(Connection con = ConnectToDB.getConnectToDB();
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            ResultSet rs = st.executeQuery(query);

            if(!rs.next())
                return false;

            if(!rs.next()) {
                rs.previous();
                UserBookProfile userBookProfile = new UserBookProfile();
                userBookProfile.setBookDetails(rs);
            }
            else {
                rs.previous();rs.previous();
                BookTable bookTable = new BookTable();
                bookTable.showTable(rs);
            }
        }catch (SQLException e) {
            JOptionPane.showMessageDialog(UserBookWindow.this ,e.toString(),"Error!",JOptionPane.ERROR_MESSAGE);
        }

        return true;
    }

    void getBookDetails(String isbn) {
        // check Database if found -> display, else -> not Exist

        String query = "select * from books where isbn = " + isbn;

        // used try with resources
        try(Connection con = ConnectToDB.getConnectToDB();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query)) {
            /*
              The new driver class is `com.mysql.cj.jdbc.Driver'.
              The driver is automatically registered via the SPI
              and manual loading of the driver class is generally unnecessary.
             */
            // Class.forName("com.mysql.jdbc.Driver");

            if(!rs.next())
                JOptionPane.showMessageDialog(UserBookWindow.this, "No such Book Found!", "Oops!", JOptionPane.INFORMATION_MESSAGE);
            else {
                // open the profile window
                UserBookProfile userBookProfile = new UserBookProfile();
                userBookProfile.setBookDetails(rs);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(UserBookWindow.this ,e.toString(),"Error!",JOptionPane.ERROR_MESSAGE);
        }
    }
}
