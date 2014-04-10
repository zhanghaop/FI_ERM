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
	 * 调用模块是否安装
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
	 * 调用启用模块API(非预算调用)
	 * @param pk_group 集团
	 * @param funcode 功能节点 数据来源于dap_dapsystem
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
	 * 根据pk_group和pk_org解析节点类型
	 * @param pk_group 集团
	 * @param pk_org 主组织
	 * @return
	 */
	public static NODE_TYPE getNodeTypeByPk_groupAndPk_org(String pk_group, String pk_org) {
        if (!StringUtil.isEmpty(pk_org) && !StringUtil.isEmpty(pk_group) && pk_org.equals(pk_group)) {
            //所属组织、所属集团均不为空且两者相等的为集团级数据
            return NODE_TYPE.GROUP_NODE;
        } else if (!StringUtil.isEmpty(pk_org) && !StringUtil.isEmpty(pk_group) && !pk_org.equals(pk_group) && !pk_org.equals(IOrgConst.GLOBEORG)){
            //所属组织、所属集团均不为空且所属组织不等于所属集团且所属组织不等于全局组织的为组织级数据
            return NODE_TYPE.ORG_NODE;
        }
        //其它为全局级数据（包括无所属组织、所属集团字段或无该字段值的数据）
        return NODE_TYPE.GLOBE_NODE;
    }
	
	/**
	 * 批量根据模块号，业务单元oid,会计期间(年-月)判断是否关账。若已关帐返回true
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
		// 设置日期
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
