package com.dataBaseApp.database1stapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PomocnikBD extends SQLiteOpenHelper {
    public final static int BASE_VERSION = 1;
    public final static String ID = "_id";
    public final static String BASE_NAME = "appDataBase.db";
    public final static String TABLE_NAME = "telephonesData";
    public final static String COLUMN1 = "producent";
    public final static String COLUMN2 = "model";
    public final static String COLUMN3 = "androidVersion";
    public final static String COLUMN4 = "wwwAdress";

    public final static String BASE_CREATE = "CREATE TABLE " + TABLE_NAME +
                                            "(" + ID + " integer primary key autoincrement, " +
                                            COLUMN1 + " text not null," +
                                            COLUMN2 + " text not null," +
                                            COLUMN3 + " text not null," +
                                            COLUMN4 + " text);";
    private static final String DEL_DATABASE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public PomocnikBD(Context context)
    {
        super(context,BASE_NAME,null,BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DEL_DATABASE);
        onCreate(db);
    }


}
