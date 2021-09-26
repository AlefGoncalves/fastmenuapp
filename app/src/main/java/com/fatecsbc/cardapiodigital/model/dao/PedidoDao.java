package com.fatecsbc.cardapiodigital.model.dao;

import com.fatecsbc.cardapiodigital.model.entities.Pedido;

import java.util.List;

public interface PedidoDao {

    void insert(Pedido obj);
    void insertPedidoUsuario(Pedido obj);
    void insertPedidoHistorico(Pedido obj);
    void update(Pedido obj);
    void updateStatus(Pedido obj);
    void delete(Pedido obj);
    void deletePedidoUsuario(Pedido obj);
    void deletePedidoHistorico(Pedido obj);
    Pedido findById(String id);
    List<Pedido> findAll();
}
