
package nc.bs.pub.action;

import java.util.Hashtable;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.uap.pf.PFBusinessException;

public class N_264X_TRANSFERFTS extends AbstractCompiler2 {

	public N_264X_TRANSFERFTS() {
		super();
//		m_methodReturnHas = new Hashtable<String, Object>();
		m_keyHas = null;
	}

	@Override
	public String getCodeRemark() {
		return "erm action script not allowed modify, all rights reserved!;";
	}

	public Object runComClass(PfParameterVO pfparametervo) throws nc.vo.pub.BusinessException {
		try{
			super.m_tmpVo=pfparametervo;
		} catch (Exception ex) {
			if (ex instanceof BusinessException)
				throw (BusinessException) ex;
			else 
		    throw new PFBusinessException(ex.getMessage(), ex);
		 
		}
		return pfparametervo;
	}


	/*
	 * 备注：设置脚本变量的HAS
	 */
	protected void setParameter(String key, Object val) {
		if (m_keyHas == null) {
			m_keyHas = new Hashtable<String, Object>();
		}
		if (val != null) {
			m_keyHas.put(key, val);
		}
	}
	
//	protected java.util.Hashtable<String, Object> m_methodReturnHas = new java.util.Hashtable<String, Object>();

	protected Hashtable<String, Object> m_keyHas = null;
	
}