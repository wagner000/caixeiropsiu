/**
 * 
 */
package com.caixeiro.heuristica;

import java.util.ArrayList;
import java.util.List;

import com.caixeiro.model.Cliente;

/**
 * @author Wagner
 * 
 * Classe responsÃ¡vel por criar a matriz de adjacencia
 * apartir de um List de Ponto
 */
public class Matriz {

	
public static Celula[][] geraMatriz(List<Cliente> clientes){
		//System.out.println("==== GERAR MATRIZ =====");
		List<Ponto> pontos = new ArrayList<>();
		
		for (Cliente cli : clientes) {
			Ponto ponto = new Ponto(cli);
			//System.out.println("PONTO ID: "+clientes.indexOf(cli));
			ponto.setId(clientes.indexOf(cli));
			pontos.add(ponto);
		}
		
		Celula matriz[][] = new Celula[pontos.size()][pontos.size()];
		
		for (int i = 0; i < pontos.size(); i++) {
			for (int j = 0; j < pontos.size(); j++) {
				if(i != j) {
					matriz[i][j] = distanceCelula(pontos.get(i), pontos.get(j));
				}
				else {
					matriz[i][j] = new Celula(pontos.get(i), pontos.get(j), 0.0);
				}
			}
		}
		
		//imprimir
		for(int l=0; l < pontos.size(); l++ ) {
			for(int c = 0; c<pontos.size(); c++) {
				//System.out.print(matriz[l][c].getDistancia() +" | ");
			}
			//System.out.println("");
		}
		
		return matriz;
	}
	

	public static Double[][] geraMatrizDouble(List<Cliente> clientes){
		Double[][] matriz = new Double[clientes.size()][clientes.size()];
		
		for(Cliente cli : clientes) {
			
			for(int i = 0; i<clientes.size(); i++) {
				
				int linha = clientes.indexOf(cli);
				int coluna = i;
				
				if(linha != coluna) {
					matriz[linha][coluna] = distanceDouble(cli, clientes.get(coluna));
				}
				else {
					matriz[linha][coluna] = 0.0;
				}
				
				
			}
			
		}
		
		return matriz;
	}
	
	
	
	public static void printMatriz (Double[][] matriz)
    {
        System.out.println("\n\nMatriz. Tamanho: " + matriz.length );
        for(int i = 0; i < matriz.length; i++)
        {
            for(int j = 0; j < matriz.length; j++)
                System.out.print( " " +   matriz[i][j] );
            System.out.println();
        }
    }


/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::                                                                         :*/
/*::  This routine calculates the distance between two points (given the     :*/
/*::  latitude/longitude of those points). It is being used to calculate     :*/
/*::  the distance between two locations using GeoDataSource (TM) prodducts  :*/
/*::                                                                         :*/
/*::  Definitions:                                                           :*/
/*::    South latitudes are negative, east longitudes are positive           :*/
/*::                                                                         :*/
/*::  Passed to function:                                                    :*/
/*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
/*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
/*::    unit = the unit you desire for results                               :*/
/*::           where: 'M' is statute miles (default)                         :*/
/*::                  'K' is kilometers                                      :*/
/*::                  'N' is nautical miles                                  :*/
/*::  Worldwide cities and other features databases with latitude longitude  :*/
/*::  are available at http://www.geodatasource.com                          :*/
/*::                                                                         :*/
/*::  For enquiries, please contact sales@geodatasource.com                  :*/
/*::                                                                         :*/
/*::  Official Web site: http://www.geodatasource.com                        :*/
/*::                                                                         :*/
/*::           GeoDataSource.com (C) All Rights Reserved 2015                :*/
/*::                                                                         :*/
/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/

//class DistanceCalculator


private static Celula distanceCelula(Ponto origem, Ponto destino) {
	String unit = "K";
	
	double lat1 = origem.getLatitude();
	double lon1 = origem.getLongitude();
	double lat2 = destino.getLatitude();
	double lon2 = destino.getLongitude();
	
    double theta = lon1 - lon2;
    double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;
    if (unit == "K") {
        dist = dist * 1.609344;
    } else if (unit == "N") {
        dist = dist * 0.8684;
    }
    return new Celula(origem, destino, dist);
}

private static Double distanceDouble(Cliente origem, Cliente destino) {
	String unit = "K";
	
	double lat1 = origem.getLatitude();
	double lon1 = origem.getLongitude();
	double lat2 = destino.getLatitude();
	double lon2 = destino.getLongitude();
	
    double theta = lon1 - lon2;
    double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;
    if (unit == "K") {
        dist = dist * 1.609344;
    } else if (unit == "N") {
        dist = dist * 0.8684;
    }
    return dist;
}

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::    This function converts decimal degrees to radians                         :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
private static double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
}

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::    This function converts radians to decimal degrees                         :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
private static double rad2deg(double rad) {
    return (rad * 180 / Math.PI);
}



	
}
