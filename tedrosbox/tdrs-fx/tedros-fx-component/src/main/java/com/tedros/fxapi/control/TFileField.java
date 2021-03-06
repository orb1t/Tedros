/**
 * TEDROS  
 * 
 * TODOS OS DIREITOS RESERVADOS
 * 21/03/2014
 */
package com.tedros.fxapi.control;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.tedros.fxapi.control.action.TActionEvent;
import com.tedros.fxapi.domain.TFileExtension;
import com.tedros.fxapi.exception.TProcessException;
import com.tedros.fxapi.modal.TModalPane;
import com.tedros.fxapi.property.TBytesLoader;
import com.tedros.util.TUrlUtil;

/**
 * DESCRIÇÃO DA CLASSE
 *
 * @author Davis Gordon
 *
 */
public class TFileField extends StackPane {
	
	private final Stage appStage;
	private BorderPane boxImageLabelSpace;
	private VBox vBox;
	private Button cleanButton;
    private TTextField fileNameField;
    private Label filePathLabel;
    private StackPane imageSpace;
    private ImageView imageView;
    private Button openButton;
    private Button selectButton;
    private ToolBar toolbar;
    private FileChooser fileChooser;
    
	private SimpleObjectProperty<File> fileProperty;
	private SimpleObjectProperty<byte[]> byteArrayProperty;
	private SimpleStringProperty fileNameProperty;
	private SimpleLongProperty fileSizeProperty;
	private SimpleLongProperty bytesEntityIdProperty;
	
	private String[] extensions;
	
	private Double minFileSize;
	private Double maxFileSize;
	private Double minImageWidth;
	private Double maxImageWidth;
	private Double minImageHeight;
	private Double maxImageHeight;
	
	private boolean showImage;
	private boolean showFilePath;
	private final TModalPane modal;
	
	private TActionEvent openAction;
	private TActionEvent cleanAction;
	private TActionEvent loadAction;
	private TActionEvent selectAction;
	private TActionEvent imageClickAction;
	
	private EventHandler<ActionEvent> openEventHandler;
	private EventHandler<ActionEvent> downloadEventHandler;
	
	public TFileField(final Stage stage) throws IOException {	
		appStage = stage;
		modal = new TModalPane(this);
		initialize();
		buildListeners();
	}

	private void initialize() {
		fileProperty = new SimpleObjectProperty<>();
		fileChooser = new FileChooser();
		selectButton = new Button();
		fileNameField = new TTextField();
		fileNameProperty = new SimpleStringProperty();
		fileSizeProperty = new SimpleLongProperty();
		byteArrayProperty = new SimpleObjectProperty<>();
		bytesEntityIdProperty = new SimpleLongProperty();
		
		vBox = new VBox();
		boxImageLabelSpace = new BorderPane();
		toolbar = new ToolBar();
		cleanButton = new Button();
		selectButton = new Button();
		openButton = new Button();
		
		fileNameProperty.bindBidirectional(fileNameField.textProperty());
		
		boxImageLabelSpace.setId("t-image-pane");
		
		toolbar.setId("t-file-toolbar");
		toolbar.setMaxWidth(Double.MAX_VALUE);
		toolbar.autosize();
		
		selectButton.setText("Selecionar");
		openButton.setText("Abrir");
		cleanButton.setText("Limpar");
		
		setAlignment(toolbar, Pos.CENTER_LEFT);
		
		selectButton.setId("t-button");
		openButton.setId("t-button");
		cleanButton.setId("t-last-button");
		
		fileNameField.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold; ");
		fileNameField.setPrefWidth(250);
		fileNameField.setMaxHeight(Double.MAX_VALUE);
		fileNameField.setDisable(true);
		
		extensions = TFileExtension.ALL_FILES.getExtension();
		
		toolbar.getItems().addAll(fileNameField, selectButton, openButton, cleanButton);
		
		vBox.setAlignment(Pos.CENTER_LEFT);
		
		HBox hBox = new HBox();
		HBox.setHgrow(toolbar, Priority.ALWAYS);
		hBox.getChildren().add(toolbar);
		vBox.getChildren().addAll(boxImageLabelSpace, hBox);
		getChildren().add(vBox);
	}
	
