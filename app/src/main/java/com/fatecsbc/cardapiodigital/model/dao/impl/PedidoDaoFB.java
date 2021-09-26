package com.fatecsbc.cardapiodigital.model.dao.impl;

import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.fatecsbc.cardapiodigital.model.dao.PedidoDao;
import com.fatecsbc.cardapiodigital.model.entities.Pedido;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;

public class PedidoDaoFB implements PedidoDao {

    public PedidoDaoFB(){

    }

    public PedidoDaoFB(Pedido obj) {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuario")
                .child( obj.getIdUsuario() )
                .child( obj.getIdEmpresa() );
        obj.setIdPedido( pedidoRef.push().getKey() );
    }

    @Override
    public void insert(Pedido obj) {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child( obj.getIdEmpresa() )
                .child( obj.getIdPedido() );
        pedidoRef.setValue(this);
    }

    @Override
    public void insertPedidoUsuario(Pedido obj) {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuario")
                .child( obj.getIdUsuario() )
                .child( obj.getIdEmpresa() );
        pedidoRef.setValue(this);
    }

    @Override
    public void insertPedidoHistorico(Pedido obj) {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("historico")
                .child( obj.getIdUsuario() )
                .child( obj.getIdPedido() );
        pedidoRef.setValue(this);
    }

    @Override
    public void update(Pedido obj) {

    }

    @Override
    public void updateStatus(Pedido obj) {
        HashMap<String, Object> status = new HashMap<>();
        status.put("status", obj.getStatus());

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child( obj.getIdEmpresa() )
                .child( obj.getIdPedido() );
        pedidoRef.updateChildren(status);

        DatabaseReference pedidoUsuarioRef = firebaseRef
                .child("pedidos_usuario")
                .child( obj.getIdUsuario() )
                .child( obj.getIdEmpresa() );
        pedidoUsuarioRef.updateChildren(status);

        DatabaseReference historicoRef = firebaseRef
                .child("historico")
                .child( obj.getIdUsuario() )
                .child( obj.getIdPedido() );
        historicoRef.updateChildren(status);
    }

    @Override
    public void delete(Pedido obj) {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoUsuarioRef = firebaseRef
                .child("pedidos_usuario")
                .child( obj.getIdUsuario() )
                .child( obj.getIdEmpresa() );
        pedidoUsuarioRef.removeValue();

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child( obj.getIdEmpresa() )
                .child( obj.getIdPedido() );
        pedidoRef.removeValue();
    }

    @Override
    public void deletePedidoUsuario(Pedido obj) {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoUsuarioRef = firebaseRef
                .child("pedidos_usuario")
                .child( obj.getIdUsuario() )
                .child( obj.getIdEmpresa() );
        pedidoUsuarioRef.removeValue();
    }

    @Override
    public void deletePedidoHistorico(Pedido obj) {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoUsuarioRef = firebaseRef
                .child("historico")
                .child( obj.getIdUsuario() )
                .child( obj.getIdPedido() );
        pedidoUsuarioRef.removeValue();
    }

    @Override
    public Pedido findById(String id) {
        return null;
    }

    @Override
    public List<Pedido> findAll() {
        return null;
    }
}
