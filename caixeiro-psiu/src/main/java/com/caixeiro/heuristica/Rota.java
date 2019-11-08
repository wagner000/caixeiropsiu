
package com.caixeiro.heuristica;

import com.caixeiro.util.heuristica.FuncoesVetor;

/*******************************************************************************
 * @author Ednaldo                                                             *
 *  date: 23.10.2018                                                           *
 ******************************************************************************/
public class Rota 
{
    /**
     * melhorRota: guarda a posicao da melhor rota na matriz de solucoes
     * ultimaSolucao: guarda a posicao da ultima solucao na matriz de solucoes
     * melhorResultado: guarda o valor do calculo da melhor rota
     * melhorSolucao: guarda sequencia de cidades de melhor solucao
     * solucoes: matriz de solucoes
     */
    private int melhorRota;         
    private int ultimaSolucao;  
    private double melhorResultado; 
    private int [] solucaoInicial;
    private int [] solucaoRefinamento;
    private int [] melhorSolucao;
    private int [][] solucoes;
    
    public Rota(int numCidades)
    {
        this.melhorRota = -1;
        this.ultimaSolucao = -1;
        this.melhorResultado = Long.MAX_VALUE;
        this.solucaoInicial = new int [numCidades];
        this.solucaoRefinamento = new int [numCidades];
        this.melhorSolucao = new int [numCidades];
        this.solucoes = new int [(numCidades)*1000][numCidades];
    }

    public void addSolucao(int [] solucao)
    {
        this.ultimaSolucao++;
        for(int i = 0; i < solucao.length; i++)
            solucoes[ultimaSolucao][i] = solucao[i];
    }

    public int [] getMelhorRota() {
        int [] vetor = new int [solucoes[0].length];
        for(int i = 0; i < vetor.length; i++)
            vetor[i] = solucoes[melhorRota][i];
        return vetor;
    }
    
    //GETs 
    public int getNumMelhorRota() {
        return melhorRota;
    }

    public int[] getSolucao(int i) {
        return solucoes[i];
    }

    public int getUltimaSolucao() {
        return ultimaSolucao;
    }
    
    public double getMelhorResultado() {
        return melhorResultado;
    }

    public int[] getSolucaoInicial() {
        return solucaoInicial;
    }

    public int[] getSolucaoRefinamento() {
        return solucaoRefinamento;
    }

    public int[] getMelhorSolucao() {
        return melhorSolucao;
    }
    
    

    //SETs
    public void setNumMelhorRota(int melhorRota) {
        this.melhorRota = melhorRota;
    }

    public void setUltimaSolucao(int ultimaSolucao) {
        this.ultimaSolucao = ultimaSolucao;
    }

    public void setMelhorResultado(double resultado) {
        this.melhorResultado = resultado;
    }

    public void setSolucaoInicial(int[] solucaoInicial) {
        FuncoesVetor.copiarVetor( solucaoInicial, this.solucaoInicial);
    }

    public void setSolucaoRefinamento(int[] solucaoRefinamento) {
        FuncoesVetor.copiarVetor( solucaoRefinamento, this.solucaoRefinamento);
    }

    public void setMelhorSolucao(int[] melhorSolucao) {
        FuncoesVetor.copiarVetor( melhorSolucao, this.melhorSolucao);
    }
    
    public void setSolucoes(int[][] solucoes) {
        this.solucoes = solucoes;
    }
    
}
