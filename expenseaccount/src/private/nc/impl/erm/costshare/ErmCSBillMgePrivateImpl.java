package nc.impl.erm.costshare;

import java.sql.SQLException;

import nc.bs.dao.BaseDAO;
import nc.bs.er.util.SqlUtils;
import nc.bs.erm.costshare.ErmCostShareBO;
import nc.bs.logging.Logger;
import nc.itf.erm.costshare.IErmCostShareBillManagePrivate;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;

/**
 * 费用结转私有事务性服务
 * 
 * @author luolch
 * 
 */
public class ErmCSBillMgePrivateImpl implements  IErmCostShareBillManagePrivate {

	public AggCostShareVO tempSaveVO(AggCostShareVO vo)
			throws BusinessException {
		((CostShareVO)vo.getParentVO()).setBillstatus(BXStatusConst.DJZT_TempSaved);
		if (vo.getParentVO().getStatus() == VOStatus.NEW) {
			vo = new ErmCostShareBO().insertVO(vo);
		} else {
			vo = new ErmCostShareBO().updateVO(vo);
		}
		return vo;
	}


	public AggCostShareVO[] printNormal(String[] pks, String businDate,
			String pk_user) throws BusinessException {
		String businDatesql = businDate==null ? null : "'"+businDate.toString()+"'";
		String pk_usersql = pk_user==null ? null : "'"+pk_user+"'";
		StringBuffer sbu = new StringBuffer();
		sbu.append("update ER_COSTSHARE set printdate = ");
		sbu.append(businDatesql);
		sbu.append(",");
		sbu.append("printer=");
		sbu.append(pk_usersql);
		sbu.append(" where ");
		sbu.append(" printdate is ");
		if (pk_user==null) {
			sbu.append(" not ");
		}
		sbu.append(" null ");
		sbu.append(" and ");
		try {
			sbu.append(SqlUtils.getInStr("pk_costshare", pks, true));
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
		}
		new BaseDAO().executeUpdate(sbu.toString());
		return new ErmCostShareBillQueryImpl().queryBillByPKs(pks);
	}


	@Override
	public boolean queryFcbz(String group,String tradetype) throws BusinessException {
		String sql="select count(1) from er_djlx where pk_group='"+group+"' and djlxbm='"+tradetype+"' and fcbz = 'Y'";

		Object value = new BaseDAO().executeQuery(sql, new ColumnProcessor());
		if(value!=null){
			if(value instanceof Integer && value.equals(Integer.valueOf(1))){
					return true;
			}
		}
		return false;
	}
}
