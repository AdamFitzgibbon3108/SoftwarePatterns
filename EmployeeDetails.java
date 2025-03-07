
/* * 
 * This is a menu driven system that will allow users to define a data structure representing a collection of 
 * records that can be displayed both by means of a dialog that can be scrolled through and by means of a table
 * to give an overall view of the collection contents.
 * 
 * */
import java.util.function.Function;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

public class EmployeeDetails extends JFrame implements ActionListener, ItemListener, DocumentListener, WindowListener {
    private static final DecimalFormat format = new DecimalFormat("\u20ac ###,###,##0.00");
    private static final DecimalFormat fieldFormat = new DecimalFormat("0.00");
    private long currentByteStart = 0;
    private RandomFile application = new RandomFile();
    private FileNameExtensionFilter datFilter = new FileNameExtensionFilter("dat files (*.dat)", "dat");
    private File file;
    private boolean change = false;
    private boolean changesMade = false;
    
    private JMenuItem open, save, saveAs, create, modify, delete, firstItem, lastItem, nextItem, prevItem, searchById, searchBySurname, listAll, closeApp;
    private JButton first, previous, next, last, add, edit, deleteButton, displayAll, searchId, searchSurname, saveChange, cancelChange;
    private JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
    private JTextField idField, ppsField, surnameField, firstNameField, salaryField, searchByIdField, searchBySurnameField;
    private static EmployeeDetails frame = new EmployeeDetails();
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 16);
    private String generatedFileName;
    private Employee currentEmployee;
    private final String[] gender = { "", "M", "F" };
    private final String[] department = { "", "Administration", "Production", "Transport", "Management" };
    private final String[] fullTime = { "", "Yes", "No" };
    
    public String[] getGenderOptions() {
        return gender;
    }

    public String[] getDepartmentOptions() {
        return department;
    }

    public String[] getFullTimeOptions() {
        return fullTime;
    }

    
    // Factory method for creating JLabel
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        return label;
    }
    
 // Factory method for creating JTextField
    private JTextField createTextField(int columns, boolean editable, int i) {
        JTextField textField = new JTextField(columns);
        textField.setFont(LABEL_FONT);
        textField.setEditable(editable);
        textField.setFocusable(editable); // Prevents focus on uneditable fields
        return textField;
    }

    
 // Factory method for creating JButton
    private JButton createButton(String text, String tooltip, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        button.setToolTipText(tooltip);
        button.setFocusable(false); // Prevents unnecessary focus shifts
        return button;
    }

    
    // Factory method for creating JComboBox
    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(LABEL_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setEnabled(false);
        return comboBox;
    }
    
    private JMenuBar menuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File"), recordMenu = new JMenu("Records"), navigateMenu = new JMenu("Navigate"), closeMenu = new JMenu("Exit");
        
        fileMenu.setMnemonic(KeyEvent.VK_F);
        recordMenu.setMnemonic(KeyEvent.VK_R);
        navigateMenu.setMnemonic(KeyEvent.VK_N);
        closeMenu.setMnemonic(KeyEvent.VK_E);
        
        menuBar.add(fileMenu);
        menuBar.add(recordMenu);
        menuBar.add(navigateMenu);
        menuBar.add(closeMenu);
        
        fileMenu.add(open = new JMenuItem("Open")).addActionListener(this);
        fileMenu.add(save = new JMenuItem("Save")).addActionListener(this);
        fileMenu.add(saveAs = new JMenuItem("Save As")).addActionListener(this);
        
        recordMenu.add(create = new JMenuItem("Create new Record")).addActionListener(this);
        recordMenu.add(modify = new JMenuItem("Modify Record")).addActionListener(this);
        recordMenu.add(delete = new JMenuItem("Delete Record")).addActionListener(this);
        
        navigateMenu.add(firstItem = new JMenuItem("First")).addActionListener(this);
        navigateMenu.add(prevItem = new JMenuItem("Previous")).addActionListener(this);
        navigateMenu.add(nextItem = new JMenuItem("Next")).addActionListener(this);
        navigateMenu.add(lastItem = new JMenuItem("Last")).addActionListener(this);
        navigateMenu.add(searchById = new JMenuItem("Search by ID")).addActionListener(this);
        navigateMenu.add(searchBySurname = new JMenuItem("Search by Surname")).addActionListener(this);
        navigateMenu.add(listAll = new JMenuItem("List all Records")).addActionListener(this);
        
        closeMenu.add(closeApp = new JMenuItem("Close")).addActionListener(this);
        return menuBar;
    }





 // Initialize search panel
 private JPanel searchPanel() {
     JPanel searchPanel = new JPanel(new MigLayout());
     searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));

     searchPanel.add(createLabel("Search by ID:"), "growx, pushx");
     searchByIdField = createTextField(20, true, 20);
     searchPanel.add(searchByIdField, "width 200:200:200, growx, pushx");
     searchByIdField.addActionListener(this);
     searchPanel.add(searchId = createButton("Go", "Search Employee By ID", this),
             "width 35:35:35, height 20:20:20, growx, pushx, wrap");

     searchPanel.add(createLabel("Search by Surname:"), "growx, pushx");
     searchBySurnameField = createTextField(20, true, 20);
     searchPanel.add(searchBySurnameField, "width 200:200:200, growx, pushx");
     searchBySurnameField.addActionListener(this);
     searchPanel.add(searchSurname = createButton("Go", "Search Employee By Surname", this),
             "width 35:35:35, height 20:20:20, growx, pushx, wrap");

     return searchPanel;
 }

 // Initialize navigation panel
 private JPanel navigPanel() {
     JPanel navigPanel = new JPanel();
     navigPanel.setBorder(BorderFactory.createTitledBorder("Navigate"));

     first = createNavigationButton("first.png", "Display first Record");
     previous = createNavigationButton("prev.png", "Display previous Record");
     next = createNavigationButton("next.png", "Display next Record");
     last = createNavigationButton("last.png", "Display last Record");

     navigPanel.add(first);
     navigPanel.add(previous);
     navigPanel.add(next);
     navigPanel.add(last);

     return navigPanel;
 }

 // Helper method to create navigation buttons
 private JButton createNavigationButton(String iconPath, String tooltip) {
     JButton button = new JButton(new ImageIcon(new ImageIcon(iconPath)
             .getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH)));
     button.setPreferredSize(new Dimension(17, 17));
     button.setToolTipText(tooltip);
     button.addActionListener(this);
     return button;
 }

 // Initialize button panel
 private JPanel buttonPanel() {
     JPanel buttonPanel = new JPanel();

     buttonPanel.add(add = createButton("Add Record", "Add new Employee Record", this));
     buttonPanel.add(edit = createButton("Edit Record", "Edit current Employee", this));
     buttonPanel.add(deleteButton = createButton("Delete Record", "Delete current Employee", this));
     buttonPanel.add(displayAll = createButton("List all Records", "List all Registered Employees", this));

     return buttonPanel;
 }

 // Initialize main/details panel
 private JPanel detailsPanel() {
     JPanel empDetails = new JPanel(new MigLayout());
     JPanel buttonPanel = new JPanel();
     empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

     empDetails.add(createLabel("ID:"), "growx, pushx");
     empDetails.add(idField = createTextField(20, false, 20), "growx, pushx, wrap");

     empDetails.add(createLabel("PPS Number:"), "growx, pushx");
     empDetails.add(ppsField = createTextField(20, true, 9), "growx, pushx, wrap");

     empDetails.add(createLabel("Surname:"), "growx, pushx");
     empDetails.add(surnameField = createTextField(20, true, 20), "growx, pushx, wrap");

     empDetails.add(createLabel("First Name:"), "growx, pushx");
     empDetails.add(firstNameField = createTextField(20, true, 20), "growx, pushx, wrap");

     empDetails.add(createLabel("Gender:"), "growx, pushx");
     empDetails.add(genderCombo = createComboBox(gender), "growx, pushx, wrap");

     empDetails.add(createLabel("Department:"), "growx, pushx");
     empDetails.add(departmentCombo = createComboBox(department), "growx, pushx, wrap");

     empDetails.add(createLabel("Salary:"), "growx, pushx");
     empDetails.add(salaryField = createTextField(20, true, 20), "growx, pushx, wrap");

     empDetails.add(createLabel("Full Time:"), "growx, pushx");
     empDetails.add(fullTimeCombo = createComboBox(fullTime), "growx, pushx, wrap");

     saveChange = createButton("Save", "Save changes", this);
     saveChange.setVisible(false);
     cancelChange = createButton("Cancel", "Cancel edit", this);
     cancelChange.setVisible(false);

     buttonPanel.add(saveChange);
     buttonPanel.add(cancelChange);
     empDetails.add(buttonPanel, "span 2,growx, pushx,wrap");

     // Loop through panel components and add listeners & formatting
     for (int i = 0; i < empDetails.getComponentCount(); i++) {
         empDetails.getComponent(i).setFont(LABEL_FONT);

         if (empDetails.getComponent(i) instanceof JTextField) {
             JTextField field = (JTextField) empDetails.getComponent(i);
             field.setEditable(false);
             field.getDocument().addDocumentListener(this);
         } else if (empDetails.getComponent(i) instanceof JComboBox) {
             empDetails.getComponent(i).setBackground(Color.WHITE);
             empDetails.getComponent(i).setEnabled(false);
             ((JComboBox<?>) empDetails.getComponent(i)).addItemListener(this);
         }
     }
     return empDetails;
 }


