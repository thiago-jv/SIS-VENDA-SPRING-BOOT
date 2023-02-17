package com.venda.domain.service;

import com.venda.domain.entity.Cliente;
import com.venda.domain.entity.ItemPedido;
import com.venda.domain.entity.Pedido;
import com.venda.domain.entity.Produto;
import com.venda.domain.enums.StatusPedido;
import com.venda.domain.repository.ClienteRespository;
import com.venda.domain.repository.ItensPedidoRepository;
import com.venda.domain.repository.PedidoRepository;
import com.venda.domain.repository.ProdutoRespository;
import com.venda.exception.PedidoNaoEncontradoException;
import com.venda.exception.RegraNegocioException;
import com.venda.rest.dto.ItemPedidoDTO;
import com.venda.rest.dto.PedidoDTO;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository repository;
    private final ClienteRespository clientesRepository;
    private final ProdutoRespository produtosRepository;
    private final ItensPedidoRepository itemsPedidoRepository;

    public PedidoServiceImpl(PedidoRepository repository, ClienteRespository clientesRepository, ProdutoRespository produtosRepository, ItensPedidoRepository itemsPedidoRepository) {
        this.repository = repository;
        this.clientesRepository = clientesRepository;
        this.produtosRepository = produtosRepository;
        this.itemsPedidoRepository = itemsPedidoRepository;
    }

    @Override
    @Transactional
    public Pedido salvar( PedidoDTO dto ) {
        Integer idCliente = dto.getCliente();
        Cliente cliente = clientesRepository
                .findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Código de cliente inválido."));

        Pedido pedido = new Pedido();
        pedido.setTotal(dto.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);

        Set<ItemPedido> items = converterItems(pedido, dto.getItems());
        repository.save(pedido);
        itemsPedidoRepository.saveAll(items);
        pedido.setItems(items);
        return pedido;
    }

    private Set<ItemPedido> converterItems(Pedido pedido, List<ItemPedidoDTO> items){
        if(items.isEmpty()){
            throw new RegraNegocioException("Não é possível realizar um pedido sem items.");
        }

        return items
                .stream()
                .map( dto -> {
                    Integer idProduto = dto.getProduto();
                    Produto produto = produtosRepository
                            .findById(idProduto)
                            .orElseThrow(
                                    () -> new RegraNegocioException(
                                            "Código de produto inválido: "+ idProduto
                                    ));

                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setPedido(pedido);
                    itemPedido.setProduto(produto);
                    return itemPedido;
                }).collect(Collectors.toSet());
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return repository.findByIdFetchItens(id);
    }

    @Override
    @Transactional
    public void atualizaStatus( Integer id, StatusPedido statusPedido ) {
        repository
                .findById(id)
                .map( pedido -> {
                    pedido.setStatus(statusPedido);
                    return repository.save(pedido);
                }).orElseThrow(() -> new PedidoNaoEncontradoException() );
    }
}
