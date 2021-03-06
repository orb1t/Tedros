/**
 * TEDROS  
 * 
 * TODOS OS DIREITOS RESERVADOS
 * 15/01/2014
 */
package com.tedros.global.brasil.module.pessoa.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.tedros.fxapi.annotation.TCodeValue;
import com.tedros.fxapi.annotation.control.TLabel;
import com.tedros.fxapi.annotation.control.TMaskField;
import com.tedros.fxapi.annotation.control.TRadioButtonField;
import com.tedros.fxapi.annotation.control.TTextField;
import com.tedros.fxapi.annotation.control.TTextInputControl;
import com.tedros.fxapi.annotation.control.TVerticalRadioGroup;
import com.tedros.fxapi.annotation.form.TForm;
import com.tedros.fxapi.annotation.presenter.TBehavior;
import com.tedros.fxapi.annotation.presenter.TDecorator;
import com.tedros.fxapi.annotation.presenter.TPresenter;
import com.tedros.fxapi.annotation.reader.TFormReaderHtml;
import com.tedros.fxapi.annotation.reader.TReaderHtml;
import com.tedros.fxapi.annotation.scene.control.TControl;
import com.tedros.fxapi.presenter.dynamic.TDynaPresenter;
import com.tedros.fxapi.presenter.entity.behavior.TDetailCrudViewWithListViewBehavior;
import com.tedros.fxapi.presenter.entity.decorator.TDetailCrudViewWithListViewDecorator;
import com.tedros.fxapi.presenter.model.TEntityModelView;
import com.tedros.fxapi.util.TPropertyUtil;
import com.tedros.global.brasil.model.Endereco;

/**
 * DESCRIÇÃO DA CLASSE
 *
 * @author Davis Gordon
 *
 */
@TForm(showBreadcrumBar=true, name = "Editar endereço")
@TFormReaderHtml
@TPresenter(type = TDynaPresenter.class,
behavior = @TBehavior(type = TDetailCrudViewWithListViewBehavior.class), 
decorator = @TDecorator(type = TDetailCrudViewWithListViewDecorator.class, viewTitle="Endereços", listTitle="Selecione"))
/*@TReaderDefaultSetting(showActionsToolTip=true, 
labelDefaultSettings=@TLabelDefaultSetting(node=@TNode(style="-fx-text-fill:yellow; -fx-font-size: 1.4em;", parse = true), font=@TFont(family="Euphemia", weight=FontWeight.BOLD)))*/
public class EnderecoModelView extends TEntityModelView<Endereco> {
	
	private SimpleLongProperty id;
	
	@TReaderHtml(codeValues={@TCodeValue(code = "1", value = "#{label.house}"), 
			@TCodeValue(code = "2", value = "#{label.work}"),
			@TCodeValue(code = "3", value = "#{label.other}")})
	@TLabel(text="Tipo")
	@TVerticalRadioGroup(required=true, alignment=Pos.TOP_LEFT, spacing=4,
			radioButtons = {@TRadioButtonField(text="#{label.house}", userData="1"), 
							@TRadioButtonField(text="#{label.job}", userData="2"),
							@TRadioButtonField(text="#{label.other}", userData="3")
			})
	
	private SimpleStringProperty tipo;
	
	@TReaderHtml
	@TLabel(text="Caixa Postal")
	@TTextField(maxLength=60, control=@TControl(prefWidth=350, parse = true))
	private SimpleStringProperty caixaPostal;
	
	@TReaderHtml
	@TLabel(text="Cep")
	@TMaskField(mask="99999-999", required=true, control=@TControl(prefWidth=350, parse = true))
	private SimpleStringProperty cep;
	
	@TReaderHtml
	@TLabel(text="Complemento")
	@TTextField(maxLength=300, control=@TControl(prefWidth=350, parse = true))
	private SimpleStringProperty complemento;
	
	@TReaderHtml
	@TLabel(text="Tipo Logradouro")
	@TTextField(maxLength=100, textInputControl=@TTextInputControl(promptText="Rua, Avenida, Pra�a...", parse = true), control=@TControl(prefWidth=350, parse = true))
	private SimpleStringProperty tipoLogradouro;
	
