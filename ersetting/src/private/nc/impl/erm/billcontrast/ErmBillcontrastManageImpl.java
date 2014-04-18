package nc.impl.erm.billcontrast;

import nc.bs.bd.baseservice.md.BatchBaseService;
import nc.itf.erm.billcontrast.IErmBillcontrastManage;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.erm.billcontrast.BillcontrastVO;
import nc.vo.pub.BusinessException;

public class ErmBillcontrastManageImpl extends BatchBaseService<BillcontrastVO>implements IErmBillcontrastManage {
	private final static String MDID="f495d9be-96af-41b6-827f-e10a45e58512";
	
	public ErmBillcontrastManageImpl(){
		super(MDID);
	}
	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO)
			throws BusinessException {
		return super.batchSave(batchVO);
	}
	
	

}
