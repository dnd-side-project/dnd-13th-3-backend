package org.minu.dnd13th3backend.user.type;


import com.fasterxml.jackson.annotation.JsonCreator;

public enum ScreenTimeGoalType {
    TWO_HOURS("2HOURS", 120),
    FOUR_HOURS("4HOURS", 240),
    SIX_HOURS("6HOURS", 360),
    EIGHT_HOURS("8HOURS", 480),
    CUSTOM("CUSTOM", 0);

    private final String value;
    private final int minutes;


    ScreenTimeGoalType(String value, int minutes) {
        this.value = value;
        this.minutes = minutes;
    }
    public String getValue() {
        return value;
    }


    public int getMinutes() {
        return minutes;
    }

    @JsonCreator
    public static ScreenTimeGoalType fromString(String value) {
        if (value == null) {
            return null;
        }

        switch (value) {
            case "120":
                return TWO_HOURS;
            case "240":
                return FOUR_HOURS;
            case "360":
                return SIX_HOURS;
            case "480":
                return EIGHT_HOURS;
            case "CUSTOM":
                return CUSTOM;
            default:
                if (value.equals(value.toUpperCase())) {
                    return ScreenTimeGoalType.valueOf(value);
                }
                return CUSTOM;
        }
    }
}

