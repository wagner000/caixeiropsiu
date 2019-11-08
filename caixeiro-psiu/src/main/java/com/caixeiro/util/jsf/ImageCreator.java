package com.caixeiro.util.jsf;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

public class ImageCreator {
	
	public static void criarIcon(String texto) throws Exception
	{		

		try
		{
			BufferedImage imagem = ImageIO.read(new File(urlArquivoBase()));
			Graphics g = imagem.createGraphics();
					
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial Regular", Font.BOLD, 13));  

			if(texto.length() ==1) {
				g.drawString("00"+texto, 12, 17);
			}else if(texto.length() ==2) {
				g.drawString("0"+texto, 12, 17);
			}else {
				g.drawString(texto, 12, 17);
			}
			g.dispose();
			
			ImageIO.write(imagem, "PNG", new File(urlArquivo(texto)));
					
		}
		catch (Exception e)
		{
			throw e;
		}
	}
	
	private static String urlArquivo(String texto) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ServletContext scontext = (ServletContext) facesContext.getExternalContext().getContext();
		String url = scontext.getRealPath("/resources/icons/" );
		return url+"icon"+texto+".png";
	}
	
	private static String urlArquivoBase() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ServletContext scontext = (ServletContext) facesContext.getExternalContext().getContext();
		String url = scontext.getRealPath("/resources/icons/" );
		return url+"icon.png";
	}
}
