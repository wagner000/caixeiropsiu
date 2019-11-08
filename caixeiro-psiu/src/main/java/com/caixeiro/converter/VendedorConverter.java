package com.caixeiro.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.caixeiro.dao.VendedorDAO;
import com.caixeiro.model.Vendedor;

@FacesConverter(forClass = Vendedor.class)
public class VendedorConverter implements Converter {
	
	@Inject
	private VendedorDAO vendedorDAO;
	
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		Vendedor e = null;
		if(StringUtils.isNotEmpty(arg2)) {

			e = this.vendedorDAO.porId(new Long(arg2));
		}
		return e;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		if (arg2 != null) {
			return new Long( ((Vendedor) arg2).getId()).toString();
		}
		return "";
	}

}
