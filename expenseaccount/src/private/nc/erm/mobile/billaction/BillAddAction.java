package nc.erm.mobile.billaction;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

public abstract class BillAddAction {
	protected String pk_group;
	protected String djlxbm;

	//设置表头默认值
	public abstract AggregatedValueObject setDefaultValue() throws BusinessException;
	
	//根据表头带出表体默认值
	public abstract String setBodyDefaultValue(String head, String tablecode,
			String itemnum, String classname) throws BusinessException;
	
}
