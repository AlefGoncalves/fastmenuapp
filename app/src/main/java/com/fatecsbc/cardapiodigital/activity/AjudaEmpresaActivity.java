package com.fatecsbc.cardapiodigital.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fatecsbc.cardapiodigital.R;

public class AjudaEmpresaActivity extends AppCompatActivity {

    private Button buttonVoltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajuda_empresa);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ajuda - Cardápio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonVoltar = findViewById(R.id.buttonVoltar);
        buttonVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizar();
            }
        });
    }

    private void finalizar(){

       finish();
    }
}
