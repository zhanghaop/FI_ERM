package nc.bs.pub.action;

import java.util.Hashtable;

import nc.bs.pub.compiler.AbstractCompiler2;
import nc.vo.ep.bx.JKVO;
import nc.vo.pub.compiler.PfParameterVO;

public class N_263X_WRITE extends AbstractCompiler2 {

	public N_263X_WRITE() {
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
			JKVO vo=(JKVO) pfparametervo.m_preValueVo;
//			 ####��Ҫ˵�������ɵ�ҵ���������������Ҫ�����޸�####
			// ####�����Ϊ�������������������...���ܽ����޸�####
			// ����˵��:null
			// ##################################################
			// ####�����Ϊ����������������ʼ...���ܽ����޸�####
			setParameter("billVO", new JKVO[]{vo});
			
			Object obj = null;
			
			obj = runClass("nc.bs.arap.bx.BXZbBO", "save", "&billVO:nc.vo.ep.bx.JKBXVO[]", pfparametervo, m_keyHas);
			
//			if (obj != null)
//				m_methodReturnHas.put("saveBill", obj);
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