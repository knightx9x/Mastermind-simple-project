package mastermind;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Mastermind extends JFrame {
    private final String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}; // Digits 0 to 9
    private String[] secretCode;
    private int codeLength;
    private int maxAttempts;
    private int currentAttempt = 0;

    // GUI Components
    private final JTextField codeLengthField = new JTextField(5);
    private final JTextField maxAttemptsField = new JTextField(5);
    private final JTextField guessField = new JTextField(10);
    private final JTextArea feedbackArea = new JTextArea(10, 30);
    private final JButton startButton = new JButton("Start Game");
    private final JButton guessButton = new JButton("Submit Guess");

    public Mastermind() {
        setTitle("Mastermind Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout());

        // Settings Panel
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(3, 2));
        settingsPanel.add(new JLabel("Code Length (1-6):"));
        settingsPanel.add(codeLengthField);
        settingsPanel.add(new JLabel("Max Attempts (1-10):"));
        settingsPanel.add(maxAttemptsField);
        add(settingsPanel, BorderLayout.NORTH);

        // Feedback Area
        feedbackArea.setEditable(false);
        add(new JScrollPane(feedbackArea), BorderLayout.CENTER);

        // Game Panel
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new FlowLayout());
        gamePanel.add(new JLabel("Your Guess (e.g., 1234):"));
        gamePanel.add(guessField);
        gamePanel.add(guessButton);
        add(gamePanel, BorderLayout.SOUTH);

        // Start Button
        add(startButton, BorderLayout.WEST);

        // Action Listeners
        startButton.addActionListener(new StartGameListener());
        guessButton.addActionListener(new GuessListener());

        guessButton.setEnabled(false);
    }

    private void startGame() {
        try {
            codeLength = Integer.parseInt(codeLengthField.getText());
            maxAttempts = Integer.parseInt(maxAttemptsField.getText());
            if (codeLength < 1 || codeLength > 6 || maxAttempts < 1 || maxAttempts > 10) {
                JOptionPane.showMessageDialog(this, "Please enter valid ranges for code length (1-6) and max attempts (1-10).", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            secretCode = generateCode(numbers, codeLength);
            currentAttempt = 0;
            feedbackArea.setText("Game started! Enter your guesses (numbers between 0 and 9).\n");
            guessButton.setEnabled(true);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for code length and max attempts.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkGuess(String guess) {
        if (guess.length() != codeLength) {
            feedbackArea.append("Invalid input length. Your guess should be " + codeLength + " digits long. Try again.\n");
            return;
        }

        currentAttempt++;
        Feedback feedback = evaluateGuess(secretCode, guess.split(""));

        feedbackArea.append(String.format("Attempt %d: %s - Black Pegs: %d | White Pegs: %d\n",
                currentAttempt, guess, feedback.blackPegs, feedback.whitePegs));

        if (feedback.blackPegs == codeLength) {
            feedbackArea.append("Congratulations! You've cracked the code!\n");
            guessButton.setEnabled(false);
        } else if (currentAttempt >= maxAttempts) {
            feedbackArea.append("Game Over! The secret code was: " + String.join("", secretCode) + "\n");
            guessButton.setEnabled(false);
        }
    }

    private String[] generateCode(String[] numbers, int length) {
        Random random = new Random();
        String[] code = new String[length];
        for (int i = 0; i < length; i++) {
            code[i] = numbers[random.nextInt(numbers.length)];
        }
        return code;
    }

    private Feedback evaluateGuess(String[] secretCode, String[] guess) {
        int blackPegs = 0;
        int whitePegs = 0;
        boolean[] codeUsed = new boolean[secretCode.length];
        boolean[] guessUsed = new boolean[guess.length];

        // Check for black pegs (correct position and color)
        for (int i = 0; i < secretCode.length; i++) {
            if (secretCode[i].equals(guess[i])) {
                blackPegs++;
                codeUsed[i] = true;
                guessUsed[i] = true;
            }
        }

        // Check for white pegs (correct color, wrong position)
        for (int i = 0; i < secretCode.length; i++) {
            if (!codeUsed[i]) {
                for (int j = 0; j < guess.length; j++) {
                    if (!guessUsed[j] && secretCode[i].equals(guess[j])) {
                        whitePegs++;
                        guessUsed[j] = true;
                        break;
                    }
                }
            }
        }

        return new Feedback(blackPegs, whitePegs);
    }

    private static class Feedback {
        int blackPegs;
        int whitePegs;

        Feedback(int blackPegs, int whitePegs) {
            this.blackPegs = blackPegs;
            this.whitePegs = whitePegs;
        }
    }

    private class StartGameListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            startGame();
        }
    }

    private class GuessListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String guess = guessField.getText();
            checkGuess(guess);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Mastermind gui = new Mastermind();
            gui.setVisible(true);
        });
    }
}
