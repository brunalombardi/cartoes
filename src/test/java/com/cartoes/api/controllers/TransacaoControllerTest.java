package com.cartoes.api.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cartoes.api.dtos.TransacaoDto;
import com.cartoes.api.entities.Cartao;
import com.cartoes.api.entities.Cliente;
import com.cartoes.api.entities.Transacao;
import com.cartoes.api.services.CartaoService;
import com.cartoes.api.services.ClienteService;
import com.cartoes.api.services.TransacaoService;
import com.cartoes.api.utils.ConsistenciaException;
import com.cartoes.api.utils.ConversaoUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransacaoControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private TransacaoService transacaoService;
	
	@MockBean
	private CartaoService cartaoService;
	
	@MockBean
	private ClienteService clienteService;

	private Transacao CriarTransacaoTestes() throws ParseException {

		Cliente clienteTeste = new Cliente();

		clienteTeste.setId(1);
		clienteTeste.setNome("Nome Teste");
		clienteTeste.setCpf("05887098082");
		clienteTeste.setUf("CE");
		
		Cartao cartaoTeste = new Cartao();
		
		cartaoTeste.setId(1);
		cartaoTeste.setNumero("1230981203");
		cartaoTeste.setBloqueado(false);
		cartaoTeste.setCliente(clienteTeste);
		
		Transacao transacaoTeste = new Transacao();
		
		transacaoTeste.setId(1);
		transacaoTeste.setCnpj("18808626000194");
		transacaoTeste.setDataTransacao(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2020"));
		transacaoTeste.setValor(1500.00);
		transacaoTeste.setQtdParcelas(3);
		transacaoTeste.setJuros(0.05);
		transacaoTeste.setCartao(cartaoTeste);
		
		return transacaoTeste;

	}
	
	@Test
	@WithMockUser
	public void testBuscarPorNumeroCartaoSucesso() throws Exception {

		List<Transacao> lst = new ArrayList<Transacao>();
		Transacao transacao = CriarTransacaoTestes();
		lst.add(transacao);
		
		BDDMockito.given(transacaoService.buscarPorCartaonumero(Mockito.anyString()))
			.willReturn(Optional.of(lst));

		mvc.perform(MockMvcRequestBuilders.get("/api/transacao/cartao/1230981203")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andExpect(jsonPath("$.dados.[0].id").value(transacao.getId()))
			.andExpect(jsonPath("$.dados.[0].cnpj").value(transacao.getCnpj()))
			.andExpect(jsonPath("$.dados.[0].valor").value(transacao.getValor()))
			.andExpect(jsonPath("$.dados.[0].qdtParcelas").value(transacao.getQtdParcelas()))
			.andExpect(jsonPath("$.dados.[0].juros").value(transacao.getJuros()))
			.andExpect(jsonPath("$.dados.[0].cartaoNumero").value(transacao.getCartao().getNumero()))
			//.andExpect(jsonPath("$.dados.[0].dataTransacao").value(transacao.getDataTransacao()))
			.andExpect(jsonPath("$.erros").isEmpty());

	}
	@Test
    @WithMockUser
    public void testBuscarPorNumeroCartaoInconsistencia() throws Exception {

        BDDMockito.given(transacaoService.buscarPorCartaonumero((Mockito.anyString())))
            .willThrow(new ConsistenciaException("Teste inconsistência"));

        mvc.perform(MockMvcRequestBuilders.get("/api/transacao/cartao/1230981203")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.erros").value("Teste inconsistência"));

    }
	@Test
    @WithMockUser
    public void testSalvarSucesso() throws Exception {

        Transacao transacao = CriarTransacaoTestes();
        TransacaoDto objEntrada = ConversaoUtils.Converter(transacao);
        objEntrada.setDataTransacao("01/01/2020");
        
        String json = new ObjectMapper().writeValueAsString(objEntrada);
        BDDMockito.given(transacaoService.salvar(Mockito.any(Transacao.class)))
            .willReturn(transacao);

        	mvc.perform(MockMvcRequestBuilders.post("/api/transacao")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.id").value(transacao.getId()))
            .andExpect(jsonPath("$.dados.cnpj").value(transacao.getCnpj()))
            .andExpect(jsonPath("$.dados.valor").value(transacao.getValor()))
            .andExpect(jsonPath("$.dados.qdtParcelas").value(transacao.getQtdParcelas()))
            .andExpect(jsonPath("$.dados.juros").value(transacao.getJuros()))
            .andExpect(jsonPath("$.dados.cartaoNumero").value(transacao.getCartao().getNumero()))
            .andExpect(jsonPath("$.erros").isEmpty());

    }
	@Test
    @WithMockUser
    public void testSalvarInconsistencia() throws Exception {

        Transacao transacao = CriarTransacaoTestes();
        TransacaoDto objEntrada = ConversaoUtils.Converter(transacao);
        objEntrada.setDataTransacao("01/01/2020");

        String json = new ObjectMapper().writeValueAsString(objEntrada);

        BDDMockito.given(transacaoService.salvar(Mockito.any(Transacao.class)))
            .willThrow(new ConsistenciaException("Teste inconsistência."));

        mvc.perform(MockMvcRequestBuilders.post("/api/transacao")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.erros").value("Teste inconsistência."));
    }
}