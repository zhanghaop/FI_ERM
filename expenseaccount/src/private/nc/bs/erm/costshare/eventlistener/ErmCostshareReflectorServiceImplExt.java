package nc.bs.erm.costshare.eventlistener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.erm.ext.common.CostshareVOGroupHelper;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.pubitf.fip.external.IBillReflectorService;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.fip.external.FipExtendAggVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * ���ý�ת�����ƽ̨����ʵ���࣬�������ڷ�̯����
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 * 
 */
public class ErmCostshareReflectorServiceImplExt implements IBillReflectorService {

	@Override
	public Collection<FipExtendAggVO> queryBillByRelations(
			Collection<FipRelationInfoVO> relationvos) throws BusinessException {
		if (null == relationvos || relationvos.size() == 0) {
			return null;
		}
		List<String> pklist = new ArrayList<String>();
		
		// ����pk��������pk
		Map<String, List<String>> RelationIdMap = new HashMap<String, List<String>>();
		for (Iterator<FipRelationInfoVO> iter = relationvos.iterator(); iter.hasNext();) {
			String relationID = iter.next().getRelationID();
			String pk_costshare = relationID;
			if(relationID.length()>20){
				// ���ڷ�̯ƾ֤����relateid��ʽΪ����pk+��������pk+���ڣ�yy-mm-dd��
				pk_costshare = relationID.substring(0, 20);
				List<String> list = RelationIdMap.get(pk_costshare);
				if(list == null){
					list = new ArrayList<String>();
					RelationIdMap.put(pk_costshare, list);
				}
				list.add(relationID);
			}
			pklist.add(pk_costshare);
		}
		// ����pks��ѯ��ת��
		ArrayList<FipExtendAggVO> rs = null;
		AggCostShareVO[] vos = NCLocator.getInstance()
				.lookup(IErmCostShareBillQuery.class)
				.queryBillByPKs(pklist.toArray(new String[pklist.size()]));
		// ��װ��������
		List<AggCostShareVO> resBillList = new ArrayList<AggCostShareVO>();
		for (int j = 0; j < vos.length; j++) {
			AggCostShareVO aggvo = vos[j];
			String pk = aggvo.getParentVO().getPrimaryKey();
			if(RelationIdMap.containsKey(pk)){
				// ���ڷ����װ����
				List<String> relationidlist = RelationIdMap.get(pk);
				for (String relationID : relationidlist) {
					String pk_pcorg = relationID.substring(20, 40);
					// ���ڷ�̯ƾ֤����
					UFDate billdate = new UFDate(relationID.substring(40));
					Map<String, List<AggCostShareVO>> groupPcorgMap = CostshareVOGroupHelper.groupPcorgVOs(pk_pcorg, billdate, aggvo);
					resBillList.addAll(groupPcorgMap.get(pk_pcorg));
				}
			}else{
				// ֱ�ӷ��ؽ�ת��
				resBillList.add(aggvo);
			}
		}
		
		if (!resBillList.isEmpty()) {
			rs = new ArrayList<FipExtendAggVO>();
			for (AggCostShareVO vo : resBillList) {
				FipExtendAggVO tempvo = new FipExtendAggVO();
				tempvo.setBillVO(vo);
				tempvo.setRelationID(vo.getParentVO().getPrimaryKey());
				rs.add(tempvo);
			}
		}
		return rs;
	}

}
