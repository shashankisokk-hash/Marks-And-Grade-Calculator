import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;

/**
 * Marks and Grade Calculator
 * Student: Shashank Yadav | ERP: 11338
 *
 * DSA concepts:
 * 1. Array       - stores Student records, kept sorted by roll number.
 * 2. Merge Sort  - ranks a copy of records by percentage (descending).
 * 3. Binary Search - searches the roll-number-sorted array.
 *
 * Compile: javac MarksGradeCalculator.java
 * Run:     java MarksGradeCalculator
 */
public class MarksGradeCalculator extends JFrame {
    private static final int SUBJECT_COUNT = 5;
    private static final String[] SUBJECTS = {
        "DSA", "Java", "Mathematics", "Computer Networks", "English"
    };

    private Student[] students = new Student[10];
    private int size = 0;

    private final DefaultTableModel model = new DefaultTableModel(
        new String[]{"Rank", "Roll No.", "Student Name", "Total / 500",
                     "Percentage", "Grade", "Result"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final JTextField searchField = new JTextField(12);
    private final JLabel totalValue = new JLabel("0");
    private final JLabel averageValue = new JLabel("0.00%");
    private final JLabel topperValue = new JLabel("—");
    private final JLabel passedValue = new JLabel("0");
    private final JLabel status = new JLabel("Ready. Add a student to begin.");

    private static final Color PURPLE = new Color(105, 66, 220);
    private static final Color DARK = new Color(42, 30, 75);
    private static final Color BG = new Color(246, 243, 252);
    private static final Color MUTED = new Color(120, 112, 143);
    private static final Color BORDER = new Color(229, 223, 241);

    static class Student {
        int roll;
        String name;
        int[] marks;
        int total;
        double percentage;
        String grade;
        String result;

        Student(int roll, String name, int[] marks) {
            this.roll = roll;
            this.name = name;
            this.marks = marks.clone();
            calculateResult();
        }

        void calculateResult() {
            total = 0;
            for (int mark : marks) total += mark;
            percentage = total / (double) SUBJECT_COUNT;

            if (percentage >= 90) grade = "A+";
            else if (percentage >= 80) grade = "A";
            else if (percentage >= 70) grade = "B";
            else if (percentage >= 60) grade = "C";
            else if (percentage >= 40) grade = "D";
            else grade = "F";

            result = percentage >= 40 ? "PASS" : "FAIL";
        }
    }

    public MarksGradeCalculator() {
        super("GradeBoard | Marks & Grade Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setSize(1160, 730);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        add(buildSidebar(), BorderLayout.WEST);
        add(buildDashboard(), BorderLayout.CENTER);

        addSampleStudents();
        refreshDashboard();
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(205, 0));
        side.setBackground(DARK);
        side.setBorder(new EmptyBorder(25, 17, 22, 17));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("◈ GradeBoard");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 21));
        side.add(logo);

        JLabel subtitle = new JLabel("STUDENT PERFORMANCE");
        subtitle.setForeground(new Color(198, 183, 245));
        subtitle.setFont(new Font("SansSerif", Font.BOLD, 9));
        subtitle.setBorder(new EmptyBorder(9, 2, 28, 0));
        side.add(subtitle);

        side.add(navItem("▦   Dashboard", true));
        side.add(navItem("↕   Ranking & Sorting", false));
        side.add(navItem("⌕   Roll Number Search", false));
        side.add(Box.createVerticalGlue());

        JLabel dsa = new JLabel(
            "<html><b style='color:#c6b7f5'>DSA CONCEPTS</b><br><br>" +
            "Array · Student storage<br>Merge Sort · Ranking<br>" +
            "Binary Search · Lookup</html>"
        );
        dsa.setForeground(new Color(224, 218, 240));
        dsa.setFont(new Font("SansSerif", Font.PLAIN, 11));
        side.add(dsa);
        return side;
    }