//Display current Employee details
public void displayRecords(Employee thisEmployee) {
  if (thisEmployee == null || thisEmployee.getEmployeeId() == 0) return;

  searchByIdField.setText("");
  searchBySurnameField.setText("");

  genderCombo.setSelectedIndex(findComboBoxIndex(gender, Character.toString(thisEmployee.getGender())));
  departmentCombo.setSelectedIndex(findComboBoxIndex(department, thisEmployee.getDepartment().trim()));

  idField.setText(Integer.toString(thisEmployee.getEmployeeId()));
  ppsField.setText(thisEmployee.getPps().trim());
  surnameField.setText(thisEmployee.getSurname().trim());
  firstNameField.setText(thisEmployee.getFirstName());
  salaryField.setText(format.format(thisEmployee.getSalary()));

  fullTimeCombo.setSelectedIndex(thisEmployee.getFullTime() ? 1 : 2);

  change = false;
}

//Helper method to find the index of a value in a combo box array
private int findComboBoxIndex(String[] comboBoxValues, String searchValue) {
  for (int i = 0; i < comboBoxValues.length; i++) {
      if (comboBoxValues[i].equalsIgnoreCase(searchValue)) return i;
  }
  return 0; // Default to first option if not found
}

//Display Employee summary dialog
private void displayEmployeeSummaryDialog() {
  if (isSomeoneToDisplay()) new EmployeeSummaryDialog(getAllEmployees());
}

