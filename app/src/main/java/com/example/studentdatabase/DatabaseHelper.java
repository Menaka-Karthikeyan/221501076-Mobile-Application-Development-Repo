package com.example.studentdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "StudentDB";
    public static final String TABLE_NAME = "students";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ROLL_NO = "RollNo";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_MARKS = "Marks";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ROLL_NO + " INTEGER UNIQUE, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_MARKS + " INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertStudent(int rollNo, String name, int marks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROLL_NO, rollNo);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_MARKS, marks);

        long result = -1;
        try {
            result = db.insert(TABLE_NAME, null, values);
        } catch (SQLException e) {
            Log.e("DB_INSERT_ERROR", "Error inserting student", e);
        } finally {
            db.close();
        }
        return result;
    }

    public int updateStudent(String rollNo, String name, int marks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_MARKS, marks);

        int rowsUpdated = 0;
        try {
            rowsUpdated = db.update(TABLE_NAME, values, COLUMN_ROLL_NO + "=?", new String[]{rollNo});
        } catch (SQLException e) {
            Log.e("DB_UPDATE_ERROR", "Error updating student", e);
        } finally {
            db.close();
        }
        return rowsUpdated;
    }

    public int deleteStudent(String rollNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = 0;
        try {
            rowsDeleted = db.delete(TABLE_NAME, COLUMN_ROLL_NO + "=?", new String[]{rollNo});
        } catch (SQLException e) {
            Log.e("DB_DELETE_ERROR", "Error deleting student", e);
        } finally {
            db.close();
        }
        return rowsDeleted;
    }

    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ROLL_NO, null);
    }
}