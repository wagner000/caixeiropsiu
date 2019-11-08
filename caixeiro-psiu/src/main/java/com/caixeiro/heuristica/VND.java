
package com.caixeiro.heuristica;

import com.caixeiro.util.heuristica.FuncoesVetor;
import com.caixeiro.util.heuristica.HeuristicaRefinamento;
import com.caixeiro.util.heuristica.Print;

/*******************************************************************************
 * @author Ednaldo                                                             *
 *  date: 26.10.2018                                                           *
 ******************************************************************************/
public class VND implements HeuristicaRefinamento
{
    EstruturaVizinhanca ev;
    /**
     * VND: Variable Neighborhood Descent
     * DVV: Descida em Vizinhança Variavel
     * @param r
     * @param grafo
     * @param rota
     */
    @Override
    public void refinar(Rota r, Double[][] grafo, int[] rota)
    {
        int [] melhorRota = new int[rota.length];
        
        
        FuncoesVetor.copiarVetor(rota, melhorRota);
        
        ev = new Swap();
        while(ev != null) {
        	ev = ev.explorar(r, grafo, rota, melhorRota);
        }
            
       
        if(r.getNumMelhorRota() == -1) {//gambi
        	 
        	
        	
        	r.addSolucao(melhorRota);
            
            r.setNumMelhorRota(r.getUltimaSolucao());
            r.setMelhorSolucao(melhorRota);
        }else {
        	FuncoesVetor.copiarVetor(r.getMelhorRota(), rota);
        }
        
    }

    
    
}
