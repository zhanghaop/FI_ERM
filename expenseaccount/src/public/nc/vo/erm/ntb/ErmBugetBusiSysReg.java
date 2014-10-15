package nc.vo.erm.ntb;

import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.server.util.NewObjectService;
import nc.itf.er.pub.IArapBillTypePublic;
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
public class ErmBugetBusiSysReg implements IBusiSysReg ,IDateType{


	public ErmBugetBusiSysReg() {
		super();
	}
	
	public ArrayList<ControlBillType> getBillType() {
		ArrayList<ControlBillType> result =new ArrayList<ControlBillType>();
		DjLXVO[] djlxs = null;
		try {
			djlxs = NCLocator.getInstance().lookup(IArapBillTypePublic.class).queryAllBillTypeByCorp("0001");
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			return null;
		}
		if (djlxs == null || djlxs.length < 1)
			return null;

		
		for (int i = 0; i < djlxs.length; i++) {
			if (djlxs[i].getFcbz() != null && djlxs[i].getFcbz().booleanValue())
				continue;
			ControlBillType e = new ControlBillType();
			e.setPk_billType(djlxs[i].getDjlxbm());
			e.setBillType_code(djlxs[i].getDjlxbm());
			e.setBillType_name(djlxs[i].getDjlxmc());
			result.add(e);
		}
		return result;
	}


	public String getBusiSysDesc() {
		
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000399")/*@res "NC��������"*/;
	}


	public String getBusiSysID() {
		
		return "NC_ERM";
	}


	public String[] getBusiType() {

		return new String[] { "������" }; /*-=notranslate=-*/
	}


	public String[] getBusiTypeDesc() {

		return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000400")/*@res "������"*/ };
	}


	public String[] getControlableDirections() {

		return new String[] { "��", "��" }; /*-=notranslate=-*/
	}


	public String[] getControlableDirectionsDesc() {

		return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000401")/*@res "��"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000402")/*@res "��"*/ };
	}


	public ArrayList<ControlObjectType> getControlableObjects() {

		ArrayList<ControlObjectType> reArrayList=new ArrayList<ControlObjectType>();
		ControlObjectType e=new ControlObjectType("FI_BILL_EXEC",nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000074")/*@res "������"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000074")/*@res "������"*/);
		reArrayList.add(e);
		return reArrayList;
	}

	
	public String[] getControlableObjectsDesc() {
		return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000400")/*@res "������"*/ };
	}

	public IBusiSysExecDataProvider getExecDataProvider() {
		return (IBusiSysExecDataProvider) NewObjectService.newInstance(BXConstans.ERM_PRODUCT_CODE_Lower,"nc.bs.er.control.ErmNtbProvider");
	}

	public boolean isBudgetControling() {
		return true;
	}

	public String[] getDataType() {
		return new String[] { "djrq", "shrq" };
	}

	public String[] getDataTypeDesc() {
		return new String[]{nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000248")/*@res "��������"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPTcommon-000037")/*@res "�������"*/};
	}

	public ArrayList<ControlObjectType> getControlableObjects(String billtype) {

		return null;
	}


	public String getMainPkOrg() {

		return null;
	}


	public boolean isSupportMutiSelect() {

		return false;
	}


	public boolean isUseAccountDate(String billtype) {

		return false;
	}


	public String[] getDateType(String billtype) {

		return null;
	}

}
