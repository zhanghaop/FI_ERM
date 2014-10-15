package nc.itf.arap.pub;

import nc.vo.pub.BusinessException;
import nc.vo.tb.obj.NtbParamVO;

/**
 * 申请单下游单据sql查询
 * 
 * @author chenshuaia
 * 
 */
public interface IErmMaSubBudgetSql {
	/**
	 * 获取下游单据有效业务行pks<br>
	 * 要实现功能是： 从detailPks中根据ntbParam 过滤出符合条件的业务行pk集合；
	 * 例如：<br>
	 * detailPks 中包含 3条数据，两条是保存态，一条是生效态；
	 * ntbParam 传的参数未取执行数，
	 * 则返回给一条生效态的pk即可
	 * @param detailPks
	 *            所有的有效业务行pk（所有状态的单据业务行pk）
	 * @param ntbParam
	 *            预算参数
	 * @return
	 * @throws BusinessException
	 */
	public String[] getMaBudgetSubBillEffectDetailPks(String[] detailPks, final NtbParamVO ntbParam)
			throws BusinessException;
}
