/**
 * TEDROS  
 * 
 * TODOS OS DIREITOS RESERVADOS
 * 25/02/2014
 */
package com.tedros.global.brasil.module.pessoa.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tedros.ejb.base.service.TResult;
import com.tedros.fxapi.exception.TProcessException;
import com.tedros.fxapi.process.TFilterProcess;
import com.tedros.global.brasil.ejb.service.IPessoaService;
import com.tedros.global.brasil.model.Pessoa;

/**
 * DESCRIÇÃO DA CLASSE
 *
 * @author Davis Gordon
 *
 */
public class TPessoaFilterProcess extends TFilterProcess {

	public TPessoaFilterProcess() throws TProcessException {
		super(Pessoa.class, "TPessoaServiceRemote", true);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public List<TResult<Object>> doFilter(Map<String, Object> objects) {
		
		String nome = (objects.containsKey("nome")) ? (String) objects.get("nome") :  null;
		String tipo = (objects.containsKey("tipoPessoa")) ? (String) objects.get("tipoPessoa") :  null;;
		String tipoDocumento = (objects.containsKey("tipo")) ? (String) objects.get("tipo") :  null;;
		String numero = (objects.containsKey("numero")) ? (String) objects.get("numero") :  null;;
		Date dataNascimento = (objects.containsKey("dataNascimento")) ? (Date) objects.get("dataNascimento") :  null;;
		
		List<TResult<Object>> resultados = new ArrayList<>();
		TResult result = ((IPessoaService)getService()).pesquisar(nome, dataNascimento, tipo, tipoDocumento, numero);
		resultados.add(result);
		return resultados;
	}

}
