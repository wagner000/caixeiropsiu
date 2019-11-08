package com.caixeiro.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.caixeiro.dao.ClienteDAO;
import com.caixeiro.heuristica.Celula;
import com.caixeiro.heuristica.Heuristica;
import com.caixeiro.heuristica.Matriz;
import com.caixeiro.heuristica.Ponto;
import com.caixeiro.model.Cliente;
import com.caixeiro.util.jsf.FacesUtil;



/**
 * @author Wagner
 *
 */
@Named
@ViewScoped
public class PesquisaClienteBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private ClienteDAO clienteDAO;
	
	private Cliente clienteSelecionado;
	public Cliente getClienteSelecionado() {
		return clienteSelecionado;
	}

	public void setClienteSelecionado(Cliente clienteSelecionado) {
		this.clienteSelecionado = clienteSelecionado;
	}

	private List<Cliente> clientes;

	private String filtroNome;
	
	private Celula matriz[][];
	
	public PesquisaClienteBean() {
		filtroNome = new String();
		clienteSelecionado = new Cliente();
	}

	public void init() {
		if(FacesUtil.isNotPostback()) {
			clientes = clienteDAO.todos();
			
			
			matriz = Matriz.geraMatriz(clientes);
			imprimir();
			 Heuristica guloso = new Heuristica();
			Ponto[] caminho = guloso.guloso(matriz);
			
			
		}
	}
	
	
	public void imprimir() {
		
		for(int l = 0; l < matriz.length; l++) {
			for(int c = 0; c < matriz.length; c++) {
				System.out.print("["+matriz[l][c].getOrigem().getId()+ "|");
				System.out.print(matriz[l][c].getDestino().getId()+ "] ");
			}
			System.out.println();
		}
	}
	
	public void limpar() {
		clienteSelecionado = new Cliente();
		clientes = clienteDAO.todos();
	}
	
	public void novo() {
		clienteSelecionado = new Cliente();
	}
	
	public void salvar() {
		try {
			this.clienteSelecionado = clienteDAO.salvar(this.clienteSelecionado);
			limpar();
			
			FacesUtil.addInfoMessage("Cliente salvo com sucesso!");
		} catch (Exception ne) {
			FacesUtil.addErrorMessage(ne.getMessage());
		}
	}
	
	public void excluir() {
		clienteDAO.remover(clienteSelecionado);
		clientes.remove(clienteSelecionado);
		FacesUtil.addInfoMessage("Cliente "+clienteSelecionado.getFantasia()+" removido com sucesso.");
	}

	
	
	
	//===================================================================================
	
	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public String getFiltroNome() {
		return filtroNome;
	}

	public void setFiltroNome(String filtroNome) {
		this.filtroNome = filtroNome;
	}

	public Celula[][] getMatriz() {
		return matriz;
	}

	public void setMatriz(Celula matriz[][]) {
		this.matriz = matriz;
	}
	
}
