package com.fatecsbc.cardapiodigital.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fatecsbc.cardapiodigital.R;
import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.fatecsbc.cardapiodigital.helper.UsuarioFirebase;
import com.fatecsbc.cardapiodigital.model.Empresa;
import com.fatecsbc.cardapiodigital.model.Produto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity {

    private EditText editEmpresaNome, editEmpresaCategoria, editEmpresaTempo, editEmpresaSlogan,
            editEmpresaEstado;
    private TextView lerTermos;
    private Button salvar, deletar;
    private FirebaseUser usuario;
    private String urlImagem;
    private ImageView imagePerfilEmpresa;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private Spinner estados;
    private TextView selecionarEstado;
    private String idUsuarioLogado;
    private String imagemRecuperada;
    private String estadoAtual;
    private boolean visible;
    private CheckBox termos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);
        // configurações iniciais
        inicializarComponentes();

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        // configurando spinner
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.estados,
                R.layout.support_simple_spinner_dropdown_item);
        estados.setAdapter(arrayAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDadosEmpresa();


            }
        });

        recuperarDadosEmpresa();

        // arruma o spinner
        // arruma o estado de volta ao spinner
        editEmpresaEstado.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editEmpresaEstado.setVisibility(View.GONE);
                selecionarEstado.setVisibility(View.VISIBLE);
                estados.setVisibility(View.VISIBLE);
                return true;
            }
        });
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

    private void recuperarDadosEmpresa() {

        DatabaseReference empresaRef = firebaseRef
                .child("empresas")
                .child(idUsuarioLogado);

        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue() != null) {

                    termosCondicao(false);

                    Empresa empresa = snapshot.getValue(Empresa.class);
                    editEmpresaNome.setText(empresa.getNome());
                    editEmpresaCategoria.setText(empresa.getCategoria());
                    editEmpresaTempo.setText(empresa.getTempo());
                    editEmpresaSlogan.setText(empresa.getSlogan());
                    editEmpresaEstado.setText(empresa.getEstado());
                    estadoAtual = empresa.getEstado();

                    imagemRecuperada = empresa.getUrlImagem();
                    if (!imagemRecuperada.equals("")) {
                        Picasso.get().load(imagemRecuperada)
                                .into(imagePerfilEmpresa);
                    }

                    if (empresa.getEstado() != null) {
                        selecionarEstado.setVisibility(View.GONE);
                        estados.setVisibility(View.GONE);
                        editEmpresaEstado.setText(empresa.getEstado());
                        editEmpresaEstado.setVisibility(View.VISIBLE);
                    }
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

        //leitura dos termos de uso
        lerTermos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTermosDeUso();
            }
        });


    }

    private void abrirTermosDeUso() {

        Bundle bundle = new Bundle();
        bundle.putString("nome", editEmpresaNome.getText().toString());
        bundle.putString("categoria", editEmpresaCategoria.getText().toString());
        bundle.putString("tempo", editEmpresaTempo.getText().toString());
        bundle.putString("slogan", editEmpresaSlogan.getText().toString());
        bundle.putString("estado", editEmpresaEstado.getText().toString());
        Intent intent = new Intent(ConfiguracoesEmpresaActivity.this, PoliticaPrivacidadeActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }

    private void recuperarApartirDePoliticaPrivacidadeActivity(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            editEmpresaNome.setText(bundle.getString("nome"));
            editEmpresaCategoria.setText(bundle.getString("categoria"));
            editEmpresaTempo.setText(bundle.getString("tempo"));
            editEmpresaSlogan.setText(bundle.getString("slogan"));
            editEmpresaEstado.setText(bundle.getString("estado"));
        }

    }


    private void validarDadosEmpresa() {

        String nome = editEmpresaNome.getText().toString();
        String categoria = editEmpresaCategoria.getText().toString();
        String tempo = editEmpresaTempo.getText().toString();
        String slogan = editEmpresaSlogan.getText().toString();
        String estado = estados.getSelectedItem().toString().trim();

        if (!nome.isEmpty()) {
            if (!categoria.isEmpty()) {
                if (!tempo.isEmpty()) {
                    if (!slogan.isEmpty()) {
                        if (visible == true && termos.isChecked() || visible == false) {
                            Empresa empresa = new Empresa();

                            empresa.setIdUsuario(idUsuarioLogado);
                            empresa.setNome(nome);
                            empresa.setNome_Filtro(nome.toLowerCase());
                            empresa.setCategoria(categoria);
                            empresa.setTempo(tempo);
                            empresa.setSlogan(slogan);
                            if (urlImagem != null) {
                                empresa.setUrlImagem(urlImagem);
                            } else if (imagemRecuperada != null) {
                                empresa.setUrlImagem(imagemRecuperada);
                            } else {
                                empresa.setUrlImagem(null);
                            }
                            if (!estado.equals("Selecionar Estado")) {
                                empresa.setEstado(estado);
                            } else {
                                empresa.setEstado(estadoAtual);
                            }

                            empresa.salvar();
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                    "Cadastrado! Já pode fazer seu pedido!",
                                    Toast.LENGTH_SHORT).show();
                            abrirTelaEmpresa();
                        } else {
                            exibirMensagem("Aceite os termos de uso antes de continuar!");
                        }
                    } else {
                        exibirMensagem("Digite o slogan do Restaurante!");
                    }
                } else {
                    exibirMensagem("Digite o tempo estimado do Restaurante!");
                }
            } else {
                exibirMensagem("Digite a categoria do Restaurante!");
            }
        } else {
            exibirMensagem("Digite o nome do Restaurante!");
        }

    }

    private void abrirTelaEmpresa() {
        startActivity(new Intent(ConfiguracoesEmpresaActivity.this, SplashActivity.class));
    }

    private void exibirMensagem(String texto) {

        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                recuperarApartirDePoliticaPrivacidadeActivity(data);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;
            try {
                switch (requestCode) {
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        if (android.os.Build.VERSION.SDK_INT >= 29) {
                            ImageDecoder.Source imageDecoder = ImageDecoder.createSource(getContentResolver(), localImagem);
                            imagem = ImageDecoder.decodeBitmap(imageDecoder);
                            Log.i("SDK", ">= 29");
                        } else {
                            imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem);
                            Log.i("SDK", "< 29");
                        }
                        break;
                }

                if (imagem != null) {
                    salvar.setClickable(false);
                    salvar.setBackgroundColor(ContextCompat.getColor(this,
                            R.color.colorButtonCancel));

                    Toast.makeText(this, "Fazendo upload da imagem...",
                            Toast.LENGTH_SHORT)
                            .show();

                    imagePerfilEmpresa.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("empresas")
                            .child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                    "Erro ao fazer upload!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    urlImagem = url.toString();
                                }
                            });
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                    "Imagem adicionada com sucesso!",
                                    Toast.LENGTH_SHORT).show();
                            salvar.setClickable(true);
                            salvar.setBackgroundColor(ContextCompat.getColor(ConfiguracoesEmpresaActivity.this,
                                    R.color.colorAccent));
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void inicializarComponentes() {
        editEmpresaNome = findViewById(R.id.editEmpresaNome);
        editEmpresaCategoria = findViewById(R.id.editEmpresaCategoria);
        editEmpresaTempo = findViewById(R.id.editEmpresaTempo);
        editEmpresaSlogan = findViewById(R.id.editEmpresaSlogan);
        imagePerfilEmpresa = findViewById(R.id.imagePerfilEmpresa);
        salvar = findViewById(R.id.buttonAcesso2);
        deletar = findViewById(R.id.buttonDeletar);
        estados = findViewById(R.id.spinnerEstados);
        selecionarEstado = findViewById(R.id.textSelecionarEstado);
        editEmpresaEstado = findViewById(R.id.editEmpresaEstado);
        termos = findViewById(R.id.termosCheck);
        lerTermos = findViewById(R.id.textTermosEmp);

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
                Empresa empresaExcluir = new Empresa();
                empresaExcluir.setIdUsuario(idUsuarioLogado);
                empresaExcluir.excluirConta();
                usuario.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                "Conta excluída com sucesso!",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ConfiguracoesEmpresaActivity.this, SplashActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                "Falha em excluir a conta! Entre em sua conta novamente e tente de novo!" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(ConfiguracoesEmpresaActivity.this, AutenticacaoActivity.class));
                    }
                });
            }

        });
        JOP.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ConfiguracoesEmpresaActivity.this, "Cancelado!",
                        Toast.LENGTH_SHORT)
                        .show();
            }

        });

        JOP.create();
        JOP.show();

    }
}
