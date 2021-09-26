package com.fatecsbc.cardapiodigital.model.dao.impl;

import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.fatecsbc.cardapiodigital.model.dao.ProdutoDao;
import com.fatecsbc.cardapiodigital.model.entities.Produto;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ProdutoDaoFB implements ProdutoDao {

    public ProdutoDaoFB(){

    }

    public ProdutoDaoFB(Produto obj){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos");
        obj.setIdProduto( produtoRef.push().getKey() );
    }

    @Override
    public void insert(Produto obj) {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos")
                .child( obj.getIdUsuario() )
                .child( obj.getIdProduto() );
        produtoRef.setValue(this);
    }

    @Override
    public void update(Produto obj) {

    }

    @Override
    public void deleteById(Produto obj) {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos")
                .child( obj.getIdUsuario() )
                .child( obj.getIdProduto() );
        produtoRef.removeValue();
    }

    @Override
    public Produto findById(String id) {
        return null;
    }

    @Override
    public List<Produto> findAll() {
        return null;
    }
}
