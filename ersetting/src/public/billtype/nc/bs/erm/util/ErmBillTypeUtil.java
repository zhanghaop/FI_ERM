package nc.bs.erm.util;

import nc.bs.framework.common.NCLocator;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.workflow.config.ConfigAgent;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;

/**
 * 单据类型工具类
 * 
 * @author lvhj
 *
 */
public class ErmBillTypeUtil {

	private ErmBillTypeUtil(){
		
	}
	
	public static BusiTypeVO getBusTypeVO(String djlxbm) {
		DjLXVO djlxvo = null;
		try {
			djlxvo = NCLocator.getInstance().lookup(IArapBillTypePublic.class).getDjlxvoByDjlxbm(djlxbm, "0001");
		} catch (BusinessException e) {
			nc.bs.logging.Log.getInstance("ermExceptionLog").error(e);
		}
		if(djlxvo==null)
			return null;
		return getBusTypeVO(djlxbm, djlxvo.getDjdl());
	}
	
	/**
	 * @return 取业务类型VO (busitype.xml定义内容)
	 * @see BusiTypeVO
	 */
	public static BusiTypeVO getBusTypeVO(String djlxbm, String djdl) {
		BusiTypeVO busiTypeVO = null;
		busiTypeVO = (BusiTypeVO) ((ConfigAgent)ConfigAgent.getInstance()).getCommonVO(BusiTypeVO.key,djlxbm);
		
		if(busiTypeVO==null){
			busiTypeVO = (BusiTypeVO) ConfigAgent.getInstance().getCommonVO(BusiTypeVO.key,djdl.equals(BXConstans.BX_DJDL)?BXConstans.BX_DJLXBM:BXConstans.JK_DJLXBM);
			
			BusiTypeVO vo=(BusiTypeVO) busiTypeVO.clone();
			vo.setId(djlxbm);
			ConfigAgent.getCache().putCommonVO(BusiTypeVO.key, vo);
		}
		
		return busiTypeVO;
	}
	
}
