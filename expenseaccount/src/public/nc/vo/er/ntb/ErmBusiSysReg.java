package nc.vo.er.ntb;

import java.util.ArrayList;
import java.util.HashMap;

import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.expamortize.ExpAmoritizeConst;
import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.core.util.ObjectCreator;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.tb.control.IBusiSysExecDataProvider;
import nc.itf.tb.control.IBusiSysReg;
import nc.itf.tb.control.IDateType;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.tb.control.ControlBillType;
import nc.vo.tb.control.ControlObjectType;

/**
 * <p>
 * 报销管理部分实现预算接口
 *  * 功能描述:需要通过预算系统进行预算控制,通过预算系统进行控制的报销业务系统必须
 			提供该接口的VO实现类,并且需要在预算的业务系统注册表(ntb_id_sysreg)中以如下的方式
 			进行注册:
 			业务系统名称	业务系统标识	注册接口实现类 		                             可控标志
 			----------------------------------------------------------------------------------------------------------------------------------------
 			NC财务报销	 NC-ERM		nc.vo.erm.ntb.ErmBugetBusiSysReg	通过在控制节点上的功能按钮来进行检查,主要是匹配是否所有的主对象类型都已经对应到指标;
 			可以通过在各个业务产品的安装盘中提供相应的SQL语句:
 			insert into ntb_id_sysreg(pk_obj,sys_name,sys_id,regclass)
 			("ERM0010001ERM0010001","NC财务报销","ERM","nc.vo.erm.ntb.ErmBugetBusiSysReg");
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>
 *
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2011-3-2 下午02:47:17
 */
public class ErmBusiSysReg implements IBusiSysReg ,IDateType{

	private final HashMap<String,HashMap<String, String>> ctrlBillActionsMap = new HashMap<String, HashMap<String,String>>();
	private final HashMap<String,ArrayList<String>> ctrlBillOrgsMap = new HashMap<String,ArrayList<String>>();

	public ErmBusiSysReg() {
		super();
	}

	/**
     * 返回需要取数和控制的单据类型，ControlBillType请查看ControlBillType的源代码有详细的字段说明。
     */
	public ArrayList<ControlBillType> getBillType() {
		ArrayList<ControlBillType> result =new ArrayList<ControlBillType>();
		DjLXVO[] djlxs = null;
		try {
			String group = InvocationInfoProxy.getInstance().getGroupId();
			//add by lvhj 查询条件增加费用结转单、事项审批单动作，与报销相同
			djlxs = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, " djdl in ('jk','bx','ma','cs','at','ac') and pk_group='" + group +"'");
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			return null;
		}
		if (djlxs == null || djlxs.length < 1)
			return null;


