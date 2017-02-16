package com.example.diazapps.startandend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class sqDB extends SQLiteOpenHelper {

    private final ArrayList<MyNotes> notesList = new ArrayList<>();

    public sqDB(Context context) {
        super(context, Constants.DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.DATABASE_NAME + "  " +
                " (" + Constants.NOTE_TEXT + " TEXT, " + Constants.DATE_TEXT + " TEXT, " + Constants.RETURN_DATE_TEXT + " TEXT, "
                + Constants.NOTE_ID + " INTEGER PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public void addreturnDate(MyNotes note, String date)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.RETURN_DATE_TEXT, date);
        values.put(Constants.NOTE_TEXT, note.getNoteText());
        if(note.getDate() == null)
        {
            values.put(Constants.DATE_TEXT, "");
        }

        db.insert(Constants.TABLE_NAME, null, values);
        db.close();

    }

    public void updatereturnDate(String date, int noteID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.RETURN_DATE_TEXT, date);

        String where = Constants.NOTE_ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(noteID)};

        db.update(Constants.TABLE_NAME, values, where, whereArgs);
    }

    public void addDate(MyNotes note, String date)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.DATE_TEXT, date);
        values.put(Constants.NOTE_TEXT, note.getNoteText());
        if(note.getReturndate() == null) {
            values.put(Constants.RETURN_DATE_TEXT, "");
        }

        db.insert(Constants.TABLE_NAME, null, values);
        db.close();

    }

    public void updateDate(String date, int noteID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.DATE_TEXT, date);

        String where = Constants.NOTE_ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(noteID)};

        db.update(Constants.TABLE_NAME, values, where, whereArgs);
    }

    public void addNotes(MyNotes note)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.NOTE_TEXT, note.getNoteText());
        values.put(Constants.DATE_TEXT, "");
        values.put(Constants.RETURN_DATE_TEXT, "");
        db.insert(Constants.TABLE_NAME, null, values);

        db.close();
    }

    public void deleteNote(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.NOTE_ID + " = ? ",
                new String[]{ String.valueOf(id)});

        db.close();

    }

    public void updateNotes(MyNotes note,int noteID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.NOTE_TEXT, note.getNoteText());

        String where = Constants.NOTE_ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(noteID)};

        db.update(Constants.TABLE_NAME, values, where, whereArgs);
        //db.insert(Constants.TABLE_NAME, null, values);

        db.close();

    }

    public ArrayList<MyNotes> getNotes(){

        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_NAME, new String[]{Constants.NOTE_ID, Constants.NOTE_TEXT, Constants.DATE_TEXT,
                Constants.RETURN_DATE_TEXT}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                MyNotes note = new MyNotes();
                note.setNoteText(cursor.getString(cursor.getColumnIndex(Constants.NOTE_TEXT)));
                note.setNoteID(cursor.getInt(cursor.getColumnIndex(Constants.NOTE_ID)));
                note.setDate(cursor.getString(cursor.getColumnIndex(Constants.DATE_TEXT)));
                note.setReturndate(cursor.getString(cursor.getColumnIndex(Constants.RETURN_DATE_TEXT)));

                notesList.add(note);

            } while (cursor.moveToNext());
        }

        return notesList;

    }

    public boolean updatecal(int date, int noteId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("REPLACE INTO sqDB (date, noteID) VALUES (" + Integer.toString(date) + ", "
                    + Integer.toString(noteId) + ")");
        return true;
    }

    public boolean isnoteThere(int noteId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM sqDB WHERE noteID = " + "'" + noteId + "'", null);
        if(c.getCount() <= 0){
            c.close();
            return false;
        }
        c.close();
        return true;
    }

    public String getnoteText(int noteId)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.NOTE_ID + " = " + "'" + noteId + "'", null);

        c.moveToFirst();

        int noteTextIndex = c.getColumnIndex("noteText");
        int dateIndex = c.getColumnIndex("date");
        int noteIDIndex = c.getColumnIndex("noteID");

        String noteText = c.getString(noteTextIndex);

        return noteText;
    }

    public boolean ifexists(String TableName, String dbfield, String fieldValue) {
            SQLiteDatabase db = this.getReadableDatabase();
            String Query = "SELECT * FROM " + TableName + " where " + dbfield + " = " + fieldValue;
            Cursor c = db.rawQuery(Query, null);
            if(c.getCount() <= 0){
                c.close();
                return false;
            }
            c.close();
            return true;
        }
    }
