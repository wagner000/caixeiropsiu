package com.caixeiro.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import com.caixeiro.model.Vendedor;
import com.caixeiro.util.jpa.Transacional;
import com.caixeiro.util.jsf.ImageCreator;

public class VendedorDAO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Vendedor porId(Long id) {
		return manager.find(Vendedor.class, id);
	}

	public Long qtdeCliente(Vendedor vendedor) {
		Query q = manager.createQuery("SELECT count(*) FROM Cliente cli WHERE cli.vendedor.id = :idVendedor");
		q.setParameter("idVendedor", vendedor.getId());
		Long qtde = (Long) q.getSingleResult();
		return qtde;
		
	}
	
	
	@Transacional
	public Vendedor salvar(Vendedor vendedor) {
		
		try {
			Vendedor vend = manager.merge(vendedor);
			ImageCreator.criarIcon(vend.getId().toString());
			return vend;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Transacional
	public void remover(Vendedor vendedor) {
		try {

			vendedor = porId(vendedor.getId());
			manager.remove(vendedor);
			manager.flush();
		} catch (PersistenceException e) {
			try {
				throw new Exception("Este Vendedor não pode ser removido");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/*
	 * public List<Vendedor> todos() {
	 * 
	 * return manager.createQuery("from Vendedor", Vendedor.class).getResultList();
	 * }
	 */
	
	public List<Vendedor> todos() {
		try {
			List<Vendedor> vendedores = manager.createQuery("from Vendedor", Vendedor.class).getResultList();
			for(Vendedor v : vendedores) {
				ImageCreator.criarIcon(v.getId().toString());
			}
			return vendedores;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return manager.createQuery("from Vendedor", Vendedor.class).getResultList();
		}
	}
	

}
