package com.fatecsbc.cardapiodigital.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fatecsbc.cardapiodigital.R;
import com.fatecsbc.cardapiodigital.adapter.AdapterHistorico;
import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.fatecsbc.cardapiodigital.helper.UsuarioFirebase;
import com.fatecsbc.cardapiodigital.model.entities.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class HistoricoActivity extends AppCompatActivity {

    private RecyclerView recyclerHistorico;
    private AdapterHistorico adapterHistorico;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        //configs iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuario = UsuarioFirebase.getIdUsuario();

        //configurando Toolbar e botão voltar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Histórico de Pedidos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //configurando recyclerView
        recyclerHistorico.setLayoutManager(new LinearLayoutManager(this));
        recyclerHistorico.setHasFixedSize(true);
        adapterHistorico = new AdapterHistorico(pedidos);
        recyclerHistorico.setAdapter(adapterHistorico);

        recuperarPedidos();
    }

    private void recuperarPedidos() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference pedidoRef = firebaseRef
                .child("historico")
                .child(idUsuario);

        //recupera dados com status "confirmado" - sem uso
        /*
        Query pedidoPesquisa = pedidoRef.orderByChild("status")
                .equalTo("confirmado");

         */


        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                pedidos.clear();
                if (snapshot.getValue() != null) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                    adapterHistorico.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Toast.makeText(HistoricoActivity.this,
                            "Não há pedidos confirmados!",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(HistoricoActivity.this, QRCodeActivity.class));
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void inicializarComponentes() {
        recyclerHistorico = findViewById(R.id.recyclerHistorico);
    }
}
