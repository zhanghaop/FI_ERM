package nc.itf.arap.pub;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppConvResVO;
import nc.vo.pub.BusinessException;

/**
 * 设置界面默认值
 * @author wangled
 *
 */
public interface IErmBillUIPublic {

	/**
	 * @param djdl : 单据大类
	 * @param funnode ：功能节点编号
	 * @return
	 */
	public JKBXVO setBillVOtoUI(DjLXVO djlx,String funnode,JKBXVO jkbxvo)throws BusinessException;

	/**
	 * 拉单场景初始数据包装
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
