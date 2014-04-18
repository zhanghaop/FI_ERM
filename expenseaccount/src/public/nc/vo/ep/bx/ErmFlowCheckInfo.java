package nc.vo.ep.bx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.prv.IWriteBackPrivate;
import nc.itf.er.reimtype.IReimTypeService;
import nc.itf.erm.ntb.IBXYsControlService;
import nc.itf.fi.pub.SysInit;
import nc.itf.pim.budget.pub.IbudgetExe4ExpenseBill;
import nc.itf.tb.control.IBudgetControl;
import nc.pubitf.erm.costshare.IErmCostShareYsControlService;
import nc.pubitf.erm.matterappctrl.IMatterAppCtrlService;
import nc.ui.pub.formulaparse.FormulaParse;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.arap.bx.util.BxUIControlUtil;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlInfoVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pm.budget.pub.BudgetEnum;
import nc.vo.pm.budget.pub.BudgetReturnMSG;
import nc.vo.pm.budget.pub.BudgetReturnVO;
import nc.vo.pmbd.budgetctrl.BudgetCtrlTypeConst;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.tb.control.DataRuleVO;
import nc.vo.tb.control.NtbCtlInfoVO;

/**
 * 审批流中检测，以及单据函数
 * 
 * @author twei nc.vo.ep.bx.ErmFlowCheckInfo
 */
public class ErmFlowCheckInfo {

	public UFBoolean isSameDept(JKBXVO vo) {
		JKBXHeaderVO head = vo.getParentVO();
		if (head == null)
			return UFBoolean.TRUE;
		if (head.getDeptid() == null || head.getFydeptid() == null)
			return UFBoolean.TRUE;
		return UFBoolean.valueOf(head.getDeptid().equals(head.getFydeptid()));
	}

	public UFBoolean isSameCorp(JKBXVO vo) {
		JKBXHeaderVO head = vo.getParentVO();
		if (head == null)
			return UFBoolean.TRUE;
		if (head.getPk_org() == null || head.getFydwbm() == null)
			return UFBoolean.TRUE;
		return UFBoolean.valueOf(head.getDwbm().equals(head.getFydwbm()));
	}

	/**
	 * 是否超申请
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public UFBoolean checkMatter(JKBXVO vo) throws BusinessException {
		UFBoolean isExceed = UFBoolean.FALSE;
		IMatterAppCtrlService service = NCLocator.getInstance().lookup(IMatterAppCtrlService.class);

		List<IMtappCtrlBusiVO> mtBusiVoList = NCLocator.getInstance().lookup(IWriteBackPrivate.class)
				.construstBusiDataForWriteBack(new JKBXVO[] { vo }, ErmEventType.TYPE_SIGN_BEFORE);

		try {
			MtappCtrlInfoVO ctrlInfo = service.matterappValidate(mtBusiVoList.toArray(new JKBXMtappCtrlBusiVO[] {}));
			isExceed = UFBoolean.valueOf(ctrlInfo.isExceed());
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}

		return isExceed;

	}
	/**
	 * 是否超标准，需求最后说要做的，变态啊
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	/**
	 * 借款报销单多个子表的编码
	 * 与单据模版的页签编码对应
	 */
	public static String[] tableCodes =  new String[]{"er_busitem",
		   		"costsharedetail","er_bxcontrast","er_tbbdetail",
		   		"accrued_verify","jk_contrast","jk_busitem"};
	public UFBoolean checkReimRule(JKBXVO vo) throws BusinessException {
		UFBoolean isExceed = UFBoolean.FALSE;
		if (vo == null || vo.getParentVO() == null)
			return isExceed;
		//比较集团级标准,若集团级已经超标准，则直接返回
		isExceed = doCheckReimRule(vo,vo.getParentVO().getPk_group(),ReimRulerVO.PKORG);
		if(isExceed.booleanValue() == true)
			return isExceed;
		//否则，继续比较组织级标准
		isExceed = doCheckReimRule(vo,vo.getParentVO().getPk_group(),getPkOrg(vo));
		return isExceed;
	}
	
