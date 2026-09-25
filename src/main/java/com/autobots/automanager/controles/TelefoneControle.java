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

import com.autobots.automanager.conversores.TelefoneConversor;
import com.autobots.automanager.dtos.TelefoneAtualizacaoDto;
import com.autobots.automanager.dtos.TelefoneCadastroDto;
import com.autobots.automanager.dtos.TelefoneDto;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.servicos.TelefoneServico;

@RestController
public class TelefoneControle {

    @Autowired
    private TelefoneServico servico;
    @Autowired
    private TelefoneConversor conversor;

    @GetMapping("/cliente/{clienteId}/telefones")
    public ResponseEntity<List<TelefoneDto>> obterTelefones(@PathVariable long clienteId) {
        return ResponseEntity.ok(conversor.paraResposta(servico.listarPorCliente(clienteId)));
    }

    @PostMapping("/cliente/{clienteId}/telefones")
    public ResponseEntity<TelefoneDto> cadastrarTelefone(@PathVariable long clienteId,
            @Valid @RequestBody TelefoneCadastroDto dto) {
        Telefone criado = servico.criar(clienteId, conversor.paraEntidade(dto));
        return ResponseEntity.created(URI.create("/telefone/" + criado.getId()))
                .body(conversor.paraResposta(criado));
    }

    @GetMapping("/telefone/{id}")
    public ResponseEntity<TelefoneDto> obterTelefone(@PathVariable long id) {
        return ResponseEntity.ok(conversor.paraResposta(servico.obterPorId(id)));
    }

    @PutMapping("/telefone/{id}")
    public ResponseEntity<TelefoneDto> atualizarTelefone(@PathVariable long id,
            @Valid @RequestBody TelefoneAtualizacaoDto dto) {
        Telefone atualizado = servico.atualizar(id, conversor.paraEntidade(dto));
        return ResponseEntity.ok(conversor.paraResposta(atualizado));
    }

    @DeleteMapping("/telefone/{id}")
    public ResponseEntity<Void> excluirTelefone(@PathVariable long id) {
        servico.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
