package nc.bs.erm.ext.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.ext.CShareMonthVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDate;

/**
 * �����̵渶��ת��vo���鹤����
 * 
 * @author lvhj
 *
 */
public class CostshareVOGroupHelper {

	/**
	 * ���ڷ�̯��������ƾ֤ʹ�á�����������+�������ڣ������װ��̯��¼
	 * 
	 * @param vos
	 * @return
	 */
	public static Map<String, List<AggCostShareVO>> groupPcorgVOs(AggCostShareVO... vos) {
		return groupPcorgVOs(null, null, vos);
	}
	
	/**
	 * ���ڷ�̯��������ƾ֤ʹ�á�����������+�������ڣ������װ��̯��¼
	 * 
	 * @param vos
	 * @return
	 */
	public static Map<String, List<AggCostShareVO>> groupPcorgVOs(String pk_pcorg,UFDate busidate,AggCostShareVO... vos) {
		Map<String, List<AggCostShareVO>> res_map = new HashMap<String, List<AggCostShareVO>>();
		for (AggCostShareVO aggvo : vos) {
			CShareMonthVO[] monthvos = (CShareMonthVO[]) aggvo.getTableVO(CShareMonthVO.getDefaultTableName());
			if(monthvos == null || monthvos.length ==0){
				continue;
			}
			CircularlyAccessibleValueObject[] childrenVO = aggvo.getChildrenVO();
			Map<String, CircularlyAccessibleValueObject> detailvoMap = VOUtils.changeCollection2Map(Arrays.asList(childrenVO));
			// ��������-��������-��ϸ���б�
			Map<String, Map<UFDate, List<CShareDetailVO>>> pcorg_date_detialmap = new HashMap<String, Map<UFDate,List<CShareDetailVO>>>();
			for (int i = 0; i < monthvos.length; i++) {
				CShareMonthVO monthvo = monthvos[i];
				String pcorg = monthvo.getPk_pcorg();
				if(!(StringUtil.isEmpty(pk_pcorg)||StringUtil.compare(pk_pcorg, pcorg) == 0)){
					// ��ָ���������ģ�����ָ������������ƥ�䵱ǰ���������ģ��Ž��а�װ
					continue;
				}
				UFDate billdate = monthvo.getBilldate();
				if(!(busidate==null||busidate.equals(billdate))){
					// ��ָ��ҵ�����ڣ�����ָ��ҵ��������ƥ�䵱ǰ��ҵ�����ڣ��Ž��а�װ
					continue;
				}
				
				// pcorg_date_detialmapά��
				Map<UFDate, List<CShareDetailVO>> date_detailmap = pcorg_date_detialmap.get(pcorg);
				if(date_detailmap == null){
					date_detailmap = new HashMap<UFDate, List<CShareDetailVO>>();
					pcorg_date_detialmap.put(pcorg, date_detailmap);
				}
				List<CShareDetailVO> detaillist = date_detailmap.get(billdate);
				if(detaillist == null){
					detaillist = new ArrayList<CShareDetailVO>();
					date_detailmap.put(billdate, detaillist);
				}
				CShareDetailVO detailvo = (CShareDetailVO) detailvoMap.get(monthvo.getPk_cshare_detail());
				CShareDetailVO v_detailvo = (CShareDetailVO) detailvo.clone();
				detaillist.add(v_detailvo);
				// ��ϸ����Ϣ,����ֶα��
				v_detailvo.setPk_org(pcorg);//����֯���Ϊ��������
				v_detailvo.setAssume_amount(monthvo.getOrig_amount());// ƾ֤�������Ϊ���ھ�̯���
				v_detailvo.setBbje(monthvo.getOrg_amount());// ƾ֤�������Ϊ���ھ�̯���
				v_detailvo.setGroupbbje(monthvo.getGroup_amount());// ƾ֤�������Ϊ���ھ�̯���
				v_detailvo.setGlobalbbje(monthvo.getGlobal_amount());// ƾ֤�������Ϊ���ھ�̯���
			}
			for (Entry<String, Map<UFDate, List<CShareDetailVO>>> pc_date_entry : pcorg_date_detialmap.entrySet()) {
				String pcorg = pc_date_entry.getKey();// ��������
				Map<UFDate, List<CShareDetailVO>> date_map = pc_date_entry.getValue();
				
				List<AggCostShareVO> list = new ArrayList<AggCostShareVO>();
				res_map.put(pcorg, list);
				
				for (Entry<UFDate, List<CShareDetailVO>> date_entry : date_map.entrySet()) {
					UFDate billdate = date_entry.getKey();// ��������
					List<CShareDetailVO> detaillist = date_entry.getValue();// ���ڷ�̯��ϸ��
					// ��װ����ƾ֤��vo
					AggCostShareVO v_aggvo = genarateNewAggVO(pcorg, billdate,detaillist,aggvo);
					list.add(v_aggvo);
				}
			}
		
		}
		return res_map;
	}



	/**
	 * ��װ����ƾ֤��vo
	 * 
	 * @param pcorg
	 * @param billdate
	 * @param detaillist
	 * @param aggvo
	 * @param close_status �ر�״̬
	 * @return
	 */
	private static AggCostShareVO genarateNewAggVO(String pcorg,
			UFDate billdate, List<CShareDetailVO> detaillist,AggCostShareVO aggvo) {
		// clone�ۺ�vo
		AggCostShareVO v_aggvo = new AggCostShareVO();
		CostShareVO v_parentVO = (CostShareVO) aggvo.getParentVO().clone();
		v_aggvo.setParentVO(v_parentVO);
		// ����������ϸ����Ϣ
		v_aggvo.setChildrenVO(detaillist.toArray(new CShareDetailVO[0]));
		// �������ñ�ͷ����ֶ���Ϣ
//		v_parentVO.setPk_org(pcorg);//����֯����Ϊ��������
		v_parentVO.setBilldate(billdate);//����������������
		// FIXME ʹ�ü��������������滻�����Զ������Ӧ��
		v_parentVO.setDefitem3("RA00");//����Ŀ�굥��Ϊ����ƾ֤
//		UFDouble[] amounts = computeTotolAmount(detaillist);// ���ñ�ͷ���
//		v_parentVO.setTotal(amounts[0]);
//		v_parentVO.setYbje(amounts[0]);
//		v_parentVO.setBbje(amounts[1]);
//		v_parentVO.setGroupbbje(amounts[2]);
//		v_parentVO.setGlobalbbje(amounts[3]);
		v_parentVO.setPrimaryKey(v_aggvo.getVoucherKey());// ��������ƾ֤������
		return v_aggvo;
	}

//	/**
//	 * ������ϸ�кϼƽ��
//	 * 
//	 * @param detaillist
//	 * @return
//	 */
//	private static UFDouble[] computeTotolAmount(List<CShareDetailVO> detaillist){
//		UFDouble[] amounts = new UFDouble[4];
//		for (CShareDetailVO v_detailvo : detaillist) {
//			// �����������ķ���ĺϼ�ֵ
//			amounts[0] = UFDoubleTool.sum(amounts[0], v_detailvo.getAssume_amount());
//			amounts[1] = UFDoubleTool.sum(amounts[1], v_detailvo.getBbje());
//			amounts[2] = UFDoubleTool.sum(amounts[2], v_detailvo.getGroupbbje());
//			amounts[3] = UFDoubleTool.sum(amounts[3], v_detailvo.getGlobalbbje());
//		}
//		return amounts;
//	}

	
}