	// 根据集团级参数“报销标准适用规则”,来取组织
	public String getPkOrg(JKBXVO vo){
		//获取组织
		String pk_org = null;
		try {
			String PARAM_ER8 = SysInit.getParaString(vo.getParentVO().getPk_group(), BXParamConstant.PARAM_ER_REIMRULE);
			if (PARAM_ER8 != null) {
				if (PARAM_ER8.equals(BXParamConstant.ER_ER_REIMRULE_PK_ORG)) {
					pk_org = (String) vo.getParentVO().getAttributeValue(JKBXHeaderVO.PK_ORG);
				} else if (PARAM_ER8.equals(BXParamConstant.ER_ER_REIMRULE_OPERATOR_ORG)) {
					pk_org = (String) vo.getParentVO().getAttributeValue(JKBXHeaderVO.DWBM);
				} else if (PARAM_ER8.equals(BXParamConstant.ER_ER_REIMRULE_ASSUME_ORG)) {
					pk_org = (String) vo.getParentVO().getAttributeValue(JKBXHeaderVO.FYDWBM);
				}
			}
		} catch (BusinessException e1) {
			ExceptionHandler.consume(e1);
		}
		return pk_org;
	}
	
	//报销标准如果设置了公式，需要进行转换
	private FormulaParse f;
	private FormulaParse getFormulaParse(){
		if(f==null)
			f = new FormulaParse();
		return f;
	}
	
	private UFBoolean doCheckReimRule(JKBXVO bxvo,String Pk_group,String pk_org) 
		throws BusinessException {
		UFBoolean isExceed = UFBoolean.FALSE;
		String djlxbm = bxvo.getParentVO().getDjlxbm();
		if (djlxbm == null || Pk_group==null || pk_org==null)
			return isExceed;
		List<ReimRulerVO> ruleVOs;
		List<ReimRuleDimVO> dimVOs;
		try {
			ruleVOs = NCLocator.getInstance().lookup(IReimTypeService.class)
			.queryReimRuler(djlxbm, Pk_group,pk_org);
			dimVOs = NCLocator.getInstance().lookup(IReimTypeService.class)
			.queryReimDim(djlxbm, Pk_group,pk_org);
			if(dimVOs!=null && dimVOs.size()>=0 && ruleVOs!=null && ruleVOs.size()>0){
				String regEx = "[^a-zA-Z0-9]";
				Pattern p = Pattern.compile(regEx);
				// 根据表头获得相应报销标准
				List<ReimRulerVO> matchReimRule = BxUIControlUtil.getMatchReimRuleByHead(bxvo, ruleVOs,dimVOs);
				//单据对应项 为子表的项，需要拿出来与表体一行一行进行比较
				//例如报销类型就对应子表的属性，需要控制子表的项，dims中存储<报销标准列的item,billrefcode,数据类型名称>
				List<String> dims = new ArrayList<String>();
				for(ReimRuleDimVO dimvo:dimVOs)
				{
					for(String tablecode:tableCodes)
						if(dimvo.getBillrefcode()!=null && dimvo.getBillrefcode().startsWith(tablecode))
						{
							dims.add(dimvo.getCorrespondingitem()+","+dimvo.getBillrefcode().substring(dimvo.getBillrefcode().indexOf(".")+1)
									+","+dimvo.getDatatypename());
						}
				}
				for(ReimRulerVO rule:matchReimRule){
					//提取标准的单据控制项,在表体中找到此项并与标准值进行比较
					String controlitem = rule.getControlitem_name();
					if(controlitem==null)
						continue;
					String formula = rule.getControlformula();
					String[] keys = controlitem.split(ReimRulerVO.REMRULE_SPLITER);
					String tableCode = keys[0];
					String itemkey = keys[1];
					CircularlyAccessibleValueObject[] bodyValueVOs = bxvo.getTableVO(tableCode);
					if (bodyValueVOs != null) {
						int row = -1;
						// 对对应子表下的所有行进行遍历
						for (CircularlyAccessibleValueObject body : bodyValueVOs) {
							row++;
							// 查看表体中对应字段值是否与标准相同
							boolean match = true;
							for(String str:dims){
								String[] itemvalues = str.split(",");
								if(itemvalues.length>3 && itemvalues[2] != null){
									if (BxUIControlUtil.doSimpleNoEquals(itemvalues[2], Pk_group,
											rule.getAttributeValue(itemvalues[0]), body.getAttributeValue(itemvalues[1]))) {
										match = false;
										break;
									}
								}else{
									if (BxUIControlUtil.doSimpleNoEquals(rule.getAttributeValue(itemvalues[0]), body.getAttributeValue(itemvalues[1]))) {
										match = false;
										break;
									}
								}
							}
							if (match) {
								if(body.getAttributeValue(itemkey)==null)
									continue;
								//比较是否超标准
								Double amount = Double.parseDouble((body.getAttributeValue(itemkey).toString()));
								Double standard = rule.getAmount().doubleValue();
								if(formula!=null){
									FormulaParse f = getFormulaParse();
									f.setExpressArray(new String[]{formula});
									Matcher m = p.matcher(formula);
									String[] variables = m.replaceAll(" ").trim().split(" ");
									for(String variable:variables){
										if(variable!=null && !variable.equals(""))
											if(body.getAttributeValue(variable) != null){
												f.addVariable(variable, Double.parseDouble(body.getAttributeValue(variable).toString()));
											}
	//									f.addVariable("defitem9", new UFDouble(3));
									}
									if(f.getValue()!=null)
										standard = Double.parseDouble(f.getValue());
								}
								if(amount > standard){
									isExceed=UFBoolean.TRUE;
									break;
								}
							}
						}
					}
					//若已经超过标准，则无需再比较
					if(isExceed==UFBoolean.TRUE)
						break;
				}
			}
			return isExceed;
		} catch (BusinessException e) {
			nc.bs.logging.Log.getInstance("ermExceptionLog").error(e);
			ExceptionHandler.consume(e);
			return UFBoolean.FALSE;
		}
		
	}