	@TReaderHtml
	@TLabel(text="Logradouro")
	@TTextField(maxLength=300, control=@TControl(prefWidth=350, parse = true))
	private SimpleStringProperty logradouro;
	
	@TReaderHtml
	@TLabel(text="Bairro")
	@TTextField(maxLength=300, control=@TControl(prefWidth=350, parse = true))
	private SimpleStringProperty bairro;
	
	@TReaderHtml
	@TLabel(text="Cidade")
	@TTextField(required=true, maxLength=300, control=@TControl(prefWidth=350, parse = true))
	private SimpleStringProperty cidade;
	
	/*@TReader
	@TLabel(text="UF")
	@TTextField(required=true, textInputControl=@TTextInputControl(promptText="Sigla do estado", parse = true))
	private SimpleStringProperty uf;*/
	
	
	public EnderecoModelView(Endereco entidade) {
		super(entidade);
	}
	
	@Override
	public String toString() {
		return getEnderecoCompleto();	
	}

	private String getEnderecoCompleto() {
		String tipo = TPropertyUtil.getValue(this.tipo);
		String tDes = "";
		if(tipo.equals("1"))
			tDes = "Residencial: ";
		if(tipo.equals("2"))
			tDes = "Comercial: ";
		
		String tLog = TPropertyUtil.getValue(tipoLogradouro)+" ";
		String logr = TPropertyUtil.getValue(logradouro)+" ";
		String compl = TPropertyUtil.getValue(complemento)+" ";
		String bair = TPropertyUtil.getValue(bairro)+" ";
		String cida = TPropertyUtil.getValue(cidade)+" ";
		//String esta = TPropertyUtil.getValue(this.uf)+" ";
		String cep = TPropertyUtil.getValue(this.cep)+" ";
		//return tDes+tLog+logr+compl+bair+cida+esta+cep;
		return tDes+tLog+logr+compl+bair+cida+cep;
	}
		
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, false);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null || !(obj instanceof EnderecoModelView))
			return false;
		
		EnderecoModelView p = (EnderecoModelView) obj;
		
		if(getId()!=null && getId().getValue()!=null &&  p.getId()!=null && p.getId().getValue()!=null)
			if(!(getId().getValue().equals(Long.valueOf(0)) && p.getId().getValue().equals(Long.valueOf(0))))
				return getId().getValue().equals(p.getId().getValue());
		
		return getEnderecoCompleto().equals(p.getEnderecoCompleto());
	}
	
	public SimpleStringProperty getTipo() {
		return tipo;
	}

	public void setTipo(SimpleStringProperty tipo) {
		this.tipo = tipo;
	}

	public SimpleStringProperty getCaixaPostal() {
		return caixaPostal;
	}

	public void setCaixaPostal(SimpleStringProperty caixaPostal) {
		this.caixaPostal = caixaPostal;
	}

	public SimpleStringProperty getCep() {
		return cep;
	}

	public void setCep(SimpleStringProperty cep) {
		this.cep = cep;
	}

	public SimpleStringProperty getComplemento() {
		return complemento;
	}

	public void setComplemento(SimpleStringProperty complemento) {
		this.complemento = complemento;
	}

	public SimpleStringProperty getTipoLogradouro() {
		return tipoLogradouro;
	}

	public void setTipoLogradouro(SimpleStringProperty tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}

	public SimpleStringProperty getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(SimpleStringProperty logradouro) {
		this.logradouro = logradouro;
	}

	public SimpleStringProperty getBairro() {
		return bairro;
	}

	public void setBairro(SimpleStringProperty bairro) {
		this.bairro = bairro;
	}

	public SimpleStringProperty getCidade() {
		return cidade;
	}

	public void setCidade(SimpleStringProperty cidade) {
		this.cidade = cidade;
	}

	/*public SimpleStringProperty getUf() {
		return uf;
	}

	public void setUf(SimpleStringProperty uf) {
		this.uf = uf;
	}*/

	@Override
	public SimpleStringProperty getDisplayProperty() {
		return cep;
	}

	public final SimpleLongProperty getId() {
		return id;
	}

	public final void setId(SimpleLongProperty id) {
		this.id = id;
	}

	

}
