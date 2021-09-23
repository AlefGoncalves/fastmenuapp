package com.fatecsbc.cardapiodigital.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fatecsbc.cardapiodigital.R;
import com.fatecsbc.cardapiodigital.model.ItemPedido;
import com.fatecsbc.cardapiodigital.model.Pedido;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;





public class AdapterPedido extends RecyclerView.Adapter<AdapterPedido.MyViewHolder> {

    private List<Pedido> pedidos;

    public AdapterPedido(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pedidos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        final Pedido pedido = pedidos.get(i);
        holder.nome.setText( pedido.getNome() );
        holder.numeroMesa.setText("Mesa nº: " + pedido.getNumeroMesa());
        holder.endereco.setText( "Status pedido: "+pedido.getStatus() );

        //recuperando metodo pagamento
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
        holder.pgto.setText("Forma de Pagto: " + pagamento);

        holder.observacao.setText( "Obs: "+ pedido.getObservacao() );

        List<ItemPedido> itens = new ArrayList<>();
        itens = pedido.getItens();
        String descricaoItens = "";
        DecimalFormat formato = new DecimalFormat("0.00");
        int numeroItem = 1;
        Double total = 0.00;
        for( ItemPedido itemPedido : itens ){

            int qtde = itemPedido.getQuantidade();
            Double preco = itemPedido.getPreco();
            total += (qtde * preco);

            String nome = itemPedido.getNomeProduto();
            descricaoItens += numeroItem + ") " + nome + " / (" + qtde + " x R$ "+ formato.format(preco) + ") \n";
            numeroItem++;
        }
        descricaoItens += "Total: R$ " + formato.format(total);
        holder.itens.setText(descricaoItens);

        holder.btConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedido.setStatus("confirmado");
                pedido.atualizarStatus();
                Toast.makeText(v.getContext(),
                        "Pedido confirmado com sucesso!",
                        Toast.LENGTH_LONG).show();
                pedido.removerPedidosUsuario();
            }
        });

        holder.btExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedido.remover();
                Toast.makeText(v.getContext(),
                        "Pedido excluido com sucesso!",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
            return pedidos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView numeroMesa;
        TextView endereco;
        TextView pgto;
        TextView observacao;
        TextView itens;
        Button btConfirmar, btExcluir;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome        = itemView.findViewById(R.id.textEmpresaNome);
            numeroMesa  = itemView.findViewById(R.id.textNmesa);
            endereco    = itemView.findViewById(R.id.textPedidoNumero);
            pgto        = itemView.findViewById(R.id.textPedidoPgto);
            observacao  = itemView.findViewById(R.id.textPedidoObs);
            itens       = itemView.findViewById(R.id.textPedidoItens);
            btConfirmar = itemView.findViewById(R.id.btConfirmar);
            btExcluir = itemView.findViewById(R.id.btExcluir);
        }
    }

}
