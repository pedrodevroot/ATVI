package com.autobots.automanager.servicos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.excecoes.RecursoNaoEncontradoException;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@Service
public class EnderecoServico {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    public Endereco obterPorCliente(long clienteId) {
        Endereco endereco = obterCliente(clienteId).getEndereco();
        if (endereco == null) {
            throw new RecursoNaoEncontradoException(mensagemAusente(clienteId));
        }
        return endereco;
    }

    @Transactional
    public Endereco definir(long clienteId, Endereco endereco) {
        Cliente cliente = obterCliente(clienteId);
        endereco.setId(null);
        cliente.setEndereco(endereco);
        return clienteRepositorio.save(cliente).getEndereco();
    }

    @Transactional
    public void excluir(long clienteId) {
        Cliente cliente = obterCliente(clienteId);
        if (cliente.getEndereco() == null) {
            throw new RecursoNaoEncontradoException(mensagemAusente(clienteId));
        }
        cliente.setEndereco(null);
        clienteRepositorio.save(cliente);
    }

    private Cliente obterCliente(long clienteId) {
        return clienteRepositorio.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", clienteId));
    }

    private String mensagemAusente(long clienteId) {
        return "O cliente de id " + clienteId + " nao possui endereco cadastrado";
    }
}
