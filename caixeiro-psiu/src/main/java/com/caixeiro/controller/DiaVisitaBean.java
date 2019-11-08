package com.caixeiro.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
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

import com.caixeiro.dao.ClienteDAO;
import com.caixeiro.model.Cliente;
import com.caixeiro.model.Vendedor;
import com.caixeiro.util.jsf.FacesUtil;
import com.google.gson.Gson;

@Named
@ViewScoped
public class DiaVisitaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ClienteDAO clienteDAO;
	private List<Cliente> clientes;

	private DefaultMapModel advancedModel;
	private Marker marker;
	private Polygon polygon;
	
	
	private String polyg;
	
	private LatLngBounds bounds;
	private int zoomLevel = 12;
	private String centro = "-2.52296133692678,-44.14902015820314";
	private List<String> diaSemana = Arrays.asList("1- Domingo","2- Segunda-Feira", "3- Terça-feira", "4- Quarta-feira",
			"5- Quinta-feira", "6- Sexta-feira", "7- Sábado");
	private String diaSelecionado = "";
	
	private Vendedor vendedorSelecionado;

	public void init() {
		if (FacesUtil.isNotPostback()) {
			
			clientes = new ArrayList<Cliente>();
			carregarMapa();
		}
	}

	public void carregarMapa() {
		advancedModel = new DefaultMapModel();

		polygon = new Polygon();
		polygon.setStrokeColor("#FF9900");
        polygon.setFillColor("#FF9900");
        polygon.setStrokeOpacity(0.7);
        polygon.setFillOpacity(0.7);
        
        advancedModel.addOverlay(polygon);
        
        this.polyg = new Gson().toJson(polygon);
		PrimeFaces.current().ajax().update("frmDiaVisita:polygon");
        
        
		
		for (Cliente cli : clientes) {
			LatLng coord = new LatLng(cli.getLatitude(), cli.getLongitude());
			String icon;
			
			if(cli.getDiaSemana()!=0) {
				icon = "http://maps.google.com/mapfiles/kml/paddle/"+cli.getDiaSemana()+"_maps.png";
			}else
				icon = "http://maps.google.com/mapfiles/kml/paddle/X_maps.png";
			
			Marker marker = new Marker(coord, cli.getFantasia(), cli,icon);
			advancedModel.addOverlay(marker);
		}
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
				if (marker.getId() == m.getId()) {
					iconMarker(m);
					break;
				}
			}
			FacesUtil.addInfoMessage(marker.getTitle());

		} else if (event.getOverlay() instanceof Polygon) {
			System.out.println("** EH POLYGON"); 
			
			//atualiza a Gson do poligono e manda pro JS
			this.polyg = new Gson().toJson(polygon);
			PrimeFaces.current().ajax().update("frmDiaVisita:polygon");
			PrimeFaces.current().executeScript("inPolygon();");
			
		} else
			System.out.println("&& NAO SEI O QUE EH");
		return;
	}
	
	public void onPointSelect(PointSelectEvent event) {
		polygon.getPaths().add(event.getLatLng());
		this.polyg = new Gson().toJson(polygon);
		PrimeFaces.current().ajax().update("frmDiaVisita:polygon");
	}
	
	
	public void iconMarker(Marker mark) { //coloca o icone correto a partir do dia selecionado
		 if( diaSelecionado.isEmpty() ){
			 FacesUtil.addErrorMessage("Selecione um Dia de Visita!");
			 return;
		 }
		 
		 Cliente cli = (Cliente) mark.getData();
			
		 if(diaSelecionado.equalsIgnoreCase("2- Segunda-Feira")) {
			 mark.setIcon("http://maps.google.com/mapfiles/kml/paddle/2_maps.png");
			 
			 cli.setDiaSemana(2);
			 clienteDAO.salvar(cli);
			 return;
		 }
		 if(diaSelecionado.equalsIgnoreCase("3- Terça-feira")) {
			 mark.setIcon("http://maps.google.com/mapfiles/kml/paddle/3_maps.png");
			 cli.setDiaSemana(3);
			 clienteDAO.salvar(cli);
			 return;
		 }
		 if(diaSelecionado.equalsIgnoreCase("4- Quarta-feira")) {
			 mark.setIcon("http://maps.google.com/mapfiles/kml/paddle/4_maps.png");
			 cli.setDiaSemana(4);
			 clienteDAO.salvar(cli);
			 return;
		 }
		 if(diaSelecionado.equalsIgnoreCase("5- Quinta-feira")) {
			 mark.setIcon("http://maps.google.com/mapfiles/kml/paddle/5_maps.png");
			 cli.setDiaSemana(5);
			 clienteDAO.salvar(cli);
			 return;
		 }
		 if(diaSelecionado.equalsIgnoreCase("6- Sexta-feira")) {
			 mark.setIcon("http://maps.google.com/mapfiles/kml/paddle/6_maps.png");
			 cli.setDiaSemana(6);
			 clienteDAO.salvar(cli);
			 return;
		 }
		 if(diaSelecionado.equalsIgnoreCase("7- Sábado")) {
			 mark.setIcon("http://maps.google.com/mapfiles/kml/paddle/7_maps.png");
			 cli.setDiaSemana(7);
			 clienteDAO.salvar(cli);
			 return;
		 }
		 if(diaSelecionado.equalsIgnoreCase("1- Domingo")) {
			 mark.setIcon("http://maps.google.com/mapfiles/kml/paddle/1_maps.png");
			 cli.setDiaSemana(1);
			 clienteDAO.salvar(cli);
			 return;
		 }
		
	}
	
	
	public void atualizaMarkers() {
		Map<String, String> requestParamMap = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String selecionados = requestParamMap.get("inPolygon");
		
		if(selecionados.isEmpty()) {
			System.out.println("= NENHUM MARCADOR SELECIONADO =");
			polygon.setPaths(new ArrayList<LatLng>());
			PrimeFaces.current().ajax().update("frmDiaVisita:gMap");
			return;
		}
			
		String[] idMarkers = selecionados.split(Pattern.quote(","));
		
		for(String id : idMarkers) {
			Marker marker = (Marker) advancedModel.findOverlay(id);
			//marker.setIcon("https://maps.google.com/mapfiles/ms/micons/yellow-dot.png");
			iconMarker(marker);
		}
		
		//limpa o polygon
		polygon.setPaths(new ArrayList<LatLng>());
		PrimeFaces.current().ajax().update("frmDiaVisita:growl");
		PrimeFaces.current().ajax().update("frmDiaVisita:gMap");
		FacesUtil.addInfoMessage(idMarkers.length+" Clientes Atualizados.");
		
	}
	
	public void filtrarClientes() {
		if(vendedorSelecionado != null) {
			clientes = clienteDAO.porVendedor(vendedorSelecionado);
			carregarMapa();
		}else {
			clientes = new ArrayList<Cliente>();
			carregarMapa();
			FacesUtil.addErrorMessage("Selecione um Vendedor.");
		}
			
		
		
		
	}
	
	public void atualizaDiaSelecionado(int linha) {
		
		diaSelecionado = diaSemana.get(linha);
		FacesUtil.addInfoMessage(diaSelecionado);
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

	public Polygon getPolygon() {
		return polygon;
	}

	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}

	public String getPolyg() {
		return polyg;
	}

	public void setPolyg(String polyg) {
		this.polyg = polyg;
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

	
}