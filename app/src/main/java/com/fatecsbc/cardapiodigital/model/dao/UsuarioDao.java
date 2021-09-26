package com.fatecsbc.cardapiodigital.model.dao;

import com.fatecsbc.cardapiodigital.model.entities.Usuario;

import java.util.List;

public interface UsuarioDao {

    void insert(Usuario obj);
    void update(Usuario obj);
    void delete(Usuario obj);
    Usuario findById(String id);
    List<Usuario> findAll();
}
