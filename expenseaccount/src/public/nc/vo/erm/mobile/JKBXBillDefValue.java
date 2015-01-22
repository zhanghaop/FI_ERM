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
	 * @author wangle ������֯���õ���Ĭ��ֵ
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
		// ���÷��óе���λ������˵�λ����������
		String[] keys = new String[] { JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM,
				JKBXHeaderVO.PK_PCORG, JKBXHeaderVO.PK_FIORG,
				JKBXHeaderVO.PK_PAYORG };
		for (String key : keys) {
			headvo.setAttributeValue(key,pk_org);
		}
		
		//
		// ���õ��ݺ���������ʱ���������س��õ���
//		if (!(((ErmBillBillManageModel)getModel()).iscydj())&& editor.getResVO()==null) {
//			// ������֯�ĳ��õ���
//			setInitBill(pk_org);
//			String initBill_org = (String)editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
//			//�ڼ����곣�õ��ݺ�Ҫ�жϳ��õ���֯�Ƿ��ڳ��ر�
//			if(initBill_org != null){
//				checkQCClose(initBill_org);
//				getModel().getContext().setPk_org(initBill_org);
//				pk_org=(String) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
//			}
//		}
		
//		//�ڼ����곣�õ��ݺ�Ҫ�жϳ��õ���֯�Ƿ��ڳ��ر�
//		String initBill_org = (String)editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
//		if(initBill_org != null){
//			checkQCClose(initBill_org);
//			getModel().getContext().setPk_org(initBill_org);
//			pk_org=(String) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
//		}
		
		// �ڳ��������õ�������
//		if (editor.isInit() && !StringUtil.isEmpty(pk_org)) {
//			UFDate startDate = BXUiUtil.getStartDate(pk_org);
//			if (startDate == null) {
//				// ����֯ģ����������Ϊ��
//				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
//						.getNCLangRes().getStrByID("expensepub_0",
//								"02011002-0001"));
//			} else {
//				getBillCardPanel().setHeadItem(JKBXHeaderVO.DJRQ,
//						startDate.getDateBefore(1));
//			}
//		}

		// ������֯���ֻ�����Ϣ
//		setCurrencyInfo(pk_org);
		
		// ���ݽ�����λ�Զ������տ������ʺ�
//		headFieldHandle.setDefaultSkyhzhByReceiver();

		// ������ٻ�������
		try {
//			setZhrq(pk_org);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		
		// v6.1�������� ���ݷ��óе����Ŵ���Ĭ�ϳɱ����� TODO : �ں�̨������ˣ�ǰֻ̨������һ�±��弴��
//		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_RESACOSTCENTER).getValueObject() == null) {// ���ȴ������õ�������֯
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
		// ����ҵ������ --> �ں�̨����
		//insertBusitype(strDjdl, pk_org);
	}

}


