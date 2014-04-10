package nc.impl.erm.costshare;

import nc.bs.erm.costshare.ErmCostShareBO;
import nc.pubitf.erm.costshare.IErmCostShareBillManage;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;

/**
 * ���ý�ת��ά���ʵ��
 * 
 * @author lvhj
 *
 */
public class ErmCostShareBillManageImpl implements IErmCostShareBillManage {
	
	public AggCostShareVO insertVO(AggCostShareVO vo) throws BusinessException {
		((CostShareVO)vo.getParentVO()).setBillstatus(BXStatusConst.DJZT_Saved);
		return new ErmCostShareBO().insertVO(vo);
	}

	public AggCostShareVO updateVO(AggCostShareVO vo) throws BusinessException {
		((CostShareVO)vo.getParentVO()).setBillstatus(BXStatusConst.DJZT_Saved);
		return new ErmCostShareBO().updateVO(vo);
	}

	public MessageVO[] deleteVOs(AggCostShareVO[] vos) throws BusinessException {
		return new ErmCostShareBO().deleteVOs(vos);
	}

}
