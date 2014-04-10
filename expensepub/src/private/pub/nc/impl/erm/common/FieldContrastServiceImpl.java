package nc.impl.erm.common;

import java.util.Collection;

import nc.bs.bd.baseservice.md.BatchBaseService;
import nc.bs.dao.BaseDAO;
import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.itf.erm.prv.IFieldContrastService;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.erm.fieldcontrast.FieldcontrastVO;
import nc.vo.pub.BusinessException;

public class FieldContrastServiceImpl extends BatchBaseService<FieldcontrastVO> implements IFieldContrastService {
	private static final String MDId = "54a18bd1-6e2b-4009-937d-08811ac8dcea";
	
	private BaseDAO dao = null;
	
	public FieldContrastServiceImpl() {
		super(MDId);
	}
	
	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException {
		BatchOperateVO res = super.batchSave(batchVO);
		// Çå³ý»º´æ
		ErmBillFieldContrastCache.clearCache();
		return res;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public FieldcontrastVO[] queryFieldContrastVOS(int app_scene, String src_billtype, String des_billtype) throws BusinessException {
		if(src_billtype != null && des_billtype != null){
			StringBuffer sql = new StringBuffer();
			
			sql.append(FieldcontrastVO.APP_SCENE + "=" + app_scene )
			   .append(" and "+FieldcontrastVO.SRC_BILLTYPE+" = '"+src_billtype+"'")
			   .append(" and "+FieldcontrastVO.DES_BILLTYPE+" = '"+des_billtype+"'");
			
			Collection<FieldcontrastVO> result = getBaseDao().retrieveByClause(FieldcontrastVO.class, sql.toString());
			
			return result.toArray(new FieldcontrastVO[]{});
		}
		
		return null;
	}
	
	public BaseDAO getBaseDao(){
		if(dao == null ){
			dao = new BaseDAO();
		}
		return dao;
	}
}
