
package com.caixeiro.util.heuristica;

import com.caixeiro.heuristica.Rota;

/*******************************************************************************
 * @author Ednaldo                                                             *
 *  date: 26.10.2018                                                           *
 ******************************************************************************/
public interface HeuristicaRefinamento 
{
    void refinar(Rota r, Double [][] grafo, int [] rota);
}
