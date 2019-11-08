
package com.caixeiro.heuristica;

import com.caixeiro.util.heuristica.FuncoesVetor;

/*******************************************************************************
 * @author Ednaldo                                                             *
 *  date: 03.11.2018                                                           *
 ******************************************************************************/
public class Reinsertion extends EstruturaVizinhanca 
{
    @Override
    public EstruturaVizinhanca explorar(Rota r, Double[][] grafo, int[] novaRota, int[] melhorRota) 
    {
        double resultadoInicial = r.getMelhorResultado(), melhorResultado = resultadoInicial;
        for(int i = 1; i < novaRota.length-2; i++)
        {
            double novoResultado = 0;
            for(int j = 1; j < novaRota.length-1; j++)
            {
                insert(novaRota, i, j);
                novoResultado = calcularResultado(grafo, novaRota);
                if(i!=j && novoResultado < melhorResultado)
                {
                    melhorResultado = novoResultado;
                    FuncoesVetor.copiarVetor(novaRota, melhorRota);
                }
                insert(novaRota, j, i);
            }
            if(melhorResultado < r.getMelhorResultado())
            {
                buildMelhorSolucao(r, grafo, melhorRota, melhorResultado);
                FuncoesVetor.copiarVetor(melhorRota, novaRota);
            }
        }
        return (melhorResultado < resultadoInicial) ? new Swap(): null;
    }
 
    private void insert (int [] list, int i, int j)
    {
        int temp = list[i];
        if( i < j )
            while(i < j)
                list[i] = list[++i];
        else
            while(i > j)
                list[i] = list[--i];
        list[j] = temp;
    }
}
