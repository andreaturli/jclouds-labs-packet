package org.jclouds.packet.domain;

/**
 * Performs an action for the given device. Possible actions include:

 power_on
 power_off
 reboot
 rescue: reboot the device into rescue OS.
 */
public enum ActionType {
    
    POWER_ON ("power_on"),
    POWER_OFF ("power_off"),
    REBOOT ("reboot"),
    RESCUE ("rescue");

    private final String type;

    ActionType(String type) {
        this.type = type;
    }
}
