package nc.bs.erm.util;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.workflow.config.ConfigAgent;
import nc.vo.ep.bx.BilltypeRuleVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

/**
 * 单据类型工具类
 * 
 * @author lvhj
 *
 */
public class ErmBillTypeUtil {

	private ErmBillTypeUtil(){
		
	}
	
	public static BusiTypeVO getBusTypeVO(String djlxbm) {
		DjLXVO djlxvo = null;
		try {
			djlxvo = NCLocator.getInstance().lookup(IArapBillTypePublic.class).getDjlxvoByDjlxbm(djlxbm, "0001");
		} catch (BusinessException e) {
			nc.bs.logging.Log.getInstance("ermExceptionLog").error(e);
		}
		if(djlxvo==null)
			return null;
		return getBusTypeVO(djlxbm, djlxvo.getDjdl());
	}
	
	/**
	 * @return 取业务类型VO (busitype.xml定义内容)
	 * @see BusiTypeVO
	 */
	public static BusiTypeVO getBusTypeVO(String djlxbm, String djdl) {
		BusiTypeVO busiTypeVO = null;
		busiTypeVO = (BusiTypeVO) ((ConfigAgent)ConfigAgent.getInstance()).getCommonVO(BusiTypeVO.key,djlxbm);
		
		if(busiTypeVO==null){
			busiTypeVO = (BusiTypeVO) ConfigAgent.getInstance().getCommonVO(BusiTypeVO.key,djdl.equals(BXConstans.BX_DJDL)?BXConstans.BX_DJLXBM:BXConstans.JK_DJLXBM);
			
			BusiTypeVO vo=(BusiTypeVO) busiTypeVO.clone();
			vo.setId(djlxbm);
			ConfigAgent.getCache().putCommonVO(BusiTypeVO.key, vo);
		}
		
		return busiTypeVO;
	}
	
	/**
	 * 获得单据类型过滤vo
	 * 
	 * @param billtype
	 * @return
	 */
	public static BilltypeRuleVO getBilltypeRuleVO(String billtype) {
		BilltypeRuleVO busiTypeVO = (BilltypeRuleVO) ((ConfigAgent) ConfigAgent
				.getInstance()).getCommonVO(BilltypeRuleVO.key, billtype);
		return busiTypeVO;
	}
	
	/**
	 * 获得单据类型过滤场景下的，过滤sql
	 * 
	 * @param rulevo
	 * @param iscludeDjlx
	 * @return
	 */
	public static String getBilltypeRuleWhereSql(BilltypeRuleVO rulevo,boolean iscludeDjlx) {
		if(rulevo == null ){
			return "parentbilltype in ('')";
		}
		List<String> parentitems = rulevo.getParentitems();
		List<String> excludeitems = rulevo.getExcludeitems();
		List<String> items = rulevo.getItems();
		String insql = parentitems == null||parentitems.isEmpty()?"parentbilltype in ('')":"parentbilltype in ("+StringUtil.getUnionStr(parentitems.toArray(new String[0]), ",", "'")+")";
		//需要包含单据类型时，要特殊处理
		if(iscludeDjlx){
			String tempSql=parentitems == null||parentitems.isEmpty()?"pk_billtypecode in ('')":"pk_billtypecode in ("+StringUtil.getUnionStr(parentitems.toArray(new String[0]), ",", "'")+")";
			insql="(" + insql + "or " + tempSql + ")";
		}
		if(items != null&&!items.isEmpty()){
			insql +=" and pk_billtypecode in ("+StringUtil.getUnionStr(items.toArray(new String[0]), ",", "'")+")";
		}
		if(excludeitems != null&&!excludeitems.isEmpty()){
			insql +=" and pk_billtypecode not in ("+StringUtil.getUnionStr(excludeitems.toArray(new String[0]), ",", "'")+")";
		}
		return insql;
	}

	
}
