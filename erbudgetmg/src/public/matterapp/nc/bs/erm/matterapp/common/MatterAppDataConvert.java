package nc.bs.erm.matterapp.common;


/**
 * ���ݷ����������ת��Ϊ�������ݵ�ҵ����
 *
 * @author luolch
 *
 */
public class MatterAppDataConvert {
	
//	/**
//	 * ����VO���ս����뵥ת��Ϊҵ�񵥾�
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
//		// ��ѯ���뵥�Ŀ���ά��
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