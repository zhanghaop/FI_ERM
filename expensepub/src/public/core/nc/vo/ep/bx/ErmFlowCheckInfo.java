package nc.vo.ep.bx;

import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.IAccessableBusiVO;
import nc.itf.tb.control.IBudgetControl;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.fibill.outer.FiBillAccessableBusiVO;
import nc.vo.fibill.outer.FiBillAccessableBusiVOProxy;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.control.NtbCtlInfoVO;

/**
 * @author twei
 * 
 * nc.vo.ep.bx.ErmFlowCheckInfo
 */
public class ErmFlowCheckInfo {
	
	
	public UFBoolean IsSameDept(JKBXVO vo) {
		JKBXHeaderVO head = vo.getParentVO();
		if (head == null)
			return UFBoolean.TRUE;
		if (head.getDeptid() == null || head.getFydeptid() == null)
			return UFBoolean.TRUE;
		return new UFBoolean(head.getDeptid().equals(head.getFydeptid()));
	}

	public UFBoolean IsSameCorp(JKBXVO vo) {
		JKBXHeaderVO head = vo.getParentVO();
		if (head == null)
			return UFBoolean.TRUE;
		if (head.getPk_org() == null || head.getFydwbm() == null)
			return UFBoolean.TRUE;
		return new UFBoolean(head.getDwbm().equals(head.getFydwbm()));
	}

	
	public UFBoolean checkNtb(JKBXVO vo) throws BusinessException {
		if (vo == null || vo.getParentVO() == null) {
			return UFBoolean.FALSE;
		} else {
			
			boolean istbbused = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
			if(!istbbused)
				return UFBoolean.FALSE;

			UFBoolean result = UFBoolean.FALSE;
			
			NtbCtlInfoVO tpcontrolvo = doNtbCheck(vo);
			
			if ((tpcontrolvo != null) && tpcontrolvo.isControl()){ // 控制
				result= UFBoolean.TRUE;
			} else if ((tpcontrolvo != null) && tpcontrolvo.isAlarm()) { // 预警
				result= UFBoolean.TRUE;
			}else if ((tpcontrolvo != null) && tpcontrolvo.isMayBeControl()) { // 柔性
				result= UFBoolean.TRUE;
			}
			
			return result;
		}
	}
	
	/**根据单据类型来生成VOS*/
	private static IFYControl[] getRealItems (IFYControl[] items,String billtype){
		return items;
	}
	
	
	//@see ErmNtbVO
	public static YsControlVO[] getSaveControlVos(IFYControl[] items, boolean iscontrary,DataRuleVO[] ruleVOs) {
		
		YsControlVO[] ps = null;
		Vector<YsControlVO> v = new Vector<YsControlVO>();
		for(int n=0;n<(ruleVOs==null?0:ruleVOs.length);n++){
			DataRuleVO ruleVo = ruleVOs[n];
			if(ruleVo == null)
				continue;
			/**单据类型/交易类型*/
			String billType =  ruleVo.getBilltype_code();
			/**预占的：PREFIND,执行：UFIND*/
			String methodFunc = ruleVo.getDataType();
	        /**如果是增加：true，如果是减少，false*/
			boolean isAdd = ruleVo.isAdd();  
			IFYControl[] itemsTemp = getRealItems(items, billType);
			for (int i = 0; i < items.length; i++) {
				YsControlVO psTemp = new YsControlVO();
				psTemp.setIscontrary(iscontrary);
				psTemp.setItems(new IFYControl[]{itemsTemp[i]});
				psTemp.setAdd(isAdd);
				psTemp.setMethodCode(methodFunc);
				v.addElement(psTemp);
			}
	    }
		ps = new YsControlVO[v.size()];
		v.copyInto(ps);
		
		return ps;
	}

	/**
	 * @param vo
	 * @throws BusinessException
	 */
	public static NtbCtlInfoVO doNtbCheck(JKBXVO vo) throws BusinessException {
		Vector<IAccessableBusiVO> v = new Vector<IAccessableBusiVO>();
		FiBillAccessableBusiVOProxy voProxyTemp = null;
		
		JKBXHeaderVO[] items = ErVOUtils.prepareBxvoItemToHeaderClone(vo);
		
		String actionCode = null;
		actionCode = BXConstans.ERM_NTB_APPROVE_KEY;
		JKBXHeaderVO head = vo.getParentVO();
		String billtype = head.getDjlxbm();
		boolean isExitParent = true; 
				
		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class).queryControlTactics(billtype, actionCode, isExitParent);
		YsControlVO[] ps = getSaveControlVos(items, false,ruleVos);
		for (YsControlVO item : ps) {
			voProxyTemp = new FiBillAccessableBusiVOProxy(item, BXConstans.ERM_PRODUCT_CODE_Lower);
			v.addElement(voProxyTemp);
		}
		if (v == null || v.size() < 1)
			return null;

		FiBillAccessableBusiVO[] psinfo = new FiBillAccessableBusiVO[] {};
		psinfo = v.toArray(psinfo);

		IBudgetControl bugetControl = NCLocator.getInstance().lookup(IBudgetControl.class);
		// 预算接口BO
		NtbCtlInfoVO tpcontrolvo = bugetControl.getCheckInfo(psinfo);
		return tpcontrolvo;
	}
}