//Display search by ID dialog
private void displaySearchByIdDialog() {
  if (isSomeoneToDisplay()) new SearchByIdDialog(EmployeeDetails.this);
}

//Display search by surname dialog
private void displaySearchBySurnameDialog() {
  if (isSomeoneToDisplay()) new SearchBySurnameDialog(EmployeeDetails.this);
}

//Find byte start in file for the first active record
private void firstRecord() {
 navigateToRecord(() -> application.getFirst()); // No argument needed
}

//Find byte start in file for the previous active record
private void previousRecord() {
 navigateToRecord(pos -> application.getPrevious(pos)); // Needs pos
}

//Find byte start in file for the next active record
private void nextRecord() {
 navigateToRecord(pos -> application.getNext(pos)); // Needs pos
}

//Find byte start in file for the last active record
private void lastRecord() {
 navigateToRecord(() -> application.getLast()); // No argument needed
}


//Generalized method to navigate to a specific record when position is required
private void navigateToRecord(Function<Long, Long> recordSupplier) {
 if (!isSomeoneToDisplay()) return;

 application.openReadFile(file.getAbsolutePath());
 currentByteStart = recordSupplier.apply(currentByteStart); // Uses current position
 setCurrentEmployee(application.readRecords(currentByteStart));

 while (getCurrentEmployee().getEmployeeId() == 0) {
     currentByteStart = recordSupplier.apply(currentByteStart);
     setCurrentEmployee(application.readRecords(currentByteStart));
 }

 application.closeReadFile();
}

