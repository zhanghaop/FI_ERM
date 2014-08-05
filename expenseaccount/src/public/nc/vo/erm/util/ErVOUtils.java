package nc.vo.erm.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

/**
 * @author twei
 * 
 * nc.vo.erm.util.ErVOUtils
 */
public class ErVOUtils {
	
	public static String getPsninfoByUser(String userid,String pk_org){
		try{
			String[] queryPsnidAndDeptid = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPsnidAndDeptid(userid, pk_org);
			if(!StringUtils.isNullWithTrim(queryPsnidAndDeptid[0]))
				return queryPsnidAndDeptid[0];
			else
				return null;
		}catch (Exception e) {
			ExceptionHandler.consume(e);
			return null;
		}
	}
	
	
	public static void clearContrastInfo(JKBXVO vo) {
		if (vo == null || vo.getParentVO() == null)
			return;
		
		vo.setContrastVO(null);
		
		BXBusItemVO[] childrenVO = vo.getChildrenVO();
		JKBXHeaderVO parentVO = vo.getParentVO();
		
		parentVO.setCjkybje(UFDouble.ZERO_DBL);
		parentVO.setCjkbbje(UFDouble.ZERO_DBL);
		
		parentVO.setHkybje(UFDouble.ZERO_DBL);
		parentVO.setHkbbje(UFDouble.ZERO_DBL);
		parentVO.setGlobalcjkbbje(UFDouble.ZERO_DBL);
		parentVO.setGroupcjkbbje(UFDouble.ZERO_DBL);
		parentVO.setGlobalhkbbje(UFDouble.ZERO_DBL);
		parentVO.setGrouphkbbje(UFDouble.ZERO_DBL);
		
		if(parentVO.isAdjustBxd()){
			// ���õ�������ʱ����ҵ�����Ҳ�����֧��
			parentVO.setZfybje(UFDouble.ZERO_DBL);
			parentVO.setZfbbje(UFDouble.ZERO_DBL);
			
			parentVO.setGlobalzfbbje(UFDouble.ZERO_DBL);
			parentVO.setGroupzfbbje(UFDouble.ZERO_DBL);
		}else{
			parentVO.setZfybje(parentVO.getYbje());
			parentVO.setZfbbje(parentVO.getBbje());
			
			parentVO.setGlobalzfbbje(parentVO.getGlobalbbje());
			parentVO.setGroupzfbbje(parentVO.getGroupbbje());
		}
		
		
		if (childrenVO == null || childrenVO.length == 0) {
			
		} else {
			for(BXBusItemVO item:childrenVO){
				item.setCjkybje(UFDouble.ZERO_DBL);
				item.setCjkbbje(UFDouble.ZERO_DBL);
				
				item.setHkybje(UFDouble.ZERO_DBL);
				item.setHkbbje(UFDouble.ZERO_DBL);
				
				item.setZfybje(item.getYbje());
				item.setZfbbje(item.getBbje());
				
				item.setGlobalcjkbbje(UFDouble.ZERO_DBL);
				item.setGroupcjkbbje(UFDouble.ZERO_DBL);
				item.setGlobalhkbbje(UFDouble.ZERO_DBL);
				item.setGrouphkbbje(UFDouble.ZERO_DBL);
				item.setGlobalzfbbje(item.getGlobalbbje());
				item.setGroupzfbbje(item.getGroupbbje());
				
			}
		}
	}
	
	public static void prepareBxvoHeaderToItem(JKBXVO vo) {
		if (vo == null || vo.getParentVO() == null)
			return;
		BXBusItemVO[] childrenVO = vo.getChildrenVO();
		JKBXHeaderVO parentVO = vo.getParentVO();
		if (childrenVO == null || childrenVO.length == 0) {
			BXBusItemVO item = new BXBusItemVO();
			String[] attributeNames = item.getAttributeNames();
			for (String attr : attributeNames) {
				item.setAttributeValue(attr, parentVO.getAttributeValue(attr));
			}
			item.setPrimaryKey(parentVO.getPrimaryKey()==null?BXConstans.TEMP_ZB_PK:parentVO.getPrimaryKey());
			
			//ҵ�����Ϊ�յĴ���,���������dr��ʶ�������ʱ�Ԥ�㣬���ƽ̨������ӿڵĴ���
			item.setDr(BXConstans.SPECIAL_DR);
			item.setTablecode(BXConstans.JK_DJDL.equals(parentVO.getDjdl())?BXConstans.BUS_PAGE_JK:BXConstans.BUS_PAGE);
			vo.setChildrenVO(new BXBusItemVO[] { item });
		} 
	}
	
