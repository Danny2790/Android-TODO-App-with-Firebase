package com.akash.sminqtask.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.akash.sminqtask.Constants.Constant;
import com.akash.sminqtask.R;

public class TodoActivity extends Activity {
    EditText editTextTodoName;
    Button btnAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        editTextTodoName = (EditText)findViewById(R.id.et_todo_name);
        btnAdd = (Button)findViewById(R.id.btn_add);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToList();
            }
        });
    }

    private void addItemToList() {
        String todoName = editTextTodoName.getText().toString();
        if(todoName.length() != 0){
            Intent intent = new Intent();
            intent.putExtra(Constant.TODO_KEY, todoName);
            this.setResult(RESULT_OK,intent);
            finish();
        }else {
            Toast.makeText(this, Constant.EMPTY_TODO_MESSAGE,
                    Toast.LENGTH_LONG).show();
        }
    }
}
