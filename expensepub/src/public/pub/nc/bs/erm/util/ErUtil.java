package nc.bs.erm.util;

import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.org.IOrgConst;
import nc.itf.uap.sf.ICreateCorpQueryService;
import nc.itf.uap.sf.IProductVersionQueryService;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.org.ICloseAccQryPubServicer;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.sm.install.ProductVersionVO;

public class ErUtil {
	/**
	 * ����ģ���Ƿ�װ
	 * 
	 * @param pro
	 * @return
	 */
	public static boolean isProductTbbInstalled(String pro) {
		boolean value = false;
		
		try {
			ProductVersionVO[] ProductVersionVOs  = NCLocator.getInstance().lookup(IProductVersionQueryService.class).queryByProductCode(pro);
			if(ProductVersionVOs == null || ProductVersionVOs.length==0){
				value = false;
			} else {
				value = true;
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return value ;
	}
	
	/**
	 * ��������ģ��API(��Ԥ�����)
	 * @param pk_group ����
	 * @param funcode ���ܽڵ� ������Դ��dap_dapsystem
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isProductInstalled(String strCorpPK,String pro) {
	
		boolean value = false;
		
		try {
			value = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).isEnabled(strCorpPK, pro);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return value ;
	}
	
	/**
	 * ����pk_group��pk_org�����ڵ�����
	 * @param pk_group ����
	 * @param pk_org ����֯
	 * @return
	 */
	public static NODE_TYPE getNodeTypeByPk_groupAndPk_org(String pk_group, String pk_org) {
        if (!StringUtil.isEmpty(pk_org) && !StringUtil.isEmpty(pk_group) && pk_org.equals(pk_group)) {
            //������֯���������ž���Ϊ����������ȵ�Ϊ���ż�����
            return NODE_TYPE.GROUP_NODE;
        } else if (!StringUtil.isEmpty(pk_org) && !StringUtil.isEmpty(pk_group) && !pk_org.equals(pk_group) && !pk_org.equals(IOrgConst.GLOBEORG)){
            //������֯���������ž���Ϊ����������֯����������������������֯������ȫ����֯��Ϊ��֯������
            return NODE_TYPE.ORG_NODE;
        }
        //����Ϊȫ�ּ����ݣ�������������֯�����������ֶλ��޸��ֶ�ֵ�����ݣ�
        return NODE_TYPE.GLOBE_NODE;
    }
	
	/**
	 * ��������ģ��ţ�ҵ��Ԫoid,����ڼ�(��-��)�ж��Ƿ���ˡ����ѹ��ʷ���true
	 * 
	 * @param moduleCode
	 * @param pk_org
	 * @param date
	 * @throws BusinessException
	 */
	public static boolean isOrgCloseAcc(String moduleCode, String pk_org, UFDate date) throws BusinessException {
		if (moduleCode == null || pk_org == null || date == null) {
			return false;
		}
		moduleCode = moduleCode.substring(0, 4);
		AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(pk_org);
		// ��������
		calendar.setDate(date);
		String period = calendar.getMonthVO().getYearmth();
		Map<String, Boolean> res = NCLocator.getInstance().lookup(ICloseAccQryPubServicer.class).isCloseByModuleIdAndPk_org(
				moduleCode, pk_org, new String[] { period });
		if (res.get(period) != null && res.get(period).booleanValue()) {
			return true;
		}
		
		return false;
	}
	
}
