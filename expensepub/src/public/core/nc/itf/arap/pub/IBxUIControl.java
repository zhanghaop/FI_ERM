package nc.itf.arap.pub;

import java.util.List;

import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * @author twei
 * 
 * nc.itf.arap.pub.IBxUIControl
 */
public interface IBxUIControl {
	
	/**
	 * @param head
	 * @return
	 * @throws BusinessException
	 * 
	 * 参数：报销单
	 * 参数2：冲销日期，在前台取登陆日期传入
	 * 参数3：处理查询其他借款人的单据功能，默认传入空，有具体查询条件时可以传入
	 * 
	 * 返回：能够进行冲销操作的借款单
	 */
	public List<JKBXHeaderVO> getJKD(JKBXHeaderVO head,UFDate cxrq,String queryStr) throws BusinessException ;
	
}
