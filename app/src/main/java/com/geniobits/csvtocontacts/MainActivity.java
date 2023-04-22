package com.geniobits.csvtocontacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> header_list;
    private Spinner name_spinner;
    private Spinner email_spinner;
    private Spinner number_spinner;
    private String name_colums;
    private String email_colums;
    private String number_colums;
    private CSV csv=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Contacts.askPermission(getApplicationContext());
        Button choose_csv = findViewById(R.id.choose_csv);
        TextView path = findViewById(R.id.file_path);
        name_spinner = (Spinner) findViewById(R.id.name_spinner);
        email_spinner = (Spinner) findViewById(R.id.email_spinner);
        number_spinner = (Spinner) findViewById(R.id.number_spinner);
        choose_csv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_csv.setEnabled(false);
                choose_csv.setVisibility(View.GONE);
                ChooserDialog dialog2 = new ChooserDialog(MainActivity.this)
                        .enableOptions(true)
                        .withFilter(false, true, "csv")
                        .enableMultiple(false)
                        .withStartFile(Environment.getExternalStorageDirectory().getAbsolutePath())
                        .withChosenListener(new ChooserDialog.Result() {

                            @Override
                            public void onChoosePath(String pat, File pathFile) {
                                Log.e("filesclicked",pat);
                                if(pat.contains(".csv")) {
                                    csv = new CSV(pat);
                                    path.setText(pat);
                                    header_list = csv.getCSVHeaders();
                                    if (header_list != null)
                                        load_spinners();
                                }

                            }
                        })
                        .withOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                Log.d("CANCEL", "CANCEL");
                                // MUST have
                                dialog.dismiss();
                            }
                        });

                dialog2.build();
                dialog2.show();

            }
        });
        EditText contact_init = findViewById(R.id.contact_init);
        Button import_button = findViewById(R.id.import_button);
        import_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(csv!=null){
                    import_button.setEnabled(false);
                    import_button.setVisibility(View.GONE);
                    ArrayList<Contacts> contactsArrayList = csv.getContactList(name_colums, email_colums, number_colums, contact_init.getText().toString());
                    Contacts.ImportAllContacts(contactsArrayList, getApplicationContext());
                    Toast.makeText(MainActivity.this, "Imported", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "No CSV selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button delete_button = findViewById(R.id.delete_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(csv!=null){
                    import_button.setEnabled(false);
                    import_button.setVisibility(View.GONE);
                    ArrayList<Contacts> contactsArrayList = csv.getContactList(name_colums, email_colums, number_colums, contact_init.getText().toString());
                    Contacts.DeleteAllContacts(contactsArrayList, getApplicationContext());
                    Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "No CSV selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void load_spinners() {
        String[] data=header_list.toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,data);
        name_spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        name_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                name_colums = name_spinner.getSelectedItem().toString();
                name_spinner.setSelection(name_spinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        email_spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        email_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                email_colums = email_spinner.getSelectedItem().toString();
                email_spinner.setSelection(email_spinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        number_spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        number_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                number_colums = number_spinner.getSelectedItem().toString();

                number_spinner.setSelection(number_spinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
}