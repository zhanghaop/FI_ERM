package nc.ui.erm.bx.ext.action;

import nc.bs.erm.ext.common.ErmConstExt;
import nc.ui.erm.billmanage.action.BatchContrastAction;
import nc.vo.ep.bx.JKBXVO;

/**
 * �����������ť����չӦ��
 * 
 * ���ݹ��������̵渶��������֧�ֳ���
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 *
 */
public class BxBatchContrastActionExt extends BatchContrastAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected String otherCheck(JKBXVO bxvo){
		String msg = "";
		String selectBillTypeCode = bxvo.getParentVO().getDjlxbm();
		if(ErmConstExt.Distributor_BX_Tradetype.equals(selectBillTypeCode)){
			msg = "�����̵渶���������ܽ��г������"+ ":"+ bxvo.getParentVO().getDjbh();
		}
		return msg;
	}

}