//Overloaded method for navigation functions that don't require arguments (getFirst, getLast)
private void navigateToRecord(Supplier<Long> recordSupplier) {
 if (!isSomeoneToDisplay()) return;

 application.openReadFile(file.getAbsolutePath());
 currentByteStart = recordSupplier.get(); // Uses Supplier instead
 setCurrentEmployee(application.readRecords(currentByteStart));

 while (getCurrentEmployee().getEmployeeId() == 0) {
     currentByteStart = recordSupplier.get();
     setCurrentEmployee(application.readRecords(currentByteStart));
 }

 application.closeReadFile();
}


//Search Employee by ID
public void searchEmployeeById() {
 try {
     if (!isSomeoneToDisplay()) return;

     firstRecord();
     int firstId = getCurrentEmployee().getEmployeeId();
     String searchId = searchByIdField.getText().trim();

     if (isMatchingId(searchId, getCurrentEmployee().getEmployeeId())) {
         displayRecords(getCurrentEmployee());
         return;
     }

     nextRecord();
     while (firstId != getCurrentEmployee().getEmployeeId()) {
         if (isMatchingId(searchId, getCurrentEmployee().getEmployeeId())) {
             displayRecords(getCurrentEmployee());
             return;
         }
         nextRecord();
     }

     showNotFoundMessage();
 } catch (NumberFormatException e) {
     highlightErrorField(searchByIdField, "Wrong ID format!");
 }
 resetSearchField(searchByIdField);
}

//Search Employee by Surname
public void searchEmployeeBySurname() {
 if (!isSomeoneToDisplay()) return;

 firstRecord();
 String firstSurname = getCurrentEmployee().getSurname().trim();
 String searchSurname = searchBySurnameField.getText().trim();

 if (isMatchingSurname(searchSurname, getCurrentEmployee().getSurname())) {
     displayRecords(getCurrentEmployee());
     return;
 }

 nextRecord();
 while (!firstSurname.equalsIgnoreCase(getCurrentEmployee().getSurname().trim())) {
     if (isMatchingSurname(searchSurname, getCurrentEmployee().getSurname())) {
         displayRecords(getCurrentEmployee());
         return;
     }
     nextRecord();
 }

 showNotFoundMessage();
 resetSearchField(searchBySurnameField);
}

//Get next available Employee ID
public int getNextFreeId() {
 if (file.length() == 0 || !isSomeoneToDisplay()) return 1;
 lastRecord();
 return getCurrentEmployee().getEmployeeId() + 1;
}

//Retrieve Employee details from input fields
private Employee getChangedDetails() {
 return new Employee(
     Integer.parseInt(idField.getText()), 
     ppsField.getText().toUpperCase(),
     surnameField.getText().toUpperCase(), 
     firstNameField.getText().toUpperCase(),
     genderCombo.getSelectedItem().toString().charAt(0), 
     departmentCombo.getSelectedItem().toString(),
     Double.parseDouble(salaryField.getText()), 
     fullTimeCombo.getSelectedItem().toString().equalsIgnoreCase("Yes")
 );
}

//Add Employee record
public void addRecord(Employee newEmployee) {
 application.openWriteFile(file.getAbsolutePath());
 currentByteStart = application.addRecords(newEmployee);
 application.closeWriteFile();
}

//Delete Employee record
private void deleteRecord() {
 if (!isSomeoneToDisplay()) return;

 int returnVal = JOptionPane.showOptionDialog(
     frame, "Do you want to delete record?", "Delete",
     JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null
 );

 if (returnVal == JOptionPane.YES_OPTION) {
     application.openWriteFile(file.getAbsolutePath());
     application.deleteRecords(currentByteStart);
     application.closeWriteFile();

     if (isSomeoneToDisplay()) {
         nextRecord();
         displayRecords(getCurrentEmployee());
     }
 }
}

//🔹 Helper Methods

