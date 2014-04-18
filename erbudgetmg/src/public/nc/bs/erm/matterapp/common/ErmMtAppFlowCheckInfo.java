package nc.bs.erm.matterapp.common;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

import nc.bs.erm.util.ErBudgetUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.IAccessableBusiVO;
import nc.itf.tb.control.IBudgetControl;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fibill.outer.FiBillAccessableBusiVO;
import nc.vo.fibill.outer.FiBillAccessableBusiVOProxy;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.control.NtbCtlInfoVO;

/**
 * 审批流中检测，以及单据函数
 * @author chenshuaia
 */
public class ErmMtAppFlowCheckInfo {
	
	public ErmMtAppFlowCheckInfo(){
		
	}
	/**
	 * 单据函数：是否超预算
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public UFBoolean checkNtb(AggMatterAppVO vo) throws BusinessException {
		if (vo == null || vo.getParentVO() == null) {
			return UFBoolean.FALSE;
		} else {

			boolean istbbused = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
			if (!istbbused)
				return UFBoolean.FALSE;

			UFBoolean result = UFBoolean.FALSE;

			NtbCtlInfoVO tpcontrolvo = doNtbCheck(vo);

			if ((tpcontrolvo != null) && tpcontrolvo.isControl()) { // 控制
				result = UFBoolean.TRUE;
			} else if ((tpcontrolvo != null) && tpcontrolvo.isAlarm()) { // 预警
				result = UFBoolean.TRUE;
			} else if ((tpcontrolvo != null) && tpcontrolvo.isMayBeControl()) { // 柔性
				result = UFBoolean.TRUE;
			}

			return result;
		}
	}
	
	/**
	 * 获取预算控制Vo
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public static NtbCtlInfoVO doNtbCheck(AggMatterAppVO vo) throws BusinessException {
		Vector<IAccessableBusiVO> iABusiVoVector = new Vector<IAccessableBusiVO>();
		FiBillAccessableBusiVOProxy voProxyTemp = null;

		String actionCode = BXConstans.ERM_NTB_APPROVE_KEY;
		MatterAppVO head = vo.getParentVO();
		String billtype = head.getPk_tradetype();
		boolean isExitParent = true;

		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class).queryControlTactics(billtype, actionCode, isExitParent);

		
		IFYControl[] ps = MatterAppUtils.getMtAppYsControlVOs(new AggMatterAppVO[]{vo});
		
		YsControlVO[] controlVos = ErBudgetUtil.getCtrlVOs(ps, false, ruleVos);

		for (YsControlVO item : controlVos) {
			voProxyTemp = new FiBillAccessableBusiVOProxy(item);
			iABusiVoVector.addElement(voProxyTemp);
		}

		if (iABusiVoVector == null || iABusiVoVector.size() < 1)
			return null;

		FiBillAccessableBusiVO[] psinfo = new FiBillAccessableBusiVO[] {};
		psinfo = iABusiVoVector.toArray(psinfo);
		IBudgetControl bugetControl = NCLocator.getInstance().lookup(IBudgetControl.class);
		// 预算接口BO
		NtbCtlInfoVO tpcontrolvo = bugetControl.getCheckInfo(psinfo);
		return tpcontrolvo;
	}
	
	/**
	 * 单据函数：是否是跨费用承担单位
	 * @param vo
	 * @return
	 * @throws BusinessException
	 * 
	 * * TODO
	 * wangle
	 */
	public UFBoolean isAssumeOrg(AggMatterAppVO vo) throws BusinessException{
		UFBoolean flag = UFBoolean.FALSE;
		if(vo!=null){
			MtAppDetailVO[] childrenVO = vo.getChildrenVO();
			String pkOrg = vo.getParentVO().getPk_org();
			Set<String> voset=new LinkedHashSet<String>();
			if(pkOrg != null ){
				voset.add(pkOrg);//判断表头“财务组织”和表体“费用承担单位”是否一致
			}
			if(childrenVO!=null && childrenVO.length != 0){
				for (MtAppDetailVO mtAppDetailVO : childrenVO) {
					voset.add(mtAppDetailVO.getAssume_org());
				}
			}
			if(voset.size()>1){
				flag=UFBoolean.TRUE;
			}
		}
		return flag;
	}
	
	/**
	 * 单据函数：是否是跨费用承担部门
	 * @param vo
	 * @return
	 * @throws BusinessException
	 * * TODO
	 * wangle
	 */
	public UFBoolean isAssumeDept(AggMatterAppVO vo) throws BusinessException{
		UFBoolean flag = UFBoolean.FALSE;
		if(vo!=null){
			MtAppDetailVO[] childrenVO = vo.getChildrenVO();
			String assume_Dept = vo.getParentVO().getAssume_dept();
			Set<String> voset=new LinkedHashSet<String>();
			if(assume_Dept != null ){
				voset.add(assume_Dept);//判断表头“费用承担部门”和表体“费用承担部门”是否一致
			}
			for (MtAppDetailVO mtAppDetailVO : childrenVO) {
				voset.add(mtAppDetailVO.getAssume_dept());
			}
			if(voset.size()>1){
				flag=UFBoolean.TRUE;
			}
		}
		return flag;
	}
	
	/**
	 * 单据函数：是否是跨成本中心
	 * @param vo
	 * @return
	 * @throws BusinessException
	 * wangle
	 * TODO
	 */
	public UFBoolean isCostcenter(AggMatterAppVO vo) throws BusinessException{
		UFBoolean flag = UFBoolean.FALSE;
		if(vo!=null && (vo.getChildrenVO()!=null && vo.getChildrenVO().length>1)){
			MtAppDetailVO[] childrenVO = vo.getChildrenVO();
			Set<String> voset=new LinkedHashSet<String>();
			for (MtAppDetailVO mtAppDetailVO : childrenVO) {
				voset.add(mtAppDetailVO.getPk_resacostcenter());
			}
			if(voset.size()>1){
				flag=UFBoolean.TRUE;
			}
		}
		return flag;
	}

	/**
	 * 单据函数：申请部门是否等于费用承担部门
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public UFBoolean isSameDept(AggMatterAppVO vo) throws BusinessException {
		UFBoolean flag = UFBoolean.FALSE;
		if (vo != null && vo.getParentVO() != null) {
			MatterAppVO head = ((AggMatterAppVO)vo).getParentVO();
			if (head.getAssume_dept() == null) {
				flag = UFBoolean.FALSE;
			} else {
				flag = UFBoolean.valueOf(head.getAssume_dept().equals(head.getApply_dept()));
			}
		}
		return flag;
	}
}
