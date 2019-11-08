package com.caixeiro.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.caixeiro.dao.VendedorDAO;
import com.caixeiro.model.Vendedor;
import com.caixeiro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class CadastroVendedorBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private VendedorDAO vendedorDAO;
	private Vendedor vendedor;
	private List<Vendedor> vendedores;
	
	public void init() {
		
		// TODO Auto-generated constructor stub
		this.vendedor = new Vendedor();
		this.vendedores = vendedorDAO.todos();
		
	}

	
	
	public void salvar() {
		if(vendedor.getId() != null) {
			vendedorDAO.salvar(vendedor);
			vendedor = new Vendedor();
			vendedores = vendedorDAO.todos();
			FacesUtil.addInfoMessage("Vendedor salvo!");
		}else
			FacesUtil.addErrorMessage("Código inválido!");
		
	}
	
	
	public Vendedor getVendedor() {
		return vendedor;
	}

	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}



	public List<Vendedor> getVendedores() {
		return vendedores;
	}



	public void setVendedores(List<Vendedor> vendedores) {
		this.vendedores = vendedores;
	}
	
}
