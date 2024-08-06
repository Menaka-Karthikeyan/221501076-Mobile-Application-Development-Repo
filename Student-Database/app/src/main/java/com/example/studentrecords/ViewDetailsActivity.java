package com.example.studentrecords;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewDetailsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        tableLayout = findViewById(R.id.tableLayout);
        dbHelper = new DatabaseHelper(this);
        loadStudentDetails();
    }

    private void loadStudentDetails() {
        Cursor cursor = dbHelper.getAllStudents();
        if (cursor != null) {
            int sNo = 1; // Initialize Serial Number
            while (cursor.moveToNext()) {
                TableRow row = new TableRow(this);

                TextView tvSNo = new TextView(this);
                tvSNo.setText(String.valueOf(sNo));
                row.addView(tvSNo);
                tvSNo.setBackgroundColor(0xFFFFE4E1);
                tvSNo.setTextColor(0xFF800080);

                TextView tvRollNo = new TextView(this);
                tvRollNo.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROLL_NO)));
                row.addView(tvRollNo);
                tvRollNo.setBackgroundColor(0xFFFFE4E1);
                tvRollNo.setTextColor(0xFF800080);

                TextView tvName = new TextView(this);
                tvName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));
                row.addView(tvName);
                tvName.setBackgroundColor(0xFFFFE4E1);
                tvName.setTextColor(0xFF800080);

                TextView tvMarks = new TextView(this);
                tvMarks.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MARKS)));
                row.addView(tvMarks);
                tvMarks.setBackgroundColor(0xFFFFE4E1);
                tvMarks.setTextColor(0xFF800080);

                tableLayout.addView(row);
                sNo++;
            }
            cursor.close();
        }
    }
}