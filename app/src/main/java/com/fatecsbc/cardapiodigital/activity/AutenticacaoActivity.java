package com.fatecsbc.cardapiodigital.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.fatecsbc.cardapiodigital.R;
import com.fatecsbc.cardapiodigital.activity.EmpresaActivity;
import com.fatecsbc.cardapiodigital.activity.HomeActivity;
import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.fatecsbc.cardapiodigital.helper.UsuarioFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class AutenticacaoActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private TextView textSenha;
    private Switch tipoAcesso,tipoUsuario;
    private FirebaseAuth autenticacao;
    private LinearLayout linearUsuario;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);

        inicializaComponentes();
        switchCadastroLogin();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        // verifica usuario logado
        verificaUsuarioLogado();
        //autenticacao.signOut();

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if(!email.isEmpty() && !senha.isEmpty() ){

                    // verifica se é cadastro ou login
                    if(tipoAcesso.isChecked()){//cadastro

                        autenticacao.createUserWithEmailAndPassword(
                                email,senha
                        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    firebaseUser = UsuarioFirebase.getUsuarioAtual();
                                    firebaseUser.sendEmailVerification();
                                    //String email = firebaseUser.getEmail();
                                    Intent i = new Intent(AutenticacaoActivity.this,ConfiguracoesUsuarioActivity.class);
                                    startActivityForResult(i,1);

                                    Toast.makeText(AutenticacaoActivity.this,
                                            "Verifique seu email em sua caixa de entrada e complete seu cadastro.",
                                            Toast.LENGTH_LONG)
                                            .show();

                                    String tipoUsuario = getTipoUsuario();
                                    UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);
                                    abrirTelaConfiguracoes(tipoUsuario);
                                    finish();

                                }else{
                                    String erroExcecao="";
                                    try{
                                        throw task.getException();
                                    }catch (FirebaseAuthWeakPasswordException e){
                                        erroExcecao = "Digite uma senha mais forte!";
                                    }catch (FirebaseAuthInvalidCredentialsException e){
                                        erroExcecao = "Digite um email válido!";
                                    }catch (FirebaseAuthUserCollisionException e){
                                        erroExcecao = "Conta já cadastrada";
                                    }catch (Exception e){
                                        erroExcecao = "Erro: "+e.getMessage();
                                        e.printStackTrace();
                                    }

                                    Toast.makeText(AutenticacaoActivity.this,
                                            "Erro: " + erroExcecao ,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });

                    }else{//login

                        autenticacao.signInWithEmailAndPassword(email,senha)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(AutenticacaoActivity.this,
                                                    "Bem Vindo!",
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                            String tipoUsuario = task.getResult().getUser().getDisplayName();
                                            abrirTelaPrincipal(tipoUsuario);
                                        }else{
                                            Toast.makeText(AutenticacaoActivity.this,
                                                    "Nome de usuário e/ou senha incorreto(s)!",
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    }
                                });
                    }

                }else{
                    Toast.makeText(AutenticacaoActivity.this, "Preencha os campos de Email e Senha!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirRecuperarSenha();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case 1:
                data.putExtra("email",campoEmail.getText());
        }
    }

    private void abrirTelaPrincipal(String tipoUsuario) {

        if (tipoUsuario.equals("E")){
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
        }else{
            startActivity(new Intent(getApplicationContext(), QRCodeActivity.class));
        }
        finish();
    }

    private void abrirRecuperarSenha(){
        startActivity(new Intent(getApplicationContext(), RecuperasenhaActivity.class));
    }

    private void verificaUsuarioLogado(){
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null){
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }
    }

    private String getTipoUsuario(){
        return tipoUsuario.isChecked() ? "E" : "C";
    }

    private void abrirTelaConfiguracoes(String tipoUsuario){
        if (tipoUsuario.equals("E")){
            startActivity(new Intent(getApplicationContext(), ConfiguracoesEmpresaActivity.class));
        }else{
            startActivity(new Intent(getApplicationContext(), ConfiguracoesUsuarioActivity.class));
        }
        finish();
    }

    private void inicializaComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        tipoAcesso = findViewById(R.id.switchAcesso);
        tipoUsuario = findViewById(R.id.switchTipoUsuario);
        linearUsuario = findViewById(R.id.linearTipoUsuario);
        textSenha = findViewById(R.id.textSenha);
    }

    private void switchCadastroLogin(){
        tipoAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tipoAcesso.isChecked()){
                    botaoAcessar.setText("CADASTRAR");
                    linearUsuario.setVisibility(View.VISIBLE);
                    textSenha.setVisibility(View.GONE);
                }else{
                    botaoAcessar.setText("ACESSAR");
                    linearUsuario.setVisibility(View.GONE);
                    textSenha.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showPassword(){
        /*implementar*/
    }



}
