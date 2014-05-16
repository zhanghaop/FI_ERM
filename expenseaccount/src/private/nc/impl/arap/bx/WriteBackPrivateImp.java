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
		//根据控制参数过滤数据
		List<JKBXVO> filtVOs = filtByParam(eventType, vos);
		if(filtVOs.isEmpty()){
			return new ArrayList<IMtappCtrlBusiVO>();
		}
		// 封装业务数据
		return construstBusiData(filtVOs.toArray(new JKBXVO[0]),eventType);
	}
	/**
	 * 根据控制参数过滤数据
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
					//保存控制环节，生效动作不回写
					continue;
				}
			}/*else if(MatterAppUtils.getParamIsEffect((String) jkbxvo.getParentVO().getAttributeValue("pk_org"))){
				//生效控制环节，保存动作不回写，2013-01-06
				continue;
			}	*/
			filtVOs.add(jkbxvo);
		}
		return filtVOs;
	}

	/**
	 * 封装业务数据
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
				// 申请单分摊场景，报销单通过结转单回写申请单，这里不进行处理
				continue;
			}
			if(WriteBackUtil.getParamIsEffect(jkbxvo.getParentVO().getPk_org())){
				if (ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)) {
					// 此处只处理预占数，可以忽略排序  2013-04-16
					positiveOperationForJKBX(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
				}else if (ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) {
					// 此处只处理预占数，可以忽略排序 2013-04-16
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo.getBxoldvo());
					negativeOperationForJKBX(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo.getBxoldvo());
					positiveOperationForJKBX(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
				}else if (ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)
						 || ErmEventType.TYPE_InValid_AFTER.equalsIgnoreCase(eventType)) {
					// 此处只处理预占数，可以忽略排序 2013-04-16
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					negativeOperationForJKBX(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
				}else if (ErmEventType.TYPE_SIGN_BEFORE.equalsIgnoreCase(eventType)) {
					// 反向预占、正向执行
					negativeOperationForJKBX(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
					positiveOperationForJKBX(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
				}else if (ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)) {
					// 反向执行、正向预占
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
						||ErmEventType.TYPE_InValid_AFTER.equalsIgnoreCase(eventType)) {
					negativeOperationForJKBX(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
				} 
			}
		}
		return mtBusiVoList;
	}
	/**
	 * 包装冲借款信息
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
		
		// 3 报销new冲借款
		BxcontrastVO[] contrastVO = jkbxvo.getContrastVO();
		if (!ArrayUtils.isEmpty(contrastVO)) {
			// 根据报销单直接包装冲借款信息
			List<JKBXVO> contrastVoList = constructBxConstrastVosByJK(contrastVO);
			for (JKBXVO jkVo : contrastVoList) {
				JKBXHeaderVO[] jkVOs = ErVOUtils.prepareBxvoItemToHeaderClone(jkVo);
				WriteBackUtil.fillBusiVo(dataType, mtBusiVoList, isAdd, true, jkVOs);
			}
			
		}
	}
	/**
	 * 正向回写，包装冲借款信息
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
	 * 正向回写
	 * 
	 * @param dataType 回写数据类型
	 * @param mtBusiVoList
	 * @param jkbxvo
	 * @author: wangyhh@ufida.com.cn
	 */
	private void positiveOperationForJKBX(String dataType, List<IMtappCtrlBusiVO> mtBusiVoList, JKBXVO jkbxvo) {
		if (jkbxvo == null || jkbxvo.getParentVO().getDjzt().intValue() == BXStatusConst.DJZT_TempSaved) {
			return;
		}
		// 4报销new新增
		JKBXHeaderVO[] headerVos = null;
		boolean ismashare = jkbxvo.getParentVO().getIsmashare()==null?false:jkbxvo.getParentVO().getIsmashare().booleanValue();
		if(ismashare && BXConstans.BX_DJDL.equals(jkbxvo.getParentVO().getDjdl())){
// 转移到结转单实现，因为保存时无法获取到分摊行pk
			headerVos = new JKBXHeaderVO[0];
//			// 报销单拉分摊申请单情况，使用分摊页签包装回写数据
//			headerVos = ErVOUtils.prepareCsharedetailToHeaderClone(jkbxvo);
		}else{
			headerVos = ErVOUtils.prepareBxvoItemToHeaderClone(jkbxvo);
		}
		WriteBackUtil.fillBusiVo(dataType, mtBusiVoList, true, false, headerVos);
	}

	/**
	 * 反向回写
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
	 * 反向回写
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
		 
		// 1 报销old删除
		JKBXHeaderVO[] oldHeaderVOs = null;
		boolean ismashare = jkbxvo.getParentVO().getIsmashare()==null?false:jkbxvo.getParentVO().getIsmashare().booleanValue();
		if(ismashare && BXConstans.BX_DJDL.equals(jkbxvo.getParentVO().getDjdl())){
// 转移到结转单实现，因为保存时无法获取到分摊行pk
			oldHeaderVOs = new JKBXHeaderVO[0];
//			// 报销单拉分摊申请单情况，使用分摊页签包装回写数据
//			oldHeaderVOs = ErVOUtils.prepareCsharedetailToHeaderClone(jkbxvo);
		}else{
			oldHeaderVOs = ErVOUtils.prepareBxvoItemToHeaderClone(jkbxvo);
		}
		WriteBackUtil.fillBusiVo(dataType, mtBusiVoList, false, false, oldHeaderVOs);
		
	}

//	/**
//	 * 根据报销单业务行，包装冲借款回写数据
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
//			// 获得冲销行对应的报销单业务行
//			BXBusItemVO busItemVoClone = (BXBusItemVO) pk2BXBusItemVOMap.get(bxcontrastVO.getPk_finitem()).clone();
//
//			// 控制金额为冲借款中涉及的原币金额
//			UFDouble ybje = bxcontrastVO.getYbje();
//			busItemVoClone.setYbje(ybje);
//			
//			//控制冲借款原币金额为费用原币金额
//			UFDouble fyybje = bxcontrastVO.getFyybje();
//			busItemVoClone.setCjkybje(fyybje);
//			
//			// 保留冲销行的PK
//			busItemVoClone.setPk_busitem(bxcontrastVO.getPk_bxcontrast());
//			
//			// 设置上下游pk
//			busItemVoClone.setBx_busitemPK(bxcontrastVO.getPk_finitem());
//			busItemVoClone.setJk_busitemPK(bxcontrastVO.getPk_busitem());
//			
//			// 按上游单据的
//			
//			contrastList.add(busItemVoClone);
//		}
//
//		// 封装主子表、返回
//		JKBXHeaderVO parentVO = jkbxvo.getParentVO();
//		return new JKVO(parentVO, contrastList.toArray(new BXBusItemVO[0]));
//	}

	/**
	 * 根据借款单包装冲借款回写数据
	 * 
	 * @param contrastVO
	 * @return
	 */
	private List<JKBXVO> constructBxConstrastVosByJK(BxcontrastVO[] contrastVO) {
		try {
			// 1 查询借款vo：主表+冲销业务行
			// 借款单pks  含重复值
			String[] ikdPks = VOUtils.getAttributeValues(contrastVO, BxcontrastVO.PK_JKD);
			// 借款单业务行pks  含重复值
			String[] busitemPks = VOUtils.getAttributeValues(contrastVO, BxcontrastVO.PK_BUSITEM);

			IBXBillPrivate queryService = NCLocator.getInstance().lookup(IBXBillPrivate.class);
			List<JKBXHeaderVO> headList = queryService.queryHeadersByWhereSql(" where " + SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, ikdPks,true), BXConstans.JK_DJDL);
			BXBusItemVO[] busitems = queryService.queryItemsByPks(busitemPks);

			// 2 替换原币值为冲销值
			//冲销行封装成借款分录，存在一行被多次冲销情况
			Map<String, SuperVO> pk2BXBusItemVOMap = VOUtils.changeCollectionToMap(Arrays.asList(busitems), BXBusItemVO.PK_BUSITEM);
			List<BXBusItemVO> contrastList = new ArrayList<BXBusItemVO>();
			for (BxcontrastVO bxcontrastVO : contrastVO) {
				BXBusItemVO busItemVoClone = (BXBusItemVO) pk2BXBusItemVOMap.get(bxcontrastVO.getPk_busitem()).clone();

				// 控制金额为冲借款中涉及的原币金额
				UFDouble ybje = bxcontrastVO.getYbje();
				busItemVoClone.setYbje(ybje);
				
				//控制冲借款原币金额为费用原币金额
				UFDouble fyybje = bxcontrastVO.getFyybje();
				busItemVoClone.setCjkybje(fyybje);
				
				//保留冲销行的PK
				busItemVoClone.setPk_busitem(bxcontrastVO.getPk_bxcontrast());
				// 设置上下游pk
				busItemVoClone.setBx_busitemPK(bxcontrastVO.getPk_finitem());
				busItemVoClone.setJk_busitemPK(bxcontrastVO.getPk_busitem());
				
				contrastList.add(busItemVoClone);
			}

			// 3 封装主子表
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
		// 过滤掉不回写的结转单
		List<AggCostShareVO> filtVOs = filtByParam(vos, eventType);
		if(filtVOs.isEmpty()){
			return new ArrayList<IMtappCtrlBusiVO>();
		}
		// 包装回写结构
		return construstBusiData(vos,eventType);
	}

	/**
	 * 根据控制参数过滤数据
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
				// 报销单拉非分摊申请单，不处理
				continue;
			}
			if (ErmEventType.TYPE_SIGN_BEFORE.equalsIgnoreCase(eventType) || ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)) {
				if(!WriteBackUtil.getParamIsEffect((String) aggvo.getParentVO().getAttributeValue("pk_org"))){
					//保存控制环节，生效动作不回写
					continue;
				}
			}/*else if(MatterAppUtils.getParamIsEffect((String) jkbxvo.getParentVO().getAttributeValue("pk_org"))){
				//生效控制环节，保存动作不回写，2013-01-06
				continue;
			}	*/
			filtVOs.add(aggvo);
		}
		return filtVOs;
	}
	
	/**
	 * 封装结转单业务数据
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
					// 此处只处理预占数
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					positiveOperationForCS(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, aggvo);
				}else if (ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)||
						ErmEventType.TYPE_UPDATE_BEFORE.equalsIgnoreCase(eventType)) {
					// 此处只处理预占数
					negativeOperationForCS(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, aggvo.getOldvo());
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo.getBxoldvo());
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					positiveOperationForCS(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, aggvo);
				}else if (ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)||
						ErmEventType.TYPE_DELETE_AFTER.equalsIgnoreCase(eventType)) {
					// 此处只处理预占数
					negativeOperationForCS(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, aggvo);
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
				}else if (ErmEventType.TYPE_APPROVE_AFTER.equalsIgnoreCase(eventType)||
						ErmEventType.TYPE_APPROVE_BEFORE.equalsIgnoreCase(eventType)) {
					// 反向预占、正向执行
					negativeOperationForCS(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, aggvo);
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_pre, mtBusiVoList, jkbxvo);
					positiveOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
					positiveOperationForCS(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, aggvo);
				}else if (ErmEventType.TYPE_UNAPPROVE_BEFORE.equalsIgnoreCase(eventType)
						||ErmEventType.TYPE_UNAPPROVE_AFTER.equalsIgnoreCase(eventType)) {
					// 反向执行、正向预占
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
				}else if (ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)) {
					negativeOperationForCS(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, aggvo);
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
				} 
			}
		}
		return mtBusiVoList;
	}
	
	/**
	 * 正向回写结转单
	 * 
	 * @param dataType 回写数据类型
	 * @param mtBusiVoList
	 * @param aggvo
	 * @author: wangyhh@ufida.com.cn
	 */
	private void positiveOperationForCS(String dataType, List<IMtappCtrlBusiVO> mtBusiVoList, AggCostShareVO aggvo) {
		if (aggvo == null || ((CostShareVO)aggvo.getParentVO()).getBillstatus().intValue() == BXStatusConst.DJZT_TempSaved) {
			// 暂存不处理
			return;
		}
		
		contrastCsBusiVO(aggvo,dataType, IMtappCtrlBusiVO.Direction_positive, mtBusiVoList);
	}
	
	/**
	 * 结转单反向回写申请单
	 * 
	 * @param dataType
	 * @param mtBusiVoList 
	 * @param aggvo
	 */
	private void negativeOperationForCS(String dataType, List<IMtappCtrlBusiVO> mtBusiVoList, AggCostShareVO aggvo) {
		if (aggvo == null || ((CostShareVO)aggvo.getParentVO()).getBillstatus().intValue() == BXStatusConst.DJZT_TempSaved) {
			// 暂存不处理
			return;
		}
		 
		contrastCsBusiVO(aggvo,dataType, IMtappCtrlBusiVO.Direction_negative, mtBusiVoList);
		
	}
	/**
	 * 包装结转单回写申请单结构
	 * 
	 * @param aggvo 结转单vo
	 * @param dataType 回写数据类型
	 * @param direction 回写数据方向
	 * @param mtBusiVoList 待回写vo集合
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
				// 设置方向和回写数据类型
				vo.setDirection(direction);
				vo.setDatatype(dataType);
				
				mtBusiVoList.add(vo);
			}
		}
	}
}
