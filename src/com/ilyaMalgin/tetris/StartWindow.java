package com.ilyaMalgin.tetris;

import org.lwjgl.openal.AL;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class StartWindow extends JFrame {

    private JButton startButton, hiScoreResetButton;
    private JComboBox<Integer> columnsComboBox, rowsComboBox;
    private JLabel nameLabel, columnsLabel, authorLabel, speedLabel, rowsLabel, hiScoreLabel;
    private JSlider speedSlider;
    private JTextArea controlsText;
    private JCheckBox showNextCB, speedIncreaseCB, messagesCB;
    private JToggleButton musicTB, soundsTB;

    public StartWindow() {
        initComponents();
    }

    private void initComponents() {
        nameLabel = new JLabel();
        startButton = new JButton();
        hiScoreResetButton = new JButton();
        columnsComboBox = new JComboBox<>();
        rowsComboBox = new JComboBox<>();
        speedLabel = new JLabel();
        columnsLabel = new JLabel();
        rowsLabel = new JLabel();
        authorLabel = new JLabel();
        hiScoreLabel = new JLabel();
        speedSlider = new JSlider();
        controlsText = new JTextArea();
        showNextCB = new JCheckBox();
        speedIncreaseCB = new JCheckBox();
        messagesCB = new JCheckBox();
        musicTB = new JToggleButton();
        soundsTB = new JToggleButton();

        nameLabel.setText("Tetris v0.9");

        startButton.setText("Start Game");
        startButton.setFocusable(false);
        startButton.addActionListener(this::startButtonPressed);

        columnsComboBox.setModel(new DefaultComboBoxModel<>(new Integer[]{14, 15, 16}));
        columnsComboBox.addActionListener(this::setColumns);
        columnsComboBox.getModel().setSelectedItem(14);

        rowsComboBox.setModel(new DefaultComboBoxModel<>(new Integer[]{8, 9, 10}));
        rowsComboBox.addActionListener(this::setRows);
        rowsComboBox.getModel().setSelectedItem(8);

        columnsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        columnsLabel.setText("Columns:");

        rowsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        rowsLabel.setText("Rows:");

        authorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        authorLabel.setText("Made by Ilya Malgin, 2017");

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
        controlsText.setText("A/D - move left/right\nW/S - rotate clockwise/counterclockwise\nP - pause\nSPACE - drop current figure\n\nHave a nice play :)\n");

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

        renewHiScore();

        hiScoreResetButton.setText("Reset");
        hiScoreResetButton.setFocusable(false);
        hiScoreResetButton.addActionListener(this::resetHiScore);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(hiScoreResetButton)
                                                .addGap(42, 42, 42)
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
                                                        .addComponent(columnsLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(rowsLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(rowsComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(columnsComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(speedIncreaseCB)
                                                        .addComponent(showNextCB)
                                                        .addComponent(messagesCB)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(musicTB, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(soundsTB, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(hiScoreLabel)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(hiScoreLabel)
                                .addGap(8, 8, 8)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(startButton)
                                        .addComponent(hiScoreResetButton))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(speedLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(speedSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(columnsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(columnsLabel))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(rowsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
                AudioHolder.music.stop();
                AL.destroy();
                System.exit(0);
            }
        });
        setResizable(false);
        setLocationRelativeTo(null);
        AudioHolder.music.loop();
    }

    private void resetHiScore(ActionEvent actionEvent) {
        try (FileWriter writer = new FileWriter(new File("res/hiscore.txt"))) {
            writer.write(String.valueOf(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        renewHiScore();
    }

    public void renewHiScore() {
        try (Scanner scanner = new Scanner(new File("res/hiscore.txt"))) {
            int hiScore = scanner.nextInt();
            hiScoreLabel.setText("Hi-score: " + hiScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setShowMessages(ChangeEvent changeEvent) {
        Options.setShowMessages(messagesCB.isSelected());
    }

    private void soundsSwitch(ChangeEvent changeEvent) {
        boolean isOn = soundsTB.isSelected();
        AudioHolder.setSoundsEnabled(isOn);
        soundsTB.setText("Sounds " + (isOn ? "ON" : "OFF"));
    }

    private void musicSwitch(ActionEvent actionEvent) {
        boolean isOn = musicTB.isSelected();
        musicTB.setText("Music " + (isOn ? "ON" : "OFF"));
        if (isOn)
            AudioHolder.music.play();
        else
            AudioHolder.music.stop();
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
        Options.setColumns(columnsComboBox.getItemAt(columnsComboBox.getSelectedIndex()));
    }

    private void setRows(ActionEvent event) {
        Options.setRows(rowsComboBox.getItemAt(rowsComboBox.getSelectedIndex()));
    }

    private void startButtonPressed(ActionEvent evt) {
        setVisible(false);
        Options.setSpeed(speedSlider.getValue());
        AudioHolder.buttonClick();
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