//Check if entered ID matches an Employee's ID
private boolean isMatchingId(String searchId, int employeeId) {
 return searchId.equals(Integer.toString(employeeId));
}

//Check if entered surname matches an Employee's surname
private boolean isMatchingSurname(String searchSurname, String employeeSurname) {
 return searchSurname.equalsIgnoreCase(employeeSurname.trim());
}

//Show "Employee not found" message
private void showNotFoundMessage() {
 JOptionPane.showMessageDialog(null, "Employee not found!");
}

//Highlight a field to indicate an error and show a message
private void highlightErrorField(JTextField field, String message) {
 field.setBackground(new Color(255, 150, 150));
 JOptionPane.showMessageDialog(null, message);
}

//Reset search field after searching
private void resetSearchField(JTextField field) {
 field.setBackground(Color.WHITE);
 field.setText("");
}


//Get all Employees as a Vector of Vectors
private Vector<Object> getAllEmployees() {
 Vector<Object> allEmployees = new Vector<>();
 long byteStart = currentByteStart;
 
 firstRecord();
 int firstId = getCurrentEmployee().getEmployeeId();
 
 do {
     allEmployees.add(getEmployeeDetailsVector(getCurrentEmployee()));
     nextRecord();
 } while (firstId != getCurrentEmployee().getEmployeeId());

 currentByteStart = byteStart;
 return allEmployees;
}

//Create a Vector of Employee details
private Vector<Object> getEmployeeDetailsVector(Employee employee) {
 Vector<Object> empDetails = new Vector<>();
 empDetails.add(employee.getEmployeeId());
 empDetails.add(employee.getPps());
 empDetails.add(employee.getSurname());
 empDetails.add(employee.getFirstName());
 empDetails.add(employee.getGender());
 empDetails.add(employee.getDepartment());
 empDetails.add(employee.getSalary());
 empDetails.add(employee.getFullTime());
 return empDetails;
}

//Enable fields for editing Employee details
private void editDetails() {
 if (isSomeoneToDisplay()) {
     salaryField.setText(fieldFormat.format(getCurrentEmployee().getSalary()));
     change = false;
     setFieldsEnabled(true);
 }
}

//Cancel changes and reset fields
private void cancelChange() {
 setFieldsEnabled(false);
 displayRecords(getCurrentEmployee());
}

//Check if any active Employee record exists
private boolean isSomeoneToDisplay() {
 application.openReadFile(file.getAbsolutePath());
 boolean someoneToDisplay = application.isSomeoneToDisplay();
 application.closeReadFile();

 if (!someoneToDisplay) {
     clearEmployeeFields();
     JOptionPane.showMessageDialog(null, "No Employees registered!");
 }
 return someoneToDisplay;
}

//Check and validate PPS format
public boolean isPpsValid(String pps, long currentByte) {
 if (!pps.matches("\\d{7}[A-Za-z]") && !pps.matches("\\d{7}[A-Za-z]{2}")) return false;

 application.openReadFile(file.getAbsolutePath());
 boolean exists = application.isPpsExist(pps, currentByte);
 application.closeReadFile();
 return !exists;
}

//Check if a file has a ".dat" extension
private boolean isValidDatFile(File fileName) {
 return fileName.getName().endsWith(".dat");
}

//Check if changes were made and prompt to save
private boolean checkForChanges() {
 if (change) {
     saveChanges();
     return true;
 }
 setFieldsEnabled(false);
 displayRecords(getCurrentEmployee());
 return false;
}

//Validate user input in form fields
private boolean validateInputFields() {
 boolean valid = true;

 valid &= validateField(ppsField, !ppsField.getText().trim().isEmpty() && isPpsValid(ppsField.getText().trim(), currentByteStart));
 valid &= validateField(surnameField, !surnameField.getText().trim().isEmpty());
 valid &= validateField(firstNameField, !firstNameField.getText().trim().isEmpty());
 valid &= validateComboBox(genderCombo);
 valid &= validateComboBox(departmentCombo);
 valid &= validateSalaryField(salaryField);
 valid &= validateComboBox(fullTimeCombo);

 if (!valid) JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");
 return valid;
}

