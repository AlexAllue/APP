package com.example.allu.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.allu.myapplication.backend.taskUser.TaskUser;
import com.example.allu.myapplication.backend.taskUser.model.TaskBean;
import com.google.android.gms.gcm.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UsersEndpointImpl {
    final TaskUser taskApiService;


    public UsersEndpointImpl() {

        TaskUser.Builder builder = new TaskUser.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
        taskApiService = builder.build();

    }


    public synchronized void pushToRemote(Context context) {
        try {

            BaseSQLiteHelper padbh =
                    new BaseSQLiteHelper(context, "DBBase", null, 1);
            SQLiteDatabase db = padbh.getWritableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM Users", null);
            taskApiService.clearTasks().execute();
            long id = 1;
            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {
                        TaskBean task = new TaskBean();
                        task.setUser(c.getString(c.getColumnIndex("user")));
                        task.setId(id);
                        task.setPassword(c.getString(c.getColumnIndex("password")));
                        task.setEmail(c.getString(c.getColumnIndex("email")));
                        taskApiService.storeTask(task).execute();
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
            List<TaskBean > remoteTasks = taskApiService.getTasks().execute().getItems();

            if (remoteTasks != null) {
                ArrayList<TaskBean> taskList = new ArrayList<TaskBean>();
                for (TaskBean taskBean : remoteTasks) {
                    TaskBean task = new TaskBean();
                    task.setUser(taskBean.getUser());
                    task.setId(taskBean.getId());
                    task.setPassword(taskBean.getPassword());
                    task.setEmail(taskBean.getEmail());
                    taskList.add(task);
                }
                BaseSQLiteHelper padbh =
                        new BaseSQLiteHelper(context, "DBBase", null, 1);
                SQLiteDatabase db = padbh.getWritableDatabase();
                db.execSQL("DELETE FROM Users");
                for(TaskBean user: taskList){
                    ContentValues nuevoRegistro = new ContentValues();
                    nuevoRegistro.put("user", user.getUser());
                    nuevoRegistro.put("password", user.getPassword());
                    nuevoRegistro.put("email", user.getEmail());
                    db.insert("Users", null, nuevoRegistro);
                }

            }
        } catch (IOException e) {
            Log.e(UsersEndpointImpl.class.getSimpleName(), "Error when loading tasks", e);
        }
    }

}