package com.br.zup.vicente_imoveis.Contrato;

import com.br.zup.vicente_imoveis.Contrato.Enums.StatusDoContrato;
import com.br.zup.vicente_imoveis.Imovel.Enums.StatusImovel;
import com.br.zup.vicente_imoveis.Imovel.Enums.TipoDeContrato;
import com.br.zup.vicente_imoveis.Imovel.Imovel;
import com.br.zup.vicente_imoveis.Imovel.ImovelService;
import com.br.zup.vicente_imoveis.Cliente.Cliente;
import com.br.zup.vicente_imoveis.Cliente.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;


@Service
public class ContratoService {
    @Autowired
    ContratoRepository contratoRepository;
    @Autowired
    ImovelService imovelService;
    @Autowired
    ClienteService clienteService;

    public Contrato salvarContrato(int id_imovel, String id_Cliente) {
        Imovel imovel = imovelService.buscarImovelPorID(id_imovel);
        Cliente cliente = clienteService.buscarClientePorID(id_Cliente);
        Contrato contrato = new Contrato();
        contrato.setImovel(imovel);
        contrato.setCliente(cliente);
        contrato.setDataDoContrato(LocalDate.now());

        if (imovel.getTipoDeContrato().equals(TipoDeContrato.ALUGUEL)) {
            imovel.setStatusImovel(StatusImovel.ALUGADO);
        } else if (imovel.getTipoDeContrato().equals(TipoDeContrato.VENDA)) {
            imovel.setStatusImovel(StatusImovel.VENDIDO);
        }

        contrato.setStatusDoContrato(StatusDoContrato.ATIVO);
        return contratoRepository.save(contrato);
    }

    public List<Contrato> exibirContratosCadastrados(String cpf, StatusDoContrato statusDoContrato, Integer idImovel){
        Iterable<Contrato> contratosIterable = contratoRepository.findAll();
        List<Contrato> contratosDoBanco = (List<Contrato>) contratosIterable;
        List<Contrato> contratos = new ArrayList<>();

        for (Contrato contrato : contratosDoBanco) {
            if (cpf != null && contrato.getCliente().getCpf().equals(cpf)) {
                contratos.add(contrato);
            } else if (statusDoContrato != null && contrato.getStatusDoContrato().equals(statusDoContrato)) {
                contratos.add(contrato);
            } else if (idImovel != null && contrato.getImovel().getId() == idImovel) {
                contratos.add(contrato);
            }
        }
        return contratos;
    }

    public Contrato localizarContratoPorId(int id){
        Optional<Contrato> contratoOptional = contratoRepository.findById(id);
        if (contratoOptional.isEmpty()){
            throw new RuntimeException("Contrato não encontrado");
        }
        return contratoOptional.get();
    }

    public Contrato atualizarContrato(int id){
        Contrato contrato = localizarContratoPorId(id);
        if (contrato.getImovel().getTipoDeContrato().equals(TipoDeContrato.ALUGUEL)){
            contrato.setStatusDoContrato(StatusDoContrato.INATIVO);
            contrato.getImovel().setStatusImovel(StatusImovel.DISPONIVEL);
            contrato.setDataTerminoContrato(LocalDate.now());
            contratoRepository.save(contrato);
        }else if (contrato.getImovel().getTipoDeContrato().equals(TipoDeContrato.VENDA)){
            throw new RuntimeException("Não é possível encerrar um contrato de venda");
        }
        return contrato;
    }

}