	public static JKBXVO prepareBxvoHeaderToItemClone(JKBXVO vo) {
		if (vo == null || vo.getParentVO() == null)
			return vo;
		BXBusItemVO[] childrenVO = vo.getChildrenVO();
		JKBXHeaderVO parentVO = vo.getParentVO();
		if (childrenVO == null || childrenVO.length == 0) {
			JKBXVO voclone=(JKBXVO) vo.clone();
			BXBusItemVO item = new BXBusItemVO();
			String[] attributeNames = item.getAttributeNames();
			for (String attr : attributeNames) {
				item.setAttributeValue(attr, parentVO.getAttributeValue(attr));
			}
			item.setPrimaryKey(parentVO.getPrimaryKey());
			voclone.setChildrenVO(new BXBusItemVO[] { item });
			return voclone;
		} else {
		}
		return vo;
	}
	
	public static JKBXHeaderVO[] prepareBxvoItemToHeaderClone(JKBXVO vo) {
		if (vo == null || vo.getParentVO() == null)
			return new JKBXHeaderVO[]{};
		
		BXBusItemVO[] childrenVO = vo.getChildrenVO();
		JKBXHeaderVO parentVO = vo.getParentVO();
		if (childrenVO == null || childrenVO.length == 0) {
			return new JKBXHeaderVO[]{(JKBXHeaderVO)parentVO.clone()};
		} else {
			JKBXHeaderVO[] vos=new JKBXHeaderVO[childrenVO.length];
			for (int i = 0; i < vos.length; i++) {
				vos[i]=(JKBXHeaderVO) parentVO.clone();
				BXBusItemVO item = childrenVO[i];
				String[] attributeNames = item.getAttributeNames();
				for (String attr : attributeNames) {
					if(attr.equals("cashproj") || attr.equals("jkbxr") || attr.equals("cashitem")
							|| attr.equals(BXBusItemVO.DWBM) || attr.equals(BXBusItemVO.DEPTID)){
						continue;
					}
					if(item.getAttributeValue(attr) != null){
						vos[i].setAttributeValue(attr, item.getAttributeValue(attr));
					}
				}
				//�������� ������ arap_item_clb
				vos[i].setPk_jkbx(parentVO.getPk_jkbx());
				
				//�����¼�ӱ�ҵ����pk����д�������뵥ʹ��
				vos[i].setPk_mtapp_detail(item.getPk_mtapp_detail());
				vos[i].setPk_busitem(item.getPk_busitem());
				vos[i].setBx_busitemPK(item.getBx_busitemPK());
				vos[i].setJk_busitemPK(item.getJk_busitemPK());
				vos[i].setPreItemJe(vos[i].getItemJe());
				// JKheadvo�����cjkybje���ò��ϣ���Ҫ������������
				vos[i].setCjkybje(item.getCjkybje());
				vos[i].setCjkbbje(item.getCjkbbje());
				vos[i].setGroupcjkbbje(item.getGroupcjkbbje());
				vos[i].setGlobalcjkbbje(item.getGlobalcjkbbje());
				//��Ϊ��clone���������ﲻ�����û�ȥ
				if(vos[i].getShrq() == null){//��Ч����ȥ��Ч����
					vos[i].setShrq(parentVO.getJsrq() == null ? null : new UFDateTime(parentVO.getJsrq().getMillis()));
				}
			}
			return vos;
		}
	}

