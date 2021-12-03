package iao.project.uwatch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import iao.project.uwatch.bean.User;

public class DataBase extends SQLiteOpenHelper {

    private static final String DB_NAME = "UserInformations.db";
    private static final int DB_VERSION = 1;
    private static final String CREATE_DB = "create table user("
            +"username TEXT PRIMARY KEY,"
            +"name TEXT NOT NULL,"
            +"prenom TEXT,"
            +"mail TEXT NOT NULL,"
            +"password TEXT,"
            +"etat TEXT,"
            +"favorites INTEGER,"
            +"likes INTEGER,"
            +"hates INTEGER);";

    public DataBase(Context context) {
        super(context , DB_NAME , null , DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sql) {

        //BD créer juste si le constructeur ne trouve pas la table
        sql.execSQL(CREATE_DB);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sql , int i , int i1) {
        sql.execSQL("drop Table if exists user");
    }
}
