package nc.vo.erm.mobile;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.pubitf.setting.defaultdata.OrgSettingAccessor;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;

import org.apache.commons.lang.ArrayUtils;


public  abstract class AbstractBillDefValue{

	private boolean isInit =false;

	public AggregatedValueObject getDefaultVO() {
		AggregatedValueObject newVO = getNewVO();
		if (newVO != null && newVO instanceof JKBXVO) {
			setCommonValue((JKBXVO) newVO);
		}
		return newVO;
	}
	String djdl=null; //单据大类 需要补充
	public String getDjdl() {
		return djdl;
	}

	public void setDjdl(String djdl) {
		this.djdl = djdl;
	}
	String pk_group=null; //需要补充
	String pk_user=null; //需要补充
	String pk_org = null;
	String tradetype = null;//交易类型编码,需要补充
	
	public String getPk_group() {
		return pk_group;
	}

	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	public String getPk_user() {
		return pk_user;
	}

	public void setPk_user(String pk_user) {
		this.pk_user = pk_user;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getTradetype() {
		return tradetype;
	}

	public void setTradetype(String tradetype) {
		this.tradetype = tradetype;
	}

	public static UFDate getBusisDate(){
		long busitime = InvocationInfoProxy.getInstance().getBizDateTime();
	    return new UFDate(busitime);
	}
	public static UFDateTime getBusisDateTime(){
		long busitime = InvocationInfoProxy.getInstance().getBizDateTime();
	    return new UFDateTime(busitime);
	}
	public static void fillRateInfo(AggregatedValueObject bill) {
//		String pk_currtype = (String) bill.getParentVO().getAttributeValue(IBillFieldGet.PK_CURRTYPE);
//		String pk_billtype = (String) bill.getParentVO().getAttributeValue(IBillFieldGet.PK_BILLTYPE);
//		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
//		String pk_org = (String) bill.getParentVO().getAttributeValue(IBillFieldGet.PK_ORG);
//
//		if (null != pk_org && null != pk_currtype && null != pk_billtype) {
//			UFDate date = getBusisDate();
//			UFDouble[] rates = ArapBillCalUtil.getRate(pk_currtype, pk_org, pk_group, date, pk_billtype);
//
//			UFDouble rate = rates[0];
//
//			UFDouble grouprate = rates[1];
//
//			UFDouble globalrate = rates[2];
//
//			bill.getParentVO().setAttributeValue(IBillFieldGet.RATE, rate);
//			bill.getParentVO().setAttributeValue(IBillFieldGet.GROUPRATE, grouprate);
//			bill.getParentVO().setAttributeValue(IBillFieldGet.GLOBALRATE, globalrate);
//
//			for (CircularlyAccessibleValueObject item : bill.getChildrenVO()) {
//				item.setAttributeValue(IBillFieldGet.RATE, rate);
//				item.setAttributeValue(IBillFieldGet.GROUPRATE, grouprate);
//				item.setAttributeValue(IBillFieldGet.GLOBALRATE, globalrate);
//			}
//		}
	}
	private void setCommonValue(JKBXVO billvo) {
		JKBXHeaderVO headvo = (JKBXHeaderVO) billvo.getParentVO();
		BXBusItemVO[] itemvos = (BXBusItemVO[]) billvo.getChildrenVO();
		if (billvo == null || ArrayUtils.isEmpty(itemvos) || headvo == null) {
			return;
		}
		BXBusItemVO  childrenVO = itemvos[0];
		try {
			pk_org = OrgSettingAccessor.getDefaultOrgUnit();
		} catch (Exception e) {
//			ExceptionHandler.consume(e);
		}
//		if (StringUtils.isEmpty(pk_org)) {
//			pk_org = (String) WorkbenchEnvironment.getInstance()
//					.getClientCache(IBillFieldGet.ARAP_DEFAULT_ORG + headvo.getPk_billtype());
//		}
//		String pk_currtype = null;
//		try {
//			pk_currtype = Currency.getOrgLocalCurrPK(pk_org);
//		} catch (BusinessException e1) {
//			ExceptionHandler.consume(e1);
//		}

//		headvo.setPk_org(pk_org);
//		headvo.setPk_fiorg(pk_org);
//		headvo.setSett_org(pk_org);
//		headvo.setPk_group(pk_group);
//		headvo.setBillmaker(pk_user);
//		headvo.setCreator(pk_user);
//		childrenVO.setPk_org(pk_org);
//		childrenVO.setPk_fiorg(pk_org);
//		childrenVO.setSett_org(pk_org);
//		childrenVO.setPk_group(pk_group);
//
//		headvo.setIsinit(UFBoolean.FALSE);
//		headvo.setEffectstatus(InureSign.NOINURE.VALUE);
		UFDate billdate = null;

		if (isInit) {
			billdate = null;
		} else {
			billdate = getBusisDate();
		}

//		headvo.setBilldate(billdate);
//		headvo.setBusidate(billdate);
//		headvo.setCreationtime(WorkbenchEnvironment.getServerTime());
//		headvo.setPk_currtype(pk_currtype);
//		headvo.setBillstatus(BillSatus.Save.VALUE);
//
//		
//		headvo.setPk_tradetype(tradetype);
//		String pkBilltypeid = PfDataCache.getBillType(tradetype).getPk_billtypeid();
//		headvo.setPk_tradetypeid(pkBilltypeid);
//		childrenVO.setPk_billtype(headvo.getPk_billtype());
//		childrenVO.setPk_tradetype(tradetype);
//		childrenVO.setPk_tradetypeid(pkBilltypeid);
//		childrenVO.setBillclass(headvo.getBillclass());
//
//		childrenVO.setBusidate(billdate);
//		childrenVO.setBilldate(billdate);
//		childrenVO.setPk_currtype(pk_currtype);
		
//		if (isInit) {
//			headvo.setBillstatus(BillSatus.Audit.VALUE.intValue());
//			headvo.setIsinit(UFBoolean.TRUE);
//			headvo.setApprover(pk_user);
//			headvo.setApprovestatus(ApproveStatus.PASSING.VALUE.intValue());
//		}
//
//		try {
//			IPFConfig ipf=NCLocator.getInstance().lookup(IPFConfig.class);
//			if(!StringUtils.isEmpty(headvo.getPk_billtype())  && !StringUtils.isEmpty(headvo.getPk_tradetype())){
//				String pk_busitype=	ipf.retBusitypeCanStart(headvo.getPk_billtype(), headvo.getPk_tradetype(), headvo.getPk_org(), headvo.getCreator());
//				if(pk_busitype==null){
//					throw new BusinessException("busitype is null");
//				}
//				headvo.setPk_busitype(pk_busitype);
//			}
//		} catch (Exception e) {
//			String msg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0127")/*@res "交易类型"*/+headvo.getPk_tradetype()+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0239")/*@res "没有找到相应的流程,请在[业务流定义]配置"*/+headvo.getPk_tradetype()+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0240")/*@res "自制流程"*/ ;
//			throw new BusinessRuntimeException(msg);
//		}

		childrenVO.setRowno(0);
		childrenVO.setPrimaryKey("ID_INDEX0");

//		headvo.setMoney(UFDouble.ZERO_DBL);
//		headvo.setLocal_money(UFDouble.ZERO_DBL);
//		headvo.setRate(UFDouble.ONE_DBL);
//		headvo.setGloballocal(UFDouble.ZERO_DBL);
//		headvo.setGrouplocal(UFDouble.ZERO_DBL);
//
//		childrenVO.setRate(UFDouble.ONE_DBL);
//		childrenVO.setMoney_de(UFDouble.ZERO_DBL);
//		childrenVO.setLocal_money_de(UFDouble.ZERO_DBL);
//		childrenVO.setLocal_money_bal(UFDouble.ZERO_DBL);
//		childrenVO.setMoney_bal(UFDouble.ZERO_DBL);
//		childrenVO.setOccupationmny(UFDouble.ZERO_DBL);
//		childrenVO.setGlobalcrebit(UFDouble.ZERO_DBL);
//		childrenVO.setGlobaldebit(UFDouble.ZERO_DBL);
//		childrenVO.setGroupdebit(UFDouble.ZERO_DBL);
//		childrenVO.setGroupdebit(UFDouble.ZERO_DBL);

		fillRateInfo(billvo);

//		if (headvo.getSrc_syscode() == null
//				|| headvo.getSrc_syscode().toString().trim().length() == 0
//				|| headvo.getSrc_syscode() == 0) {
//			if (ArapBillPubUtil.isARSysBilltype(headvo.getPk_billtype())) {
//				headvo.setSrc_syscode(FromSystem.AR.VALUE);
//			} else {
//				headvo.setSrc_syscode(FromSystem.AP.VALUE);
//			}
//		}
//		if (headvo.getSyscode() == null
//				|| headvo.getSyscode().toString().trim().length() == 0
//				|| headvo.getSrc_syscode() == 0) {
//			if (ArapBillPubUtil.isARSysBilltype(headvo.getPk_billtype())) {
//				headvo.setSyscode(FromSystem.AR.VALUE);
//			} else {
//				headvo.setSyscode(FromSystem.AP.VALUE);
//			}
//		}

//		try{
//			DjLXVO billType = FiPubDataCache.getBillType(tradetype, pk_group);
//			if (billType != null && (billType.getIschangedeptpsn() == null ? false : billType.getIschangedeptpsn().booleanValue())) {
//				String pk_psndoc = BillEventHandlerUtil.getPsndocByUserid(WorkbenchEnvironment.getInstance().getLoginUser().getCuserid(),pk_org);
//				String pk_deptid = BillEventHandlerUtil.getDeptDocByPsnid(pk_psndoc);
//				//获取多版本部门id
//				String pk_deptid_v = BillOrgVUtils.getDept_vid(pk_deptid, billdate);
////				headvo.setPk_deptid_v(pk_deptid_v);
////				headvo.setPk_psndoc(pk_psndoc);
////				headvo.setPk_deptid(pk_deptid);
////				childrenVO.setPk_deptid_v(pk_deptid_v);
////				childrenVO.setPk_psndoc(pk_psndoc);
////				childrenVO.setPk_deptid(pk_deptid);
//			}
//		} catch (Exception e) {
//			ExceptionHandler.consume(e);
//		}
		
//		childrenVO.setBuysellflag(headvo.getSyscode().equals(FromSystem.AR.VALUE)?BillEnumCollection.BuySellType.IN_SELL.VALUE:BillEnumCollection.BuySellType.IN_BUY.VALUE);
	}

	//设置表体模板默认值
//	public static void dealBodyTemplateDefaultValue(BaseItemVO childrenVO, BillCardPanel billCardPanel) {
//		
//	}

	protected abstract AggregatedValueObject getNewVO();

	public boolean isInit() {
		return isInit;
	}

	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}

}
