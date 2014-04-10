package nc.impl.arap.bx;

import java.util.Collection;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IBxUIControl;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKBusItemVO;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * @author twei
 *
 * nc.impl.arap.bx.ErUIControlImp
 */
public class ErUIControlImp implements IBxUIControl{

	/**
	 * @param head
	 * @param cxrq 冲销日期，在前台取登陆日期
	 * @param sampQuery
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXHeaderVO> getJKD(JKBXVO bxvo,UFDate cxrq,String sampQuery) throws BusinessException {
		
		BxcontrastVO[] contrastVO = bxvo.getContrastVO();
		if(bxvo.getParentVO().getJkbxr()==null && bxvo.getParentVO().getReceiver()==null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000179")/*@res "请录入报销人,再进行冲借款操作!"*/);
		}

		if(bxvo.getParentVO().getYbje()==null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000180")/*@res "请录入报销金额,再进行冲借款操作!"*/);
		}

		if(bxvo.getParentVO().getBzbm()==null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000390")/*@res "请录入报销币种,再进行冲借款操作!"*/);
		}

		String jkbxr=StringUtils.isNullWithTrim(bxvo.getParentVO().getReceiver())?bxvo.getParentVO().getJkbxr():bxvo.getParentVO().getReceiver();
		String pkbxd=bxvo.getParentVO().getPk_jkbx();
		String bzbm= bxvo.getParentVO().getBzbm();

		UFBoolean ischeck = bxvo.getParentVO().getIscheck()==null?UFBoolean.FALSE:bxvo.getParentVO().getIscheck();

		String sql = "" ;

		if(StringUtils.isNullWithTrim(sampQuery)){
			sql = "  where (zb.yjye>0 and zb.dr=0 and zb.djzt=3 and zb.ischeck='"+ischeck+"' and zb.bzbm='"+bzbm+"' and zb.jkbxr='"
			+ jkbxr + "' and zb.djrq<='"+cxrq+"') ";
			
			sql = dealCjkQueryCondition(bxvo, sql);
			
			if(!StringUtils.isNullWithTrim(pkbxd)&& contrastVO!=null && contrastVO.length!=0){
				/**
				 *如果报销单冲过借款保存过，要将冲过的借款单查询出来
				 */
				//sql+=" or  ( zb.pk_jkbx in (select pk_jkd from er_bxcontrast where pk_bxd='"+pkbxd+"' and (sxbz=0 or sxbz=2)))" ;
				sql+=" or  ( zb.pk_jkbx in (select er_bxcontrast.pk_jkd from er_bxcontrast join er_bxzb  on  er_bxcontrast.pk_bxd=er_bxzb.pk_jkbx where er_bxcontrast.pk_bxd='"+pkbxd+"' and (er_bxcontrast.sxbz=0 or er_bxcontrast.sxbz=2) and er_bxzb.bzbm='"+bzbm+"'))" ;
			}
		}else{
			sql = "  where zb.yjye>0 and zb.dr=0 and zb.djzt=3 and zb.djrq<='"+cxrq+"' and zb.ischeck='"+ischeck+"' and zb.bzbm='"+bzbm+"' and "+sampQuery ;
			
			sql = dealCjkQueryCondition(bxvo, sql);
			
			if(!StringUtils.isNullWithTrim(pkbxd)&& contrastVO!=null && contrastVO.length!=0){
				/**
				 *如果报销单冲过借款保存过，要将冲过的借款单查询出来
				 */
				//sql+=" or  ( zb.pk_jkbx in (select pk_jkd from er_bxcontrast where pk_bxd='"+pkbxd+"' and (sxbz=0 or sxbz=2) ))" ;
				sql+=" or  ( zb.pk_jkbx in (select er_bxcontrast.pk_jkd from er_bxcontrast join er_bxzb  on  er_bxcontrast.pk_bxd=er_bxzb.pk_jkbx where er_bxcontrast.pk_bxd='"+pkbxd+"' and (er_bxcontrast.sxbz=0 or er_bxcontrast.sxbz=2) and er_bxzb.bzbm='"+bzbm+"'))" ;

			}
		}
		
		if(!StringUtils.isNullWithTrim(sql)){
			
			sql+="order by zb.djrq desc";
		}
		
		IBXBillPrivate bxpri=NCLocator.getInstance().lookup(IBXBillPrivate.class);

		List<JKBXHeaderVO> bxvos = bxpri.queryHeadersByWhereSql(sql, BXConstans.JK_DJDL);

		for(JKBXHeaderVO vo:bxvos){
			vo.setYbye(vo.getYjye());
		}

		return bxvos;
	}
	/**
	 * 还款单不受限制
	 * @param bxvo
	 * @param sql
	 * @return
	 */
	private String dealCjkQueryCondition(JKBXVO bxvo, String sql) {
		if(!BXConstans.BILLTYPECODE_RETURNBILL.equals(bxvo.getParentVO().getDjlxbm())){
			/**
			 * 如果是拉单生成的报销单，这个报销单只能冲销由这个费用申请单生成的借款单
			 */
			boolean isMtApp=false;//是否拉单标记
			BXBusItemVO[] childrenVO = bxvo.getChildrenVO();
			String[] mtAppPks = VOUtils.getAttributeValues(childrenVO, BXBusItemVO.PK_ITEM);
			if(mtAppPks!=null){
				for (String mtAppPk : mtAppPks) {
					if(!StringUtils.isNullWithTrim(mtAppPk)){
						isMtApp=true;
						sql+=" and  ( zb.pk_jkbx in (select pk_jkbx from er_busitem where pk_item='"+mtAppPk+"'))" ;
						break;
					}
				}
			}
			if(!isMtApp){
				//自制的报销单不能冲拉了申请单的借款单，只能冲自制的借款单
				sql+=" and  ( zb.pk_jkbx in (select pk_jkbx from er_busitem where pk_item is null or pk_item='~'))" ;
			}
		}
		return sql;
	}
	
	/**
	 * 该方法仅供冲借款Dialog使用
	 */
	@SuppressWarnings("unchecked")
	@Override
	public BXBusItemVO[] queryByPk(String pkJk,String pkBx) throws BusinessException {
		StringBuffer sql=new StringBuffer();
		sql.append("( pk_jkbx='"+pkJk+"' and yjye>'"+new UFDouble(0)+"')");
        if (!StringUtils.isEmpty(pkBx)) {
        	sql.append(" or pk_busitem in (select pk_busitem from er_bxcontrast where pk_jkd='"+pkJk+"'");
            sql.append(" and pk_bxd='"+pkBx+"')");
        }    
        
        sql.append(" order by rowno ");
		Collection<BXBusItemVO> c =  MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(JKBusItemVO.class, sql.toString(), false);
		if(c == null || c.isEmpty()){
			return null;
		}
		BXBusItemVO[] result = c.toArray(new BXBusItemVO[]{});
		for(BXBusItemVO vo:result){
			vo.setYbye(vo.getYjye());
		}
		return result;
	}
	

}