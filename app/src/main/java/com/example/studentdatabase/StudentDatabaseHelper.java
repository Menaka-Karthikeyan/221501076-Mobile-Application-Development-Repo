package com.example.studentdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudentDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "student.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CREATE =
            "CREATE TABLE Student (" +
                    "RollNo INTEGER PRIMARY KEY, " +
                    "Name TEXT, " +
                    "CGPA REAL, " +
                    "PhoneNo TEXT, " +
                    "Address TEXT);";

    public StudentDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Student");
        onCreate(db);
    }

    // Insert student
    public void insertStudent(String rollNo, String name, String cgpa, String phoneNo, String address) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("RollNo", rollNo);
        values.put("Name", name);
        values.put("CGPA", cgpa);
        values.put("PhoneNo", phoneNo);
        values.put("Address", address);
        db.insert("Student", null, values);
    }

    // Update student
    public void updateStudent(String rollNo, String name, String cgpa, String phoneNo, String address) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("CGPA", cgpa);
        values.put("PhoneNo", phoneNo);
        values.put("Address", address);
        db.update("Student", values, "RollNo = ?", new String[]{rollNo});
    }

    // Delete student
    public void deleteStudent(String rollNo) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Student", "RollNo = ?", new String[]{rollNo});
    }

    // Get details of a student
    public Cursor getStudentDetails(String rollNo) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query("Student", null, "RollNo = ?", new String[]{rollNo}, null, null, null);
    }

    // Get all students
    public Cursor getAllStudents() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query("Student", null, null, null, null, null, null);
    }
}