package com.autobots.automanager.dtos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ClienteRespostaDto {
    private Long id;
    private String nome;
    private String nomeSocial;
    private Date dataNascimento;
    private Date dataCadastro;
    private List<DocumentoDto> documentos = new ArrayList<>();
    private EnderecoDto endereco;
    private List<TelefoneDto> telefones = new ArrayList<>();
}
