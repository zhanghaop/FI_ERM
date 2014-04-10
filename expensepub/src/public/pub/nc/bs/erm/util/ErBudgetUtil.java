package nc.bs.erm.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.CostShareYsControlVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.tb.control.DataRuleVO;

/**
 * Ԥ�㹤����
 * 
 * @author chenshuaia
 */
public class ErBudgetUtil {

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
	
	public static IFYControl[] getCostControlVOByCSVO(AggCostShareVO[] csVo,String pk_user) throws BusinessException {
		IFYControl[] items;
		// ��ѯ���ý�ת��vo
		List<CostShareYsControlVO> itemList = new ArrayList<CostShareYsControlVO>();
		List<String> depts = new ArrayList<String>();
		List<String> corps = new ArrayList<String>();
		List<String> centers = new ArrayList<String>();
		for (int i = 0; i < csVo.length; i++) {
			AggCostShareVO aggvo = (AggCostShareVO) csVo[i];
			CShareDetailVO[] dtailvos = (CShareDetailVO[]) aggvo.getChildrenVO();
			for (int j = 0; j < dtailvos.length; j++) {
				if (dtailvos[j].getAssume_dept() != null) {
					depts.add(dtailvos[j].getAssume_dept());
				}
				if (dtailvos[j].getPk_resacostcenter() != null) {
					centers.add(dtailvos[j].getPk_resacostcenter());
				}
				corps.add(dtailvos[j].getAssume_org());
			}
		}
		List<String> listPerson = getCorpPerson(corps,pk_user);
		List<String> listDept = getPassDeptPerson(depts.toArray(new String[0]),pk_user);
		List<String> listCenter = getPassCenterPerson(centers.toArray(new String[0]),pk_user);
		
		for (int i = 0; i < csVo.length; i++) {
			AggCostShareVO aggvo = (AggCostShareVO) csVo[i];
			CostShareVO headvo = (CostShareVO) aggvo.getParentVO();
			CShareDetailVO[] dtailvos = (CShareDetailVO[]) aggvo.getChildrenVO();
			for (int j = 0; j < dtailvos.length; j++) {
				// ת������controlvo
				CostShareYsControlVO cscontrolvo = new CostShareYsControlVO(headvo, dtailvos[j]);
				if (csVo != null && csVo.length == 1) {
					//��������ڵ���Ҫ����
					if (listPerson.contains(dtailvos[j].getAssume_org())) {
						// ���鸺��λ��Ԥ��ִ�����
						itemList.add(cscontrolvo);
						continue;
					}
					if (listDept.contains(dtailvos[j].getAssume_dept())) {
						// ���鸺���ŵ�Ԥ�����
						itemList.add(cscontrolvo);
						continue;
					} 
					
					if (listCenter !=null && listCenter.contains(dtailvos[j].getPk_resacostcenter())) {
						// ���鸺��ɱ����ĵ�Ԥ��ִ�����
						itemList.add(cscontrolvo);
						continue;
					}
					
				}
			}
		}
		items = itemList.toArray(new IFYControl[0]);
		return items;
	}
	
	/*
	 * �Ƿ��ǿ粿�ŷ�̯
	 * (��̯��˾�ͷ��óе���˾��ͬ����̯��Ϣ�з�̯��˾һ�£����Ų�Ϊnull)
	 */
	public static Boolean isSameAssumeDept(AggCostShareVO vo) {
		CShareDetailVO[] detailvos = (CShareDetailVO[]) vo.getChildrenVO();
		if (detailvos == null || detailvos.length == 0 || !isShareAssumeDept(vo).booleanValue()) {
			return false;
		}

		Set<String> deptSet = new HashSet<String>();
		for (int i = 0; i < detailvos.length; i++) {
			// ����Ϊ��ʱ,����false;
			if (detailvos[i].getAssume_dept() == null) {
				return false;
			}
			deptSet.add(detailvos[i].getAssume_dept());
		}

		if (deptSet.size() <= 1) {// ���Ž���һ������·���false
			return false;
		}
		return true;
	}
	
