package com.autobots.automanager.controles;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.conversores.EnderecoConversor;
import com.autobots.automanager.dtos.EnderecoAtualizacaoDto;
import com.autobots.automanager.dtos.EnderecoCadastroDto;
import com.autobots.automanager.dtos.EnderecoDto;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.servicos.EnderecoServico;

@RestController
public class EnderecoControle {

    @Autowired
    private EnderecoServico servico;
    @Autowired
    private EnderecoConversor conversor;

    @GetMapping("/cliente/{clienteId}/enderecos")
    public ResponseEntity<List<EnderecoDto>> obterEnderecos(@PathVariable long clienteId) {
        return ResponseEntity.ok(conversor.paraResposta(servico.listarPorCliente(clienteId)));
    }

    @PostMapping("/cliente/{clienteId}/enderecos")
    public ResponseEntity<EnderecoDto> cadastrarEndereco(@PathVariable long clienteId,
            @Valid @RequestBody EnderecoCadastroDto dto) {
        Endereco criado = servico.criar(clienteId, conversor.paraEntidade(dto));
        return ResponseEntity.created(URI.create("/endereco/" + criado.getId()))
                .body(conversor.paraResposta(criado));
    }

    @GetMapping("/endereco/{id}")
    public ResponseEntity<EnderecoDto> obterEndereco(@PathVariable long id) {
        return ResponseEntity.ok(conversor.paraResposta(servico.obterPorId(id)));
    }

    @PutMapping("/endereco/{id}")
    public ResponseEntity<EnderecoDto> atualizarEndereco(@PathVariable long id,
            @Valid @RequestBody EnderecoAtualizacaoDto dto) {
        Endereco atualizado = servico.atualizar(id, conversor.paraEntidade(dto));
        return ResponseEntity.ok(conversor.paraResposta(atualizado));
    }

    @DeleteMapping("/endereco/{id}")
    public ResponseEntity<Void> excluirEndereco(@PathVariable long id) {
        servico.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
