/**
 * TEDROS  
 * 
 * TODOS OS DIREITOS RESERVADOS
 * 11/11/2013
 */
package com.tedros.fxapi.form;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.scene.web.WebViewBuilder;

import org.apache.commons.lang3.StringUtils;

import com.tedros.core.model.ITModelView;
import com.tedros.fxapi.annotation.TDebugConfig;
import com.tedros.fxapi.builder.ITReaderHtmlBuilder;
import com.tedros.fxapi.descriptor.TFieldDescriptor;
import com.tedros.fxapi.domain.TViewMode;
import com.tedros.fxapi.reader.THtmlReader;
import com.tedros.fxapi.util.TReflectionUtil;

/**
 * DESCRIÇÃO DA CLASSE
 *
 * @author Davis Gordon
 *
 */
public final class TFormEngine<M extends ITModelView<?>, F extends ITModelForm<M>>  {
	
	private TViewMode mode;
	private TModelViewLoader<M> modelViewLoader;
	private final M modelView;
	private final F form;
	private final Map<String, Object> associatedObjectsMap;
	private ObservableList<Node> fields;
	private WebView webView;
	
	public TFormEngine(final F form, final M modelView) {
		this.form = form;
		if(StringUtils.isBlank(this.form.getId()))
			this.form.setId("t-form");
		this.modelView = modelView;
		this.associatedObjectsMap = new HashMap<>(0);
		setEditMode();
	}
	
	public TFormEngine(final F form, final M modelView, boolean readerMode) {
		this.form = form;
		this.modelView = modelView;
		this.associatedObjectsMap = new HashMap<>(0);
		if(readerMode)
			setReaderMode();
		else
			setEditMode();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setReaderMode(){
		mode = TViewMode.READER;
		
		if(TDebugConfig.detailParseExecution)
			System.out.println("[TFormEngine][ReadeMode][initialized]");
		
		resetForm();
		this.modelViewLoader = new TModelViewLoader<M>(modelView, this.form);
		
		try {
			if(StringUtils.isBlank(this.form.getId()))
				form.setId("t-reader");
				int totalHtmlReaders = 0;
				fields = this.modelViewLoader.getReaders();
				
				for (Node node : fields) {
					if(node instanceof THtmlReader)
						totalHtmlReaders++;		
				}
				
				if(fields.size() == totalHtmlReaders){
					
					StringBuffer sbf = new StringBuffer();
					for (Node node : fields){
						THtmlReader htmlReader = (THtmlReader) node;
						sbf.append(htmlReader.getText()).append("\n");
					}
					
					/*int x=0;
					if(modelView.getClass().getSimpleName().contains("TUserModelView"))
						x=1;*/
						
					webView = WebViewBuilder.create().build();
					form.tAddFormItem(webView);
					webView.setDisable(true);
					
					ChangeListener<Number> hListener = new ChangeListener<Number>() {
						@Override
						public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
							webView.setPrefHeight((double) arg2);
						}
					};
					
					((Pane)this.form).heightProperty().addListener(hListener);
										
					if(modelView.getClass().getAnnotations()!=null){
						Object[] arrReaderHtml = TReflectionUtil.getReaderHtmlBuilder(Arrays.asList(modelView.getClass().getAnnotations()));	
						if(arrReaderHtml !=null ){
							Annotation readerAnnotation = (Annotation) arrReaderHtml[0];
							ITReaderHtmlBuilder readerBuilder = (ITReaderHtmlBuilder) arrReaderHtml[1];
							webView.getEngine().loadContent(readerBuilder.build(readerAnnotation, sbf).getText());
						}else
							webView.getEngine().loadContent(sbf.toString());
					}else
						webView.getEngine().loadContent(sbf.toString());
					
				}else{
					StringBuffer sbf = null;
					for (Node node : fields){
						if(node instanceof THtmlReader){
							if(webView==null){
								sbf = new StringBuffer();
								webView = WebViewBuilder.create().build();
								form.tAddFormItem(webView);
							}
							THtmlReader htmlReader = (THtmlReader) node;
							sbf.append(htmlReader.getText()).append("\n");
						}else						
							form.tAddFormItem(node);
					}
					
					if(sbf!=null)
						webView.getEngine().loadContent(sbf.toString());
				}
				
					
			initializeReader();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setEditMode(){
		mode = TViewMode.EDIT;
		if(TDebugConfig.detailParseExecution)
			System.out.println("[TFormEngine][EditMode][initialized]");
		
		resetForm();
		this.modelViewLoader = new TModelViewLoader<M>(modelView, this.form);
		
		try {
			if(StringUtils.isBlank(this.form.getId()))
				this.form.setId("t-form");
			fields = this.modelViewLoader.getControls();
			for(Node node : fields)
				form.tAddFormItem(node);
			initializeForm();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void reloadForm(){
		if(mode.equals(TViewMode.EDIT))
			setEditMode();
		if(mode.equals(TViewMode.READER))
			setReaderMode();
	}
	
	public TFieldBox getFieldBox(String fieldName){
		if(modelViewLoader!=null)
			return modelViewLoader.getFieldBox(fieldName);
		return null;
		
	}
	
	public TViewMode getMode(){
		return mode;
	}
	
	
	public ObservableMap<String, TFieldBox> getFieldBoxMap() {
		return (modelViewLoader!=null) ? modelViewLoader.getFieldBoxMap() : null;
	}
	
	public M getModelView() {
		return modelView;
	}
	
	public void initializeForm(){
		form.tInitializeForm();
	}; 
	
	public void initializeReader(){
		form.tInitializeReader();
	}
	
	public List<TFieldDescriptor> getFieldDescriptorList(){
		return modelViewLoader.getFieldDescriptorList();
	}
		
	public void addAssociatedObject(final String name, final Object obj) {
		this.associatedObjectsMap.put(name, obj);
	}
	
	public Object getAssociatedObject(String name) {
		return (this.associatedObjectsMap.containsKey(name)) ? this.associatedObjectsMap.get(name) : null;
	}

	private void resetForm() {
		
		this.modelViewLoader = null;
		if(form.getChildren()!=null){
			try{
				form.getChildren().clear();
			}catch(IllegalArgumentException e){
				// bug
				form.getChildren().clear();
			}
		}
		
		fields = null;
		webView = null;
	}
	
	public WebView getWebView() {
		return webView;
	}

	/**
	 * @return the fields
	 */
	public ObservableList<Node> getFields() {
		return fields;
	}
	
}
