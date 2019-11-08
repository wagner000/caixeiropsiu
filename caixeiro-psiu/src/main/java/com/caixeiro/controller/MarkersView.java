package com.caixeiro.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polyline;

import com.caixeiro.dao.ClienteDAO;
import com.caixeiro.heuristica.Celula;
import com.caixeiro.heuristica.ControllerPCV;
import com.caixeiro.heuristica.Heuristica;
import com.caixeiro.heuristica.HeuristicaVMP;
import com.caixeiro.heuristica.Matriz;
import com.caixeiro.heuristica.Ponto;
import com.caixeiro.heuristica.VND;
import com.caixeiro.model.Cliente;
import com.caixeiro.util.heuristica.FuncoesVetor;
import com.caixeiro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class MarkersView implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ClienteDAO clienteDAO;
	private MapModel simpleModel;
	private List<Cliente> clientes;
	private Celula[][] matriz;
	
	private Double[][] matrizDouble;
	
	private Ponto[] caminho;
	private int[] caminho2;
	
	ControllerPCV controller;
	
  
	public void init() {
        
    	if(FacesUtil.isNotPostback()) {
			clientes = clienteDAO.todos();
			
			
			
			matriz = Matriz.geraMatriz(clientes);
			
			matrizDouble = Matriz.geraMatrizDouble(clientes);
			
			controller = new ControllerPCV(matrizDouble);
			//controller.controlePCV(new HeuristicaVMP(), new VND());
			
			
			//Print.printResultado(matrizDouble, caminho2, "RESULTADO");
			//Print.printVetor(caminho2, "CAMINHO");
			
			//caminho2 = new int[controller.r.getMelhorRota().length];
			//FuncoesVetor.copiarVetor(controller.r.getMelhorRota(), caminho2);
			
			Heuristica guloso = new Heuristica();
			caminho = guloso.guloso(matriz);
			carregarMapa();
			//desenharCaminho();
			
		}
    }
    
    public void carregarMapa(){
    	simpleModel = new DefaultMapModel();
    	
    	for(Cliente cli : clientes) {
    		LatLng coord = new LatLng(cli.getLatitude(), cli.getLongitude());
    		simpleModel.addOverlay(new Marker(coord, cli.getFantasia(),cli));
    	}
    }
    
    public void desenharCaminho() {
    	int[] inicial = new int[clientes.size()];
    	int[] melhor = new int[clientes.size()];
    	FuncoesVetor.copiarVetor(controller.r.getSolucaoInicial(), inicial );
    	FuncoesVetor.copiarVetor(controller.r.getMelhorRota(), melhor);
    	
    	Polyline polyline = new Polyline();
    	//polyline.setStrokeColor("black");
    	for(int i=0; i<inicial.length; i++) {
    		
    		Cliente cli = clientes.get(inicial[i]);
    		
    		LatLng coord = new LatLng(cli.getLatitude(), cli.getLongitude());
    		polyline.getPaths().add(coord);
    	}
    	simpleModel.addOverlay(polyline);
    	
    	
    	Polyline polyline2 = new Polyline();
    	polyline2.setStrokeColor("red");
    	for(int i=0; i<melhor.length; i++) {
    		
    		Cliente cli = clientes.get(melhor[i]);
    		
    		LatLng coord = new LatLng(cli.getLatitude(), cli.getLongitude());
    		polyline2.getPaths().add(coord);
    	}
    	simpleModel.addOverlay(polyline2);
    	
    }
    
    
    public void onMarkerSelect(OverlaySelectEvent event) {
        Marker marker = (Marker) event.getOverlay();
        Cliente cli = (Cliente) marker.getData();
        
        int index = clientes.indexOf(cli);
        controller.controlePCV(new HeuristicaVMP(), new VND(),index);
        desenharCaminho();
        
        FacesUtil.addInfoMessage("INDEX: "+index);
        System.out.println("cli selecionado: "+clientes.get(index).getFantasia());
    }
    
    
  //=================
    
    public MapModel getSimpleModel() {
        return simpleModel;
    }

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public Ponto[] getCaminho() {
		return caminho;
	}

	public void setCaminho(Ponto[] caminho) {
		this.caminho = caminho;
	}
	
	public Celula[][] getMatriz() {
		return matriz;
	}

	public void setMatriz(Celula[][] matriz) {
		this.matriz = matriz;
	}

	public Double[][] getMatrizDouble() {
		return matrizDouble;
	}

	public void setMatrizDouble(Double[][] matrizDouble) {
		this.matrizDouble = matrizDouble;
	}

	public int[] getCaminho2() {
		return caminho2;
	}

	public void setCaminho2(int[] caminho2) {
		this.caminho2 = caminho2;
	}
	
	
}