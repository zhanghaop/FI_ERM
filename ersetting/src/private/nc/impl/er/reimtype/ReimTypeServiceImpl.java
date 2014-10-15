package nc.impl.er.reimtype;


import java.util.ArrayList;
import java.util.List;

import nc.bs.bd.baseservice.BaseService;
import nc.bs.dao.BaseDAO;
import nc.itf.er.reimtype.IReimTypeService;
import nc.jdbc.framework.SQLParameter;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.er.reimrule.ReimRulerVO;
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

	/**
	 * @author shiwla
	 */
	
	@SuppressWarnings("unchecked")
	public List<ReimRulerVO> queryReimRuler(String billtype, String pk_group,String pk_org) throws BusinessException {
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
			if (!StringUtil.isEmpty(pk_group)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_group = ? ");
				params.addParam(pk_group);
			}
			if (!StringUtil.isEmpty(pk_org)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_org = ? ");
				params.addParam(pk_org);
			}
			return (List<ReimRulerVO>) new BaseDAO().retrieveByClause(ReimRulerVO.class, buf.toString(), params);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ReimRulerVO> queryGroupOrgReimRuler(String billtype, String pk_group,String pk_org) throws BusinessException {
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
			if (!StringUtil.isEmpty(pk_group)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_group = ? ");
				params.addParam(pk_group);
			}
			if (!StringUtil.isEmpty(pk_org)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_org in (?,?) ");
				params.addParam(ReimRulerVO.PKORG);
				params.addParam(pk_org);
			}
			return (List<ReimRulerVO>) new BaseDAO().retrieveByClause(ReimRulerVO.class, buf.toString(), params);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ReimRuleDimVO> queryReimDim(String billtype, String pk_group,String pk_org) throws BusinessException {
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
			if (!StringUtil.isEmpty(pk_group)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_group = ? ");
				params.addParam(pk_group);
			}
			if (!StringUtil.isEmpty(pk_org)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_org = ? ");
				params.addParam(pk_org);
			}
			return (List<ReimRuleDimVO>) new BaseDAO().retrieveByClause(ReimRuleDimVO.class, buf.toString(), params);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}
	@SuppressWarnings("unchecked")
	public List<ReimRuleDimVO> queryGroupOrgReimDim(String billtype, String pk_group,String pk_org) throws BusinessException {
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
			if (!StringUtil.isEmpty(pk_group)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_group = ? ");
				params.addParam(pk_group);
			}
			if (!StringUtil.isEmpty(pk_org)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_org in (?,?) ");
				params.addParam(ReimRulerVO.PKORG);
				params.addParam(pk_org);
			}
			return (List<ReimRuleDimVO>) new BaseDAO().retrieveByClause(ReimRuleDimVO.class, buf.toString(), params);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}
	public List<ReimRulerVO> saveReimRule(String billtype, String pk_group,String pk_org, ReimRulerVO[] reimRuleVOs) throws BusinessException {
		try {
			if (billtype == null || billtype.trim().length() == 0) {
				return new ArrayList<ReimRulerVO>();
			}
			BaseDAO baseDAO = new BaseDAO();
			StringBuffer buf = new StringBuffer();
			SQLParameter params = new SQLParameter();
			if (!StringUtil.isEmpty(billtype)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_billtype = ? ");
				params.addParam(billtype);
			}
			if (!StringUtil.isEmpty(pk_group)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_group = ? ");
				params.addParam(pk_group);
			}
			if (!StringUtil.isEmpty(pk_org)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_org = ? ");
				params.addParam(pk_org);
			}
			baseDAO.deleteByClause(ReimRulerVO.class, buf.toString(), params);
			baseDAO.insertVOArray(reimRuleVOs);
			return queryReimRuler(billtype, pk_group,pk_org);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public List<ReimRulerVO> saveControlItem(String centControl,String billtype,String pk_group,String pk_org, ReimRulerVO[] reimRuleVOs) throws BusinessException {
		try {
			if (billtype == null || billtype.trim().length() == 0 
				|| centControl == null || centControl.trim().length() == 0) {
				return new ArrayList<ReimRulerVO>();
			}
			BaseDAO baseDAO = new BaseDAO();
			StringBuffer buf = new StringBuffer();
			if (!StringUtil.isEmpty(billtype)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_billtype ='"+billtype+"'");
			}
			if (!StringUtil.isEmpty(pk_group)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_group ='"+pk_group+"'");
			}
			if (!StringUtil.isEmpty(pk_org)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_org ='"+pk_org+"'");
			}
			if (buf.length() > 0) {
				buf.append(" and ");
			}
			buf.append(centControl+" ='");
			for(ReimRulerVO vo: reimRuleVOs){
				StringBuffer set = new StringBuffer();
				if (set.length() > 0) {
					set.append(" , ");
				}
				if(!StringUtil.isEmpty(vo.getShowitem())){
					set.append(" showitem='"+ vo.getShowitem() +"',showitem_name='" + vo.getShowitem_name()+"'");
				}
				else{
					set.append(" showitem=null ,showitem_name=null");
				}
				if (set.length() > 0) {
					set.append(" , ");
				}
				if(!StringUtil.isEmpty(vo.getControlitem())){
					set.append(" controlitem='"+ vo.getControlitem() +"',controlitem_name='" + vo.getControlitem_name()+"'");
				}
				else{
					set.append(" controlitem=null ,controlitem_name=null");
				}
				if (set.length() > 0) {
					set.append(" , ");
				}
				if(vo.getControlflag() != null){
					set.append(" controlflag='"+ vo.getControlflag() +"'");
				}
				else{
					set.append(" controlflag=null");
				}
				if (set.length() > 0) {
					set.append(" , ");
				}
				if(!StringUtil.isEmpty(vo.getControlformula())){
					set.append(" controlformula='"+ vo.getControlformula() +"'");
				}
				else{
					set.append(" controlformula=null");
				}
				String sql = "update er_reimruler set"+set+" where " + buf + vo.getAttributeValue(centControl)+"'";
				baseDAO.executeUpdate(sql);
			}
			return queryReimRuler(billtype, pk_group,pk_org);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}
	public List<ReimRuleDimVO> saveReimDim(String billtype, String pk_group,String pk_org, ReimRuleDimVO[] reimDimVOs) throws BusinessException {
		try {
			if (billtype == null || billtype.trim().length() == 0) {
				return new ArrayList<ReimRuleDimVO>();
			}
			BaseDAO baseDAO = new BaseDAO();
			StringBuffer buf = new StringBuffer();
			SQLParameter params = new SQLParameter();
			if (!StringUtil.isEmpty(billtype)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_billtype = ? ");
				params.addParam(billtype);
			}
			if (!StringUtil.isEmpty(pk_group)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_group = ? ");
				params.addParam(pk_group);
			}
			if (!StringUtil.isEmpty(pk_org)) {
				if (buf.length() > 0) {
					buf.append(" and ");
				}
				buf.append(" pk_org = ? ");
				params.addParam(pk_org);
			}
			baseDAO.deleteByClause(ReimRuleDimVO.class, buf.toString(), params);
			baseDAO.insertVOArray(reimDimVOs);
			return queryReimDim(billtype, pk_group,pk_org);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}
	
}
