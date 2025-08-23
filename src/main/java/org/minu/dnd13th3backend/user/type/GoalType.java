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
            case "규칙적인 수면 습관을 만들고 싶어요":
                return SLEEP_REGULARITY;
            case "집중력을 높이고 산만함을 줄이고 싶어요":
                return FOCUS_IMPROVEMENT;
            case "눈 건강을 지키고 싶어요":
                return HEALTH_CARE;
            case "혼자 있는 시간 디지털 없이 보내보기":
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