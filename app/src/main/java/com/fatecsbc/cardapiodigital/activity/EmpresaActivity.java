package com.fatecsbc.cardapiodigital.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.fatecsbc.cardapiodigital.R;
import com.fatecsbc.cardapiodigital.adapter.AdapterProduto;
import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.fatecsbc.cardapiodigital.helper.UsuarioFirebase;
import com.fatecsbc.cardapiodigital.listener.RecyclerItemClickListener;
import com.fatecsbc.cardapiodigital.model.entities.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerProdutos;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private SmartTabLayout smartTabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        inicializarComponentes();

        // configurando toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cardápio");
        setSupportActionBar(toolbar);

        // configurando recyclerView
        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos,this);
        recyclerProdutos.setAdapter(adapterProduto);

        // Recupera produtos
        recuperarProdutos();

        // adicionar evento de clique no recyclerView
        recyclerProdutos.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerProdutos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(EmpresaActivity.this, "Mantenha pressionado para excluir",
                        Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onLongItemClick(View view, int position) {
                alertDialog(position);


            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

    }

    public void alertDialog(final int position){
        AlertDialog.Builder JOP = new AlertDialog.Builder(this);
        JOP.setTitle("Confirmação excluir produto");
        JOP.setMessage("Realmente deseja excluir o produto?");
        JOP.setIcon(getResources().getDrawable(R.drawable.ic_warning));
        JOP.setCancelable(false);

        JOP.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Produto produtoSelecionado = produtos.get(position);
                produtoSelecionado.remover();
                Toast.makeText(EmpresaActivity.this, "Produto Excluído com Sucesso!",
                        Toast.LENGTH_SHORT)
                        .show();
            }

        });
        JOP.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(EmpresaActivity.this, "Exclusão Cancelada!",
                        Toast.LENGTH_SHORT)
                        .show();
            }

        });

        JOP.create();
        JOP.show();


    }

    private void recuperarProdutos(){

        DatabaseReference produtosRef = firebaseRef.child("produtos").child(idUsuarioLogado);

        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                produtos.clear();
                if(snapshot.getValue() != null) {
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        produtos.add(ds.getValue(Produto.class));

                    }

                    adapterProduto.notifyDataSetChanged();
                }else{
                    setContentView(R.layout.img_empty);
                    // configurando toolbar
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    toolbar.setTitle("Cardápio");
                    setSupportActionBar(toolbar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void inicializarComponentes(){
        recyclerProdutos = findViewById(R.id.recyclerProdutos);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.menuSair:
                deslogarUsuario();
                break;
            case R.id.menuAjuda:
                abrirAjudaEmpresa();
                break;
            case R.id.menuExibirPedidos:
                abrirPedidosEmpresa();
                break;
            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;
            case R.id.menuNovoProduto:
                abrirNovoProduto();
                break;
            case R.id.menuSenha:
                abrirTrocaSenha();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try{
            autenticacao.signOut();
            Toast.makeText(this, "Deslogado com sucesso!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EmpresaActivity.this, SplashActivity.class));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void abrirTrocaSenha() {
        startActivity(new Intent(EmpresaActivity.this, RecuperasenhaActivity.class));
    }

    private void abrirPedidosEmpresa(){
        startActivity(new Intent(EmpresaActivity.this, PedidosActivity.class));

    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(EmpresaActivity.this, ConfiguracoesEmpresaActivity.class));

    }

    private void abrirNovoProduto(){
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));

    }

    private void abrirAjudaEmpresa(){
        startActivity(new Intent(EmpresaActivity.this, AjudaEmpresaActivity.class));

    }
}
