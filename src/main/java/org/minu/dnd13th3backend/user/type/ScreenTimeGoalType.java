package org.minu.dnd13th3backend.user.type;

public enum ScreenTimeGoalType {
    TWO_HOURS("2HOURS"), 
    FOUR_HOURS("4HOURS"), 
    SIX_HOURS("6HOURS"), 
    EIGHT_HOURS("8HOURS"), 
    CUSTOM("CUSTOM");
    
    private final String value;
    
    ScreenTimeGoalType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}