	/**
	 * ��������̯��ϸ��ת��Ϊheadvo
	 * 
	 * @param vo
	 * @return
	 */
	public static JKBXHeaderVO[] prepareCsharedetailToHeaderClone(JKBXVO vo) {
		if (vo == null || vo.getParentVO() == null)
			return new JKBXHeaderVO[]{};
		
		CShareDetailVO[] childrenVO = vo.getcShareDetailVo();
		JKBXHeaderVO parentVO = vo.getParentVO();
		if (childrenVO == null || childrenVO.length == 0) {
			return new JKBXHeaderVO[]{(JKBXHeaderVO)parentVO.clone()};
		} else {
			JKBXHeaderVO[] vos=new JKBXHeaderVO[childrenVO.length];
			for (int i = 0; i < vos.length; i++) {
				vos[i]=(JKBXHeaderVO) parentVO.clone();
				CShareDetailVO item = childrenVO[i];
				//�������� ������ arap_item_clb
				JKBXHeaderVO jkbxHeaderVO = vos[i];
				jkbxHeaderVO.setPk_jkbx(parentVO.getPk_jkbx());
				jkbxHeaderVO.setPk_busitem(item.getPrimaryKey());
				jkbxHeaderVO.setPk_mtapp_detail(item.getPk_mtapp_detail());
				jkbxHeaderVO.setPreItemJe(jkbxHeaderVO.getItemJe());
				// �̶��ֶζ���ת��
				jkbxHeaderVO.setPk_busitem(item.getPk_cshare_detail());
				jkbxHeaderVO.setPk_mtapp_detail(item.getPk_mtapp_detail());
				jkbxHeaderVO.setFydwbm(item.getAssume_org());
				jkbxHeaderVO.setFydeptid(item.getAssume_dept());
				jkbxHeaderVO.setPk_pcorg(item.getPk_pcorg());
				jkbxHeaderVO.setPk_checkele(item.getPk_checkele());
				jkbxHeaderVO.setPk_resacostcenter(item.getPk_resacostcenter());
				jkbxHeaderVO.setSzxmid(item.getPk_iobsclass());
				jkbxHeaderVO.setJobid(item.getJobid());
				jkbxHeaderVO.setProjecttask(item.getProjecttask());
				jkbxHeaderVO.setCustomer(item.getCustomer());
				jkbxHeaderVO.setHbbm(item.getHbbm());
				jkbxHeaderVO.setYbje(item.getAssume_amount());
				jkbxHeaderVO.setBbje(item.getBbje());
				jkbxHeaderVO.setGroupbbje(item.getGroupbbje());
				jkbxHeaderVO.setGlobalbbje(item.getGlobalbbje());
				
				// �Զ��������ת��
				for (int j = 0; j < 30; j++) {
					jkbxHeaderVO.setAttributeValue("zyx"+j, item.getAttributeValue("defitem"+j));
				}
				
				//��Ϊ��clone���������ﲻ�����û�ȥ
				if(jkbxHeaderVO.getShrq() == null){//��Ч����ȥ��Ч����
					jkbxHeaderVO.setShrq(parentVO.getJsrq() == null ? null : new UFDateTime(parentVO.getJsrq().getMillis()));
				}
			}
			return vos;
		}
	}

	/**
	 * @param contrasts
	 * @param jkdCollection
	 * @return ����Ԥ��س�������
	 */
	public static JKBXHeaderVO[] prepareItemToHeaderForJkContrast(Collection<BxcontrastVO> contrasts,
			Collection<JKBXHeaderVO> jkdCollection, Collection<BXBusItemVO> jkBusItemCollection) {
		List<JKBXHeaderVO> headers = new ArrayList<JKBXHeaderVO>();
		Map<String, SuperVO> jkdMap = VOUtils.changeCollectionToMap(jkdCollection, new String[] { JKBXHeaderVO.PK_JKBX });
		Map<String, SuperVO> busItemMap = VOUtils.changeCollectionToMap(jkBusItemCollection,
				new String[] { BXBusItemVO.PK_BUSITEM });
		
		for(BxcontrastVO vo:contrasts){
			JKBXHeaderVO head = (JKBXHeaderVO) jkdMap.get(vo.getPk_jkd()).clone();
			
			if((head.getQcbz()!=null && head.getQcbz().booleanValue()) || (head.getPk_item() != null && head.getPk_item() != "~")){ //�ڳ�����ռ��ִ����������Ҫ���лس�
				continue;
			}
			BXBusItemVO busItem = (BXBusItemVO)busItemMap.get(vo.getPk_busitem()).clone();
			
			String[] attributeNames = busItem.getAttributeNames();
			for (String attr : attributeNames) {
				if(attr.equals("cashproj") || attr.equals("jkbxr") || attr.equals("cashitem")){
					continue;
				}
				head.setAttributeValue(attr, busItem.getAttributeValue(attr));
			}
			
			head.setYbje(vo.getCjkybje());
			head.setBbje(vo.getCjkbbje());
			head.setGroupbbje(vo.getGroupcjkbbje());
			head.setGlobalbbje(vo.getGlobalcjkbbje());
			
			// ����Ԥ����
			head.setPreItemJe(new UFDouble[] { vo.getGlobalfybbje(), vo.getGroupfybbje(), vo.getFybbje(),
					vo.getFyybje()});
			headers.add(head);
		}
		
		return headers.toArray(new JKBXHeaderVO[]{});
	}
	
	public static boolean isSimpleEquals(Object obj,Object obj1){
		if(obj==null && obj1==null)
			return true;
		else if(obj1==null || obj==null)
			return false;
		else
			return obj1.equals(obj);
		
	}

}
