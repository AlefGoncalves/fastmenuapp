package com.fatecsbc.cardapiodigital.model.dao;

import com.fatecsbc.cardapiodigital.model.entities.Empresa;

import java.util.List;

public interface EmpresaDao {

    void insert(Empresa obj);
    void update(Empresa obj);
    void deleteById(String id);
    Empresa findById(String id);
    List<Empresa> findAll();
}