	/*
	 * 是否跨组织分摊 (费用承担公司与分摊公司不同)
	 */
	public UFBoolean isSameAssumeOrg(JKBXVO vo) {
		CShareDetailVO[] detailvos = vo.getcShareDetailVo();
		if (detailvos == null || detailvos.length == 0) {
			return UFBoolean.FALSE;
		}

		return UFBoolean.valueOf(!isShareAssumeDept(vo).booleanValue());
	}

	/*
	 * 是否是跨部门分摊 (分摊公司和费用承担公司相同，分摊信息中分摊公司一致，部门不为null) 不一致时返回TRUE 一致时返回FALSE
	 */
	public UFBoolean isSameAssumeDept(JKBXVO vo) {
		CShareDetailVO[] detailvos = vo.getcShareDetailVo();

		if (detailvos == null || detailvos.length == 0 || !isShareAssumeDept(vo).booleanValue()) {
			return UFBoolean.FALSE;
		}

		Set<String> deptSet = new HashSet<String>();
		for (int i = 0; i < detailvos.length; i++) {
			// 部门为空时,返回false;
			if (detailvos[i].getAssume_dept() != null) {
				deptSet.add(detailvos[i].getAssume_dept());
			}
		}

		if (deptSet.size() == 1) {//
			if (deptSet.contains(vo.getParentVO().getFydeptid())) {
				return UFBoolean.FALSE;
			}
		} else if (deptSet.size() == 0) {
			return UFBoolean.FALSE;
		}

		return UFBoolean.TRUE;
	}

	/*
	 * 是否跨成本中心分摊 (成本中心与分摊成本中心不同)
	 */
	public UFBoolean isSameCenter(JKBXVO vo) {
		CShareDetailVO[] detailvos = vo.getcShareDetailVo();
		if (detailvos == null || detailvos.length == 0 || !isShareAssumeDept(vo).booleanValue()) {
			return UFBoolean.FALSE;
		}

		Set<String> centerSet = new HashSet<String>();
		for (int i = 0; i < detailvos.length; i++) {
			// 成本中心为空时,返回false;
			if (detailvos[i].getPk_resacostcenter() == null) {
				return UFBoolean.FALSE;
			}
			centerSet.add(detailvos[i].getPk_resacostcenter());
		}

		if (centerSet.size() <= 1) {// 部门仅有一个情况下返回false
			return UFBoolean.FALSE;
		}
		return UFBoolean.TRUE;
	}

