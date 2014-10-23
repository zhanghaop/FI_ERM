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
 * �������͵����ƽ̨ʵ����
 * 
 * @author lvhj
 *
 */
public class JkbxToFipHelper {
	/**
	 * @ ���ո��Լ����ƽ̨���� @
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
	 * ������ƽ̨�����ݣ� ���ţ���֯����Դϵͳ��ҵ�����ڣ�����PK���������� �Զ�������������ڻ��ƽ̨��Ҫչʾ����Ŀ
	 * 
	 * @param message
	 * */
	private void sendMessageToFip(JKBXHeaderVO headVO, JKBXVO bxvo, Object object, int message)
			throws BusinessException {

		FipRelationInfoVO reVO = new FipRelationInfoVO();
		// ����������ϢӦ�û��ƽ̨
		reVO.setPk_group(headVO.getPk_group());

		// 63�󴫻��ƾ֤��֧����λ�����д���
		reVO.setPk_org(headVO.getPk_payorg());
		reVO.setRelationID(headVO.getPk());
		reVO.setPk_system(BXConstans.ERM_PRODUCT_CODE_Lower);
		reVO.setBusidate(headVO.getJsrq() == null ? new UFDate() : headVO.getJsrq());
		reVO.setPk_billtype(headVO.getDjlxbm());
		reVO.setPk_operator(headVO.getOperator());
		reVO.setFreedef1(headVO.getDjbh());
		reVO.setFreedef2(headVO.getZy());
		UFDouble total = headVO.getYbje();

		// added by chendya ���ý���ֶεľ���
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
