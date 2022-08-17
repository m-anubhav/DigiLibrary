import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;


public class BookProfile extends JFrame{
    private JPanel basePanel;
    private JPanel buttonPanel;
    private  JButton deleteButton;
    private  JButton saveButton;
    private  JButton assignButton;
    private  JButton receiveButton;
    private JPanel fieldsPanel;
    private JTextField subjectTextField;
    private JTextField priceTextField;
    private JTextField pubYearTextField;
    private JTextField pubTextField;
    private JTextField authorTextField;
    private JTextField nameTextField;
    private JTextField isbnTextField;
    private JLabel isbnLabel;
    private JLabel nameLabel;
    private JLabel authorLabel;
    private JLabel pubLabel;
    private JLabel pubYearLabel;
    private JLabel subjectLabel;
    private JLabel priceLabel;
    // just displays book availability
    private JCheckBox availableCheckBox;
    private JButton backButton;
    // true -> if book present in DB, else false. If this window is created
    // using search button of "LibrarianBookWindow" then set it
    // else if created by "Add" button then unset it
    private static boolean isAlreadyPresent;
    // useful to make changes in "book_assigned" DB
    private String aadharId;
    private boolean isAdmin;


    public BookProfile() {

        setContentPane(basePanel);
        //setTitle("Librarian Book Profile");
        setLocation(250,200);
        setMinimumSize(new Dimension(600, 450));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // initially all these are disabled by default
        // As this window may be constructed using "Add" button of the "LibrarianBookWindow"
        // hence we can't assign, delete, receive book until saving it in the "books" database
        // These will be enabled as per need
        deleteButton.setEnabled(false);
        assignButton.setEnabled(false);
        receiveButton.setEnabled(false);
        setVisible(true);

        isAdmin = false;
        receiveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(!availableCheckBox.isSelected()) {

                    // first find the assigned time to perform the update
                    Time time = null;
                    try(Connection con = ConnectToDB.getConnectToDB();
                        Statement st = con.createStatement()) {
                        ResultSet rs = st.executeQuery("SELECT time, ret_time FROM books_assigned WHERE isbn = '" + isbnTextField.getText() + "'");
                         time = getAssignedTime(rs);
                    }catch (SQLException exception) {
                        JOptionPane.showMessageDialog(BookProfile.this, exception.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    String query = "UPDATE books_assigned SET ret_date =  '" + LocalDate.now() + "' , ret_time = '" + LocalTime.now() + "' , received_by = '"  + aadharId + "' WHERE time = '" + time + "'" ;
                    try(Connection con = ConnectToDB.getConnectToDB();
                        Statement st = con.createStatement()) {
                        st.executeUpdate(query);
                        try(Statement st2 = con.createStatement()) {
                            st2.executeUpdate("UPDATE books SET available = 'yes' WHERE isbn = '" + isbnTextField.getText() + "'");
                            availableCheckBox.setSelected(true);
                            assignButton.setEnabled(true);
                            receiveButton.setEnabled(false);
                            JOptionPane.showMessageDialog(BookProfile.this, "Book returned successfully");
                        }
                    }catch (SQLException exception) {
                        JOptionPane.showMessageDialog(BookProfile.this, exception.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String name,author,pub, pubYear, subject, price;
                name = nameTextField.getText();
                author = authorTextField.getText();
                pub = pubTextField.getText();
                pubYear = pubYearTextField.getText();
                subject = subjectTextField.getText();
                price = priceTextField.getText();
                if(!BookWindow.isValidISBN(isbnTextField.getText()))
                    JOptionPane.showMessageDialog(BookProfile.this, "ISBN should be a 13 digit number!");
                if(!validYear(pubYear)) {
                    JOptionPane.showMessageDialog(BookProfile.this, "Invalid publication year");
                    return;
                }

                if(name.equals("") || author.equals("") || pub.equals("") || subject.equals("") || price.equals("")) {
                    JOptionPane.showMessageDialog(BookProfile.this, "Fill ALL fields!");
                    return;
                }


                String query;
                // if book is already present in DB then make updates else insert a new record
                if(isAlreadyPresent) {
                    query = "UPDATE books " +
                            "SET " +
                            "name = '" + name +
                            "', author = '" + author +
                            "', publication = '" + pub +
                            "', publication_year = '" + pubYear+
                            "', subject = '" + subject +
                            "', price = '" + price +
                            "' WHERE isbn = '" + isbnTextField.getText() + "'";

                }
                else {

                    query = "INSERT INTO books VALUES( '" +
                            isbnTextField.getText() + "' , '" + name+ "' , '" + author + "' , '" + pub + "' , '" + pubYear + "' , '" + subject + "' , '" + price + "' , 'yes')";
                    System.out.println(query);
                    int option = JOptionPane.showConfirmDialog(BookProfile.this, "ISBN is permanent and can't be changed later.\n Are you sure to save " + isbnTextField.getText(), "Warning!", JOptionPane.YES_NO_OPTION);
                    if(option == JOptionPane.NO_OPTION)
                        return;

                }

                // connecting to the DB...
                try(Connection con = ConnectToDB.getConnectToDB();
                    Statement st = con.createStatement()) {

                    st.executeUpdate(query);
                    // after update, if newly saved then tick the checkbox as now available
                    // disable the isbn field and checkbox as well and now the book is already present.
                    //enable buttons accordingly
                    if(!isAlreadyPresent) {
                        availableCheckBox.setSelected(true);
                        isbnTextField.setEditable(false);
                        availableCheckBox.setEnabled(false);
                        isAlreadyPresent = true;
                        assignButton.setEnabled(true);
                        deleteButton.setEnabled(true);
                       // receiveButton.setEnabled(false);
                    }
                    JOptionPane.showMessageDialog(BookProfile.this, "Details Updated Successfully", "Success!",JOptionPane.INFORMATION_MESSAGE);
                }catch(SQLException exception) {
                    JOptionPane.showMessageDialog(BookProfile.this, exception.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        assignButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(availableCheckBox.isSelected()) {
                    AssignBookWindow assignBookWindow = new AssignBookWindow();
                    assignBookWindow.setIsbn(isbnTextField.getText());
                    assignBookWindow.setAadharId(aadharId);

                    // check weather the book is assigned may be cancel button invoked in between
                    String query = "SELECT available FROM books WHERE isbn = '" + isbnTextField.getText() + "'";
                    try(Connection con = ConnectToDB.getConnectToDB();
                        Statement st = con.createStatement()) {
                        ResultSet rs = st.executeQuery(query);
                        rs.next();
                        if(rs.getString(1).equals("yes")) {
                            // book assigned
                            availableCheckBox.setSelected(false);
                            assignButton.setEnabled(false);
                            receiveButton.setEnabled(true);
                        }
                    }catch (SQLException exception) {
                        JOptionPane.showMessageDialog(BookProfile.this, exception);
                    }

                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isAlreadyPresent) {
                    String query = "DELETE FROM books WHERE isbn = " + isbnTextField.getText();
                    int option = JOptionPane.showConfirmDialog(BookProfile.this, "Are you sure to delete " + isbnTextField.getText(), "Warning!, this operation is permanent", JOptionPane.YES_NO_OPTION);
                    if(option == JOptionPane.NO_OPTION)
                        return;

                    try(Connection con = ConnectToDB.getConnectToDB();
                        Statement st = con.createStatement()) {
                        st.executeUpdate(query);

                        JOptionPane.showMessageDialog(BookProfile.this, "Record deleted successfully", "Success!", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    }catch (SQLException exception) {
                        JOptionPane.showMessageDialog(BookProfile.this,exception,"Error!", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else
                    JOptionPane.showMessageDialog(BookProfile.this, "No such Book Found!", "Oops!", JOptionPane.INFORMATION_MESSAGE);

            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BookWindow bookWindow = new BookWindow();
                bookWindow.setAadharId(aadharId);
                dispose();
            }
        });
    }
    void setAlreadyPresent(boolean b) {
        isAlreadyPresent = b;
    }
    void setAadharId(String aadharId) {
        this.aadharId = aadharId;
        this.setTitle(aadharId + "'s Book Profile");
    }
    Time getAssignedTime(ResultSet rs) throws SQLException {
        Time time = null;
        while(rs.next()) {
            if(rs.getTime(2) == null) {
                time = rs.getTime(1);
                break;
            }
        }
        return time;
    }

    private boolean validYear(String y) {
        if(y.length() != 4)
            return false;
        for(int i = 0; i < 4; ++i)
            if(y.charAt(i) < '0' || y.charAt(i) > '9')
                return false;
        String currY = String.valueOf(LocalDate.now().getYear());
        for(int i = 0; i < 4; ++i) {

            if (y.charAt(i) > currY.charAt(i))
                return false;
            else if (y.charAt(i) == currY.charAt(i) && y.charAt(i+1) > currY.charAt(i+1))
                return false;

        }
        return true;

    }
    void setBookDetails(ResultSet rs) {
        try {
            // Enabling the already disabled buttons as now this has been called from
            // the search button of the "LibrarianBookWindow" and queried from the books database
            deleteButton.setEnabled(true);
            isbnTextField.setText(rs.getString(1));
            isbnTextField.setEditable(false);       // No changes in the ISBN, once saved
            nameTextField.setText(rs.getString(2));
            authorTextField.setText(rs.getString(3));
            pubTextField.setText(rs.getString(4));
            pubYearTextField.setText(rs.getString(5));
            subjectTextField.setText(rs.getString(6));
            priceTextField.setText(String.valueOf(rs.getDouble(7)));
            // availability is governed by database and the assign and receive buttons
            // checkbox just displays the availability of the book
            String available = (rs.getString(8));

            // if book is available we can't receive and if unavailable ,so we can't assign,
            // hence disabling accordingly
            if(available.equals("yes")) {
                assignButton.setEnabled(true);
                receiveButton.setEnabled(false);
                availableCheckBox.setSelected(true);
            }
            else {
                receiveButton.setEnabled(true);
                assignButton.setEnabled(false);
                availableCheckBox.setSelected(false);
            }
            availableCheckBox.setEnabled(false);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(BookProfile.this, e.toString(), "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }


}
