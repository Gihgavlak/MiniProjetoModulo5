package com.br.zup.vicente_imoveis.Imovel;


import com.br.zup.vicente_imoveis.Contrato.Contrato;
import com.br.zup.vicente_imoveis.Contrato.ContratoService;
import com.br.zup.vicente_imoveis.Custom_exception.ImovelCadastradoException;
import com.br.zup.vicente_imoveis.Custom_exception.ImovelNaoCadastradoException;
import com.br.zup.vicente_imoveis.Custom_exception.VinculoContratualException;
import com.br.zup.vicente_imoveis.Endereco.EnderecoService;
import com.br.zup.vicente_imoveis.Imovel.Dtos.ImovelAtualizarDTO;
import com.br.zup.vicente_imoveis.Imovel.Enums.StatusImovel;
import com.br.zup.vicente_imoveis.Imovel.Enums.TipoDeContrato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImovelService {

    @Autowired
    ImovelRepository imovelRepository;
    @Autowired
    EnderecoService enderecoService;
    @Autowired
    ContratoService contratoService;

    public Imovel salvarImovel(Imovel imovel){
        if (enderecoService.enderecoExiste(imovel.getEndereco())){
            throw new ImovelCadastradoException("Imóvel já consta no banco de dados");
        }
        enderecoService.salvarEndereco(imovel.getEndereco());
        imovel.setStatusImovel(StatusImovel.DISPONIVEL);
        return imovelRepository.save(imovel);
    }

    public List<Imovel> exibirImoveisCadastrados(){
        Iterable<Imovel> imoveis = imovelRepository.findAll();
        return (List<Imovel>) imoveis;
    }

    public Imovel buscarImovelPorID(int id){
        Optional<Imovel> imovel=imovelRepository.findById(id);
        if (imovel.isEmpty()){
            throw new ImovelNaoCadastradoException("Imovel não encontrado");
        }
        return imovel.get();
    }

    public void deletarImovel(int id){
        Imovel imovel = buscarImovelPorID(id);
        List <Contrato> contratos = contratoService.buscarContratoPorEndereco(imovel.getEndereco());
        if (contratos.isEmpty()){
            imovelRepository.deleteById(id);
        }
        throw new VinculoContratualException("Impossível deletar pois possui vínculo contratual");
    }

    public Imovel atualizarImovel(int id, ImovelAtualizarDTO imovelEntrada){
        Optional<Imovel> imovel = imovelRepository.findById(id);
        if (imovel.isEmpty()){
            throw new ImovelNaoCadastradoException("Imóvel não cadastrado, por gentileza efetue o cadastro primeiro.");
        }

        Imovel imovelParaAtualizar = imovel.get();
        imovelParaAtualizar.setValor(imovelEntrada.getValor());
        imovelParaAtualizar.setTipoDeImovel(imovelEntrada.getTipoDeImovel());
        imovelParaAtualizar.setQtdBanheiros(imovelEntrada.getQtdBanheiros());
        imovelParaAtualizar.setMetragem(imovelEntrada.getMetragem());
        imovelParaAtualizar.setTipoDeContrato(imovelEntrada.getTipoDeContrato());

        imovelRepository.save(imovelParaAtualizar);
        return imovelParaAtualizar;

    }

    public List<Imovel> aplicarFiltroParaBusca(TipoDeContrato tipoDeContrato, String tipoDeImovel,
                                               StatusImovel statusImovel, Double valor){
        if (tipoDeContrato != null){
            return imovelRepository.findAllByTipoDeContrato(tipoDeContrato);
        }
        else if (tipoDeImovel != null){
            return imovelRepository.findAllByTipoDeImovel(tipoDeImovel);
        }
        else if (statusImovel != null){
            return imovelRepository.findAllByStatusImovel(statusImovel);
        }
        else if (valor != null){
            return imovelRepository.findAllByValor(valor);

        }

        return exibirImoveisCadastrados();
    }

}
