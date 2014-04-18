package nc.bs.erm.util;

import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.IBudgetControl;
import nc.itf.tb.control.IFormulaFuncName;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.pub.BusinessException;
import nc.vo.tb.control.DataRuleVO;

/**
 * Ԥ�㹤����
 * 
 * @author chenshuaia
 */
public class ErBudgetUtil {

	/**
	 * ��ѯ��������/�������ͣ�����ִ��Ԥ��Ŀ��Ʋ���
	 * 
	 * @param pk_billtype
	 * @return
	 * @throws BusinessException 
	 */
	public static DataRuleVO getBillYsExeDataRule(String pk_billtype) throws BusinessException{
		// ��ѯ������Ч
		DataRuleVO[] bxruleVos = NCLocator
				.getInstance()
				.lookup(IBudgetControl.class)
				.queryControlTactics(pk_billtype,
						BXConstans.ERM_NTB_APPROVE_KEY, false);
		
		if (bxruleVos == null || bxruleVos.length == 0) {
			// ��ֹֻ�ڱ��滷�������˿��Ʋ���
			bxruleVos = NCLocator
					.getInstance()
					.lookup(IBudgetControl.class)
					.queryControlTactics(pk_billtype,
							BXConstans.ERM_NTB_SAVE_KEY, false);
		}
		return bxruleVos == null || bxruleVos.length == 0? null:bxruleVos[0];
	}
	
	/**
	 * �������ε�������/�������ͣ���ǰ����Ԥ�㶯�����Ʋ��ԣ�������ε��ݵ�Ԥ����Ʋ���
	 * ע����ǰԤ����Ʋ�������ʱ��һ������ֻ������һ�����ο��Ʋ��ԣ�����billrules��󳤶���2
	 * 
	 * @param src_billtype
	 * @param billrules
	 * @return
	 * @throws BusinessException 
	 */
	public static DataRuleVO[] getSrcBillDataRule(String src_billtype,DataRuleVO[] billrules) throws BusinessException{
		
		DataRuleVO srcrule = getBillYsExeDataRule(src_billtype);
		
		if(srcrule == null || billrules == null || billrules.length == 0){
			// ���ε���û�п��Ʋ��Ի��ߵ�ǰ����û��Ԥ����Ʋ���������������ε���Ԥ��
			return null;
		}
		boolean isBillExerule = false;
		DataRuleVO billrule = billrules[0];
		if(IFormulaFuncName.UFIND.equals(billrule.getDataType())){
			// ִ���������ն���
			isBillExerule = true;
		}else{
			DataRuleVO[] ruleVos = NCLocator
			.getInstance()
			.lookup(IBudgetControl.class)
			.queryTakeDataTactics(billrule.getBilltype_code(),IFormulaFuncName.UFIND);
			
			isBillExerule = ruleVos == null || ruleVos.length ==0;
		}
		// clone ���ε��ݵĿ��Ʋ��ԣ������ε���ʹ��
		DataRuleVO[] newbillrules = new DataRuleVO[billrules.length];
		for (int i = 0; i < newbillrules.length; i++) {
			newbillrules[i] = (DataRuleVO) billrules[i].clone();
			
			DataRuleVO newdataRuleVO = newbillrules[i];
			newdataRuleVO.setPk_billtype(srcrule.getPk_billtype());
			newdataRuleVO.setBilltype_code(srcrule.getBilltype_code());
			newdataRuleVO.setBilltype_name(srcrule.getBilltype_name());
		}
		// �������յĿ��Ʋ��Խ��
		if(isBillExerule){
			// �ǵ�ǰ�������ն����������ѯ���ε�������Ԥ��ִ�в���
			if(newbillrules.length > 1){
				return new DataRuleVO[]{srcrule,newbillrules[1]};
			}else{
				return new DataRuleVO[]{srcrule};
			}
		}else{
			return newbillrules;
		}
		
	}
	
	/**
	 * ��ȡ�޸ĺ��Ԥ�����VO����
	 * 
	 * @param items
	 *            �޸ĺ�VO����
	 * @param items_old
	 *            �޸�ǰVO����
	 * @param ruleVOs
	 *            ���Ʋ���
	 * @return
	 */
	public static YsControlVO[] getEditControlVOs(IFYControl[] items, IFYControl[] items_old, DataRuleVO[] ruleVOs) {
		YsControlVO[] ps = null;
		Vector<YsControlVO> v = new Vector<YsControlVO>();
		for (int n = 0; n < (ruleVOs == null ? 0 : ruleVOs.length); n++) {
			DataRuleVO ruleVo = ruleVOs[n];
			if (ruleVo == null)
				continue;
			/** ��������/�������� */
			/** Ԥռ�ģ�PREFIND,ִ�У�UFIND */
			String methodFunc = ruleVo.getDataType();
			/** ��������ӣ�true������Ǽ��٣�false */
			boolean isAdd = ruleVo.isAdd();
			for (int i = 0; i < items.length; i++) {
				if (items[i].isYSControlAble()) {
					YsControlVO psTemp = new YsControlVO();
					psTemp.setIscontrary(false);
					psTemp.setItems(new IFYControl[] { items[i] });
					psTemp.setAdd(isAdd);
					psTemp.setMethodCode(methodFunc);
					v.addElement(psTemp);
				}
			}

			for (int i = 0; i < items_old.length; i++) {
				if (items_old[i].isYSControlAble()) {
					YsControlVO psTemp = new YsControlVO();
					psTemp.setIscontrary(true);
					psTemp.setItems(new IFYControl[] { items_old[i] });
					psTemp.setAdd(!isAdd);
					psTemp.setMethodCode(methodFunc);
					v.addElement(psTemp);
				}
			}
		}
		ps = new YsControlVO[v.size()];
		v.copyInto(ps);
		return ps;
	}
	
