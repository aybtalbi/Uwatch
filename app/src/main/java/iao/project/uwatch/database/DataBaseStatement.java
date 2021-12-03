package iao.project.uwatch.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import iao.project.uwatch.bean.MyList;
import iao.project.uwatch.bean.User;

public class DataBaseStatement {
    private User user;
    private DataBase data;
    private SQLiteDatabase sql;
    private final String UNFOUND = "unfounded";
    private final String CONNECTED = "connected";
    private final String DISCONNECTED = "disconnected";

    public DataBaseStatement(Context context){
        data = new DataBase(context);
    }

    public boolean insertUser(User user)
    {
        SQLiteDatabase sql = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username",user.getUserName());
        contentValues.put("name",user.getNom());
        contentValues.put("mail",user.getMail());
        if(user.getPrenom()!=null && user.getPassword() !=null && user.getEtat()!=null) {
            contentValues.put("prenom", user.getPrenom());
            contentValues.put("password", user.getPassword());
            contentValues.put("etat", user.getEtat());
        }
        long result = sql.insert("user" , null , contentValues);
        if(result == -1)
        {
            return false;
        }
        else {
            return true;
        }
    }
    public List<User> getAllUsers(Context context)
    {
        List<User> users = new ArrayList<User>();
        sql = data.getReadableDatabase();
        String[] colonnes = {"username","name","prenom","mail","password","etat","favorites","likes","hates"};
        Cursor cursor = sql.query("user",colonnes,null,null,null,null,null);

        while(cursor.moveToNext())
        {
            User user = new User();
            user.setUserName(cursor.getString(0));
            user.setNom(cursor.getString(1));
            user.setPrenom(cursor.getString(2));
            user.setMail(cursor.getString(3));
            user.setPassword(cursor.getString(4));
            user.setEtat(cursor.getString(5));
            MyList list = new MyList(cursor.getInt(6) , cursor.getInt(7) ,cursor.getInt(8));
            user.setList(list);
            users.add(user);
        }
        cursor.close();
        return users;
    }
    public User getUser(String etat)
    {
        User user;
        SQLiteDatabase sql = data.getReadableDatabase();
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE etat =?", new String[]{etat});
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            MyList list = new MyList(cursor.getInt(6) , cursor.getInt(7) ,cursor.getInt(8));
            user = new User(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
            user.setList(list);
        }
        else
        {
            user = new User();
        }
        return user;
    }
    public User getOutsiderUser(String userId)
    {
        User user;
        SQLiteDatabase sql = data.getReadableDatabase();
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE username =?", new String[]{userId});
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            MyList list = new MyList(cursor.getInt(6) , cursor.getInt(7) ,cursor.getInt(8));
            user = new User(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
            user.setList(list);
        }
        else
        {
            user = new User();
        }
        return user;
    }

    public boolean updateData(User user)
    {
        SQLiteDatabase sql = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",user.getNom());
        contentValues.put("prenom",user.getPrenom());
        contentValues.put("mail",user.getMail());
        contentValues.put("password",user.getPassword());
        contentValues.put("etat",user.getEtat());
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE username = ?" , new String[]{user.getUserName()});
        if(cursor.getCount()>0) {
            long result = sql.update("user", contentValues, "username=?", new String[]{user.getUserName()});
            if(result == -1)
            {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }
    public boolean updateEtat(String userName , String etat)
    {
        SQLiteDatabase sql = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("etat",etat);
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE username = ?" , new String[]{userName});
        if(cursor.getCount()>0) {
            long result = sql.update("user", contentValues, "username=?", new String[]{userName});
            if(result == -1)
            {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }
    public boolean updateName(String userName , String name )
    {
        SQLiteDatabase sql = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE username = ?" , new String[]{userName});
        if(cursor.getCount()>0) {
            long result = sql.update("user", contentValues, "username=?", new String[]{userName});
            if(result == -1)
            {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }
    public boolean updateLastName(String userName , String lastName )
    {
        SQLiteDatabase sql = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("prenom",lastName);
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE username = ?" , new String[]{userName});
        if(cursor.getCount()>0) {
            long result = sql.update("user", contentValues, "username=?", new String[]{userName});
            if(result == -1)
            {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }
    public boolean updatePassword(String userName , String password)
    {
        SQLiteDatabase sql = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("password",password);
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE username = ?" , new String[]{userName});
        if(cursor.getCount()>0) {
            long result = sql.update("user", contentValues, "username=?", new String[]{userName});
            if(result == -1)
            {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }
    public boolean updatefavorite(String userName , int favorite)
    {
        SQLiteDatabase sql = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("favorites",favorite);
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE username = ?" , new String[]{userName});
        if(cursor.getCount()>0) {
            long result = sql.update("user", contentValues, "username=?", new String[]{userName});
            if(result == -1)
            {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }
    public boolean updateLike(String userName , int like)
    {
        SQLiteDatabase sql = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("likes",like);
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE username = ?" , new String[]{userName});
        if(cursor.getCount()>0) {
            long result = sql.update("user", contentValues, "username=?", new String[]{userName});
            if(result == -1)
            {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }
    public boolean updateHate(String userName , int hate)
    {
        SQLiteDatabase sql = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("hates",hate);
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE username = ?" , new String[]{userName});
        if(cursor.getCount()>0) {
            long result = sql.update("user", contentValues, "username=?", new String[]{userName});
            if(result == -1)
            {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }
    public boolean deleteData(String userName)
    {
        SQLiteDatabase sql = data.getWritableDatabase();
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE username = ?" , new String[]{userName});
        if(cursor.getCount()>0) {
            long result = sql.delete("user", "username=?", new String[]{userName});
            if(result == -1)
            {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }
    public Cursor getData()
    {
        SQLiteDatabase sql = data.getReadableDatabase();
        Cursor cursor = sql.rawQuery("SELECT * FROM user", null);
        return cursor;
    }
    public String getUserID(String etat)
    {
        SQLiteDatabase sql = data.getReadableDatabase();
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE etat =?", new String[]{etat});
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            return cursor.getString(0);
        }
        else
        {
            return UNFOUND;
        }
    }
    public String getPassword(String username)
    {
        SQLiteDatabase sql = data.getReadableDatabase();
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE username =?", new String[]{username});
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            return cursor.getString(4);
        }
        else
        {
            return UNFOUND;
        }
    }
    public String getNom(String username)
    {
        SQLiteDatabase sql = data.getReadableDatabase();
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE username =?", new String[]{username});
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            return cursor.getString(1);
        }
        else
        {
            return UNFOUND;
        }
    }
    public String getPrenom(String username)
    {
        SQLiteDatabase sql = data.getReadableDatabase();
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE username =?", new String[]{username});
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            return cursor.getString(2);
        }
        else
        {
            return UNFOUND;
        }
    }
    public String getMail(String username)
    {
        SQLiteDatabase sql = data.getReadableDatabase();
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE username =?", new String[]{username});
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            return cursor.getString(3);
        }
        else
        {
            return UNFOUND;
        }
    }
    public boolean isExist(String username)
    {
        SQLiteDatabase sql = data.getReadableDatabase();
        Cursor cursor = sql.rawQuery("SELECT * FROM user WHERE username =?", new String[]{username});
        cursor.moveToLast();
        if(!cursor.isAfterLast())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public void logout()
    {
        updateEtat(getUserID(CONNECTED),DISCONNECTED);
    }



}
