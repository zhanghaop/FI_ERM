package nc.bs.erm.buget;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.er.ntbcontrol.ErmNtbSqlFactory;
import nc.bs.logging.Log;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.erm.ntb.IBugetLinkBO;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.bd.accessor.GeneralAccessorFactory;
import nc.pubitf.bd.accessor.IGeneralAccessor;
import nc.vo.arap.bx.util.CurrencyControlBO;
import nc.vo.ep.bx.BxDetailLinkQueryVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.MultiLangText;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tb.control.IdBdcontrastVO;
import nc.vo.tb.obj.NtbParamVO;

public class BugetLinkBOImpl implements IBugetLinkBO {

	@SuppressWarnings("serial")
	private void query(String sql, final List<BxDetailLinkQueryVO> result, final CurrencyControlBO ccbo) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		dao.executeQuery(sql, new ResultSetProcessor() {
			// private static final long serialVersionUID = 739153572741107303L;
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while (rs.next()) {
					BxDetailLinkQueryVO vo = new BxDetailLinkQueryVO();
					vo.setPk_org(rs.getString(JKBXHeaderVO.PK_ORG));
					vo.setPk_group(rs.getString(JKBXHeaderVO.PK_GROUP));
					vo.setPk_currtype(rs.getString(JKBXHeaderVO.BZBM));
					vo.setDjrq(rs.getString(JKBXHeaderVO.DJRQ) == null ? null : new UFDate(rs.getString(JKBXHeaderVO.DJRQ)));
					vo.setShrq(rs.getString(JKBXHeaderVO.SHRQ) == null ? null : new UFDate(rs.getString(JKBXHeaderVO.SHRQ)));
					vo.setEffectdate(rs.getString(JKBXHeaderVO.JSRQ) == null ? null : new UFDate(rs.getString(JKBXHeaderVO.JSRQ)));
					vo.setZy(rs.getString(JKBXHeaderVO.ZY));
					vo.setPk_billtype(PfDataCache.getBillTypeInfo(rs.getString(JKBXHeaderVO.DJLXBM)).getBilltypenameOfCurrLang());
					vo.setDjbh(rs.getString(JKBXHeaderVO.DJBH));
					vo.setOri(rs.getBigDecimal(JKBXHeaderVO.YBJE) == null ? null : new UFDouble(rs.getBigDecimal(JKBXHeaderVO.YBJE)));
					vo.setLoc(rs.getBigDecimal(JKBXHeaderVO.BBJE) == null ? null : new UFDouble(rs.getBigDecimal(JKBXHeaderVO.BBJE)));
					vo.setGr_loc(rs.getBigDecimal(JKBXHeaderVO.GROUPBBJE) == null ? null : new UFDouble(rs.getBigDecimal(JKBXHeaderVO.GROUPBBJE)));
					vo.setGl_loc(rs.getBigDecimal(JKBXHeaderVO.GLOBALBBJE) == null ? null : new UFDouble(rs.getBigDecimal(JKBXHeaderVO.GLOBALBBJE)));

					vo.setPk_billtype(rs.getString("pk_billtype"));
					vo.setPk_jkbx(rs.getString(JKBXHeaderVO.PK_JKBX));
					ccbo.dealLinkQuerydigit(vo);
					result.add(vo);
				}
				return null;
			}
		});
	}

	public List<BxDetailLinkQueryVO> getLinkDatas(NtbParamVO ntbParamVO) throws BusinessException {
		String[] sqls = null;
		try {
			sqls = ErmNtbSqlFactory.getInstance(ntbParamVO).getDetailSqls();
		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e);
		}

		BaseDAO dao = new BaseDAO();
		final CurrencyControlBO ccbo = new CurrencyControlBO();
		final List<BxDetailLinkQueryVO> result = new ArrayList<BxDetailLinkQueryVO>();
		for (String sql : sqls) {
			query(sql, result, ccbo);
		}

		// 获取 档案的元数据
		@SuppressWarnings("unchecked")
		Collection<IdBdcontrastVO> vo = dao.retrieveByClause(IdBdcontrastVO.class, "BUSISYS_ID ='erm'");
		Map<String, String> map = new HashMap<String, String>();
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = vo.iterator(); iterator.hasNext();) {
			IdBdcontrastVO idBdcontrastVO = (IdBdcontrastVO) iterator.next();
			map.put(idBdcontrastVO.getBdinfo_type(), idBdcontrastVO.getPk_bdinfo());
		}

		// 处理 维度的多语
		int t = 0;
		for (String mdid : ntbParamVO.getTypeDim()) {
			IGeneralAccessor a = GeneralAccessorFactory.getAccessor(map.get(mdid));
			MultiLangText name = a.getDocByPk(ntbParamVO.getPkDim()[t]).getName();
			String value = name.toString();
			for (BxDetailLinkQueryVO qryvo : result) {
				qryvo.setAttributeValue(BxDetailLinkQueryVO.QRYOBJ_PREFIX + (t), value);
			}
			t++;
		}

		return result;
	}
}
