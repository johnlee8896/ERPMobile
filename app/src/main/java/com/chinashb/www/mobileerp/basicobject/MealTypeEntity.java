package com.chinashb.www.mobileerp.basicobject;

import java.util.Date;

/**
 * Created by Paul on 2017/1/21.
 */

public class MealTypeEntity {
    private Date date;
    private boolean Breakfast;
    private boolean lunch;
    private boolean dinner;
    private boolean snack;


    public boolean isBreakfast() {
        return Breakfast;
    }

    public void setBreakfast(boolean breakfast) {
        Breakfast = breakfast;
    }

    public boolean isLunch() {
        return lunch;
    }

    public void setLunch(boolean lunch) {
        this.lunch = lunch;
    }

    public boolean isDinner() {
        return dinner;
    }

    public void setDinner(boolean dinner) {
        this.dinner = dinner;
    }

    public boolean isSnack() {
        return snack;
    }

    public void setSnack(boolean snack) {
        this.snack = snack;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
