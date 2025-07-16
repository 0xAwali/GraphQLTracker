package burpsuite;

import burp.api.montoya.persistence.PersistedObject;
import javax.swing.*;
import java.awt.*;
import static burpsuite.GraphQLTracker.*;

public class Settings extends JPanel {

    PersistedObject persistence;
    private JCheckBox bodyCheckBox;
    private JCheckBox urlCheckBox;
    private JCheckBox jsonCheckBox;

    public Settings(PersistedObject persistence) {
        this.persistence = persistence;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // Font definitions
        Font descriptionFont = new Font("Arial", Font.BOLD, 16);
        Font labelFont = new Font("Arial", Font.PLAIN, 16);
        Font textFieldFont = new Font("Arial", Font.PLAIN, 16);

        // Main container panel with BorderLayout
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);

        // Settings panel containing all components
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBackground(Color.WHITE);

        GridBagConstraints gbcSettings = new GridBagConstraints();
        gbcSettings.fill = GridBagConstraints.HORIZONTAL;
        gbcSettings.anchor = GridBagConstraints.NORTHWEST;
        gbcSettings.insets = new Insets(15, 15, 5, 15);
        gbcSettings.gridx = 0;
        gbcSettings.gridy = 0;

        // First row: GraphQL Endpoint
        String graphqlDescription = "Enter keyword to track GraphQL endpoint:";
        JLabel graphqlDescriptionLabel = new JLabel(graphqlDescription);
        graphqlDescriptionLabel.setFont(descriptionFont);
        graphqlDescriptionLabel.setToolTipText("Specify the keyword to identify GraphQL endpoints");
        gbcSettings.gridwidth = 3;
        settingsPanel.add(graphqlDescriptionLabel, gbcSettings);

        String graphql = persistence.getString(graphQLEndpoint);
        JLabel jLabelOne = new JLabel("GraphQL Endpoint");
        jLabelOne.setFont(labelFont);
        JTextField jTextFieldOne = new JTextField(graphql, 30);
        jTextFieldOne.setFont(textFieldFont);
        JButton jButtonOne = new JButton("Save");
        jButtonOne.setFont(labelFont);
        jButtonOne.addActionListener(e -> {
            String graphQLtext = jTextFieldOne.getText().trim();
            persistence.setString(graphQLEndpoint, graphQLtext);
            JOptionPane.showMessageDialog(this, graphQLtext + " saved", "GraphQL Tracker", JOptionPane.INFORMATION_MESSAGE);
        });
        gbcSettings.gridy = 1;
        gbcSettings.gridwidth = 1;
        settingsPanel.add(jLabelOne, gbcSettings);
        gbcSettings.gridx = 1;
        settingsPanel.add(jTextFieldOne, gbcSettings);
        gbcSettings.gridx = 2;
        settingsPanel.add(jButtonOne, gbcSettings);

        // Second row: Operation Name
        String operationNamesDescription = "Enter parameter for GraphQL operation names:";
        JLabel operationNamesDescriptionLabel = new JLabel(operationNamesDescription);
        operationNamesDescriptionLabel.setFont(descriptionFont);
        operationNamesDescriptionLabel.setToolTipText("Specify the parameter that returns operation names");
        gbcSettings.gridx = 0;
        gbcSettings.gridy = 2;
        gbcSettings.gridwidth = 3;
        settingsPanel.add(operationNamesDescriptionLabel, gbcSettings);

        String operationNames = persistence.getString(operationNamesParameter);
        JLabel jLabelThree = new JLabel("Operation Names");
        jLabelThree.setFont(labelFont);
        JTextField jTextFieldThree = new JTextField(operationNames, 30);
        jTextFieldThree.setFont(textFieldFont);
        JButton jButtonThree = new JButton("Save");
        jButtonThree.setFont(labelFont);
        jButtonThree.addActionListener(e -> {
            String operationNamesInput = jTextFieldThree.getText().trim();
            persistence.setString(operationNamesParameter, operationNamesInput);
            JOptionPane.showMessageDialog(this, operationNamesInput + " saved", "GraphQL Tracker", JOptionPane.INFORMATION_MESSAGE);
        });
        gbcSettings.gridx = 0;
        gbcSettings.gridy = 3;
        gbcSettings.gridwidth = 1;
        settingsPanel.add(jLabelThree, gbcSettings);
        gbcSettings.gridx = 1;
        settingsPanel.add(jTextFieldThree, gbcSettings);
        gbcSettings.gridx = 2;
        settingsPanel.add(jButtonThree, gbcSettings);

