package com.fatecsbc.cardapiodigital.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;

import com.fatecsbc.cardapiodigital.R;

public class PoliticaPrivacidadeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica_privacidade);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //exibindo bagina da web
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("https://sites.google.com/view/fastmanu-termos-de-uso/in%C3%ADcio");

    }

    private void abrirConfiguracao() {
        Intent intent = new Intent();
        intent.putExtras(getIntent().getExtras());
        setResult(Activity.RESULT_OK, intent);
        finish();

        /*
        String nome = "", cpf = "", email = "", estado = "", nascimento = "", telefone = "";

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            nome = getIntent().getStringExtra("nome");
            cpf = getIntent().getStringExtra("cpf");
            email = getIntent().getStringExtra("email");
            estado = getIntent().getStringExtra("estado");
            nascimento = getIntent().getStringExtra("nascimento");
            telefone = getIntent().getStringExtra("telefone");

        }

        data.putExtra("nomeR", nome);
        data.putExtra("cpfR", cpf);
        data.putExtra("emailR", email);
        data.putExtra("estadoR", estado);
        data.putExtra("nascimentoR", nascimento);
        data.putExtra("telefoneR", telefone);
        startActivity(data);

         */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_termos, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuCadastro:
                abrirConfiguracao();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
