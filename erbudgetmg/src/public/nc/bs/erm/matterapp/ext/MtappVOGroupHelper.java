package nc.bs.erm.matterapp.ext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.matterapp.ext.MtappMonthExtVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * ���뵥vo���鹤����
 * 
 * @author lvhj
 *
 */
public class MtappVOGroupHelper {

	/**
	 * ���ڷ�̯��������ƾ֤ʹ�á�����������+�������ڣ������װ���뵥
	 * 
	 * @param vos
	 * @return
	 */
	public static Map<String, List<AggMatterAppVO>> groupPcorgVOs(AggMatterAppVO... vos) {
		return groupPcorgVOs(null,null,vos);
	}
	
	/**
	 * ���ڷ�̯��������ƾ֤ʹ�á�����������+�������ڣ������װ���뵥
	 * 
	 * @param pk_pcorg ָ��������������
	 * @param busidate ָ������ҵ������
	 * @param vos
	 * @return
	 */
	public static Map<String, List<AggMatterAppVO>> groupPcorgVOs(String pk_pcorg,UFDate busidate,AggMatterAppVO... vos) {
		Map<String, List<AggMatterAppVO>> res_map = new HashMap<String, List<AggMatterAppVO>>();
		for (AggMatterAppVO aggvo : vos) {
			MtappMonthExtVO[] monthvos = (MtappMonthExtVO[]) aggvo.getTableVO(MtappMonthExtVO.getDefaultTableName());
			if(monthvos == null || monthvos.length ==0){
				continue;
			}
			MtAppDetailVO[] childrenVO = aggvo.getChildrenVO();
			Map<String, MtAppDetailVO> detailvoMap = VOUtils.changeCollection2Map(Arrays.asList(childrenVO));
			// ��������-��������-��ϸ���б�
			Map<String, Map<UFDate, List<MtAppDetailVO>>> pcorg_date_detialmap = new HashMap<String, Map<UFDate,List<MtAppDetailVO>>>();
			for (int i = 0; i < monthvos.length; i++) {
				MtappMonthExtVO monthvo = monthvos[i];
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
				Map<UFDate, List<MtAppDetailVO>> date_detailmap = pcorg_date_detialmap.get(pcorg);
				if(date_detailmap == null){
					date_detailmap = new HashMap<UFDate, List<MtAppDetailVO>>();
					pcorg_date_detialmap.put(pcorg, date_detailmap);
				}
				List<MtAppDetailVO> detaillist = date_detailmap.get(billdate);
				if(detaillist == null){
					detaillist = new ArrayList<MtAppDetailVO>();
					date_detailmap.put(billdate, detaillist);
				}
				MtAppDetailVO detailvo = detailvoMap.get(monthvo.getPk_mtapp_detail());
				MtAppDetailVO v_detailvo = (MtAppDetailVO) detailvo.clone();
				detaillist.add(v_detailvo);
				// ��ϸ����Ϣ,����ֶα��
				v_detailvo.setPk_org(pcorg);//����֯���Ϊ��������
				v_detailvo.setBilldate(billdate);// �Ƶ����ڱ��Ϊ��������
				v_detailvo.setOrig_amount(monthvo.getOrig_amount());// ƾ֤�������Ϊ���ھ�̯���
				v_detailvo.setOrg_amount(monthvo.getOrg_amount());// ƾ֤�������Ϊ���ھ�̯���
				v_detailvo.setGroup_amount(monthvo.getGroup_amount());// ƾ֤�������Ϊ���ھ�̯���
				v_detailvo.setGlobal_amount(monthvo.getGlobal_amount());// ƾ֤�������Ϊ���ھ�̯���
				v_detailvo.setClose_status(ErmMatterAppConst.CLOSESTATUS_N);//���ùر�״̬Ϊδ�ر�
			}
			for (Entry<String, Map<UFDate, List<MtAppDetailVO>>> pc_date_entry : pcorg_date_detialmap.entrySet()) {
				String pcorg = pc_date_entry.getKey();// ��������
				Map<UFDate, List<MtAppDetailVO>> date_map = pc_date_entry.getValue();
				
				List<AggMatterAppVO> list = new ArrayList<AggMatterAppVO>();
				res_map.put(pcorg, list);
				
				for (Entry<UFDate, List<MtAppDetailVO>> date_entry : date_map.entrySet()) {
					UFDate billdate = date_entry.getKey();// ��������
					List<MtAppDetailVO> detaillist = date_entry.getValue();// ���ڷ�̯��ϸ��
					// ��װ����ƾ֤��vo
					AggMatterAppVO v_aggvo = genarateNewAggVO(pcorg, billdate,detaillist,aggvo,ErmMatterAppConst.CLOSESTATUS_N);
					list.add(v_aggvo);
				}
			}
		
		}
		return res_map;
	}


	/**
	 * ���ڷ�̯��������ƾ֤ʹ�á���������װ�ر����ڵ������뵥
	 * 
	 * @param vos
	 * @return
	 */
	public static List<AggMatterAppVO> getCloseVOs(AggMatterAppVO... vos) {
		return getCloseVOs(null,vos);
	}
	
