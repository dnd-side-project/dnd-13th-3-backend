package org.minu.dnd13th3backend.user.type;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GoalType {
    FOCUS_IMPROVEMENT, 
    SLEEP_REGULARITY, 
    HEALTH_CARE, 
    NO_SCREEN, 
    CUSTOM;

    @JsonCreator
    public static GoalType fromString(String value) {
        if (value == null) {
            return null;
        }
        
        switch (value) {
            case "집중력을 높이고 산만함을 줄이고 싶어요":
                return FOCUS_IMPROVEMENT;
            case "수면 패턴을 개선하고 싶어요":
                return SLEEP_REGULARITY;
            case "건강 관리를 위해 스크린타임을 줄이고 싶어요":
                return HEALTH_CARE;
            case "스크린 없는 시간을 늘리고 싶어요":
                return NO_SCREEN;
            case "custom":
                return CUSTOM;
            default:
                if (value.equals(value.toUpperCase())) {
                    return GoalType.valueOf(value);
                }
                throw new IllegalArgumentException("Unknown GoalType: " + value);
        }
    }
}