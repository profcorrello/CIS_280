package mcc.examples;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUILibraryApp_02 extends JFrame {
    private static final long serialVersionUID = -283697314982196505L;
	private JComboBox<String> memberComboBox;
    private JComboBox<String> bookComboBox;
    private JEditorPane allBooksArea;
    private JTable allBooksTable;
    private DefaultTableModel allBooksTableModel;
    private Map<String, Integer> memberMap = new HashMap<>();
    private Map<String, Integer> booksMap = new HashMap<>();
	private DBUtil dbUtil = new DBUtil();
	private DataManager dataManager = new DataManager();

    public GUILibraryApp_02() {
        setTitle("Library Management Application");
        setSize(800, 600);
        setDefaultCloseOperation(closeApp());
        setLayout(new BorderLayout());
        
        // Apply modern look and feel
        applyModernLookAndFeel();

        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Library Members", createCheckOutPanel());
        tabbedPane.addTab("All Books", createBooksPanel());
        add(tabbedPane, BorderLayout.CENTER);

        this.setLocationRelativeTo(null); // Center the frame on screen
        
    
        
        initialize();
    }
    
    private int closeApp() {
    	dbUtil.closeConnection();
        int exitOnClose = JFrame.EXIT_ON_CLOSE;
        return exitOnClose;
    }
    
    private void initialize() {
    	dataManager.resetData();
    	loadMembers();
		loadBooks();
		loadAllBooks();
		displayCheckedOutBooks();
    }
    
    private void applyModernLookAndFeel() {
        try {
//            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        	UIManager.setLookAndFeel(new FlatLightLaf());
//        	UIManager.setLookAndFeel(new FlatDarculaLaf());
//        	UIManager.setLookAndFeel(new FlatIntelliJLaf());
        	UIManager.put( "Button.arc", 999 );
        	UIManager.put( "TextComponent.arc", 500 );
        	UIManager.put( "TabbedPane.showTabSeparators", true );
        	UIManager.put("Table.alternateRowColor", new java.awt.Color(240, 240, 240)); // Light gray
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    

    private JPanel createCheckOutPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Check Out Book"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Member Selection
        memberComboBox = new JComboBox<>();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        panel.add(new JLabel("Select Member:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(memberComboBox, gbc);

        // Book Selection
        bookComboBox = new JComboBox<>();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        panel.add(new JLabel("Select Book:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(bookComboBox, gbc);

        // Check Out Button
        JButton checkOutButton = new JButton("Check Out Book");
        JPanel checkOutPanel = new JPanel(new BorderLayout());
        checkOutPanel.add(checkOutButton, BorderLayout.EAST);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.8;
        panel.add(checkOutPanel, gbc);

        checkOutButton.addActionListener(this::checkOutBook);
        
        memberComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					displayCheckedOutBooks();
				}
			}
		});

        // Checked-Out Books Area
//        allBooksArea = new JTextArea(5, 40);
        
     // Create a JEditorPane for HTML input
        allBooksArea = new JEditorPane();
        allBooksArea.setSize(5, 40);
        allBooksArea.setContentType("text/html"); // Enable HTML rendering 
        allBooksArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(allBooksArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Checked-Out Books"));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        return panel;
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("All Books"));

        // Define column names for the JTable
        String[] columnNames = {"Book Name", "Author", "ISBN", "Checked Out", "Overdue"};
//        allBooksTableModel = new DefaultTableModel(columnNames, 0);
        
        allBooksTableModel= new DefaultTableModel(columnNames, 0){

			private static final long serialVersionUID = 6154409544538419952L;

			@Override
            public Class<?> getColumnClass(int columnIndex) {
                // Set the third column to Boolean.class
				if (columnIndex == 3 || columnIndex == 4) {
					 return Boolean.class;
				}               
                return super.getColumnClass(columnIndex);
            }
        };
       
        allBooksTable = new JTable(allBooksTableModel);

        // Sorting and Filtering
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(allBooksTable.getModel());
        allBooksTable.setRowSorter(sorter);

        // Scroll Pane for Table
        JScrollPane scrollPane = new JScrollPane(allBooksTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Filter Panel
        JPanel filterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel filterLabel = new JLabel("Filter:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        filterPanel.add(filterLabel, gbc);

        JTextField filterField = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        filterPanel.add(filterField, gbc);

        filterField.addActionListener(e -> {
            String text = filterField.getText();
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        });

        panel.add(filterPanel, BorderLayout.NORTH);

//        // Load Books Button
//        JButton loadBooksButton = new JButton("Load Books");
//        loadBooksButton.addActionListener(e -> loadAllBooks());
//        panel.add(loadBooksButton, BorderLayout.SOUTH);

        return panel;
    }
    


	private void loadMembers() {
		memberComboBox.removeAllItems();
		memberMap.clear();
		
		List<LibraryMember> members = dataManager.getLibraryMembers();
		
		for (LibraryMember member : members) {
			String name = member.getFirstName() + " " + member.getLastName();
            memberComboBox.addItem(name);
            memberMap.put(name, member.getMemberId());
		}

	}

	private void loadBooks() {
		bookComboBox.removeAllItems();
		booksMap.clear();
		
		List<Book> books = dataManager.getBooks();
		
		for (Book book : books) {
			String title = book.getBook_title();
            bookComboBox.addItem(title);
            booksMap.put(title, book.getIndex());
		}

	}

	private void checkOutBook(ActionEvent event) {
		String memberName = (String) memberComboBox.getSelectedItem();
		String bookName = (String) bookComboBox.getSelectedItem();
		
		if (memberName != null && bookName != null) {
			int memberId = memberMap.get(memberName);
            int bookId = booksMap.get(bookName);
            
            boolean success = dataManager.checkOutBook(memberId, bookId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Book checked out successfully!");
                dataManager.resetData();
                loadAllBooks();
                displayCheckedOutBooks();
            } else {
                JOptionPane.showMessageDialog(this, "The book is already checked out. Please select another book.");
            }
		}

	}

	private void loadAllBooks() {
		
//		String[] columnNames = { "Book Name", "Author", "ISBN", "Checked Out", "Overdue" };
		allBooksTableModel.setRowCount(0); // Clear existing data from the table model to avoid duplicate rows
		
		List<Book> books = dataManager.getBooks();
		
		for (Book book : books) {
			boolean overdue = dataManager.isBookOverDue(book);
			allBooksTableModel.addRow(new Object[] { book.getBook_title(), 
					book.getAuthorFullName(), 
					book.getIsbn(), 
					book.isCheckedOut(), 
                    overdue }); 
		}



	}

	private void displayCheckedOutBooks() {
		allBooksArea.setText("");
		
		String memberName = memberComboBox.getSelectedItem().toString();
		
		if (memberName != null && memberMap.get(memberName) != null) {
			int memberId = memberMap.get(memberName);
            String checkedOutBookInfo = dataManager.getCheckedOutBooksForMember(memberId);
            allBooksArea.setText(checkedOutBookInfo);
            
		}else {
			int memberId = memberMap.getOrDefault(memberName, 1);
            String checkedOutBookInfo = dataManager.getCheckedOutBooksForMember(memberId);
            allBooksArea.setText(checkedOutBookInfo);
		}


	}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUILibraryApp_02 app = new GUILibraryApp_02();
            app.setVisible(true);
        });
    }
}
