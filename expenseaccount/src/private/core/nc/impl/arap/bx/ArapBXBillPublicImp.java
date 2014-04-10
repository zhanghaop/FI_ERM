package nc.impl.arap.bx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.arap.bx.BXZbBO;
import nc.bs.dao.BaseDAO;
import nc.bs.er.util.SqlUtil;
import nc.itf.arap.pub.IBXBillPublic;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.ArrayUtils;

/**
 * nc.impl.arap.bx.ArapBXBillPublicImp
 * 
 * @author twei
 * 
 *         借款报销类单据公共业务处理接口实现
 */
public class ArapBXBillPublicImp implements IBXBillPublic {
	private BXZbBO bxZbBO = new BXZbBO();

	public MessageVO[] deleteBills(JKBXVO[] bxvos) throws BusinessException {

		return bxZbBO.delete(bxvos);
	}

	public JKBXVO[] save(JKBXVO[] vos) throws BusinessException {
		return bxZbBO.save(vos);
	}

	public JKBXVO[] tempSave(JKBXVO[] vos) throws BusinessException {
		return bxZbBO.tempSave(vos);
	}

	public JKBXVO[] update(JKBXVO[] vos) throws BusinessException {

		return bxZbBO.update(vos);
	}

	public void updateQzzt(JKBXVO[] vos) throws BusinessException {
		bxZbBO.updateQzzt(vos);
	}

	public JKBXHeaderVO updateHeader(JKBXHeaderVO header, String[] fields) throws BusinessException {
		return bxZbBO.updateHeader(header, fields);
	}

	public void updateDataAfterImport(String[] pk_tradetype) throws BusinessException {
	}

	@Override
	public JKBXVO[] queryBxBill4ProjBudget(String pk_group, String pk_project, String[] djlxbms) throws BusinessException {
		return queryBxBill4ProjBudget2(pk_group, pk_project, !ArrayUtils.isEmpty(djlxbms) ? null : djlxbms, false);
	}

	@Override
	public JKBXVO[] queryBxApproveBill4ProjBudget(String pk_group, String pk_Project, String[] djlxbms) throws BusinessException {
		return queryBxBill4ProjBudget2(pk_group, pk_Project, !ArrayUtils.isEmpty(djlxbms) ? null : djlxbms, true);
	}

	@SuppressWarnings("unchecked")
	private JKBXVO[] queryBxBill4ProjBudget2(String pk_group, String pk_project, String[] djlxbms, boolean isSign) throws BusinessException {
		if (StringUtil.isEmpty(pk_project) || StringUtil.isEmpty(pk_group)) {
			return null;
		}
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("select distinct zb.pk_jkbx from er_bxzb zb,er_busitem bs where zb.PK_JKBX = bs.PK_JKBX and zb.dr = 0 and bs.dr = 0 ");

		// 集团
		sqlBuf.append(" and zb.pk_group='" + pk_group + "'");
		// 项目
		sqlBuf.append(" and bs.jobid ='" + pk_project + "'");

		if (isSign) {
			sqlBuf.append(" and zb.djzt >= " + BXStatusConst.DJZT_Verified);
		} else {
			sqlBuf.append(" and zb.djzt >= " + BXStatusConst.DJZT_Saved);
		}

		if (!ArrayUtils.isEmpty(djlxbms)) {
			sqlBuf.append(" and ").append(SqlUtil.buildInSql("zb.djlxbm", djlxbms));
		}

		final List<String> pks = new ArrayList<String>();

		new BaseDAO().executeQuery(sqlBuf.toString(), new ResultSetProcessor() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while (rs.next()) {
					pks.add(rs.getString(JKBXHeaderVO.PK_JKBX));
				}
				return pks;
			}
		});

		if (pks.size() == 0) {
			return null;
		}

		Collection<BXHeaderVO> headers = new BaseDAO().retrieveByClause(BXHeaderVO.class, SqlUtil.buildInSql(JKBXHeaderVO.PK_JKBX, pks));
		
		//排序好，便于信息处理
		Collection<BXBusItemVO> busitems = new BaseDAO().retrieveByClause(BXBusItemVO.class, SqlUtil.buildInSql(JKBXHeaderVO.PK_JKBX, pks) 
					+ " and  dr = 0 and jobid ='" + pk_project + "'" + " order by "  + JKBXHeaderVO.PK_JKBX);
		
		List<JKBXVO> result = new ArrayList<JKBXVO>();
		for (JKBXHeaderVO headVO : headers) {
			BXVO vo = new BXVO();
			vo.setParentVO(headVO);
			// 业务行
			BXBusItemVO[] busItemVOs = getBxBusitems(headVO, busitems);
			vo.setChildrenVO(busItemVOs);
			result.add(vo);
		}
		return result.toArray(new JKBXVO[] {});
	}

	private BXBusItemVO[] getBxBusitems(JKBXHeaderVO headVO, Collection<BXBusItemVO> busitems) {
		if(headVO != null){
			String pk_jkbx = headVO.getPk_jkbx();
			List<BXBusItemVO> resultList = new ArrayList<BXBusItemVO>();
			
			BXBusItemVO[] busItems = busitems.toArray(new BXBusItemVO[]{});
			for (BXBusItemVO bxBusItemVO: busItems) {
				if(bxBusItemVO.getPk_jkbx().equals(pk_jkbx)){
					resultList.add(bxBusItemVO);
					busitems.remove(bxBusItemVO);
				}else if(resultList.size() > 0){
					break;
				}
			}
			
			return resultList.toArray(new BXBusItemVO[]{});
		}
		return null;
	}

	@Override
	public Map<String, UFDouble> queryAmount4ProjFinal(String pkGroup, String pkProject) throws BusinessException {
		if (StringUtil.isEmpty(pkGroup) || StringUtil.isEmpty(pkProject)) {
			return null;
		}
		final Map<String, UFDouble> result = new HashMap<String, UFDouble>();

		StringBuffer buf = new StringBuffer();
		buf.append(" select zb.bzbm, sum(bs.ybje) ybje from er_bxzb zb, er_busitem bs ");
		buf.append(" where zb.dr=0 ");
		buf.append(" and zb.pk_jkbx = bs.pk_jkbx ");// 单据状态签字
		buf.append(" and zb.djzt=" + BXStatusConst.DJZT_Sign);// 单据状态签字
		buf.append(" and zb.sxbz=" + BXStatusConst.SXBZ_VALID);// 生效标识为生效
		buf.append(" and zb.pk_group='" + pkGroup + "'");// 集团
		buf.append(" and bs.jobid ='" + pkProject + "'");// 项目
		buf.append(" group by zb.bzbm ");

		new BaseDAO().executeQuery(buf.toString(), new ResultSetProcessor() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while (rs.next()) {
					String bzbm = rs.getString(JKBXHeaderVO.BZBM);
					UFDouble amount = new UFDouble(rs.getBigDecimal(JKBXHeaderVO.YBJE));
					result.put(bzbm, amount);
				}
				return result;
			}
		});
		return result;
	}
}
