
package com.caixeiro.heuristica;

import com.caixeiro.util.heuristica.FuncoesVetor;

/*******************************************************************************
 * @author Ednaldo                                                             *
 *  date 24.10.2018                                                            *
 ******************************************************************************/
public class HeuristicaVMP 
{
    int contRotas[];
    int inicio;
    int[] vetorSCV;
    /*
    VMP: Vizinho mais proximo
    SCV: Solucao do caixeiro viajante
    */
    //nova funcao simplificada
    public void solucionarPCV(Double[][] matriz, int[] vetorSCV)
    {
        //vetorSCV = new int[matriz.length+1];
    	FuncoesVetor.iniciarVetor(vetorSCV);
    	//contador de rotas por cidade e ponto de partida
        contRotas = new int[matriz.length];
        inicio = 0;
        int cidadeAtual = inicio, proximaCidade = -1, n = 0;
        vetorSCV[0] = inicio;
        contRotas[inicio]++;
        //enquanto o ponto inicial nao for igual ao final:
        //while(!isHamiltoniano(vetorSCV))
        for(int i = 0; i<matriz.length; i++) 
        {
            double menor = Long.MAX_VALUE;
            proximaCidade = -1;
            for(int j = 0; j < matriz.length; j++)
            {
                if( matriz[cidadeAtual][j]!= 0 && matriz[cidadeAtual][j] < menor 
                    && contRotas[j] < 2 && j != inicio)
                {
                    proximaCidade = j;
                    menor = matriz[cidadeAtual][proximaCidade];
                }
            }
            
            if(proximaCidade != -1)
            {   //teoricamente encontrou uma proxima rota
                addCidade(matriz, vetorSCV, cidadeAtual, proximaCidade, ++n);
                cidadeAtual = proximaCidade;
            }
        }
        
        //return vetorSCV; se hamilton
        //return retirarUltimo(vetorSCV);
    }
    
    
    
    public void solucionarPCV(Double[][] matriz, int[] vetorSCV, int primeiro)
    {
        //vetorSCV = new int[matriz.length+1];
    	FuncoesVetor.iniciarVetor(vetorSCV);
    	//contador de rotas por cidade e ponto de partida
        contRotas = new int[matriz.length];
        this.inicio = primeiro;
        int cidadeAtual = inicio, proximaCidade = -1, n = 0;
        vetorSCV[0] = inicio;
        contRotas[inicio]++;
        //enquanto o ponto inicial nao for igual ao final:
        //while(!isHamiltoniano(vetorSCV))
        for(int i = 0; i<matriz.length; i++) 
        {
            double menor = Long.MAX_VALUE;
            proximaCidade = -1;
            for(int j = 0; j < matriz.length; j++)
            {
                if( matriz[cidadeAtual][j]!= 0 && matriz[cidadeAtual][j] < menor 
                    && contRotas[j] < 2 && j != inicio)
                {
                    proximaCidade = j;
                    menor = matriz[cidadeAtual][proximaCidade];
                }
            }
            
            if(proximaCidade != -1)
            {   //teoricamente encontrou uma proxima rota
                addCidade(matriz, vetorSCV, cidadeAtual, proximaCidade, ++n);
                cidadeAtual = proximaCidade;
            }
        }
        
        //return vetorSCV; se hamilton
        //return retirarUltimo(vetorSCV);
    }
    
    
    
    private void addCidade( Double [][] matriz, int [] vetor, int i, int j, int pos)
    {
        vetor[pos] = j;
        contRotas[i]++;
        contRotas[j]++;
        //vetor[pos+1] = inicio;
    }
    
    //contador simplificado usando vetor
    private boolean isHamiltoniano( int [] vetor)
    {
        return (vetor[vetor.length-1] == vetor[0]);
    }
    
}
