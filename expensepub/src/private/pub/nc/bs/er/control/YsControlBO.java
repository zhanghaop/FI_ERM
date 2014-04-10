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
 *         预算控制BO nc.bs.arap.control.YsControlBO
 */
public class YsControlBO {

	public YsControlBO() {
		super();
	}

	/**
	 * 预算控制 
	 * @author modified by chendya
	 * @param items 实现预算控制的单据
	 * @param iscontrary 是否反向控制(删除和反生效为反向,保存和生效为正向)
	 * @param hascheck 是否再校验
	 * @param ruleVOs 控制规则
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
	 *            要控制的元素数组
	 * @param items_old
	 *            要控制的元素原始数组
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
			/** 单据类型/交易类型 */
			/** 预占的：PREFIND,执行：UFIND */
			String methodFunc = ruleVo.getDataType();
			/** 如果是增加：true，如果是减少，false */
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
					// 问下twei
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
			/** 单据类型/交易类型 */
			String billType = ruleVo.getBilltype_code();
			/** 预占的：PREFIND,执行：UFIND */
			String methodFunc = ruleVo.getDataType();
			/** 如果是增加：true，如果是减少，false */
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

	/** 根据单据类型来生成VOS */
	private IFYControl[] getRealItems(IFYControl[] items, String billtype) {
		return items;
	}

	/**
	 * 调用预算控制接口
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
	 * 报销管理预算控制核心代码
	 * @param ps
	 * @param corp
	 * @param hascheck
	 * @param iscontrary
	 * @return
	 * @throws BusinessException
	 */
	private String ysControl(YsControlVO[] ps, String corp, Boolean hascheck,
			boolean iscontrary) throws BusinessException {
		
		// 返回的控制信息
		StringBuffer retCtrlMsg = new StringBuffer();
		
		// 是否安装预算
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
		
		//调用预算接口查询控制信息
		NtbCtlInfoVO ctrlInfoVO = NCLocator.getInstance().lookup(IBudgetControl.class).getControlInfo(fiBillAccessableBusiVOs);

		if(ctrlInfoVO==null){
			return null;
		}
		// 刚性控制
		if (ctrlInfoVO.isControl()) {
			String[] infos = ctrlInfoVO.getControlInfos();
			for (int j = 0; j < infos.length; j++) {
				retCtrlMsg.append("\n" + infos[j]);
			}
			//刚性控制直接抛出异常
			throw new BusinessException(retCtrlMsg.toString());
		}
		// 预警控制
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
			//预警控制直接抛出异常
			throw new BugetAlarmBusinessException(retCtrlMsg.toString());
		}
		// 柔性控制
		else if ((ctrlInfoVO != null) && ctrlInfoVO.isMayBeControl()) {
			//柔性控制弹出警告提示信息
			String[] infos = ctrlInfoVO.getFlexibleControlInfos();
			for (int j = 0; j < infos.length; j++) {
				retCtrlMsg.append("\n" + infos[j]);
			}
		}
		return retCtrlMsg.toString();
	}
}
