package nc.vo.erm.matterapp.ext;

import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MatterAppYsControlVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

/**
 * �������뵥Ԥ�����vo
 * 
 * ����Ԫ��Ŀר��
 * 
 * @author lvhj
 *
 */
public class MatterAppYsControlVOExt extends MatterAppYsControlVO{
	
	private static final long serialVersionUID = 1L;
	
	public MatterAppYsControlVOExt(MatterAppVO parentvo, MtAppDetailVO detailvo) {
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
		parentvo.setApprovetime(new UFDateTime(ysDate.toDate()));
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
		detailvo.setOrig_amount(amount[0]);
		detailvo.setOrg_amount(amount[1]);
		detailvo.setGroup_amount(amount[2]);
		detailvo.setGlobal_amount(amount[3]);
	}
}
