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
	 * 过滤出关联费用申请单的业务单据 根据事件类型，重设预占数和执行数
	 * 
	 * @param dataType
	 * @param mtBusiVoList
	 * @param isAdd
	 *            是否正向操作
	 * @param isContrast
	 *            是否冲销：只处理执行数
	 * @param headers
	 * @author: wangyhh@ufida.com.cn
	 */
	public static void fillBusiVo(String dataType, List<IMtappCtrlBusiVO> mtBusiVoList, boolean isAdd, boolean isContrast, JKBXHeaderVO[] headers) {
		for (JKBXHeaderVO jkbxHeaderVO : headers) {
			if (jkbxHeaderVO.getPk_item() == null) {
				continue;
			}
			
			JKBXMtappCtrlBusiVO busiVo = new JKBXMtappCtrlBusiVO(jkbxHeaderVO);
			//设置回写方向
			busiVo.setDirection(isAdd?IMtappCtrlBusiVO.Direction_positive:IMtappCtrlBusiVO.Direction_negative);
			//计算回写数据
			resetPre_ExeData(dataType,true, isContrast, busiVo);
			mtBusiVoList.add(busiVo);
		}
	}

	/**
	 * 设置预占数和执行数
	 * 
	 * 生效控制环节： 
	 * 	冲销：只处理执行数 
	 * 	保存操作，业务单据占费用申请单预占数 
	 * 	非保存操作，业务单据释放费用申请单预占数，占执行数 
	 * 保存控制环节：
	 * 	处理执行数
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
			// 回写冲借款预占数，需要使用冲借款费用原币金额
			exeData = busiVo.getFyyb_data();
		}
		if (!isAdd) {
			exeData = exeData.multiply(-1);
		}
		busiVo.setDatatype(dataType);
		busiVo.setAmount(exeData);
	}

	/**
	 * 查询回写执行数环节参数
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
