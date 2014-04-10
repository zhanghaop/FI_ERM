package nc.bs.pub.action;

import java.util.Hashtable;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.pubitf.erm.matterapp.IErmMatterAppBillManage;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.compiler.PfParameterVO;

public class N_261X_DELETE extends AbstractCompiler2 {
	
	protected Hashtable<String, Object> m_keyHas = null;

	public N_261X_DELETE() {
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
			
//			 ####本脚本必须含有返回值,返回DLG和PNL的组件不允许有返回值####
//			 ####重要说明：生成的业务组件方法尽量不要进行修改####
			// ####该组件为单动作工作流处理结束...不能进行修改####
			// 方法说明:null
			// ##################################################
			// ####该组件为单动作工作流处理开始...不能进行修改####
			
			AggMatterAppVO maVo = (AggMatterAppVO) pfparametervo.m_preValueVo;
			
			NCLocator.getInstance().lookup(IErmMatterAppBillManage.class).deleteVOs(new AggMatterAppVO[]{maVo});
			return null;
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
}