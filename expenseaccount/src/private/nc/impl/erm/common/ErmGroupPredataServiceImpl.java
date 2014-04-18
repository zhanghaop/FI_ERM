package nc.impl.erm.common;

import java.util.ArrayList;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.erm.billcontrast.IErmBillcontrastQuery;
import nc.itf.erm.fieldcontrast.IFieldContrastQryService;
import nc.itf.erm.service.IErmGroupPredataService;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.erm.billcontrast.BillcontrastVO;
import nc.vo.erm.fieldcontrast.FieldcontrastVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.util.AuditInfoUtil;

/**
 * 预置数据：字段对照、单据对照
 * 
 * @author lvhj
 * 
 */
public class ErmGroupPredataServiceImpl implements IErmGroupPredataService {

	@Override
	public void initGroupData(String[] groupPks) throws BusinessException {
		// 初始化维度对照
		initFieldContrast(groupPks);
		//初始化单据对照
		initBillContrast(groupPks);
	}

	private void initBillContrast(String[] groupPks) throws BusinessException {
		// 先删除后插入
		BaseDAO dao = new BaseDAO();
		@SuppressWarnings("rawtypes")
		List c = (List) dao.retrieveByClause(BillcontrastVO.class, SqlUtils.getInStr("pk_group", groupPks, true));
		dao.deleteVOList(c);
		
		// 查询待预置的单据对照数据
		BillcontrastVO[] billvos = NCLocator.getInstance()
				.lookup(IErmBillcontrastQuery.class).queryAllByGloble();
		// 复制新增数据
		List<BillcontrastVO> billvoList = new ArrayList<BillcontrastVO>();
		for (int i = 0; i < billvos.length; i++) {
			String src_tradetype = billvos[i].getSrc_tradetype();
			String des_tradetype = billvos[i].getDes_tradetype();
			for (int j = 0; j < groupPks.length; j++) {
				String groupPk = groupPks[j];
				BilltypeVO[] srcbillTypeInfos = (BilltypeVO[]) CacheUtil.getValueFromCacheByWherePart(BilltypeVO.class,
						" pk_group='" + groupPk + "' and pk_billtypecode='" + src_tradetype + "' ");

				BilltypeVO[] desbillTypeInfos = (BilltypeVO[]) CacheUtil.getValueFromCacheByWherePart(BilltypeVO.class,
						" pk_group='" + groupPk + "' and pk_billtypecode='" + des_tradetype + "' ");
				
				if (srcbillTypeInfos == null || desbillTypeInfos == null) {
					// 交易类型若不存在于这个集团，则不复制
					continue;
				}
				BillcontrastVO vo = (BillcontrastVO) billvos[i].clone();
				vo.setPrimaryKey(null);
				vo.setPk_group(groupPk);
				vo.setPk_org(groupPk);
				vo.setSrc_tradetypeid(srcbillTypeInfos[0].getPrimaryKey());
				vo.setDes_tradetypeid(desbillTypeInfos[0].getPrimaryKey());
				AuditInfoUtil.addData(vo);
				vo.setStatus(VOStatus.NEW);
				billvoList.add(vo);

			}
		}
		
		if (!billvoList.isEmpty()) {
			MDPersistenceService
			.lookupPersistenceService().saveBill(billvoList.toArray(new BillcontrastVO[0]));
		}
	}

	private void initFieldContrast(String[] groupPks)
			throws BusinessException, MetaDataException {
		// 先删除后插入
		BaseDAO dao = new BaseDAO();
		@SuppressWarnings("rawtypes")
		List c = (List) dao.retrieveByClause(FieldcontrastVO.class, SqlUtils.getInStr("pk_group", groupPks, true));
		dao.deleteVOList(c);
		// 查询待预置的字段对照数据
		FieldcontrastVO[] fieldvos = NCLocator.getInstance()
				.lookup(IFieldContrastQryService.class).qryPredataVOs();
		// 复制新增数据
		List<FieldcontrastVO> fieldvoList = new ArrayList<FieldcontrastVO>();
		for (int i = 0; i < fieldvos.length; i++) {
			for (int j = 0; j < groupPks.length; j++) {
				FieldcontrastVO vo = (FieldcontrastVO) fieldvos[i].clone();
				vo.setPrimaryKey(null);
				String groupPk = groupPks[j];
				vo.setPk_group(groupPk);
				vo.setPk_org(groupPk);
				String des_billtype = vo.getDes_billtype();
				if (des_billtype == null) {
					vo.setDes_billtype("~");
				}else{
					vo.setDes_billtypepk(PfDataCache.getBillTypeInfo(groupPk,
							des_billtype) == null ? null : PfDataCache
									.getBillTypeInfo(groupPk, des_billtype)
									.getPrimaryKey());
				}
				String src_billtype = vo.getSrc_billtype();
				vo.setSrc_billtypepk(PfDataCache.getBillTypeInfo(groupPk,
						src_billtype) == null ? null : PfDataCache
						.getBillTypeInfo(groupPk, src_billtype)
						.getPrimaryKey());
				AuditInfoUtil.addData(vo);
				vo.setStatus(VOStatus.NEW);
				fieldvoList.add(vo);
			}
		}
		if (!fieldvoList.isEmpty()) {
			MDPersistenceService
			.lookupPersistenceService().saveBill(fieldvoList.toArray(new FieldcontrastVO[0]));
		}
	}
}
