import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class MarksGradeCalculator {
    private static final int SUBJECTS = 5;
    private static final String[] SUBJECT_NAMES = {
        "Subject 1", "Subject 2", "Subject 3", "Subject 4", "Subject 5"
    };
    private static final String FILE_NAME = "marks_data.txt";

    private static final Scanner input = new Scanner(System.in);
    private static int[] marks = new int[SUBJECTS];
    private static String studentName = "";
    private static boolean marksEntered = false;

    public static void main(String[] args) {
        loadData();

        while (true) {
            System.out.println("\n===== MARKS AND GRADE CALCULATOR (PS No. 23) =====");
            System.out.println("1. Enter / Update Student Marks");
            System.out.println("2. Display Result");
            System.out.println("3. Find Highest and Lowest Marks");
            System.out.println("4. Sort Marks (High to Low)");
            System.out.println("5. Search for a Particular Mark");
            System.out.println("6. Save and Exit");
            System.out.print("Enter your choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    enterMarks();
                    break;
                case 2:
                    displayResult();
                    break;
                case 3:
                    findHighestLowest();
                    break;
                case 4:
                    sortMarksDescending();
                    break;
                case 5:
                    searchMark();
                    break;
                case 6:
                    saveData();
                    System.out.println("Data saved. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please select 1 to 6.");
            }
        }
    }

    // Reads an integer safely from the keyboard.
    private static int readInt() {
        while (!input.hasNextInt()) {
            System.out.print("Invalid input. Enter a whole number: ");
            input.next();
        }
        return input.nextInt();
    }

    // Accepts the student's name and marks in five subjects.
    private static void enterMarks() {
        input.nextLine(); // Clear the newline left by nextInt().
        System.out.print("Enter student name: ");
        studentName = input.nextLine().trim();

        if (studentName.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        for (int i = 0; i < SUBJECTS; i++) {
            while (true) {
                System.out.print("Enter marks in " + SUBJECT_NAMES[i] + " (0-100): ");
                int mark = readInt();

                if (mark >= 0 && mark <= 100) {
                    marks[i] = mark;
                    break;
                }
                System.out.println("Invalid marks. Enter a value between 0 and 100.");
            }
        }

        marksEntered = true;
        saveData();
        System.out.println("Marks entered and saved successfully.");
    }

    // Calculates and displays total, percentage and grade.
    private static void displayResult() {
        if (!checkMarks()) return;

        int total = calculateTotal(marks);
        double percentage = total / 5.0;
        String grade = calculateGrade(percentage);

        System.out.println("\n----- STUDENT RESULT -----");
        System.out.println("Student: " + studentName);
        System.out.println("Total: " + total + "/500");
        System.out.printf("Percentage: %.2f%%%n", percentage);
        System.out.println("Grade: " + grade + (percentage >= 40 ? " (Pass)" : " (Fail)"));
    }

    private static int calculateTotal(int[] values) {
        int total = 0;
        for (int value : values) total += value;
        return total;
    }

    // Grade scale from the official PS 23 description.
    private static String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 75) return "A";
        if (percentage >= 60) return "B";
        if (percentage >= 40) return "C";
        return "F";
    }

    // Finds the highest and lowest marks and their subject positions.
    private static void findHighestLowest() {
        if (!checkMarks()) return;

        int highestIndex = 0;
        int lowestIndex = 0;

        for (int i = 1; i < SUBJECTS; i++) {
            if (marks[i] > marks[highestIndex]) highestIndex = i;
            if (marks[i] < marks[lowestIndex]) lowestIndex = i;
        }

        System.out.println("Highest: " + SUBJECT_NAMES[highestIndex] +
                           " (" + marks[highestIndex] + ")");
        System.out.println("Lowest: " + SUBJECT_NAMES[lowestIndex] +
                           " (" + marks[lowestIndex] + ")");
    }

    // Sorts a copy of the marks in descending order, preserving subject order.
    private static void sortMarksDescending() {
        if (!checkMarks()) return;

        int[] sorted = Arrays.copyOf(marks, marks.length);
        for (int i = 0; i < sorted.length - 1; i++) {
            for (int j = 0; j < sorted.length - 1 - i; j++) {
                if (sorted[j] < sorted[j + 1]) {
                    int temp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = temp;
                }
            }
        }

        System.out.println("Marks (high to low): " + Arrays.toString(sorted));
    }

    // Searches for a mark using linear search and reports its subject.
    private static void searchMark() {
        if (!checkMarks()) return;

        System.out.print("Enter the mark to search (0-100): ");
        int target = readInt();

        if (target < 0 || target > 100) {
            System.out.println("Invalid marks. Enter a value between 0 and 100.");
            return;
        }

        boolean found = false;
        for (int i = 0; i < SUBJECTS; i++) {
            if (marks[i] == target) {
                System.out.println("Mark " + target + " found in " + SUBJECT_NAMES[i]);
                found = true;
            }
        }

        if (!found) System.out.println("Mark " + target + " not found.");
    }

    // Saves the current student's marks to a text file.
    private static void saveData() {
        if (!marksEntered) return;

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            writer.println(studentName);
            for (int i = 0; i < SUBJECTS; i++) {
                writer.println(marks[i]);
            }
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    // Loads previously saved marks when the program starts.
    private static void loadData() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String name = reader.readLine();
            if (name == null) return;

            int[] loadedMarks = new int[SUBJECTS];
            for (int i = 0; i < SUBJECTS; i++) {
                String line = reader.readLine();
                if (line == null) return;
                int value = Integer.parseInt(line);
                if (value < 0 || value > 100) return;
                loadedMarks[i] = value;
            }

            studentName = name;
            marks = loadedMarks;
            marksEntered = true;
            System.out.println("Previous student data loaded for " + studentName + ".");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Saved data could not be loaded. Enter marks again.");
        }
    }

    private static boolean checkMarks() {
        if (!marksEntered) {
            System.out.println("Please enter student marks first (Option 1).");
            return false;
        }
        return true;
    }
}
