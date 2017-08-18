package com.akash.sminqtask.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akash.sminqtask.Models.Todo;
import com.akash.sminqtask.R;
import com.akash.sminqtask.Utilities.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by akash on 8/10/2017.
 */

public class AdapterTodo extends RecyclerView.Adapter<AdapterTodo.TodoViewHolder> {
    private ArrayList<Todo> todoList;
    private String TAG = AdapterTodo.class.getSimpleName();

    public AdapterTodo(ArrayList<Todo> todolist) {
        this.todoList = todolist;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, final int position) {
        Todo todo = todoList.get(position);
        holder.textViewTodoName.setText(todo.getTask());
        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: delete called ");
                String taskTitle = todoList.get(position).getTask();
                FirebaseDatabase firebaseDatabase = Utils.getDatabase();
                DatabaseReference databaseReference = firebaseDatabase.getReference("todolist");
                Query deletequery = databaseReference.orderByChild("task").equalTo(taskTitle);
                deletequery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Cancelled Exception", databaseError.toException());
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTodoName;
        ImageView imageViewDelete;

        public TodoViewHolder(View itemView) {
            super(itemView);
            textViewTodoName = (TextView) itemView.findViewById(R.id.tv_todo_name);
            imageViewDelete = (ImageView) itemView.findViewById(R.id.im_todo_delete);
        }
    }
}
