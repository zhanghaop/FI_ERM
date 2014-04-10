package nc.bs.er.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.IAccessableBusiVO;
import nc.itf.tb.control.IBudgetControl;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.verifynew.BusinessShowException;
import nc.vo.fibill.outer.FiBillAccessableBusiVO;
import nc.vo.fibill.outer.FiBillAccessableBusiVOProxy;
import nc.vo.pub.BusinessException;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.control.NtbCtlInfoVO;

/**
 * @author twei
 * 
 *         Ԥ�����BO nc.bs.arap.control.YsControlBO
 */
public class YsControlBO {

	public YsControlBO() {
		super();
	}

	/**
	 * Ԥ����� 
	 * @author modified by chendya
	 * @param items ʵ��Ԥ����Ƶĵ���
	 * @param iscontrary �Ƿ������(ɾ���ͷ���ЧΪ����,�������ЧΪ����)
	 * @param hascheck �Ƿ���У��
	 * @param ruleVOs ���ƹ���
	 * @return
	 * @throws BusinessException
	 */
	public String budgetCtrl(IFYControl[] items, boolean iscontrary,
			Boolean hascheck, DataRuleVO[] ruleVOs) throws BusinessException {

		YsControlVO[] ps = getCtrlVOs(items, iscontrary, ruleVOs);

		return ysControl(ps, hascheck, iscontrary);
	}

	/**
	 * @param items
	 *            Ҫ���Ƶ�Ԫ������
	 * @param items_old
	 *            Ҫ���Ƶ�Ԫ��ԭʼ����
	 * @throws BusinessException
	 */
	public String edit(IFYControl[] items, IFYControl[] items_old,
			Boolean hascheck, DataRuleVO[] ruleVOs) throws BusinessException {

		YsControlVO[] ps = getEditControlVOs(items, items_old, ruleVOs);

		return ysControl(ps, hascheck, false);

	}

	private YsControlVO[] getEditControlVOs(IFYControl[] items,
			IFYControl[] items_old, DataRuleVO[] ruleVOs) {

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
					// ����twei
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

	// @see ErmNtbVO
	public YsControlVO[] getCtrlVOs(IFYControl[] items,
			boolean iscontrary, DataRuleVO[] ruleVOs) {

		YsControlVO[] ps = null;
		Vector<YsControlVO> v = new Vector<YsControlVO>();
		for (int n = 0; n < (ruleVOs == null ? 0 : ruleVOs.length); n++) {
			DataRuleVO ruleVo = ruleVOs[n];
			if (ruleVo == null)
				continue;
			/** ��������/�������� */
			String billType = ruleVo.getBilltype_code();
			/** Ԥռ�ģ�PREFIND,ִ�У�UFIND */
			String methodFunc = ruleVo.getDataType();
			/** ��������ӣ�true������Ǽ��٣�false */
			boolean isAdd = ruleVo.isAdd();
			IFYControl[] itemsTemp = getRealItems(items, billType);
			for (int i = 0; i < items.length; i++) {
				YsControlVO psTemp = new YsControlVO();
				psTemp.setIscontrary(iscontrary);
				psTemp.setItems(new IFYControl[] { itemsTemp[i] });
				psTemp.setAdd(isAdd);
				psTemp.setMethodCode(methodFunc);
				v.addElement(psTemp);
			}
		}
		ps = new YsControlVO[v.size()];
		v.copyInto(ps);

		return ps;
	}

	/** ���ݵ�������������VOS */
	private IFYControl[] getRealItems(IFYControl[] items, String billtype) {
		return items;
	}

	/**
	 * ����Ԥ����ƽӿ�
	 * 
	 * @param ps
	 * @param hascheck
	 * @param iscontrary
	 * @return
	 * @throws BusinessShowException
	 */
	private String ysControl(YsControlVO[] ps, Boolean hascheck,
			boolean iscontrary) throws BusinessException {

		if (ps == null || ps.length == 0) {
			return null;
		}

		Map<String, List<YsControlVO>> map = new HashMap<String, List<YsControlVO>>();
		for (YsControlVO vo : ps) {
			String key = vo.getPKOrg();
			if (map.containsKey(key)) {
				List<YsControlVO> list = map.get(key);
				list.add(vo);
			} else {
				ArrayList<YsControlVO> list = new ArrayList<YsControlVO>();
				list.add(vo);
				map.put(key, list);
			}
		}
		Set<String> pkcorps = map.keySet();

		StringBuffer sb = new StringBuffer("");

		for (String corp : pkcorps) {
			String msg = ysControl(map.get(corp).toArray(new YsControlVO[] {}),
					corp, hascheck, iscontrary);
			if (msg != null && msg.length()>0) {
				sb.append(msg).append("\n");
			}
		}

		if (sb.length() != 0) {
			return sb.toString();
		}

		return null;
	}

	/**
	 * ��������Ԥ����ƺ��Ĵ���
	 * @param ps
	 * @param corp
	 * @param hascheck
	 * @param iscontrary
	 * @return
	 * @throws BusinessException
	 */
	private String ysControl(YsControlVO[] ps, String corp, Boolean hascheck,
			boolean iscontrary) throws BusinessException {
		
		// ���صĿ�����Ϣ
		StringBuffer retCtrlMsg = new StringBuffer();
		
		// �Ƿ�װԤ��
		boolean isInstallTBB = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return null;
		}
		Vector<IAccessableBusiVO> v = new Vector<IAccessableBusiVO>();
		for (YsControlVO vo : ps) {
			FiBillAccessableBusiVOProxy busiVOProxy = new FiBillAccessableBusiVOProxy(vo, BXConstans.ERM_PRODUCT_CODE_Lower);
			v.addElement(busiVOProxy);
		}
		if (v == null || v.size() == 0){
			return null;
		}
		FiBillAccessableBusiVO[] fiBillAccessableBusiVOs = v.toArray(new FiBillAccessableBusiVO[0]);
		
		//����Ԥ��ӿڲ�ѯ������Ϣ
		NtbCtlInfoVO ctrlInfoVO = NCLocator.getInstance().lookup(IBudgetControl.class).getControlInfo(fiBillAccessableBusiVOs);

		if(ctrlInfoVO==null){
			return null;
		}
		// ���Կ���
		if (ctrlInfoVO.isControl()) {
			String[] infos = ctrlInfoVO.getControlInfos();
			for (int j = 0; j < infos.length; j++) {
				retCtrlMsg.append("\n" + infos[j]);
			}
			//���Կ���ֱ���׳��쳣
			throw new BusinessException(retCtrlMsg.toString());
		}
		// Ԥ������
		else if (ctrlInfoVO.isAlarm()) {
			if (hascheck != null && Boolean.TRUE.equals(hascheck)) {
				return null;
			}
			if (iscontrary) {
				return null;
			}
			retCtrlMsg = new StringBuffer("");
			String[] seminfos = ctrlInfoVO.getAlarmInfos();
			for (int j = 0; j < seminfos.length; j++) {
				retCtrlMsg.append("\n" + seminfos[j]);
			}
			//Ԥ������ֱ���׳��쳣
			throw new BugetAlarmBusinessException(retCtrlMsg.toString());
		}
		// ���Կ���
		else if ((ctrlInfoVO != null) && ctrlInfoVO.isMayBeControl()) {
			//���Կ��Ƶ���������ʾ��Ϣ
			String[] infos = ctrlInfoVO.getFlexibleControlInfos();
			for (int j = 0; j < infos.length; j++) {
				retCtrlMsg.append("\n" + infos[j]);
			}
		}
		return retCtrlMsg.toString();
	}
}
