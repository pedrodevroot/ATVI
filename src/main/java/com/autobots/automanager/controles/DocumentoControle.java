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

import com.autobots.automanager.conversores.DocumentoConversor;
import com.autobots.automanager.dtos.DocumentoAtualizacaoDto;
import com.autobots.automanager.dtos.DocumentoCadastroDto;
import com.autobots.automanager.dtos.DocumentoDto;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.servicos.DocumentoServico;

@RestController
public class DocumentoControle {

    @Autowired
    private DocumentoServico servico;
    @Autowired
    private DocumentoConversor conversor;

    @GetMapping("/cliente/{clienteId}/documentos")
    public ResponseEntity<List<DocumentoDto>> obterDocumentos(@PathVariable long clienteId) {
        return ResponseEntity.ok(conversor.paraResposta(servico.listarPorCliente(clienteId)));
    }

    @PostMapping("/cliente/{clienteId}/documentos")
    public ResponseEntity<DocumentoDto> cadastrarDocumento(@PathVariable long clienteId,
            @Valid @RequestBody DocumentoCadastroDto dto) {
        Documento criado = servico.criar(clienteId, conversor.paraEntidade(dto));
        return ResponseEntity.created(URI.create("/documento/" + criado.getId()))
                .body(conversor.paraResposta(criado));
    }

    @GetMapping("/documento/{id}")
    public ResponseEntity<DocumentoDto> obterDocumento(@PathVariable long id) {
        return ResponseEntity.ok(conversor.paraResposta(servico.obterPorId(id)));
    }

    @PutMapping("/documento/{id}")
    public ResponseEntity<DocumentoDto> atualizarDocumento(@PathVariable long id,
            @Valid @RequestBody DocumentoAtualizacaoDto dto) {
        Documento atualizado = servico.atualizar(id, conversor.paraEntidade(dto));
        return ResponseEntity.ok(conversor.paraResposta(atualizado));
    }

    @DeleteMapping("/documento/{id}")
    public ResponseEntity<Void> excluirDocumento(@PathVariable long id) {
        servico.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
