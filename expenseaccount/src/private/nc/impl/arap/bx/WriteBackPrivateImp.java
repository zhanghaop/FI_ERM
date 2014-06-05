package nc.impl.arap.bx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nc.bs.er.util.WriteBackUtil;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.prv.IWriteBackPrivate;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.CsMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.erm.util.VOUtils;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.ArrayUtils;

public class WriteBackPrivateImp implements IWriteBackPrivate {

	@Override
	public List<IMtappCtrlBusiVO> construstBusiDataForWriteBack(JKBXVO[] vos, String eventType) {
		//���ݿ��Ʋ�����������
		List<JKBXVO> filtVOs = filtByParam(eventType, vos);
		if(filtVOs.isEmpty()){
			return new ArrayList<IMtappCtrlBusiVO>();
		}
		// ��װҵ������
		return construstBusiData(filtVOs.toArray(new JKBXVO[0]),eventType);
	}
	/**
	 * ���ݿ��Ʋ�����������
	 * 
	 * @param eventType
	 * @param vos
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	private List<JKBXVO> filtByParam(String eventType, JKBXVO[] vos) {
		List<JKBXVO> filtVOs = new ArrayList<JKBXVO>();
		for (JKBXVO jkbxvo : vos) {
			JKBXHeaderVO parentVO = jkbxvo.getParentVO();
			if(StringUtil.isEmpty(parentVO.getPk_item())&&!BXConstans.BILLTYPECODE_RETURNBILL.equals(parentVO.getDjlxbm())){
				continue;
			}
			if (ErmEventType.TYPE_SIGN_BEFORE.equalsIgnoreCase(eventType) || ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)) {
				if(!WriteBackUtil.getParamIsEffect((String) parentVO.getAttributeValue("pk_org"))){
					//������ƻ��ڣ���Ч��������д
					continue;
				}
			}/*else if(MatterAppUtils.getParamIsEffect((String) jkbxvo.getParentVO().getAttributeValue("pk_org"))){
				//��Ч���ƻ��ڣ����涯������д��2013-01-06
				continue;
			}	*/
			filtVOs.add(jkbxvo);
		}
		return filtVOs;
	}

	/**
	 * ��װҵ������
	 * 
	 * @param vos
	 * @param eventType
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	private List<IMtappCtrlBusiVO> construstBusiData(JKBXVO[] vos, String eventType) {
		List<IMtappCtrlBusiVO> mtBusiVoList = new ArrayList<IMtappCtrlBusiVO>();
		for (JKBXVO jkbxvo : vos) {
			boolean ismashare = jkbxvo.getParentVO().getIsmashare()==null?false:jkbxvo.getParentVO().getIsmashare().booleanValue();
			if(ismashare && BXConstans.BX_DJDL.equals(jkbxvo.getParentVO().getDjdl())){
				// ���뵥��̯������������ͨ����ת����д���뵥�����ﲻ���д���
				continue;
			}
			if(WriteBackUtil.getParamIsEffect(jkbxvo.getParentVO().getPk_org())){
				if (ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)) {
					// �˴�ֻ����Ԥռ�������Ժ�������  2013-04-16
					positiveOperationForJKBX(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
				}else if (ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) {
					// �˴�ֻ����Ԥռ�������Ժ������� 2013-04-16
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo.getBxoldvo());
					negativeOperationForJKBX(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo.getBxoldvo());
					positiveOperationForJKBX(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
				}else if (ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)
						 || ErmEventType.TYPE_INVALID_BEFORE.equalsIgnoreCase(eventType)) {
					// �˴�ֻ����Ԥռ�������Ժ������� 2013-04-16
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					negativeOperationForJKBX(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
				}else if (ErmEventType.TYPE_SIGN_BEFORE.equalsIgnoreCase(eventType)) {
					// ����Ԥռ������ִ��
					negativeOperationForJKBX(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
					positiveOperationForJKBX(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
				}else if (ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)) {
					// ����ִ�С�����Ԥռ
					negativeOperationForJKBX(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					positiveOperationForJKBX(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
				}
			}else{
				if (ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)) {
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
					positiveOperationForJKBX(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
				}else if (ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) {
					negativeOperationForJKBX(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo.getBxoldvo());
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo.getBxoldvo());
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
					positiveOperationForJKBX(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
				}else if (ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)
						||ErmEventType.TYPE_INVALID_BEFORE.equalsIgnoreCase(eventType)) {
					negativeOperationForJKBX(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
				} 
			}
		}
		return mtBusiVoList;
	}
	/**
	 * ��װ������Ϣ
	 * 
	 * @param dataType
	 * @param mtBusiVoList
	 * @param jkbxvo
	 * @author: wangyhh@ufida.com.cn
	 */
	private void operationForContrast(String dataType, List<IMtappCtrlBusiVO> mtBusiVoList, JKBXVO jkbxvo,boolean isAdd) {
		if (jkbxvo == null || jkbxvo.getParentVO().getDjzt().intValue() == BXStatusConst.DJZT_TempSaved) {
			return;
		}
		
		// 3 ����new����
		BxcontrastVO[] contrastVO = jkbxvo.getContrastVO();
		if (!ArrayUtils.isEmpty(contrastVO)) {
			// ���ݱ�����ֱ�Ӱ�װ������Ϣ
			List<JKBXVO> contrastVoList = constructBxConstrastVosByJK(contrastVO);
			for (JKBXVO jkVo : contrastVoList) {
				JKBXHeaderVO[] jkVOs = ErVOUtils.prepareBxvoItemToHeaderClone(jkVo);
				WriteBackUtil.fillBusiVo(dataType, mtBusiVoList, isAdd, true, jkVOs);
			}
			
		}
	}
	/**
	 * �����д����װ������Ϣ
	 * 
	 * @param dataType
	 * @param mtBusiVoList
	 * @param jkbxvo
	 * @author: wangyhh@ufida.com.cn
	 */
	private void positiveOperationForContrast(String dataType, List<IMtappCtrlBusiVO> mtBusiVoList, JKBXVO jkbxvo) {
		operationForContrast(dataType, mtBusiVoList, jkbxvo, false);
	}
	/**
	 * �����д
	 * 
	 * @param dataType ��д��������
	 * @param mtBusiVoList
	 * @param jkbxvo
	 * @author: wangyhh@ufida.com.cn
	 */
	private void positiveOperationForJKBX(String dataType, List<IMtappCtrlBusiVO> mtBusiVoList, JKBXVO jkbxvo) {
		if (jkbxvo == null || jkbxvo.getParentVO().getDjzt().intValue() == BXStatusConst.DJZT_TempSaved) {
			return;
		}
		// 4����new����
		JKBXHeaderVO[] headerVos = null;
		boolean ismashare = jkbxvo.getParentVO().getIsmashare()==null?false:jkbxvo.getParentVO().getIsmashare().booleanValue();
		if(ismashare && BXConstans.BX_DJDL.equals(jkbxvo.getParentVO().getDjdl())){
// ת�Ƶ���ת��ʵ�֣���Ϊ����ʱ�޷���ȡ����̯��pk
			headerVos = new JKBXHeaderVO[0];
//			// ����������̯���뵥�����ʹ�÷�̯ҳǩ��װ��д����
//			headerVos = ErVOUtils.prepareCsharedetailToHeaderClone(jkbxvo);
		}else{
			headerVos = ErVOUtils.prepareBxvoItemToHeaderClone(jkbxvo);
		}
		WriteBackUtil.fillBusiVo(dataType, mtBusiVoList, true, false, headerVos);
	}

	/**
	 * �����д
	 * 
	 * @param dataType
	 * @param mtBusiVoList 
	 * @param jkbxvo
	 * @author: wangyhh@ufida.com.cn
	 */
	private void negativeOperationForContrast(String dataType, List<IMtappCtrlBusiVO> mtBusiVoList, JKBXVO jkbxvo) {
		operationForContrast(dataType, mtBusiVoList, jkbxvo, true);
	}
	/**
	 * �����д
	 * 
	 * @param dataType
	 * @param mtBusiVoList 
	 * @param jkbxvo
	 * @author: wangyhh@ufida.com.cn
	 */
	private void negativeOperationForJKBX(String dataType, List<IMtappCtrlBusiVO> mtBusiVoList, JKBXVO jkbxvo) {
		if (jkbxvo == null || jkbxvo.getParentVO().getDjzt().intValue() == BXStatusConst.DJZT_TempSaved) {
			return;
		}
		 
		// 1 ����oldɾ��
		JKBXHeaderVO[] oldHeaderVOs = null;
		boolean ismashare = jkbxvo.getParentVO().getIsmashare()==null?false:jkbxvo.getParentVO().getIsmashare().booleanValue();
		if(ismashare && BXConstans.BX_DJDL.equals(jkbxvo.getParentVO().getDjdl())){
// ת�Ƶ���ת��ʵ�֣���Ϊ����ʱ�޷���ȡ����̯��pk
			oldHeaderVOs = new JKBXHeaderVO[0];
//			// ����������̯���뵥�����ʹ�÷�̯ҳǩ��װ��д����
//			oldHeaderVOs = ErVOUtils.prepareCsharedetailToHeaderClone(jkbxvo);
		}else{
			oldHeaderVOs = ErVOUtils.prepareBxvoItemToHeaderClone(jkbxvo);
		}
		WriteBackUtil.fillBusiVo(dataType, mtBusiVoList, false, false, oldHeaderVOs);
		
	}

