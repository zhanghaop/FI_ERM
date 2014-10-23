package nc.bs.arap.bx;

import nc.bs.er.callouter.FipCallFacade;
import nc.bs.er.util.FipUtil;
import nc.itf.fi.pub.Currency;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * 借款报销发送到会计平台实现类
 * 
 * @author lvhj
 *
 */
public class JkbxToFipHelper {
	/**
	 * @ 与收付以及会计平台交互 @
	 * */

	public void sendMessage(JKBXVO vo, int message) throws BusinessException {
		try {
			JKBXHeaderVO headVO = VOFactory.createHeadVO(vo.getParentVO().getDjdl());
			headVO = vo.getParentVO();

			AggregatedValueObject object = createJkbxToFIPVO(vo, message);

			sendMessageToFip(headVO, vo, object, message);
		} catch (BusinessException e) {

			ExceptionHandler.handleException(e);

		}
	}

	public static AggregatedValueObject createJkbxToFIPVO(JKBXVO vo, int message) throws BusinessException {
		AggregatedValueObject object = null;

		JKBXVO bxvo = ErVOUtils.prepareBxvoHeaderToItemClone(vo);

		if (message != FipMessageVO.MESSAGETYPE_DEL) {
			object = new FipUtil().addOtherInfo(bxvo);
		}
		return object;
	}

	/**
	 * 传入会计平台的数据： 集团，组织，来源系统，业务日期，单据PK，单据类型 自定义项：报销管理在会计平台需要展示的项目
	 * 
	 * @param message
	 * */
	private void sendMessageToFip(JKBXHeaderVO headVO, JKBXVO bxvo, Object object, int message)
			throws BusinessException {

		FipRelationInfoVO reVO = new FipRelationInfoVO();
		// 具体设置信息应用会计平台
		reVO.setPk_group(headVO.getPk_group());

		// 63后传会计凭证按支付单位来进行处理
		reVO.setPk_org(headVO.getPk_payorg());
		reVO.setRelationID(headVO.getPk());
		reVO.setPk_system(BXConstans.ERM_PRODUCT_CODE_Lower);
		reVO.setBusidate(headVO.getJsrq() == null ? new UFDate() : headVO.getJsrq());
		reVO.setPk_billtype(headVO.getDjlxbm());
		reVO.setPk_operator(headVO.getOperator());
		reVO.setFreedef1(headVO.getDjbh());
		reVO.setFreedef2(headVO.getZy());
		UFDouble total = headVO.getYbje();

		// added by chendya 设置金额字段的精度
		total = total.setScale(Currency.getCurrDigit(headVO.getBzbm()), UFDouble.ROUND_HALF_UP);
		reVO.setFreedef3(String.valueOf(total));

		FipMessageVO messageVO = new FipMessageVO();
		messageVO.setBillVO(object);
		messageVO.setMessagetype(message);
		messageVO.setMessageinfo(reVO);
		try {
			new FipCallFacade().sendMessage(messageVO);
		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);

		}
	}
}
