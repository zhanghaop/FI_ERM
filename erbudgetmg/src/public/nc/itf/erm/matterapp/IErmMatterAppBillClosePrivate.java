package nc.itf.erm.matterapp;

import nc.pubitf.erm.matterapp.IErmMatterAppBillClose;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * �������뵥���ڹرշ��񣬺�̨����ִ��ʹ�ã��������ṩ
 * 
 * @author lvhj
 *
 */
public interface IErmMatterAppBillClosePrivate extends IErmMatterAppBillClose{
	
	/**
	 * ��̨ϵͳ�û�-�Զ��رյ���
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */

	public AggMatterAppVO[] closeVOsBackgroud(AggMatterAppVO[] vos) throws BusinessException;

}
