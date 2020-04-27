package com.dataBaseApp.database1stapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class Provider extends ContentProvider {

    private PomocnikBD mPomocnikBD;
    private SQLiteDatabase mBD;

    private static final String IDENTYFIKATOR = "com.dataBaseApp.appDataBase.Provider";
    public static final Uri URI_ZAWARTOSCI = Uri.parse("content://"+IDENTYFIKATOR+"/"+PomocnikBD.TABLE_NAME);
    private static final int CALA_TABELA = 1;
    private static final int WYBRANY_WIERSZ = 2;

    private static final UriMatcher sDopasowanyUri = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sDopasowanyUri.addURI(IDENTYFIKATOR, PomocnikBD.TABLE_NAME, CALA_TABELA);
        sDopasowanyUri.addURI(IDENTYFIKATOR,PomocnikBD.TABLE_NAME+"/#",WYBRANY_WIERSZ);
    }


    @Override
    public boolean onCreate() {
        mPomocnikBD = new PomocnikBD(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int typUri = sDopasowanyUri.match(uri);
        mPomocnikBD = new PomocnikBD(getContext());
        mBD = mPomocnikBD.getWritableDatabase();

        Cursor kursor=null;
        switch (typUri){
            case CALA_TABELA:
                kursor = mBD.query(true,PomocnikBD.TABLE_NAME,projection,selection,selectionArgs, null,null,sortOrder,null);
                break;
            case WYBRANY_WIERSZ:
                kursor=mBD.query(true, PomocnikBD.TABLE_NAME,projection,selection,selectionArgs, null,null,sortOrder,null);
                break;
            default:throw new IllegalArgumentException("Nieznane Uri: "+uri);
        }
        kursor.setNotificationUri(getContext().getContentResolver(),uri);
        return kursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int typUri = sDopasowanyUri.match(uri);
        mPomocnikBD = new PomocnikBD(getContext());
        mBD = mPomocnikBD.getWritableDatabase();

        long idDodanego = 0;
        switch (typUri){
            case CALA_TABELA:
                idDodanego=mBD.insert(PomocnikBD.TABLE_NAME,null,values);
                break;
            default:
                throw new IllegalArgumentException("Nieznane Uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return Uri.parse(PomocnikBD.TABLE_NAME + "/" + idDodanego);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int typUri = sDopasowanyUri.match(uri);
        mPomocnikBD = new PomocnikBD(getContext());
        mBD=mPomocnikBD.getWritableDatabase();

        int liczbaUsunietych=0;
        switch (typUri){
            case CALA_TABELA:
                liczbaUsunietych=mBD.delete(PomocnikBD.TABLE_NAME,selection,selectionArgs);
                break;
            case WYBRANY_WIERSZ:
                liczbaUsunietych=mBD.delete(PomocnikBD.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Nieznane Uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return liczbaUsunietych;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int typUri = sDopasowanyUri.match(uri);
        mPomocnikBD = new PomocnikBD(getContext());
        mBD=mPomocnikBD.getWritableDatabase();
        int liczbaZaktualizowanych=0;
        switch (typUri){
            case CALA_TABELA:
                liczbaZaktualizowanych=mBD.update(PomocnikBD.TABLE_NAME,values,selection,selectionArgs);
                break;
            case WYBRANY_WIERSZ:
                liczbaZaktualizowanych=mBD.update(PomocnikBD.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:throw new IllegalArgumentException("Nieznane Uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return liczbaZaktualizowanych;
    }
}
