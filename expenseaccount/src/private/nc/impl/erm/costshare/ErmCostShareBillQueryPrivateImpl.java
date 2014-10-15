package nc.impl.erm.costshare;

import java.util.Collection;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.erm.costshare.IErmCostShareBillQueryPrivate;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * 
 * @author lvhj
 *
 */
public class ErmCostShareBillQueryPrivateImpl implements
		IErmCostShareBillQueryPrivate {

	@SuppressWarnings("unchecked")
	public String[] queryCostSharePksByCond(String condition)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		Collection<CostShareVO> c= dao.retrieveByClause(CostShareVO.class, condition,new String[]{CostShareVO.PK_COSTSHARE});
		if(c != null && c.size() > 0){
			String[] pks = new String[c.size()];
			int i = 0;
			for (Object vo : c) {
				pks[i] = ((CostShareVO)vo).getPrimaryKey();
				i++;
			}
			return pks;
		}
		return null;
	}

	public List<JKBXHeaderVO> queryBXVOByCond(String condition) throws BusinessException {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(" where ");
		sbuf.append(condition);
		sbuf.append("and zb.");
		sbuf.append(BXHeaderVO.DJZT);
		sbuf.append("=");
		sbuf.append(BXStatusConst.DJZT_Sign);
		sbuf.append(" and zb.");
		sbuf.append(BXHeaderVO.DJLXBM);
		sbuf.append("<>");
		sbuf.append("2647");
		sbuf.append("  and not exists  (select * from ER_COSTSHARE er where er.src_id = zb.pk_jkbx)");
		List<JKBXHeaderVO> vos = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryHeadersByWhereSql(sbuf.toString(), BXConstans.BX_DJDL);
		return vos;
	}

}
