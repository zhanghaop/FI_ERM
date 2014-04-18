package nc.impl.erm.fieldcontrast;

import java.util.Collection;

import nc.bs.dao.BaseDAO;
import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.itf.erm.fieldcontrast.IFieldContrastQryService;
import nc.itf.org.IOrgConst;
import nc.jdbc.framework.SQLParameter;
import nc.vo.erm.fieldcontrast.FieldcontrastVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

public class FieldContrastQryServiceImpl implements IFieldContrastQryService {

	@Override
	public FieldcontrastVO[] qryVOs(String pk_org, int app_scene,
			String src_billtype) throws BusinessException {
		String where = " pk_org = ? and app_scene=?";
		SQLParameter params = new SQLParameter();
		params.addParam(pk_org);
		params.addParam(app_scene);
		if (!StringUtil.isEmpty(src_billtype)) {
			where += " and src_billtype = ?";
			params.addParam(src_billtype);
		}
		BaseDAO dao = new BaseDAO();
		@SuppressWarnings("unchecked")
		Collection<FieldcontrastVO> c = dao.retrieveByClause(
				FieldcontrastVO.class, where, params);
		if (c != null && !c.isEmpty()) {
			return c.toArray(new FieldcontrastVO[0]);
		}
		return null;
	}

	@Override
	public FieldcontrastVO[] qryPredataVOs() throws BusinessException {
		// 待预置的数据为全局配置的费用控制规则维度对照和分摊规则对照
		String where = " pk_org = '"
				+ IOrgConst.GLOBEORG
				+ "' and app_scene in ("
				+ ErmBillFieldContrastCache.FieldContrast_SCENE_MatterAppCtrlField
				+ ","+ErmBillFieldContrastCache.FieldContrast_SCENE_SHARERULEField
				+ ")";
		BaseDAO dao = new BaseDAO();
		@SuppressWarnings("unchecked")
		Collection<FieldcontrastVO> c = dao.retrieveByClause(
				FieldcontrastVO.class, where);
		if (c != null && !c.isEmpty()) {
			return c.toArray(new FieldcontrastVO[0]);
		}
		return new FieldcontrastVO[0];
	}
}
