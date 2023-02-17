package com.venda.domain.service;

import com.venda.domain.entity.Pedido;
import com.venda.domain.enums.StatusPedido;
import com.venda.rest.dto.PedidoDTO;

import java.util.Optional;

public interface PedidoService {

    Pedido salvar(PedidoDTO dto );

    Optional<Pedido> obterPedidoCompleto(Integer id);
    void atualizaStatus(Integer id, StatusPedido statusPedido);

}
