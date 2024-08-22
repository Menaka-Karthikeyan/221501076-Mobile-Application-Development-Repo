package com.example.studentdatabase;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ViewStudentListActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private StudentDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        tableLayout = findViewById(R.id.tableLayoutStudentList);
        dbHelper = new StudentDatabaseHelper(this);

        displayStudentList();
    }

    private void displayStudentList() {
        Cursor cursor = dbHelper.getAllStudents();
        tableLayout.removeAllViews();

        // Add header row
        TableRow headerRow = new TableRow(this);
        String[] headers = {"S.No", "Roll No", "Name", "CGPA", "Phone No", "Address"};
        for (String header : headers) {
            TextView headerTextView = new TextView(this);
            headerTextView.setText(header);
            headerTextView.setPadding(8, 8, 8, 8);
            headerTextView.setBackgroundColor(ContextCompat.getColor(this, R.color.soft_peach));
            headerTextView.setTextColor(ContextCompat.getColor(this, R.color.dark_purple));
            headerRow.addView(headerTextView);
        }
        tableLayout.addView(headerRow);

        // Add data rows
        int index = 1;
        while (cursor.moveToNext()) {
            TableRow row = new TableRow(this);

            // Add S.No column
            TextView sNoTextView = new TextView(this);
            sNoTextView.setText(String.valueOf(index++));
            sNoTextView.setPadding(8, 8, 8, 8);
            sNoTextView.setBackgroundColor(ContextCompat.getColor(this, R.color.misty_rose));
            sNoTextView.setTextColor(ContextCompat.getColor(this, R.color.purple));
            row.addView(sNoTextView);

            // Add other columns
            for (int i = 0; i < 5; i++) {
                TextView textView = new TextView(this);
                textView.setText(cursor.getString(i));
                textView.setPadding(8, 8, 8, 8);
                textView.setBackgroundColor(ContextCompat.getColor(this, R.color.misty_rose));
                textView.setTextColor(ContextCompat.getColor(this, R.color.purple));
                row.addView(textView);
            }

            tableLayout.addView(row);
        }
        cursor.close();
    }
}