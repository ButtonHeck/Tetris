package com.ilyaMalgin.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class StartWindow extends JFrame {

    private JButton startButton;
    private JComboBox<Integer> columnsComboBox;
    private JLabel nameLabel, columnsLabel, authorLabel, speedLabel;
    private JSlider speedSlider;

    public StartWindow() {
        initComponents();
    }

    private void initComponents() {
        nameLabel = new javax.swing.JLabel();
        startButton = new javax.swing.JButton();
        columnsComboBox = new JComboBox<>();
        speedLabel = new javax.swing.JLabel();
        columnsLabel = new javax.swing.JLabel();
        authorLabel = new javax.swing.JLabel();
        speedSlider = new javax.swing.JSlider();

        nameLabel.setText("Tetris v0.31");

        startButton.setText("Start Game");
        startButton.setFocusable(false);
        startButton.addActionListener(this::startButtonPressed);

        columnsComboBox.setModel(new DefaultComboBoxModel<>(new Integer[]{14, 15, 16}));
        columnsComboBox.addActionListener(this::setColumns);
        columnsComboBox.getModel().setSelectedItem(14);

        columnsLabel.setText("Choose number of columns");

        authorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        authorLabel.setText("Made by Ilya Malgin");

        speedSlider.setMajorTickSpacing(5);
        speedSlider.setMaximum(40);
        speedSlider.setMinimum(10);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setSnapToTicks(true);
        speedSlider.setFocusable(false);
        speedSlider.addChangeListener(this::speedSliderStateChanged);
        speedSlider.setValue(25);

        speedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        speedLabel.setText("Speed:" + speedSlider.getModel().getValue());

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
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(columnsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(startButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(columnsComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(speedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(0, 81, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(nameLabel)
                                .addGap(18, 18, 18)
                                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(speedLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(columnsLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(columnsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                                .addComponent(authorLabel)
                                .addContainerGap())
        );
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void speedSliderStateChanged(javax.swing.event.ChangeEvent evt) {
        speedLabel.setText("Speed: " + speedSlider.getModel().getValue());
        Options.setSpeed(speedSlider.getModel().getValue());
    }

    private void setColumns(ActionEvent evt) {
        Options.setColumns(columnsComboBox.getItemAt(columnsComboBox.getSelectedIndex()));
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
        EventQueue.invokeLater(() -> new StartWindow().setVisible(true));
    }
}