//Validate a text field and change its background if invalid
private boolean validateField(JTextField field, boolean condition) {
 if (!condition) {
     field.setBackground(new Color(255, 150, 150));
     return false;
 }
 field.setBackground(UIManager.getColor("TextField.background"));
 return true;
}

//Validate a JComboBox
private boolean validateComboBox(JComboBox<String> comboBox) {
 boolean valid = comboBox.getSelectedIndex() > 0;
 if (!valid) comboBox.setBackground(new Color(255, 150, 150));
 else comboBox.setBackground(UIManager.getColor("TextField.background"));
 return valid;
}

//Validate salary field
private boolean validateSalaryField(JTextField salaryField) {
 try {
     double salary = Double.parseDouble(salaryField.getText());
     return validateField(salaryField, salary >= 0);
 } catch (NumberFormatException e) {
     salaryField.setBackground(new Color(255, 150, 150));
     return false;
 }
}


//Enable/Disable all fields
private void setFieldsEnabled(boolean enabled) {
 ppsField.setEditable(enabled);
 surnameField.setEditable(enabled);
 firstNameField.setEditable(enabled);
 salaryField.setEditable(enabled);
 genderCombo.setEnabled(enabled);
 departmentCombo.setEnabled(enabled);
 fullTimeCombo.setEnabled(enabled);
 saveChange.setVisible(enabled);
 cancelChange.setVisible(enabled);
}

//Clear employee detail fields
private void clearEmployeeFields() {
 setCurrentEmployee(null);
 idField.setText("");
 ppsField.setText("");
 surnameField.setText("");
 firstNameField.setText("");
 salaryField.setText("");
 genderCombo.setSelectedIndex(0);
 departmentCombo.setSelectedIndex(0);
 fullTimeCombo.setSelectedIndex(0);
}

//Set text field background color to default
private void resetFieldColors() {
 Color defaultColor = UIManager.getColor("TextField.background");
 ppsField.setBackground(defaultColor);
 surnameField.setBackground(defaultColor);
 firstNameField.setBackground(defaultColor);
 salaryField.setBackground(defaultColor);
 genderCombo.setBackground(defaultColor);
 departmentCombo.setBackground(defaultColor);
 fullTimeCombo.setBackground(defaultColor);
}

//Enable or disable fields
public void toggleFields(boolean isEnabled) {
 boolean searchState = !isEnabled;
 
 ppsField.setEditable(isEnabled);
 surnameField.setEditable(isEnabled);
 firstNameField.setEditable(isEnabled);
 salaryField.setEditable(isEnabled);
 genderCombo.setEnabled(isEnabled);
 departmentCombo.setEnabled(isEnabled);
 fullTimeCombo.setEnabled(isEnabled);
 
 saveChange.setVisible(isEnabled);
 cancelChange.setVisible(isEnabled);
 
 searchByIdField.setEnabled(searchState);
 searchBySurnameField.setEnabled(searchState);
 searchId.setEnabled(searchState);
 searchSurname.setEnabled(searchState);
}

//Open file with JFileChooser
private void openFile() {
 final JFileChooser fc = new JFileChooser();
 fc.setDialogTitle("Open");
 fc.setFileFilter(datFilter);
 File newFile;
 
 if (file.length() != 0 || change) {
     int choice = showConfirmationDialog("Do you want to save changes?", "Save");
     if (choice == JOptionPane.YES_OPTION) {
         saveFile();
     }
 }

 int returnVal = fc.showOpenDialog(EmployeeDetails.this);
 if (returnVal == JFileChooser.APPROVE_OPTION) {
     newFile = fc.getSelectedFile();
     if (file.getName().equals(generatedFileName)) {
         file.delete();
     }
     file = newFile;
     application.openReadFile(file.getAbsolutePath());
     firstRecord();
     displayRecords(getCurrentEmployee());
     application.closeReadFile();
 }
}

