package nc.ui.erm.bx.ext.action;

import nc.bs.erm.ext.common.ErmConstExt;
import nc.ui.erm.billpub.action.ContrastAction;
import nc.ui.erm.billpub.view.ContrastDialog;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

/**
 * 报销冲借款按钮，扩展类
 * 
 * 1、单据管理节点应用时，控制经销商垫付报销单不可冲借款
 * 2、报销单拉单场景，默认可以冲对应申请单下的全部借款单
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public class BxContrastActionExt extends ContrastAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected boolean isActionEnable() {
		JKBXVO vo= ((ErmBillBillForm)getEditor()).getJKBXVO();
		return super.isActionEnable()&& vo != null && !ErmConstExt.Distributor_BX_Tradetype.equals(vo.getParentVO().getDjlxbm());
	}
	
	@Override
	public ContrastDialog getContrastDialog(JKBXVO vo, String pk_corp)
			throws BusinessException {
		// 报销单拉单场景，默认可以冲对应申请单下的全部借款单
		qrySql = StringUtil.isEmptyWithTrim(vo.getParentVO().getPk_item())?null:"1=1";
		return super.getContrastDialog(vo, pk_corp);
	}

}
