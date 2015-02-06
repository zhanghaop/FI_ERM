package nc.erm.mobile.billaction;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

public abstract class BillAddAction {
	protected String pk_group;
	protected String djlxbm;

	//���ñ�ͷĬ��ֵ
	public abstract AggregatedValueObject setDefaultValue() throws BusinessException;
	
	//���ݱ�ͷ��������Ĭ��ֵ
	public abstract String setBodyDefaultValue(String head, String tablecode,
			String itemnum, String classname) throws BusinessException;
	
}
