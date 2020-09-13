package com.cartoes.api.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.cartoes.api.repositories.TransacaoRepository;
import com.cartoes.api.utils.ConsistenciaException;
import com.cartoes.api.entities.Cartao;
import com.cartoes.api.entities.Transacao;
import com.cartoes.api.repositories.CartaoRepository;
 
@Service
public class TransacaoService {
 
   	private static final Logger log = LoggerFactory.getLogger(TransacaoService.class);
 
   	@Autowired
   	private TransacaoRepository transacaoRepository;
 
   	@Autowired
   	private CartaoRepository cartaoRepository;
 

   	public Optional<List<Transacao>> buscarPorCartaonumero(String cartaonumero) throws ConsistenciaException {
 
         	log.info("Service: buscando as transacoes do cartao de numero: {}", cartaonumero);
 
         	Optional<List<Transacao>> transacao = transacaoRepository.findByCartaonumero(cartaonumero);
 
         	if (!transacao.isPresent() || transacao.get().size() < 1)  {
 
                	log.info("Service: Nenhuma transacao encontrado para o cartao de numero: {}", cartaonumero);
                	throw new ConsistenciaException("Nenhuma transacao encontrado para o cartao de numero:  {}", cartaonumero);
 
         	}
 
         	return transacao;
 
   	}
 
   	public Transacao salvar(Transacao transacao) throws ConsistenciaException {
   	 
     	log.info("Service: salvando a transacao: {}", transacao);

     	Cartao cartao = cartaoRepository.findByNumero(transacao.getCartao().getNumero()).get();
		Date data = new Date();
		
		transacao.getCartao().setId(cartao.getId());
		
     	if (!cartaoRepository.findByNumero(transacao.getCartao().getNumero()).isPresent()) {

            	log.info("Service: Nenhum cartao de numero: {} foi encontrado", transacao.getCartao().getNumero());
            	throw new ConsistenciaException("Nenhum cartao de numero: {} foi encontrado", transacao.getCartao().getNumero());

     	}
     	
     	if(cartao.getDataValidade().before(data)) {
			
			log.info("Cartão vencido, não é possível incluir transações.");
			throw new ConsistenciaException("Cartão vencido, não foi possível incluir transações.");
		}			
		
     	if(cartaoRepository.findByNumero(transacao.getCartao().getNumero()).get().getBloqueado()) {
			
			log.info("Service: Cartão bloqueado, não é possível incluir transações.");
			throw new ConsistenciaException("Cartão bloqueado, não foi possível incluir transações.");
		}
     	
     	if (transacao.getId() > 0) {
            log.info("Service: Transações não podem ser alteradas, apenas incluídas");
            throw new ConsistenciaException("Transações não podem ser alteradas, apenas incluídas");
     	}
		
		
		try {
			return transacaoRepository.save(transacao);
		} catch (DataIntegrityViolationException e) {
			log.info("Service: Erro adicionar transação");
			throw new ConsistenciaException("Erro adicionar transação");
		}
		
	}
   	
}
