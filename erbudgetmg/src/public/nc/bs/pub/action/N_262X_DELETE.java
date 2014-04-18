package nc.bs.pub.action;

import java.util.Hashtable;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillManage;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.compiler.PfParameterVO;

public class N_262X_DELETE extends AbstractCompiler2 {
	
	protected Hashtable<String, Object> m_keyHas = null;

	public N_262X_DELETE() {
		super();
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
			// ####�����Ϊ�������������������...���ܽ����޸�####
			// ����˵��:null
			// ##################################################
			// ####�����Ϊ����������������ʼ...���ܽ����޸�####
			
			AggAccruedBillVO aggvo = (AggAccruedBillVO) pfparametervo.m_preValueVo;
			
			NCLocator.getInstance().lookup(IErmAccruedBillManage.class).deleteVOs(new AggAccruedBillVO[]{aggvo});
			return null;
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
}
