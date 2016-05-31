package com.example.allu.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.allu.myapplication.backend.messaging.Messaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.util.ArrayList;


public class LoginActivity extends Activity {
    private Button btnRegister,btnLogin;
    private EditText editUser,editPassword;
    private SQLiteDatabase db;
    private Cursor c;
    private String[] args;
    public UsersEndpointImpl userImpl = new UsersEndpointImpl();
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       if(!isOnline()){
           showToast(getApplication().getString(R.string.no_net));
       }
       new userDownloadTask().execute();
        editUser = (EditText)findViewById(R.id.user);
        editPassword = (EditText)findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editUser.getText().toString().matches("")){
                    showToast(getApplication().getString(R.string.null_user));
                }else if (editPassword.getText().toString().matches("")){
                    showToast(getApplication().getString(R.string.null_pass));
                }else {
                    BaseSQLiteHelper padbh =
                            new BaseSQLiteHelper(getApplication(), "DBBase", null, 1);
                    db = padbh.getReadableDatabase();
                    args = new String[]{editUser.getText().toString()};
                    c = db.rawQuery("SELECT user,password,email FROM Users WHERE user=?", args);
                    if(c.moveToFirst()){
                        if(editPassword.getText().toString().matches(c.getString(c.getColumnIndex("password")))){
                            Intent i = new Intent(getApplication(),WelcomeActivity.class);
                            i.putExtra("user", editUser.getText().toString());
                            i.putExtra("email",c.getString(c.getColumnIndex("email")));
                            startActivity(i);
                            finish();
                        }else{
                            showToast(getString(R.string.no_pass));
                        }
                    }else{
                        showToast(getString(R.string.no_user));
                    }
                }
            }
        });

        btnRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplication(),RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("user", editUser.getText().toString());
        outState.putString("pass", editPassword.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        editUser.setText(savedInstanceState.getString("user"));
        editPassword.setText(savedInstanceState.getString("pass"));
    }

    protected void showToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(db!=null){
            db.close();
        }
    }
    private class userDownloadTask extends AsyncTask<Void, Integer, Void> {

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

            userImpl.pullFromRemote(LoginActivity.this);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null) {
            if (netInfo.isConnectedOrConnecting()) {
                return true;
            }
        }

        return false;
    }

}
