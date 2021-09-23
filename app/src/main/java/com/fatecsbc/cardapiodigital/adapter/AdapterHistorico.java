package com.fatecsbc.cardapiodigital.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fatecsbc.cardapiodigital.R;
import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.fatecsbc.cardapiodigital.model.Empresa;
import com.fatecsbc.cardapiodigital.model.ItemPedido;
import com.fatecsbc.cardapiodigital.model.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class AdapterHistorico extends RecyclerView.Adapter<AdapterHistorico.MyViewHolder> {

    private List<Pedido> pedidos;
    private DatabaseReference firebaseRef;
    private String nomeEmpresa;

    public AdapterHistorico(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_historico, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        final Pedido pedido = pedidos.get(i);

        //recupera nome da Empresa
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        recuperaEmpresa(pedido,holder);
        holder.pedidoNumero.setText("ID Pedido: " + pedido.getIdPedido());
        if (pedido.getObservacao().equals("")) {
            holder.observacao.setText("Não adicionou observações");
        } else {
            holder.observacao.setText("Obs: " + pedido.getObservacao());
        }
        holder.status.setText("Status do pedido: " + pedido.getStatus());

        List<ItemPedido> itens = new ArrayList<>();
        itens = pedido.getItens();
        String descricaoItens = "";
        DecimalFormat formato = new DecimalFormat("0.00");
        int numeroItem = 1;
        Double total = 0.00;
        for (ItemPedido itemPedido : itens) {

            int qtde = itemPedido.getQuantidade();
            Double preco = itemPedido.getPreco();
            total += (qtde * preco);


            formato.format(total);
            formato.format(preco);

            String nome = itemPedido.getNomeProduto();
            descricaoItens += numeroItem + ") " + nome + " (" + qtde + " x R$ " + formato.format(preco) + ") \n";
            numeroItem++;
        }

        descricaoItens += "Total: R$ " + formato.format(total);
        holder.itens.setText(descricaoItens);

        int metodoPagamento = pedido.getMetodoPagamento();
        String pagamento = " ";
        switch (metodoPagamento) {
            case 0:
                pagamento = "Dinheiro";
                break;
            case 1:
                pagamento = "Cartão de Crédito";
                break;
            case 2:
                pagamento = "Cartão de Débito";
                break;
        }
        holder.pgto.setText("pgto: " + pagamento);

    }

    public void recuperaEmpresa(Pedido pedido, final MyViewHolder holder){
        String idEmpresa = pedido.getIdEmpresa();

        DatabaseReference empresaRef = firebaseRef
                .child("empresas")
                .child(idEmpresa);
        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {

                    Empresa empresa = snapshot.getValue(Empresa.class);
                    holder.empresaNome.setText(empresa.getNome());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView empresaNome;
        TextView pedidoNumero;
        TextView status;
        TextView pgto;
        TextView observacao;
        TextView itens;

        public MyViewHolder(View itemView) {
            super(itemView);

            empresaNome = itemView.findViewById(R.id.textEmpresaNome);
            pedidoNumero = itemView.findViewById(R.id.textPedidoNumero);
            status = itemView.findViewById(R.id.textStatus);
            pgto = itemView.findViewById(R.id.textPedidoPgto);
            observacao = itemView.findViewById(R.id.textPedidoObs);
            itens = itemView.findViewById(R.id.textPedidoItens);
        }
    }

}
