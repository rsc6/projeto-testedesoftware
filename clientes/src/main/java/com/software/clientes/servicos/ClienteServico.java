package com.software.clientes.servicos;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.software.clientes.entidades.Cliente;
import com.software.clientes.repositorios.ClienteRepositorio;

@Service
public class ClienteServico {
	
	@Autowired
	private ClienteRepositorio repository;
	
	@Transactional
	public Cliente save(Cliente entry) {
		Cliente entity = new Cliente(); 
		entity.setName(entry.getName());
		entity.setCpf(entry.getCpf());
		repository.save(entity);
		return entity;
	}
	
	@Transactional
	public Cliente update(Long id, Cliente clienteEntry) {
		try {
			Cliente entity = repository.getReferenceById(id);
			entity.setName(clienteEntry.getName());
			entity.setCpf(clienteEntry.getCpf());
			entity = repository.save(entity);
			return entity;
		}
		catch (EntityNotFoundException e) {
			throw new EntityNotFoundException("Id not found " + id);
		}
	}
	
	@Transactional(readOnly = true)
	public Cliente findById(Long id) {
		Optional<Cliente> obj = repository.findById(id);
		Cliente entity = obj.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		return entity;
	}
	
	@Transactional(readOnly = true)
	public Page<Cliente> findAllPaged(Pageable pageable) {
		Page<Cliente> list = repository.findAll(pageable);
		return list;
	}
	
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}
		catch (EmptyResultDataAccessException  e ) {
			throw new EntityNotFoundException("Id not found " + id);
		}
		
	}
	
}
