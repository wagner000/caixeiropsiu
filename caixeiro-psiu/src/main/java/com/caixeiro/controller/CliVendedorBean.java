package com.caixeiro.controller;

import java.io.Serializable;
import java.util.ArrayList;
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
import com.caixeiro.dao.VendedorDAO;
import com.caixeiro.model.Cliente;
import com.caixeiro.model.Vendedor;
import com.caixeiro.util.jsf.FacesUtil;
import com.google.gson.Gson;

@Named
@ViewScoped
public class CliVendedorBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private VendedorDAO vendedorDAO;
	
	private Vendedor vendedorSelecionado;
	
	@Inject
	private ClienteDAO clienteDAO;
	private List<Cliente> clientes;
	
	private LatLngBounds bounds;
	private int zoomLevel = 12;
	private String centro = "-2.52296133692678,-44.14902015820314";
	
	private DefaultMapModel advancedModel;
	private Marker marker;
	private Polygon polygon;
	private String polyg;
	
	private String rotaClientes;
	
	public void init() {
		if(FacesUtil.isNotPostback()) {
			
			clientes = clienteDAO.todos();
			advancedModel = new DefaultMapModel();
			//CRIAÇÃO POLIGONO
			polygon = new Polygon();
			polygon.setStrokeColor("#FF9900");
	        polygon.setFillColor("#FF9900");
	        polygon.setStrokeOpacity(0.7);
	        polygon.setFillOpacity(0.7);
	        advancedModel.addOverlay(polygon);
	        
	      //== CRIAÇÃO MARKERS
			for (Cliente cli : clientes) {
				LatLng coord = new LatLng(cli.getLatitude(), cli.getLongitude());
				Marker marker = new Marker(coord, cli.getFantasia(), cli,cli.getIcon());
				advancedModel.addOverlay(marker);
			}
			
			atualizarMapa();
		}
	}
	
	
	public void atualizarMapa() {

		//limpa o poligono
		polygon.setPaths(new ArrayList<LatLng>()); 
		this.polyg = new Gson().toJson(polygon);
		PrimeFaces.current().ajax().update("frmCliVendedor:polygon");
		PrimeFaces.current().ajax().update("frmCliVendedor:gMap");
	}
	
	public Long qtdeClientes(Vendedor vendedor) {
		return vendedorDAO.qtdeCliente(vendedor);
	}
	
	public void vendedorSelecionadoMsg() {
		FacesUtil.addInfoMessage(vendedorSelecionado.getNome() +" Selecionado!");
	}
	
	public void onStateChange(StateChangeEvent event) {
		//atualiza a visualização do mapa para ficar menos feio no UPDATE
		bounds = event.getBounds();
		zoomLevel = event.getZoomLevel();
		centro = event.getCenter().getLat() + "," + event.getCenter().getLng();
	}
	
	public void overlaySelect(OverlaySelectEvent event) {
		
		if( vendedorSelecionado == null || vendedorSelecionado.getId() == null ){
			 FacesUtil.addErrorMessage("Selecione um Vendedor!");
			 return;
		 }
		
		if (event.getOverlay() instanceof Marker) {

			System.out.println("** EH MARKER");
			System.out.println("VENDEDOR ID: "+vendedorSelecionado.getId());
			vendedorDAO.qtdeCliente(vendedorSelecionado);
			
			marker = (Marker) event.getOverlay();
			
			Cliente cli = (Cliente) marker.getData();
			cli.setVendedor(vendedorSelecionado);
			cli = clienteDAO.salvar(cli);
			
			marker.setIcon(cli.getIcon());
			advancedModel.addOverlay(marker);
			
			atualizarMapa();
			
//			for (Marker m : advancedModel.getMarkers()) {
//				if (marker.getId() == m.getId()) {
//					
//					Cliente cli = (Cliente) marker.getData();
//					cli = clienteDAO.porId(cli.getId());
//					cli.setVendedor(vendedorSelecionado);
//					clienteDAO.salvar(cli);
//					clientes = clienteDAO.todos();
//					
//					marker.setIcon(cli.getIcon());
//					advancedModel.addOverlay(marker);
//					
//					atualizarMapa();
//					break;
//				}
//			}
			
			FacesUtil.addInfoMessage(marker.getTitle());

		} else if (event.getOverlay() instanceof Polygon) {
			System.out.println("** EH POLYGON"); 
			
			//atualiza a Gson do poligono e manda pro JS
			this.polyg = new Gson().toJson(polygon);
			PrimeFaces.current().ajax().update("frmCliVendedor:polygon");
			PrimeFaces.current().executeScript("inPolygon();");
			
		} else
			System.out.println("&& NAO SEI O QUE EH");
		return;
	}
	
	public void onPointSelect(PointSelectEvent event) {
		polygon.getPaths().add(event.getLatLng());
		this.polyg = new Gson().toJson(polygon);
		PrimeFaces.current().ajax().update("frmCliVendedor:polygon");
		//PrimeFaces.current().executeScript("atualizaLabel();");
	}
	
	public void atualizaMarkers() {
		Map<String, String> requestParamMap = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String selecionados = requestParamMap.get("inPolygon");
		
		if(selecionados.isEmpty()) {
			System.out.println("= NENHUM MARCADOR SELECIONADO =");
			//limpa o poligono
			polygon.setPaths(new ArrayList<LatLng>());
			PrimeFaces.current().ajax().update("frmCliVendedor:gMap");
			//PrimeFaces.current().executeScript("atualizaLabel();");
			
			return;
		}
			
		String[] idMarkers = selecionados.split(Pattern.quote(","));
		
		for(String id : idMarkers) {
			Marker marker = (Marker) advancedModel.findOverlay(id);
			Cliente cli = (Cliente) marker.getData();
			cli = clienteDAO.porId(cli.getId());
			cli.setVendedor(vendedorSelecionado);
			clienteDAO.salvar(cli);
			marker.setIcon(cli.getIcon());
		}
		
		clientes = clienteDAO.todos();
		atualizarMapa();
		FacesUtil.addInfoMessage(idMarkers.length+" Clientes Atualizados.");
	}
	
	
	//==================================================

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
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


	public String getRotaClientes() {
		return rotaClientes;
	}


	public void setRotaClientes(String cliVend) {
		this.rotaClientes = cliVend;
	}
	
	public Vendedor getVendedorSelecionado() {
		return vendedorSelecionado;
	}


	public void setVendedorSelecionado(Vendedor vendedorSelecionado) {
		this.vendedorSelecionado = vendedorSelecionado;
	}
	
}
