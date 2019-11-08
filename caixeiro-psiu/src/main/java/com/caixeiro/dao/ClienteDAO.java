package com.caixeiro.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import com.caixeiro.model.Cliente;
import com.caixeiro.model.Vendedor;
import com.caixeiro.util.jpa.Transacional;

public class ClienteDAO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Cliente porId(Long id) {
		return manager.find(Cliente.class, id);
	}

	@Transacional
	public Cliente salvar(Cliente cliente) {
		return manager.merge(cliente);
	}

	@Transacional
	public void remover(Cliente cliente) {
		try {

			cliente = porId(cliente.getId());
			manager.remove(cliente);
			manager.flush();
		} catch (PersistenceException e) {
			try {
				throw new Exception("Este Cliente não pode ser removido");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public List<Cliente> todos() {

		return manager.createQuery("from Cliente", Cliente.class).getResultList();
	}
	
	public List<Cliente> porVendedor(Vendedor vendedor){
		Query q = manager.createQuery("SELECT cliente "
										+ "FROM Cliente cliente JOIN FETCH cliente.vendedor vendedor WHERE "
										+ "vendedor.id = :idVendedor");
		q.setParameter("idVendedor", vendedor.getId());
		return q.getResultList();
	}
	
	public List<Cliente> porVendedorDia(Vendedor vendedor, int dia){ // dia 1 = domingo
		Query q = manager.createQuery("SELECT cliente "
										+ "FROM Cliente cliente JOIN FETCH cliente.vendedor vendedor WHERE "
										+ "vendedor.id = :idVendedor AND "
										+ "cliente.diaSemana = :dia");
		q.setParameter("idVendedor", vendedor.getId());
		q.setParameter("dia", dia);
		return q.getResultList();
	}
	

}