        // Third row: Unique Identifier
        JPanel uniqueIdentifierPanel = new JPanel(new GridBagLayout());
        uniqueIdentifierPanel.setBorder(BorderFactory.createTitledBorder("Unique Identifier Options"));
        uniqueIdentifierPanel.setBackground(Color.WHITE);

        String docIDDescription = "Enter parameters for unique identifier to track operations:";
        JLabel docIDDescriptionLabel = new JLabel(docIDDescription);
        docIDDescriptionLabel.setFont(descriptionFont);
        docIDDescriptionLabel.setToolTipText("Specify parameters for unique operation tracking");
        GridBagConstraints gbcUnique = new GridBagConstraints();
        gbcUnique.fill = GridBagConstraints.HORIZONTAL;
        gbcUnique.anchor = GridBagConstraints.NORTHWEST;
        gbcUnique.insets = new Insets(15, 15, 5, 15);
        gbcUnique.gridx = 0;
        gbcUnique.gridy = 0;
        gbcUnique.gridwidth = 3;
        uniqueIdentifierPanel.add(docIDDescriptionLabel, gbcUnique);

        String docID = persistence.getString(docIDParameter);
        JLabel jLabelTwo = new JLabel("Unique Identifier *");
        jLabelTwo.setFont(labelFont);
        JTextField jTextFieldTwo = new JTextField(docID, 30);
        jTextFieldTwo.setFont(textFieldFont);
        JButton jButtonTwo = new JButton("Save");
        jButtonTwo.setFont(labelFont);
        jButtonTwo.addActionListener(e -> {
            String docIDInput = jTextFieldTwo.getText().trim();
            if (docIDInput.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Unique Identifier cannot be empty", "GraphQL Tracker", JOptionPane.ERROR_MESSAGE);
            } else {
                persistence.setString(docIDParameter, docIDInput);
                JOptionPane.showMessageDialog(this, docIDInput + " saved", "GraphQL Tracker", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        gbcUnique.gridx = 0;
        gbcUnique.gridy = 1;
        gbcUnique.gridwidth = 1;
        uniqueIdentifierPanel.add(jLabelTwo, gbcUnique);
        gbcUnique.gridx = 1;
        gbcUnique.gridy = 1;
        uniqueIdentifierPanel.add(jTextFieldTwo, gbcUnique);
        gbcUnique.gridx = 2;
        gbcUnique.gridy = 1;
        uniqueIdentifierPanel.add(jButtonTwo, gbcUnique);

        String docIDTwo = persistence.getString(docIDParameterTwo);
        JLabel jLabelParameterTwo = new JLabel("Unique Identifier");
        jLabelParameterTwo.setFont(labelFont);
        JTextField jTextFieldParameterTwo = new JTextField(docIDTwo, 30);
        jTextFieldParameterTwo.setFont(textFieldFont);
        JButton jButtonParameterTwo = new JButton("Save");
        jButtonParameterTwo.setFont(labelFont);
        jButtonParameterTwo.addActionListener(e -> {
            String parameterTwoInput = jTextFieldParameterTwo.getText().trim();
            persistence.setString(docIDParameterTwo, parameterTwoInput);
            JOptionPane.showMessageDialog(this, parameterTwoInput + " saved", "GraphQL Tracker", JOptionPane.INFORMATION_MESSAGE);
        });
        gbcUnique.gridx = 0;
        gbcUnique.gridy = 2;
        gbcUnique.gridwidth = 1;
        uniqueIdentifierPanel.add(jLabelParameterTwo, gbcUnique);
        gbcUnique.gridx = 1;
        uniqueIdentifierPanel.add(jTextFieldParameterTwo, gbcUnique);
        gbcUnique.gridx = 2;
        uniqueIdentifierPanel.add(jButtonParameterTwo, gbcUnique);

        gbcUnique.gridx = 0;
        gbcUnique.gridy = 3;
        gbcUnique.gridwidth = 3;
        gbcUnique.weighty = 1.0;
        uniqueIdentifierPanel.add(new JLabel(), gbcUnique);

        gbcSettings.gridx = 0;
        gbcSettings.gridy = 4;
        gbcSettings.gridwidth = 3;
        settingsPanel.add(uniqueIdentifierPanel, gbcSettings);

        // Fourth row: Query Parameter
        String variablesDescription = "Enter parameter name for QueryQL sub-tab in Burp Suite:";
        JLabel variablesDescriptionLabel = new JLabel(variablesDescription);
        variablesDescriptionLabel.setFont(descriptionFont);
        variablesDescriptionLabel.setToolTipText("Specify the parameter for QueryQL display");
        gbcSettings.gridx = 0;
        gbcSettings.gridy = 5;
        gbcSettings.gridwidth = 3;
        settingsPanel.add(variablesDescriptionLabel, gbcSettings);

        String variables = persistence.getString(variablesParameter);
        JLabel jLabelFour = new JLabel("Query Parameter");
        jLabelFour.setFont(labelFont);
        JTextField jTextFieldFour = new JTextField(variables, 30);
        jTextFieldFour.setFont(textFieldFont);
        JButton jButtonFour = new JButton("Save");
        jButtonFour.setFont(labelFont);
        jButtonFour.addActionListener(e -> {
            String variablesInput = jTextFieldFour.getText().trim();
            persistence.setString(variablesParameter, variablesInput);
            JOptionPane.showMessageDialog(this, variablesInput + " saved", "GraphQL Tracker", JOptionPane.INFORMATION_MESSAGE);
        });
        gbcSettings.gridx = 0;
        gbcSettings.gridy = 6;
        gbcSettings.gridwidth = 1;
        settingsPanel.add(jLabelFour, gbcSettings);
        gbcSettings.gridx = 1;
        settingsPanel.add(jTextFieldFour, gbcSettings);
        gbcSettings.gridx = 2;
        settingsPanel.add(jButtonFour, gbcSettings);

        // Fifth row: Checkboxes (listed vertically)
        String checkBoxDescription = "Select HTTP parameter type for actions:";
        JLabel checkBoxDescriptionLabel = new JLabel(checkBoxDescription);
        checkBoxDescriptionLabel.setFont(descriptionFont);
        checkBoxDescriptionLabel.setToolTipText("Choose where to apply GraphQL tracking actions");
        gbcSettings.gridx = 0;
        gbcSettings.gridy = 7;
        gbcSettings.gridwidth = 3;
        settingsPanel.add(checkBoxDescriptionLabel, gbcSettings);

        bodyCheckBox = new JCheckBox("BODY");
        urlCheckBox = new JCheckBox("URL");
        jsonCheckBox = new JCheckBox("JSON");
        bodyCheckBox.setFont(labelFont);
        urlCheckBox.setFont(labelFont);
        jsonCheckBox.setFont(labelFont);
        bodyCheckBox.setFocusable(true);
        urlCheckBox.setFocusable(true);
        jsonCheckBox.setFocusable(true);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(bodyCheckBox);
        buttonGroup.add(urlCheckBox);
        buttonGroup.add(jsonCheckBox);
        bodyCheckBox.setSelected(true);

        bodyCheckBox.addActionListener(e -> {
            if (bodyCheckBox.isSelected()) {
                persistence.setString(parameterType, "BODY");
                JOptionPane.showMessageDialog(this, "BODY selection saved", "GraphQL Tracker", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        urlCheckBox.addActionListener(e -> {
            if (urlCheckBox.isSelected()) {
                persistence.setString(parameterType, "URL");
                JOptionPane.showMessageDialog(this, "URL selection saved", "GraphQL Tracker", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        jsonCheckBox.addActionListener(e -> {
            if (jsonCheckBox.isSelected()) {
                persistence.setString(parameterType, "JSON");
                JOptionPane.showMessageDialog(this, "JSON selection saved", "GraphQL Tracker", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        gbcSettings.gridx = 0;
        gbcSettings.gridy = 8;
        gbcSettings.gridwidth = 1;
        settingsPanel.add(bodyCheckBox, gbcSettings);
        gbcSettings.gridy = 9;
        settingsPanel.add(urlCheckBox, gbcSettings);
        gbcSettings.gridy = 10;
        settingsPanel.add(jsonCheckBox, gbcSettings);

        // Load the last saved user choice
        String lastChoice = persistence.getString(parameterType);
        if (lastChoice != null) {
            switch (lastChoice) {
                case "BODY" -> bodyCheckBox.setSelected(true);
                case "URL" -> urlCheckBox.setSelected(true);
                case "JSON" -> jsonCheckBox.setSelected(true);
            }
        }

        // Add vertical glue to push content to the top
        gbcSettings.gridx = 0;
        gbcSettings.gridy = 11;
        gbcSettings.gridwidth = 3;
        gbcSettings.weighty = 1.0;
        settingsPanel.add(Box.createVerticalGlue(), gbcSettings);

        // Wrap settingsPanel in a JScrollPane to ensure all content is visible
        JScrollPane scrollPane = new JScrollPane(settingsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);

        // Copyright Section (Right-Aligned)
        JPanel copyrightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        copyrightPanel.setBackground(Color.WHITE);

        // Copyright symbol
        JLabel copyrightLabel = new JLabel("© ");
        copyrightLabel.setFont(new Font("Arial", Font.BOLD, 40));
        copyrightPanel.add(copyrightLabel);

        // ISEC logo
        ImageIcon imageIcon = new ImageIcon(Settings.class.getResource("/iSec.png"));
        Image scaledImage = imageIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setToolTipText("iSec Group logo");
        copyrightPanel.add(logoLabel);

        // Additional text
        JLabel infoLabel = new JLabel("2025 ||");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        infoLabel.setForeground(new Color(70, 70, 70));
        copyrightPanel.add(infoLabel);

        ImageIcon LinkedIn = new ImageIcon(Settings.class.getResource("/in.png"));
        Image LinkedINImage = LinkedIn.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JLabel LinkedINLabel = new JLabel(new ImageIcon(LinkedINImage));
        LinkedINLabel.setToolTipText("LinkedIn Logo");
        copyrightPanel.add(LinkedINLabel);

        ImageIcon Twitter = new ImageIcon(Settings.class.getResource("/X.png"));
        Image TwitterImage = Twitter.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JLabel TwitterLabel = new JLabel(new ImageIcon(TwitterImage));
        TwitterLabel.setToolTipText("Twitter Logo");
        copyrightPanel.add(TwitterLabel);


        JLabel labelinkedin = new JLabel("0xAwali");
        labelinkedin.setFont(new Font("Arial", Font.BOLD, 24));
        labelinkedin.setForeground(new Color(70, 70, 70));
        copyrightPanel.add(labelinkedin);


        // Container with padding (right-aligned)
        JPanel copyrightContainer = new JPanel(new BorderLayout());
        copyrightContainer.setBackground(Color.WHITE);
        copyrightContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 15));
        copyrightContainer.add(copyrightPanel, BorderLayout.EAST);

        // Add components to main container
        mainContainer.add(scrollPane, BorderLayout.CENTER);
        mainContainer.add(copyrightContainer, BorderLayout.SOUTH);

        // Add the main container to the main layout
        this.add(mainContainer, BorderLayout.CENTER);
    }
}