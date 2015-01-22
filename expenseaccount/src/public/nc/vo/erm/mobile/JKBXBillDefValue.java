package nc.vo.erm.mobile;

import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.ui.er.util.BXUiUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.pub.BillEnumCollection;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/*
 * 
 */
public class JKBXBillDefValue extends AbstractBillDefValue {
	public JKBXBillDefValue(String pk_group,String pk_org,String pk_user,String tradetype,String djdl) {
		super();
		setPk_group(pk_group);
		setPk_user(pk_user);
		setPk_org(pk_org);
		setTradetype(tradetype);
		setDjdl(djdl);
	}
	
	@Override
	public AggregatedValueObject getNewVO() {
		JKBXVO aggvo = null;
		JKBXHeaderVO headvo = null;
		if(BXConstans.JK_DJDL.equals(djdl)){
			aggvo = new JKVO();
			headvo = new JKHeaderVO();
		}else if(BXConstans.BX_DJDL.equals(djdl)){
			aggvo = new BXVO();
			headvo = new BXHeaderVO();
		}
		
//		headvo.setBillclass(IBillFieldGet.YF);
		String billtype=IBillFieldGet.F1;
		headvo.setPk_billtype(billtype);
//		headvo.setSrc_syscode(BillEnumCollection.FromSystem.AP.VALUE);
//		headvo.setSyscode(BillEnumCollection.FromSystem.AP.VALUE);
//		headvo.setObjtype(BillEnumCollection.ObjType.SUPPLIER.VALUE);
		
		headvo.setPk_group(getPk_group());
		headvo.setJkbxr(getPk_user());
		headvo.setCreator(getPk_user());
		headvo.setPk_org(this.getPk_org());
		UFDate time = getBusisDate();
		headvo.setDjrq(time);
		headvo.setCreationtime(getBusisDateTime());
		headvo.setDjzt(BillEnumCollection.BillSatus.Save.VALUE);
//		headvo.setPk_currtype("1002Z0100000000001K1");
		
		
		BXBusItemVO childrenVO = new BXBusItemVO();
//		childrenVO.setBillclass(IBillFieldGet.YF);
//		childrenVO.setPk_billtype(billtype);
//		childrenVO.setDirection(BillEnumCollection.Direction.CREDIT.VALUE);
//		childrenVO.setObjtype(BillEnumCollection.ObjType.SUPPLIER.VALUE);
//		childrenVO.setPk_currtype("1002Z0100000000001K1");
		
		aggvo.setParentVO(headvo);
		aggvo.setChildrenVO(new BXBusItemVO[]{childrenVO});
		return aggvo;
		
	}
		
	/**
	 * @author wangle 根据组织设置单据默认值
	 * @param strDjdl
	 * @param strDjlxbm
	 * @param org
	 * @param isAdd
	 * @param permOrgs
	 * @throws BusinessException
	 */
	public void setDefaultWithOrg(JKBXVO billvo,String strDjdl, String strDjlxbm,
			String pk_org, boolean isEdit) throws BusinessException {
		JKBXHeaderVO headvo = (JKBXHeaderVO) billvo.getParentVO();
		// 设置费用承担单位，借款人单位和利润中心
		String[] keys = new String[] { JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM,
				JKBXHeaderVO.PK_PCORG, JKBXHeaderVO.PK_FIORG,
				JKBXHeaderVO.PK_PAYORG };
		for (String key : keys) {
			headvo.setAttributeValue(key,pk_org);
		}
		
		//
		// 常用单据和拉单过来时，都不加载常用单据
//		if (!(((ErmBillBillManageModel)getModel()).iscydj())&& editor.getResVO()==null) {
//			// 加载组织的常用单据
//			setInitBill(pk_org);
//			String initBill_org = (String)editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
//			//在加载完常用单据后，要判断常用的组织是否期初关闭
//			if(initBill_org != null){
//				checkQCClose(initBill_org);
//				getModel().getContext().setPk_org(initBill_org);
//				pk_org=(String) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
//			}
//		}
		
//		//在加载完常用单据后，要判断常用的组织是否期初关闭
//		String initBill_org = (String)editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
//		if(initBill_org != null){
//			checkQCClose(initBill_org);
//			getModel().getContext().setPk_org(initBill_org);
//			pk_org=(String) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
//		}
		
		// 期初单据设置单据日期
//		if (editor.isInit() && !StringUtil.isEmpty(pk_org)) {
//			UFDate startDate = BXUiUtil.getStartDate(pk_org);
//			if (startDate == null) {
//				// 该组织模块启用日期为空
//				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
//						.getNCLangRes().getStrByID("expensepub_0",
//								"02011002-0001"));
//			} else {
//				getBillCardPanel().setHeadItem(JKBXHeaderVO.DJRQ,
//						startDate.getDateBefore(1));
//			}
//		}

		// 设置组织币种汇率信息
//		setCurrencyInfo(pk_org);
		
		// 根据借款报销单位自动带出收款银行帐号
//		headFieldHandle.setDefaultSkyhzhByReceiver();

		// 设置最迟还款日期
		try {
//			setZhrq(pk_org);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		
		// v6.1新增单据 根据费用承担部门带出默认成本中心 TODO : 在后台处理好了，前台只需设置一下表体即可
//		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_RESACOSTCENTER).getValueObject() == null) {// 优先带出常用单据中组织
//
//			Object pk_body_center = getBillCardPanel().getBodyValueAt(0, BXBusItemVO.PK_RESACOSTCENTER);
//			if (pk_body_center == null) {
//				String pk_fydept = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDEPTID).getValueObject();
//				String pk_pcorg = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_PCORG).getValueObject();
//				setCostCenter(pk_fydept, pk_pcorg);
//			}
//		}
		
//		Object valueObject = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_RESACOSTCENTER).getValueObject();
//		if(valueObject != null){
//			String pk_costcenter =valueObject.toString();
//			changeBusItemValue(BXBusItemVO.PK_RESACOSTCENTER, pk_costcenter);
//		}
		// 插入业务流程 --> 在后台处理
		//insertBusitype(strDjdl, pk_org);
	}

}


