package com.fatecsbc.cardapiodigital.model.dao;

import com.fatecsbc.cardapiodigital.model.entities.ItemPedido;

import java.util.List;

public interface ItemPedidoDao {

    void insert(ItemPedido obj);
    void update(ItemPedido obj);
    void deleteById(Integer id);
    ItemPedido findById(Integer id);
    List<ItemPedido> findAll();
}
