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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.conversores.ClienteConversor;
import com.autobots.automanager.dtos.ClienteAtualizacaoDto;
import com.autobots.automanager.dtos.ClienteCadastroDto;
import com.autobots.automanager.dtos.ClienteRespostaDto;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.servicos.ClienteServico;

@RestController
@RequestMapping("/cliente")
public class ClienteControle {

    @Autowired
    private ClienteServico servico;
    @Autowired
    private ClienteConversor conversor;

    @GetMapping("/{id}")
    public ResponseEntity<ClienteRespostaDto> obterCliente(@PathVariable long id) {
        return ResponseEntity.ok(conversor.paraResposta(servico.obterPorId(id)));
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<ClienteRespostaDto>> obterClientes() {
        return ResponseEntity.ok(conversor.paraResposta(servico.listar()));
    }

    @PostMapping("/cadastro")
    public ResponseEntity<ClienteRespostaDto> cadastrarCliente(
            @Valid @RequestBody ClienteCadastroDto dto) {
        Cliente cadastrado = servico.cadastrar(conversor.paraEntidade(dto));
        return ResponseEntity.created(URI.create("/cliente/" + cadastrado.getId()))
                .body(conversor.paraResposta(cadastrado));
    }

    @PutMapping("/atualizar")
    public ResponseEntity<ClienteRespostaDto> atualizarCliente(
            @Valid @RequestBody ClienteAtualizacaoDto dto) {
        Cliente atualizado = servico.atualizar(conversor.paraEntidade(dto));
        return ResponseEntity.ok(conversor.paraResposta(atualizado));
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> excluirCliente(@PathVariable long id) {
        servico.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
