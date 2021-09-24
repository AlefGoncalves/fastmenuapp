package com.fatecsbc.cardapiodigital.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fatecsbc.cardapiodigital.R;
import com.fatecsbc.cardapiodigital.adapter.AdapterEmpresa;
import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.fatecsbc.cardapiodigital.helper.UsuarioFirebase;
import com.fatecsbc.cardapiodigital.model.entities.Empresa;
import com.fatecsbc.cardapiodigital.model.entities.Pedido;
import com.fatecsbc.cardapiodigital.model.entities.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class QRCodeActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private Button btnScan;
    private List<Empresa> empresas = new ArrayList<>();
    private Empresa empresa;
    private Pedido pedido = new Pedido();
    private DatabaseReference firebaseRef;
    private AdapterEmpresa adapterEmpresa;
    private Dialog dialog;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        adapterEmpresa = new AdapterEmpresa(empresas);
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        // configurando toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        recuperarDadosUsuario(toolbar);
        setSupportActionBar(toolbar);

        //recupera empresas
        recuperarEmpresas();

        btnScan = (Button) findViewById(R.id.btnScan);
        final Activity activity = this;

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(toolbar.getTitle().equals("Fast Menu"))) {
                    IntentIntegrator integrator = new IntentIntegrator(activity);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    integrator.setPrompt("QR Code Scanner");
                    integrator.setCameraId(0);
                    integrator.initiateScan();
                    integrator.setOrientationLocked(true);
                }else{
                    Toast.makeText(QRCodeActivity.this,
                            "Precisa confirmar seu email e cadastrar seus dados antes de fazer o pedido!",
                            Toast.LENGTH_SHORT).show();
                    abrirConfiguracao();
                }
            }
        });

    }

    private void abrirConfiguracao(){
        startActivity(new Intent(QRCodeActivity.this, ConfiguracoesUsuarioActivity.class));
    }

    private void recuperarEmpresas() {
        DatabaseReference empresaRef = firebaseRef.child("empresas");
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                empresas.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

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

    private void recuperarDadosUsuario(final Toolbar toolbar) {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuarioLogado);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    toolbar.setTitle(usuario.getNome());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.dismiss();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuSair:
                deslogarUsuario();
                finish();
                break;
            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;
            case R.id.menuPedidos:
                abrirPedidos();
                break;
            case R.id.menuHistorico:
                abrirHistorico();
                break;
            case R.id.menuSenha:
                //deslogarUsuario();
                abrirTrocaSenha();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario() {
        try {
            autenticacao.signOut();
            startActivity(new Intent(QRCodeActivity.this, SplashActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirTrocaSenha() {
        startActivity(new Intent(QRCodeActivity.this, RecuperasenhaActivity.class));
    }

    private void abrirPedidos() {
        startActivity(new Intent(QRCodeActivity.this, CarrinhoActivity.class));

    }

    private void abrirConfiguracoes() {
        startActivity(new Intent(QRCodeActivity.this, ConfiguracoesUsuarioActivity.class));

    }

    private void abrirHistorico() {
        startActivity(new Intent(QRCodeActivity.this, HistoricoActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                try {
                    recuperarEmpresa(result.getContents());
                    /* metodo antigo para listar restaurantes

                    int pos = Integer.parseInt(result.getContents());
                    //alert(result.getContents());
                    listarRestaurante(pos);

                     */

                } catch (Exception e) {
                    alert("Este Restaurante não Está Cadastrado no Sistema");
                }
            } else {
                alert("Scan cancelado");
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void alert(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void listarRestaurante(int position) {
        Empresa empresaSelecionada = empresas.get(position);
        Intent i = new Intent(QRCodeActivity.this, CardapioActivity.class);
        i.putExtra("empresa", empresaSelecionada);
        startActivity(i);
    }

    private void recuperarEmpresa(String idEmpresa) {

        String[] idSeparado = {"", ""};
        int numeroMesa = 0;

        try {
            idSeparado = idEmpresa.split("-");
            numeroMesa = Integer.parseInt(idSeparado[1]);
        } catch (Exception e) {

        }

        pedido.setNumeroMesa(numeroMesa);

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference empresaRef = firebaseRef
                .child("empresas")
                .child(idSeparado[0]);

        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    empresa = snapshot.getValue(Empresa.class);
                    Intent i = new Intent(QRCodeActivity.this, CardapioActivity.class);
                    i.putExtra("empresa", empresa);
                    i.putExtra("pedido", pedido);
                    startActivity(i);
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    alert("Este Restaurante não Está Cadastrado no Sistema");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
