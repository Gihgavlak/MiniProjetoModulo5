package com.br.zup.vicente_imoveis.cliente;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@RequiredArgsConstructor
@Getter
@Setter
public class ClienteDTO {
    @CPF(message ="CPF inválido")
    @NotNull
    private String cpf;
    @NotBlank
    @Size(min = 2,message = "O nome não pode ter menos de 2 caracteres")
    @NotNull
    private String nome;
    // @Pattern("\\")
    @NotNull
    private String telefone;

    public ClienteDTO(String cpf, String nome, String telefone) {
        this.cpf = cpf;
        this.nome = nome;
        this.telefone = telefone;
    }

}
