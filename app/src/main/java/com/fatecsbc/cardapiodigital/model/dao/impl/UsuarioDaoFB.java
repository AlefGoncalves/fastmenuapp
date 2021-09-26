package com.fatecsbc.cardapiodigital.model.dao.impl;

import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.fatecsbc.cardapiodigital.model.dao.UsuarioDao;
import com.fatecsbc.cardapiodigital.model.entities.Usuario;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class UsuarioDaoFB implements UsuarioDao {

    @Override
    public void insert(Usuario obj) {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference UsuarioRef = firebaseRef.child("usuarios")
                .child( obj.getIdUsuario() );
        UsuarioRef.setValue(this);
    }

    @Override
    public void update(Usuario obj) {

    }

    @Override
    public void delete(Usuario obj) {
        // exclui os pedidos em andamento do usuario.
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef
                .child("pedidos_usuario")
                .child( obj.getIdUsuario() );
        usuarioRef.removeValue();

        // exclui os produtos cadastrados pela conta
        DatabaseReference usuarioContaRef = firebaseRef.child("usuarios")
                .child(obj.getIdUsuario());
        usuarioContaRef.removeValue();
    }

    @Override
    public Usuario findById(String id) {
        return null;
    }

    @Override
    public List<Usuario> findAll() {
        return null;
    }
}
