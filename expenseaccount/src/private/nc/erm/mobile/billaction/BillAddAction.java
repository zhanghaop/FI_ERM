package nc.erm.mobile.billaction;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

public abstract class BillAddAction {
	protected String pk_group;
	protected String djlxbm;

	//���ñ�ͷĬ��ֵ
	public abstract AggregatedValueObject setDefaultValue() throws BusinessException;
	
	//���ݱ�ͷ��������Ĭ��ֵ
	public SuperVO setBodyDefaultValue(SuperVO parentVO, String tablecode, String classname) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
