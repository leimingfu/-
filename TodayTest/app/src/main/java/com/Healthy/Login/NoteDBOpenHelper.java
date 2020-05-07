package com.Healthy.Login;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDBOpenHelper extends SQLiteOpenHelper {

    public NoteDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Noteinfo (_id integer primary " + " key autoincrement,user text,pwd text,pwd_remember text,date text,step text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("数据库升级了");

    }
}
