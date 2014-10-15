package nc.impl.erm.jkbx;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.pubitf.erm.jkbx.IJkbxBillQueryService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

public class JkbxBillQueryServiceImpl implements IJkbxBillQueryService {

	@Override
	public Map<String, Map<String, String>> queryJKBills()
			throws BusinessException {
		// FIXME ����ѯ���ֶΣ���������д������������ȷ���������ٸ��촦��
		String[] queryFields = new String[]{JKBXHeaderVO.DJRQ,JKBXHeaderVO.DJBH,JKBXHeaderVO.ZY,JKBXHeaderVO.YBJE};
		// FIXME �����ڣ���Ԥ��һ�������Ĳ�ѯ������ʹ��
		String sqlwhere = "";
		IBXBillPrivate queryservice = NCLocator.getInstance().lookup(IBXBillPrivate.class);
		List<JKBXHeaderVO> list = queryservice.queryHeadersByWhereSql(sqlwhere, BXConstans.JK_DJDL);
		
		return getBillFiledValueMap(queryFields, list);
	}

	@Override
	public Map<String, Map<String, String>> queryJKBusItems(String headpk)
			throws BusinessException {
		return queryBusitems(headpk);
	}

	@Override
	public Map<String, Map<String, String>> queryBXBills()
			throws BusinessException {
		// FIXME ����ѯ���ֶΣ���������д������������ȷ���������ٸ��촦��
		String[] queryFields = new String[]{JKBXHeaderVO.DJRQ,JKBXHeaderVO.DJBH,JKBXHeaderVO.ZY,JKBXHeaderVO.YBJE};
		// FIXME �����ڣ���Ԥ��һ�������Ĳ�ѯ������ʹ��
		String sqlwhere = "";
		IBXBillPrivate queryservice = NCLocator.getInstance().lookup(IBXBillPrivate.class);
		List<JKBXHeaderVO> list = queryservice.queryHeadersByWhereSql(sqlwhere, BXConstans.BX_DJDL);
		
		return getBillFiledValueMap(queryFields, list);
	}

	@Override
	public Map<String, Map<String, String>> queryBXBusItems(String headpk)
			throws BusinessException {
		return queryBusitems(headpk);
	}

	/**
	 * ��ѯҵ������Ϣ
	 * 
	 * @param headpk
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, Map<String, String>> queryBusitems(String headpk)
			throws BusinessException {
		// FIXME ����ѯ���ֶΣ���������д������������ȷ���������ٸ��촦��
		String[] queryFields = new String[]{BXBusItemVO.SZXMMC,BXBusItemVO.YBJE};
		IBXBillPrivate queryservice = NCLocator.getInstance().lookup(IBXBillPrivate.class);
		BXBusItemVO[] vos = queryservice.queryItemsByPks(new String[]{headpk});
		if(vos == null || vos.length == 0){
			return new HashMap<String,Map<String,String>>();
		}
		return getBillFiledValueMap(queryFields,Arrays.asList(vos));
	}
	/**
	 * ת��supervo����Ϊ��vo����ֶ�����ֵmap
	 * 
	 * @param queryFields
	 * @param list
	 * @return
	 */
	private Map<String, Map<String, String>> getBillFiledValueMap(
			String[] queryFields, List<? extends SuperVO> list) {
		Map<String,Map<String,String>> resultmap = new HashMap<String,Map<String,String>>();
		if(list != null && !list.isEmpty()){
			for (SuperVO vo : list) {
				Map<String,String> fieldvalueMap = new HashMap<String,String>();
				for (int i = 0; i < queryFields.length; i++) {
					String attributeValue = vo.getAttributeValue(queryFields[i]) == null?""
							:vo.getAttributeValue(queryFields[i]).toString();
					fieldvalueMap.put(queryFields[i], attributeValue);
				}
				resultmap.put(vo.getPrimaryKey(), fieldvalueMap);
			}
		}
		return resultmap;
	}

}
