/*
 * 
 * This is a dialog for searching Employees by their surname.
 * 
 * */

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

public class SearchBySurnameDialog extends JDialog implements ActionListener {
    private final EmployeeDetails parent;
    private JButton searchButton;
	private JButton cancelButton;
    private JTextField searchField;

    // Constructor for SearchBySurnameDialog
    public SearchBySurnameDialog(EmployeeDetails parent) {
        super(parent, "Search by Surname", true);
        this.parent = parent;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane(createSearchPane());
        setContentPane(scrollPane);
        getRootPane().setDefaultButton(searchButton);

        setSize(500, 190);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    // Create the search container
    private Container createSearchPane() {
        JPanel searchPanel = new JPanel(new GridLayout(3, 1));

        searchPanel.add(new JLabel("Search by Surname", SwingConstants.CENTER));

        searchField = new JTextField(20);
        searchField.setFont(parent.getFont());
        searchField.setDocument(new JTextFieldLimit(20));

        JPanel textPanel = new JPanel();
        textPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        textPanel.add(new JLabel("Enter Surname:"));
        textPanel.add(searchField);

        JPanel buttonPanel = new JPanel();
        searchButton = createButton(buttonPanel, "Search");
        cancelButton = createButton(buttonPanel, "Cancel");

        searchPanel.add(textPanel);
        searchPanel.add(buttonPanel);

        return searchPanel;
    }

    // Create a button and add it to the given panel
    private JButton createButton(JPanel panel, String text) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        panel.add(button);
        return button;
    }

    // Action listener for search and cancel buttons
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            handleSearch();
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }

    // Handle the search functionality
    private void handleSearch() {
        parent.setSearchByIdField(searchField.getText().trim());
        parent.searchEmployeeBySurname();
        dispose();
    }
}
