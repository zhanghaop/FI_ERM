package nc.ui.erm.bx.ext.action;

import nc.bs.erm.ext.common.ErmConstExt;
import nc.ui.erm.billpub.action.ContrastAction;
import nc.ui.erm.billpub.view.ContrastDialog;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

/**
 * �������ť����չ��
 * 
 * 1�����ݹ���ڵ�Ӧ��ʱ�����ƾ����̵渶���������ɳ���
 * 2������������������Ĭ�Ͽ��Գ��Ӧ���뵥�µ�ȫ����
 * 
 * ����Ԫר��
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
		// ����������������Ĭ�Ͽ��Գ��Ӧ���뵥�µ�ȫ����
		qrySql = StringUtil.isEmptyWithTrim(vo.getParentVO().getPk_item())?null:"1=1";
		return super.getContrastDialog(vo, pk_corp);
	}

}
