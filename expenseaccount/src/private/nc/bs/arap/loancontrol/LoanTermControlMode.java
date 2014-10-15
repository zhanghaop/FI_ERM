package nc.bs.arap.loancontrol;

import java.util.Date;

import nc.vo.ep.bx.LoanControlVO;
import nc.vo.er.pub.IFYControl;
import nc.vo.er.util.StringUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * @author twei
 *
 * nc.bs.arap.loancontrol.LoanTermControlMode
 * 
 * 按照最迟未还款日进行借款控制
 * 
 * @see SimpleSqlLoanControlMode
 * @see LoanControlMode
 */
public class LoanTermControlMode extends SimpleSqlLoanControlMode {

	@Override
	public boolean compare(Object dataValue, Object controlValue,Object itemValue) {
		
		if(dataValue==null)
			return true;
		
		UFDate data=new UFDate(dataValue.toString());
	
		if(new UFDate(new Date()).after(data)){
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

		String sql="select min(zb.zhrq) from "+tableName+" zb " +
				"where zb.sxbz<>0 and (zb.qzzt=0) and zb.dr=0 and "+djlxbmStr+
				" and zb."+controlattr+" = '"+vo.getItemValue(controlattr).toString()+"'"
//				"' and zb.pk_corp='"+vo.getPk_corp()+
				+(StringUtils.isNullWithTrim(defvo.getCurrency())?"":(" and zb.bzbm='"+defvo.getCurrency()+"' "));
		
		return sql;
	}

}
