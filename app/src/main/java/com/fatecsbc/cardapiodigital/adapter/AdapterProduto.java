package com.fatecsbc.cardapiodigital.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fatecsbc.cardapiodigital.R;
import com.fatecsbc.cardapiodigital.model.Produto;
import com.squareup.picasso.Picasso;


import java.util.List;


public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder>{

    private List<Produto> produtos;
    private Context context;

    public AdapterProduto(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Produto produto = produtos.get(i);
        String urlImagem = produto.getUrlImagem();
        Picasso.get().load( urlImagem ).into( holder.imagem );

        holder.nome.setText(produto.getNome());
        holder.descricao.setText(produto.getDescricao());
        if(produto.getTempo().equals("0")){
            holder.tempo.setText("Instantâneo");
        }else{
            holder.tempo.setText(produto.getTempo() + " Minutos");
        }
        holder.valor.setText(String.format("R$ %.2f", produto.getPreco()));

    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView descricao;
        TextView valor;
        TextView tempo;
        ImageView imagem;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNomeRefeicao);
            descricao = itemView.findViewById(R.id.textDescricaoRefeicao);
            tempo = itemView.findViewById(R.id.textTempo);
            valor = itemView.findViewById(R.id.textPreco);
            imagem = itemView.findViewById(R.id.imageProduto);
        }
    }
}
