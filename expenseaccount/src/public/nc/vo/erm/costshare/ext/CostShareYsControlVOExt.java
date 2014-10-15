package nc.vo.erm.costshare.ext;

import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.CostShareYsControlVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * ���ý�ת����дԤ��VO
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 *
 */
public class CostShareYsControlVOExt extends CostShareYsControlVO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CostShareYsControlVOExt(CostShareVO parentvo, CShareDetailVO detailvo) {
		super(parentvo, detailvo);
	}

	/**
	 * ����Ԥ������
	 * 
	 * @param ysDate
	 */
	public void setYsDate(UFDate ysDate){
		// ��������ʱ���Ϊ��ǰԤ������
		parentvo.setBilldate(ysDate);
		parentvo.setApprovedate(ysDate);
	}
	
	/**
	 * ���÷��ڷ�̯�Ľ��
	 * 
	 * ��ԭ�ҽ���֯���ҽ����ű��ҽ�ȫ�ֱ��ҽ�
	 * 
	 * @param amount
	 */
	public void setYsAmount(UFDouble[] amount){
		if(amount == null || amount.length != 4){
			return ;
		}
		detailvo.setAssume_amount(amount[0]);
		detailvo.setBbje(amount[1]);
		detailvo.setGroupbbje(amount[2]);
		detailvo.setGlobalbbje(amount[3]);
	}

}
