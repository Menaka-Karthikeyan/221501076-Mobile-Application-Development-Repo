package com.example.studentrecords;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etRollNo, etName, etMarks;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etRollNo = findViewById(R.id.etRollNo);
        etName = findViewById(R.id.etName);
        etMarks = findViewById(R.id.etMarks);
        Button btnInsert = findViewById(R.id.btnInsert);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnView = findViewById(R.id.btnView);

        dbHelper = new DatabaseHelper(this);

        btnInsert.setOnClickListener(v -> insertStudent());

        btnUpdate.setOnClickListener(v -> updateStudent());

        btnDelete.setOnClickListener(v -> deleteStudent());

        btnView.setOnClickListener(v -> viewStudentDetails());
    }

    private void insertStudent() {
        String rollNo = etRollNo.getText().toString();
        String name = etName.getText().toString();
        int marks = Integer.parseInt(etMarks.getText().toString());
        long result = dbHelper.insertStudent(Integer.parseInt(rollNo), name, marks);
        if (result != -1) {
            Toast.makeText(this, "Student inserted successfully", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(this, "Error inserting student", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStudent() {
        String rollNo = etRollNo.getText().toString();
        String name = etName.getText().toString();
        int marks = Integer.parseInt(etMarks.getText().toString());
        int rowsUpdated = dbHelper.updateStudent(rollNo, name, marks);
        if (rowsUpdated > 0) {
            Toast.makeText(this, "Student updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error updating student", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteStudent() {
        String rollNo = etRollNo.getText().toString();
        int rowsDeleted = dbHelper.deleteStudent(rollNo);
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Student deleted successfully", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(this, "Error deleting student", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewStudentDetails() {
        Log.d("MainActivity", "View Details button clicked");
        Intent intent = new Intent(this, ViewDetailsActivity.class);
        startActivity(intent);
    }

    private void clearFields() {
        etRollNo.setText("");
        etName.setText("");
        etMarks.setText("");
    }
}