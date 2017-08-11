package com.akash.sminqtask.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.akash.sminqtask.Constants.Constant;
import com.akash.sminqtask.Models.Todo;
import com.akash.sminqtask.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TodoActivity.class);
                startActivityForResult(intent, Constant.REQUEST_ADD_TODO);
            }
        });
    }

    public void setupFirebaseDB() {
        mFirebaseDatabaseReference = mFirebaseDatabase.getReference("todolist");
        Log.d(TAG, "onCreate: db root " + mFirebaseDatabaseReference.toString());
        setupFirebaseEventListener();
    }

    public void setupFirebaseEventListener() {
        mFirebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: ");
                getAllTodo(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged: ");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: ");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved: ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });
    }

    private void getAllTodo(DataSnapshot dataSnapshot) {
        String todoName;
        for (DataSnapshot singleShot : dataSnapshot.getChildren()) {
            todoName = singleShot.getValue(String.class);
            Log.d(TAG, "todo Item: " + todoName);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        signInAnonymously();
    }

    public void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            setupFirebaseDB();
                            Log.d(TAG, "signInAnonymously:success");
                        } else {
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == Constant.REQUEST_ADD_TODO) {
                String todoName = data.getStringExtra(Constant.TODO_KEY);
                Log.d(TAG, "onActivityResult: " + todoName);
                updateTodoList(todoName);
            }
        }

    }

    public void retriveTodoList(FirebaseUser user) {

    }

    public void updateTodoList(String todoName) {
        Todo todo = new Todo(todoName);
        mFirebaseDatabaseReference.push().setValue(todo);
        Log.d(TAG, "updateTodoList: pushed value" + todoName);
    }
}
