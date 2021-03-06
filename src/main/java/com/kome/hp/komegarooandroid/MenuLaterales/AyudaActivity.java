package com.kome.hp.komegarooandroid.MenuLaterales;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.kome.hp.komegarooandroid.MenuLaterales.PreguntasAyuda.Pregunta1Activity;
import com.kome.hp.komegarooandroid.MenuLaterales.PreguntasAyuda.Pregunta2Activity;
import com.kome.hp.komegarooandroid.MenuLaterales.PreguntasAyuda.Pregunta3Activity;
import com.kome.hp.komegarooandroid.MenuLaterales.PreguntasAyuda.Pregunta4Activity;
import com.kome.hp.komegarooandroid.R;

public class AyudaActivity extends AppCompatActivity {
    private Button close;
    String[] preguntas ={
            "¿Primera Pregunta?",
            "¿Segunda Pregunta?",
            "¿Tercera Pregunta?",
            "¿Cuarta Pregunta?",
    };
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);
        close = (Button) findViewById(R.id.btnAyuda);
        list = (ListView)findViewById(R.id.listView);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,preguntas);
        list.setAdapter(adapter);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pregunta = (String)parent.getItemAtPosition(position);
                if(position == 0){
                    Intent intent = new Intent(AyudaActivity.this, Pregunta1Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }else if(position == 1){
                    Intent intent = new Intent(AyudaActivity.this, Pregunta2Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
                }else if(position == 2){
                    Intent intent = new Intent(AyudaActivity.this, Pregunta3Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
                }else if(position == 3){
                    Intent intent = new Intent(AyudaActivity.this, Pregunta4Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}