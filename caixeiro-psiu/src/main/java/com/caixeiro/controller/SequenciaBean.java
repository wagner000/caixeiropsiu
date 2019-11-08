package com.caixeiro.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.LatLngBounds;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polygon;
import org.primefaces.model.map.Polyline;

import com.caixeiro.dao.ClienteDAO;
import com.caixeiro.heuristica.ControllerPCV;
import com.caixeiro.heuristica.HeuristicaVMP;
import com.caixeiro.heuristica.Matriz;
import com.caixeiro.heuristica.VND;
import com.caixeiro.model.Cliente;
import com.caixeiro.model.Vendedor;
import com.caixeiro.util.heuristica.FuncoesVetor;
import com.caixeiro.util.jsf.FacesUtil;
import com.google.gson.Gson;

@Named
@ViewScoped
public class SequenciaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ClienteDAO clienteDAO;
	private List<Cliente> clientes;

	private DefaultMapModel advancedModel;
	private Marker marker;
	
	private String cliSequencia;
	
	private LatLngBounds bounds;
	private int zoomLevel = 12;
	private String centro = "-2.52296133692678,-44.14902015820314";
	private List<String> diaSemana = Arrays.asList("1- Domingo","2- Segunda-Feira", "3- Terça-feira", "4- Quarta-feira",
			"5- Quinta-feira", "6- Sexta-feira", "7- Sábado");
	private String diaSelecionado = "";
	
	private Vendedor vendedorSelecionado;
	
	private Double primeiroResultado;
	private Double melhorResultado;
	
	private boolean flagCaixeiro = false;

	public void init() {
		if (FacesUtil.isNotPostback()) {
			
			clientes = new ArrayList<Cliente>();
			advancedModel = new DefaultMapModel();
			
			Polyline polyline2 = new Polyline();
			polyline2.setStrokeColor("red");
			
			advancedModel.addOverlay(polyline2);
			
			//atualizarMapa();
		}
	}

	public void atualizarMapa() {
		
		
		//CRIAÇÃO DO LABEL NO MARKER
		//clientes = clienteDAO.todos();
		List<String> lista = new ArrayList<String>();
		for(Cliente cli : clientes) {
			lista.add( String.valueOf( cli.getSequencia() ));
		}
		cliSequencia = new Gson().toJson(lista);
		PrimeFaces.current().ajax().update("frmSequencia:cliSequencia");
		//ATUALIZA MAPA E DESENHA LABEL
		PrimeFaces.current().ajax().update("frmSequencia");
		PrimeFaces.current().executeScript("atualizaLabel();");
	}
	
	public void onStateChange(StateChangeEvent event) {
		//atualiza a visualização do mapa para ficar menos feio no UPDATE
		bounds = event.getBounds();
		zoomLevel = event.getZoomLevel();
		centro = event.getCenter().getLat() + "," + event.getCenter().getLng();
	}

	public void overlaySelect(OverlaySelectEvent event) {
		
		if( diaSelecionado.isEmpty() ){
			 FacesUtil.addErrorMessage("Selecione um Dia de Visita!");
			 return;
		 }
		
		if (event.getOverlay() instanceof Marker) {

			System.out.println("** EH MARKER");
			marker = (Marker) event.getOverlay();
			
			for (Marker m : advancedModel.getMarkers()) {
				if (marker.getId() == m.getId()) {//achou o marker selecionado
					if(flagCaixeiro) {
						Cliente cli = (Cliente) m.getData();
						int primeiro = clientes.indexOf(cli);
						System.out.println(primeiro + " ===== PRIMEIRO");
						auto(primeiro);
					}
					break;
				}
			}
			FacesUtil.addInfoMessage(marker.getTitle());

		} else if (event.getOverlay() instanceof Polygon) {
			System.out.println("** EH POLYGON"); 
			
		} else
			System.out.println("&& NAO SEI O QUE EH");
		
	}
	
	public void onPointSelect(PointSelectEvent event) {
		
		
	}
	
	
	public void filtrarClientes() {
		if(vendedorSelecionado != null) {
			if(diaSelecionado != null) {
				
				int diaSelecionadoIndex = diaSemana.indexOf(diaSelecionado)+1;
				clientes = clienteDAO.porVendedorDia(vendedorSelecionado,diaSelecionadoIndex);
				advancedModel = new DefaultMapModel();
				Polyline polyline2 = new Polyline();
				polyline2.setStrokeColor("red");
				advancedModel.addOverlay(polyline2);
				//== CRIAÇÃO MARKERS
				for (Cliente cli : clientes) {
					LatLng coord = new LatLng(cli.getLatitude(), cli.getLongitude());
					Marker marker = new Marker(coord, cli.getFantasia(), cli);
					advancedModel.addOverlay(marker);
				}
				atualizarMapa();
			}
		}else {
			clientes = new ArrayList<Cliente>();
			atualizarMapa();
			FacesUtil.addErrorMessage("Selecione um Vendedor.");
		}
	}
	
	public void atualizaDiaSelecionado(int linha) {
		
		diaSelecionado = diaSemana.get(linha);
		FacesUtil.addInfoMessage(diaSelecionado);
	}
	
	
	
	public void auto(int primeiroCliente) {
		
		
		Polyline polyline2 = advancedModel.getPolylines().get(0);
		polyline2.setPaths(new ArrayList<LatLng>());
		
		Double[][] matrizDouble;
		ControllerPCV controller;
		
		matrizDouble = Matriz.geraMatrizDouble(clientes);
		Matriz.printMatriz(matrizDouble);
		controller = new ControllerPCV(matrizDouble);
		controller.controlePCV(new HeuristicaVMP(), new VND(),primeiroCliente);
		
		System.out.println("primeiro resultado: "+controller.getPrimeiroResultado());
		System.out.println("melhor resultado: "+controller.getMelhorResultado());
		
		primeiroResultado = new Double(controller.getPrimeiroResultado());
		melhorResultado = new Double(controller.getMelhorResultado());
		
		int [] caminho = new int[controller.r.getMelhorSolucao().length];
		FuncoesVetor.copiarVetor(controller.r.getMelhorSolucao(), caminho);

		for(int i = 0; i<caminho.length; i++) {
			
			Cliente cli = clientes.get(caminho[i]);
			cli.setSequencia(i+1);
			clienteDAO.salvar(cli);
			
			LatLng coord = new LatLng(cli.getLatitude(), cli.getLongitude());
			polyline2.getPaths().add(coord);
		}
		
		int diaSelecionadoIndex = diaSemana.indexOf(diaSelecionado)+1;
		clientes = clienteDAO.porVendedorDia(vendedorSelecionado,diaSelecionadoIndex);
		
		advancedModel.addOverlay(polyline2);
		
		atualizarMapa();
	}
	
	public void setCaixeiro() {
		flagCaixeiro = true;
	}
	
