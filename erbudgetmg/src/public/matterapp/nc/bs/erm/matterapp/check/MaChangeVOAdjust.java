package nc.bs.erm.matterapp.check;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.mactrlschema.IErmMappCtrlFieldQuery;
import nc.itf.uap.pf.IPFConfig;
import nc.md.data.access.NCObject;
import nc.vo.erm.mactrlschema.MtappCtrlfieldVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pf.change.ChangeVOAdjustContext;
import nc.vo.pf.change.ExchangeRuleVO;
import nc.vo.pf.change.IChangeVOAdjust;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

/**
 * �����뵥����У����
 * 
 * 1\����ά�ȱ�����Ŀ���ֶ�ӳ��
 * 2\����ά��ֻ��ӳ�䵽Ŀ�굥�ݵ�һ���ֶ���
 * 3\����ά��Ϊ�����ֶΣ���ӳ�䵽Ŀ���ͷ�ֶ�ʱ�����뵥������ֶε�ֵ����һ��
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

	@Override
	public AggregatedValueObject[] batchAdjustBeforeChange(
			AggregatedValueObject[] srcVOs, ChangeVOAdjustContext adjustContext)
			throws BusinessException {
		if (srcVOs == null || srcVOs.length == 0) {
			return srcVOs;
		}
		// ��Ӧ���뵥���ƹ����У�顣ֻ������һ�����뵥ת�����
		AggMatterAppVO aggvo = (AggMatterAppVO) srcVOs[0];

		NCObject mtNcobj = NCObject.newInstance(aggvo);

		MatterAppVO parentVO = (MatterAppVO) aggvo.getParentVO();
		String pk_org = parentVO.getPk_org();
		String pk_tradetype = parentVO.getPk_tradetype();

		// ��ѯ���뵥�Ŀ���ά��
		IErmMappCtrlFieldQuery ctrlfieldQryService = NCLocator.getInstance()
				.lookup(IErmMappCtrlFieldQuery.class);
		
		Map<String, List<MtappCtrlfieldVO>> ctrlFieldVoMap = ctrlfieldQryService.queryFieldVOs(pk_org,
				new String[] { pk_tradetype });
		
		List<MtappCtrlfieldVO> ctrlFieldVOList = ctrlFieldVoMap.get(pk_tradetype);
		Map<String, MtappCtrlfieldVO> code2VoMap = getCode2VoMap(ctrlFieldVOList);

		if (ctrlFieldVoMap != null && !ctrlFieldVoMap.isEmpty()) {
			List<String> ctrlFieldList = new ArrayList<String>();
			ctrlFieldList.addAll(code2VoMap.keySet());
			// �����ݿ��ѯ��������
			List<ExchangeRuleVO> rulelist = findExchangeRule(adjustContext);
			if (rulelist == null || rulelist.isEmpty()) {
				throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0",
						"0upp2012V575-0129")/* @res "�������뵥����ά��δ����vo���գ����飡" */);
			}
			// ��¼δ�����յ��ֶ�
			List<String> notContainFieldList = new ArrayList<String>();
			notContainFieldList.addAll(ctrlFieldList);

			Map<String, String> rulemap = new HashMap<String, String>();
			for (ExchangeRuleVO rulevo : rulelist) {
				String ruleData = rulevo.getRuleData();
				if (ctrlFieldList.contains(ruleData)) {
					if (rulemap.containsKey(ruleData)) {
						// ����ά��ֻ��ӳ�䵽Ŀ�굥�ݵ�һ���ֶ���
						throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0",
						"0upp2012V575-0130")/* @res "�������뵥����ά�Ȳ���ͬʱӳ�䵽Ŀ�굥�ݵĶ���ֶ��ϣ�����!*/);
					}
					if (ruleData.indexOf((int) '.') > 0
							&& rulevo.getDest_attr().indexOf((int) '.') == -1) {
						// ����ά��Ϊ�����ֶΣ���ӳ�䵽Ŀ���ͷ�ֶ�ʱ�����뵥������ֶε�ֵ����һ��
						Object[] values = (Object[]) mtNcobj
								.getAttributeValue(ruleData);
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
					notContainFieldList.remove(ruleData);
				}
			}
			// ����ά�ȱ�����Ŀ���ֶ�ӳ��
			if (!notContainFieldList.isEmpty()) {
				List<String> fieldShowList = new ArrayList<String>();
				for(String field: notContainFieldList){
					fieldShowList.add(code2VoMap.get(field).getFieldname() + "[" + field + "]");
				}
				
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0",
						"0upp2012V575-0131")/* @res "�������뵥���¿���ά��û������vo���գ����飡" */ + "\r\n"
						+ StringUtil.getUnionStr(fieldShowList.toArray(new String[0]), ",", ""));
			}

		}

		return srcVOs;
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
	 * ����ά��Ϊ�����ֶΣ���ӳ�䵽Ŀ���ͷ�ֶ�ʱ�����뵥������ֶε�ֵ����һ��
	 * 
	 * @throws BusinessException
	 */
	private void handleSameValueException() throws BusinessException {
		throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0058")/*@res "�������뵥����һ���ֶεĶ��ֵ���ܴ������ε��ݱ�ͷ��һ���ֶΣ����޸ĵ���ת������"*/);
	}

	/**
	 * ��ѯ���vo���չ���
	 * 
	 * @param context
	 * @return
	 */
	private ArrayList<ExchangeRuleVO> findExchangeRule(
			ChangeVOAdjustContext context) {
		String srcBilltypeOrTrantype = context.getSrcBilltype();
		String destBilltypeOrTrantype = context.getDestBilltype();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		@SuppressWarnings("unchecked")
		ArrayList<ExchangeRuleVO> exchangeRuleVO = (ArrayList<ExchangeRuleVO>) NCLocator
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
