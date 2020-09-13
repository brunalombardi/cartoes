package com.cartoes.api.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.cartoes.api.entities.Cartao;
import com.cartoes.api.entities.Transacao;
import com.cartoes.api.repositories.CartaoRepository;
import com.cartoes.api.repositories.TransacaoRepository;
import com.cartoes.api.utils.ConsistenciaException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TransacaoServiceTest {

	@MockBean
	private TransacaoRepository transacaoRepository;

	@Autowired
	private TransacaoService transacaoService;

	@MockBean
	private CartaoRepository cartaoRepository;

	@Test
	public void testBuscarPorNumeroCartaoExistente() throws ConsistenciaException {

		List<Transacao> lstTransacao = new ArrayList<>();
		lstTransacao.add(new Transacao());

		BDDMockito.given(transacaoRepository.findByCartaonumero(Mockito.anyString()))
				.willReturn(Optional.of(lstTransacao));

		Optional<List<Transacao>> resultado = transacaoService.buscarPorCartaonumero("1230981203");

		assertTrue(resultado.isPresent());

	}

	@Test(expected = ConsistenciaException.class)
	public void testBuscarPorNumeroCartaoNaoExistente() throws ConsistenciaException {

		BDDMockito.given(transacaoRepository.findByCartaonumero(Mockito.anyString())).willReturn(Optional.empty());

		transacaoService.buscarPorCartaonumero("1230981203");

	}

	@Test
	public void testSalvarComSucesso() throws ConsistenciaException, ParseException {

		Cartao cartao = new Cartao();
		cartao.setNumero("1321564654");
		cartao.setDataValidade(new SimpleDateFormat("dd/MM/yyyy").parse("01/12/2020"));
		Transacao transacao = new Transacao();
		transacao.setCartao(cartao);

		BDDMockito.given(cartaoRepository.findByNumero(Mockito.anyString())).willReturn(Optional.of(cartao));
		BDDMockito.given(transacaoRepository.save(Mockito.any(Transacao.class))).willReturn(new Transacao());

		Transacao resultado = transacaoService.salvar(transacao);

		assertNotNull(resultado);
	}

	@Test(expected = ConsistenciaException.class)
	public void testSalvarNumeroCartaoNaoEncontrado() throws ConsistenciaException {

		BDDMockito.given(transacaoRepository.findByCartaonumero(Mockito.anyString())).willReturn(Optional.empty());

		Cartao a = new Cartao();
		a.setNumero("34244234");
		Transacao c = new Transacao();

		c.setCartao(a);

		transacaoService.salvar(c);

	}
	
}