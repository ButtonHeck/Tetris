package com.ilyaMalgin.tetris;

import com.ilyaMalgin.tetris.controllers.AudioController;
import com.ilyaMalgin.tetris.util.Options;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class StartWindow extends JFrame {

    /*
    CB == CheckBox
    TB == ToggleButton
     */
    private JButton startButton;
    private JComboBox<Integer> columnsCB, rowsCB;
    private JLabel nameLabel, columnsLabel, authorLabel, speedLabel, rowsLabel;
    private JSlider speedSlider;
    private JTextArea controlsText;
    private JCheckBox showNextCB, speedIncreaseCB, messagesCB;
    private JToggleButton musicTB, soundsTB;

    private StartWindow() {
        initComponents();
    }

    private void initComponents() {
        nameLabel = new JLabel();
        startButton = new JButton();
        columnsCB = new JComboBox<>();
        rowsCB = new JComboBox<>();
        speedLabel = new JLabel();
        columnsLabel = new JLabel();
        rowsLabel = new JLabel();
        authorLabel = new JLabel();
        speedSlider = new JSlider();
        controlsText = new JTextArea();
        showNextCB = new JCheckBox();
        speedIncreaseCB = new JCheckBox();
        messagesCB = new JCheckBox();
        musicTB = new JToggleButton();
        soundsTB = new JToggleButton();

        nameLabel.setText("Tetris v1.1");
        nameLabel.setForeground(Color.DARK_GRAY);

        startButton.setText("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setFocusable(false);
        startButton.addActionListener(this::startButtonPressed);

        columnsCB.setModel(new DefaultComboBoxModel<>(new Integer[]{14, 15, 16}));
        columnsCB.addActionListener(this::setColumns);
        columnsCB.getModel().setSelectedItem(14);

        rowsCB.setModel(new DefaultComboBoxModel<>(new Integer[]{8, 9, 10}));
        rowsCB.addActionListener(this::setRows);
        rowsCB.getModel().setSelectedItem(8);

        columnsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        columnsLabel.setText("Columns:");

        rowsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rowsLabel.setText("Rows:");

        authorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        authorLabel.setText("Made by Ilya Malgin, 2017");
        authorLabel.setForeground(Color.DARK_GRAY);

        speedSlider.setMajorTickSpacing(5);
        speedSlider.setMaximum(40);
        speedSlider.setMinimum(15);
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
        controlsText.setText("A/D - move left/right\nW/S - rotate\nP - pause\nSPACE - drop current figure\nESC - finish and back to menu\n\nHave a nice play :)\n");

        showNextCB.setText("Show next shape");
        showNextCB.addChangeListener(this::setShowNext);
        showNextCB.setSelected(true);

        speedIncreaseCB.addChangeListener(this::setSpeedIncrease);
        speedIncreaseCB.setSelected(true);
        speedIncreaseCB.setText("Increase speed");

        messagesCB.setText("Show messages");
        messagesCB.addChangeListener(this::setShowMessages);
        messagesCB.setSelected(true);

        musicTB.setText("Music ON");
        musicTB.setFocusable(false);
        musicTB.addActionListener(this::musicSwitch);
        musicTB.setSelected(true);

        soundsTB.setText("Sounds ON");
        soundsTB.setFocusable(false);
        soundsTB.addChangeListener(this::soundsSwitch);
        soundsTB.setSelected(true);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(90, 90, 90)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(startButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(speedLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(101, 101, 101))
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(nameLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(authorLabel))
                                        .addComponent(controlsText)
                                        .addComponent(speedSlider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(columnsLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                        .addComponent(rowsLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(rowsCB, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(columnsCB, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(speedIncreaseCB)
                                                        .addComponent(showNextCB)
                                                        .addComponent(messagesCB)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(musicTB, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(soundsTB, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(startButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(speedLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(speedSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(columnsCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(columnsLabel))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(rowsCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(rowsLabel))
                                                .addGap(36, 36, 36)
                                                .addComponent(controlsText, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(musicTB)
                                                        .addComponent(soundsTB))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(authorLabel)
                                                        .addComponent(nameLabel)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(showNextCB)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(speedIncreaseCB)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(messagesCB)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        pack();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AudioController.finish();
                System.exit(0);
            }
        });
        setResizable(false);
        setTitle("Cheeky bricky: Menu");
        setLocationRelativeTo(null);
        AudioController.playMusic();
    }

    private void setShowMessages(ChangeEvent changeEvent) {
        Options.setShowMessages(messagesCB.isSelected());
    }

    private void soundsSwitch(ChangeEvent changeEvent) {
        boolean isOn = soundsTB.isSelected();
        AudioController.setSoundsEnabled(isOn);
        soundsTB.setText("Sounds " + (isOn ? "ON" : "OFF"));
    }

    private void musicSwitch(ActionEvent actionEvent) {
        boolean isOn = musicTB.isSelected();
        musicTB.setText("Music " + (isOn ? "ON" : "OFF"));
        if (isOn)
            AudioController.playMusic();
        else
            AudioController.stopMusic();
    }

    private void setSpeedIncrease(ChangeEvent actionEvent) {
        Options.setSpeedIncrease(speedIncreaseCB.isSelected());
    }

    private void setShowNext(ChangeEvent actionEvent) {
        Options.setShowNext(showNextCB.isSelected());
    }

    private void speedSliderStateChanged(ChangeEvent evt) {
        speedLabel.setText("Speed: " + speedSlider.getModel().getValue());
        Options.setSpeed(speedSlider.getModel().getValue());
    }

    private void setColumns(ActionEvent evt) {
        Options.setColumns(columnsCB.getItemAt(columnsCB.getSelectedIndex()));
    }

    private void setRows(ActionEvent event) {
        Options.setRows(rowsCB.getItemAt(rowsCB.getSelectedIndex()));
    }

    private void startButtonPressed(ActionEvent evt) {
        setVisible(false);
        Options.setSpeed(speedSlider.getValue());
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
