package nc.vo.er.ntb;

import java.util.ArrayList;
import java.util.HashMap;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.core.util.ObjectCreator;
import nc.bs.pf.pub.PfDataCache;
import nc.impl.er.proxy.ProxyDjlx;
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
 * TODO 
 * ����������ʵ��Ԥ��ӿ�
 *  * ��������:��Ҫͨ��Ԥ��ϵͳ����Ԥ�����,ͨ��Ԥ��ϵͳ���п��Ƶı���ҵ��ϵͳ����
 			�ṩ�ýӿڵ�VOʵ����,������Ҫ��Ԥ���ҵ��ϵͳע���(ntb_id_sysreg)�������µķ�ʽ
 			����ע��:
 			ҵ��ϵͳ����	ҵ��ϵͳ��ʶ	ע��ӿ�ʵ���� 		                             �ɿر�־
 			----------------------------------------------------------------------------------------------------------------------------------------
 			NC������	 NC-ERM		nc.vo.erm.ntb.ErmBugetBusiSysReg	ͨ���ڿ��ƽڵ��ϵĹ��ܰ�ť�����м��,��Ҫ��ƥ���Ƿ����е����������Ͷ��Ѿ���Ӧ��ָ��;
 			����ͨ���ڸ���ҵ���Ʒ�İ�װ�����ṩ��Ӧ��SQL���:
 			insert into ntb_id_sysreg(pk_obj,sys_name,sys_id,regclass)
 			("ERM0010001ERM0010001","NC������","ERM","nc.vo.erm.ntb.ErmBugetBusiSysReg");
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * <br><br>
 *
 * @see 
 * @author liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2011-3-2 ����02:47:17
 */
public class ErmBusiSysReg implements IBusiSysReg ,IDateType{

	private final HashMap<String, String> ctrlBillActions = new HashMap<String, String>();
	public ErmBusiSysReg() {
		super();
	}
	
	/**
     * ������Ҫȡ���Ϳ��Ƶĵ������ͣ�ControlBillType��鿴ControlBillType��Դ��������ϸ���ֶ�˵����
     */
	public ArrayList<ControlBillType> getBillType() {
		ArrayList<ControlBillType> result =new ArrayList<ControlBillType>();
		DjLXVO[] djlxs = null;
		try {
			
			String group = InvocationInfoProxy.getInstance().getGroupId();
			djlxs = ProxyDjlx.getIArapBillTypePublic().queryByWhereStr(" dr=0  and djdl in ('jk','bx') and pk_group='" + group +"'");
			
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			return null;
		}
		if (djlxs == null || djlxs.length < 1)
			return null;
		
		
		//���ݵ�����֯����,����Ϊ���,��Ӧҵ��ϵͳע�ᵽntb_id_bdcontrast���е�PK_OBJ�ֶ�*/
		ArrayList<String> pk_orgs = new ArrayList<String> ();
		pk_orgs.add(BXConstans.ERM_NTB_PK_ORG);
		pk_orgs.add(BXConstans.ERM_NTB_EXP_ORG);
		pk_orgs.add(BXConstans.ERM_NTB_ERM_ORG);
		
		//����������voת����ControlBillType������ֵ��Ӧ������
		for (int i = 0; i < djlxs.length; i++) {
			if (djlxs[i].getFcbz() != null && djlxs[i].getFcbz().booleanValue())
				continue;
			ControlBillType ctlBilltype = new ControlBillType();
			ctlBilltype.setPk_billType(djlxs[i].getDjlxbm());
			ctlBilltype.setBillType_code(djlxs[i].getDjlxbm());
			//ctlBilltype.setBillType_name(djlxs[i].getDjlxmc());
			ctlBilltype.setBillType_name(PfDataCache.getBillTypeInfo(djlxs[i].getDjlxbm()).getBilltypenameOfCurrLang());
			ctlBilltype.setPk_orgs(pk_orgs);
			ctlBilltype.setActionList(getCtrlBillActions(djlxs[i].getDjdl()));
			
			//Ĭ�ϱ���������Ԥ��ִ�У�Ԥռ��ͳһ����
			ctlBilltype.setRunBillType(true);  		//�Ƿ�ִ����
			ctlBilltype.setReadyBillType(false);  	//�Ƿ�Ԥռ��
			ctlBilltype.setUseControl(true);			//�Ƿ������ڿ���
			ctlBilltype.setUseUfind(true);			//�Ƿ�������ȡ��								
			result.add(ctlBilltype);
		}
		return result;
	}
	
