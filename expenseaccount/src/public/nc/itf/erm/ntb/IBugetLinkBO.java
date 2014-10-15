package nc.itf.erm.ntb;

import java.util.List;

import nc.vo.ep.bx.BxDetailLinkQueryVO;
import nc.vo.pub.BusinessException;
import nc.vo.tb.obj.NtbParamVO;

/**
 * @author liansg
 *
 */
public interface IBugetLinkBO {
	
	public List<BxDetailLinkQueryVO> getLinkDatas(NtbParamVO ntbParamVO) throws BusinessException;
	
	
}
