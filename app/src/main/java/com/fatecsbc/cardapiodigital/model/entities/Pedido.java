package com.fatecsbc.cardapiodigital.model.entities;

import com.fatecsbc.cardapiodigital.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Pedido implements Serializable {

    private String idUsuario;
    private String idEmpresa;
    private String idPedido;
    private String nome;
    private String status= "pendente";
    private String cpf;
    private String telefone;
    private List<ItemPedido> itens;
    private Double total;
    private String observacao="";
    private int metodoPagamento;
    private int numeroMesa;

    public Pedido() {
    }

    public Pedido(String idUsuario, String idEmpresa) {

        setIdUsuario(idUsuario);
        setIdEmpresa(idEmpresa);

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuario")
                .child( idUsuario )
                .child( idEmpresa );
        setIdPedido( pedidoRef.push().getKey() );
    }


    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child( getIdEmpresa() )
                .child( getIdPedido() );
        pedidoRef.setValue(this);

    }

    public void atualizarStatus(){

        HashMap<String, Object> status = new HashMap<>();
        status.put("status", getStatus());

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child( getIdEmpresa() )
                .child( getIdPedido() );
        pedidoRef.updateChildren(status);

        DatabaseReference pedidoUsuarioRef = firebaseRef
                .child("pedidos_usuario")
                .child( getIdUsuario() )
                .child( getIdEmpresa() );
        pedidoUsuarioRef.updateChildren(status);

        DatabaseReference historicoRef = firebaseRef
                .child("historico")
                .child( getIdUsuario() )
                .child( getIdPedido() );
        historicoRef.updateChildren(status);

    }

    public void removerPedidosUsuario(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoUsuarioRef = firebaseRef
                .child("pedidos_usuario")
                .child( getIdUsuario() )
                .child( getIdEmpresa() );
        pedidoUsuarioRef.removeValue();

    }

    public void removerItemHistorico(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoUsuarioRef = firebaseRef
                .child("historico")
                .child( getIdUsuario() )
                .child( getIdPedido() );
        pedidoUsuarioRef.removeValue();
    }

    public void remover(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoUsuarioRef = firebaseRef
                .child("pedidos_usuario")
                .child( getIdUsuario() )
                .child( getIdEmpresa() );
        pedidoUsuarioRef.removeValue();

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child( getIdEmpresa() )
                .child( getIdPedido() );
        pedidoRef.removeValue();

    }

    public void confirmar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuario")
                .child( getIdUsuario() )
                .child( getIdEmpresa() );
        pedidoRef.setValue(this);

    }

    public void confirmarHistorico(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("historico")
                .child( getIdUsuario() )
                .child( getIdPedido() );
        pedidoRef.setValue(this);

    }


    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idRequisicao) {
        this.idPedido = idRequisicao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(int numeroMesa) {
        this.numeroMesa = numeroMesa;
    }
}
