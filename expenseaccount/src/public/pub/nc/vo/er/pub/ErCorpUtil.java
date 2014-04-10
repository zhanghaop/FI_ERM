package nc.vo.er.pub;

import nc.bs.er.util.BXBsUtil;
import nc.itf.fi.pub.SysInit;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;


public class ErCorpUtil {


	/**
	 * ���б��ʷ��ش����½��˾PK
	 * ���򷵻ر�������PK
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
	*  �����������
	 */
	public static String getBxCtlMan(IFYControl ify) throws BusinessException{

		if(ify.getOperator()==null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0158")/*@res "����¼���˲���Ϊ��"*/);
		}
		
		if(BXParamConstant.ER_PF_STARTER_CREATOR.equals(SysInit.getParaString(ify.getPk_group(), BXParamConstant.PARAM_PF_STARTER))){/*-=notranslate=-*/
			return ify.getOperator();
		}else{
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