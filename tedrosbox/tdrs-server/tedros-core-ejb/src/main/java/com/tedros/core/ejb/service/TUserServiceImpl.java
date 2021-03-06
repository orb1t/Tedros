package com.tedros.core.ejb.service;

import javax.ejb.Singleton;
import javax.ejb.Stateless;

import com.tedros.core.ejb.bo.TUserBO;
import com.tedros.core.security.model.TProfile;
import com.tedros.core.security.model.TUser;
import com.tedros.ejb.base.bo.ITGenericBO;
import com.tedros.ejb.base.service.TEjbService;
import com.tedros.ejb.base.service.TResult;
import com.tedros.ejb.base.service.TResult.EnumResult;

@Singleton
@Stateless(name = "TUserService")
public class TUserServiceImpl extends TEjbService<TUser>	implements	TUserService {

	private TUserBO bo = new TUserBO();
	
	@Override
	public ITGenericBO<TUser> getBussinesObject() {
		return bo;
	}

	@Override
	public TResult<TUser> login(String login, String password) {
		try{
			TUser entity = bo.getUserByLoginPassword(getEntityManager(), login, password);
			return new TResult<TUser>(EnumResult.SUCESS, entity);
		}catch(Exception e){
			e.printStackTrace();
			return new TResult<TUser>(EnumResult.ERROR, e.getMessage());
		}
	}
	
	public TResult<TUser> saveActiveProfile(TProfile profile, Long userId) {
		try{
			bo.saveActiveProfile(getEntityManager(), profile, userId);
			return new TResult<TUser>(EnumResult.SUCESS);
		}catch(Exception e){
			e.printStackTrace();
			return new TResult<TUser>(EnumResult.ERROR, e.getMessage());
		}
	}

	
}