//Save file with handling for "Save As"
private void saveFile() {
 if (file.getName().equals(generatedFileName)) {
     saveFileAs();
 } else {
     if (change) {
         int choice = showConfirmationDialog("Do you want to save changes?", "Save");
         if (choice == JOptionPane.YES_OPTION && !idField.getText().isEmpty()) {
             application.openWriteFile(file.getAbsolutePath());
             setCurrentEmployee(getChangedDetails());
             application.changeRecords(getCurrentEmployee(), currentByteStart);
             application.closeWriteFile();
         }
     }
     displayRecords(getCurrentEmployee());
     toggleFields(false);
 }
}

//Save changes to current Employee
private void saveChanges() {
 int choice = showConfirmationDialog("Do you want to save changes to current Employee?", "Save");
 if (choice == JOptionPane.YES_OPTION) {
     application.openWriteFile(file.getAbsolutePath());
     setCurrentEmployee(getChangedDetails());
     application.changeRecords(getCurrentEmployee(), currentByteStart);
     application.closeWriteFile();
     setChangesMade(false);
 }
 displayRecords(getCurrentEmployee());
 toggleFields(false);
}

//Save file as 'Save As'
private void saveFileAs() {
 final JFileChooser fc = new JFileChooser();
 fc.setDialogTitle("Save As");
 fc.setFileFilter(datFilter);
 fc.setApproveButtonText("Save");
 fc.setSelectedFile(new File("new_Employee.dat"));

 int returnVal = fc.showSaveDialog(EmployeeDetails.this);
 if (returnVal == JFileChooser.APPROVE_OPTION) {
     File newFile = fc.getSelectedFile();
     if (!isValidDatFile(newFile)) {
         newFile = new File(newFile.getAbsolutePath() + ".dat");
     }
     application.createFile(newFile.getAbsolutePath());

     try {
         Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
         if (file.getName().equals(generatedFileName)) {
             file.delete();
         }
         file = newFile;
     } catch (IOException e) {
         showErrorDialog("Error saving file.");
     }
 }
 setChangesMade(false);
}

//Handle application exit with save prompt
private void exitApp() {
 if (file.length() != 0) {
     if (isChangesMade()) {
         int choice = showConfirmationDialog("Do you want to save changes?", "Save");
         if (choice == JOptionPane.YES_OPTION) {
             saveFile();
             if (file.getName().equals(generatedFileName)) {
                 file.delete();
             }
             System.exit(0);
         } else if (choice == JOptionPane.NO_OPTION) {
             if (file.getName().equals(generatedFileName)) {
                 file.delete();
             }
             System.exit(0);
         }
     } else {
         if (file.getName().equals(generatedFileName)) {
             file.delete();
         }
         System.exit(0);
     }
 } else {
     if (file.getName().equals(generatedFileName)) {
         file.delete();
     }
     System.exit(0);
 }
}

//Generate a 20-character long random file name
private String generateRandomFileName() {
 String fileNameChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
 StringBuilder fileName = new StringBuilder();
 Random rnd = new Random();

 while (fileName.length() < 20) {
     int index = (int) (rnd.nextFloat() * fileNameChars.length());
     fileName.append(fileNameChars.charAt(index));
 }
 return fileName.toString() + ".dat";
}

