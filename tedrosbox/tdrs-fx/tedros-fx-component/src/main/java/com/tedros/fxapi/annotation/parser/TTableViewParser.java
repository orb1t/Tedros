package com.tedros.fxapi.annotation.parser;

import org.apache.commons.lang3.StringUtils;

import com.tedros.fxapi.annotation.control.TTableColumn;
import com.tedros.fxapi.annotation.control.TTableNestedColumn;
import com.tedros.fxapi.annotation.control.TTableSubColumn;
import com.tedros.fxapi.annotation.control.TTableView;
import com.tedros.fxapi.descriptor.TComponentDescriptor;
import com.tedros.fxapi.presenter.model.TModelView;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

@SuppressWarnings("rawtypes")
public class TTableViewParser extends TAnnotationParser<TTableView, TableView> {

	private static TTableViewParser instance;
	
	private TTableViewParser(){
		
	}
	
	public static  TTableViewParser getInstance(){
		if(instance==null)
			instance = new TTableViewParser();
		return instance;	
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void parse(TTableView annotation, TableView tableView, String... byPass) throws Exception {
		
		TTableColumn[] columns = annotation.columns();
		
		for (final TTableColumn tTableColumn : columns) {
			final TableColumn tableColumn = new TableColumn<>();
			TTableSubColumn[] parenteColumns = tTableColumn.columns();
			
			boolean clnVf = false;
			for (final TTableSubColumn tTableSubColumn : parenteColumns) {
				final TableColumn tableSubColumn = new TableColumn<>();
				tableColumn.getColumns().add(tableSubColumn);
				
				TTableNestedColumn[] nestedColumns = tTableSubColumn.columns();
				boolean sbClnVf = false;
				for (final TTableNestedColumn tTableNestedColumn : nestedColumns) {
					final TableColumn tableNestedColumn = new TableColumn<>();
					tableSubColumn.getColumns().add(tableNestedColumn);
					if(StringUtils.isNotBlank(tTableNestedColumn.cellValue())){
						tableNestedColumn.setCellValueFactory(
								new PropertyValueFactory(tTableNestedColumn.cellValue()){   
									 @Override
									 public ObservableValue call(CellDataFeatures p) {
									  return new ReadOnlyObjectWrapper(((ObservableValue)((TModelView) p.getValue()).getProperty(tTableNestedColumn.cellValue())).getValue());
									 }
									});
						sbClnVf = true;
					}
					final TComponentDescriptor descriptor = new TComponentDescriptor(getComponentDescriptor(), null);
					callParser(tTableNestedColumn, tableNestedColumn, descriptor);
				}
				
				if(!sbClnVf){
					if(StringUtils.isNotBlank(tTableSubColumn.cellValue())){
						tableSubColumn.setCellValueFactory(
								new PropertyValueFactory(tTableSubColumn.cellValue()){   
									 @Override
									 public ObservableValue call(CellDataFeatures p) {
									  return new ReadOnlyObjectWrapper(((ObservableValue)((TModelView) p.getValue()).getProperty(tTableSubColumn.cellValue())).getValue());
									 }
									});
						clnVf = true;
					}
				}
				final TComponentDescriptor descriptor = new TComponentDescriptor(getComponentDescriptor(), null);
				callParser(tTableSubColumn, tableSubColumn, descriptor);
			}
			
			if(!clnVf){
				if(StringUtils.isNotBlank(tTableColumn.cellValue())){
					tableColumn.setCellValueFactory(
							new PropertyValueFactory(tTableColumn.cellValue()){   
								 @Override
								 public ObservableValue call(CellDataFeatures p) {
								  return new ReadOnlyObjectWrapper( ((ObservableValue)((TModelView) p.getValue()).getProperty(tTableColumn.cellValue())).getValue());
								 }
								});
					clnVf = true;
				}
			}
			final TComponentDescriptor descriptor = new TComponentDescriptor(getComponentDescriptor(), null);
			callParser(tTableColumn, tableColumn, descriptor);
			
			tableView.getColumns().add(tableColumn);
		}
		
		
		// *******************
		
		for (final TTableColumn tTableColumn : columns) {
			final TableColumn tableColumn = getColumn(tableView, tTableColumn.text());
			TTableSubColumn[] parenteColumns = tTableColumn.columns();
			
			boolean clnVf = false;
			for (final TTableSubColumn tTableSubColumn : parenteColumns) {
				final TableColumn tableSubColumn = getColumn(tableView, tTableSubColumn.text());
				//tableColumn.getColumns().add(tableSubColumn);
				
				TTableNestedColumn[] nestedColumns = tTableSubColumn.columns();
				boolean sbClnVf = false;
				for (final TTableNestedColumn tTableNestedColumn : nestedColumns) {
					final TableColumn tableNestedColumn = getColumn(tableView, tTableNestedColumn.text());
					//tableSubColumn.getColumns().add(tableNestedColumn);
					if(StringUtils.isNotBlank(tTableNestedColumn.cellValue())){
						
						if(tTableNestedColumn.cellValueFactory().parse() && tTableNestedColumn.cellValueFactory().value().parse()){
							Class callbackClass = tTableNestedColumn.cellValueFactory().value().value();
							Callback callback = (Callback) callbackClass.newInstance();
							setCellValueFactory(tableNestedColumn, callback);
						}
						
						if(tTableNestedColumn.cellFactory().parse()){
							Class callbackClass = tTableNestedColumn.cellFactory().callBack().parse() 
									? tTableNestedColumn.cellFactory().callBack().value()
											: null;
							Class tableCellClass = tTableNestedColumn.cellFactory().tableCell();
							
							setCellFactory(tableNestedColumn, callbackClass, tableCellClass);
						
							tableNestedColumn.setEditable(true);
							sbClnVf = true;
						}
					}
				}
				
				if(!sbClnVf){
					
					if(tTableSubColumn.cellValueFactory().parse() && tTableSubColumn.cellValueFactory().value().parse()){
						Class callbackClass = tTableSubColumn.cellValueFactory().value().value();
						Callback callback = (Callback) callbackClass.newInstance();
						setCellValueFactory(tableSubColumn, callback);
					}
					
					if(tTableSubColumn.cellFactory().parse()){
						Class callbackClass = tTableSubColumn.cellFactory().callBack().parse() 
								? tTableSubColumn.cellFactory().callBack().value()
										: null;
						Class tableCellClass = tTableSubColumn.cellFactory().tableCell();
						
						setCellFactory(tableSubColumn, callbackClass, tableCellClass);
					
						tableSubColumn.setEditable(true);
						clnVf = true;
					}
				}
			}
			
			if(!clnVf){
				
				if(tTableColumn.cellValueFactory().parse() && tTableColumn.cellValueFactory().value().parse()){
					Class callbackClass = tTableColumn.cellValueFactory().value().value();
					Callback callback = (Callback) callbackClass.newInstance();
					setCellValueFactory(tableColumn, callback);
				}
				
				if(tTableColumn.cellFactory().parse()){
					Class callbackClass = tTableColumn.cellFactory().callBack().parse() 
							? tTableColumn.cellFactory().callBack().value()
									: null;
					Class tableCellClass = tTableColumn.cellFactory().tableCell();
					
					setCellFactory(tableColumn, callbackClass, tableCellClass);
					
					tableColumn.setEditable(true);
					clnVf = true;
				}
			}
		}
		
		
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		tableView.setEditable(true);
				
		super.parse(annotation, tableView, "columns");
	}

	@SuppressWarnings("unchecked")
	private void setCellValueFactory(final TableColumn tableColumn, Callback callback) {
		tableColumn.setCellValueFactory(callback);
	}

	@SuppressWarnings("unchecked")
	private void setCellFactory(final TableColumn tableColumn, Class callbackClass, Class tableCellClass)
			throws InstantiationException, IllegalAccessException {
	
		if(callbackClass != null && callbackClass != Callback.class){
			Callback callback = (Callback) callbackClass.newInstance();
			tableColumn.setCellFactory(callback);
		
		}else if(tableCellClass!=TableCell.class){
			
			if(tableCellClass==CheckBoxTableCell.class) 
				tableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(tableColumn));
			else
			if(tableCellClass==ChoiceBoxTableCell.class) 
				tableColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(tableColumn));
			else						
			if(tableCellClass==ComboBoxTableCell.class) 
				tableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(tableColumn));
			else						
			if(tableCellClass==ProgressBarTableCell.class) 
				tableColumn.setCellFactory(ProgressBarTableCell.forTableColumn());
			else						
			if(tableCellClass==TextFieldTableCell.class)
				tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		}
	}
	
	
	private TableColumn getColumn(Object obj, String text){
		TableColumn tableColumn = null;
		if(obj instanceof TableView){
			TableView tbv = (TableView) obj;
			for(Object o : tbv.getColumns()){
				TableColumn tc = (TableColumn) o;
				if(tc.getText().equals(text))
					tableColumn = tc;
				if(tc.getColumns()!=null && tc.getColumns().size()>0)
					tableColumn = getColumn(tc, text);
			}
		}else{
			TableColumn tbc = (TableColumn) obj;
			for(Object o : tbc.getColumns()){
				TableColumn tc = (TableColumn) o;
				if(tc.getText().equals(text))
					tableColumn = tc;
				if(tc.getColumns()!=null && tc.getColumns().size()>0)
					tableColumn = getColumn(tc, text);
			}
		}
		
		return tableColumn;
	}
	
}
