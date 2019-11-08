
package com.caixeiro.heuristica;

/*******************************************************************************
 * @author Ednaldo                                                             *
 *  date: 30.10.2018                                                           *
 ******************************************************************************/
public abstract class EstruturaVizinhanca 
{
    //STATE
    public abstract EstruturaVizinhanca explorar(Rota r, Double[][] grafo, int[] novaRota, int[] melhorRota);
            
    protected void buildMelhorSolucao(Rota r, Double [][] grafo, int [] melhorRota, double resultado)
    {
        r.addSolucao(melhorRota);
        r.setMelhorResultado(resultado);
        r.setNumMelhorRota(r.getUltimaSolucao());
        r.setMelhorSolucao(melhorRota);
    }
    
    protected double calcularResultado (Double [][] grafo, int [] vetorSCV)
    {
        double calculo = 0;
        for(int i = 0; i < vetorSCV.length-1; i++) 
            calculo += grafo[vetorSCV[i]][vetorSCV[i+1]];
        return calculo;
    }
}
