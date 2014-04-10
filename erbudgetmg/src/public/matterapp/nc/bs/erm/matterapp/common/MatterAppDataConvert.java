package nc.bs.erm.matterapp.common;


/**
 * 根据费用申请规则转换为其它单据的业务行
 *
 * @author luolch
 *
 */
public class MatterAppDataConvert {
	
//	/**
//	 * 根据VO对照将申请单转换为业务单据
//	 * @param des_billtype
//	 * @param pk_org
//	 * @param pk_group
//	 * @param retvo
//	 * @return
//	 * @throws BusinessException
//	 */
//	public static MatterAppConvResVO getConvertBusiVOs(String des_billtype,
//			String pk_org, String pk_group, AggMatterAppVO retvo)
//			throws BusinessException {
//		IPfExchangeService exchangeservice = NCLocator.getInstance().lookup(IPfExchangeService.class);
//		String ma_tradetype = retvo.getParentVO().getPk_tradetype();
//		AggregatedValueObject newbusivo = exchangeservice.runChangeData(ma_tradetype, des_billtype, retvo, null);
//		
//		// 查询申请单的控制维度
//		IErmMappCtrlFieldQuery ctrlfieldQryService = NCLocator.getInstance()
//				.lookup(IErmMappCtrlFieldQuery.class);
//		Map<String, List<String>> ctrlFieldMap = ctrlfieldQryService
//				.queryCtrlFields(pk_org, new String[] { ma_tradetype });
//		
//		MatterAppConvResVO res =new MatterAppConvResVO();
//		res.setBusiobj(newbusivo);
//		res.setMtCtrlBusiFieldMap(ctrlFieldMap);
//		
//		return res;
//	}

}