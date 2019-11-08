package com.caixeiro.heuristica;

import com.caixeiro.model.Cliente;

/**
 * @author wagner rodrigues
 * @version 1.0
 * @see A classe ponto representa cada cliente que o caixeiro viajante
 *          deve visitar
 *          @
 */

public class Ponto {

	private int id;
	
	private Cliente cliente;

	private Boolean usado = false;
	private Boolean origem = false;

	public Ponto(Cliente c) {
		this.cliente = c;
	}

	public Double getLatitude() {

		return cliente.getLatitude();
	}

	public Double getLongitude() {
		return cliente.getLongitude();
	}

	public Boolean getUsado() {
		return usado;
	}

	public void setUsado(Boolean usado) {
		this.usado = usado;
	}

	public Boolean getOrigem() {
		return origem;
	}

	public void setOrigem(Boolean origem) {
		this.origem = origem;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	public Boolean isOrigem() {
		return origem;
	}
	
	public Boolean isUsado() {
		return usado;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}