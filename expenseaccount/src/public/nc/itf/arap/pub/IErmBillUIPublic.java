package nc.itf.arap.pub;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppConvResVO;
import nc.vo.pub.BusinessException;

/**
 * ���ý���Ĭ��ֵ
 * @author wangled
 *
 */
public interface IErmBillUIPublic {

	/**
	 * @param djdl : ���ݴ���
	 * @param funnode �����ܽڵ���
	 * @return
	 */
	public JKBXVO setBillVOtoUI(DjLXVO djlx,String funnode,JKBXVO jkbxvo)throws BusinessException;

	/**
	 * ����������ʼ���ݰ�װ
	 * 
	 * @param pk_org
	 * @param retvo
	 * @param djlx
	 * @param funnode
	 * @return
	 * @throws BusinessException
	 */
	public MatterAppConvResVO setBillVOtoUIByMtappVO(String pk_org, AggMatterAppVO retvo,
			DjLXVO djlx,String funnode)throws BusinessException;
}
