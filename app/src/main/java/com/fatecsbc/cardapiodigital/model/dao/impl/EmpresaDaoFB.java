package com.fatecsbc.cardapiodigital.model.dao.impl;

import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.fatecsbc.cardapiodigital.model.dao.EmpresaDao;
import com.fatecsbc.cardapiodigital.model.entities.Empresa;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class EmpresaDaoFB implements EmpresaDao {

    @Override
    public void insert(Empresa obj) {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef.child("empresas")
                .child( obj.getIdUsuario() );
        empresaRef.setValue(this);
    }

    @Override
    public void update(Empresa obj) {

    }

    @Override
    public void deleteById(String id) {
        // exclui as informações da empresa.
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef
                .child("produtos")
                .child( id );
        empresaRef.removeValue();

        // exclui os produtos cadastrados pela conta
        DatabaseReference empresaProdutosRef = firebaseRef.child("empresas")
                .child(id);
        empresaProdutosRef.removeValue();
    }

    @Override
    public Empresa findById(String id) {
        return null;
    }

    @Override
    public List<Empresa> findAll() {
        return null;
    }
}
