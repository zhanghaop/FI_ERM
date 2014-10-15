package nc.impl.erm.matterapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.matterapp.IErmMatterAppBillClose;
import nc.pubitf.erm.matterapp.IErmMatterAppBillCloseService;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;

public class ErmMatterAppBillCloseServiceImpl implements IErmMatterAppBillCloseService{

	@Override
	public void closeVOs(String[] mtapp_detail_pks)
			throws BusinessException {
		// ������ϸ��pk����ѯ��װ�ۺ�vo
		AggMatterAppVO[] aggvos = queryAggVObyDetailPk(mtapp_detail_pks);
		// �������뵥�رշ��񣬽��йر�
		IErmMatterAppBillClose service =  NCLocator.getInstance().lookup(IErmMatterAppBillClose.class);
		service.closeVOs(aggvos);
	}

	@Override
	public void openVOs(String[] mtapp_detail_pks)
			throws BusinessException {
		// ������ϸ��pk����ѯ��װ�ۺ�vo
		AggMatterAppVO[] aggvos = queryAggVObyDetailPk(mtapp_detail_pks);
		// �������뵥�رշ��񣬽���ȡ���ر�
		IErmMatterAppBillClose service =  NCLocator.getInstance().lookup(IErmMatterAppBillClose.class);
		service.openVOs(aggvos);
	}
	
	@SuppressWarnings("unchecked")
	private AggMatterAppVO[] queryAggVObyDetailPk(String[] mtapp_detail_pks)
			throws DAOException, BusinessException {
		List<AggMatterAppVO> agglist = new ArrayList<AggMatterAppVO>();

		if(mtapp_detail_pks == null || mtapp_detail_pks.length == 0){
			return null;
		}
		BaseDAO dao = new BaseDAO();
		// ��ѯ���뵥��ϸ��vos
		Collection<MtAppDetailVO> detail_res = dao.retrieveByClause(MtAppDetailVO.class,
				SqlUtils.getInStr(MtAppDetailVO.PK_MTAPP_DETAIL, mtapp_detail_pks, true));
		if(detail_res.isEmpty()){
			return null;
		}
		Map<String, List<MtAppDetailVO>> map = new HashMap<String, List<MtAppDetailVO>>();
		for ( MtAppDetailVO detailvo : detail_res) {
			String pk_mtapp_bill = detailvo.getPk_mtapp_bill();
			List<MtAppDetailVO> list = map.get(pk_mtapp_bill);
			if(list == null){
				list = new ArrayList<MtAppDetailVO>();
				map.put(pk_mtapp_bill, list);
			}
			list.add(detailvo);
		}
		// ��ѯ���뵥����
		Collection<MatterAppVO> app_res = dao.retrieveByClause(MatterAppVO.class,
				SqlUtils.getInStr(MatterAppVO.PK_MTAPP_BILL, map.keySet().toArray(new String[0]), true));
		// ��װ�ۺ�vo
		for (MatterAppVO vo : app_res) {
			AggMatterAppVO aggvo = new AggMatterAppVO();
			aggvo.setParentVO(vo);
			aggvo.setChildrenVO(map.get(vo.getPrimaryKey()).toArray(new MtAppDetailVO[0]));
			agglist.add(aggvo);
		}
		return agglist.toArray(new AggMatterAppVO[0]);
	}
}
