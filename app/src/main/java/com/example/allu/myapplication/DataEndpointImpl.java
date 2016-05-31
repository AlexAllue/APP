package com.example.allu.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.allu.myapplication.backend.taskUser.TaskUser;
import com.example.allu.myapplication.backend.taskUser.model.TaskBean;

import com.example.allu.myapplication.backend.taskUser.model.TaskData;
import com.google.android.gms.gcm.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataEndpointImpl {
    final TaskUser taskApiService;

    public DataEndpointImpl() {

        TaskUser.Builder builder = new TaskUser.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
        taskApiService = builder.build();

    }


    public synchronized void pushToRemote(Context context) {
        try {

            BaseSQLiteHelper padbh =
                    new BaseSQLiteHelper(context, "DBBase", null, 1);
            SQLiteDatabase db = padbh.getWritableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM Weather", null);
            taskApiService.clearTasksData().execute();
            long id = 1;
            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {
                        TaskData task = new TaskData();
                        task.setTown(c.getString(c.getColumnIndex("town")));
                        task.setId(id);
                        task.setDegrees(c.getString(c.getColumnIndex("degrees")));
                        task.setHumidity(c.getString(c.getColumnIndex("humidity")));
                        task.setLatitude(c.getString(c.getColumnIndex("latitude")));
                        task.setLongitude(c.getString(c.getColumnIndex("longitude")));
                        task.setOpcional(c.getString(c.getColumnIndex("optional")));
                        taskApiService.storeTaskData(task).execute();
                        id++;
                    } while (c.moveToNext());
                }
            }


        } catch (IOException e) {
            Log.e(UsersEndpointImpl.class.getSimpleName(), "Error when storing tasks", e);
        }
    }


    public synchronized void pullFromRemote(Context context) {

        try {
            // Remote Call
            List<TaskData > remoteTasks = taskApiService.getTasksData().execute().getItems();

            if (remoteTasks != null) {
                ArrayList<TaskData> taskList = new ArrayList<TaskData>();
                for (TaskData taskBean : remoteTasks) {
                    TaskData task = new TaskData();
                    task.setTown(taskBean.getTown());
                    task.setId(taskBean.getId());
                    task.setDegrees(taskBean.getDegrees());
                    task.setHumidity(taskBean.getHumidity());
                    task.setLatitude(taskBean.getLatitude());
                    task.setLongitude(taskBean.getLongitude());
                    task.setOpcional(taskBean.getOpcional());
                    taskList.add(task);
                }
                BaseSQLiteHelper padbh =
                        new BaseSQLiteHelper(context, "DBBase", null, 1);
                SQLiteDatabase db = padbh.getWritableDatabase();
                db.execSQL("DELETE FROM Weather");
                for(TaskData user: taskList){
                    ContentValues nuevoRegistro = new ContentValues();
                    nuevoRegistro.put("town", user.getTown());
                    nuevoRegistro.put("degrees", user.getDegrees());
                    nuevoRegistro.put("humidity", user.getHumidity());
                    nuevoRegistro.put("latitude", user.getLatitude());
                    nuevoRegistro.put("longitude", user.getLongitude());
                    nuevoRegistro.put("optional", user.getOpcional());
                    db.insert("Weather", null, nuevoRegistro);
                }

            }
        } catch (IOException e) {
            Log.e(UsersEndpointImpl.class.getSimpleName(), "Error when loading tasks", e);
        }
    }

}