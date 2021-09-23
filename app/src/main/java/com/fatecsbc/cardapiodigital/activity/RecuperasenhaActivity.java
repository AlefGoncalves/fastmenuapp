package com.fatecsbc.cardapiodigital.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.fatecsbc.cardapiodigital.R;
import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.Tag;

public class RecuperasenhaActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail;
    private FirebaseAuth autenticacao;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperasenha);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Redefinição de Senha");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inicializaComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = campoEmail.getText().toString();
                if (!email.isEmpty()) {
                    enviarEmail(email);
                } else {
                    Toast.makeText(RecuperasenhaActivity.this,
                            "Digite o email",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }


    private void abrirAutenticacao() {
        startActivity(new Intent(RecuperasenhaActivity.this, AutenticacaoActivity.class));
        finish();
    }

    private void inicializaComponentes() {
        campoEmail = findViewById(R.id.editCadastroEmail);
        botaoAcessar = findViewById(R.id.buttonAcesso);
    }

    private void enviarEmail(String email) {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RecuperasenhaActivity.this,
                                    "Enviamos um email para você! Clique no link para alterar a senha",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }else{
                            Toast.makeText(RecuperasenhaActivity.this,
                                    "Email não encontrado! Informe um email válido.",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });

        abrirAutenticacao();
    }
}
