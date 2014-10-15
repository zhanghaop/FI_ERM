package nc.bs.erm.expamortize.control;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.pub.BusinessException;

/**
 * 
 * ��̯��ϢԤ����ƽӿ�
 * 
 * @author chenshuaia
 *
 */
public interface IErmExpamortizeYsControlService {
	/**
	 * Ԥ��ҵ����
	 * 
	 * @param vos
	 *            ��̯��Ϣvos
	 * @param isContray
	 *            �Ƿ������
	 * @param actionCode
	 *            ��������
	 * @throws BusinessException
	 */
	@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "̯����ϢԤ����ƽӿ�" /*-=notranslate=-*/,type=BusinessType.DOMAIN_INT)
	public void ysControl(AggExpamtinfoVO[] vos, boolean isContray,
			String actionCode) throws BusinessException;
}
