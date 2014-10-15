package nc.pubitf.erm.billcontrast;

import nc.vo.pub.BusinessException;

/**
 * 费用单据对照，对外提供的查询服务
 * 
 * @author lvhj
 *
 */
public interface IErmBillcontrastQueryService {

	/**
	 * 根据组织、来源交易类型code，查询目标交易类型code
	 * 
	 * @param pk_org
	 * @param src_tradetype
	 * @return
	 * @throws BusinessException
	 */
	public String queryDesTradetypeBySrc(String pk_org,String src_tradetype) throws BusinessException;
}
