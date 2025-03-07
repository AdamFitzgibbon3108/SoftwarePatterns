
/*
 * 
 * This is a dialog for adding new Employees and saving records to file
 * 
 * */

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

/**
 * Dialog for adding new Employees and saving records to file.
 */
public class AddRecordDialog extends JDialog implements ActionListener {
    private JTextField idField;
	private JTextField ppsField;
	private JTextField surnameField;
	private JTextField firstNameField;
	private JTextField salaryField;
    private JComboBox<String> genderCombo;
	private JComboBox<String> departmentCombo;
	private JComboBox<String> fullTimeCombo;
    private JButton save;
	private JButton cancel;
    private final EmployeeDetails parent;

    // Constructor for add record dialog
    public AddRecordDialog(EmployeeDetails parent,
    JComboBox<String> genderCombo, JComboBox<String> fullTimeCombo, JTextField firstNameField, JComboBox<String> departmentCombo)
    {
        super(parent, "Add Record", true);
		this.idField = new JTextField();
		this.ppsField = new JTextField();
		this.surnameField = new JTextField();
		this.firstNameField = firstNameField;
		this.salaryField = new JTextField();
		this.genderCombo = genderCombo;
		this.departmentCombo = departmentCombo;
		this.fullTimeCombo = fullTimeCombo;
		this.save = new JButton();
		this.cancel = new JButton();
        this.parent = parent;
        this.parent.setEnabled(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane(createDialogPane());
        setContentPane(scrollPane);

        getRootPane().setDefaultButton(save);
        setSize(500, 370);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

 // Initialize dialog container
    private Container createDialogPane() {
        JPanel empDetails = new JPanel(new MigLayout());
        empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        idField = createTextField(empDetails, "ID:", false);
        idField.setText(Integer.toString(parent.getNextFreeId()));
        ppsField = createTextField(empDetails, "PPS Number:", true);
        surnameField = createTextField(empDetails, "Surname:", true);
        firstNameField = createTextField(empDetails, "First Name:", true);
        salaryField = createTextField(empDetails, "Salary:", true);

        // Using getter methods for encapsulation
        genderCombo = createComboBox(empDetails, "Gender:", parent.getGenderOptions());
        departmentCombo = createComboBox(empDetails, "Department:", parent.getDepartmentOptions());
        fullTimeCombo = createComboBox(empDetails, "Full Time:", parent.getFullTimeOptions());

        JPanel buttonPanel = new JPanel();
        save = createButton(buttonPanel, "Save");
        cancel = createButton(buttonPanel, "Cancel");

        empDetails.add(buttonPanel, "span 2, growx, pushx, wrap");
        return empDetails;
    }


    // Create and configure a text field
    private JTextField createTextField(JPanel panel, String label, boolean editable) {
        panel.add(new JLabel(label), "growx, pushx");
        JTextField textField = new JTextField(20);
        textField.setEditable(editable);
        if (editable) {
            textField.setDocument(new JTextFieldLimit(label.equals("PPS Number:") ? 9 : 20));
        }
        panel.add(textField, "growx, pushx, wrap");
        return textField;
    }

    // Create and configure a combo box
    private JComboBox<String> createComboBox(JPanel panel, String label, String[] items) {
        panel.add(new JLabel(label), "growx, pushx");
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setBackground(Color.WHITE);
        panel.add(comboBox, "growx, pushx, wrap");
        return comboBox;
    }

    // Create and configure a button
    private JButton createButton(JPanel panel, String text) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        panel.add(button);
        return button;
    }

    // Add record to file
    private void addRecord() {
        boolean fullTime = fullTimeCombo.getSelectedItem().toString().equalsIgnoreCase("Yes");
        Employee theEmployee = new Employee(
            Integer.parseInt(idField.getText()),
            ppsField.getText().toUpperCase(),
            surnameField.getText().toUpperCase(),
            firstNameField.getText().toUpperCase(),
            genderCombo.getSelectedItem().toString().charAt(0),
            departmentCombo.getSelectedItem().toString(),
            Double.parseDouble(salaryField.getText()),
            fullTime
        );
        parent.setCurrentEmployee(theEmployee);
        parent.addRecord(theEmployee);
        parent.displayRecords(theEmployee);
    }

    // Validate input fields
    private boolean checkInput() {
        resetFieldColors();
        boolean valid = true;

        if (isFieldEmpty(ppsField) || parent.correctPps(ppsField.getText().trim(), -1)) valid = false;
        if (isFieldEmpty(surnameField)) valid = false;
        if (isFieldEmpty(firstNameField)) valid = false;
        if (isComboBoxUnselected(genderCombo)) valid = false;
        if (isComboBoxUnselected(departmentCombo)) valid = false;
        if (!isValidSalary(salaryField)) valid = false;
        if (isComboBoxUnselected(fullTimeCombo)) valid = false;

        if (!valid) {
            JOptionPane.showMessageDialog(this, "Wrong values or format! Please check!");
        }
        return valid;
    }

    // Helper method to check if a text field is empty and highlight it if so
    private boolean isFieldEmpty(JTextField field) {
        if (field.getText().trim().isEmpty()) {
            field.setBackground(new Color(255, 150, 150));
            return true;
        }
        return false;
    }

    // Helper method to validate salary field
    private boolean isValidSalary(JTextField field) {
        try {
            double salary = Double.parseDouble(field.getText());
            if (salary < 0) {
                field.setBackground(new Color(255, 150, 150));
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            field.setBackground(new Color(255, 150, 150));
            return false;
        }
    }

    // Helper method to check if a combo box selection is invalid
    private boolean isComboBoxUnselected(JComboBox<String> comboBox) {
        if (comboBox.getSelectedIndex() == 0) {
            comboBox.setBackground(new Color(255, 150, 150));
            return true;
        }
        return false;
    }

    // Reset text field backgrounds to default
    private void resetFieldColors() {
        ppsField.setBackground(Color.WHITE);
        surnameField.setBackground(Color.WHITE);
        firstNameField.setBackground(Color.WHITE);
        salaryField.setBackground(Color.WHITE);
        genderCombo.setBackground(Color.WHITE);
        departmentCombo.setBackground(Color.WHITE);
        fullTimeCombo.setBackground(Color.WHITE);
    }

    // Handle button actions
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == save && checkInput()) {
            addRecord();
            dispose();
            parent.setChangesMade(true);
        } else if (e.getSource() == cancel) {
            dispose();
        }
    }
}
