package com.fatecsbc.cardapiodigital.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fatecsbc.cardapiodigital.R;
import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.fatecsbc.cardapiodigital.helper.UsuarioFirebase;
import com.fatecsbc.cardapiodigital.model.entities.Produto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editProdutoNome,editProdutoDescricao,editProdutoTempo,editProdutoPreco;
    private Button salvar;
    private String urlImagem;
    private ImageView imageProduto;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        inicializarComponentes();
        // configurações firebase
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        imageProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDadosProduto();
                Toast.makeText(NovoProdutoEmpresaActivity.this,
                        "Dados Salvos com Sucesso!",
                        Toast.LENGTH_SHORT).show();
                abrirTelaEmpresa();
            }
        });
    }

    private void inicializarComponentes(){
        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
        editProdutoTempo = findViewById(R.id.editProdutoTempo);
        editProdutoPreco = findViewById(R.id.editProdutoPreco);
        imageProduto = findViewById(R.id.imageProduto);
        salvar = findViewById(R.id.buttonSalvar);
    }

    private void abrirTelaEmpresa(){
        startActivity(new Intent(NovoProdutoEmpresaActivity.this, EmpresaActivity.class));
        finish();
    }

    private void validarDadosProduto(){

        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String tempo = editProdutoTempo.getText().toString();
        String preco = editProdutoPreco.getText().toString();

        if(!nome.isEmpty()){
            if(!descricao.isEmpty()){
                if(!tempo.isEmpty()){
                    if(!preco.isEmpty()){

                        Produto produto = new Produto();
                        produto.setIdUsuario(idUsuarioLogado);
                        produto.setNome(nome);
                        produto.setDescricao(descricao);
                        produto.setPreco( Double.parseDouble (preco) );
                        produto.setTempo(tempo);
                        produto.setUrlImagem(urlImagem);
                        produto.salvar();

                        exibirMensagem("Produto Salvo com Sucesso!");



                    }else{
                        exibirMensagem("Digite o preço do produto!");
                    }
                }else{
                    exibirMensagem("Digite o tempo de preparo do produto!");
                }
            }else{
                exibirMensagem("Digite a descrição!");
            }
        }else{
            exibirMensagem("Digite o nome do Produto!");
        }

    }

    private void exibirMensagem(String texto){

        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try{
                switch(requestCode){
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

                if( imagem != null ){
                    salvar.setClickable(false);
                    final int min = 0;
                    final int max = 10000000;
                    final int random = new Random().nextInt((max - min) + 1) + min;
                    imageProduto.setImageBitmap( imagem );

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,10, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("produtos")
                            .child(idUsuarioLogado).child("produto"+random);


                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NovoProdutoEmpresaActivity.this,
                                    "Erro ao fazer upload!Tente novamente",
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
                            Toast.makeText(NovoProdutoEmpresaActivity.this,
                                    "Imagem adicionada com sucesso!",
                                    Toast.LENGTH_SHORT).show();

                            salvar.setClickable(true);
                            salvar.setBackgroundColor(ContextCompat.getColor(NovoProdutoEmpresaActivity.this,
                                    R.color.colorAccent));
                        }
                    });

                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


}
