package com.fatecsbc.cardapiodigital.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fatecsbc.cardapiodigital.R;
import com.fatecsbc.cardapiodigital.adapter.AdapterProduto;
import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.fatecsbc.cardapiodigital.helper.UsuarioFirebase;
import com.fatecsbc.cardapiodigital.listener.RecyclerItemClickListener;
import com.fatecsbc.cardapiodigital.model.entities.Empresa;
import com.fatecsbc.cardapiodigital.model.entities.ItemPedido;
import com.fatecsbc.cardapiodigital.model.entities.Pedido;
import com.fatecsbc.cardapiodigital.model.entities.Produto;
import com.fatecsbc.cardapiodigital.model.entities.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CardapioActivity extends AppCompatActivity {

    private String nomeRestaurante;
    private RecyclerView recyclerProdutosCardapio;
    private MaterialSearchView searchView;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresaCardapio, textCarrinhoQtde, textCarrinhoTotal, textVerCarrinho;
    private Empresa empresaSelecionada;
    private String idEmpresaSelecionada;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AlertDialog dialog;
    private String idUsuarioLogado, observacao;
    private Usuario usuario;
    private Pedido pedidoRecuperado, pedidoMesa;
    private int qtdeItensCarrinho;
    private Double totalCarrinho;
    private int metodoPagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        // configurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String url = "";
            empresaSelecionada = (Empresa) bundle.getSerializable("empresa");
            nomeRestaurante = empresaSelecionada.getNome();
            textNomeEmpresaCardapio.setText(empresaSelecionada.getNome());
            if (empresaSelecionada.getUrlImagem() != null) {
                url = empresaSelecionada.getUrlImagem();
            }
            if (!url.equals("")) {
                Picasso.get().load(url).into(imageEmpresaCardapio);
            }

        }

        // configurando toolbar com botão voltar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(nomeRestaurante);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // configurando recyclerView

        recyclerProdutosCardapio.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutosCardapio.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutosCardapio.setAdapter(adapterProduto);


        // evento de clique recycler

        recyclerProdutosCardapio.addOnItemTouchListener(new RecyclerItemClickListener(this
                , recyclerProdutosCardapio, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                confirmarQuantidade(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        //evento de clique no "Ver Carrinho"
        textVerCarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarPedido();
            }
        });

        // Recupera produtos
        recuperarProdutos();

        // recuperar dados usuário
        recuperarDadosUsuario();

        //Configurar search view
        searchView.setHint("Pesquisar pratos");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pesquisarPratos(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    recuperarProdutos();
                } else {
                    pesquisarPratos(newText);
                }
                return true;
            }
        });
    }

    private void pesquisarPratos(String pesquisa) {
        DatabaseReference pratosRef = firebaseRef
                .child("produtos")
                .child(idEmpresaSelecionada);
        Query query = pratosRef.orderByChild("nome")
                .startAt(pesquisa)
                .endAt(pesquisa + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                produtos.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    produtos.add(ds.getValue(Produto.class));

                }

                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void confirmarQuantidade(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adicionar ao carrinho");
        builder.setMessage("Digite a quantidade");

        final EditText editQuantidade = new EditText(this);
        editQuantidade.setText("1");
        editQuantidade.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setView(editQuantidade);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String quantidade = editQuantidade.getText().toString();

                if (quantidade.equals("0")) {
                    Toast.makeText(CardapioActivity.this,
                            "Quantidade Inválida!",
                            Toast.LENGTH_SHORT).show();
                } else {

                    Produto produtoSelecionado = produtos.get(position);


                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setIdProduto(produtoSelecionado.getIdProduto());
                    itemPedido.setNomeProduto(produtoSelecionado.getNome());
                    try {
                        itemPedido.setQuantidade(Integer.parseInt(quantidade));
                    } catch (Exception e) {
                        Toast.makeText(CardapioActivity.this,
                                "Quantidade Inválida!",
                                Toast.LENGTH_SHORT).show();
                    }
                    itemPedido.setPreco(produtoSelecionado.getPreco());

                    itensCarrinho.add(itemPedido);

                    if (pedidoRecuperado == null) {
                        pedidoRecuperado = new Pedido(idUsuarioLogado, idEmpresaSelecionada);
                    }

                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null) {
                        pedidoMesa = (Pedido) bundle.getSerializable("pedido");
                    }

                    pedidoRecuperado.setNome(usuario.getNome());
                    pedidoRecuperado.setCpf(usuario.getCpf());
                    pedidoRecuperado.setTelefone(usuario.getTelefone());
                    pedidoRecuperado.setItens(itensCarrinho);
                    pedidoRecuperado.setNumeroMesa(pedidoMesa.getNumeroMesa());

                    pedidoRecuperado.confirmar();
                    pedidoRecuperado.confirmarHistorico();

                    Toast.makeText(CardapioActivity.this,
                            itemPedido.getQuantidade() + " " + itemPedido.getNomeProduto() + " adicionado ao carrinho!",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void recuperarDadosUsuario() {

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
                    usuario = snapshot.getValue(Usuario.class);
                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void recuperarPedido() {

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuario")
                .child(idUsuarioLogado)
                .child(idEmpresaSelecionada);

        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                qtdeItensCarrinho = 0;
                totalCarrinho = 0.0;
                itensCarrinho = new ArrayList<>();

                if (snapshot.getValue() != null) {

                    pedidoRecuperado = snapshot.getValue(Pedido.class);
                    itensCarrinho = pedidoRecuperado.getItens();


                    for (ItemPedido itemPedido : itensCarrinho) {

                        int qtde = itemPedido.getQuantidade();
                        Double preco = itemPedido.getPreco();

                        totalCarrinho += (qtde * preco);
                        qtdeItensCarrinho += qtde;
                    }
                }

                DecimalFormat df = new DecimalFormat("0.00");

                textCarrinhoQtde.setText("qtde:" + String.valueOf(qtdeItensCarrinho));
                textCarrinhoTotal.setText("R$:" + df.format(totalCarrinho));
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void recuperarProdutos() {
        idEmpresaSelecionada = empresaSelecionada.getIdUsuario();

        DatabaseReference produtosRef = firebaseRef.child("produtos").child(idEmpresaSelecionada);

        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                produtos.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    produtos.add(ds.getValue(Produto.class));

                }

                adapterProduto.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cardapio, menu);

        //Configurar botao pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuCadastro:
                confirmarPedido();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void confirmarPedido() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Método de pagamento");

        CharSequence[] itens = new CharSequence[]{
                "Dinheiro", "Cartão de Crédito", "Cartão de Débito"
        };
        builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                metodoPagamento = which;

            }
        });

        final EditText editObservacao = new EditText(this);
        editObservacao.setHint("Observações sobre o pedido?");

        builder.setView(editObservacao);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                observacao = editObservacao.getText().toString();

                if (pedidoRecuperado != null) {
                    pedidoRecuperado.setMetodoPagamento(metodoPagamento);
                    pedidoRecuperado.setObservacao(observacao);
                    pedidoRecuperado.setStatus("pendente");
                    pedidoRecuperado.salvar();
                    pedidoRecuperado.confirmar();
                    pedidoRecuperado.confirmarHistorico();
                    pedidoRecuperado = null;
                    Toast.makeText(CardapioActivity.this,
                            "Quase lá! Confirme o pedido no carrinho",
                            Toast.LENGTH_LONG).show();
                    abrirCarrinho();
                } else {
                    Toast.makeText(CardapioActivity.this,
                            "Adicione produtos ao carrinho para fazer um pedido!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void abrirCarrinho() {
        startActivity(new Intent(CardapioActivity.this, CarrinhoActivity.class));
        finish();
    }

    private void abrirConfiguracao() {
        startActivity(new Intent(CardapioActivity.this, ConfiguracoesUsuarioActivity.class));
        finish();
    }


    private void inicializarComponentes() {

        recyclerProdutosCardapio = findViewById(R.id.recyclerProdutosCardapio);
        textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);
        imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
        textCarrinhoQtde = findViewById(R.id.textCarrinhoQtd);
        textCarrinhoTotal = findViewById(R.id.textCarrinhoTotal);
        searchView = findViewById(R.id.materialSearchView);
        textVerCarrinho = findViewById(R.id.textVerCarrinho);


    }
}
