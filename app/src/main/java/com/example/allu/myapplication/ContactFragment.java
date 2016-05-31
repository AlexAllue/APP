package com.example.allu.myapplication;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Allu on 15/03/2016.
 */
public class ContactFragment extends Fragment {
    private Spinner spinner;
    private Button btnSendEmail;
    private EditText txtAsunto,txtDescription;
    private Boolean seleccionValida;
    private String selection;
    private int opcion;
    private TextView txtError;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.contact_fragment, container, false);

        spinner = (Spinner)ll.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new SpinnerInfo());

        btnSendEmail = (Button) ll.findViewById(R.id.btnSendEmail);
        txtAsunto = (EditText)ll.findViewById(R.id.issue);
        txtDescription = (EditText)ll.findViewById(R.id.description);
        txtError = (TextView)ll.findViewById(R.id.error);

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtAsunto.getText().toString().matches("")){
                    txtError.setText(getString(R.string.error_issue));
                }else if(!seleccionValida){
                    txtError.setText(getString(R.string.error_motive));
                }else if(txtDescription.getText().toString().matches("")){
                    txtError.setText(getString(R.string.error_description));
                } else{
                    String[] to = { "alluesan@hotmail.com"};
                    String[] cc = {};
                    enviar(to, cc, txtAsunto.getText().toString(),
                            "("+selection+") "+txtDescription.getText().toString());
                }
            }
        });

        return ll;
    }

    private void enviar(String[] to, String[] cc,
                        String asunto, String mensaje) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_CC, cc);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        emailIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Enviar correo con"));
    }

    private class SpinnerInfo implements AdapterView.OnItemSelectedListener {

        /** Shows a Toast for the selected item. Ignored the very first time,
         *  which is when the item is selected on initial display, rather than
         *  by user interaction.
         */
        @Override
        public void onItemSelected(AdapterView<?> spinner, View selectedView,
                                   int selectedIndex, long id) {
            opcion=selectedIndex;
            if(selectedIndex==0){seleccionValida=false;}
            else {seleccionValida=true;}
            selection = spinner.getItemAtPosition(selectedIndex).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> spinner) {
            // Won't be invoked for a Spinner unless you programmatically remove items from the view
        }
    }




}