	private void buildListeners() {
			
		bytesEntityIdProperty.addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				if(bytesEntityIdProperty.getValue()!=null && byteArrayProperty.getValue()==null){
					setLoadByteAction();
				}else if(bytesEntityIdProperty.getValue()!=null && byteArrayProperty.getValue()!=null){
					setShowImage(true);
					loadImageView(byteArrayProperty.getValue());
					setOpenAction();
				}else if(byteArrayProperty.getValue()!=null){
					setOpenAction();
				}
				
			}
		});	
				/*
				new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0,	Number arg1, Number arg2) {
				if(arg2!=null && byteArrayProperty.getValue()==null){
					setLoadByteAction();
				}else if(byteArrayProperty.getValue()!=null){
					setOpenAction();
				}
			}						
		});*/
		
		byteArrayProperty.addListener(new ChangeListener<byte[]>() {
			@Override
			public void changed(ObservableValue<? extends byte[]> arg0, byte[] arg1, byte[] arg2) {
				
				if(showImage && arg2!=null ){
					loadImageView(arg2);
				}
			}
		});
		
		selectButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                    	
                    	if(selectAction!=null)
                    		if(!selectAction.runBefore(e))
                    			return;
                    	
                        configureFileChooser(fileChooser,extensions);
                        final File file = fileChooser.showOpenDialog(appStage);
                        if (file != null) {
                        	openFile(file);
                        	openButton.setDisable(false);
                        }
                        
                        
                        if(selectAction!=null)
                    		if(!selectAction.runAfter(e))
                    			return;
                    }
                });
		
		cleanButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if(cleanAction!=null)
            		if(!cleanAction.runBefore(e))
            			return;
				cleanField();
				if(cleanAction!=null)
            		if(!cleanAction.runAfter(e))
            			return;
			}
		});
		
		
		downloadEventHandler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				
				if(loadAction!=null)
            		if(!loadAction.runBefore(arg0))
            			return;
				
				try {
					TBytesLoader.loadBytesFromTFileEntity(bytesEntityIdProperty.getValue(), byteArrayProperty);
				} catch (TProcessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(loadAction!=null)
            		if(!loadAction.runAfter(arg0))
            			return;
			}
		};
		
		openEventHandler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				
				if(openAction!=null)
            		if(!openAction.runBefore(arg0))
            			return;
				
				if(filePathLabel!=null && StringUtils.isNotBlank(filePathLabel.getText())){
					String[] cmdArray = {"explorer.exe", filePathLabel.getText()};
	                try {
	                    java.lang.Runtime.getRuntime().exec(cmdArray);
	                } catch (Exception e) {
	                	showModal("Não foi possivel abrir o arquivo! Erro:"+e.getMessage());
	                }
				}else{
					if(byteArrayProperty!=null && byteArrayProperty.getValue()!=null && fileNameField!=null && fileNameField.getText()!=null){
						try {
							String folder = FileUtils.getUserDirectoryPath();
							
							File file = FileUtils.toFile(TUrlUtil.getURL(folder+"/"+fileNameField.getText()));
							int x = 1;
							boolean flag = file.exists();
							while(flag){
								String[] arr = new String[]{FilenameUtils.getBaseName(file.getName()), FilenameUtils.getExtension(file.getName())};
								file = FileUtils.toFile(TUrlUtil.getURL(folder+"/"+arr[0]+" "+(x++)+"."+arr[1]));
								flag = file.exists();
							}
							
							FileUtils.writeByteArrayToFile(file, byteArrayProperty.getValue());
							String[] cmdArray = {"explorer.exe", file.getAbsolutePath()};
							java.lang.Runtime.getRuntime().exec(cmdArray);
						} catch (Exception e) {
							showModal("Não foi possivel abrir o arquivo! Erro:"+e.getMessage());
						}
					}
				}
				
				if(openAction!=null)
            		if(!openAction.runAfter(arg0))
            			return;
			}
		};
		
		
		openButton.setOnAction(openEventHandler);
	}
	
	private void setLoadByteAction() {
		openButton.setText("Carregar");
		openButton.setOnAction(downloadEventHandler);
	}
	
	private void setOpenAction() {
		openButton.setText("Abrir");
		openButton.setOnAction(openEventHandler);
	}
	
	private void showModal(String msg){
		Label label = new Label(msg);
		label.setId("t-label");
		label.setStyle(	"-fx-font: Arial; "+
						"-fx-font-size: 1.0em; "+
						"-fx-font-weight: bold; "+
						"-fx-font-smoothing-type:lcd; "+
						"-fx-text-fill: #ffffff; "+
						"-fx-padding: 2 5 5 2; ");
		modal.showModal(label, true);
	}
	
	private static void configureFileChooser(final FileChooser fileChooser, String[] extensions){
		fileChooser.setTitle("Selecionar arquivo");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))); 
        for(String ext : extensions){
        	fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(ext, ext));
        }
    }
	
	private void openFile(File file)  {
    	try {
    		long size = FileUtils.sizeOf(file);
    		if(maxFileSize!=null && maxFileSize < size)
    			showModal("O tamanho maximo permitido � "+maxFileSize+" bytes");
    		else if(minFileSize!=null && minFileSize > FileUtils.sizeOf(file))
    			showModal("O tamanho minimo permitido � "+minFileSize+" bytes");
    		else{
	    		if(showFilePath)
	    			filePathLabel.setText(file.getAbsolutePath());
	            
	    		this.fileNameProperty.setValue(file.getName());
	    		this.byteArrayProperty.setValue(FileUtils.readFileToByteArray(file));
	    		this.fileProperty.setValue(file);
	    		this.fileSizeProperty.setValue(size);
    		}
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

	public final File getFile() {
		return (fileProperty!=null) ? fileProperty.getValue() : null;
	}
	
	public final SimpleObjectProperty<File> fileProperty() {
		return fileProperty;
	}
	
	public final SimpleLongProperty fileSizeProperty() {
		return fileSizeProperty;
	}
	
	public SimpleLongProperty bytesEntityIdProperty() {
		return bytesEntityIdProperty;
	}
	
	public final String[] getExtensions() {
		return extensions;
	}

	public final void setExtensions(String[] extensions) {
		this.extensions = extensions;
	}

	public final Double getMaxFileSize() {
		return maxFileSize;
	}

	public final void setMaxFileSize(Double maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public final Button getSelectFileButton() {
		return selectButton;
	}

	public final void setSelectFileButton(Button selectFileButton) {
		this.selectButton = selectFileButton;
	}

	public final SimpleObjectProperty<byte[]> byteArrayProperty() {
		return byteArrayProperty;
	}

	
	public final SimpleStringProperty fileNameProperty() {
		return fileNameProperty;
	}
	
	public final byte[] getByteArray() {
		return (byteArrayProperty!=null) ? byteArrayProperty.getValue() : null;
	}
	
	public final String getFileName() {
		return (fileNameProperty!=null) ? fileNameProperty.getValue() : null;
	}
	
	public SimpleBooleanProperty requiredProperty() {
		return fileNameField.requiredProperty();
	}
	
	public SimpleBooleanProperty requirementAccomplishedProperty() {
		return fileNameField.requirementAccomplishedProperty();
	}
	
	public boolean isRequirementAccomplished(){
		return fileNameField.isRequirementAccomplished(); 
	}

	public final void setByteArray(byte[] byteArray) {
		this.byteArrayProperty.setValue(byteArray);
	}

	public final boolean isShowImage() {
		return showImage;
	}

	public final void setShowImage(boolean showImage) {
		if(showImage){
			addImageView();
		}else{
			removeImageView();
		}
		this.showImage = showImage;
		showHideImageSpace();
	}
	
	private void showHideImageSpace(){
		if(!showImage && !showFilePath){
			vBox.getChildren().remove(boxImageLabelSpace);
		}else if(!vBox.getChildren().contains(boxImageLabelSpace)){
			vBox.getChildren().add(0, boxImageLabelSpace);
		}
	}

	private void removeImageView() {
		boxImageLabelSpace.setCenter(null);
	}

	private void addImageView() {
		if(imageView==null){
			imageView = new ImageView();
			imageView.setCache(false);
		}
		if(imageSpace==null){
			imageSpace = new StackPane();
			imageSpace.setPadding(new Insets(5,10,5,10));
			imageSpace.getChildren().add(imageView);
		}
		if(boxImageLabelSpace.getCenter()==null)
			boxImageLabelSpace.setCenter(imageSpace);
	}
	
	public void cleanField() {
		fileProperty.setValue(null);
		fileNameProperty.setValue(null);
		byteArrayProperty.setValue(null);
		fileSizeProperty.setValue(null);
		
		if(imageView!=null)
			imageView.setImage(null);
		if(filePathLabel!=null)
			filePathLabel.setText("");
		setOpenAction();
		openButton.setDisable(true);
	}

	public final boolean isShowFilePath() {
		return showFilePath;
	}

	public final void setShowFilePath(boolean showFilePath) {
		if(showFilePath){
			addFilePath();
		}else{
			removeFilePath();
		}
		this.showFilePath = showFilePath;
		showHideImageSpace();
	}

	private void removeFilePath() {
		boxImageLabelSpace.setBottom(null);
	}

	private void addFilePath() {
		filePathLabel = new Label();
		filePathLabel.setId("t-label");
		boxImageLabelSpace.setBottom(filePathLabel);
	}

	public final Button getCleanButton() {
		return cleanButton;
	}

	public final TTextField getFileNameField() {
		return fileNameField;
	}

	public final Label getFilePathLabel() {
		return filePathLabel;
	}

	public final StackPane getImageSpace() {
		return imageSpace;
	}

	public final ImageView getImageView() {
		return imageView;
	}

	public final Button getOpenButton() {
		return openButton;
	}

	public final Button getSelectButton() {
		return selectButton;
	}
	
	public final Double getMinFileSize() {
		return minFileSize;
	}

	public final void setMinFileSize(Double minFileSize) {
		this.minFileSize = minFileSize;
	}

	public final Double getMinImageWidth() {
		return minImageWidth;
	}

	public final void setMinImageWidth(Double minImageWidth) {
		this.minImageWidth = minImageWidth;
	}

	public final Double getMaxImageWidth() {
		return maxImageWidth;
	}

	public final void setMaxImageWidth(Double maxImageWidth) {
		this.maxImageWidth = maxImageWidth;
	}

	public final Double getMinImageHeight() {
		return minImageHeight;
	}

	public final void setMinImageHeight(Double minImageHeight) {
		this.minImageHeight = minImageHeight;
	}

	public final Double getMaxImageHeight() {
		return maxImageHeight;
	}

	public final void setMaxImageHeight(Double maxImageHeight) {
		this.maxImageHeight = maxImageHeight;
	}	
	
	public void setRequired(boolean required){
		if(fileNameField!=null)
			fileNameField.setRequired(required);
	}
	
	public StringProperty textProperty(){
		return fileNameField.textProperty();
	}

	private void loadImageView(byte[] arg2) {
		if((fileNameProperty!=null && StringUtils.isNotBlank(fileNameProperty.getValue()))){
			String[] arr = TFileExtension.ALL_IMAGES.getExtension();
			for (String ext : arr) {
				final String[] e = ext.split("\\.");
				if(e[1].equals("\\\\*"))
					continue;
				String fileExt = FilenameUtils.getExtension(fileNameProperty.getValue());
				if(StringUtils.containsIgnoreCase(fileExt, e[1])){
					Image image = new Image(new ByteArrayInputStream(arg2));
					
					double iW = image.getWidth();
					double iH = image.getHeight();
					String msg = "";
					if(maxImageWidth!=null && iW>maxImageWidth)
						msg += "A imagem precisa ser menor que "+maxImageWidth+" de largura\n";
					if(minImageWidth!=null && iW<minImageWidth)
						msg += "A imagem precisa ser maior que "+minImageWidth+" de largura\n";
					if(maxImageHeight!=null && iH>maxImageHeight)
						msg += "A imagem precisa ser menor que "+maxImageHeight+" de altura\n";
					if(minImageHeight!=null && iH>minImageHeight)
						msg += "A imagem precisa ser maior que "+minImageHeight+" de altura\n";
					
					if(StringUtils.isNotBlank(msg)){
						showModal(msg);
						cleanField();
					}else{
						
						if(imageClickAction!=null){
							if(imageView!=null && imageView.getOnMouseClicked()==null){
								imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
									@Override
									public void handle(MouseEvent arg0) {
										if(!imageClickAction.runBefore(arg0))
											return;
										if(!imageClickAction.runAfter(arg0))
											return;
									}
								});
							}
						}
						
						imageView.setImage(image);
						
					}
				}
			}
		}
	}

	public final TActionEvent getOpenAction() {
		return openAction;
	}

	public final void setOpenAction(TActionEvent openAction) {
		this.openAction = openAction;
	}

	public final TActionEvent getCleanAction() {
		return cleanAction;
	}

	public final void setCleanAction(TActionEvent cleanAction) {
		this.cleanAction = cleanAction;
	}

	public final TActionEvent getLoadAction() {
		return loadAction;
	}

	public final void setLoadAction(TActionEvent loadAction) {
		this.loadAction = loadAction;
	}

	public final TActionEvent getSelectAction() {
		return selectAction;
	}

	public final void setSelectAction(TActionEvent selectAction) {
		this.selectAction = selectAction;
	}

	public final TActionEvent getImageClickAction() {
		return imageClickAction;
	}

	public final void setImageClickAction(TActionEvent imageClickAction) {
		this.imageClickAction = imageClickAction;
	}
}
