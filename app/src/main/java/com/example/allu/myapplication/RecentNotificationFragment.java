package com.example.allu.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Allu on 19/03/2016.
 */
public class RecentNotificationFragment extends Fragment {

    ArrayAdapter adapter;
    public DataEndpointImpl dataImpl = new DataEndpointImpl();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.recent_notification_fragment, container, false);

        new dataDownloadTask().execute();

        ListView myListView = (ListView) ll.findViewById(R.id.recentList);
        ArrayList<String> myStringArray = new ArrayList<String>();
        BaseSQLiteHelper padbh =
                new BaseSQLiteHelper(getContext(), "DBBase", null, 1);
        SQLiteDatabase db = padbh.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Weather ORDER BY _id DESC LIMIT 3", null);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    myStringArray.add(c.getString(c.getColumnIndex("town")) + c.getString(c.getColumnIndex("degrees")) + c.getString(c.getColumnIndex("humidity")));
                } while (c.moveToNext());
            }
        }
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_row, myStringArray);
        myListView.setAdapter(adapter);

        return ll;
    }

    private class dataDownloadTask extends AsyncTask<Void, Integer, Void> {

        /*
        Se hace visible el botón "Cancelar" y se desactiva
        el botón "Ordenar"
         */
        @Override
        protected void onPreExecute() {

        }

        /*
        Ejecución del ordenamiento y transmision de progreso
         */
        @Override
        protected Void doInBackground(Void... params) {

            dataImpl.pullFromRemote(getActivity().getApplicationContext());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //rellenarLista();

        }
    }
}
