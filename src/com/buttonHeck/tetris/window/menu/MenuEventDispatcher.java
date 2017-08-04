package com.buttonHeck.tetris.window.menu;

import com.buttonHeck.tetris.handler.AudioHandler;
import com.buttonHeck.tetris.util.Options;
import com.buttonHeck.tetris.window.game.GameWindow;

import javax.swing.*;

class MenuEventDispatcher {

    void setShowMessages(JCheckBox messagesCB) {
        Options.setShowMessages(messagesCB.isSelected());
    }

    void soundsSwitch(JToggleButton soundsTB) {
        boolean isOn = soundsTB.isSelected();
        AudioHandler.setSoundsEnabled(isOn);
        soundsTB.setText("Sounds " + (isOn ? "ON" : "OFF"));
    }

    void musicSwitch(JToggleButton musicTB) {
        boolean isOn = musicTB.isSelected();
        musicTB.setText("Music " + (isOn ? "ON" : "OFF"));
        if (isOn)
            AudioHandler.playMusic();
        else
            AudioHandler.stopMusic();
    }

    void setSpeedIncrease(JCheckBox speedIncreaseCB) {
        Options.setSpeedIncrease(speedIncreaseCB.isSelected());
    }

    void setShowNext(JCheckBox showNextCB) {
        Options.setShowNext(showNextCB.isSelected());
    }

    void speedSliderStateChanged(JLabel speedLabel, JSlider speedSlider) {
        speedLabel.setText("Speed: " + speedSlider.getModel().getValue());
        Options.setSpeed(speedSlider.getModel().getValue());
    }

    void setColumns(JComboBox<Integer> columnsCmB) {
        Options.setColumns(columnsCmB.getItemAt(columnsCmB.getSelectedIndex()));
    }

    void setRows(JComboBox<Integer> rowsCmB) {
        Options.setRows(rowsCmB.getItemAt(rowsCmB.getSelectedIndex()));
    }

    void startButtonPressed(StartWindow menu) {
        menu.setVisible(false);
        new GameWindow(menu);
    }
}
