package nc.bs.pub.action;

import java.util.Hashtable;

import nc.bs.pub.compiler.AbstractCompiler2;
import nc.vo.ep.bx.JKVO;
import nc.vo.pub.compiler.PfParameterVO;

public class N_263X_SAVE extends AbstractCompiler2 {

	public N_263X_SAVE() {
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
//			 ####本脚本必须含有返回值,返回DLG和PNL的组件不允许有返回值####
			JKVO vo=(JKVO) pfparametervo.m_preValueVo;
//			 ####重要说明：生成的业务组件方法尽量不要进行修改####
			// ####该组件为单动作工作流处理结束...不能进行修改####
			// 方法说明:null
			// ##################################################
			// ####该组件为单动作工作流处理开始...不能进行修改####
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