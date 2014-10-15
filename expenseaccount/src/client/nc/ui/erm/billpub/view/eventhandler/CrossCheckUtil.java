package nc.ui.erm.billpub.view.eventhandler;

import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.workflow.util.ERMCrossCheckUtil;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;
/**
 * 交叉校验 
 * @author wangled
 */
public class CrossCheckUtil {
	public static  void checkRule(String headOrBody, String key,BillForm editor) throws BusinessException {
		String currentBillTypeCode = ((ErmBillBillManageModel)editor.getModel()).getCurrentBillTypeCode();
		DjLXVO currentDjlx = ((ErmBillBillManageModel)editor.getModel()).getCurrentDjlx(currentBillTypeCode);
		String djdl = currentDjlx.getDjdl();
		
		// 获得所属单据类型
		String parentBilltype = BXConstans.BX_DJDL.equals(djdl)?BXConstans.BX_DJLXBM:BXConstans.JK_DJLXBM;
		
		String[] orgFields = new String[]{JKBXHeaderVO.FYDWBM,JKBXHeaderVO.PK_ORG,JKBXHeaderVO.DWBM,JKBXHeaderVO.PK_ORG};
		ERMCrossCheckUtil.checkRule(headOrBody, key, editor, currentBillTypeCode, parentBilltype,orgFields);
	}
	 
}
