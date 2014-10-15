package nc.bs.erm.matterapp.check;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.erm.mactrlschema.IErmMappCtrlBillQuery;
import nc.itf.uap.pf.IPFConfig;
import nc.md.data.access.NCObject;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.mactrlschema.MtappCtrlfieldVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pf.change.ChangeVOAdjustContext;
import nc.vo.pf.change.ExchangeRuleVO;
import nc.vo.pf.change.IChangeVOAdjust;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;

/**
 * 拉申请单交换校验类
 * 
 * 1\控制维度必须有目标字段映射
 * 2\控制维度为表体字段，且映射到目标表头字段时，申请单表体该字段的值必须一致
 * 
 * @author lvhj
 * 
 */
public class MaChangeVOAdjust implements IChangeVOAdjust {

	@Override
	public AggregatedValueObject adjustBeforeChange(
			AggregatedValueObject srcVO, ChangeVOAdjustContext adjustContext)
			throws BusinessException {
		this.batchAdjustBeforeChange(new AggregatedValueObject[] { srcVO },
				adjustContext);
		return srcVO;
	}

	@Override
	public AggregatedValueObject adjustAfterChange(AggregatedValueObject srcVO,
			AggregatedValueObject destVO, ChangeVOAdjustContext adjustContext)
			throws BusinessException {
		// TODO Auto-generated method stub
		return destVO;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AggregatedValueObject[] batchAdjustBeforeChange(
			AggregatedValueObject[] srcVOs, ChangeVOAdjustContext adjustContext)
			throws BusinessException {
		if (srcVOs == null || srcVOs.length == 0) {
			return srcVOs;
		}
		// 对应申请单控制规则的校验。只处理拉一张申请单转换情况
		AggMatterAppVO aggvo = (AggMatterAppVO) srcVOs[0];

		NCObject mtNcobj = NCObject.newInstance(aggvo);

		MatterAppVO parentVO = (MatterAppVO) aggvo.getParentVO();
		String pk_org = parentVO.getPk_org();
		String pk_tradetype = parentVO.getPk_tradetype();
		boolean ismashare = parentVO.getIscostshare() == null?false:parentVO.getIscostshare().booleanValue();
		// 判断目的单据是否是报销单
		BilltypeVO desttypevo = PfDataCache.getBillType(adjustContext.getDestBilltype());
		boolean is_destbx = BXConstans.BX_DJLXBM.equals(desttypevo.getPk_billtypecode())||BXConstans.BX_DJLXBM.equals(desttypevo.getParentbilltype());

		
//		if(ismashare && !is_destbx){
//			// 借款单拉分摊情况的申请单时，不进行校验控制维度
//			return srcVOs;
//		}
		
		if(!is_destbx){
			// 借款单拉申请单时时，不进行校验控制维度
			return srcVOs;
		}
		
		
		
		// 查询申请单的控制维度
//		IErmMappCtrlFieldQuery ctrlfieldQryService = NCLocator.getInstance().lookup(IErmMappCtrlFieldQuery.class);
//		Map<String, List<MtappCtrlfieldVO>> ctrlFieldVoMap = ctrlfieldQryService.queryFieldVOs(pk_org,
//				new String[] { pk_tradetype });
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		List<String[]> paramList = new ArrayList<String[]>();
		paramList.add(new String[]{pk_org, pk_tradetype});
		@SuppressWarnings("rawtypes")
        Map[] ctrlmap = NCLocator.getInstance().lookup(IErmMappCtrlBillQuery.class).queryCtrlShema(paramList, pk_group);
		List<MtappCtrlfieldVO> ctrlFieldVOList = (List<MtappCtrlfieldVO>) ctrlmap[1].get(pk_org+pk_tradetype);
		
		
		Map<String, MtappCtrlfieldVO> code2VoMap = getCode2VoMap(ctrlFieldVOList);

		// 控制维度在vo对照中配置检查
		if(code2VoMap!=null && !code2VoMap.isEmpty()){
			checkVOchange(adjustContext, mtNcobj, ismashare, is_destbx, code2VoMap);
		}


		return srcVOs;
	}

	protected void checkVOchange(ChangeVOAdjustContext adjustContext,
			NCObject mtNcobj, boolean ismashare, boolean is_destbx,
			Map<String, MtappCtrlfieldVO> code2VoMap) throws BusinessException {
		List<String> ctrlFieldList = new ArrayList<String>();
		ctrlFieldList.addAll(code2VoMap.keySet());
		// 从数据库查询交换规则
		List<ExchangeRuleVO> rulelist = findExchangeRule(adjustContext);
		if (rulelist == null || rulelist.isEmpty()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0",
					"0upp2012V575-0129")/* @res "费用申请单控制维度未配置vo对照，请检查！" */);
		}
		// 从vo对照中获得控制维度对应的目标 字段
		Map<String, List<String>> voKeyMap = new HashMap<String, List<String>>();
		for (ExchangeRuleVO ruleVo : rulelist) {
			String ruleData = ruleVo.getRuleData();
			if (ctrlFieldList.contains(ruleData)) {
				List<String> list = voKeyMap.get(ruleData);
				if(list == null){
					list = new ArrayList<String>();
					voKeyMap.put(ruleData, list);
				}
				list.add(ruleVo.getDest_attr());
			}
		}
		// 记录未做对照的字段
		List<String> notContainFieldList = new ArrayList<String>();
		// 控制维度校验
		for (String ctrlfield : ctrlFieldList) {
			List<String> list = voKeyMap.get(ctrlfield);
			if(list == null || list.isEmpty()){
				// 标记未做vo对照的控制维度
				notContainFieldList.add(ctrlfield);
				continue;
			}
			
			boolean isCtrlHead = false;// 是否控制表头字段
			String busifield = null;
			for (int i = 0; i < list.size(); i++) {
				String attr = list.get(i);
				String[] split = StringUtil.split(attr, ".");
				if(split.length ==1){
					isCtrlHead = true;
					busifield = attr;
				}else {
					if(ismashare){
						if(BXConstans.COSTSHAREDETAIL.equals(split[0])&& is_destbx){
							// 申请单分摊拉单到报销单时，按照分摊页签字段控制
							isCtrlHead = false;
							busifield = attr;
							break;
						}
					}else if(BXConstans.ER_BUSITEM.equals(split[0])||BXConstans.JK_BUSITEM.equals(split[0])){
						// 按业务行字段控制
						isCtrlHead = false;
						busifield = attr;
						break;
					}
					
				}
			}
			if(busifield == null){
				// 标记未做vo对照的控制维度
				notContainFieldList.add(ctrlfield);
			}
			// 校验来源字段多值对应目标字段1个值情况
			checkN2One(mtNcobj, ctrlfield, isCtrlHead);
		}

		
		// 控制维度必须有目标字段映射
		if (!notContainFieldList.isEmpty()) {
			List<String> fieldShowList = new ArrayList<String>();
			for(String field: notContainFieldList){
				fieldShowList.add(code2VoMap.get(field).getFieldname() + "[" + field + "]");
			}
			
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0",
					"0upp2012V575-0131")/* @res "费用申请单如下控制维度没有配置vo对照，请检查！" */ + "\r\n"
					+ StringUtil.getUnionStr(fieldShowList.toArray(new String[0]), ",", ""));
		}
	}

	protected void checkN2One(NCObject mtNcobj, String ctrlfield,
			boolean isCtrlHead) throws BusinessException {
		if(ctrlfield.indexOf((int) '.') > 0&& isCtrlHead){
			// 控制维度为表体字段，且映射到目标表头字段时，申请单表体该字段的值必须一致
			Object[] values = (Object[]) mtNcobj
					.getAttributeValue(ctrlfield);
			if (values != null && values.length > 1) {
				Object value = values[0];
				for (int i = 1; i < values.length; i++) {
					if (value == null && values[i] != null) {
						handleSameValueException();
					} else if (value != null
							&& !value.equals(values[i])) {
						handleSameValueException();
					}
				}
			}
		}
	}

	private Map<String, MtappCtrlfieldVO> getCode2VoMap(List<MtappCtrlfieldVO> ctrlFieldList) {
		Map<String, MtappCtrlfieldVO> result = new HashMap<String, MtappCtrlfieldVO>();
		if (ctrlFieldList != null) {
			for (MtappCtrlfieldVO fieldVo : ctrlFieldList) {
				result.put(fieldVo.getFieldcode(), fieldVo);
			}
		}
		return result;
	}

	/**
	 * 控制维度为表体字段，且映射到目标表头字段时，申请单表体该字段的值必须一致
	 * 
	 * @throws BusinessException
	 */
	private void handleSameValueException() throws BusinessException {
		throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0058")/*@res "费用申请单表体一个字段的多个值不能传到下游单据表头的一个字段，请修改单据转换规则"*/);
	}

	/**
	 * 查询获得vo对照规则
	 * 
	 * @param context
	 * @return
	 */
	private List<ExchangeRuleVO> findExchangeRule(
			ChangeVOAdjustContext context) {
		String srcBilltypeOrTrantype = context.getSrcBilltype();
		String destBilltypeOrTrantype = context.getDestBilltype();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		@SuppressWarnings("unchecked")
		List<ExchangeRuleVO> exchangeRuleVO = (List<ExchangeRuleVO>) NCLocator
				.getInstance()
				.lookup(IPFConfig.class)
				.getMappingRelation(srcBilltypeOrTrantype,
						destBilltypeOrTrantype, null, pk_group);
		return exchangeRuleVO;
	}

	@Override
	public AggregatedValueObject[] batchAdjustAfterChange(
			AggregatedValueObject[] srcVOs, AggregatedValueObject[] destVOs,
			ChangeVOAdjustContext adjustContext) throws BusinessException {
		// TODO Auto-generated method stub
		return destVOs;
	}

}
