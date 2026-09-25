package com.autobots.automanager.controles;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.conversores.EnderecoConversor;
import com.autobots.automanager.dtos.EnderecoCadastroDto;
import com.autobots.automanager.dtos.EnderecoDto;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.servicos.EnderecoServico;

@RestController
@RequestMapping("/cliente/{clienteId}/endereco")
public class EnderecoControle {

    @Autowired
    private EnderecoServico servico;
    @Autowired
    private EnderecoConversor conversor;

    @GetMapping
    public ResponseEntity<EnderecoDto> obterEndereco(@PathVariable long clienteId) {
        return ResponseEntity.ok(conversor.paraResposta(servico.obterPorCliente(clienteId)));
    }

    @PutMapping
    public ResponseEntity<EnderecoDto> definirEndereco(@PathVariable long clienteId,
            @Valid @RequestBody EnderecoCadastroDto dto) {
        Endereco definido = servico.definir(clienteId, conversor.paraEntidade(dto));
        return ResponseEntity.ok(conversor.paraResposta(definido));
    }

    @DeleteMapping
    public ResponseEntity<Void> excluirEndereco(@PathVariable long clienteId) {
        servico.excluir(clienteId);
        return ResponseEntity.noContent().build();
    }
}
