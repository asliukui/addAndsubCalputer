package calputer.demo.com.calputer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import calputer.demo.com.calputer.widget.KeyboardUtil;

public class MainActivity extends AppCompatActivity {

    private KeyboardUtil keyboardUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText ed = findViewById(R.id.ed);
        keyboardUtil = new KeyboardUtil(MainActivity.this, MainActivity.this, ed);
        keyboardUtil.setOnEnterListener(new KeyboardUtil.EnterListener() {
            @Override
            public void enter() {
                Toast.makeText(MainActivity.this, "确定", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
