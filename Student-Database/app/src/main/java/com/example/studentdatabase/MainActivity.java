package com.example.studentdatabase;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

public class MainActivity extends AppCompatActivity {

    private EditText etRollNo, etName, etCGPA, etPhoneNo, etAddress;
    private StudentDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        etRollNo = findViewById(R.id.etRollNo);
        etName = findViewById(R.id.etName);
        etCGPA = findViewById(R.id.etCGPA);
        etPhoneNo = findViewById(R.id.etPhoneNo);
        etAddress = findViewById(R.id.etAddress);
        Button btnInsert = findViewById(R.id.btnInsert);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnView = findViewById(R.id.btnView);
        Button btnGetDetails = findViewById(R.id.btnGetDetails);

        dbHelper = new StudentDatabaseHelper(this);

        btnInsert.setOnClickListener(v -> insertStudent());
        btnUpdate.setOnClickListener(v -> updateStudent());
        btnDelete.setOnClickListener(v -> deleteStudent());
        btnView.setOnClickListener(v -> viewDetails());
        btnGetDetails.setOnClickListener(v -> getDetails());
    }

    private void insertStudent() {
        String rollNo = etRollNo.getText().toString();
        String name = etName.getText().toString();
        String cgpa = etCGPA.getText().toString();
        String phoneNo = etPhoneNo.getText().toString();
        String address = etAddress.getText().toString();

        if (rollNo.isEmpty() || name.isEmpty() || cgpa.isEmpty() || phoneNo.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.insertStudent(rollNo, name, cgpa, phoneNo, address);
        Toast.makeText(this, "Student inserted", Toast.LENGTH_SHORT).show();
        clearFields();
    }

    private void updateStudent() {
        String rollNo = etRollNo.getText().toString();
        String name = etName.getText().toString();
        String cgpa = etCGPA.getText().toString();
        String phoneNo = etPhoneNo.getText().toString();
        String address = etAddress.getText().toString();

        if (rollNo.isEmpty()) {
            Toast.makeText(this, "Roll No is required", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.updateStudent(rollNo, name, cgpa, phoneNo, address);
        Toast.makeText(this, "Student updated", Toast.LENGTH_SHORT).show();
        clearFields();
    }

    private void deleteStudent() {
        String rollNo = etRollNo.getText().toString();

        if (rollNo.isEmpty()) {
            Toast.makeText(this, "Roll No is required", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.deleteStudent(rollNo);
        Toast.makeText(this, "Student deleted", Toast.LENGTH_SHORT).show();
        clearFields();
    }

    private void viewDetails() {
        Intent intent = new Intent(this, ViewStudentListActivity.class);
        startActivity(intent);
    }

    private void getDetails() {
        String rollNo = etRollNo.getText().toString().trim();
        if (rollNo.isEmpty()) {
            Toast.makeText(this, "Please enter a roll number", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.getStudentDetails(rollNo);

        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("Name");
            int cgpaIndex = cursor.getColumnIndex("CGPA");
            int phoneNoIndex = cursor.getColumnIndex("PhoneNo");
            int addressIndex = cursor.getColumnIndex("Address");

            if (nameIndex != -1 && cgpaIndex != -1 && phoneNoIndex != -1 && addressIndex != -1) {
                String name = cursor.getString(nameIndex);
                String cgpa = cursor.getString(cgpaIndex);
                String phoneNo = cursor.getString(phoneNoIndex);
                String address = cursor.getString(addressIndex);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Student Details")
                        .setMessage("Roll No: " + rollNo + "\nName: " + name + "\nCGPA: " + cgpa + "\nPhone No: " + phoneNo + "\nAddress: " + address)
                        .setPositiveButton("OK", null)
                        .show();

                Toast.makeText(this, "Student found", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(this, "No student found with this roll number", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } else {
            Toast.makeText(this, "No student found with this roll number", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        etRollNo.setText("");
        etName.setText("");
        etCGPA.setText("");
        etPhoneNo.setText("");
        etAddress.setText("");
    }
}