//==================================
	
	public DefaultMapModel getAdvancedModel() {
		return advancedModel;
	}

	public void setAdvancedModel(DefaultMapModel advancedModel) {
		this.advancedModel = advancedModel;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public LatLngBounds getBounds() {
		return bounds;
	}

	public void setBounds(LatLngBounds bounds) {
		this.bounds = bounds;
	}

	public int getZoomLevel() {
		return zoomLevel;
	}

	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	public String getCentro() {
		return centro;
	}

	public void setCentro(String centro) {
		this.centro = centro;
	}

	public ClienteDAO getClienteDAO() {
		return clienteDAO;
	}

	public void setClienteDAO(ClienteDAO clienteDAO) {
		this.clienteDAO = clienteDAO;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public List<String> getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(List<String> diaSemana) {
		this.diaSemana = diaSemana;
	}

	public String getDiaSelecionado() {
		return diaSelecionado;
	}

	public void setDiaSelecionado(String diaSelecionado) {
		this.diaSelecionado = diaSelecionado;
	}

	public Vendedor getVendedorSelecionado() {
		return vendedorSelecionado;
	}

	public void setVendedorSelecionado(Vendedor vendedorSelecionado) {
		this.vendedorSelecionado = vendedorSelecionado;
	}

	public String getCliSequencia() {
		return cliSequencia;
	}

	public void setCliSequencia(String cliSequencia) {
		this.cliSequencia = cliSequencia;
	}

	public Double getPrimeiroResultado() {
		return primeiroResultado;
	}

	public void setPrimeiroResultado(Double primeiroResultado) {
		this.primeiroResultado = primeiroResultado;
	}

	public Double getMelhorResultado() {
		return melhorResultado;
	}

	public void setMelhorResultado(Double melhorResultado) {
		this.melhorResultado = melhorResultado;
	}

	public boolean isFlagCaixeiro() {
		return flagCaixeiro;
	}

	public void setFlagCaixeiro(boolean flagCaixeiro) {
		this.flagCaixeiro = flagCaixeiro;
	}

	
}