    private JLabel navItem(String text, boolean active) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(active ? Color.WHITE : new Color(218, 210, 239));
        label.setBorder(new EmptyBorder(12, 9, 12, 7));
        label.setOpaque(active);
        if (active) label.setBackground(new Color(91, 65, 157));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel buildDashboard() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(23, 24, 20, 24));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 4));
        titles.setOpaque(false);

        JLabel heading = new JLabel("Marks & Grade Calculator");
        heading.setFont(new Font("SansSerif", Font.BOLD, 24));
        heading.setForeground(DARK);

        JLabel subheading = new JLabel(
            "Manage marks, calculate grades and rank student performance"
        );
        subheading.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subheading.setForeground(MUTED);
        titles.add(heading);
        titles.add(subheading);

        JButton addButton = primaryButton("+ Add Student");
        addButton.addActionListener(e -> showAddStudentDialog());
        header.add(titles, BorderLayout.WEST);
        header.add(addButton, BorderLayout.EAST);

        content.add(header);
        content.add(Box.createVerticalStrut(18));
        content.add(buildStatistics());
        content.add(Box.createVerticalStrut(17));
        content.add(buildTablePanel());
        content.add(Box.createVerticalStrut(10));

        status.setFont(new Font("SansSerif", Font.PLAIN, 11));
        status.setForeground(MUTED);
        content.add(status);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(15);
        main.add(scroll, BorderLayout.CENTER);
        return main;
    }

    private JPanel buildStatistics() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setOpaque(false);
        panel.add(statCard("TOTAL STUDENTS", totalValue, PURPLE));
        panel.add(statCard("CLASS AVERAGE", averageValue, DARK));
        panel.add(statCard("CLASS TOPPER", topperValue, DARK));
        panel.add(statCard("PASSED STUDENTS", passedValue, new Color(35, 145, 91)));
        return panel;
    }

    private JPanel statCard(String title, JLabel value, Color valueColor) {
        JPanel card = new JPanel(new BorderLayout(0, 11));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(14, 13, 14, 13)
        ));

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 9));
        label.setForeground(MUTED);
        value.setFont(new Font("SansSerif", Font.BOLD, 19));
        value.setForeground(valueColor);

        card.add(label, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 13));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBox.setOpaque(false);
        JLabel title = new JLabel("Student Performance");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(DARK);
        JLabel hint = new JLabel("Ranked by percentage · highest first");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 10));
        hint.setForeground(MUTED);
        titleBox.add(title);
        titleBox.add(hint);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 11));
        searchField.setToolTipText("Enter roll number");

        JButton searchButton = primaryButton("Search");
        JButton showAllButton = secondaryButton("Show All");
        JButton sortButton = secondaryButton("Sort Ranking");

        searchButton.addActionListener(e -> searchStudent());
        searchField.addActionListener(e -> searchStudent());
        showAllButton.addActionListener(e -> {
            refreshDashboard();
            status.setText("Showing all students, ranked by percentage.");
        });
        sortButton.addActionListener(e -> {
            refreshDashboard();
            status.setText("Merge Sort completed: students ranked by percentage.");
        });

        actions.add(searchField);
        actions.add(searchButton);
        actions.add(showAllButton);
        actions.add(sortButton);

        top.add(titleBox, BorderLayout.WEST);
        top.add(actions, BorderLayout.EAST);

        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 11));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 10));
        table.getTableHeader().setBackground(new Color(244, 240, 250));
        table.getTableHeader().setForeground(DARK);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(237, 230, 251));
        table.setFillsViewportHeight(true);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER));

        panel.add(top, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        return panel;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 11));
        button.setForeground(Color.WHITE);
        button.setBackground(PURPLE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(9, 13, 9, 13));
        return button;
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 10));
        button.setForeground(PURPLE);
        button.setBackground(new Color(241, 236, 251));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 10, 8, 10));
        return button;
    }

    /*
     * ARRAY OPERATION:
     * Inserts a student and keeps the array sorted by roll number.
     * This makes Binary Search possible.
     */
    private void addStudent(Student student) {
        if (size == students.length) {
            students = Arrays.copyOf(students, students.length * 2);
        }

        int i = size - 1;
        while (i >= 0 && students[i].roll > student.roll) {
            students[i + 1] = students[i];
            i--;
        }
        students[i + 1] = student;
        size++;
    }

    /*
     * BINARY SEARCH:
     * Searches the roll-number-sorted student array in O(log n) time.
     */
    private int binarySearchByRoll(int roll) {
        int low = 0;
        int high = size - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (students[mid].roll == roll) return mid;
            if (students[mid].roll < roll) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }

    /*
     * MERGE SORT:
     * Sorts a copy by percentage, highest first.
     * The original array remains sorted by roll number.
     */
    private Student[] getRankedStudents() {
        Student[] copy = Arrays.copyOf(students, size);
        if (copy.length > 1) mergeSort(copy, 0, copy.length - 1);
        return copy;
    }

    private void mergeSort(Student[] array, int left, int right) {
        if (left >= right) return;

        int middle = left + (right - left) / 2;
        mergeSort(array, left, middle);
        mergeSort(array, middle + 1, right);
        merge(array, left, middle, right);
    }

    private void merge(Student[] array, int left, int middle, int right) {
        Student[] temp = new Student[right - left + 1];
        int i = left;
        int j = middle + 1;
        int k = 0;

        while (i <= middle && j <= right) {
            if (array[i].percentage >= array[j].percentage) {
                temp[k++] = array[i++];
            } else {
                temp[k++] = array[j++];
            }
        }

        while (i <= middle) temp[k++] = array[i++];
        while (j <= right) temp[k++] = array[j++];

        for (int x = 0; x < temp.length; x++) {
            array[left + x] = temp[x];
        }
    }

    private void refreshDashboard() {
        Student[] ranked = getRankedStudents();
        model.setRowCount(0);

        int passed = 0;
        double sum = 0;

        for (int i = 0; i < ranked.length; i++) {
            Student student = ranked[i];
            if (student.result.equals("PASS")) passed++;
            sum += student.percentage;

            model.addRow(new Object[]{
                i + 1,
                student.roll,
                student.name,
                student.total + " / 500",
                String.format("%.2f%%", student.percentage),
                student.grade,
                student.result
            });
        }

        totalValue.setText(String.valueOf(size));
        averageValue.setText(size == 0 ? "0.00%" :
            String.format("%.2f%%", sum / size));
        topperValue.setText(size == 0 ? "—" : ranked[0].name);
        passedValue.setText(String.valueOf(passed));
    }

    private void searchStudent() {
        String input = searchField.getText().trim();

        if (input.isEmpty()) {
            status.setText("Enter a roll number to search.");
            return;
        }

        try {
            int roll = Integer.parseInt(input);
            int index = binarySearchByRoll(roll);
            model.setRowCount(0);

            if (index == -1) {
                status.setText("No student found with roll number " + roll + ".");
            } else {
                Student s = students[index];
                model.addRow(new Object[]{
                    "—", s.roll, s.name, s.total + " / 500",
                    String.format("%.2f%%", s.percentage), s.grade, s.result
                });
                status.setText("Binary Search: student found for roll number " + roll + ".");
            }
        } catch (NumberFormatException ex) {
            status.setText("Roll number must be a valid integer.");
        }
    }

    private void showAddStudentDialog() {
        JTextField rollField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField[] markFields = new JTextField[SUBJECT_COUNT];

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Roll number:"));
        form.add(rollField);
        form.add(new JLabel("Student name:"));
        form.add(nameField);

        for (int i = 0; i < SUBJECT_COUNT; i++) {
            markFields[i] = new JTextField();
            form.add(new JLabel(SUBJECTS[i] + " (0–100):"));
            form.add(markFields[i]);
        }

        int choice = JOptionPane.showConfirmDialog(
            this, form, "Add Student",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) return;

        try {
            int roll = Integer.parseInt(rollField.getText().trim());
            String name = nameField.getText().trim();

            if (roll <= 0 || name.isEmpty()) {
                throw new IllegalArgumentException();
            }

            if (binarySearchByRoll(roll) != -1) {
                JOptionPane.showMessageDialog(this,
                    "That roll number already exists.",
                    "Duplicate Roll Number", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int[] marks = new int[SUBJECT_COUNT];
            for (int i = 0; i < SUBJECT_COUNT; i++) {
                marks[i] = Integer.parseInt(markFields[i].getText().trim());
                if (marks[i] < 0 || marks[i] > 100) {
                    throw new IllegalArgumentException();
                }
            }

            addStudent(new Student(roll, name, marks));
            refreshDashboard();
            status.setText("Student added successfully. Ranking updated using Merge Sort.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Enter a unique positive roll number, a name and marks from 0 to 100.",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSampleStudents() {
        addStudent(new Student(1001, "Aarav Sharma", new int[]{91, 88, 94, 86, 90}));
        addStudent(new Student(1002, "Diya Verma", new int[]{78, 82, 75, 80, 84}));
        addStudent(new Student(1003, "Rohan Patel", new int[]{65, 71, 69, 73, 68}));
        addStudent(new Student(1004, "Ananya Singh", new int[]{95, 97, 92, 94, 96}));
        addStudent(new Student(1005, "Kabir Khan", new int[]{35, 42, 38, 40, 33}));
        addStudent(new Student(1006, "Meera Gupta", new int[]{86, 89, 84, 88, 91}));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MarksGradeCalculator app = new MarksGradeCalculator();
            app.setVisible(true);
        });
    }
}
