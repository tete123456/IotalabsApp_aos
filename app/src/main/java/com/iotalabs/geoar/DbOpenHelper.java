package com.iotalabs.geoar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper {

    private static final String DATABASE_NAME = "friend_list_3.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;



    private class DatabaseHelper extends SQLiteOpenHelper {

        // 생성자
        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // 최초 DB를 만들때 한번만 호출된다.
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DataBases.CreateDB._CREATE);
            db.execSQL(DataBases.CreateDB2._CREATE);
            db.execSQL(DataBases.CreateDB3._CREATE);
        }

        // 버전이 업데이트 되었을 경우 DB를 다시 만들어 준다.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB._TABLENAME);
            onCreate(db);
            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB2._TABLENAME2);
            onCreate(db);
            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB3._TABLENAME3);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context){
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        mDB.close();
    }
    //친구정보용 테이블
    public long insertColumn( String UUID, String name) { //친구리스트용 /////
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.UUID, UUID);
        values.put(DataBases.CreateDB.NAME, name);
        return mDB.insert(DataBases.CreateDB._TABLENAME, null, values);
    }
    /////// id 기준으로 카럼 검색후 업데이트
    public boolean updateColumn(int id, String name, int relation){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.NAME, name);
        values.put(DataBases.CreateDB.Relation, relation);
        return mDB.update(DataBases.CreateDB._TABLENAME, values, "_id=" + id, null) > 0;
    }
    public boolean deleteColumn(long id) {
        return mDB.delete(DataBases.CreateDB._TABLENAME, "_id=" +id, null) > 0;
    }
    public Cursor getAllColumns() {
        return mDB.query(DataBases.CreateDB._TABLENAME, null, null, null, null, null, null);
    }
    ///////
    //모든사용자 위치정보 테이블용
    public long insertColumn2( String UUID, String str_latitude, String str_longitude) { // 모든사용자 실시간 위치정보 ////
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB2.UUID, UUID);
        values.put(DataBases.CreateDB2.str_latitude, str_latitude);
        values.put(DataBases.CreateDB2.str_longitude, str_longitude);
        return mDB.insert(DataBases.CreateDB2._TABLENAME2, null, values);
    }
    public void deleteAll(){//lacation delete
        mDB.execSQL("DELETE FROM " + DataBases.CreateDB2._TABLENAME2);
    }

    public Cursor getAllColumns2() {
        return mDB.query(DataBases.CreateDB2._TABLENAME2, null, null, null, null, null, null);
    }
    //
    //내 친구 위치 테이블용
    public long insertColumn3( String name, String str_latitude, String str_longitude) { //친구리스트용 /////
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB3.name, name);
        values.put(DataBases.CreateDB3.str_latitude, str_latitude);
        values.put(DataBases.CreateDB3.str_longitude, str_longitude);
        return mDB.insert(DataBases.CreateDB3._TABLENAME3, null, values);
    }
    public void deleteAll3(){//lacation delete
        mDB.execSQL("DELETE FROM " + DataBases.CreateDB3._TABLENAME3);
    }
    public boolean existtable(){//
        Cursor cursor = mDB.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name ='friendNameLoc'" , null);
        cursor.moveToFirst();
        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }
    public Cursor getAllColumns3() {
        return mDB.query(DataBases.CreateDB3._TABLENAME3, null, null, null, null, null, null);
    }





}