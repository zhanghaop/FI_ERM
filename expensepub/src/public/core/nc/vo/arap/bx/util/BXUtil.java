package nc.vo.arap.bx.util;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.itf.uap.sf.ICreateCorpQueryService;
import nc.itf.uap.sf.IProductVersionQueryService;
import nc.vo.arap.workflow.config.ConfigAgent;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.ReimRuleDef;
import nc.vo.ep.bx.ReimRuleDefVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.sm.install.ProductVersionVO;

public class BXUtil {

	/**
	 * 调用启用模块API(非预算调用)
	 * @param pk_group 集团
	 * @param funcode 功能节点 数据来源于dap_dapsystem
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isProductInstalled(String strCorpPK,String pro) {
		boolean value = false;
		
//		value = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).queryEnabledPeriod(strCorpPK, pro);
		try {
			value = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).isEnabled(strCorpPK, pro);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return value ;
	}
	/**
	 * 调用启用模块API(预算调用)
	 * @param pk_group 集团
	 * @param funcode 功能节点 数据来源于dap_dapsystem
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isProductTbbInstalled(String pro) {
		boolean value = false;
		
		try {
			ProductVersionVO[] ProductVersionVOs  = NCLocator.getInstance().lookup(IProductVersionQueryService.class).queryByProductCode(pro);
			if(ProductVersionVOs == null || ProductVersionVOs.length==0){
				value = false;
			} else {
				value = true;
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return value ;
	}
	
	
//	/**
//	 * 调用启用模块API
//	 * @param pk_group 集团
//	 * @param funcode 功能节点 数据来源于dap_dapsystem
//	 * @return
//	 * @throws BusinessException
//	 */
//	public static boolean isProductInstalled(String pk_group,String funcode) throws BusinessException{
//		try {
//			return InitGroupQuery.isEnabled(pk_group==null?InvocationInfoProxy.getInstance().getGroupId():pk_group, funcode);
//		} catch (BusinessException be) {
//			Logger.error(be.getMessage(), be);
//			return true;
//		}
//		
//	}
	public static String getBusiPk_corp(JKBXHeaderVO head,String key){
		String pk_corp = "";
		BusiTypeVO busTypeVO = getBusTypeVO(head.getDjlxbm());
		List<String> costentity_billitems = busTypeVO.getCostentity_billitems();
		List<String> payentity_billitems = busTypeVO.getPayentity_billitems();
		List<String> useentity_billitems = busTypeVO.getUseentity_billitems();
		if(costentity_billitems.contains(key)){
			pk_corp = head.getFydwbm();
		}else if(payentity_billitems.contains(key)){
			pk_corp = head.getPk_org();
		}else if(useentity_billitems.contains(key)){
			pk_corp = head.getDwbm();
		}else{
			pk_corp = head.getPk_group();//取得当前登录公司
		}
		return pk_corp;
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
			ConfigAgent config = (ConfigAgent) ConfigAgent.getInstance();
			
			BusiTypeVO vo=(BusiTypeVO) busiTypeVO.clone();
			vo.setId(djlxbm);
			config.getCache().putCommonVO(BusiTypeVO.key, vo);
		}
		
		return busiTypeVO;
	}
	
	public static ReimRuleDefVO getReimRuleDefvo(String djlxbm) {
		final ConfigAgent config = (ConfigAgent) ConfigAgent.getInstance();
		ReimRuleDefVO reimRuleDefVO = (ReimRuleDefVO) config.getCommonVO(ReimRuleDefVO.key,djlxbm);
		if (reimRuleDefVO == null) {
			//单个交易类型的读取不到则读全局的
			reimRuleDefVO = (ReimRuleDefVO) config.getCommonVO(ReimRuleDefVO.key, "");
			if(reimRuleDefVO==null){
				//全局的也没有配置，则返回空
				return null;
			}
			ReimRuleDefVO vo = (ReimRuleDefVO) reimRuleDefVO.clone();
			vo.setId(djlxbm);
			config.getCache().putCommonVO(ReimRuleDefVO.key, vo);
		}
		
		return reimRuleDefVO;
	}
	
	public static List<List<String>> getReimRuleFields(String djlxbm) {
		ReimRuleDefVO reimRuleDefvo = getReimRuleDefvo(djlxbm);
		List<List<String>> fields=new ArrayList<List<String>>();
		List<String> headFields=new ArrayList<String>();
		List<String> bodyFields=new ArrayList<String>();
		
		headFields.add(JKBXHeaderVO.JKBXR);
		headFields.add(JKBXHeaderVO.DEPTID);
		headFields.add(JKBXHeaderVO.BZBM);
		bodyFields.add(BXBusItemVO.PK_REIMTYPE);

		if(reimRuleDefvo!=null){
			List<ReimRuleDef> reimRuleDefList = reimRuleDefvo.getReimRuleDefList();
			if(reimRuleDefList!=null){
				for(ReimRuleDef reimrule:reimRuleDefList){
					String item=reimrule.getItemvalue();
						
					int i=item.indexOf(ReimRuleVO.REMRULE_SPLITER);
					if(i==-1)
						continue;
					
					if(item.startsWith(ReimRuleVO.Reim_body_key)){
						bodyFields.add(item.substring(i+1));
					}else if(item.startsWith(ReimRuleVO.Reim_head_key)){
						headFields.add(item.substring(i+1));
					}else{
						headFields.add(item.substring(0,i));
					}
				}
			}
		}
		
		fields.add(headFields);
		fields.add(bodyFields);
		return fields;
	}
}
