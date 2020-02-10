package com.gmail.freshideassoftware.skyislimit;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


public class ExperienceDatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "Experience";
    private static final String DATABASE_NAME = "ExperienceDatabase.db";

    public ExperienceDatabaseOpenHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME +" ("+
                "ID INTEGER Primary key autoincrement, " +
                "Experience	 INTEGER NOT NULL" +
                ");" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
        sqLiteDatabase.execSQL("DROP TABLE if exists " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insert(int exp) {
        deletePrevious();

        SQLiteDatabase db;
        Cursor cursor;
        db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("Experience", exp);
        contentValues.put("ID", 0);

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public int getExperience(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("Select Experience From " + TABLE_NAME + " WHERE ID==0", new String[]{});
        int ret = 0;
        while(cur.moveToNext()){
            ret = cur.getInt(0);
        }
        db.close();
        return ret;
    }

    public void deletePrevious(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_NAME, "ID = ?", new String[]{0+""});
        db.close();
    }
}
