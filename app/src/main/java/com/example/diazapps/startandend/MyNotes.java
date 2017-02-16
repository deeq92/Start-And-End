package com.example.diazapps.startandend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MyNotes {

    private String noteText;
    private int noteID;
    private String date;
    private String returndate;
    private Date DATE_date;
    public boolean isSelected = false;
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReturndate() {
        return returndate;
    }

    public void setReturndate(String returndate) {
        this.returndate = returndate;
    }

    public Date setDATE_date() {

        if(!date.equals(null)) {
            try {
                DATE_date = sdf.parse(returndate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return DATE_date;
    }

    public Date getDATE_dateValue()
    {
        return DATE_date;
    }

    public long getDaysLeft()
    {
        Date date;
        Date returnDate;
        long daysLeft = 0;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, c.getActualMinimum(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, c.getActualMinimum(Calendar.MINUTE));
        c.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND));
        c.set(Calendar.MILLISECOND, c.getActualMinimum(Calendar.MILLISECOND));

        Date todaysDate = c.getTime();

            if(!getReturndate().equals("")) {
                returnDate = getDATE_dateValue();

                daysLeft = (returnDate.getTime() - todaysDate.getTime());
                daysLeft = TimeUnit.DAYS.convert(daysLeft, TimeUnit.MILLISECONDS);
            }
            else
            {
                return daysLeft;
            }

        return daysLeft;
    }

    public boolean getisSelected() {
        return isSelected;
    }

    public void setisSelected(boolean selected) {
        isSelected = selected;
    }
}
