package com.fatecsbc.cardapiodigital.model.dao;

import com.fatecsbc.cardapiodigital.model.entities.ItemPedido;

import java.util.List;

public interface ItemPedidoDao {

    void insert(ItemPedido obj);
    void update(ItemPedido obj);
    void deleteById(String id);
    ItemPedido findById(String id);
    List<ItemPedido> findAll();
}
