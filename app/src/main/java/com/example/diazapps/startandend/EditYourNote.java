package com.example.diazapps.startandend;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;

public class EditYourNote extends Activity implements TextWatcher, PopupMenu.OnMenuItemClickListener {

    int noteId;
    String notePosition;
    Calendar cal = Calendar.getInstance();
    private sqDB db;
    private SQLiteDatabase readable;
    EditText editText;
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
    Boolean isChecked;
    Boolean isCheckedDesc;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_your_note);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bar_color)));

        pref = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);

        db = new sqDB(this);
        readable = db.getReadableDatabase();

        editText = (EditText) findViewById(R.id.editText);

        TextView startAlert = (TextView) findViewById(R.id.startAlert);
        TextView returnAlert = (TextView) findViewById(R.id.returnAlert);
        startAlert.setVisibility(View.INVISIBLE);
        returnAlert.setVisibility(View.INVISIBLE);

        Intent i = getIntent();
        noteId = i.getIntExtra("noteId", -1);
        notePosition = i.getStringExtra("notePosition");
        isChecked = i.getBooleanExtra("noteChecked", false);
        isCheckedDesc = i.getBooleanExtra("noteCheckedDesc", false);

        pref.edit().putBoolean("noteChecked", isChecked).apply();
        pref.edit().putBoolean("noteCheckedDesc", isCheckedDesc).apply();

        if (notePosition == null)
        {
            startAlert.setVisibility(View.VISIBLE);
            returnAlert.setVisibility(View.VISIBLE);
        }
        else if(MainActivity.arrayList.get(Integer.valueOf(notePosition)).getDate().equals(""))
        {
            startAlert.setVisibility(View.VISIBLE);

            if(MainActivity.arrayList.get(Integer.valueOf(notePosition)).getReturndate().equals(""))
            {
            returnAlert.setVisibility(View.VISIBLE);
            }
        }
        else if(MainActivity.arrayList.get(Integer.valueOf(notePosition)).getReturndate().equals(""))
        {
            returnAlert.setVisibility(View.VISIBLE);

            if(MainActivity.arrayList.get(Integer.valueOf(notePosition)).getDate().equals(""))
            {
                startAlert.setVisibility(View.VISIBLE);
            }
        }

        if(noteId >= 0) {
            editText.setText(db.getnoteText(noteId));
        }

        editText.addTextChangedListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
        return true;
    }

    public void showpopup(View v)
    {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(EditYourNote.this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.mypopup, popup.getMenu());
        popup.show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_your_note, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        EditText editText = (EditText) findViewById(R.id.editText);

        if(item.getItemId() == android.R.id.home)
        {


        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.cal_action) {
            new DatePickerDialog(this, listener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
            return true;
        }
        if (id == R.id.cal_return) {
            showpopup((View) findViewById(R.id.cal_return));
            return true;
        }
        if (id == R.id.saveButton)
        {
            if(noteId >= 0) {

                MainActivity.arrayList.get(Integer.valueOf(notePosition))
                        .setNoteText(editText.getText().toString().trim());
                db.updateNotes(MainActivity.arrayList.get(Integer.valueOf(notePosition)), noteId);

                Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
            else
            {
                MyNotes note = new MyNotes();
                note.setNoteText(editText.getText().toString().trim());
                db.addNotes(note);
                db.close();

                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            String formattedDate = sdf.format(new Date(year - 1900,month,day));
            if(noteId >= 0)
            {
                db.updateDate(formattedDate, MainActivity.arrayList
                        .get(Integer.valueOf(notePosition)).getNoteID());

                Toast.makeText(getApplicationContext(), "Start date has been set!", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
            else
            {
                MyNotes note = new MyNotes();
                note.setNoteText(editText.getText().toString().trim());
                db.addDate(note, formattedDate);

                Toast.makeText(getApplicationContext(), formattedDate, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }

        }
    };
    DatePickerDialog.OnDateSetListener listener2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {


            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            String formattedDate = sdf.format(new Date(year - 1900,month,day));
            if(noteId >= 0)
            {
                db.updatereturnDate(formattedDate, MainActivity.arrayList
                        .get(Integer.valueOf(notePosition)).getNoteID());

                Toast.makeText(getApplicationContext(), "Return date has been set!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

            }
            else
            {
                MyNotes note = new MyNotes();
                note.setNoteText(editText.getText().toString().trim());
                db.addreturnDate(note, formattedDate);

                Toast.makeText(getApplicationContext(), formattedDate, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }

        }
    };

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.return_manually)
        {
            new DatePickerDialog(this, listener2, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        }
        else if(menuItem.getItemId() == R.id.return_setdays)
        {
            if(noteId < 0)
            {
                Toast.makeText(this, "Please set a start date first!", Toast.LENGTH_SHORT).show();
            }
            else if(!MainActivity.arrayList.get(Integer.valueOf(notePosition)).getDate().equals("")) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setTitle("Enter the amount of days");
                alert.setMessage("");

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        int days = Integer.valueOf(String.valueOf(input.getText()));
                        String date = MainActivity.arrayList.get(Integer.valueOf(notePosition)).getDate();

                        Calendar c = Calendar.getInstance();
                        try {
                            c.setTime(sdf.parse(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        c.add(Calendar.DATE, days);
                        Date resultDate = new Date(c.getTimeInMillis());

                        String newdate = sdf.format(resultDate);
                        db.updatereturnDate(newdate, MainActivity.arrayList.get(Integer.valueOf(notePosition)).getNoteID());

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();
            }
            else if(MainActivity.arrayList.get(Integer.valueOf(notePosition)).getDate().equals(""))
            {
                Toast.makeText(this, "Please set a start date first!", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
