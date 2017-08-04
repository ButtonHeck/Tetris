package com.buttonHeck.tetris.window.menu;

import com.buttonHeck.tetris.handler.AudioHandler;
import com.buttonHeck.tetris.util.Options;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class StartWindow extends JFrame {

    private MenuEventDispatcher eventDispatcher;
    private JButton startButton;
    private JComboBox<Integer> columnsCmB, rowsCmB;
    private JLabel nameLabel, columnsLabel, authorLabel, speedLabel, rowsLabel;
    private JSlider speedSlider;
    private JTextArea controlsText;
    private JCheckBox showNextCB, speedIncreaseCB, messagesCB;
    private JToggleButton musicTB, soundsTB;

    private StartWindow() {
        eventDispatcher = new MenuEventDispatcher();
        MenuInitializer initializer = new MenuInitializer();
        initializeLabelElements(initializer);
        initializeSpeedSlider(initializer);
        initializeStartButton(initializer);
        initializeGameplayOptions(initializer);
        initializeGameGridOptions(initializer);
        initializeSoundOptions(initializer);
        buildMenuViewStructure();
        initializeApplicationWindow();
        AudioHandler.playMusic();
    }

    private void initializeLabelElements(MenuInitializer initializer) {
        nameLabel = new JLabel();
        columnsLabel = new JLabel();
        rowsLabel = new JLabel();
        authorLabel = new JLabel();
        controlsText = new JTextArea();
        initializer.setupNameLabel(nameLabel);
        initializer.setupLabel(columnsLabel, "Columns");
        initializer.setupLabel(rowsLabel, "Rows");
        initializer.setupLabel(authorLabel, "ButtonHeck, 2017");
        initializer.setupControlsText(controlsText);
    }

    private void initializeSpeedSlider(MenuInitializer initializer) {
        speedSlider = new JSlider();
        speedLabel = new JLabel();
        initializer.setupSpeedSlider(speedSlider);
        speedSlider.addChangeListener(e -> eventDispatcher.speedSliderStateChanged(speedLabel, speedSlider));
        initializer.setupLabel(speedLabel, "Speed: " + speedSlider.getModel().getValue());
        Options.setSpeed(speedSlider.getValue());
    }

    private void initializeStartButton(MenuInitializer initializer) {
        startButton = new JButton();
        initializer.setupStartButton(startButton);
        startButton.addActionListener(e -> eventDispatcher.startButtonPressed(this));
    }

    private void initializeGameplayOptions(MenuInitializer initializer) {
        showNextCB = new JCheckBox();
        showNextCB.addChangeListener(e -> eventDispatcher.setShowNext(showNextCB));
        initializer.setupCheckBox(showNextCB, "Show next shape");
        speedIncreaseCB = new JCheckBox();
        speedIncreaseCB.addChangeListener(e -> eventDispatcher.setSpeedIncrease(speedIncreaseCB));
        initializer.setupCheckBox(speedIncreaseCB, "Increase speed");
        messagesCB = new JCheckBox();
        messagesCB.addChangeListener(e -> eventDispatcher.setShowMessages(messagesCB));
        initializer.setupCheckBox(messagesCB, "Show messages");
    }

    private void initializeGameGridOptions(MenuInitializer initializer) {
        rowsCmB = new JComboBox<>();
        initializer.setupComboBox(rowsCmB, new Integer[]{8, 9, 10}, 8);
        rowsCmB.addActionListener(e -> eventDispatcher.setRows(rowsCmB));
        columnsCmB = new JComboBox<>();
        initializer.setupComboBox(columnsCmB, new Integer[]{14, 15, 16}, 14);
        columnsCmB.addActionListener(e -> eventDispatcher.setColumns(columnsCmB));
    }

    private void initializeSoundOptions(MenuInitializer initializer) {
        musicTB = new JToggleButton();
        musicTB.addActionListener(e -> eventDispatcher.musicSwitch(musicTB));
        initializer.setupSoundSwitch(musicTB, "Music ON");
        soundsTB = new JToggleButton();
        soundsTB.addChangeListener(e -> eventDispatcher.soundsSwitch(soundsTB));
        initializer.setupSoundSwitch(soundsTB, "Sounds ON");
    }

    private void initializeApplicationWindow() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AudioHandler.finish();
                System.exit(0);
            }
        });
        setResizable(false);
        setTitle("Cheeky bricky: Menu");
        setLocationRelativeTo(null);
    }

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new StartWindow().setVisible(true));
    }

    //This INSANITY was created automatically in NetBeans's Form Editor
    private void buildMenuViewStructure() {
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
                                                        .addComponent(rowsCmB, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(columnsCmB, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                                                        .addComponent(columnsCmB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(columnsLabel))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(rowsCmB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
    }
}
