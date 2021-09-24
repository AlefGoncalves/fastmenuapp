package com.fatecsbc.cardapiodigital.model.dao;

import com.fatecsbc.cardapiodigital.model.dao.impl.EmpresaDaoFB;
import com.fatecsbc.cardapiodigital.model.dao.impl.ItemPedidoDaoFB;
import com.fatecsbc.cardapiodigital.model.dao.impl.PedidoDaoFB;
import com.fatecsbc.cardapiodigital.model.dao.impl.ProdutoDaoFB;
import com.fatecsbc.cardapiodigital.model.dao.impl.UsuarioDaoFB;

public class DaoFactory {

    public static EmpresaDao createEmpresaDao() {
        return new EmpresaDaoFB();
    }

    public static ItemPedidoDao createItemPedidoDao(){
        return new ItemPedidoDaoFB();
    }

    public static PedidoDao createPedidoDao(){
        return new PedidoDaoFB();
    }

    public static ProdutoDao createProdutoDao(){
        return new ProdutoDaoFB();
    }

    public static UsuarioDao createUsuarioDao(){
        return new UsuarioDaoFB();
    }
}
