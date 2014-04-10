package nc.itf.erm.closeaccbook;

import java.util.List;

import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.org.LiabilityBookVO;
import nc.vo.pub.BusinessException;

public interface IBatchCloseAccBookQryService {

	/**
	 * 根据期间方案获取所有关联的会计期间
	 * <p>
	 * 修改记录：
	 * </p>
	 * 
	 * @param pk_accperiodscheme
	 * @return
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	public List<AccperiodmonthVO> getPeriodmonthsByScheme(
			String pk_accperiodscheme) throws BusinessException;

	/**
	 * 根据期间方案获取所有关联的责任账簿
	 * <p>修改记录：</p>
	 * @param pk_accperiodscheme
	 * @return
	 * @throws BusinessException
	 * @see 
	 * @since V6.0
	 */
	public List<LiabilityBookVO> getLiabooksByScheme(String pk_accperiodscheme)
			throws BusinessException;
}
