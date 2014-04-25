package nc.bs.pub.action;

import java.util.Hashtable;

import nc.bs.pub.compiler.AbstractCompiler2;
import nc.vo.pub.compiler.PfParameterVO;

public class N_264X_DELETE extends AbstractCompiler2 {

	public N_264X_DELETE() {
		super();
//		m_methodReturnHas = new Hashtable<String, Object>();
		m_keyHas = null;
	}
	@Override
	public String getCodeRemark() {
		return "erm action script not allowed modify, all rights reserved!;";
	}
	@Override
	public Object runComClass(PfParameterVO pfparametervo) throws nc.vo.pub.BusinessException {
		try {
			super.m_tmpVo = pfparametervo;
			
//			 ####���ű����뺬�з���ֵ,����DLG��PNL������������з���ֵ####
//			 ####��Ҫ˵�������ɵ�ҵ���������������Ҫ�����޸�####
			// ####�����Ϊ��������������������...���ܽ����޸�####
			// ����˵��:null
			// ##################################################
			// ####�����Ϊ������������������ʼ...���ܽ����޸�####
			
			Object obj = null;
			
			setParameter("billVO", pfparametervo.m_userObj);
			
			obj = runClass("nc.bs.arap.bx.BXZbBO", "delete", "&billVO:nc.vo.ep.bx.JKBXVO[]", pfparametervo, m_keyHas);
			
//			if (obj != null)
//				m_methodReturnHas.put("deleteBill", obj);
			return obj;
		} catch (Exception exception) {
			if (exception instanceof nc.vo.pub.BusinessException)
				throw (nc.vo.pub.BusinessException) exception;
			else
				throw new nc.vo.pub.BusinessException("Remote Call", exception);
		}
	}
	

	/*
	 * ��ע�����ýű�������HAS
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