//	/**
//	 * ���ݱ�����ҵ���У���װ�����д����
//	 * @param contrastVOs 
//	 * @param jkbxvo
//	 * @return
//	 */
//	private JKBXVO constructBxConstrastVosByBX(BxcontrastVO[] contrastVOs, JKBXVO jkbxvo) {
//		BXBusItemVO[] busitems = jkbxvo.getChildrenVO();
//		
//		Map<String, SuperVO> pk2BXBusItemVOMap = VOUtils.changeCollectionToMap(Arrays.asList(busitems), BXBusItemVO.PK_BUSITEM);
//		List<BXBusItemVO> contrastList = new ArrayList<BXBusItemVO>();
//		for (BxcontrastVO bxcontrastVO : contrastVOs) {
//			// ��ó����ж�Ӧ�ı�����ҵ����
//			BXBusItemVO busItemVoClone = (BXBusItemVO) pk2BXBusItemVOMap.get(bxcontrastVO.getPk_finitem()).clone();
//
//			// ���ƽ��Ϊ�������漰��ԭ�ҽ��
//			UFDouble ybje = bxcontrastVO.getYbje();
//			busItemVoClone.setYbje(ybje);
//			
//			//���Ƴ���ԭ�ҽ��Ϊ����ԭ�ҽ��
//			UFDouble fyybje = bxcontrastVO.getFyybje();
//			busItemVoClone.setCjkybje(fyybje);
//			
//			// ���������е�PK
//			busItemVoClone.setPk_busitem(bxcontrastVO.getPk_bxcontrast());
//			
//			// ����������pk
//			busItemVoClone.setBx_busitemPK(bxcontrastVO.getPk_finitem());
//			busItemVoClone.setJk_busitemPK(bxcontrastVO.getPk_busitem());
//			
//			// �����ε��ݵ�
//			
//			contrastList.add(busItemVoClone);
//		}
//
//		// ��װ���ӱ�����
//		JKBXHeaderVO parentVO = jkbxvo.getParentVO();
//		return new JKVO(parentVO, contrastList.toArray(new BXBusItemVO[0]));
//	}

	/**
	 * ���ݽ���װ�����д����
	 * 
	 * @param contrastVO
	 * @return
	 */
	private List<JKBXVO> constructBxConstrastVosByJK(BxcontrastVO[] contrastVO) {
		try {
			// 1 ��ѯ���vo������+����ҵ����
			// ��pks  ���ظ�ֵ
			String[] ikdPks = VOUtils.getAttributeValues(contrastVO, BxcontrastVO.PK_JKD);
			// ��ҵ����pks  ���ظ�ֵ
			String[] busitemPks = VOUtils.getAttributeValues(contrastVO, BxcontrastVO.PK_BUSITEM);

			IBXBillPrivate queryService = NCLocator.getInstance().lookup(IBXBillPrivate.class);
			List<JKBXHeaderVO> headList = queryService.queryHeadersByWhereSql(" where " + SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, ikdPks,true), BXConstans.JK_DJDL);
			BXBusItemVO[] busitems = queryService.queryItemsByPks(busitemPks);

			// 2 �滻ԭ��ֵΪ����ֵ
			//�����з�װ�ɽ���¼������һ�б���γ������
			Map<String, SuperVO> pk2BXBusItemVOMap = VOUtils.changeCollectionToMap(Arrays.asList(busitems), BXBusItemVO.PK_BUSITEM);
			List<BXBusItemVO> contrastList = new ArrayList<BXBusItemVO>();
			for (BxcontrastVO bxcontrastVO : contrastVO) {
				BXBusItemVO busItemVoClone = (BXBusItemVO) pk2BXBusItemVOMap.get(bxcontrastVO.getPk_busitem()).clone();

				// ���ƽ��Ϊ�������漰��ԭ�ҽ��
				UFDouble ybje = bxcontrastVO.getYbje();
				busItemVoClone.setYbje(ybje);
				
				//���Ƴ���ԭ�ҽ��Ϊ����ԭ�ҽ��
				UFDouble fyybje = bxcontrastVO.getFyybje();
				busItemVoClone.setCjkybje(fyybje);
				
				//���������е�PK
				busItemVoClone.setPk_busitem(bxcontrastVO.getPk_bxcontrast());
				// ����������pk
				busItemVoClone.setBx_busitemPK(bxcontrastVO.getPk_finitem());
				busItemVoClone.setJk_busitemPK(bxcontrastVO.getPk_busitem());
				
				contrastList.add(busItemVoClone);
			}

			// 3 ��װ���ӱ�
			List<JKBXVO> jkVoList = new ArrayList<JKBXVO>();
			Map<String, List<CircularlyAccessibleValueObject>> kbxPk2BXBusItemVOMapList = VOUtils.changeArrayToMapList(contrastList.toArray(new BXBusItemVO[0]), new String[] { BXBusItemVO.PK_JKBX });
			for (JKBXHeaderVO headVo : headList) {
				List<CircularlyAccessibleValueObject> list = kbxPk2BXBusItemVOMapList.get(headVo.getPk_jkbx());
				JKBXVO jkVO = new JKVO(headVo, list.toArray(new BXBusItemVO[0]));
				jkVoList.add(jkVO);
			}
			return jkVoList;
		} catch (Exception e) {
			Logger.error(e);
			throw new RuntimeException(e);
		}
	}
	@Override
	public List<IMtappCtrlBusiVO> construstCostshareDataForWriteBack(
			AggCostShareVO[] vos, String eventType) {
		// ���˵�����д�Ľ�ת��
		List<AggCostShareVO> filtVOs = filtByParam(vos, eventType);
		if(filtVOs.isEmpty()){
			return new ArrayList<IMtappCtrlBusiVO>();
		}
		// ��װ��д�ṹ
		return construstBusiData(vos,eventType);
	}

	/**
	 * ���ݿ��Ʋ�����������
	 * 
	 * @param eventType
	 * @param vos
	 * @return
	 */
	private List<AggCostShareVO> filtByParam(AggCostShareVO[] vos, String eventType) {
		List<AggCostShareVO> filtVOs = new ArrayList<AggCostShareVO>();
		for (AggCostShareVO aggvo : vos) {
			if(aggvo.getBxvo() == null || aggvo.getBxvo().getParentVO() == null || aggvo.getBxvo().getParentVO().getIsmashare() == null 
					|| !aggvo.getBxvo().getParentVO().getIsmashare().booleanValue()){
				// ���������Ƿ�̯���뵥��������
				continue;
			}
			if (ErmEventType.TYPE_SIGN_BEFORE.equalsIgnoreCase(eventType) || ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)) {
				if(!WriteBackUtil.getParamIsEffect((String) aggvo.getParentVO().getAttributeValue("pk_org"))){
					//������ƻ��ڣ���Ч��������д
					continue;
				}
			}/*else if(MatterAppUtils.getParamIsEffect((String) jkbxvo.getParentVO().getAttributeValue("pk_org"))){
				//��Ч���ƻ��ڣ����涯������д��2013-01-06
				continue;
			}	*/
			filtVOs.add(aggvo);
		}
		return filtVOs;
	}
	
	/**
	 * ��װ��ת��ҵ������
	 * 
	 * @param vos
	 * @param eventType
	 * @return
	 */
	private List<IMtappCtrlBusiVO> construstBusiData(AggCostShareVO[] vos, String eventType) {
		List<IMtappCtrlBusiVO> mtBusiVoList = new ArrayList<IMtappCtrlBusiVO>();
		for (AggCostShareVO aggvo : vos) {
			JKBXVO jkbxvo = aggvo.getBxvo();
			boolean paramIsEffect = WriteBackUtil.getParamIsEffect((String) aggvo.getParentVO().getAttributeValue("pk_org"));
			if(paramIsEffect){
				if (ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)
						||ErmEventType.TYPE_INSERT_BEFORE.equalsIgnoreCase(eventType)) {
					// �˴�ֻ����Ԥռ��
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					positiveOperationForCS(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, aggvo);
				}else if (ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)||
						ErmEventType.TYPE_UPDATE_BEFORE.equalsIgnoreCase(eventType)) {
					// �˴�ֻ����Ԥռ��
					negativeOperationForCS(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, aggvo.getOldvo());
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo.getBxoldvo());
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					positiveOperationForCS(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, aggvo);
				}else if (ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)||
						ErmEventType.TYPE_DELETE_AFTER.equalsIgnoreCase(eventType)
						|| ErmEventType.TYPE_INVALID_BEFORE.equalsIgnoreCase(eventType)) {
					// �˴�ֻ����Ԥռ��
					negativeOperationForCS(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, aggvo);
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
				}else if (ErmEventType.TYPE_APPROVE_AFTER.equalsIgnoreCase(eventType)||
						ErmEventType.TYPE_APPROVE_BEFORE.equalsIgnoreCase(eventType)) {
					// ����Ԥռ������ִ��
					negativeOperationForCS(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, aggvo);
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
					positiveOperationForCS(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, aggvo);
				}else if (ErmEventType.TYPE_UNAPPROVE_BEFORE.equalsIgnoreCase(eventType)
						||ErmEventType.TYPE_UNAPPROVE_AFTER.equalsIgnoreCase(eventType)) {
					// ����ִ�С�����Ԥռ
					negativeOperationForCS(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, aggvo);
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					positiveOperationForCS(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, aggvo);
				}
			}else{
				if (ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)) {
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
					positiveOperationForCS(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, aggvo);
				}else if (ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) {
					negativeOperationForCS(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, aggvo.getOldvo());
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo.getBxoldvo());
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
					positiveOperationForCS(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, aggvo);
				}else if (ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)
						|| ErmEventType.TYPE_INVALID_BEFORE.equalsIgnoreCase(eventType)) {
					negativeOperationForCS(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, aggvo);
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
				} 
			}
		}
		return mtBusiVoList;
	}
	
	/**
	 * �����д��ת��
	 * 
	 * @param dataType ��д��������
	 * @param mtBusiVoList
	 * @param aggvo
	 * @author: wangyhh@ufida.com.cn
	 */
	private void positiveOperationForCS(String dataType, List<IMtappCtrlBusiVO> mtBusiVoList, AggCostShareVO aggvo) {
		if (aggvo == null || ((CostShareVO)aggvo.getParentVO()).getBillstatus().intValue() == BXStatusConst.DJZT_TempSaved) {
			// �ݴ治����
			return;
		}
		
		contrastCsBusiVO(aggvo,dataType, IMtappCtrlBusiVO.Direction_positive, mtBusiVoList);
	}
	
	/**
	 * ��ת�������д���뵥
	 * 
	 * @param dataType
	 * @param mtBusiVoList 
	 * @param aggvo
	 */
	private void negativeOperationForCS(String dataType, List<IMtappCtrlBusiVO> mtBusiVoList, AggCostShareVO aggvo) {
		if (aggvo == null || ((CostShareVO)aggvo.getParentVO()).getBillstatus().intValue() == BXStatusConst.DJZT_TempSaved) {
			// �ݴ治����
			return;
		}
		 
		contrastCsBusiVO(aggvo,dataType, IMtappCtrlBusiVO.Direction_negative, mtBusiVoList);
		
	}
	/**
	 * ��װ��ת����д���뵥�ṹ
	 * 
	 * @param aggvo ��ת��vo
	 * @param dataType ��д��������
	 * @param direction ��д���ݷ���
	 * @param mtBusiVoList ����дvo����
	 */
	protected void contrastCsBusiVO(AggCostShareVO aggvo,String dataType, int direction,
			List<IMtappCtrlBusiVO> mtBusiVoList) {
		CircularlyAccessibleValueObject[] childrenVO = aggvo.getChildrenVO();
		if(childrenVO != null && childrenVO.length > 0){
			CostShareVO parentVO = (CostShareVO)aggvo.getParentVO();
			for (int i = 0; i < childrenVO.length; i++) {
				CShareDetailVO detailVO = (CShareDetailVO) childrenVO[i];
				if(StringUtil.isEmpty(detailVO.getPk_item())||detailVO.getStatus() == VOStatus.DELETED){
					continue;
				}
				CsMtappCtrlBusiVO vo = new CsMtappCtrlBusiVO(parentVO,detailVO);
				// ���÷���ͻ�д��������
				vo.setDirection(direction);
				vo.setDatatype(dataType);
				
				mtBusiVoList.add(vo);
			}
		}
	}
}