	// ��½�û��Ƿ�Ϊ��ǰ��˾�ķ��õ�λ������
	public static List<String> getCorpPerson(List<String> corps,String pk_loginUser) {
		List<String> corpList = null;
		try {
			corpList = NCLocator.getInstance().lookup(IBXBillPrivate.class).getPassedCorpOfPerson(corps.toArray(new String[0]), pk_loginUser);
		} catch (ComponentException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		} catch (BusinessException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}
		return corpList;
	}
	
	// ��½�û��Ƿ�Ϊ��ǰ��½��˾�Ĳ��Ÿ�����
	public static List<String> getPassDeptPerson(String[] depts,String pk_loginUser) {
		List<String> deptList = null;
		try {
			deptList = NCLocator.getInstance().lookup(IBXBillPrivate.class).getPassedDeptOfPerson(depts, pk_loginUser);
		} catch (ComponentException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		} catch (BusinessException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}

		// ���óе����Ź���
		return deptList;
	}
	// ��½�û��Ƿ�Ϊ��ǰ��½��˾�Ĳ��Ÿ�����
	public static List<String> getPassCenterPerson(String[] centers,String pk_loginUser) {
		List<String> centerList = null;
		try {
			centerList = NCLocator.getInstance().lookup(IBXBillPrivate.class).getPassedCenterOfPerson(centers, pk_loginUser);
		} catch (ComponentException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		} catch (BusinessException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}
		
		// ���óе����Ź���
		return centerList;
	}
	
	/*
	 * �Ƿ����֯��̯
	 * (���óе���˾�ڷ�̯��˾��ͬ)
	 */
	public static Boolean isSameAssumeOrg(AggCostShareVO vo) {
		CShareDetailVO[] detailvos = (CShareDetailVO[]) vo.getChildrenVO();
		String fydwbm = ((CostShareVO)vo.getParentVO()).getFydwbm();
		
		if (detailvos == null || detailvos.length == 0) {
			return Boolean.FALSE;
		}
		
		for (int i = 0; i < detailvos.length; i++) {
			if (!fydwbm.equals(detailvos[i].getAssume_org())) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	/*
	 * �Ƿ��ɱ����ķ�̯ (�ɱ������ڷ�̯�ɱ����Ĳ�ͬ)
	 */
	public static Boolean isSameCenter(AggCostShareVO vo) {
		CShareDetailVO[] detailvos = (CShareDetailVO[]) vo.getChildrenVO();

		if (detailvos == null || detailvos.length == 0 || !isShareAssumeDept(vo).booleanValue()) {
			return false;
		}

		Set<String> centerSet = new HashSet<String>();
		for (int i = 0; i < detailvos.length; i++) {
			// �ɱ�����Ϊ��ʱ,����false;
			if (detailvos[i].getPk_resacostcenter() == null) {
				return false;
			}
			centerSet.add(detailvos[i].getPk_resacostcenter());
		}

		if (centerSet.size() <= 1) {// ���Ž���һ������·���false
			return false;
		}
		return true;
	}
	
	/**
	 * �ж��Ƿ���ͬ��˾��̯�������ͬ��˾��̯���򷵻�true
	 * @param vo
	 * @return
	 */
	private static UFBoolean isShareAssumeDept(AggCostShareVO vo) {
		CShareDetailVO[] detailvos = (CShareDetailVO[]) vo.getChildrenVO();
		String fydwbm = ((CostShareVO)vo.getParentVO()).getFydwbm();
		
		if (detailvos == null || detailvos.length == 0) {
			return UFBoolean.FALSE;
		}
		
		for (int i = 0; i < detailvos.length; i++) {
			if (!fydwbm.equals(detailvos[i].getAssume_org())) {
				return UFBoolean.FALSE;
			}
		}
		return UFBoolean.TRUE;
	}
}
