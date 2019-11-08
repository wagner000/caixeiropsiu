
package com.caixeiro.util.heuristica;

/*******************************************************************************
 * @author Ednaldo                                                             *
 *  date: 24.09.2018                                                           *
 ******************************************************************************/
public class FuncoesVetor {
    
    
    
    public static void iniciarVetor(int[] vetor) 
    {
        for(int i = 0; i < vetor.length; i++)
            vetor[i] = -1;
    }
    
    
    public static void iniciarMatriz(double[][] matriz)
    {
        for(int i = 0; i < matriz.length; i++)
            for(int j = 0; j < matriz.length; j++)
                matriz[i][j] = 0;
    }
    
    public static void copiarVetor(int[] vetor, int [] copia) 
    {
    	
        for(int i = 0; i < vetor.length; i++)
            copia[i] = vetor[i];
    }
    
}
