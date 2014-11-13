package nc.bs.arap.loancontrol;

import nc.vo.ep.bx.LoanControlVO;
import nc.vo.er.pub.IFYControl;
import nc.vo.er.util.StringUtils;
import nc.vo.pub.BusinessException;

/**
 * @author twei
 *
 * nc.bs.arap.loancontrol.LoanNumsControlMode
 * 
 * 按照借款单数进行借款控制
 * 
 * @see SimpleSqlLoanControlMode
 * @see LoanControlMode
 */
public class LoanNumsControlMode extends SimpleSqlLoanControlMode {

	@Override
	public boolean compare(Object dataValue, Object controlValue,Object itemValue) {
		
		if (dataValue == null) {
			dataValue = Integer.valueOf(0);
		}
		
		Integer data=new Integer(dataValue.toString());
		Integer control=new Integer(controlValue.toString());
		
		data = Integer.valueOf(data.intValue() + 1);
		
		if(data.compareTo(control)>0){
			return false;
		}
		
		return true;
	}

	@Override
	public String getSql(LoanControlVO defvo,IFYControl[] vos) throws BusinessException {
		
		String djlxbmStr = getDjlxbmAndJsfsInStr(defvo,vos);
		
		IFYControl vo=vos[0];
		
		String tableName = getTableName(vo);
		
		String controlattr = defvo.getControlattr();

		String sql="select count(distinct zb.pk_jkbx) from "+tableName+" zb " +
				"where zb.djzt>="+vo.getDjzt()+" and (zb.qzzt=0) and zb.dr=0 and "+djlxbmStr+
				" and zb."+controlattr+" = '"+vo.getItemValue(controlattr).toString()+"'"
//				"' and zb.pk_corp='"+vo.getPk_corp()+
				+(StringUtils.isNullWithTrim(defvo.getCurrency())?"":(" and zb.bzbm='"+defvo.getCurrency()+"'"))+
				" and zb.pk_jkbx<>'"+vo.getPk()+"'";
		
		return sql;
	}

}
