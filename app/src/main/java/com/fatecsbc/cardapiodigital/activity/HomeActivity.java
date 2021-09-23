package com.fatecsbc.cardapiodigital.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fatecsbc.cardapiodigital.R;
import com.fatecsbc.cardapiodigital.adapter.AdapterEmpresa;
import com.fatecsbc.cardapiodigital.adapter.AdapterProduto;
import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.fatecsbc.cardapiodigital.helper.UsuarioFirebase;
import com.fatecsbc.cardapiodigital.listener.RecyclerItemClickListener;
import com.fatecsbc.cardapiodigital.model.Empresa;
import com.fatecsbc.cardapiodigital.model.Produto;
import com.fatecsbc.cardapiodigital.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private MaterialSearchView materialSearchView;
    private RecyclerView recyclerEmpresa;
    private List<Empresa> empresas = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterEmpresa adapterEmpresa;
    private FirebaseUser usuarioLogado;
    private int cont;

/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuarioLogado = UsuarioFirebase.getUsuarioAtual();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();


        // configurando toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Encontrar Restaurantes");
        setSupportActionBar(toolbar);

        // configurando recyclerView
        recyclerEmpresa.setLayoutManager(new LinearLayoutManager(this));
        recyclerEmpresa.setHasFixedSize(true);
        adapterEmpresa = new AdapterEmpresa(empresas);
        recyclerEmpresa.setAdapter(adapterEmpresa);


        // Recupera empresas e usuario
        recuperarEmpresas();

        // configurando searchView

        materialSearchView.setHint("Pesquisar Restaurantes");
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarEmpresas( newText );


                return true;
            }
        });

        // configura exibição cardápio
        recyclerEmpresa.addOnItemTouchListener(new RecyclerItemClickListener
                (this,
                        recyclerEmpresa,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                    if(!usuarioLogado.isEmailVerified()){

                                        Toast.makeText(HomeActivity.this,
                                                "Complete seu cadastro para continuar!",
                                                Toast.LENGTH_SHORT).show();
                                        String email = usuarioLogado.getEmail();
                                        Intent i = new Intent(HomeActivity.this,ConfiguracoesUsuarioActivity.class);
                                        i.putExtra("email",email);
                                        startActivity(i);
                                    } else{
                                Empresa empresaSelecionada = empresas.get(position);

                                Intent i = new Intent(HomeActivity.this,CardapioActivity.class);
                                i.putExtra("empresa",empresaSelecionada);
                                startActivity(i);
                                    }

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                ));


    }


    @Override
    protected void onRestart() {
        super.onRestart();
        usuarioLogado.reload();
    }

    private void pesquisarEmpresas(String pesquisa){

        DatabaseReference empresasRef = firebaseRef
        .child("empresas");
        Query query = empresasRef.orderByChild("nome_Filtro")
                .startAt(pesquisa)
                .endAt(pesquisa + "\uf8ff" );

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                empresas.clear();

                for(DataSnapshot ds: snapshot.getChildren()){
                   empresas.add(ds.getValue(Empresa.class));

                }

                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                empresas.clear();
                recuperarEmpresas();
            }
        });

    }

    private void recuperarEmpresas(){

        DatabaseReference empresaRef = firebaseRef.child("empresas");
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresas.clear();

                for(DataSnapshot ds: snapshot.getChildren()){

                    Empresa empresa1 = ds.getValue(Empresa.class);

                        empresas.add(empresa1);


                }

                adapterEmpresa.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);

        //configura menu pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        materialSearchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.menuSair:
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void inicializarComponentes(){

        materialSearchView = findViewById(R.id.materialSearchView);
        recyclerEmpresa = findViewById(R.id.recyclerEmpresa);
    }

    private void deslogarUsuario(){
        try{
            autenticacao.signOut();
            Toast.makeText(this, "Deslogado com sucesso!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, SplashActivity.class));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(HomeActivity.this, ConfiguracoesUsuarioActivity.class));

    }

    @Override
    public void onBackPressed()
    {
        if(materialSearchView.isSearchOpen())
        {
            materialSearchView.closeSearch();

        }

        // atualiza o contador apenas se o materialSearch view estiver fechado
        else if(cont >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            cont = 0;
        }
        else
        {
            Toast.makeText(this, "Aperte o botão voltar novamente para sair.", Toast.LENGTH_SHORT).show();
            cont++;
        }
    } */
}
