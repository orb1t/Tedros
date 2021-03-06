/**
 * TEDROS  
 * 
 * TODOS OS DIREITOS RESERVADOS
 * 14/01/2014
 */
package com.tedros.global.brasil.ejb.eao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tedros.ejb.base.eao.TGenericEAO;
import com.tedros.global.brasil.domain.DomainSchema;
import com.tedros.global.brasil.domain.DomainTables;
import com.tedros.global.brasil.model.Endereco;

/**
 * DESCRIÇÃO DA CLASSE
 *
 * @author Davis Gordon
 *
 */
public final class EnderecoEAO extends TGenericEAO<Endereco> {

	private static String SCHEMA = DomainSchema.tedros_global_brasil;
	
	public void excluirTodos(EntityManager em, final Long idPessoa)throws Exception{
	
		Query qry = em.createNativeQuery("delete from "+SCHEMA+"."+DomainTables.endereco+
				" where id in (select ende_id from "+SCHEMA+"."+DomainTables.pessoa_endereco+" where pess_id = "+idPessoa+" )");
		//qry.setParameter("id", idPessoa);
		qry.executeUpdate();
		
		qry = em.createNativeQuery("delete from "+SCHEMA+"."+DomainTables.pessoa_endereco+
				" where pess_id = "+idPessoa);
		//qry.setParameter("id", idPessoa);
		qry.executeUpdate();
	}
	
}