	/**
	 * �����Ҫ���Ƶĵ��ݶ���
	 * 
	 * @return
	 */
	private HashMap<String, String> getCtrlBillActions(String djdl) {
		if (ctrlBillActions.size() == 0) {
			if(BXConstans.JK_DJDL.equals(djdl)){
				ctrlBillActions.put(BXConstans.ERM_NTB_SAVE_KEY, BXConstans.ERM_NTB_SAVE_VALUE);
				ctrlBillActions.put(BXConstans.ERM_NTB_DELETE_KEY, BXConstans.ERM_NTB_DELETE_VALUE);
				ctrlBillActions.put(BXConstans.ERM_NTB_APPROVE_KEY, BXConstans.ERM_NTB_APPROVE_VALUE);
				ctrlBillActions.put(BXConstans.ERM_NTB_UNAPPROVE_KEY, BXConstans.ERM_NTB_UNAPPROVE_VALUE);
				ctrlBillActions.put(BXConstans.ERM_NTB_CONTRASTAPPROVE_KEY, BXConstans.ERM_NTB_CONTRASTAPPROVE_VALUE);
				ctrlBillActions.put(BXConstans.ERM_NTB_CONTRASTUNAPPROVE_KEY, BXConstans.ERM_NTB_CONTRASTUNAPPROVE_VALUE);
			}else if(BXConstans.BX_DJDL.equals(djdl)){
				ctrlBillActions.put(BXConstans.ERM_NTB_SAVE_KEY, BXConstans.ERM_NTB_SAVE_VALUE);
				ctrlBillActions.put(BXConstans.ERM_NTB_DELETE_KEY, BXConstans.ERM_NTB_DELETE_VALUE);
				ctrlBillActions.put(BXConstans.ERM_NTB_APPROVE_KEY, BXConstans.ERM_NTB_APPROVE_VALUE);
				ctrlBillActions.put(BXConstans.ERM_NTB_UNAPPROVE_KEY, BXConstans.ERM_NTB_UNAPPROVE_VALUE);
			}
		}
		return ctrlBillActions;
	}

	
	/**
     * ���ر�������ҵ��ϵͳ������
     */
	public String getBusiSysDesc() {
		
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000399")/*@res "NC��������"*/;
	}

	/**
     * ���ر�������ҵ��ϵͳ��ID
     */
	public String getBusiSysID() {
		
		return  BXConstans.ERM_PRODUCT_CODE_Lower;
		
	}

	/**
	 * ��ȡҵ��ϵͳ�Ŀɿ�ҵ������
	 */
	public String[] getBusiType() {

		return null;
	}

	/**
	 * ��ȡҵ��ϵͳ�Ŀɿ�ҵ����������
	 */
	public String[] getBusiTypeDesc() {

		return null;
	}

	/**
	 * ��ȡҵ��ϵͳ�Ŀɿط������� 
	 */
	public String[] getControlableDirections() {

//		return new String[] { "��", "��" }; /*-=notranslate=-*/		
		return new String[] {BXConstans.RECEIVABLE , BXConstans.PAYABLE};
	}

	/**
	 * ��ȡҵ��ϵͳ�Ŀɿط����������� 
	 */
	public String[] getControlableDirectionsDesc() {

		return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000401")/*@res "��"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000402")/*@res "��"*/ };
	}
	
	/**
	 * ��ȡҵ��ϵͳ�Ŀɿض������ͣ���鿴ControlObjectType��Դ��������ϸ���ֶ�˵�� 
	 */
	public ArrayList<ControlObjectType> getControlableObjects() {

		ArrayList<ControlObjectType> reArrayList=new ArrayList<ControlObjectType>();
		ControlObjectType e=new ControlObjectType(BXConstans.ERM_NTB_CTL_KEY,BXConstans.ERM_NTB_CTL_KEY,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000400"));//UPP2011-000400-->������

		reArrayList.add(e);
		return reArrayList;
	}

	/**
	 * ��ȡҵ��ϵͳ�Ŀɿض�����������
	 */
	public String[] getControlableObjectsDesc() {
		return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000400")/*@res "������"*/ };
	}
	/**
	 * ��ȡִ����������ṩ�࣬��Ҫ�����Ƿ��ڷ�������
	 */
	public IBusiSysExecDataProvider getExecDataProvider() {
		return (IBusiSysExecDataProvider) ObjectCreator.newInstance(BXConstans.ERM_PRODUCT_CODE_Lower, "nc.bs.er.control.ErmNtbProvider");
	}

	/**
	 * ����ҵ��ϵͳ�Ƿ�����Ԥ����ƵĲ���ֵ
	 */
	public boolean isBudgetControling() {
		return true;
	}
	/**
	 * �������ͣ����棬��� ��Ҫ����ǩ������
	 */
	public String[] getDataType() {
		return new String[] { BXConstans.BILLDATE, BXConstans.APPROVEDATE, BXConstans.EFFECTDATE};
	}
	/**
	 * �������Ͷ�Ӧ������
	 */
	public String[] getDataTypeDesc() {
		return new String[]{nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000248")/*@res "��������"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPTcommon-000037")/*@res "�������"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000114")/*@res "��Ч����"*/};
	}
	
	/**
	 * ��ȡҵ��ϵͳͨ������������ʹ�õ�����
	 */
	public ArrayList<ControlObjectType> getControlableObjects(String billtype) {

		return null;
	}


	public String getMainPkOrg() {

		return null;
	}

	/**
	 * �Ƿ�֧�ֵ��ݿ��Զ�ѡ
	 */
	public boolean isSupportMutiSelect() {

		return true;
	}

	/**
	 * �Ƿ񵥾�ʹ�û���ڼ�
	 */
	public boolean isUseAccountDate(String billtype) {

		return false;
	}
	/**
	 * ����ϵͳ�͵������ͷ����������͵ı���,BILLTYPE����Ϊ��,������ǵ��ݵı���
	 * ע�⣺����������ҵ������Ĳ���Ҫʵ������ӿڡ�
	 */
	public String[] getDateType(String billtype) {

		return new String[] { "djrq", "shrq", "jsrq"};
	}

}
