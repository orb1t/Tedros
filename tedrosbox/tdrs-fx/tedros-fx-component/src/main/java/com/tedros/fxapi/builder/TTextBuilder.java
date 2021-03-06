/**
 * TEDROS  
 * 
 * TODOS OS DIREITOS RESERVADOS
 * 10/01/2014
 */
package com.tedros.fxapi.builder;

import java.lang.annotation.Annotation;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.text.Text;

import com.tedros.fxapi.annotation.text.TText;


/**
 * DESCRIÇÃO DA CLASSE
 *
 * @author Davis Gordon
 *
 */
public class TTextBuilder 
extends TBuilder 
implements ITControlBuilder<Text, SimpleStringProperty>, ITReaderBuilder<Text, SimpleStringProperty> {
	
	private static TTextBuilder instance;
	
	private TTextBuilder(){
		
	}
	
	public static TTextBuilder getInstance(){
		if(instance==null)
			instance = new TTextBuilder();
		return instance;
	}

	@Override
	public Text build(Annotation annotation, final SimpleStringProperty property) throws Exception {
		TText tAnnotation = (TText) annotation;
		final Text control = new Text();
		control.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0,String arg1, String arg2) {
				property.setValue(arg2);
			}
		});
		callParser(tAnnotation, control);
		
		return control;
	}	
}