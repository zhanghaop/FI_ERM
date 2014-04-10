package nc.bs.er.util;

import java.util.List;

import nc.itf.uap.busibean.SysinitAccessor;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXMtappCtrlBusiVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

public class WriteBackUtil {
	/**
	 * ���˳������������뵥��ҵ�񵥾� �����¼����ͣ�����Ԥռ����ִ����
	 * 
	 * @param dataType
	 * @param mtBusiVoList
	 * @param isAdd
	 *            �Ƿ��������
	 * @param isContrast
	 *            �Ƿ������ֻ����ִ����
	 * @param headers
	 * @author: wangyhh@ufida.com.cn
	 */
	public static void fillBusiVo(String dataType, List<IMtappCtrlBusiVO> mtBusiVoList, boolean isAdd, boolean isContrast, JKBXHeaderVO[] headers) {
		for (JKBXHeaderVO jkbxHeaderVO : headers) {
			if (jkbxHeaderVO.getPk_item() == null) {
				continue;
			}
			
			JKBXMtappCtrlBusiVO busiVo = new JKBXMtappCtrlBusiVO(jkbxHeaderVO);
			//���û�д����
			busiVo.setDirection(isAdd?IMtappCtrlBusiVO.Direction_positive:IMtappCtrlBusiVO.Direction_negative);
			//�����д����
			resetPre_ExeData(dataType,isAdd, isContrast, busiVo);
			mtBusiVoList.add(busiVo);
		}
	}

	/**
	 * ����Ԥռ����ִ����
	 * 
	 * ��Ч���ƻ��ڣ� 
	 * 	������ֻ����ִ���� 
	 * 	���������ҵ�񵥾�ռ�������뵥Ԥռ�� 
	 * 	�Ǳ��������ҵ�񵥾��ͷŷ������뵥Ԥռ����ռִ���� 
	 * ������ƻ��ڣ�
	 * 	����ִ����
	 * 
	 * @param dataType
	 * @param isAdd
	 * @param isContrast
	 * @param busiVo
	 * @author: wangyhh@ufida.com.cn
	 */
	private static void resetPre_ExeData(String dataType, boolean isAdd, boolean isContrast,JKBXMtappCtrlBusiVO busiVo) {
		UFDouble exeData = busiVo.getAmount();
		if (IMtappCtrlBusiVO.DataType_pre.equals(dataType)&&isContrast){
			// ��д����Ԥռ������Ҫʹ�ó������ԭ�ҽ��
			exeData = busiVo.getFyyb_data();
		}
		if (!isAdd) {
			exeData = exeData.multiply(-1);
		}
		busiVo.setDatatype(dataType);
		busiVo.setAmount(exeData);
	}

	/**
	 * ��ѯ��дִ�������ڲ���
	 * 
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	public static boolean getParamIsEffect(String pk_org) {
		boolean isEffect = true;
		try {
			String param = SysinitAccessor.getInstance().getParaString(pk_org, BXParamConstant.PARAM_MTAPP_CTRL);
			if (BXParamConstant.getMTAPP_CTRL_SAVE().equals(param)) {
				isEffect = false;
			}
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
		return isEffect;
	}
}
