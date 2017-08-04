package com.buttonHeck.tetris.window.menu;

import javax.swing.*;
import java.awt.*;

class MenuInitializer {

    void setupNameLabel(JLabel label) {
        label.setText("Tetris v1.1");
        label.setForeground(Color.DARK_GRAY);
    }

    void setupLabel(JLabel label, String text) {
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setText(text);
    }

    void setupControlsText(JTextArea textArea) {
        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setText("A/D - move left/right\n" +
                "W/S - rotate\n" +
                "P - pause\n" +
                "SPACE - drop current figure\n" +
                "ESC - finish and back to menu\n\n" +
                "Have a nice play :)\n");
    }

    void setupSpeedSlider(JSlider slider) {
        slider.setMajorTickSpacing(5);
        slider.setMaximum(40);
        slider.setMinimum(15);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        slider.setFocusable(false);
        slider.setValue(25);
    }

    void setupStartButton(JButton button) {
        button.setText("Start Game");
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusable(false);
    }

    void setupCheckBox(JCheckBox checkBox, String text) {
        checkBox.setText(text);
        checkBox.setSelected(true);
    }

    void setupComboBox(JComboBox<Integer> comboBox, Integer[] values, int selected) {
        comboBox.setModel(new DefaultComboBoxModel<>(values));
        comboBox.getModel().setSelectedItem(selected);
    }

    void setupSoundSwitch(JToggleButton button, String text) {
        button.setText(text);
        button.setFocusable(false);
        button.setSelected(true);
    }
}