		//将单据类型vo转换成ControlBillType，并赋值相应的属性
		for (int i = 0; i < djlxs.length; i++) {
			if (djlxs[i].getFcbz() != null && djlxs[i].getFcbz().booleanValue())
				continue;
			ControlBillType ctlBilltype = new ControlBillType();
			ctlBilltype.setPk_billType(djlxs[i].getDjlxbm());
			ctlBilltype.setBillType_code(djlxs[i].getDjlxbm());
			
			ctlBilltype.setBillType_name(PfDataCache.getBillTypeInfo(djlxs[i].getDjlxbm()).getBilltypenameOfCurrLang());
			ctlBilltype.setPk_orgs(getCtrlBillOrgs(djlxs[i].getDjdl()));
			ctlBilltype.setActionList(getCtrlBillActions(djlxs[i].getDjdl()));

			//默认报销管理部分预算执行，预占数统一处理
			ctlBilltype.setRunBillType(true);  		//是否执行数
			ctlBilltype.setReadyBillType(false);  	//是否预占数
			ctlBilltype.setUseControl(true);			//是否适用于控制
			ctlBilltype.setUseUfind(true);			//是否适用于取数
			result.add(ctlBilltype);
		}
		return result;
	}

	/**
	 * 获得需要控制的单据主组织
	 *
	 * @author lvhj
	 * @return
	 */
	private ArrayList<String> getCtrlBillOrgs(String djdl) {
		ArrayList<String> ctrlBillOrgs = ctrlBillOrgsMap.get(djdl);
		if (ctrlBillOrgs == null) {
			ctrlBillOrgs = new ArrayList<String>();
			ctrlBillOrgsMap.put(djdl, ctrlBillOrgs);

			if (BXConstans.BX_DJDL.equals(djdl) || BXConstans.JK_DJDL.equals(djdl)) {
				// 借款报销单主组织
				ctrlBillOrgs.add(BXConstans.ERM_NTB_PK_ORG);// 借款报销单位
				ctrlBillOrgs.add(BXConstans.ERM_NTB_EXP_ORG);// 费用承担单位
				ctrlBillOrgs.add(BXConstans.ERM_NTB_ERM_ORG);// 借款报销人单位
				ctrlBillOrgs.add(BXConstans.ERM_NTB_PAY_ORG);// 支付单位
				ctrlBillOrgs.add(BXConstans.ERM_NTB_PK_PCORG);// 利润中心
			} else if (ErmBillConst.MatterApp_DJDL.equals(djdl) || ErmBillConst.AccruedBill_DJDL.equals(djdl)) {
				// 费用申请单主组织
				ctrlBillOrgs.add(BXConstans.ERM_NTB_EXP_ORG);// 费用承担单位
				ctrlBillOrgs.add(BXConstans.ERM_NTB_PK_ORG);// 财务组织
				ctrlBillOrgs.add(BXConstans.ERM_NTB_PK_PCORG);// 利润中心
			} else if (IErmCostShareConst.COSTSHARE_DJDL.equals(djdl)
					|| ExpAmoritizeConst.Expamoritize_DJDL.equals(djdl)) {
				// 费用结转单、摊销信息主组织
				ctrlBillOrgs.add(BXConstans.ERM_NTB_EXP_ORG);// 费用承担单位
				ctrlBillOrgs.add(BXConstans.ERM_NTB_PK_PCORG);// 利润中心
				ctrlBillOrgs.add(BXConstans.ERM_NTB_PK_ORG);// 财务组织
			}
		}
		return ctrlBillOrgs;
	}
	/**
	 * 获得需要控制的单据动作
	 *
	 * @return
	 */
	private HashMap<String, String> getCtrlBillActions(String djdl) {
		HashMap<String, String> ctrlBillActions = ctrlBillActionsMap.get(djdl);
		if (ctrlBillActions == null) {
			ctrlBillActions = new HashMap<String, String>();
			ctrlBillActionsMap.put(djdl, ctrlBillActions);
			if(BXConstans.JK_DJDL.equals(djdl)){
				ctrlBillActions.put(BXConstans.ERM_NTB_SAVE_KEY, BXConstans.ERM_NTB_SAVE_VALUE);
				ctrlBillActions.put(BXConstans.ERM_NTB_APPROVE_KEY, BXConstans.ERM_NTB_APPROVE_VALUE);
			}else if(BXConstans.BX_DJDL.equals(djdl)){
				//add by lvhj 增加费用申请单动作
				ctrlBillActions.put(BXConstans.ERM_NTB_SAVE_KEY, BXConstans.ERM_NTB_SAVE_VALUE);
				ctrlBillActions.put(BXConstans.ERM_NTB_APPROVE_KEY, BXConstans.ERM_NTB_APPROVE_VALUE);
			}else if (ErmBillConst.MatterApp_DJDL.equals(djdl)){
				//add by lvhj 增加费用申请单动作
				ctrlBillActions.put(BXConstans.ERM_NTB_SAVE_KEY, BXConstans.ERM_NTB_SAVE_VALUE);
				ctrlBillActions.put(BXConstans.ERM_NTB_APPROVE_KEY, BXConstans.ERM_NTB_APPROVE_VALUE);
				ctrlBillActions.put(BXConstans.ERM_NTB_CLOSE_KEY, BXConstans.ERM_NTB_CLOSE_VALUE);
			}else if(IErmCostShareConst.COSTSHARE_DJDL.equals(djdl)){
				//add by lvhj 增加费用结转单
				ctrlBillActions.put(BXConstans.ERM_NTB_SAVE_KEY, BXConstans.ERM_NTB_SAVE_VALUE);
				ctrlBillActions.put(BXConstans.ERM_NTB_APPROVE_KEY, BXConstans.ERM_NTB_APPROVE_VALUE);
				ctrlBillActions.put(BXConstans.ERM_NTB_COSTSHAREAPPROVE_KEY, BXConstans.ERM_NTB_COSTSHAREAPPROVE_VALUE);
			}else if(ExpAmoritizeConst.Expamoritize_DJDL.equals(djdl)){
				//摊销预算动作
				ctrlBillActions.put(BXConstans.ERM_NTB_AMORTIZE_KEY, BXConstans.ERM_NTB_AMORTIZE_VALUE);
			}else if(ErmBillConst.AccruedBill_DJDL.equals(djdl)){
				//预提单
				ctrlBillActions.put(BXConstans.ERM_NTB_SAVE_KEY, BXConstans.ERM_NTB_SAVE_VALUE);
				ctrlBillActions.put(BXConstans.ERM_NTB_APPROVE_KEY, BXConstans.ERM_NTB_APPROVE_VALUE);
			}
		}
		return ctrlBillActions;
	}


	/**
     * 返回报销管理业务系统的名称
     */
	public String getBusiSysDesc() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0116")/*@res "NC费用管理"*/;
	}

	/**
     * 返回报销管理业务系统的ID
     */
	public String getBusiSysID() {
		return  BXConstans.ERM_PRODUCT_CODE_Lower;

	}

	/**
	 * 获取业务系统的可控业务类型
	 */
	public String[] getBusiType() {
		return null;
	}

	/**
	 * 获取业务系统的可控业务类型描述
	 */
	public String[] getBusiTypeDesc() {
		return null;
	}

	/**
	 * 获取业务系统的可控方向类型
	 */
	public String[] getControlableDirections() {

//		return new String[] { "收", "付" }; /*-=notranslate=-*/
		return new String[] {BXConstans.RECEIVABLE , BXConstans.PAYABLE};
	}

	/**
	 * 获取业务系统的可控方向类型描述
	 */
	public String[] getControlableDirectionsDesc() {
		return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000401")/*@res "收"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000402")/*@res "付"*/ };
	}

	/**
	 * 获取业务系统的可控对象类型，请查看ControlObjectType的源代码有详细的字段说明
	 */
	public ArrayList<ControlObjectType> getControlableObjects() {

		ArrayList<ControlObjectType> reArrayList=new ArrayList<ControlObjectType>();
		ControlObjectType e=new ControlObjectType(BXConstans.ERM_NTB_CTL_KEY,BXConstans.ERM_NTB_CTL_KEY,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000400"));//UPP2011-000400-->发生额

		reArrayList.add(e);
		return reArrayList;
	}

	/**
	 * 获取业务系统的可控对象类型描述
	 */
	public String[] getControlableObjectsDesc() {
		return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000400")/*@res "发生额"*/ };
	}
	/**
	 * 获取执行数服务的提供类，需要区分是否在服务器端
	 */
	public IBusiSysExecDataProvider getExecDataProvider() {
		return (IBusiSysExecDataProvider) ObjectCreator.newInstance(BXConstans.ERM_PRODUCT_CODE_Lower, "nc.bs.er.control.ErmNtbProvider");
	}

	/**
	 * 返回业务系统是否启用预算控制的参数值
	 */
	public boolean isBudgetControling() {
		return true;
	}
	/**
	 * 日期类型：保存，审核 需要增加签字日期
	 */
	public String[] getDataType() {
		return new String[] { BXConstans.BILLDATE, BXConstans.APPROVEDATE, BXConstans.EFFECTDATE};
	}
	/**
	 * 日期类型对应的描述
	 */
	public String[] getDataTypeDesc() {
		return new String[]{nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000248")/*@res "单据日期"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPTcommon-000037")/*@res "审核日期"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000114")/*@res "生效日期"*/};
	}

	/**
	 * 获取业务系统通过单据来过滤使用的属性
	 */
	public ArrayList<ControlObjectType> getControlableObjects(String billtype) {

		return null;
	}


	public String getMainPkOrg() {

		return null;
	}

	/**
	 * 是否支持单据可以多选
	 */
	public boolean isSupportMutiSelect() {

		return true;
	}

	/**
	 * 是否单据使用会计期间
	 */
	public boolean isUseAccountDate(String billtype) {

		return false;
	}
	/**
	 * 根据系统和单据类型返回日期类型的编码,BILLTYPE可能为空,传入的是单据的编码
	 * 注意：有日期类型业务需求的才需要实现这个接口。
	 */
	public String[] getDateType(String billtype) {
		return new String[] {BXConstans.BILLDATE, BXConstans.APPROVEDATE, BXConstans.EFFECTDATE};
	}

}