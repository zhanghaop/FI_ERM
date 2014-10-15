package nc.impl.erm.fieldcontrast;

import nc.bs.bd.baseservice.md.BatchBaseService;
import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.itf.erm.fieldcontrast.IFieldContrastMgService;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.erm.fieldcontrast.FieldcontrastVO;
import nc.vo.pub.BusinessException;

public class FieldContrastMgServiceImpl extends BatchBaseService<FieldcontrastVO> implements IFieldContrastMgService {
	private static final String MDId = "54a18bd1-6e2b-4009-937d-08811ac8dcea";
	
	public FieldContrastMgServiceImpl() {
		super(MDId);
	}
	
	@Override
	public BatchOperateVO batchSave(BatchOperateVO vo) throws BusinessException {
		BatchOperateVO res = super.batchSave(vo);
		// Çå³ý»º´æ
		ErmBillFieldContrastCache.clearCache();
		return res;
	}

}