	/**
	 * ���ڷ�̯��������ƾ֤ʹ�á���������װ�ر����ڵ������뵥
	 * 
	 * @param pk_pcorg ��������pk��Ϊ��ֵʱ�����������ķ����װ
	 * @param vos
	 * @return
	 */
	public static List<AggMatterAppVO> getCloseVOs(String pk_pcorg,AggMatterAppVO... vos) {
		List<AggMatterAppVO> res_list = new ArrayList<AggMatterAppVO>();
		for (AggMatterAppVO aggvo : vos) {
			MatterAppVO parentVO = aggvo.getParentVO();
			if(!isManualClose(parentVO)){
				// ���ֹ��ر������������
				continue;
			}
			UFDate closedate = parentVO.getClosedate();
			// ���������ķ�������뵥��ϸ
			Map<String, List<MtAppDetailVO>> detailmap = new HashMap<String, List<MtAppDetailVO>>();

			MtAppDetailVO[] childrenVO = aggvo.getChildrenVO();
			if(childrenVO != null && childrenVO.length >0){
				for (MtAppDetailVO detailvo : childrenVO) {
					String pcorg = detailvo.getPk_pcorg();// ��������
					if(StringUtil.isEmpty(pk_pcorg)||StringUtil.compare(pk_pcorg, pcorg) == 0){
						// ������ָ���������Ĳ�ѯ�����ߵ�ǰ������������ָ���������ģ��ɽ��а�װ
						if(detailvo.getRest_amount().compareTo(UFDouble.ZERO_DBL) <= 0){
							// ���С�ڵ���0�����������
							continue ;
						}
						List<MtAppDetailVO> list = detailmap.get(pcorg);
						if(list == null){
							list = new ArrayList<MtAppDetailVO>();
							detailmap.put(pcorg, list);
						}
						MtAppDetailVO v_detailvo = (MtAppDetailVO) detailvo.clone();
						list.add(v_detailvo);
						// �����ϸ�У�����ֶ���Ϣ
						v_detailvo.setPk_org(pcorg);//����֯����Ϊ��������
						v_detailvo.setBilldate(closedate);// ������������Ϊ�ر�����
						v_detailvo.setOrig_amount(v_detailvo.getRest_amount().multiply(-1));// ����ƾ֤�������Ϊ���ĸ���
						v_detailvo.setOrg_amount(v_detailvo.getOrg_rest_amount().multiply(-1));
						v_detailvo.setGroup_amount(v_detailvo.getGroup_rest_amount().multiply(-1));
						v_detailvo.setGlobal_amount(v_detailvo.getGlobal_rest_amount().multiply(-1));
					}
				}
			}
			
			for (Entry<String, List<MtAppDetailVO>> pc_detail : detailmap.entrySet()) {
				String pcorg = pc_detail.getKey();
				List<MtAppDetailVO> detaillist = pc_detail.getValue();

				// ��װ����ƾ֤��vo
				AggMatterAppVO v_aggvo = genarateNewAggVO(pcorg, closedate,detaillist,aggvo,ErmMatterAppConst.CLOSESTATUS_Y);
				
				res_list.add(v_aggvo);
			}
		}
		return res_list;
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
	private static AggMatterAppVO genarateNewAggVO(String pcorg,
			UFDate billdate, List<MtAppDetailVO> detaillist,AggMatterAppVO aggvo,Integer close_status) {
		// clone�ۺ�vo
		AggMatterAppVO v_aggvo = new AggMatterAppVO();
		MatterAppVO v_parentVO = (MatterAppVO) aggvo.getParentVO().clone();
		v_aggvo.setParentVO(v_parentVO);
		// ����������ϸ����Ϣ
		v_aggvo.setChildrenVO(detaillist.toArray(new MtAppDetailVO[0]));
		// �������ñ�ͷ����ֶ���Ϣ
//		v_parentVO.setPk_org(pcorg);//����֯����Ϊ��������
		v_parentVO.setBilldate(billdate);//������������Ϊ�ر�����
		v_parentVO.setClose_status(close_status);
//		UFDouble[] amounts = computeTotolAmount(detaillist);// ���ñ�ͷ���
//		v_parentVO.setOrig_amount(amounts[0]);
//		v_parentVO.setOrg_amount(amounts[1]);
//		v_parentVO.setGroup_amount(amounts[2]);
//		v_parentVO.setGlobal_amount(amounts[3]);
		v_parentVO.setPrimaryKey(v_aggvo.getVoucherKey());// ��������ƾ֤������
		return v_aggvo;
	}

//	/**
//	 * ������ϸ�кϼƽ��
//	 * 
//	 * @param detaillist
//	 * @return
//	 */
//	private static UFDouble[] computeTotolAmount(List<MtAppDetailVO> detaillist){
//		UFDouble[] amounts = new UFDouble[4];
//		for (MtAppDetailVO v_detailvo : detaillist) {
//			// �����������ķ���ĺϼ�ֵ
//			amounts[0] = UFDoubleTool.sum(amounts[0], v_detailvo.getOrig_amount());
//			amounts[1] = UFDoubleTool.sum(amounts[1], v_detailvo.getOrg_amount());
//			amounts[2] = UFDoubleTool.sum(amounts[2], v_detailvo.getGroup_amount());
//			amounts[3] = UFDoubleTool.sum(amounts[3], v_detailvo.getGlobal_amount());
//		}
//		return amounts;
//	}
	/**
	 * �Ƿ��ֹ��ر�
	 * 
	 * @param parentVO
	 * @return
	 */
	private static boolean isManualClose(MatterAppVO parentVO){
		return parentVO.getClose_status() == ErmMatterAppConst.CLOSESTATUS_Y &&
		parentVO.getRest_amount().compareTo(UFDouble.ZERO_DBL)>0;
	}
	
}
