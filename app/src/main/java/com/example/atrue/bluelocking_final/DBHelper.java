package com.example.atrue.bluelocking_final;

/**
 * Created by Yun on 2016-12-03.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yun on 2016-11-16.
 */

public class DBHelper extends SQLiteOpenHelper {

    private Context context;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("CREATE TABLE TEST_TABLE (");
            sb.append("entry INTEGER PRIMARY KEY AUTOINCREMENT,");
            sb.append("deviceKey CHAR(8),");
            sb.append("pcAddress VARCHAR(20),");
            sb.append("Time DATETIME,");
            sb.append("act CHAR(4));");
            //SQL실행
            db.execSQL(sb.toString());

            //Toast.makeText(context, "DB 생성 완료", Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Toast.makeText(context, "Version 올라감", Toast.LENGTH_SHORT).show();
    }

    public void testDB(){
        SQLiteDatabase db = getReadableDatabase();
    }

    public void InsertTable(TableBowl tb){
        SQLiteDatabase db = getWritableDatabase();

        StringBuffer sb = new StringBuffer();

        sb.append("INSERT INTO TEST_TABLE (");
        sb.append("deviceKey, pcAddress, time, act) ");
        sb.append("VALUES ('" + tb.getDeviceKey() + "','" + tb.getPcAddress() + "','" + tb.getTime() + "','" + tb.getAct() + "')");

        //SQL실행
        db.execSQL(sb.toString());

        //Toast.makeText(context, "Insert 완료", Toast.LENGTH_SHORT).show();
    }

    public String SelectTable(){
        StringBuffer sb = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        sb.append("SELECT * FROM TEST_TABLE");

        SQLiteDatabase db = getReadableDatabase();

        //SELECT 실행
        Cursor cursor = db.rawQuery(sb.toString(), null);

        List<TableBowl> tbs = new ArrayList<TableBowl>();

        TableBowl tb = null;

        //sb2.append("<html><body><table><tr><th>TIME</th><th>DEVICE</th><th>PC</th><th>ACT</th></tr>");

        sb2.append("TIME\tDEVICE\tPC\tACT\n");
        while (cursor.moveToNext() ){
            //sb2.append("<tr><td>" + cursor.getString(3) + "</td><td>" + cursor.getString(1) + "</td><td>" + cursor.getString(2) + "</td><td>" + cursor.getString(4) + "</td></tr>");
            sb2.append(cursor.getString(3) + "\t" + cursor.getString(1) + "\t" + cursor.getString(2) + "\t" + cursor.getString(4) + "\n");

            tb = new TableBowl();
            tb.setDeviceKey(cursor.getString(1));
            tb.setPcAddress(cursor.getString(2));
            tb.setTime(cursor.getString(3));
            tb.setAct(cursor.getString(4));

            tbs.add(tb);
        }

        //sb2.append("</table></body></html>");

        return sb2.toString();
    }
}
