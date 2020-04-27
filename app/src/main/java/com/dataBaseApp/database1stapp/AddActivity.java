package com.dataBaseApp.database1stapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {
    private EditText producent, model, androidVersion, www;
    private PomocnikBD mPomocnikBD;
    private SQLiteDatabase mBD;
    private Provider provider;
    private Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        initVariables();
        actionHelper();
        setClickListeners();
        setChangeStatesForTextPools();
    }

    //inicjalizacja zmiennych oraz obiektów
    public void initVariables(){
        producent = (EditText)findViewById(R.id.row1EditText);
        model = (EditText)findViewById(R.id.row2EditText);
        androidVersion = (EditText)findViewById(R.id.row3EditText);
        www = (EditText)findViewById(R.id.row4EditText);

        mPomocnikBD = new PomocnikBD(this);
        mBD=mPomocnikBD.getWritableDatabase();
        provider = new Provider();

        getSupportActionBar().setTitle("Szczegolowe dane");
    }

    //procedura dziląca wykonywaną akcje miedzy "add/update"
    private void actionHelper(){
        www.setText("http://");
        Bundle bundle = getIntent().getExtras();
        id=bundle.getLong("id");
        if(id != 0){
            producent.setText(getValueFromDBColumn(PomocnikBD.COLUMN1,id));
            model.setText(getValueFromDBColumn(PomocnikBD.COLUMN2,id));
            androidVersion.setText(getValueFromDBColumn(PomocnikBD.COLUMN3,id));
            www.setText(getValueFromDBColumn(PomocnikBD.COLUMN4,id));
        }
    }

    //procedura zapisu stanu, np. przed obrotem telefonu
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("producent",producent.getText().toString());
        outState.putString("model",model.getText().toString());
        outState.putString("androidVersion",androidVersion.getText().toString());
        outState.putString("www",www.getText().toString());
    }

    //procedura wczytująca stan np. po obrocie telefonu
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        producent.setText(savedInstanceState.getString("producent"));
        model.setText(savedInstanceState.getString("model"));
        androidVersion.setText(savedInstanceState.getString("androidVersion"));
        www.setText(savedInstanceState.getString("www"));
    }

    //pobieranie wartości z kolumny bazy danych do pól formularza
    private String getValueFromDBColumn(String column, long id){
        Cursor cursor = mBD.query(true,
                PomocnikBD.TABLE_NAME,new String[]{PomocnikBD.ID,PomocnikBD.COLUMN1,PomocnikBD.COLUMN2,PomocnikBD.COLUMN3,PomocnikBD.COLUMN4},
                PomocnikBD.ID+" = "+Long.toString(id),
                null,null,null,null,null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(index);
    }

    //procedura ustawiająca clickListenery dla elementów typu "Button"
    private void setClickListeners(){
        findViewById(R.id.saveButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(validation()) {
                            addPhoneData();
                        }
                        else
                        {
                            showToast("Wypelnij pierwsze 3 pola!");
                        }
                    }
                }
        );

        findViewById(R.id.cancelButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showToast("Anulowano operacje");
                        finish();
                    }
                }
        );

        findViewById(R.id.WWW).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!www.getText().toString().isEmpty() && www.getText().toString().startsWith("http://"))
                        {
                            Intent webSite = new Intent("android.intent.action.VIEW", Uri.parse(www.getText().toString()));
                            startActivity(webSite);
                        }
                        else
                        {
                            showToast("Bledny adres strony!");
                        }
                    }
                }
        );
    }

    //walidacja danych podczas zmiany akcji zmiany tekstu
    private void setChangeStatesForTextPools(){
        EditText[] editTextPools = {findViewById(R.id.row1EditText),findViewById(R.id.row2EditText),
                findViewById(R.id.row3EditText)};
        for(final EditText e:editTextPools){
            e.addTextChangedListener(
                    new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if(e.getText().toString().isEmpty()) {
                                showToast("Pole nie moze byc puste!");
                                e.setError("Podaj dane!");
                        }
                        }
                    }
            );
        }
    }

    //metoda walidująca dane formularza
    private boolean validation(){
        if(!producent.getText().toString().isEmpty() && !model.getText().toString().isEmpty() && !androidVersion.getText().toString().isEmpty())
        {
            if(!www.getText().toString().isEmpty()){
                if(!www.getText().toString().startsWith("http://")){
                    www.setError("Zacznij od 'http://'");
                    return false;
                }
                else return true;
            }
            else return true;
        }
        return false;
    }

    //procedura wstawiająca/aktualizująca rekordy bazy danych
    private void addPhoneData(){

        ContentValues values = new ContentValues();
        values.put(PomocnikBD.COLUMN1,producent.getText().toString());
        values.put(PomocnikBD.COLUMN2,model.getText().toString());
        values.put(PomocnikBD.COLUMN3,androidVersion.getText().toString());
        values.put(PomocnikBD.COLUMN4,www.getText().toString());
        if(id==0) {
            getContentResolver().insert(provider.URI_ZAWARTOSCI, values);
        }
        else{
            getContentResolver().update(provider.URI_ZAWARTOSCI,values,PomocnikBD.ID +" = "+ Long.toString(id),null);
        }
        showToast("Dodano nowe dane!");
        finish();
    }

    //procedura wspomagająca wyświetlania wiadomości typu "Toast"
    private void showToast(String massage){
        Toast toast = Toast.makeText(this,massage,Toast.LENGTH_SHORT);
        toast.show();
    }
}
