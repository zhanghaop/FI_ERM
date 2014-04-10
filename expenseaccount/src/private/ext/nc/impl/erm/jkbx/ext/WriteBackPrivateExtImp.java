package nc.impl.erm.jkbx.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.er.util.WriteBackUtil;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.erm.jkbx.ext.IWriteBackPrivateExt;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.ArrayUtils;

/**
 * 构造整单回写申请单的回写结构,服务实现
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public class WriteBackPrivateExtImp implements IWriteBackPrivateExt {

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
			if (ErmEventType.TYPE_SIGN_BEFORE.equalsIgnoreCase(eventType) || ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)) {
				if(!WriteBackUtil.getParamIsEffect((String) jkbxvo.getParentVO().getAttributeValue("pk_org"))){
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
				}else if (ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)) {
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
				}else if (ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)) {
					negativeOperationForJKBX(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
				} 
			}
		}
		return mtBusiVoList;
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
		JKBXHeaderVO parentVO = jkbxvo.getParentVO();
		if (jkbxvo == null || parentVO.getDjzt().intValue() == BXStatusConst.DJZT_TempSaved) {
			return;
		}
		// clone主表，且设置明细pk为本身pk
		JKBXHeaderVO new_parentVO = (JKBXHeaderVO) parentVO.clone();
		new_parentVO.setPk_busitem(new_parentVO.getPrimaryKey());
		// 获得主表数据即可
		JKBXHeaderVO[] headerVos = new JKBXHeaderVO[]{new_parentVO};
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
	private void negativeOperationForJKBX(String dataType, List<IMtappCtrlBusiVO> mtBusiVoList, JKBXVO jkbxvo) {
		JKBXHeaderVO parentVO = jkbxvo.getParentVO();
		if (jkbxvo == null || parentVO.getDjzt().intValue() == BXStatusConst.DJZT_TempSaved) {
			return;
		}
		// clone主表，且设置明细pk为本身pk
		JKBXHeaderVO new_parentVO = (JKBXHeaderVO) parentVO.clone();
		new_parentVO.setPk_busitem(new_parentVO.getPrimaryKey());
		// 获得主表数据即可
		JKBXHeaderVO[] oldHeaderVOs = new JKBXHeaderVO[]{new_parentVO};
		WriteBackUtil.fillBusiVo(dataType, mtBusiVoList, false, false, oldHeaderVOs);
		
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
			JKBXHeaderVO[] contrastVos = constructBxConstrastVosByJK(contrastVO);
			WriteBackUtil.fillBusiVo(dataType, mtBusiVoList, isAdd, true, contrastVos);
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
	 * 根据借款单包装冲借款回写数据
	 * @param jkbxvo 
	 * @param contrastVO
	 * 
	 * @return
	 * @throws BusinessException 
	 */
	private JKBXHeaderVO[] constructBxConstrastVosByJK(BxcontrastVO[] contrastVO){
		// 借款单pks  含重复值
		String[] ikdPks = VOUtils.getAttributeValues(contrastVO, BxcontrastVO.PK_JKD);

		IBXBillPrivate queryService = NCLocator.getInstance().lookup(IBXBillPrivate.class);
		List<JKBXHeaderVO> headList;
		try {
			headList = queryService.queryHeadersByWhereSql(" where " + SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, ikdPks,true), BXConstans.JK_DJDL);
		} catch (BusinessException e) {
			Logger.error(e);
			throw new RuntimeException(e);
		}
		Map<String, JKBXHeaderVO> jkmap = VOUtils.changeCollection2Map(headList);
		
		// 借款单+报销单，分组的冲销行总额信息
		Map<String, JKBXHeaderVO> contrastmap = new HashMap<String, JKBXHeaderVO>();
		
		for (BxcontrastVO bxcontrastVO : contrastVO) {
			String pk_busitem = bxcontrastVO.getPk_jkd()+bxcontrastVO.getPk_bxd();
			JKBXHeaderVO headvo = contrastmap.get(pk_busitem);
			if(headvo == null){
				// 回写头的数据使用借款单
				headvo = (JKBXHeaderVO) jkmap.get(bxcontrastVO.getPk_jkd()).clone();
				// 清空原币金额、冲借款金额
				headvo.setYbje(UFDouble.ZERO_DBL);
				headvo.setCjkybje(UFDouble.ZERO_DBL);
				// 明细行pk设置为 pk_jkd +　pk_bxd
				headvo.setPk_busitem(pk_busitem);
				// 设置上下游pk
				headvo.setBx_busitemPK(bxcontrastVO.getPk_bxd());
				headvo.setJk_busitemPK(bxcontrastVO.getPk_jkd());
				
				contrastmap.put(pk_busitem, headvo);
			}
			// 控制金额为冲借款中涉及的原币金额
			UFDouble ybje = bxcontrastVO.getYbje();
			headvo.setYbje(ybje.add(headvo.getYbje()));
			
			//控制冲借款原币金额为费用原币金额
			UFDouble fyybje = bxcontrastVO.getFyybje();
			headvo.setCjkybje(fyybje.add(headvo.getCjkybje()));
			
		}
		
		return contrastmap.values().toArray(new JKBXHeaderVO[0]);
	}

}
