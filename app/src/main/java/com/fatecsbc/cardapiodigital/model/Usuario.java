package com.fatecsbc.cardapiodigital.model;

import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Usuario {

    private String idUsuario;
    private String nome;
    private String email;
    private String estado;
    private String dataNascimento;
    private String cpf;
    private String telefone;

    public Usuario() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference UsuarioRef = firebaseRef.child("usuarios")
                .child( getIdUsuario() );
        UsuarioRef.setValue(this);
    }

    public void excluirConta(){
        // exclui os pedidos em andamento do usuario.
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef
                .child("pedidos_usuario")
                .child( getIdUsuario() );
        usuarioRef.removeValue();

        // exclui os produtos cadastrados pela conta
        DatabaseReference usuarioContaRef = firebaseRef.child("usuarios")
                .child(getIdUsuario());
        usuarioContaRef.removeValue();
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
