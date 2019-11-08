/**
 * 
 */
package com.caixeiro.heuristica;

import java.util.Arrays;

/**
 * @author Wagner
 * IMPLEMENTAÇÕES PARA ESCOLHA DO MELHOR CAMINHO
 */
public class Heuristica {

	public Ponto[] guloso(Celula[][] matriz) { //recebe matriz de distancias
		
		/*Ponto[] ponto = new Ponto[matriz.length];
		
		matriz[0][0].getOrigem().setUsado(true);
		matriz[0][0].getOrigem().setOrigem(true);
		
		Ponto proximo = proximoPontoGuloso(matriz, matriz[0][0].getOrigem());
		//Ponto proximo = matriz[0][0].getOrigem();
		ponto[0] = proximo;
		
		for(int i = 0; i < matriz.length - 1; i++){
			proximo = proximoPontoGuloso(matriz, proximo);
			ponto[i+1] = proximo;
		}
		return ponto;*/
		
		
		Ponto[] caminho = new Ponto[matriz.length];
		
		Ponto origem = matriz[0][0].getOrigem();
		origem.setUsado(true);
		origem.setOrigem(true);
		caminho[0] = origem;
		
		for (int i = 1; i<matriz.length; i++) {
			origem = proximoPontoGuloso(matriz, origem);
			caminho[i] = origem;
		}
		
		return caminho;
	}
	
	
public Ponto proximoPontoGuloso(Celula[][] matriz, Ponto origem){
		
		Ponto destino = null;
		
		Celula[] celulas = matriz[origem.getId()]; //aqui tinha -1
		Arrays.sort(celulas);
		
		for (int i = 0; i < celulas.length; i++) {
			if(celulas[i].getDistancia() != 0 && !celulas[i].getDestino().isOrigem() && !celulas[i].getDestino().isUsado()){
				destino = celulas[i].getDestino();
				celulas[i].getDestino().setUsado(true);	
				break;
			}
		}
		
		if(destino != null){
			return destino;			
		}
		else {
			
			for (int i = 0; i < celulas.length; i++) {
				if(celulas[i].getDestino().isOrigem()){
					return celulas[i].getDestino();					
				}
			}
		}			
		
		return null;
	}

	
	
}
