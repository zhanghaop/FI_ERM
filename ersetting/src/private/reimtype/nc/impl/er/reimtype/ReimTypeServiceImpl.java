package nc.impl.er.reimtype;


import java.util.ArrayList;
import java.util.List;

import nc.bs.bd.baseservice.BaseService;
import nc.bs.dao.BaseDAO;
import nc.itf.er.reimtype.IReimTypeService;
import nc.jdbc.framework.SQLParameter;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.er.reimtype.ReimTypeVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

public class ReimTypeServiceImpl extends BaseService<ReimTypeVO> implements IReimTypeService {
	/**
	 * @author liansg
	 */
	public ReimTypeServiceImpl(String MDId) {

		super(MDId);

	}
	
	public ReimTypeServiceImpl(){
		
		super("");
	}
	@Override	
	public BatchOperateVO batchSaveReimType(BatchOperateVO batchVO)
			throws BusinessException {
//		IMDPersistenceService service = MDPersistenceService
//				.lookupPersistenceService();
//		ArrayList<ReimTypeVO> list = new ArrayList<ReimTypeVO>();
//
//		Object[] addObjs = batchVO.getAddObjs();
//		if (addObjs != null) {
//			for (int i = 0; i < addObjs.length; i++) {
//				ReimTypeVO addVO = (ReimTypeVO) addObjs[i];
//				addVO.setStatus(VOStatus.NEW);
//				list.add(addVO);
//			}
//		}
//
//		Object[] delObjs = batchVO.getDelObjs();
//		if (delObjs != null) {
//			for (int i = 0; i < delObjs.length; i++) {
//				ReimTypeVO delVO = (ReimTypeVO) delObjs[i];
//				delVO.setStatus(VOStatus.DELETED);
//				list.add(delVO);
//			}
//		}
//
//		Object[] updObjs = batchVO.getUpdObjs();
//		if (updObjs != null) {
//			for (int i = 0; i < updObjs.length; i++) {
//				ReimTypeVO updVO = (ReimTypeVO) updObjs[i];
//				updVO.setStatus(VOStatus.UPDATED);
//				list.add(updVO);
//			}
//		}
//
//		service.saveBillWithRealDelete(list.toArray(new ReimTypeVO[0]));
//
//		return new BatchOperateVO();
		return batchSave(batchVO, ReimTypeVO.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<ReimRuleVO> queryReimRule(String billtype, String pk_org) throws BusinessException {
		try {
			StringBuffer buf = new StringBuffer();
			SQLParameter params = new SQLParameter();
			if (!StringUtil.isEmpty(billtype)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_billtype = ? ");
				params.addParam(billtype);
			}
			if (!StringUtil.isEmpty(pk_org)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_org = ? ");
				params.addParam(pk_org);
			}
			return (List<ReimRuleVO>) new BaseDAO().retrieveByClause(ReimRuleVO.class, buf.toString(), params);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}
	
	public List<ReimRuleVO> saveReimRule(String billtype, String pk_org, ReimRuleVO[] reimRuleVOs) throws BusinessException {
		try {
			if (billtype == null || billtype.trim().length() == 0) {
				return new ArrayList<ReimRuleVO>();
			}
			BaseDAO baseDAO = new BaseDAO();
			baseDAO.deleteByClause(ReimRuleVO.class, "pk_billtype='" + billtype + "' and pk_org='" + pk_org + "'");
			baseDAO.insertVOArray(reimRuleVOs);
			return queryReimRule(billtype, pk_org);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

}
