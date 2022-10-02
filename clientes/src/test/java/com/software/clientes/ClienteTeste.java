package com.software.clientes;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.software.clientes.entidades.Cliente;
import com.software.clientes.repositorios.ClienteRepositorio;
import com.software.clientes.servicos.ClienteServico;

@ExtendWith(SpringExtension.class)
public class ClienteTeste {

	@InjectMocks
	private ClienteServico servico;

	@Mock
	private ClienteRepositorio repositorio;

	private Long existeId;
	private Long naoExistegId;
	private Long contFinalCliente;
	private PageImpl<Cliente> page;
	private Cliente cliente;
	private String nome;
	private String cpf;
	private String updateNome;
	private String updateCpf;

	@BeforeEach
	void setup() throws Exception {
		existeId = 1L;
		naoExistegId = 2000L;
		contFinalCliente = 3L;
		nome = "delphi Desatualizado";
		cpf = "11111111111";
		cliente = new Cliente(nome, cpf);
		page = new PageImpl<>(List.of(cliente));

		Mockito.when(repositorio.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repositorio.save(ArgumentMatchers.any())).thenReturn(cliente);
		Mockito.when(repositorio.findById(existeId)).thenReturn(Optional.of(cliente));
		Mockito.when(repositorio.getReferenceById(existeId)).thenReturn(cliente);
		Mockito.when(repositorio.getReferenceById(naoExistegId)).thenThrow(EntityNotFoundException.class);
		Mockito.doNothing().when(repositorio).deleteById(existeId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repositorio).deleteById(naoExistegId);

	}

	@Test
	public void salveDeveriaSalvaERetornarCliente() {

		Cliente entidade = servico.save(cliente);

		Assertions.assertNotNull(entidade);
		Mockito.verify(repositorio, Mockito.times(1)).save(cliente);
	}

	@Test void updateDeveriaAtualizarDadosQuandoIdExistir() {
		
		Cliente entidade = servico.update(existeId, cliente);
		
		Assertions.assertNotNull(entidade);
		Mockito.verify(repositorio, Mockito.times(1)).getReferenceById(existeId);
		Mockito.verify(repositorio, Mockito.times(1)).save(cliente);
	}

	@Test void updateDeveriaLancarEntityNotFoundExceptionQuandoIdNaoExistir() {
		
		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			servico.update(naoExistegId, cliente);
		});
		Mockito.verify(repositorio).getReferenceById(naoExistegId);
	}
	
	@Test
	public void findByIdDeveriaLancarEntityNotFoundExceptionQuandoIdNaoExistir() {
		
		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			servico.findById(naoExistegId);
		});
		Mockito.verify(repositorio).findById(naoExistegId);
	}
	
	@Test
	public void findByIdDeveriaRetornarClienteQuandoIdExistir() {
		
		Cliente findByIdCliente = servico.findById(existeId);
		
		Assertions.assertNotNull(findByIdCliente);
		Mockito.verify(repositorio).findById(existeId);
	}
	
	@Test
	public void findAllPageDeveriaRetornarPage() {
		
Pageable pageable = PageRequest.of(0, 10);
		
		Page<Cliente> result = servico.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		Mockito.verify(repositorio).findAll(pageable);
	}
	
	@Test
	public void deleteDeveriaLancarEntityNotFoundExceptionQuandoIdNaoExistir() {
		
		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			servico.delete(naoExistegId);
		});
		Mockito.verify(repositorio, Mockito.times(1)).deleteById(naoExistegId);
	}
	
	@Test
	public void deleteDeveriaFazerNadaQuandoIdExister() {
		
		Assertions.assertDoesNotThrow( () -> {
			servico.delete(existeId);
		});
		Mockito.verify(repositorio, Mockito.times(1)).deleteById(existeId);
	}
}
