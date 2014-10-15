package nc.itf.erm.matterapp;

import nc.bs.erm.matterapp.common.MatterAppQueryCondition;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * �ڲ���ѯʹ�ýӿ�
 * 
 * @author lvhj
 *
 */
public interface IErmMatterAppBillQueryPrivate {
	/**
	 * ���ݲ�ѯ������ѯ
	 * <li>�������뵥¼�� :�ɲ�ѯ��ǰ�û�¼��ĺ�������Ϊ���˵ĵ��� 
	 * <li>�������뵥����:�ɲ�ѯ��ǰ�û�Ϊ�����˵ĵ��ݡ�
	 * @param sqlWhere
	 * @return
	 * @throws BusinessException
	 */
	public String[] queryBillPksByWhere(MatterAppQueryCondition condVo) throws BusinessException;

	public MatterAppVO[] getMtappByMthPk(String pk_org, String begindate,
			String enddate) throws BusinessException;
}
