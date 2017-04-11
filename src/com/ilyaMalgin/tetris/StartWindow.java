package com.ilyaMalgin.tetris;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;

public class StartWindow extends JFrame {

    private JButton startButton;
    private JComboBox<Integer> columnsComboBox, rowsComboBox;
    private JLabel nameLabel, columnsLabel, authorLabel, speedLabel, rowsLabel;
    private JSlider speedSlider;
    private JTextArea controlsText;
    private JCheckBox showNextCB;

    public StartWindow() {
        initComponents();
    }

    private void initComponents() {
        nameLabel = new JLabel();
        startButton = new JButton();
        columnsComboBox = new JComboBox<>();
        rowsComboBox = new JComboBox<>();
        speedLabel = new JLabel();
        columnsLabel = new JLabel();
        rowsLabel = new JLabel();
        authorLabel = new JLabel();
        speedSlider = new JSlider();
        controlsText = new JTextArea();
        showNextCB = new JCheckBox();

        nameLabel.setText("Tetris v0.76");

        startButton.setText("Start Game");
        startButton.setFocusable(false);
        startButton.addActionListener(this::startButtonPressed);

        columnsComboBox.setModel(new DefaultComboBoxModel<>(new Integer[]{14, 15, 16}));
        columnsComboBox.addActionListener(this::setColumns);
        columnsComboBox.getModel().setSelectedItem(14);

        rowsComboBox.setModel(new DefaultComboBoxModel<>(new Integer[]{8, 9, 10}));
        rowsComboBox.addActionListener(this::setRows);
        rowsComboBox.getModel().setSelectedItem(8);

        columnsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        columnsLabel.setText("Columns:");

        rowsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rowsLabel.setText("Rows:");

        authorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        authorLabel.setText("Made by Ilya Malgin, 2017");

        speedSlider.setMajorTickSpacing(5);
        speedSlider.setMaximum(40);
        speedSlider.setMinimum(10);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setSnapToTicks(true);
        speedSlider.setFocusable(false);
        speedSlider.addChangeListener(this::speedSliderStateChanged);
        speedSlider.setValue(25);

        speedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        speedLabel.setText("Speed: " + speedSlider.getModel().getValue());

        controlsText.setEditable(false);
        controlsText.setColumns(20);
        controlsText.setRows(5);
        controlsText.setText("A/D - move left/right\nW/S - rotate clockwise/counterclockwise\nP - pause\nSPACE - drop current figure\n\nHave a nice play :)\n");

        showNextCB.setText("Show next shape");
        showNextCB.addActionListener(this::setShowNext);
        showNextCB.setSelected(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(authorLabel))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(nameLabel))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(102, 102, 102)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(startButton, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                                                                        .addComponent(speedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                                        .addComponent(columnsComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                        .addComponent(columnsLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE))
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(rowsComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                        .addComponent(rowsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(showNextCB)
                                                                                .addGap(0, 0, Short.MAX_VALUE))))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(81, 81, 81))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(controlsText)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(nameLabel)
                                .addGap(2, 2, 2)
                                .addComponent(startButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(speedLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(columnsLabel)
                                        .addComponent(rowsLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(columnsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(rowsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(showNextCB)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(controlsText, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(authorLabel)
                                .addContainerGap())
        );
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void setShowNext(ActionEvent actionEvent) {
        Options.setShowNext(showNextCB.isSelected());
    }

    private void speedSliderStateChanged(ChangeEvent evt) {
        speedLabel.setText("Speed: " + speedSlider.getModel().getValue());
        Options.setSpeed(speedSlider.getModel().getValue());
    }

    private void setColumns(ActionEvent evt) {
        Options.setColumns(columnsComboBox.getItemAt(columnsComboBox.getSelectedIndex()));
    }

    private void setRows(ActionEvent event) {
        Options.setRows(rowsComboBox.getItemAt(rowsComboBox.getSelectedIndex()));
    }

    private void startButtonPressed(ActionEvent evt) {
        setVisible(false);
        new Game(this);
    }

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StartWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        SwingUtilities.invokeLater(() -> new StartWindow().setVisible(true));
    }
}
