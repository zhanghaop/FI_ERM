/**
 * 
 */
package nc.vo.fibill.outer;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.bs.logging.Logger;
import nc.erm.pub.cache.FiBillFieldContrastCache;
import nc.itf.tb.control.IAccessableBusiVO;
import nc.itf.tb.control.IBillDataGeter;
import nc.itf.tb.control.IBillsControl;
import nc.itf.uap.busibean.ISysInitQry;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

/**
 * ���񵥾�ΪԤ����ƽ���vo�ӿ�ʵ�ֵ���Ĵ�����

 * @version V5.5
 * @since V5.5
 * 2008-7-22
 */
public class FiBillAccessableBusiVOProxy extends FiBillAccessableBusiVO{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9086703653236006650L;

	private FiBillAccessableBusiVO proxyed = null;
	private String subSysID = null;
	
	private boolean isLinkQuery = false;
	
	private ISysInitQry sysInit = null;


	protected ISysInitQry getISysInitQry()
	{
		if(sysInit == null)
			sysInit = NCLocator.getInstance().lookup(ISysInitQry.class);
		return sysInit;
	}

	/**
	 * @param proxyed ��������࣬����ͬʱʵ��Ԥ��IBillsControl, IBillDataGeter�ӿڡ�
	 * @param subSysID ��ϵͳ��־��ss/yt/arap/er/cmp
	 */
	public FiBillAccessableBusiVOProxy(FiBillAccessableBusiVO proxyed, String subSysID) {
		super();
		this.proxyed = proxyed;
		this.subSysID = subSysID;
	}

	/* (non-Javadoc)
	 * @see nc.vo.ntb.outer.IAccessableBusiVO#getAllUpLevels(java.lang.String, java.lang.String)
	 */
	@Override
	public String[] getAllUpLevels(String fieldname, String pk)
	throws Exception {
		return proxyed.getAllUpLevels(fieldname, pk);
	}

	/* (non-Javadoc)
	 * @see nc.vo.ntb.outer.IAccessableBusiVO#getAttributesValue(java.lang.String[])
	 */
	public String[] getAttributesValue(String[] attrs) {
		return null;
	}

	/* (non-Javadoc)
	 * @see nc.vo.ntb.outer.IAccessableBusiVO#getAttributesValue(java.lang.String)
	 */
	public String getAttributesValue(String attr) {
//		V5.5������Ԥ�㰴�������Ϳ��ƣ�����ͨ���÷���ȡ����ͬ�����ڸ���ͬ��attrName
//		Ϊʵ��ִ�������ѯ�����˱���ע���������ֶζ���
		if(attr.trim().equals(BXConstans.BILLDATE) || attr.trim().equals(BXConstans.APPROVEDATE)) 
			return proxyed.getAttributesValue(attr);
		if(attr.trim().equalsIgnoreCase("zb.pk_org") || attr.trim().equalsIgnoreCase("pk_org") || attr.trim().equalsIgnoreCase("pkorg"))
			return proxyed.getPKOrg();
		String newAttr = FiBillFieldContrastCache.getBusiAttFieldBySubSysID(subSysID, attr);
		if(newAttr == null)  //�ֶβ�����
			newAttr=attr;
		String str = proxyed.getAttributesValue(newAttr);

		if(isLinkQuery)
		{
//			String ctlParam = "0";
//			try {
//				ctlParam = getControlParam();
//			} catch (BusinessException e) {
//				nc.bs.logging.Log.getInstance(this.getClass()).error(e);
//			}
//			if(ctlParam.equals("1") && "zb.shrq".equals(attr) && StringUtil.isEmptyWithTrim(str)){
//				Logger.debug("Ԥ��ȡ����������ڣ��������Ϊ�գ����ص�¼���ڣ�");
//				if(RuntimeEnv.getInstance().isRunningInServer())
//					//FIXME
////					return UFDate.getDate(Long.parseLong(InvocationInfoProxy.getInstance().getDate())).toString();
//					return null;
//				else					
//					return null;
//			}
//			else
				return str;
		}
		else
			return str;
	}
	
	
	private String getControlParam() throws BusinessException
	{
		String pk_corp = null;
		if(RuntimeEnv.getInstance().isRunningInServer())
			//FIXME
			pk_corp = null;
//			pk_corp = InvocationInfoProxy.getInstance().getCorpCode();
		else
//			pk_corp = ClientEnvironment.getInstance().getCorporation().getPk_corp();
		    pk_corp = null;
		String param = null;
		//��̨
		param = getISysInitQry().getParaString(pk_corp, "FICOMMON03");
		return param;
	}
	
	
	


