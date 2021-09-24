package com.fatecsbc.cardapiodigital.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.fatecsbc.cardapiodigital.R;
import com.fatecsbc.cardapiodigital.adapter.AdapterCarrinho;
import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.fatecsbc.cardapiodigital.helper.UsuarioFirebase;
import com.fatecsbc.cardapiodigital.model.entities.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CarrinhoActivity extends AppCompatActivity {

    private RecyclerView recyclerCarrinho;
    private AdapterCarrinho adapterCarrinho;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        //configs iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuario = UsuarioFirebase.getIdUsuario();

        //configurando Toolbar e botão voltar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Itens do Carrinho");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //configurando recyclerView
        recyclerCarrinho.setLayoutManager(new LinearLayoutManager(this));
        recyclerCarrinho.setHasFixedSize(true);
        adapterCarrinho = new AdapterCarrinho(pedidos);
        recyclerCarrinho.setAdapter(adapterCarrinho);

        RecyclerView.ViewHolder btConfirmar = recyclerCarrinho.findViewHolderForItemId(R.id.btConfirmar);

        recuperarPedidos();

        /*
        //adiciona evento de click no recyclerview
        recyclerCarrinho.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerCarrinho,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                final Pedido pedido = pedidos.get(position);

                                AlertDialog.Builder builder = new AlertDialog.Builder(CarrinhoActivity.this);
                                builder.setTitle("Deseja confirmar o pedido?");

                                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pedido.setStatus("preparando");
                                        pedido.atualizarStatus();
                                        Toast.makeText(CarrinhoActivity.this,
                                                "Pedido Confirmado! Aguarde.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });

                                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });


                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                final Pedido pedido = pedidos.get(position);

                                AlertDialog.Builder builder = new AlertDialog.Builder(CarrinhoActivity.this);
                                builder.setTitle("Tem certeza que deseja cancelar o pedido?");

                                builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pedido.remover();
                                        Toast.makeText(CarrinhoActivity.this,
                                                "Pedido removido com sucesso!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                                builder.setNegativeButton("Manter pedido", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );*/
    }

    private void recuperarPedidos() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuario")
                .child(idUsuario);

        Query pedidoPesquisa = pedidoRef.orderByChild("status")
                .equalTo("pendente");


        pedidoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                pedidos.clear();
                if (snapshot.getValue() != null) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                    adapterCarrinho.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Toast.makeText(CarrinhoActivity.this,
                            "Não há mais pedidos no sistema!",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CarrinhoActivity.this, QRCodeActivity.class));
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void inicializarComponentes() {
        recyclerCarrinho = findViewById(R.id.recyclerHistorico);
    }
}
