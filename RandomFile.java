/*
 * This class is for accessing, creating, and modifying records in a file
 */

import java.io.IOException;
import java.io.RandomAccessFile;
import javax.swing.JOptionPane;

public class RandomFile {
    private RandomAccessFile output;
    private RandomAccessFile input;

    // Create new file
    public void createFile(String fileName) {
        try (RandomAccessFile file = new RandomAccessFile(fileName, "rw")) {
            // File created successfully
        } catch (IOException e) {
            showError("Error processing file!");
        }
    }

    // Open file for adding or modifying records
    public void openWriteFile(String fileName) {
        output = openFile(fileName, "rw");
    }

    // Close file for writing
    public void closeWriteFile() {
        closeFile(output);
    }

    // Add records to file
    public long addRecords(Employee employeeToAdd) {
        if (output == null) {
            showError("File is not opened for writing!");
            return -1;
        }

        try {
            RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord(
                employeeToAdd.getEmployeeId(), employeeToAdd.getPps(),
                employeeToAdd.getSurname(), employeeToAdd.getFirstName(),
                employeeToAdd.getGender(), employeeToAdd.getDepartment(),
                employeeToAdd.getSalary(), employeeToAdd.getFullTime()
            );

            output.seek(output.length()); // Move to end of file
            record.write(output);
            return output.length() - RandomAccessEmployeeRecord.SIZE; // Return position
        } catch (IOException e) {
            showError("Error writing to file!");
            return -1;
        }
    }

    // Generic method to open a file
    private RandomAccessFile openFile(String fileName, String mode) {
        try {
            return new RandomAccessFile(fileName, mode);
        } catch (IOException e) {
            showError("File does not exist!");
            return null;
        }
    }

    // Generic method to close a file
    private void closeFile(RandomAccessFile file) {
        if (file != null) {
            try {
                file.close();
            } catch (IOException e) {
                showError("Error closing file!");
            }
        }
    }

 // Display error messages with debugging
    private void showError(String message, Exception e) {
        JOptionPane.showMessageDialog(null, message);
        e.printStackTrace(); // Print error to console for debugging
    }


 // Change details for an existing record
    public void changeRecords(Employee newDetails, long byteToStart) {
        try {
            output.seek(byteToStart); // Locate position
            new RandomAccessEmployeeRecord(
                newDetails.getEmployeeId(),
                newDetails.getPps(),
                newDetails.getSurname(),
                newDetails.getFirstName(),
                newDetails.getGender(),
                newDetails.getDepartment(),
                newDetails.getSalary(),
                newDetails.getFullTime()
            ).write(output); // Write new data
        } catch (IOException e) {
            showError("Error writing to file!");
        }
    }

    // Delete a record (mark as inactive)
    public void deleteRecords(long byteToStart) {
        try {
            output.seek(byteToStart); // Locate position
            new RandomAccessEmployeeRecord().write(output); // Write empty record
        } catch (IOException e) {
            showError("Error writing to file!");
        }
    }

    // Open file for reading
    public void openReadFile(String fileName) {
        try {
            input = new RandomAccessFile(fileName, "r");
        } catch (IOException e) {
            showError("File is not supported!");
        }
    }

    // Close file after reading
    public void closeReadFile() {
        try {
            if (input != null) input.close();
        } catch (IOException e) {
            showError("Error closing file!");
        }
    }

    // Get position of the first record
    public long getFirst() {
        return 0;
    }

    // Get position of the last record
    public long getLast() {
        try {
            return input.length() - RandomAccessEmployeeRecord.SIZE;
        } catch (IOException e) {
            return 0;
        }
    }

    // Get position of the next record
    public long getNext(long readFrom) {
        try {
            return (readFrom + RandomAccessEmployeeRecord.SIZE == input.length()) ? 0 : readFrom + RandomAccessEmployeeRecord.SIZE;
        } catch (IOException e) {
            return readFrom;
        }
    }

    // Get position of the previous record
    public long getPrevious(long readFrom) {
        try {
            return (readFrom == 0) ? input.length() - RandomAccessEmployeeRecord.SIZE : readFrom - RandomAccessEmployeeRecord.SIZE;
        } catch (IOException e) {
            return readFrom;
        }
    }

    // Read record from file at specified position
    public Employee readRecords(long byteToStart) {
        try {
            input.seek(byteToStart);
            RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
            record.read(input);
            return record;
        } catch (IOException e) {
            return null;
        }
    }

    // Check if a PPS number already exists in the file
    public boolean isPpsExist(String pps, long currentByteStart) {
        return checkConditionInFile(currentByteStart, record -> record.getPps().trim().equalsIgnoreCase(pps), "PPS number already exists!");
    }

    // Check if any record contains a valid ID
    public boolean isSomeoneToDisplay() {
        return checkConditionInFile(0, record -> record.getEmployeeId() > 0, null);
    }

    // Helper method to check a condition in the file
    private boolean checkConditionInFile(long startByte, RecordCondition condition, String errorMessage) {
        long currentByte = startByte;
        boolean found = false;

        try {
            while (currentByte < input.length() && !found) {
                if (currentByte != startByte) {
                    input.seek(currentByte);
                    RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
                    record.read(input);
                    if (condition.test(record)) {
                        found = true;
                        if (errorMessage != null) showError(errorMessage);
                    }
                }
                currentByte += RandomAccessEmployeeRecord.SIZE;
            }
        } catch (IOException e) {
            return false;
        }
        return found;
    }

    // Functional interface for condition checking
    @FunctionalInterface
    private interface RecordCondition {
        boolean test(RandomAccessEmployeeRecord record);
    }

    // Display error messages
    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}


