package com.fatecsbc.cardapiodigital.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fatecsbc.cardapiodigital.R;
import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.fatecsbc.cardapiodigital.helper.UsuarioFirebase;
import com.fatecsbc.cardapiodigital.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {

    private EditText editUsuarioNome, editUsuarioEmail, editUsuarioNascimento, editUsuarioCPF,
            editUsuarioTelefone, editUsuarioEstado;
    private TextView lerTermos;
    private Button salvar, deletar;
    private Spinner estados;
    private CheckBox termos;
    private TextView selecionarEstado;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado, estadoAtual;
    private FirebaseUser usuario, usuarioEmail;
    private boolean visible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);

        // configurações iniciais
        inicializarComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        usuarioEmail = UsuarioFirebase.getUsuarioAtual();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        //pegar infos de outras Activitys
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String email = getIntent().getStringExtra("email");
            editUsuarioEmail.setText(email);
        }

        // configurando spinner
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.estados,
                R.layout.support_simple_spinner_dropdown_item);
        estados.setAdapter(arrayAdapter);

        // configurando toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações Usuário");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // evento clique salvarDados
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDadosUsuario();
            }
        });

        // recuperando informações do usuario
        recuperarDadosUsuario();

        // arruma o estado de volta ao spinner
        editUsuarioEstado.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editUsuarioEstado.setVisibility(View.GONE);
                selecionarEstado.setVisibility(View.VISIBLE);
                estados.setVisibility(View.VISIBLE);
                return true;
            }
        });

        //leitura dos termos de uso
        lerTermos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTermosDeUso();
            }
        });

        editUsuarioTelefone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usuarioEmail = UsuarioFirebase.getUsuarioAtual();
                usuarioEmail.reload();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                recuperarApartirDePoliticaPrivacidadeActivity(data);
            }
        }
    }

    private void abrirTermosDeUso() {

        Bundle bundle = new Bundle();
        bundle.putString("nome", editUsuarioNome.getText().toString());
        bundle.putString("cpf", editUsuarioCPF.getText().toString());
        bundle.putString("email", editUsuarioEmail.getText().toString());
        bundle.putString("estado", editUsuarioEstado.getText().toString());
        bundle.putString("nascimento", editUsuarioNascimento.getText().toString());
        bundle.putString("telefone", editUsuarioTelefone.getText().toString());
        Intent intent = new Intent(ConfiguracoesUsuarioActivity.this, PoliticaPrivacidadeActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }

    private void termosCondicao(boolean ativar) {
        if (ativar == true) {
            termos.setVisibility(View.VISIBLE);
            visible = true;
        } else {
            termos.setVisibility(View.GONE);
            visible = false;
        }
    }

    private void recuperarApartirDePoliticaPrivacidadeActivity(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            editUsuarioTelefone.setText(bundle.getString("telefone"));
            editUsuarioNome.setText(bundle.getString("nome"));
            editUsuarioCPF.setText(bundle.getString("cpf"));
            editUsuarioEmail.setText(bundle.getString("email"));
            editUsuarioEstado.setText(bundle.getString("estado"));
            editUsuarioNascimento.setText(bundle.getString("nascimento"));
        }

    }

    private void recuperarDadosUsuario() {

        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuarioLogado);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {

                    termosCondicao(false);
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    editUsuarioNome.setText(usuario.getNome());
                    editUsuarioEmail.setText(usuario.getEmail());
                    editUsuarioNascimento.setText(usuario.getDataNascimento());
                    if (usuario.getEstado() != null) {
                        selecionarEstado.setVisibility(View.GONE);
                        estados.setVisibility(View.GONE);
                        editUsuarioEstado.setText(usuario.getEstado());
                        editUsuarioEstado.setVisibility(View.VISIBLE);
                        estadoAtual = usuario.getEstado();
                    }
                    editUsuarioCPF.setText(usuario.getCpf());
                    editUsuarioTelefone.setText(usuario.getTelefone());
                    deletar.setVisibility(View.VISIBLE);

                } else {

                    termosCondicao(true);
                    deletar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void validarDadosUsuario() {

        usuarioEmail = UsuarioFirebase.getUsuarioAtual();
        usuarioEmail.reload();

        String nome = editUsuarioNome.getText().toString().trim();
        String email = editUsuarioEmail.getText().toString().trim();
        String nascimento = editUsuarioNascimento.getText().toString().trim();
        String estado = estados.getSelectedItem().toString().trim();
        String cpf = editUsuarioCPF.getText().toString().trim();
        String telefone = editUsuarioTelefone.getText().toString().trim();

        if (!nome.isEmpty()) {
            if (!email.isEmpty()) {
                if (!nascimento.isEmpty()) {
                    if (!cpf.isEmpty()) {
                        if (!telefone.isEmpty()) {
                            if (visible == true && termos.isChecked() || visible == false) {
                                if (usuarioEmail.isEmailVerified()) {

                                    Usuario usuario = new Usuario();
                                    usuario.setIdUsuario(idUsuarioLogado);
                                    usuario.setNome(nome);
                                    usuario.setEmail(email);
                                    usuario.setDataNascimento(nascimento);
                                    usuario.setCpf(cpf);
                                    usuario.setTelefone(telefone);
                                    if (!estado.equals("Selecionar Estado")) {
                                        usuario.setEstado(estado);
                                    } else {
                                        usuario.setEstado(estadoAtual);
                                    }
                                    Toast.makeText(this,
                                            "Cadastrado! Já pode fazer seu pedido!",
                                            Toast.LENGTH_SHORT).show();
                                    usuario.salvar();
                                    startActivity(new Intent(ConfiguracoesUsuarioActivity.this, SplashActivity.class));
                                    finish();

                                } else {

                                    exibirMensagem("Precisa confirmar seu email! Verifique em sua caixa de entrada");
                                    usuarioEmail.reload();

                                    /*
                                    AlertDialog.Builder JOP = new AlertDialog.Builder(this);
                                    JOP.setTitle("Aviso! Verifique seu email antes de prosseguir");
                                    JOP.setMessage("Para usar o aplicativo, é necessário confirma-lo!");
                                    JOP.setIcon(getResources().getDrawable(R.drawable.ic_warning));
                                    JOP.setCancelable(false);

                                    JOP.setPositiveButton("Entendi!", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            usuarioEmail.reload();
                                            startActivity(new Intent(ConfiguracoesUsuarioActivity.this, SplashActivity.class));
                                            finish();

                                        }
                                    });
                                    JOP.setNegativeButton("Não recebi o email", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            usuarioEmail.sendEmailVerification();
                                            Toast.makeText(ConfiguracoesUsuarioActivity.this, "Verificação enviada novamente!Caso não funcione, entre em contato com o suporte",
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                        }

                                    });
                                    JOP.create();
                                    JOP.show();

                                     */


                                }
                            } else {
                                exibirMensagem("Aceite os termos de uso antes de continuar!");
                            }

                        } else {
                            exibirMensagem("Digite seu telefone corretamente !");
                        }
                    } else {
                        exibirMensagem("Digite o seu CPF!");
                    }
                } else {
                    exibirMensagem("Digite sua data de nascimento xx/xx/xxxx");
                }
            } else {
                exibirMensagem("Digite seu email!");
            }
        } else {
            exibirMensagem("Digite seu nome!");
        }

    }

    private void inicializarComponentes() {
        editUsuarioNome = findViewById(R.id.editUsuarioNome);
        editUsuarioEmail = findViewById(R.id.editUsuarioEmail);
        editUsuarioNascimento = findViewById(R.id.editUsuarioNascimento);
        editUsuarioCPF = findViewById(R.id.editUsuarioCPF);
        editUsuarioTelefone = findViewById(R.id.editUsuarioTelefone);
        salvar = findViewById(R.id.buttonSalvarUsuario);
        deletar = findViewById(R.id.buttonDeletar);
        estados = findViewById(R.id.spinnerEstados);
        selecionarEstado = findViewById(R.id.textSelecionarEstado);
        editUsuarioEstado = findViewById(R.id.editUsuarioEstado);
        termos = findViewById(R.id.termosCheck);
        lerTermos = findViewById(R.id.textTermosEmp);

    }

    private void exibirMensagem(String texto) {

        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();

    }

    public void deletarConta(View view) {

        AlertDialog.Builder JOP = new AlertDialog.Builder(this);
        JOP.setTitle("Confirmação excluir conta");
        JOP.setMessage("Realmente deseja excluir sua conta?");
        JOP.setIcon(getResources().getDrawable(R.drawable.ic_warning));
        JOP.setCancelable(false);

        JOP.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                usuario = UsuarioFirebase.getUsuarioAtual();
                Usuario usuarioExcluir = new Usuario();
                usuarioExcluir.setIdUsuario(idUsuarioLogado);
                usuarioExcluir.excluirConta();
                usuario.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ConfiguracoesUsuarioActivity.this,
                                "Conta excluída com sucesso!",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ConfiguracoesUsuarioActivity.this, SplashActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ConfiguracoesUsuarioActivity.this,
                                "Falha em excluir a conta! Entre em sua conta novamente e tente de novo!" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(ConfiguracoesUsuarioActivity.this, AutenticacaoActivity.class));
                    }
                });
            }

        });
        JOP.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ConfiguracoesUsuarioActivity.this, "Cancelado!",
                        Toast.LENGTH_SHORT)
                        .show();
            }

        });

        JOP.create();
        JOP.show();

    }
}
