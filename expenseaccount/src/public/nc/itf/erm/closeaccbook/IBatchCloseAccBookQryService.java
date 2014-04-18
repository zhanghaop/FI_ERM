package nc.itf.erm.closeaccbook;

import java.util.List;

import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.org.LiabilityBookVO;
import nc.vo.pub.BusinessException;

public interface IBatchCloseAccBookQryService {

	/**
	 * �����ڼ䷽����ȡ���й����Ļ���ڼ�
	 * <p>
	 * �޸ļ�¼��
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
	 * �����ڼ䷽����ȡ���й����������˲�
	 * <p>�޸ļ�¼��</p>
	 * @param pk_accperiodscheme
	 * @return
	 * @throws BusinessException
	 * @see 
	 * @since V6.0
	 */
	public List<LiabilityBookVO> getLiabooksByScheme(String pk_accperiodscheme)
			throws BusinessException;
}
