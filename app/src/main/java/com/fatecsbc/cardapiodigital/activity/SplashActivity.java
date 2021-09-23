package com.fatecsbc.cardapiodigital.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.fatecsbc.cardapiodigital.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // mostra a tela por 3 segundos depois abre a autenticação
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                abrirAutenticacao();
            }
        }, 3000);
    }


    private void abrirAutenticacao(){
        Intent i = new Intent(SplashActivity.this,AutenticacaoActivity.class);
        startActivity(i);
        finish();
    }
}
