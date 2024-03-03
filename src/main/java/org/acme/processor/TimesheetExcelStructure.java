package org.acme.processor;

public enum TimesheetExcelStructure {
    MONTH(0),
    HOURS(1),
    TASK(2),

    TOTAL_HOURS(1),
    TOTAL_HOURS_FORMULA(2);

    int index;

    TimesheetExcelStructure(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
