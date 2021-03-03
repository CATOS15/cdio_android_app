package group15.card;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RulesActivity extends AppCompatActivity implements View.OnClickListener{

    Button rules_backbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        rules_backbutton = findViewById(R.id.rules_backbutton);
        rules_backbutton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == rules_backbutton){
            finish();
        }
    }

}