	public UFBoolean checkNtb(JKBXVO vo) throws BusinessException {
		if (vo == null || vo.getParentVO() == null) {
			return UFBoolean.FALSE;
		} else {

			boolean istbbused = BXUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
			if (!istbbused) {
				return UFBoolean.FALSE;
			}

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

	// 登陆用户是否为当前公司的费用单位负责人
	private static List<String> getCorpsByPrincipal(List<String> corps) {
		List<String> corpList = null;
		try {
			String pkUser = InvocationInfoProxy.getInstance().getUserId();
			corpList = getIBXBillPrivate().getPassedCorpOfPerson(corps.toArray(new String[0]), pkUser);
		} catch (ComponentException e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		} catch (Exception e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}
		return corpList;
	}

	// 登陆用户是否为当前登陆公司的部门负责人
	private static List<String> getDeptsByPrincipal(List<String> depts) {
		List<String> deptList = null;
		String pkUser = InvocationInfoProxy.getInstance().getUserId();
		try {
			deptList = getIBXBillPrivate().getPassedDeptOfPerson(depts.toArray(new String[0]), pkUser);
		} catch (Exception e) {
			nc.bs.logging.Logger.error(e.getMessage(), e);
		}

		// 费用承担部门过滤
		return deptList;
	}

	private static IBXBillPrivate getIBXBillPrivate() throws ComponentException {
		return NCLocator.getInstance().lookup(IBXBillPrivate.class);
	}

	/**
	 * @param vo
	 * @throws BusinessException
	 */
	public static NtbCtlInfoVO doNtbCheck(JKBXVO vo) throws BusinessException {
		JKBXHeaderVO head = vo.getParentVO();
		String billtype = head.getDjlxbm();
		String actionCode = BXConstans.ERM_NTB_APPROVE_KEY;
		boolean isExitParent = true;

		DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
				.queryControlTactics(billtype, actionCode, isExitParent);

		if (ruleVos == null) {
			return null;
		}

		NtbCtlInfoVO tpcontrolvo = null;
		fillUpMapf(vo);

		if (!ErmForCShareUtil.isHasCShare(vo)) {// 无费用分摊，按报销单
			IBXYsControlService service = NCLocator.getInstance().lookup(IBXYsControlService.class);
			List<YsControlVO> ysControlVoList = service.getYsControlVos(new JKBXVO[] { vo }, false, actionCode);

			if (ysControlVoList != null && ysControlVoList.size() > 0) {
				IBudgetControl bugetControl = NCLocator.getInstance().lookup(IBudgetControl.class);
				// 预算接口BO
				tpcontrolvo = bugetControl.getCheckInfo(ysControlVoList.toArray(new YsControlVO[0]));
			}
		} else {
			tpcontrolvo = dealCostYsControl(vo, actionCode);
		}
		return tpcontrolvo;
	}

	private static NtbCtlInfoVO dealCostYsControl(JKBXVO vo, String actionCode) throws BusinessException {
		if(vo == null){
			return null;
		}

		AggCostShareVO csVo = ErmForCShareUtil.convertFromBXVO(vo);
		if(csVo == null){
			return null;
		}
		
		// 按部门、单位负责人进行过滤
		if(!vo.getParentVO().isAdjustBxd()){
			// 费用调整单，不进行负责人过滤
			CShareDetailVO[] dtailvos = (CShareDetailVO[]) csVo.getChildrenVO();
			
			List<String> depts = new ArrayList<String>();
			List<String> corps = new ArrayList<String>();
			
			for (int j = 0; j < dtailvos.length; j++) {
				if (dtailvos[j].getAssume_dept() != null) {
					depts.add(dtailvos[j].getAssume_dept());
				}
				corps.add(dtailvos[j].getAssume_org());
			}
			
			List<String> listCorp = getCorpsByPrincipal(corps);// 当前登录用户负责公司
			List<String> listDept = getDeptsByPrincipal(depts);// 当前登录用户负责部门
			
			boolean isNotSameAssumeOrg = new ErmFlowCheckInfo().isSameAssumeOrg(vo).booleanValue();
			boolean isNotSameAssumeDept = new ErmFlowCheckInfo().isSameAssumeDept(vo).booleanValue();
			
			List<CShareDetailVO> resultList = new ArrayList<CShareDetailVO>();
			for (int j = 0; j < dtailvos.length; j++) {
				if (dtailvos[j].getStatus() == VOStatus.DELETED) {
					continue;
				}
				
				if (isNotSameAssumeOrg) {// 跨单位
					if (listCorp.contains(dtailvos[j].getAssume_org())) {
						// 联查负责单位的预算执行情况
						resultList.add(dtailvos[j]);
					}
				} else if (isNotSameAssumeDept) {// 跨部门
					if (listDept.contains(dtailvos[j].getAssume_dept())) {
						// 联查负责部门的预算情况
						resultList.add(dtailvos[j]);
					}
				} else {
					resultList.add(dtailvos[j]);
				}
			}
			
			csVo.setChildrenVO(resultList.toArray(new CShareDetailVO[] {}));
		}

		IErmCostShareYsControlService service = NCLocator.getInstance().lookup(IErmCostShareYsControlService.class);
		List<YsControlVO> costYsVoList = service.getCostShareYsVOList(new AggCostShareVO[] { csVo }, false, actionCode);

		if (costYsVoList != null && costYsVoList.size() > 0) {
			// 这里处理报销单冲借款部分，分摊没有处理冲借款部分
			IBXYsControlService bxYsservice = NCLocator.getInstance().lookup(IBXYsControlService.class);
			List<YsControlVO> ysControlVoList = bxYsservice.getYsControlVos(new JKBXVO[] { vo }, false, actionCode);
			if (ysControlVoList != null && ysControlVoList.size() > 0) {
				costYsVoList.addAll(ysControlVoList);
			}

			IBudgetControl bugetControl = NCLocator.getInstance().lookup(IBudgetControl.class);
			return bugetControl.getCheckInfo(costYsVoList.toArray(new YsControlVO[] {}));
		}

		return null;
	}

	/*
	 * 超项目预算
	 */
	public UFBoolean checkProjBudget(JKBXVO vo) throws BusinessException {
		if (vo == null || vo.getParentVO() == null) {
			return UFBoolean.FALSE;
		} else {

			boolean isprojControlUsed = BXUtil.isProductTbbInstalled(BXConstans.PIM_FUNCODE);
			if (!isprojControlUsed) {
				return UFBoolean.FALSE;
			}
			try {
				BudgetReturnMSG control = doProjBudgetCheck(new JKBXVO[] { vo });
				if (control != null && !StringUtil.isEmpty(control.getErrorMSG())) {// 柔性控制
					return UFBoolean.TRUE;
				}
			} catch (BusinessException e) {// 刚性控制
				return UFBoolean.FALSE;
			}

			return UFBoolean.FALSE;
		}
	}

	/**
	 * 判断是否是同公司分摊，如果是同公司分摊，则返回true
	 * 
	 * @param vo
	 * @return
	 */
	private UFBoolean isShareAssumeDept(JKBXVO vo) {
		CShareDetailVO[] detailvos = vo.getcShareDetailVo();
		String fydwbm = vo.getParentVO().getFydwbm();

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

	/**
	 * 检测
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	public static BudgetReturnMSG doProjBudgetCheck(JKBXVO[] vos) throws BusinessException {
		BudgetReturnMSG resultMsg = NCLocator.getInstance().lookup(IbudgetExe4ExpenseBill.class)
				.checkProjectBudget(vos);
		if (resultMsg != null) {
			UFBoolean isOver = isOverBudget(resultMsg.getDetailList());
			vos[0].getParentVO().setFlexible_flag(isOver);
		}
		return resultMsg;
	}

	/**
	 * 是否超出项目预算
	 * 
	 * @param checkResult
	 * @return
	 * @Author:ligangm
	 * @Date:2012-3-28
	 */
	public static UFBoolean isOverBudget(List<BudgetReturnVO> checkResult) {
		if (checkResult != null && checkResult.size() > 0) {
			for (Iterator<BudgetReturnVO> iterator = checkResult.iterator(); iterator.hasNext();) {
				BudgetReturnVO ctlInfoVO = iterator.next();
				if (BudgetEnum.OVERBUDGET.equals(ctlInfoVO.getWbsBudget())
						&& BudgetCtrlTypeConst.flexible_control == ctlInfoVO.getControlStyle()) {
					return UFBoolean.TRUE;
				}
			}
		}
		return UFBoolean.FALSE;
	}

	/**
	 * 补充申请记录
	 * 
	 * @param bxVo
	 * @throws BusinessException
	 */
	private static void fillUpMapf(JKBXVO bxVo) throws BusinessException {
		// 申请单处理
		MtapppfVO[] pfVos = MtappfUtil.getMaPfVosByJKBXVo(new JKBXVO[] { bxVo });
		bxVo.setMaPfVos(pfVos);

		if (bxVo instanceof BXVO) {
			MtapppfVO[] contrastPfs = MtappfUtil.getContrastMaPfVos(new JKBXVO[] { bxVo });
			bxVo.setContrastMaPfVos(contrastPfs);
		}
	}
}
