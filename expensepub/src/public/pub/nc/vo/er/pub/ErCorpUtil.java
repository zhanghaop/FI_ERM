package nc.vo.er.pub;

import nc.bs.er.util.BXBsUtil;
import nc.itf.fi.pub.SysInit;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;


public class ErCorpUtil {


	/**
	 * 集中报帐返回代表登陆公司PK
	 * 否则返回报销主体PK
	 * @throws BusinessException
	 */
	public static String getLogin_unit(JKBXHeaderVO ify){
		return ify.getPk_group();
	}
	public static String getPsn_unit(JKBXHeaderVO ify){
		return ify.getDwbm();
	}
	public static String getPay_unit(JKBXHeaderVO ify){
		return ify.getPk_org();
	}
	public static String getCost_unit(JKBXHeaderVO ify) {
		return ify.getPk_group();
	}

	/**
	*  审批流起点人
	 */
	public static String getBxCtlMan(IFYControl ify) throws BusinessException{

		if(ify.getOperator()==null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0158")/*@res "单据录入人不能为空"*/);
		}
//begin--modified by chendya@ufida.com.cn	修改为取集团级参数
		if("录入人".equalsIgnoreCase(SysInit.getParaString(ify.getPk_group(), BXParamConstant.PARAM_PF_STARTER))){/*-=notranslate=-*/
//--end
			return ify.getOperator();
		}else{
//			UserVO user = ((nc.itf.uap.rbac.IUserManageQuery)NCLocator.getInstance().lookup(nc.itf.uap.rbac.IUserManageQuery.class.getName())).queryUserVOByPsnDocID(ify.getJkbxr());
//			if(user!=null){
//				return user.getCuserid();
//			}else{
//				return null;
//			}
			return BXBsUtil.getCuserIdByPK_psndoc(ify.getJkbxr());

		}
	}
	public static String getLogin_unit(IFYControl control) {
		return getLogin_unit((JKBXHeaderVO)control);
	}
	public static String getCost_unit(IFYControl control) {
		return getCost_unit((JKBXHeaderVO)control);
	}
}