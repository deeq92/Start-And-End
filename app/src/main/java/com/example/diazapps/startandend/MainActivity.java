package com.example.diazapps.startandend;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends Activity {

    sqDB db;
    static ArrayList<MyNotes> arrayList;
    static ArrayList<MyNotes> arrayListDelete;
    static ArrayAdapter arrayAdapter;

    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
    MenuItem sortbyDate;
    MenuItem sortbyDateDesc;
    Boolean isChecked;
    Boolean isCheckedDesc;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bar_color)));

        setTaskDescription(new ActivityManager.TaskDescription("Notes",
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_new), getResources().getColor(R.color.bar_color)));

        final ListView listView = (ListView) findViewById(R.id.listView);
        db = new sqDB(this);

        pref = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        isChecked = pref.getBoolean("noteChecked", false);
        isCheckedDesc = pref.getBoolean("noteCheckedDesc", false);

        arrayList = db.getNotes();
        arrayListDelete = new ArrayList<MyNotes>();

        for(int i = 0; i < arrayList.size(); i++)
        {
            if(!arrayList.get(i).getReturndate().equals("")) {
                arrayList.get(i).setDATE_date();
            }
        }

        arrayAdapter = new CustomAdapter(this, arrayList);

        listView.setAdapter(arrayAdapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int deleteCount = 0;
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {

                if(arrayList.get(position).getisSelected() == true) {
                    deleteCount--;
                    arrayList.get(position).setisSelected(false);
                    arrayListDelete.remove(arrayList.get(position));
                }
                else if(arrayList.get(position).getisSelected() == false)
                {
                    deleteCount++;
                    arrayList.get(position).setisSelected(true);
                    arrayListDelete.add(arrayList.get(position));
                }
                actionMode.setTitle("Delete " + String.valueOf(deleteCount));
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.delete_multiple, menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.deleteMenuButton:
                        for(MyNotes i : arrayListDelete)
                        {
                            db.deleteNote(i.getNoteID());
                        }
                        arrayAdapter.notifyDataSetChanged();
                        deleteCount = 0;
                        actionMode.finish();

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                for(MyNotes i : arrayList)
                {
                    i.setisSelected(false);
                }
                arrayListDelete.clear();
                deleteCount = 0;

            }
        });

        if(isChecked)
        {
            Collections.sort(arrayList, new Comparator<MyNotes>() {
                @Override
                public int compare(MyNotes o1, MyNotes o2) {
                    if (o1.getDATE_dateValue() == null) {
                        return (o2.getDATE_dateValue() == null) ? 0 : -1;
                    }
                    if (o2.getDATE_dateValue() == null) {
                        return 1;
                    }
                    return o2.getDATE_dateValue().compareTo(o1.getDATE_dateValue());
                }
            });

            Collections.reverse(arrayList);
            arrayAdapter.notifyDataSetChanged();
        }
        if(isCheckedDesc)
        {
            Collections.sort(arrayList, new Comparator<MyNotes>() {
                @Override
                public int compare(MyNotes o1, MyNotes o2) {
                    if (o1.getDATE_dateValue() == null) {
                        return (o2.getDATE_dateValue() == null) ? 0 : 1;
                    }
                    if (o2.getDATE_dateValue() == null) {
                        return -1;
                    }
                    return o2.getDATE_dateValue().compareTo(o1.getDATE_dateValue());
                }
            });

            arrayAdapter.notifyDataSetChanged();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getApplicationContext(),  EditYourNote.class);
                i.putExtra("noteId", (arrayList.get(position).getNoteID()));
                i.putExtra("notePosition", (String.valueOf(position)));
                i.putExtra("noteChecked", isChecked);
                i.putExtra("noteCheckedDesc", isCheckedDesc);
                startActivity(i);

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        sortbyDate = menu.findItem(R.id.sortbyDate);
        sortbyDateDesc = menu.findItem(R.id.sortbyDateDesc);

        sortbyDate.setChecked(isChecked);
        sortbyDateDesc.setChecked(isCheckedDesc);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add) {

            Intent i = new Intent(getApplicationContext(),  EditYourNote.class);
            i.putExtra("noteChecked", isChecked);
            i.putExtra("noteCheckedDesc", isCheckedDesc);
            startActivity(i);

            return true;

        }
        if (id == R.id.sortbyDate) {

            if(sortbyDate.isChecked())
            {
                sortbyDate.setChecked(false);
                isChecked = false;
            }
            else if(!sortbyDate.isChecked())
            {
                if(!sortbyDateDesc.isChecked()) {
                    sortbyDate.setChecked(true);
                    isChecked = true;

                    Collections.sort(arrayList, new Comparator<MyNotes>() {
                        @Override
                    public int compare(MyNotes o1, MyNotes o2) {
                        if (o1.getDATE_dateValue() == null) {
                            return (o2.getDATE_dateValue() == null) ? 0 : -1;
                        }
                        if (o2.getDATE_dateValue() == null) {
                            return 1;
                        }
                        return o2.getDATE_dateValue().compareTo(o1.getDATE_dateValue());
                    }
                    });

                    Collections.reverse(arrayList);
                    arrayAdapter.notifyDataSetChanged();
                }
                else if (sortbyDateDesc.isChecked())
                {
                    sortbyDate.setChecked(true);
                    isChecked = true;

                    sortbyDateDesc.setChecked(false);
                    isCheckedDesc = false;

                    Collections.sort(arrayList, new Comparator<MyNotes>() {
                        @Override
                        public int compare(MyNotes o1, MyNotes o2) {
                            if (o1.getDATE_dateValue() == null) {
                                return (o2.getDATE_dateValue() == null) ? 0 : -1;
                            }
                            if (o2.getDATE_dateValue() == null) {
                                return 1;
                            }
                            return o2.getDATE_dateValue().compareTo(o1.getDATE_dateValue());
                        }
                    });

                    Collections.reverse(arrayList);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }

        if (id == R.id.sortbyDateDesc)
        {
            if(sortbyDateDesc.isChecked())
            {
                sortbyDateDesc.setChecked(false);
                isCheckedDesc = false;
            }
            else if(!sortbyDateDesc.isChecked())
            {
                if(!sortbyDate.isChecked()) {
                    sortbyDateDesc.setChecked(true);
                    isCheckedDesc = true;

                    Collections.sort(arrayList, new Comparator<MyNotes>() {
                        @Override
                        public int compare(MyNotes o1, MyNotes o2) {
                            if (o1.getDATE_dateValue() == null) {
                                return (o2.getDATE_dateValue() == null) ? 0 : 1;
                            }
                            if (o2.getDATE_dateValue() == null) {
                                return -1;
                            }
                            return o2.getDATE_dateValue().compareTo(o1.getDATE_dateValue());
                        }
                    });

                    arrayAdapter.notifyDataSetChanged();

                }
                else if(sortbyDate.isChecked())
                {
                    sortbyDateDesc.setChecked(true);
                    isCheckedDesc = true;

                    sortbyDate.setChecked(false);
                    isChecked = false;

                    Collections.sort(arrayList, new Comparator<MyNotes>() {
                        @Override
                        public int compare(MyNotes o1, MyNotes o2) {
                            if (o1.getDATE_dateValue() == null) {
                                return (o2.getDATE_dateValue() == null) ? 0 : 1;
                            }
                            if (o2.getDATE_dateValue() == null) {
                                return -1;
                            }
                            return o2.getDATE_dateValue().compareTo(o1.getDATE_dateValue());
                        }
                    });

                    arrayAdapter.notifyDataSetChanged();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
