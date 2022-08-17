import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BookWindow extends JFrame{
    private JPanel basePanel;
    private JPanel searchPanel;
    private JRadioButton isbnRadioButton;
    private JRadioButton nameRadioButton;
    private JTextField searchTextField;
    private JButton searchButton;
    private JLabel infoLabel;
    private JButton viewAllButton;
    private JButton addButton;
    private JTabbedPane tabbedPane1;
    private JButton backButton;
    private JLabel iconLabel;
    private JLabel authorLabel;
    private JTextField authorTextField;
    private JTextField pubTextField;
    private JButton filterSearchButton;
    private JButton resetButton;
    private JTextField subjectTextField;
    private JLabel subjectLabel;
    private JButton booksAssignedButton;
    private String aadharId,libName;
    private boolean isAdmin;

    public BookWindow() {

        setContentPane(basePanel);
        //set according to admin or books
        //setTitle("Books' Section");
        setLocation(150,150);
        setMinimumSize(new Dimension(600, 450));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        isAdmin = false;

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isbnRadioButton.isSelected()) {
                    if(isValidISBN(searchTextField.getText())) {
                        getBookDetails(searchTextField.getText());
                    }
                    else
                        JOptionPane.showMessageDialog(BookWindow.this, "ISBN should be a 13 digit number");
                }
                else if(nameRadioButton.isSelected()) {
                    if(!searchByName(searchTextField.getText()))
                        JOptionPane.showMessageDialog(BookWindow.this, "No record found having name as " + searchTextField.getText());
                }
                else
                    JOptionPane.showMessageDialog(BookWindow.this, "Please select one the radio buttons");
            }
        });
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
                    if(isAdmin)
                        aadharId = "ADMIN";
                    bookTable.setAadharId(aadharId);


                }catch (SQLException exception) {
                    JOptionPane.showMessageDialog(BookWindow.this ,exception.toString(),"Error!",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BookProfile bookProfile = new BookProfile();
                bookProfile.setAlreadyPresent(false);
                if(isAdmin)
                    aadharId = "ADMIN";
                bookProfile.setAadharId(aadharId);
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



                try(Connection con = ConnectToDB.getConnectToDB();
                    Statement st = con.createStatement()) {
                    ResultSet rs = st.executeQuery(query + " ORDER BY name");
                    BookTable bookTable = new BookTable();
                    bookTable.setFilter(true);
                    bookTable.showTable(rs);
                    bookTable.setQuery(query);

                }catch (SQLException exception) {
                    JOptionPane.showMessageDialog(BookWindow.this ,exception.toString(),"Error!",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isAdmin) {
                    dispose();
                    return;
                }
                Librarian librarian = new Librarian();
                librarian.setAadharId(aadharId, libName);
                dispose();
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                subjectTextField.setText("");
                pubTextField.setText("");
                authorTextField.setText("");
            }
        });
        booksAssignedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query;
                if(isAdmin)
                    query = "SELECT * FROM books_assigned ORDER BY date,time DESC";
                else
                    query = "SELECT * FROM books_assigned WHERE assigned_by = '" + aadharId + "' ORDER BY date,time DESC ";
                try(Connection con = ConnectToDB.getConnectToDB();
                    Statement st = con.createStatement()) {
                    ResultSet rs = st.executeQuery(query);
                    BookTable bookTable = new BookTable();
                    bookTable.showAssignedTable(rs);
                    if(isAdmin)
                       aadharId = "ADMIN";
                    bookTable.setAadharId(aadharId);

                }catch (SQLException exception) {
                    JOptionPane.showMessageDialog(BookWindow.this ,exception.toString(),"Error!",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    static boolean isValidISBN(String isbn) {
        int n = isbn.length();
        if(n != 13)
            return false;

        for(int i = 0; i < n; ++i) {
            if(isbn.charAt(i) < '0' || isbn.charAt(i) > '9')
                return false;
        }

        return true;
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
                BookProfile bookProfile = new BookProfile();
                bookProfile.setAlreadyPresent(true);
                if(isAdmin)
                    aadharId = "ADMIN";
                bookProfile.setAadharId(aadharId);
                // fill details ,if exist

                bookProfile.setBookDetails(rs);
                dispose();

            }
            else {
                rs.previous();rs.previous();
                BookTable bookTable = new BookTable();
                bookTable.showTable(rs);
                if(isAdmin)
                    aadharId = "ADMIN";
                bookTable.setAadharId(aadharId);
            }
        }catch (SQLException e) {
            JOptionPane.showMessageDialog(BookWindow.this ,e.toString(),"Error!",JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(BookWindow.this, "No such Book Found!", "Oops!", JOptionPane.INFORMATION_MESSAGE);
            else {
                // open the profile window
                BookProfile bookProfile = new BookProfile();
                bookProfile.setBookDetails(rs);
                bookProfile.setAlreadyPresent(true);
               if(isAdmin)
                    aadharId = "ADMIN";
                 bookProfile.setAadharId(aadharId);
                    dispose();

            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(BookWindow.this ,e.toString(),"Error!",JOptionPane.ERROR_MESSAGE);
        }
    }
    void setAdmin() {isAdmin = true;
    this.setTitle("Books' Section By ADMIN");}
    // function overloading -> compile time polymorphism
    void setAadharId(String aadharId, String name) {
        this.aadharId = aadharId;
        this.libName = name;
        this.setTitle("Books' Section By " + aadharId);
    }
    void setAadharId(String aadharId) {
        this.aadharId = aadharId;
        this.setTitle("Librarian: " + aadharId);
    }

}
