package nc.itf.erm.ntb;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.pub.BusinessException;

/**
 * 
 * ������ Ԥ����ơ���д�ӿ�
 * 
 * @author chenshuaia
 *
 */
public interface IBXYsControlService {
	
	/**
	 * Ԥ��ҵ����
	 * @param vos ����vos
	 * @param isContray
	 *            �Ƿ������
	 * @param actionCode
	 *            ��������
	 * @throws BusinessException
	 */
	@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "������Ԥ����ƽӿ�" /*-=notranslate=-*/,type=BusinessType.DOMAIN_INT)
	public void ysControl(JKBXVO[] vos, boolean isContray, String actionCode) throws BusinessException;
	
	/**
	 * Ԥ������޸�
	 * @param vos ����VO
	 * @throws BusinessException
	 */
	@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "����������Ԥ����ƽӿ�" /*-=notranslate=-*/,type=BusinessType.DOMAIN_INT)
	public void ysControlUpdate(JKBXVO[] vos) throws BusinessException;
}