//Show confirmation dialog with custom message and title
private int showConfirmationDialog(String message, String title) {
 return JOptionPane.showOptionDialog(frame, message, title,
         JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
}

//Show error dialog
private void showErrorDialog(String message) {
 JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
}


	// create file with generated file name when application is opened
	private void createRandomFile() {
		generatedFileName = getName() + ".dat";
		// assign generated file name to file
		file = new File(generatedFileName);
		// create file
		application.createFile(file.getName());
	}// end createRandomFile

	@Override
	public void actionPerformed(ActionEvent e) {
	    if (checkForChanges()) return;

	    Object source = e.getSource();

	    if (source == closeApp) exitApp();
	    else if (source == open) openFile();
	    else if (source == save) { saveFile(); change = false; }
	    else if (source == saveAs) { saveFileAs(); change = false; }
	    else if (source == searchById) displaySearchByIdDialog();
	    else if (source == searchBySurname) displaySearchBySurnameDialog();
	    else if (source == searchId || source == searchByIdField) searchEmployeeById();
	    else if (source == searchSurname || source == searchBySurnameField) searchEmployeeBySurname();
	    else if (source == saveChange) saveChanges();
	    else if (source == cancelChange) cancelChange();
	    else if (source == firstItem || source == first) navigateToRecord(() -> application.getFirst()); // Fixed
	    else if (source == prevItem || source == previous) navigateToRecord(application::getPrevious);
	    else if (source == nextItem || source == next) navigateToRecord(application::getNext);
	    else if (source == lastItem || source == last) navigateToRecord(() -> application.getLast()); // Fixed
	    else if (source == listAll || source == displayAll) {
	        if (isSomeoneToDisplay()) displayEmployeeSummaryDialog();
	    } else if (source == create || source == add) new AddRecordDialog(EmployeeDetails.this, departmentCombo, departmentCombo, firstNameField, departmentCombo);
	    else if (source == modify || source == edit) editDetails();
	    else if (source == delete || source == deleteButton) deleteRecord();
	}





	// Create content pane for the main dialog
	private void createContentPane() {
	    setTitle("Employee Details");
	    createRandomFile(); // Create a random file name
	    JPanel dialog = new JPanel(new MigLayout());

	    setJMenuBar(menuBar()); // Add menu bar to frame
	    dialog.add(searchPanel(), "width 400:400:400, growx, pushx"); // Add search panel
	    dialog.add(navigPanel(), "width 150:150:150, wrap"); // Add navigation panel
	    dialog.add(buttonPanel(), "growx, pushx, span 2,wrap"); // Add button panel
	    dialog.add(detailsPanel(), "gap top 30, gap left 150, center"); // Add details panel

	    // Wrap everything in a scroll pane for better UI usability
	    JScrollPane scrollPane = new JScrollPane(dialog);
	    getContentPane().add(scrollPane, BorderLayout.CENTER);
	    addWindowListener(this);
	    
	 // Absorb unhandled key events (Prevents error beeps)
	    getRootPane().addKeyListener(new java.awt.event.KeyAdapter() {
	        @Override
	        public void keyTyped(KeyEvent e) {
	            e.consume(); // Absorbs the event to prevent beeping
	        }
	    });

	 // Prevent clicks on empty space from triggering a beep
	    getContentPane().addMouseListener(new java.awt.event.MouseAdapter() {
	        @Override
	        public void mouseClicked(java.awt.event.MouseEvent e) {
	            getRootPane().requestFocus(); // Transfers focus back to main pane
	        }
	    });

	    
	}

	// Create and show the main application GUI
	private static void createAndShowGUI() {
	    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    frame.createContentPane(); // Setup UI components
	    frame.setSize(760, 600);
	    frame.setLocationRelativeTo(null); // Center the window on the screen
	    frame.setVisible(true);
	    frame.requestFocusInWindow();

	}

	// Main method - Entry point of the application
	public static void main(String[] args) {
	    javax.swing.SwingUtilities.invokeLater(EmployeeDetails::createAndShowGUI);
	}

	// DocumentListener methods - Track changes in text fields
	@Override
	public void changedUpdate(DocumentEvent d) {
	    handleDocumentChange();
	}

	@Override
	public void insertUpdate(DocumentEvent d) {
	    handleDocumentChange();
	}

	@Override
	public void removeUpdate(DocumentEvent d) {
	    handleDocumentChange();
	}

	// Handles document field changes
	private void handleDocumentChange() {
	    change = true;
	}

	// ItemListener method - Track changes in combo boxes
	@Override
	public void itemStateChanged(ItemEvent e) {
	    change = true;
	}

	// WindowListener methods - Handle window events
	@Override
	public void windowClosing(WindowEvent e) {
	    exitApp();
	}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

	public Employee getCurrentEmployee() {
		return currentEmployee;
	}

	public void setCurrentEmployee(Employee currentEmployee) {
		this.currentEmployee = currentEmployee;
	}

	public boolean correctPps(String trim, int i) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isChangesMade() {
		return changesMade;
	}

	
	
	public void setChangesMade(boolean changesMade) {
		this.changesMade = changesMade;
	}

	public void setSearchByIdField(String trim) {
		// TODO Auto-generated method stub
		
	}
}
