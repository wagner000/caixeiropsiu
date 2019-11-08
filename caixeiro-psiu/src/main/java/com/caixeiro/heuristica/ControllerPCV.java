
package com.caixeiro.heuristica;

import com.caixeiro.util.heuristica.FuncoesVetor;
import com.caixeiro.util.heuristica.HeuristicaRefinamento;
import com.caixeiro.util.heuristica.Print;


/*******************************************************************************
 * @author Ednaldo                                                             *
 *  date: 24.10.2018                                                           *
 ******************************************************************************/
public class ControllerPCV 
{
    //private final String ARQUIVO_ORIGEM = "..\\caixeiro\\instancias\\pcv7_29.txt";
    private final Double[][] matriz;
    public Rota r;          //Objeto Rota guarda varias informacoes sobre rotas
    public int [] vetorSCV;    //primeira solucao

    
    
    public ControllerPCV(Double[][] matrizDouble) 
    {
        this.matriz = matrizDouble;
        this.r = new Rota(matriz.length);
    }
    
    public void controlePCV (HeuristicaVMP heuristica, HeuristicaRefinamento refinamento)
    {
        vetorSCV = new int[matriz.length];
        this.r = new Rota(matriz.length);
        FuncoesVetor.iniciarVetor(vetorSCV);
        heuristica.solucionarPCV(matriz, vetorSCV);
        r.setSolucaoInicial(vetorSCV);
        r.addSolucao(vetorSCV);
        refinamento.refinar(r, matriz, vetorSCV);
        r.setSolucaoRefinamento(vetorSCV);
        
        
        
        Print.printResultado(matriz, r.getSolucaoInicial(), "INICIAL");
        
        Print.printResultado(matriz, r.getMelhorSolucao(), "MELHOR");
    }
    
    public void controlePCV (HeuristicaVMP heuristica, HeuristicaRefinamento refinamento, int inicio) {
    	
    	vetorSCV = new int[matriz.length];
    	this.r = new Rota(matriz.length);
        FuncoesVetor.iniciarVetor(vetorSCV);
        heuristica.solucionarPCV(matriz, vetorSCV, inicio);
        r.setSolucaoInicial(vetorSCV);
        r.addSolucao(vetorSCV);
        refinamento.refinar(r, matriz, vetorSCV);
        r.setSolucaoRefinamento(vetorSCV);
        
        
        System.out.println("RESULTADO: "+r.getMelhorResultado());
        
        Print.printResultado(matriz, r.getSolucaoInicial(), "INICIAL");
        
        Print.printResultado(matriz, r.getMelhorSolucao(), "MELHOR");
    	
    }
    
    public double getMelhorResultado() {
    	return r.getMelhorResultado();
    }
    
    public double getPrimeiroResultado() {
    	return resultado(matriz, r.getSolucaoInicial());
    }
    
    public static double resultado (Double [][] matriz, int [] scv)
    {
        double resultado = 0;
        for(int i = 0; i < scv.length-1; i++)
        {
            resultado += matriz[scv[i]][scv[i+1]];
        }
        return resultado;
    }
    
}