	/**
	 * ��ȡԤ�����VO
	 * �г�����дִ�кͻ�дԤռ�����ݲ�һ�£��ṩ�˷���
	 * @param items
	 * @param contrayItems 
	 * @param iscontrary
	 * @param ruleVOs
	 * @return
	 */
	public static YsControlVO[] getCtrlVOs(IFYControl[] items, IFYControl[] contrayItems, boolean iscontrary, DataRuleVO[] ruleVOs) {
		YsControlVO[] result = null;
		Vector<YsControlVO> resultVector = new Vector<YsControlVO>();
		for (int n = 0; n < (ruleVOs == null ? 0 : ruleVOs.length); n++) {
			DataRuleVO ruleVo = ruleVOs[n];
			if (ruleVo == null)
				continue;
			/** ��������/�������� */
			String billType = ruleVo.getBilltype_code();
			/** Ԥռ�ģ�PREFIND,ִ�У�UFIND */
			String methodFunc = ruleVo.getDataType();
			/** ��������ӣ�true������Ǽ��٣�false */
			boolean isAdd = true; 
			
			if (n == 1) {//�������ε����
				isAdd = iscontrary ? ruleVo.isAdd() : !ruleVo.isAdd();
			} else {
				isAdd = iscontrary ? !ruleVo.isAdd() : ruleVo.isAdd();
			}
			
			IFYControl[] fyItems = null;
			if (contrayItems == null || contrayItems.length == 0) {
				fyItems = items;
			} else if (n == 0) {
				fyItems = items;
			} else {
				fyItems = contrayItems;
			}
			
			for (int i = 0; i < fyItems.length; i++) {
				IFYControl item = fyItems[i];
				if (billType.equals(item.getParentBillType()) || billType.equals(item.getDjlxbm())) {
					if(item.isYSControlAble()){
						YsControlVO controlVo = new YsControlVO();
						controlVo.setIscontrary(iscontrary);
						controlVo.setItems(new IFYControl[] { item });
						controlVo.setAdd(isAdd);
						controlVo.setMethodCode(methodFunc);
						resultVector.addElement(controlVo);
					}
				}
			}
			
		}
		result = new YsControlVO[resultVector.size()];
		resultVector.copyInto(result);
		return result;
	}
	
	/**
	 * ��ȡԤ�����VO
	 * 
	 * @param items
	 * @param iscontrary
	 * @param ruleVOs
	 * @return
	 */
	public static YsControlVO[] getCtrlVOs(IFYControl[] items, boolean iscontrary, DataRuleVO[] ruleVOs) {
		return getCtrlVOs(items, null, iscontrary, ruleVOs);
	}
	
//	public static IFYControl[] getCostControlVOByCSVO(AggCostShareVO[] csVo,String pk_user) throws BusinessException {
//		IFYControl[] items;
//		// ��ѯ���ý�ת��vo
//		List<CostShareYsControlVO> itemList = new ArrayList<CostShareYsControlVO>();
//		List<String> depts = new ArrayList<String>();
//		List<String> corps = new ArrayList<String>();
//		List<String> centers = new ArrayList<String>();
//		for (int i = 0; i < csVo.length; i++) {
//			AggCostShareVO aggvo = (AggCostShareVO) csVo[i];
//			CShareDetailVO[] dtailvos = (CShareDetailVO[]) aggvo.getChildrenVO();
//			for (int j = 0; j < dtailvos.length; j++) {
//				if (dtailvos[j].getAssume_dept() != null) {
//					depts.add(dtailvos[j].getAssume_dept());
//				}
//				if (dtailvos[j].getPk_resacostcenter() != null) {
//					centers.add(dtailvos[j].getPk_resacostcenter());
//				}
//				corps.add(dtailvos[j].getAssume_org());
//			}
//		}
//		List<String> listPerson = getCorpPerson(corps,pk_user);
//		List<String> listDept = getPassDeptPerson(depts.toArray(new String[0]),pk_user);
//		List<String> listCenter = getPassCenterPerson(centers.toArray(new String[0]),pk_user);
//		
//		for (int i = 0; i < csVo.length; i++) {
//			AggCostShareVO aggvo = (AggCostShareVO) csVo[i];
//			CostShareVO headvo = (CostShareVO) aggvo.getParentVO();
//			CShareDetailVO[] dtailvos = (CShareDetailVO[]) aggvo.getChildrenVO();
//			for (int j = 0; j < dtailvos.length; j++) {
//				// ת������controlvo
//				CostShareYsControlVO cscontrolvo = new CostShareYsControlVO(headvo, dtailvos[j]);
//				if (csVo != null && csVo.length == 1) {
//					//��������ڵ���Ҫ����
//					if (listPerson.contains(dtailvos[j].getAssume_org())) {
//						// ���鸺��λ��Ԥ��ִ�����
//						itemList.add(cscontrolvo);
//						continue;
//					}
//					if (listDept.contains(dtailvos[j].getAssume_dept())) {
//						// ���鸺���ŵ�Ԥ�����
//						itemList.add(cscontrolvo);
//						continue;
//					} 
//					
//					if (listCenter !=null && listCenter.contains(dtailvos[j].getPk_resacostcenter())) {
//						// ���鸺��ɱ����ĵ�Ԥ��ִ�����
//						itemList.add(cscontrolvo);
//						continue;
//					}
//				}
//			}
//		}
//		items = itemList.toArray(new IFYControl[0]);
//		return items;
//	}
}
