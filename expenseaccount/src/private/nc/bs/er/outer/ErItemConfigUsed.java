// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2008-12-2 14:07:42
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ErItemConfigUsed.java

package nc.bs.er.outer;

import java.util.ArrayList;
import java.util.List;
import nc.bs.dao.BaseDAO;
import nc.bs.er.processor.IsExitsRecordResultSetProcessor;
import nc.bs.fibill.outer.IFiBillFunctionForBudget;
import nc.bs.framework.common.NCLocator;
import nc.itf.er.prv.IArapBillTypePrivate;
import nc.vo.cmp.djlx.DjLXVO;
import nc.vo.cmp.func.QueryVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFBoolean;

public class ErItemConfigUsed
    implements IFiBillFunctionForBudget
{

    public ErItemConfigUsed()
    {
    }

    public UFBoolean isItemConfigUsed(String billtypecode, String pk_corp)
        throws BusinessException
    {
        String sql = "select  count(*) from er_jkzb zb inner join arap_item item on item.vouchid = zb.pk_item where zb.dr=0 and zb.pk_item is not null ";
        if(billtypecode != null)
            sql = (new StringBuilder()).append(sql).append("and item.djlxbm='").append(billtypecode).append("' ").toString();
        if(pk_corp != null)
            sql = (new StringBuilder()).append(sql).append("and  zb.dwbm='").append(pk_corp).append("' ").toString();
        BaseDAO dao = new BaseDAO();
        UFBoolean bflage = (UFBoolean)dao.executeQuery(sql, new IsExitsRecordResultSetProcessor());
        
        if(!bflage.booleanValue()){
        	sql=nc.vo.jcom.lang.StringUtil.replaceIgnoreCase(sql,"er_jkzb" , "er_bxzb");
        	bflage = (UFBoolean)dao.executeQuery(sql, new IsExitsRecordResultSetProcessor());
        }
        
        return bflage;
    }

    public List<DjLXVO> getBillSsCtlTypes(String pk_corp)
        throws BusinessException
    {
        String condition_kekong = (new StringBuilder()).append(" dwbm ='").append(pk_corp).append("' ").toString();
        IArapBillTypePrivate query = (IArapBillTypePrivate)NCLocator.getInstance().lookup(IArapBillTypePrivate.class);
        nc.vo.er.djlx.DjLXVO billTypesByWhere[] = query.getBillTypesByWhere(condition_kekong);
        List list = new ArrayList();
        if(billTypesByWhere != null)
        {
            nc.vo.er.djlx.DjLXVO arr$[] = billTypesByWhere;
            int len$ = arr$.length;
            for(int i$ = 0; i$ < len$; i$++)
            {
                nc.vo.er.djlx.DjLXVO vo = arr$[i$];
                DjLXVO newvo = new DjLXVO();
                newvo.setDjlxbm(vo.getDjlxbm());
//                newvo.setDjlxmc(vo.getDjlxmc());
                newvo.setDjdl(vo.getDjlxmc());
                newvo.setDjdl(vo.getDjdl());
                newvo.setDjlxoid(vo.getDjlxoid());
                list.add(newvo);
            }

        }
        return list;
    }

	public boolean[] canUpdateBudgetAndBalMaintParam(String pk_corp) throws BusinessException {
		String sql = null;
		String sqlBal = null;  //判断余额维护环节参数是否能够修改的sql
		if (pk_corp != null) {
			sql = "select count(zb.pk_jkbx) from er_jkzb zb where zb.pk_corp ='"+pk_corp+"' and zb.sxbz=0 and zb.djzt<>0 and zb.dr=0";
			sqlBal = "select count(zb.djdl) from er_jkzb zb where zb.pk_item is not null and zb.pk_corp = '"+pk_corp+"' and zb.sxbz=0 and zb.djzt<>0 and zb.dr=0 ";
		} else {
			throw new BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("arapinitdata","UPParapinitdata-000030")/*@res "参数pk_corp为空！"*/);
		}
		UFBoolean[] isExists = new UFBoolean[]{UFBoolean.FALSE, UFBoolean.FALSE};
		
		BaseDAO dao = new BaseDAO();
		isExists[0] = (UFBoolean)dao.executeQuery(sql, new IsExitsRecordResultSetProcessor());
		isExists[1] = (UFBoolean)dao.executeQuery(sqlBal, new IsExitsRecordResultSetProcessor());
        
        if(!isExists[0].booleanValue()){
        	sql=nc.vo.jcom.lang.StringUtil.replaceIgnoreCase(sql,"er_jkzb" , "er_bxzb");
        	isExists[0] = (UFBoolean)dao.executeQuery(sql, new IsExitsRecordResultSetProcessor());
        }
        
        if(!isExists[1].booleanValue()){
        	sqlBal=nc.vo.jcom.lang.StringUtil.replaceIgnoreCase(sqlBal,"er_jkzb" , "er_bxzb");
        	isExists[1] = (UFBoolean)dao.executeQuery(sqlBal, new IsExitsRecordResultSetProcessor());
        }
        
		return new boolean[]{!isExists[0].booleanValue(),!isExists[1].booleanValue()};
	}

	public ArrayList<CircularlyAccessibleValueObject[]> queryBudgetExecBillInfo(QueryVO[] qvos, ArrayList<String[]> selectFlds, String[] amountFldAlias) throws BusinessException {
		//FIXME 20110223注销
//		return new QueryFuncBO().queryFuncByQueryVO(qvos, selectFlds, amountFldAlias);
		return null;
	}

	public UFBoolean isItemUsed(String vouchid) throws BusinessException {
		String sql = "select  count(*) from er_jkzb zb where zb.dr=0 and zb.pk_item ='"+vouchid+"'";
	    BaseDAO dao = new BaseDAO();
        UFBoolean bflage = (UFBoolean)dao.executeQuery(sql, new IsExitsRecordResultSetProcessor());
        
        if(!bflage.booleanValue()){
        	sql=nc.vo.jcom.lang.StringUtil.replaceIgnoreCase(sql,"er_jkzb" , "er_bxzb");
        	bflage = (UFBoolean)dao.executeQuery(sql, new IsExitsRecordResultSetProcessor());
        }
        
        return bflage;
	}
}