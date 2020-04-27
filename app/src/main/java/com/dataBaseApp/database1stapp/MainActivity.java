package com.dataBaseApp.database1stapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AbsListView.MultiChoiceModeListener {

    private PomocnikBD mPomocnikBD;
    private SQLiteDatabase mBD;
    private SimpleCursorAdapter mAdapterKursor;
    private ListView mLista;
    private int counter;
    private ActionMenuItemView counterView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVariables();
        setContextMultiChoiceListener();
        setListOnClickListener();
        uruchomLoader();

    }
    //inicjacja obiektow i zmiennych uzytych w ponizszych procedurach
    private void initVariables(){
        mPomocnikBD = new PomocnikBD(this);
        mBD = mPomocnikBD.getWritableDatabase();
        mLista = (ListView) findViewById(android.R.id.list);
    }

    //ustawienie ClickListenera dla listy telefonow
    private void setListOnClickListener(){
        mLista.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this,AddActivity.class);
                        intent.putExtra("id",id);
                        startActivity(intent);
                    }
                }
        );
    }

    //procedura ustawiająca menu wielokrotnego wyboru po "długim kliknięciu"
    private void setContextMultiChoiceListener(){
        mLista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mLista.setMultiChoiceModeListener(
                new AbsListView.MultiChoiceModeListener() {
                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                        if(checked){counter++;}
                        if(!checked){counter--;}
                        counterView = findViewById(R.id.counterView);
                        counterView.setText(Integer.toString(counter));
                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        MenuInflater menuInflater = mode.getMenuInflater();
                        menuInflater.inflate(R.menu.context_menu,menu);
                        counter = 0;
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.action_delete:
                                deleteChecked();
                                counter = 0;
                                showToast("Usunieto elementy!");
                                return true;
                        }
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }
                }
        );
    }

    //procedura kasujaca pozycje zaznaczone dlugim kliknięciem
    private void deleteChecked(){
        long checkedItems[] = mLista.getCheckedItemIds();
        for(int i=0;i<checkedItems.length;i++){
            getContentResolver().delete(ContentUris.withAppendedId(Provider.URI_ZAWARTOSCI,checkedItems[i]),PomocnikBD.ID+" = "+Long.toString(checkedItems[i]),null);
        }
    }

    //prodecura zapisu stanu przed np.obroceniem telefonu
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("counter",counter);
    }

    //procedura wczytania zapisanego stanu, po np.obrocie telefonu
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        counter=savedInstanceState.getInt("counter");
        counterView=findViewById(R.id.counterView);
        counterView.setText(Integer.toString(counter));
    }

    //procedura tworzaca pasek funkcjonalny menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //procedura obługująca pasek funkcjonalny menu, obsluga wykonanych akcji na pasku
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.action_add) {
            Intent addIntend = new Intent (MainActivity.this,AddActivity.class);
            addIntend.putExtra("id",0);
            startActivity(addIntend);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //procedura uruchamiajaca loader, wczytywanie danych na liste telefonow
    private void uruchomLoader(){
       LoaderManager.getInstance(this).initLoader(0,null, this);

        String[] mapujZ = new String[]{PomocnikBD.COLUMN1,PomocnikBD.COLUMN2};
        int[] mapujDo = new int[]{R.id.label1,R.id.label2};

       mAdapterKursor = new SimpleCursorAdapter(getApplicationContext(),R.layout.list_element,null,mapujZ,mapujDo,0);
       mLista.setAdapter(mAdapterKursor);
    }
    //procedura wywoływana podczas tworzenia loadera, wczytująca listę
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projekcja = {PomocnikBD.ID,PomocnikBD.COLUMN1,PomocnikBD.COLUMN2};
        CursorLoader loaderKursora = new CursorLoader(this,Provider.URI_ZAWARTOSCI,projekcja,null,null,null);
        return loaderKursora;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(data.isAfterLast())findViewById(R.id.empty).setVisibility(View.VISIBLE);
        else findViewById(R.id.empty).setVisibility(View.GONE);
        mAdapterKursor.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapterKursor.swapCursor(null);
    }
    //procedura wywoływana po zakończeniu działania programu, zamykająca bazę danych
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBD.close();
    }
    //metoda wspierdająca wyświetlanie wiadomości typu Toast
    void showToast(String massage){
        Toast toast = Toast.makeText(this,massage,Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
}
