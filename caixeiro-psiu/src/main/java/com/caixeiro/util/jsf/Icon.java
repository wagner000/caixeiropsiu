package com.caixeiro.util.jsf;

public enum Icon {

	AZUL("http://maps.google.com/mapfiles/kml/paddle/blu-stars_maps.png"),
	BRANCO("http://maps.google.com/mapfiles/kml/paddle/wht-blank_maps.png"),
	AMARELO("http://maps.google.com/mapfiles/kml/paddle/ylw-stars_maps.png"),
	VERMELHO("http://maps.google.com/mapfiles/kml/paddle/red-stars_maps.png"),
	VERDE("http://maps.google.com/mapfiles/kml/paddle/grn-stars_maps.png"),
	ROXO("http://maps.google.com/mapfiles/kml/paddle/pink-stars_maps.png");
	
private String url;
	
	Icon(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
	
}
