package com.fatecsbc.cardapiodigital.model.dao;

import com.fatecsbc.cardapiodigital.model.entities.Produto;

import java.util.List;

public interface ProdutoDao {

    void insert(Produto obj);
    void update(Produto obj);
    void deleteById(Produto obj);
    Produto findById(String id);
    List<Produto> findAll();
}
