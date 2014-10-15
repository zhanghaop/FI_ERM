package nc.impl.erm.accruedexpense;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.pubitf.fip.external.IBillReflectorService;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.fip.external.FipExtendAggVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;

/**
 * Ԥ�ᵥ-���ƽ̨�������ʵ��
 * 
 * @author chenshuaia
 * 
 */
public class ErmAccruedBillReflectorServiceImpl implements IBillReflectorService {

	@Override
	public Collection<FipExtendAggVO> queryBillByRelations(Collection<FipRelationInfoVO> relationvos)
			throws BusinessException {
		if (null == relationvos || relationvos.size() == 0) {
			return null;
		}
		// Ԥ�ᵥpk,��ֹ�ظ�ʹ��set
		Set<String> billIdSet = new HashSet<String>();
		for (Iterator<FipRelationInfoVO> iter = relationvos.iterator(); iter.hasNext();) {
			String relationID = iter.next().getRelationID();
			billIdSet.add(relationID);
		}
		// ��ѯ������Ϣ��Ӧ��Ԥ�ᵥ
		IErmAccruedBillQuery qryservice = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class);
		AggAccruedBillVO[] aggvos = qryservice.queryBillByPks(billIdSet.toArray(new String[0]));
		
		// ��װ���ص���������
		List<FipExtendAggVO> result = new ArrayList<FipExtendAggVO>();
		for (AggAccruedBillVO accruedBillVo : aggvos) {
			// ��װ
			FipExtendAggVO tempvo = new FipExtendAggVO();
			tempvo.setBillVO(accruedBillVo);
			tempvo.setRelationID(accruedBillVo.getPrimaryKey());
			result.add(tempvo);
		}
		return result;
	}
}
