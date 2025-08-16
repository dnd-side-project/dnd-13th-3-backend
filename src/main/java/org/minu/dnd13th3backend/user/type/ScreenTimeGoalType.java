package org.minu.dnd13th3backend.user.type;

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

    public int getMinutes() { return minutes; }
}