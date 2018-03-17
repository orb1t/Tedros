/**
 * TEDROS  
 * 
 * TODOS OS DIREITOS RESERVADOS
 * 18/01/2014
 */
package com.tedros.fxapi.annotation.parser;

import java.lang.annotation.Annotation;

import javafx.scene.text.Text;

/**
 * DESCRIÇÃO DA CLASSE
 *
 * @author Davis Gordon
 *
 */
public final class TTextParser extends TAnnotationParser<Annotation, Text> {

	private static TTextParser instance;
	
	private TTextParser() {
		
	}
	
	public static TTextParser getInstance(){
		if(instance==null)
			instance = new TTextParser();
		return instance;	
	}	
		
}