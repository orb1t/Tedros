package com.tedros.fxapi.annotation.parser;

import org.apache.commons.lang3.ArrayUtils;

import com.tedros.fxapi.annotation.control.TFileField;
import com.tedros.fxapi.control.action.TActionEvent;

public class TFileFieldParser extends TAnnotationParser<TFileField, com.tedros.fxapi.control.TFileField> {
	
	private static TFileFieldParser instance;
	
	private TFileFieldParser(){
		
	}
	
	public static  TFileFieldParser getInstance(){
		if(instance==null)
			instance = new TFileFieldParser();
		return instance;	
	}
	
	@Override
	public void parse(TFileField tAnnotation, com.tedros.fxapi.control.TFileField control, String...byPass) throws Exception {
		
		super.parse(tAnnotation, control, "openAction","loadAction", "imageClickAction", "cleanAction", "selectAction", "control", "textInputControl", "extensions", "moreExtensions");
		
		TControlParser.getInstance().setComponentDescriptor(getComponentDescriptor());
		TControlParser.getInstance().parse(tAnnotation.control(), control.getFileNameField());
		TTextInputControlParse.getInstance().setComponentDescriptor(getComponentDescriptor());
		TTextInputControlParse.getInstance().parse(tAnnotation.textInputControl(), control.getFileNameField());
		
		String[] extensions = tAnnotation.extensions().getExtension();
		if(tAnnotation.moreExtensions()!=null && tAnnotation.moreExtensions().length>0)
			extensions = ArrayUtils.addAll(extensions, tAnnotation.moreExtensions());
		control.setExtensions(extensions);
		
		try {
			if(tAnnotation.openAction() != TActionEvent.class)
				control.setOpenAction(tAnnotation.openAction().newInstance());
			if(tAnnotation.cleanAction() != TActionEvent.class)
				control.setCleanAction(tAnnotation.cleanAction().newInstance());
			if(tAnnotation.loadAction() != TActionEvent.class)
				control.setLoadAction(tAnnotation.loadAction().newInstance());
			if(tAnnotation.selectAction() != TActionEvent.class)
				control.setSelectAction(tAnnotation.selectAction().newInstance());
			if(tAnnotation.imageClickAction() != TActionEvent.class)
				control.setImageClickAction(tAnnotation.imageClickAction().newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
}
