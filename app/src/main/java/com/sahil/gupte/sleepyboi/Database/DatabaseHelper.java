package com.sahil.gupte.sleepyboi.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sahil.gupte.sleepyboi.Constants;
import com.sahil.gupte.sleepyboi.Customs.PlaceInfoHolder;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String dbName="PlaceKeeper.db";
    static final String tableName="Places";
    static final String colID="PlaceID";
    static final String colName= Constants.placeName;
    static final String colAddress=Constants.placeAddress;
    static final String colLat="PlaceLat";
    static final String colLng="PlaceLng";

    public DatabaseHelper(Context context) {
        super(context, dbName, null,1);
    }


    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL("CREATE TABLE " + tableName + " (" + colID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + colName + " TEXT, " + colAddress + " Integer, " + colLat + " LONG NOT NULL ," + colLng + " LONG NOT NULL );");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public void addHandler(PlaceInfoHolder placeInfoHolder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(colName, placeInfoHolder.getName());
        values.put(colAddress, placeInfoHolder.getAddress());
        values.put(colLat, placeInfoHolder.getLatitude());
        values.put(colLng, placeInfoHolder.getLongitude());
        if (!getCountArray().contains(placeInfoHolder.getDbCount())) {
            db.insert(tableName, null, values);
            db.close();
        } else {
            db.update(tableName, values, colID+" = "+placeInfoHolder.getDbCount(), null);
            db.close();
        }
    }

    public boolean removeHandler(int id) {
        boolean result = false;
        String query = "Select * FROM " + tableName + " WHERE " + colID + " = '" + id + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            db.delete(tableName, colID + "=?",
                    new String[] {
                ""+id
            });
            cursor.close();
            result = true;
        }
        db.close();
        return result;

    }

    public PlaceInfoHolder getHandler(int id) {
        String query = "Select * FROM " + tableName + " WHERE " + colID + " = " + "'" + id + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        PlaceInfoHolder placeInfoHolder;
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            placeInfoHolder = new PlaceInfoHolder(cursor.getDouble(3), cursor.getDouble(4), cursor.getString(2), cursor.getString(1), cursor.getInt(0));
            cursor.close();
        } else {
            placeInfoHolder = null;
        }
        db.close();
        return placeInfoHolder;
    }

    public ArrayList<Integer> getCountArray() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + tableName;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Integer> countArray = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            countArray.add(cursor.getInt(0));
            cursor.moveToNext();
        }
        cursor.close();
        return countArray;
    }
}
