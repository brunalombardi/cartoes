package com.cartoes.api.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.cartoes.api.entities.Cartao;
import com.cartoes.api.entities.Cliente;
import com.cartoes.api.entities.Transacao;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TransacaoRepositoryTest {
	
	
	
	@Autowired
	private CartaoRepository cartaoRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private TransacaoRepository transacaoRepository;
	
	private Cliente clienteTeste;
	private Cartao cartaoTeste;
	private Transacao transacaoTeste;
	
	
	private void criarClienteTeste() throws ParseException{
		clienteTeste = new Cliente();
		
		clienteTeste.setCpf("99562613607");
		clienteTeste.setNome("lucas");
		clienteTeste.setUf("sp");
	}
	
	private void criarCartaoTeste() throws ParseException{
		cartaoTeste = new Cartao();
		
		cartaoTeste.setBloqueado(false);
		cartaoTeste.setCliente(clienteTeste);
		cartaoTeste.setDataValidade(new SimpleDateFormat("dd/MM/yyyy").parse("11/08/2000"));
		cartaoTeste.setNumero("666");
	}
	
	
	private void criarTransacaoTeste() throws ParseException{
		transacaoTeste = new Transacao();
		
		transacaoTeste.setCnpj("42464468000187");		
		transacaoTeste.setJuros(0.15);
		transacaoTeste.setCartao(cartaoTeste);
		transacaoTeste.setQtdParcelas(12);
		transacaoTeste.setValor(89.0);
		transacaoTeste.prePersist();
		
	}
	

	@Before
	public void setUp() throws Exception{
		criarClienteTeste();
		criarCartaoTeste();
		criarTransacaoTeste();
		
		clienteRepository.save(clienteTeste);
		cartaoRepository.save(cartaoTeste);
		transacaoRepository.save(transacaoTeste);
	}
	
	@After
	public void tearDown() throws Exception {
		
		transacaoRepository.deleteAll();
		cartaoRepository.deleteAll();
		clienteRepository.deleteAll();
		
	}
	
	@Test 
	public void testFindById() {
		Transacao transacao = transacaoRepository.findById(transacaoTeste.getId()).get();
		assertEquals(transacaoTeste.getId(), transacao.getId());
	}
	
	@Test
	public void testFindByCartaoNumero() {
		List<Transacao> lstTransacao = transacaoRepository.findByCartaonumero(transacaoTeste.getCartao().getNumero());
		if(lstTransacao.size() != 1) {
			fail();
		}
		
		Transacao transacao = lstTransacao.get(0);
		
		assertTrue(transacao.getCartao().getNumero().equals(transacaoTeste.getCartao().getNumero()));
	}
	
}