	/* (non-Javadoc)
	 * @see nc.vo.ntb.outer.IAccessableBusiVO#getBillType()
	 */
	public String getBillType() {
		return proxyed.getBillType();
	}

	/* (non-Javadoc)
	 * @see nc.vo.ntb.outer.IAccessableBusiVO#getBusiDate()
	 */
	public String getBusiDate() {
		Logger.debug("Ԥ�����V5.5��ǰ��ʱ������������ֲ���Ԥ�ϵĴ���");
		return proxyed.getBusiDate();
	}

//	/* (non-Javadoc)
//	 * @see nc.vo.ntb.outer.IAccessableBusiVO#getBusiSys()
//	 */
//	public String getBusiSys() {
//		return proxyed.getBusiSys();
//	}
//
//	/* (non-Javadoc)
//	 * @see nc.vo.ntb.outer.IAccessableBusiVO#getBusiType()
//	 */
//	public String getBusiType() {
//		return proxyed.getBusiType();
//	}

	/* (non-Javadoc)
	 * @see nc.vo.ntb.outer.IAccessableBusiVO#getCurrency()
	 */
	public String getCurrency() {
		return proxyed.getCurrency();
	}

	/* (non-Javadoc)
	 * @see nc.vo.ntb.outer.IAccessableBusiVO#getHasLevelFlds()
	 */
	@Override
	public String[] getHasLevelFlds() {
		return proxyed.getHasLevelFlds();
	}

	/* (non-Javadoc)
	 * @see nc.vo.ntb.outer.IAccessableBusiVO#getPKCorp()
	 */
	/* (non-Javadoc)
	 * @see nc.vo.ntb.outer.IAccessableBusiVO#getPkNcEntity()
	 */
	public String getPkNcEntity() {
		return null;
	}

	/* (non-Javadoc)
	 * @see nc.vo.ntb.outer.IAccessableBusiVO#isUnInure()
	 */
	public boolean isUnInure() {
		return proxyed.isUnInure();
	}

	public boolean[][] isBillControl(String[] billTypes, IAccessableBusiVO[][] busiVOs) throws Exception {
		return ((IBillsControl)proxyed).isBillControl(billTypes, busiVOs);
	}

	public String getDdlx() {
		return ((IBillDataGeter)proxyed).getDdlx();
	}

	public String getDjdl() {
		return ((IBillDataGeter)proxyed).getDjdl();
	}

	public Integer getItemFx() {
		return ((IBillDataGeter)proxyed).getItemFx();
	}

	public String getItem_bill_pk() {
		return ((IBillDataGeter)proxyed).getItem_bill_pk();
	}

	@Override
	public String getPkOrgInBillHead() {
		return proxyed.getPkOrgInBillHead();
	}

	public boolean isLinkQuery() {
		return isLinkQuery;
	}

	public void setLinkQuery(boolean isLinkQuery) {
		this.isLinkQuery = isLinkQuery;
	}

	@Override
	public String getDateType() {
		return proxyed.getDateType();
	}

	@Override
	public String getPKGroup() {
		return proxyed.getPKGroup();
	}

	@Override
	public String getPKOrg() {
		return proxyed.getPKOrg();
	}


	@Override
	public String getDataType() {
		// TODO Auto-generated method stub
		return proxyed.getDataType();
	}

	@Override
	public UFDouble[] getExeData(String direction, String obj, String extObj) {
		// TODO Auto-generated method stub
		return proxyed.getExeData(direction, obj, extObj);
	}

}
