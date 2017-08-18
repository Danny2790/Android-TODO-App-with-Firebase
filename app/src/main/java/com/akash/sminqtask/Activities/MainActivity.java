package com.akash.sminqtask.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.akash.sminqtask.Adapters.AdapterTodo;
import com.akash.sminqtask.Constants.Constant;
import com.akash.sminqtask.Models.Todo;
import com.akash.sminqtask.R;
import com.akash.sminqtask.Utilities.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerTodoList;
    private AdapterTodo adapterTodo;
    private ArrayList<Todo> todoArrayList = new ArrayList<>();
    Boolean isFirstFetch = true;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        if (mFirebaseDatabase == null) {
            mFirebaseDatabase = Utils.getDatabase();
        }
        mRecyclerTodoList = (RecyclerView) findViewById(R.id.rv_todo);
        mRecyclerTodoList.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerTodoList.setHasFixedSize(true);
        adapterTodo = new AdapterTodo(todoArrayList);
        mRecyclerTodoList.setAdapter(adapterTodo);

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
        //get the reference of database topnode todolist
        // All the child would go under todolist
        mFirebaseDatabaseReference = mFirebaseDatabase.getReference("todolist");
        setupFirebaseEventListener();
    }

    public void setupFirebaseEventListener() {
        hideProgressDialog();
        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");
                isFirstFetch = false;
                getAllTodo(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onDataChange: ");
            }
        });

        mFirebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!isFirstFetch) {
                    getUpdatedTodo(dataSnapshot, s);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged: ");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: ");
                removeFromTodo(dataSnapshot);

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

    private void removeFromTodo(DataSnapshot dataSnapshot) {
        for (DataSnapshot singleShot : dataSnapshot.getChildren()) {
            Log.d(TAG, "getUpdatedTodo: " + singleShot.getValue());
            String todoName = singleShot.getValue(String.class);
            for (int i = 0; i < todoArrayList.size(); i++) {
                if (todoArrayList.get(i).getTask().equals(todoName)) {
                    todoArrayList.remove(i);
                    adapterTodo.notifyItemRemoved(i);
                    adapterTodo.notifyItemRangeChanged(i, todoArrayList.size());
                    break;
                }
            }
        }
    }

    private void getUpdatedTodo(DataSnapshot dataSnapshot, String s) {
        Todo todo;
        for (DataSnapshot singleShot : dataSnapshot.getChildren()) {
            todo = new Todo(singleShot.getValue(String.class));
            todoArrayList.add(todo);
            adapterTodo.notifyItemInserted(todoArrayList.size() - 1);
        }

    }

    private void getAllTodo(DataSnapshot dataSnapshot) {
        todoArrayList.clear();
        Todo todo;
        for (DataSnapshot singleShot : dataSnapshot.getChildren()) {
            todo = singleShot.getValue(Todo.class);
            todoArrayList.add(todo);
        }
        adapterTodo.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        signInAnonymously();
    }

    public void signInAnonymously() {
        showProgressDialog();
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
                addItemToTodoList(todoName);
            }
        }

    }

    public void addItemToTodoList(String todoName) {
        Todo todo = new Todo(todoName);
        mFirebaseDatabaseReference.push().setValue(todo);
    }

    public void showProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading");
        progressDialog.show();
    }

    public void hideProgressDialog(){
        progressDialog.hide();
    }

}
