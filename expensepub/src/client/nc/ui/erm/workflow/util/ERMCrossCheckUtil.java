package nc.ui.erm.workflow.util;

import java.util.List;

import nc.ui.fipub.crossrule.CrossCheckBeforeUtil;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.workflow.config.ConfigAgent;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
/**
 * 交叉校验 
 * 
 */
@SuppressWarnings("restriction")
public class ERMCrossCheckUtil {

	/**
	 * 交叉校验 
	 * 
	 * @param headOrBody
	 * @param itemKey
	 * @param editor
	 * @param currentBillTypeCode
	 * @param djdl
	 * @param orgFields  new String[]{costentity_billitems,payentity_billitems,useentity_billitems,default}
	 * @throws BusinessException
	 */
	public static void checkRule(String headOrBody, String itemKey, BillForm editor, String currentBillTypeCode, String djdl,String[] orgFields) throws BusinessException {
		String key = BusiTypeVO.key;
		/*if("ma".equals(djdl)){
			key = BusiTypeVO.mattapp_key;
		}else if("cs".equals(djdl)){
			key = BusiTypeVO.costshare_key;
		}*/
		
		BusiTypeVO busTypeVO = getBusTypeVO(currentBillTypeCode, djdl,key);
		List<String> costentity_billitems = busTypeVO.getCostentity_billitems();
		List<String> payentity_billitems = busTypeVO.getPayentity_billitems();
		List<String> useentity_billitems = busTypeVO.getUseentity_billitems();
		
		String pk_orgField = "";
		if (costentity_billitems.contains(itemKey)) {
			pk_orgField = orgFields[0];
		} else if (payentity_billitems.contains(itemKey)) {
			pk_orgField = orgFields[1];
		} else if (useentity_billitems.contains(itemKey)) {
			pk_orgField = orgFields[2];
		} else {
			pk_orgField = orgFields[3];
		}
		//对于集团级档案不关联组织时，需要特殊处理一下
		if(StringUtil.isEmpty(pk_orgField.trim())){
			pk_orgField = JKBXHeaderVO.PK_ORG;
		}
		
		CrossCheckBeforeUtil util = new CrossCheckBeforeUtil(editor.getBillCardPanel(), currentBillTypeCode);
		
		util.handler(itemKey, pk_orgField, headOrBody.equals("Y"));
	}
	
	/**
	 * @return 取业务类型VO (***.xml定义内容)
	 * @see BusiTypeVO
	 */
	@SuppressWarnings("static-access")
	public static BusiTypeVO getBusTypeVO(String djlxbm, String djdl,String key) {
		BusiTypeVO busiTypeVO = null;
		busiTypeVO = (BusiTypeVO) ((ConfigAgent)ConfigAgent.getInstance()).getCommonVO(key,djlxbm);
		
		if(busiTypeVO==null){
			busiTypeVO = (BusiTypeVO) ConfigAgent.getInstance().getCommonVO(key,djdl);
			ConfigAgent config = (ConfigAgent) ConfigAgent.getInstance();
			
			BusiTypeVO vo=(BusiTypeVO) busiTypeVO.clone();
			vo.setId(djlxbm);
			config.getCache().putCommonVO(key, vo);
		}
		
		return busiTypeVO;
	}
	 
}
