package com.akash.sminqtask.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akash.sminqtask.R;

import java.util.ArrayList;

/**
 * Created by akash on 8/10/2017.
 */

public class AdapterTodo extends RecyclerView.Adapter<AdapterTodo.TodoViewHolder> {
    private Context context;
    private ArrayList<String> todoList;

    public AdapterTodo(Context context, ArrayList<String> todolist) {
        this.context = context;
        this.todoList = todolist;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        String todoName = todoList.get(position);
        holder.textViewTodoName.setText(todoName);
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTodoName;

        public TodoViewHolder(View itemView) {
            super(itemView);
            textViewTodoName = (TextView) itemView.findViewById(R.id.tv_todo_name);
        }
    }
}
