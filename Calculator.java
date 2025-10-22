import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dark themed calculator with Comic Sans font, orange buttons,
 * operation symbols shown on display, and a white triangle erase button.
 */
public class Calculator extends JFrame implements ActionListener {
    private final JTextField display = new JTextField("0");
    private String operator = "";
    private double firstOperand = 0;
    private boolean startNewNumber = true;
    private boolean showingResult = false;

    private final Color bgColor = new Color(34, 34, 34);
    private final Color panelColor = new Color(40, 40, 40);
    private final Color buttonOrange = new Color(255, 140, 0);
    private final Color buttonText = Color.BLACK;
    private final Color textColor = Color.WHITE;

    public Calculator() {
        super("Emma Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(360, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        Font baseFont = chooseFont("Comic Sans MS", Font.PLAIN, 20);

        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setBackground(bgColor);
        display.setForeground(textColor);
        display.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        display.setFont(baseFont.deriveFont(Font.PLAIN, 32f));

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(bgColor);
        main.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        main.add(display, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(5, 4, 8, 8));
        buttons.setBackground(panelColor);

        addButton(buttons, "C", baseFont);
        addButton(buttons, "÷", baseFont);
        addButton(buttons, "×", baseFont);
        addEraseButton(buttons, "◁", baseFont); // erase button

        addButton(buttons, "7", baseFont);
        addButton(buttons, "8", baseFont);
        addButton(buttons, "9", baseFont);
        addButton(buttons, "-", baseFont);

        addButton(buttons, "4", baseFont);
        addButton(buttons, "5", baseFont);
        addButton(buttons, "6", baseFont);
        addButton(buttons, "+", baseFont);

        addButton(buttons, "1", baseFont);
        addButton(buttons, "2", baseFont);
        addButton(buttons, "3", baseFont);
        addButton(buttons, "=", baseFont);

        addButton(buttons, "0", baseFont);
        addButton(buttons, ".", baseFont);

        JButton filler = new JButton();
        filler.setEnabled(false);
        filler.setBackground(panelColor);
        filler.setBorderPainted(false);
        buttons.add(filler);

        main.add(buttons, BorderLayout.CENTER);
        add(main);

        getContentPane().setBackground(bgColor);
        setVisible(true);
    }

    private Font chooseFont(String name, int style, int size) {
        try {
            return new Font(name, style, size);
        } catch (Exception e) {
            return new Font("SansSerif", style, size);
        }
    }

    private JButton createButton(String text, Font baseFont) {
        JButton b = new JButton(text);
        b.setFont(baseFont.deriveFont(Font.PLAIN, 22f));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setPreferredSize(new Dimension(70, 60));
        b.setBackground(buttonOrange);
        b.setForeground(buttonText);
        b.addActionListener(this);
        return b;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ("0123456789.".contains(command)) {
            if (startNewNumber || showingResult) {
                display.setText(command);
                startNewNumber = false;
                showingResult = false;
            } else {
                display.setText(display.getText() + command);
            }
        } else if ("+-×÷".contains(command)) {
            try {
                if (!operator.isEmpty() && !startNewNumber) {
                    // If there's already an operation pending, calculate it first
                    String[] parts = display.getText().split("[+\\-×÷]");
                    if (parts.length > 1) {
                        double secondOperand = Double.parseDouble(parts[1]);
                        calculateResult(secondOperand);
                    }
                }
                operator = command;
                firstOperand = Double.parseDouble(display.getText());
                if (!display.getText().endsWith(operator)) {
                    display.setText(display.getText() + operator);
                }
                startNewNumber = false;
            } catch (NumberFormatException ex) {
                display.setText("Error");
                startNewNumber = true;
            }
        } else if ("=".equals(command)) {
            try {
                String[] parts = display.getText().split("[+\\-×÷]");
                if (parts.length > 1) {
                    double secondOperand = Double.parseDouble(parts[1]);
                    calculateResult(secondOperand);
                    showingResult = true;
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                display.setText("Error");
                startNewNumber = true;
            }
        } else if ("C".equals(command)) {
            display.setText("0");
            operator = "";
            firstOperand = 0;
            startNewNumber = true;
            showingResult = false;
        } else if ("◁".equals(command)) {
            String current = display.getText();
            if (current.length() > 1) {
                String newText = current.substring(0, current.length() - 1);
                if ("+-×÷".contains(current.substring(current.length() - 1))) {
                    operator = "";
                }
                display.setText(newText);
            } else {
                display.setText("0");
                startNewNumber = true;
            }
            showingResult = false;
        }
    }

    private void addButton(JPanel container, String text, Font baseFont) {
        JButton b = createButton(text, baseFont);
        container.add(b);
    }

    private void addEraseButton(JPanel container, String text, Font baseFont) {
        JButton b = new JButton(text);
        b.setFont(baseFont.deriveFont(Font.PLAIN, 22f));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setPreferredSize(new Dimension(70, 60));
        b.setBackground(Color.WHITE);
        b.setForeground(buttonOrange);
        b.addActionListener(this);
        container.add(b);
    }

    private void calculateResult(double secondOperand) {
        double result = 0;
        try {
            switch (operator) {
                case "+":
                    result = firstOperand + secondOperand;
                    break;
                case "-":
                    result = firstOperand - secondOperand;
                    break;
                case "×":
                    result = firstOperand * secondOperand;
                    break;
                case "÷":
                    if (secondOperand == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    result = firstOperand / secondOperand;
                    break;
            }
            String formatted = String.format("%.12f", result).replaceAll("0*$", "").replaceAll("\\.$", "");
            display.setText(formatted);
            operator = "";
            firstOperand = result;
            startNewNumber = true;
        } catch (ArithmeticException e) {
            display.setText("Error");
            operator = "";
            startNewNumber = true;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Calculator());
    }
}
