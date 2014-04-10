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
 * ����������д���뵥�Ļ�д�ṹ,����ʵ��
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 *
 */
public class WriteBackPrivateExtImp implements IWriteBackPrivateExt {

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
			if (ErmEventType.TYPE_SIGN_BEFORE.equalsIgnoreCase(eventType) || ErmEventType.TYPE_UNSIGN_BEFORE.equalsIgnoreCase(eventType)) {
				if(!WriteBackUtil.getParamIsEffect((String) jkbxvo.getParentVO().getAttributeValue("pk_org"))){
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
				}else if (ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)) {
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
				}else if (ErmEventType.TYPE_DELETE_BEFORE.equalsIgnoreCase(eventType)) {
					negativeOperationForJKBX(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
					negativeOperationForContrast(IMtappCtrlBusiVO.DataType_exe, mtBusiVoList, jkbxvo);
				} 
			}
		}
		return mtBusiVoList;
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
		JKBXHeaderVO parentVO = jkbxvo.getParentVO();
		if (jkbxvo == null || parentVO.getDjzt().intValue() == BXStatusConst.DJZT_TempSaved) {
			return;
		}
		// clone������������ϸpkΪ����pk
		JKBXHeaderVO new_parentVO = (JKBXHeaderVO) parentVO.clone();
		new_parentVO.setPk_busitem(new_parentVO.getPrimaryKey());
		// ����������ݼ���
		JKBXHeaderVO[] headerVos = new JKBXHeaderVO[]{new_parentVO};
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
	private void negativeOperationForJKBX(String dataType, List<IMtappCtrlBusiVO> mtBusiVoList, JKBXVO jkbxvo) {
		JKBXHeaderVO parentVO = jkbxvo.getParentVO();
		if (jkbxvo == null || parentVO.getDjzt().intValue() == BXStatusConst.DJZT_TempSaved) {
			return;
		}
		// clone������������ϸpkΪ����pk
		JKBXHeaderVO new_parentVO = (JKBXHeaderVO) parentVO.clone();
		new_parentVO.setPk_busitem(new_parentVO.getPrimaryKey());
		// ����������ݼ���
		JKBXHeaderVO[] oldHeaderVOs = new JKBXHeaderVO[]{new_parentVO};
		WriteBackUtil.fillBusiVo(dataType, mtBusiVoList, false, false, oldHeaderVOs);
		
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
			JKBXHeaderVO[] contrastVos = constructBxConstrastVosByJK(contrastVO);
			WriteBackUtil.fillBusiVo(dataType, mtBusiVoList, isAdd, true, contrastVos);
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
	 * @param dataType
	 * @param mtBusiVoList 
	 * @param jkbxvo
	 * @author: wangyhh@ufida.com.cn
	 */
	private void negativeOperationForContrast(String dataType, List<IMtappCtrlBusiVO> mtBusiVoList, JKBXVO jkbxvo) {
		operationForContrast(dataType, mtBusiVoList, jkbxvo, true);
	}


	/**
	 * ���ݽ���װ�����д����
	 * @param jkbxvo 
	 * @param contrastVO
	 * 
	 * @return
	 * @throws BusinessException 
	 */
	private JKBXHeaderVO[] constructBxConstrastVosByJK(BxcontrastVO[] contrastVO){
		// ��pks  ���ظ�ֵ
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
		
		// ��+������������ĳ������ܶ���Ϣ
		Map<String, JKBXHeaderVO> contrastmap = new HashMap<String, JKBXHeaderVO>();
		
		for (BxcontrastVO bxcontrastVO : contrastVO) {
			String pk_busitem = bxcontrastVO.getPk_jkd()+bxcontrastVO.getPk_bxd();
			JKBXHeaderVO headvo = contrastmap.get(pk_busitem);
			if(headvo == null){
				// ��дͷ������ʹ�ý�
				headvo = (JKBXHeaderVO) jkmap.get(bxcontrastVO.getPk_jkd()).clone();
				// ���ԭ�ҽ�������
				headvo.setYbje(UFDouble.ZERO_DBL);
				headvo.setCjkybje(UFDouble.ZERO_DBL);
				// ��ϸ��pk����Ϊ pk_jkd +��pk_bxd
				headvo.setPk_busitem(pk_busitem);
				// ����������pk
				headvo.setBx_busitemPK(bxcontrastVO.getPk_bxd());
				headvo.setJk_busitemPK(bxcontrastVO.getPk_jkd());
				
				contrastmap.put(pk_busitem, headvo);
			}
			// ���ƽ��Ϊ�������漰��ԭ�ҽ��
			UFDouble ybje = bxcontrastVO.getYbje();
			headvo.setYbje(ybje.add(headvo.getYbje()));
			
			//���Ƴ���ԭ�ҽ��Ϊ����ԭ�ҽ��
			UFDouble fyybje = bxcontrastVO.getFyybje();
			headvo.setCjkybje(fyybje.add(headvo.getCjkybje()));
			
		}
		
		return contrastmap.values().toArray(new JKBXHeaderVO[0]);
	}

}
