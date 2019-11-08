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
import org.primefaces.model.map.Polyline;

import com.caixeiro.dao.ClienteDAO;
import com.caixeiro.dao.VendedorDAO;
import com.caixeiro.heuristica.ControllerPCV;
import com.caixeiro.heuristica.HeuristicaVMP;
import com.caixeiro.heuristica.Matriz;
import com.caixeiro.heuristica.VND;
import com.caixeiro.model.Cliente;
import com.caixeiro.model.Vendedor;
import com.caixeiro.util.heuristica.FuncoesVetor;
import com.caixeiro.util.heuristica.Print;
import com.caixeiro.util.jsf.FacesUtil;
import com.google.gson.Gson;

@Named
@ViewScoped
public class CliVendedorAutoBean implements Serializable {

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
	//===
	private int[] caminho2;
	
	public void init() {
		if(FacesUtil.isNotPostback()) {
			
			vendedorSelecionado = new Vendedor();
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
				Marker marker = new Marker(coord, cli.getFantasia(), cli);
				advancedModel.addOverlay(marker);
			}
			//auto();
			atualizarMapa();
			
		}
	}
	
	
	public void atualizarMapa() {
		//limpa o poligono
		polygon.setPaths(new ArrayList<LatLng>()); 
		this.polyg = new Gson().toJson(polygon);
		PrimeFaces.current().ajax().update("frmCliVendedor:polygon");
		
		//CRIAÇÃO DO LABEL NO MARKER
		//clientes = clienteDAO.todos();
		List<String> lista = new ArrayList<String>();
		for(Cliente cli : clientes) {
			if(cli.getVendedor() != null) {
				lista.add(cli.getVendedor().getId().toString());
			}else
				lista.add("X");
		}
		this.rotaClientes = new Gson().toJson(lista);
		PrimeFaces.current().ajax().update("frmCliVendedor:rotaClientes");
		//ATUALIZA MAPA E DESENHA LABEL
		PrimeFaces.current().ajax().update("frmCliVendedor:gMap");
		PrimeFaces.current().executeScript("atualizaLabel();");
	}
	
	public void vendedorSelecionadoMsg() {
		
		FacesUtil.addInfoMessage(vendedorSelecionado.getNome());
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
			marker = (Marker) event.getOverlay();
			
			for (Marker m : advancedModel.getMarkers()) {
				if (marker.getId() == m.getId()) {
					
					Cliente cli = (Cliente) marker.getData();
					cli = clienteDAO.porId(cli.getId());
					cli.setVendedor(vendedorSelecionado);
					clienteDAO.salvar(cli);
					clientes = clienteDAO.todos();
					atualizarMapa();
					break;
				}
			}
			
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
		PrimeFaces.current().executeScript("atualizaLabel();");
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
			PrimeFaces.current().executeScript("atualizaLabel();");
			
			return;
		}
			
		String[] idMarkers = selecionados.split(Pattern.quote(","));
		
		for(String id : idMarkers) {
			Marker marker = (Marker) advancedModel.findOverlay(id);
			Cliente cli = (Cliente) marker.getData();
			cli = clienteDAO.porId(cli.getId());
			cli.setVendedor(vendedorSelecionado);
			clienteDAO.salvar(cli);
		}
		
		clientes = clienteDAO.todos();
		atualizarMapa();
		FacesUtil.addInfoMessage(idMarkers.length+" Clientes Atualizados.");
	}
	
	//==================================================
	
	public void auto() {
		
		for(Cliente cli : clientes) {
			cli.setVendedor(null);
			clienteDAO.salvar(cli);
		}
		clientes = clienteDAO.todos();
		//=== limpei todos os vendedores/clientes
		
		List<Vendedor> vendedores = vendedorDAO.todos();
		
		Double[][] matrizDouble;
		ControllerPCV controller;
		
		matrizDouble = Matriz.geraMatrizDouble(clientes);
		
		controller = new ControllerPCV(matrizDouble);
		controller.controlePCV(new HeuristicaVMP(), new VND());
		
		caminho2 = new int[controller.r.getMelhorRota().length];
		FuncoesVetor.copiarVetor(controller.r.getMelhorRota(), caminho2);
		Print.printVetor(caminho2, "CAMINHO");
		int qtdeClienteVendedor = clientes.size()/vendedores.size();
		int contVendedor = 0;
		int totalClientes = 0;

		for (int i = 0; i < clientes.size(); i++) {
			if (totalClientes <= qtdeClienteVendedor) {
				clientes.get(caminho2[i]).setVendedor(vendedores.get(contVendedor));
				clienteDAO.salvar(clientes.get(caminho2[i]));
				totalClientes++;
			} else {
				contVendedor++;
				totalClientes = 0;
				clientes.get(caminho2[i]).setVendedor(vendedores.get(contVendedor));
				clienteDAO.salvar(clientes.get(caminho2[i]));
			}
		}
		 clientes = clienteDAO.todos();
		 atualizarMapa();
	}
	
	public void desenharCaminho() {
    	
    	Polyline polyline2 = new Polyline();
    	polyline2.setStrokeColor("red");
    	for(int i=0; i<caminho2.length; i++) {
    		
    		Cliente cli = clientes.get(caminho2[i]);
    		
    		LatLng coord = new LatLng(cli.getLatitude(), cli.getLongitude());
    		polyline2.getPaths().add(coord);
    	}
    	advancedModel.addOverlay(polyline2);
    	
    }
	
	//==================================================
	public Vendedor getVendedorSelecionado() {
		return vendedorSelecionado;
	}

	public void setVendedorSelecionado(Vendedor filtroVendedor) {
		this.vendedorSelecionado = filtroVendedor;
	}



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
	
}
