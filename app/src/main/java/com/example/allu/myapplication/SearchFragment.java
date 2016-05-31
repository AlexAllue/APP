package com.example.allu.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Allu on 15/03/2016.
 */
public class SearchFragment extends Fragment {

    private EditText editTown;
    private Button btnSearch;
    private SQLiteDatabase db;
    private Cursor c;
    private String[] args;
    ArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.search_fragment, container, false);

        editTown = (EditText)ll.findViewById(R.id.town);
        btnSearch = (Button)ll.findViewById(R.id.btnSearch);

        final ListView myListView = (ListView) ll.findViewById(R.id.searchList);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTown.getText().toString().matches("")){
                    showToast(getString(R.string.null_town));
                }else {
                    ArrayList<String> myStringArray = new ArrayList<String>();
                    BaseSQLiteHelper padbh =
                            new BaseSQLiteHelper(getActivity().getApplication(), "DBBase", null, 1);
                    db = padbh.getReadableDatabase();
                    args = new String[]{editTown.getText().toString()};
                    c = db.rawQuery("SELECT town,degrees,humidity,longitude,latitude,optional FROM Weather WHERE town=?", args);
                    if (c.getCount() > 0) {
                        if (c.moveToFirst()) {
                            do {
                                myStringArray.add(c.getString(c.getColumnIndex("degrees")) + c.getString(c.getColumnIndex("humidity")) + c.getString(c.getColumnIndex("optional")));
                            } while (c.moveToNext());
                        }
                    }
                    adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_row, myStringArray);
                    myListView.setAdapter(adapter);
                }
            }
        });

        return ll;
    }


    protected void showToast(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(db!=null){
            db.close();
        }
    }

}
