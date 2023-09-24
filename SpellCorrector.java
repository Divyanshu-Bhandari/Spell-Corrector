import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

//We will require a txt file to show its working

public class SpellCorrector extends JFrame {
    private JTextArea inputField;
    private JPanel suggestionPanel;
    private Set<String> dictionary;
    private Map<JButton, String> suggestionMap;

    public SpellCorrector() {
        dictionary = new HashSet<>();
        suggestionMap = new HashMap<>();
        loadDictionary("C:\\Users\\divya\\Desktop\\dictionary.txt");
        setTitle("Spell Corrector");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage("D:\\All FIles\\Downloads\\SpellCorrecter.png"));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel inputLabel = new JLabel("Enter Text Here:");
        inputLabel.setFont(new Font("Impact", Font.BOLD, 16));
        inputPanel.add(inputLabel, BorderLayout.NORTH);

        inputField = new JTextArea();
        inputField.setFont(new Font("Roboto", Font.PLAIN, 16));
        inputField.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(inputField);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        inputPanel.add(scrollPane, BorderLayout.CENTER);

        contentPane.add(inputPanel, BorderLayout.CENTER);

        suggestionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        suggestionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        contentPane.add(suggestionPanel, BorderLayout.SOUTH);

        inputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSuggestions();
            }
        });

        setVisible(true);
    }

    private void updateSuggestions() {
        suggestionPanel.removeAll();
        String[] words = inputField.getText().split("\\s+");
        String currentWord = words[words.length - 1]; // Get the current word
        List<String> suggestions = findSuggestions(currentWord);
        addSuggestionBoxes(suggestions);
        revalidate();
        repaint();
    }

    private List<String> findSuggestions(String word) {
        List<String> suggestions = new ArrayList<>();
        if (dictionary.contains(word)) {
            suggestions.add(word);
        } else {
            suggestions.addAll(getCandidateWords(word));
        }
        return suggestions.subList(0, Math.min(suggestions.size(), 4));
    }

    private List<String> getCandidateWords(String word) {
        List<String> candidates = new ArrayList<>();
        for (String candidate : PeterNorvigSpellingCorrectionAlgorithm(word)) {
            if (dictionary.contains(candidate)) {
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    private Set<String> PeterNorvigSpellingCorrectionAlgorithm(String word) {
        Set<String> edits = new HashSet<>();
        // Generate deletion edits
        for (int i = 0; i < word.length(); i++) {
            String edit = word.substring(0, i) + word.substring(i + 1);
            edits.add(edit);
        }
        // Generate transposition edits
        for (int i = 0; i < word.length() - 1; i++) {
            String edit = word.substring(0, i) + word.charAt(i + 1) + word.charAt(i) + word.substring(i + 2);
            edits.add(edit);
        }
        // Generate alteration edits
        for (int i = 0; i < word.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                String edit = word.substring(0, i) + c + word.substring(i + 1);
                edits.add(edit);
            }
        }
        // Generate insertion edits
        for (int i = 0; i <= word.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                String edit = word.substring(0, i) + c + word.substring(i);
                edits.add(edit);
            }
        }
        return edits;
    }

    private void addSuggestionBoxes(List<String> suggestions) {
        JPanel suggestionBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        suggestionBox.setBorder(new EmptyBorder(5, 5, 5, 5));
        for (String suggestion : suggestions) {
            JButton suggestionButton = new JButton(suggestion);
            suggestionButton.setFont(new Font("Arial", Font.PLAIN, 16));
            suggestionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    replaceWordWithSuggestion(suggestion);
                }
            });
            suggestionBox.add(suggestionButton);
            suggestionMap.put(suggestionButton, suggestion);
        }
        suggestionPanel.add(suggestionBox);
    }

    private void replaceWordWithSuggestion(String suggestion) {
        String[] words = inputField.getText().split("\\s+");
        words[words.length - 1] = suggestion; // Replace the current word with the selected suggestion
        StringBuilder updatedText = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                updatedText.append(" ");
            }
            updatedText.append(words[i]);
        }
        inputField.setText(updatedText.toString());
        suggestionPanel.removeAll();
        suggestionMap.clear();
        revalidate();
        repaint();
    }

    private void loadDictionary(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String word = line.trim().toLowerCase();
                dictionary.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SpellCorrector::new);
    }
}
