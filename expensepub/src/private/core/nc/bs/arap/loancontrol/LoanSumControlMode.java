package nc.bs.arap.loancontrol;

import nc.vo.ep.bx.LoanControlVO;
import nc.vo.er.pub.IFYControl;
import nc.vo.er.util.StringUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

/**
 * @author twei
 *
 * nc.bs.arap.loancontrol.LoanSumControlMode
 * 
 * 按照借款单金额总数进行借款控制
 * 
 * @see SimpleSqlLoanControlMode
 * @see LoanControlMode
 */
public class LoanSumControlMode extends SimpleSqlLoanControlMode {

	@Override
	public boolean compare(Object dataValue, Object controlValue,Object itemValue) {

		if(dataValue==null)
			dataValue=new UFDouble(0);
		
		UFDouble data=new UFDouble(dataValue.toString());
		UFDouble control=new UFDouble(controlValue.toString());
		UFDouble item=new UFDouble(itemValue.toString());

		data=data.add(item);
		
		if(data.compareTo(control)>0){
			return false;
		}
		
		return true;
	}
	
	@Override
	protected Object getValue(Object value, Object itemValue) {
		
		if(value==null){
			return new UFDouble("0");
		}
		
		UFDouble data=new UFDouble(value.toString());
		UFDouble item=new UFDouble(itemValue.toString());

		return data.setScale(item.getPower(), 4);
	}

	@Override
	public String getSql(LoanControlVO defvo,IFYControl[] vos) throws BusinessException {
		
		String djlxbmStr = getDjlxbmAndJsfsInStr(defvo,vos);
		
		IFYControl vo=vos[0];

		String tableName = getTableName(vo);
		
		String controlattr = defvo.getControlattr();
		//2011年4月12日 更改
		String sql="select sum(zb."+getControlJeField(defvo,false)+") from "+tableName+" zb " +
//		String sql="select (case when sum(zb."+getControlJeField(defvo,false)+") is null then 0.0 else sum(zb."+getControlJeField(defvo,false)+") end ) sumybye from "+tableName+" zb " +
				"where zb.djzt>="+vo.getDjzt()+" and ( zb.qzzt=0) and zb.dr=0 and "+djlxbmStr+
//				"where zb.djzt>="+vo.getDjzt()+" and (zb.qzzt is null or zb.qzzt=0) and zb.dr=0 and "+djlxbmStr+
				" and zb."+controlattr+" = '"+vo.getItemValue(controlattr).toString()+"'"+
				(StringUtils.isNullWithTrim(defvo.getCurrency())?"":(" and zb.bzbm='"+defvo.getCurrency()+"'"))+
				" and zb.pk_jkbx<>'"+vo.getPk()+"'";
		
		return sql;
	}

	
}
