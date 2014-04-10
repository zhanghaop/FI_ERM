package nc.vo.arap.bx.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nc.itf.fi.pub.Currency;
import nc.itf.org.IOrgConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.BxDetailLinkQueryVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.StringUtils;

/**
 *
 * �������ָ�ʽ����
 * @author liaobx
 *
 */
/**
 * @author liaobx
 *
 */
public final class CurrencyControlBO {

	Map<String , DigitObj> field2digit = new HashMap<String, DigitObj>();

	DigitObj ori = new DigitObj(DigitType.OriCurr);//ԭ�Ҿ���
	DigitObj curr = new DigitObj(DigitType.LocalCurr);//���Ҿ���
	DigitObj rate = new DigitObj(DigitType.LocalRate);//���ʾ���
	DigitObj grcurr = new DigitObj(DigitType.GroupCurr);//���ű��Ҿ���
	DigitObj grrate = new DigitObj(DigitType.GroupRate);//���Ż��ʾ���
	DigitObj glcurr = new DigitObj(DigitType.GlobalCurr);//ȫ�ֱ��Ҿ���
	DigitObj glrate = new DigitObj(DigitType.GlobalRate);//ȫ�ֻ��ʾ���

	{
		field2digit.put(JKBXHeaderVO.YBJE, ori);//ԭ�ҽ��
		field2digit.put(JKBXHeaderVO.TOTAL, ori);//���ܽ��
		field2digit.put(JKBXHeaderVO.YBYE, ori);//ԭ�����
		field2digit.put(JKBXHeaderVO.YJYE, ori);//Ԥ�ƽ��

		field2digit.put(JKBXHeaderVO.BBJE, curr);//���ҽ��
		field2digit.put(JKBXHeaderVO.BBYE, curr);//�������
		field2digit.put(JKBXHeaderVO.BBHL, rate);//���һ���

		field2digit.put(JKBXHeaderVO.GROUPBBJE, grcurr);//���Ž��
		field2digit.put(JKBXHeaderVO.GROUPBBYE, grcurr);//�������
		field2digit.put(JKBXHeaderVO.GROUPBBHL, grrate);//���ű��һ���

		field2digit.put(JKBXHeaderVO.GLOBALBBJE, glcurr);//ȫ�ֽ��
		field2digit.put(JKBXHeaderVO.GLOBALBBYE, glcurr);//ȫ�����
		field2digit.put(JKBXHeaderVO.GLOBALBBHL, glrate);//ȫ�ֱ��һ���

		field2digit.put(BXBusItemVO.YBJE, ori);//ԭ�ҽ��
		field2digit.put(BXBusItemVO.CJKYBJE, ori);//������
		field2digit.put(BXBusItemVO.ZFYBJE, ori);//֧�����
		field2digit.put(BXBusItemVO.HKYBJE, ori);//������

		field2digit.put(BXBusItemVO.GROUPBBJE, grcurr);//���ű��ҽ��
		field2digit.put(BXBusItemVO.GROUPBBYE, grcurr);//���ű������
		field2digit.put(BXBusItemVO.GROUPHKBBJE, grcurr);//���Ż���ҽ��
		field2digit.put(BXBusItemVO.GROUPZFBBJE, grcurr);//����֧�����ҽ��
		field2digit.put(BXBusItemVO.GROUPCJKBBJE, grcurr);//���ų���ҽ��

		field2digit.put(BXBusItemVO.GLOBALBBJE, glcurr);//ȫ�ֱ��ҽ��
		field2digit.put(BXBusItemVO.GLOBALBBYE, glcurr);//ȫ�ֱ������
		field2digit.put(BXBusItemVO.GLOBALHKBBJE, glcurr);//ȫ�ֻ���ҽ��
		field2digit.put(BXBusItemVO.GLOBALZFBBJE, glcurr);//ȫ��֧�����ҽ��
		field2digit.put(BXBusItemVO.GLOBALCJKBBJE, glcurr);//ȫ�ֳ���ҽ��


		field2digit.put(BXBusItemVO.AMOUNT, ori);//���

		field2digit.put(BxcontrastVO.BBJE, curr);//
		field2digit.put(BxcontrastVO.CJKBBJE, curr);//
		field2digit.put(BxcontrastVO.CJKYBJE, curr);//
		field2digit.put(BxcontrastVO.FYYBJE, ori);//
		field2digit.put(BxcontrastVO.HKYBJE, ori);//
		field2digit.put(BxcontrastVO.GROUPFYBBJE,grcurr);//���ŷ��ñ��ҽ��
		field2digit.put(BxcontrastVO.GROUPBBJE, grcurr);//
		field2digit.put(BxcontrastVO.GROUPCJKBBJE, grcurr);//���ų���ҽ��
		field2digit.put(BxcontrastVO.GLOBALFYBBJE, glcurr);//ȫ�ַ��ñ��ҽ��
		field2digit.put(BxcontrastVO.GLOBALBBJE, glcurr);//
		field2digit.put(BxcontrastVO.GLOBALCJKBBJE, glcurr);//ȫ�ֳ���ҽ��


		//֧�����龫��
		field2digit.put(BxDetailLinkQueryVO.ORI, ori);//ԭ�ҽ��
		field2digit.put(BxDetailLinkQueryVO.LOC, curr);//���ҽ��
		field2digit.put(BxDetailLinkQueryVO.GR_LOC, grcurr);//���Ž��
		field2digit.put(BxDetailLinkQueryVO.GL_LOC, glcurr);//ȫ�ֳ���ҽ��
	}
	//�������� ������ֵ�����ݽṹ
	class DigitObj {
		DigitType type;
		int digit= 2;
		DigitObj(DigitType type) {
			this.type = type;
		}
	}
	//��������
	enum DigitType {
		OriCurr("oriCurrtypeDigit"), LocalCurr("LocalCurrtypeDigit"), LocalRate("localRateDigi"),

		GroupCurr("GroupCurrtypeDigit"), GroupRate("GroupRateDigi"),

		GlobalCurr("GlobalCurrtypeDigit"), GlobalRate("GlobalRateDigi");
		String value = null;

		DigitType(String type) {
			this.value = type;
		}
	}


	/**
	 * ����������
	 * @param bxvo ��������
	 */
	public void dealBXVOdigit(JKBXVO bxvo) {
		
		if(bxvo == null || bxvo.getParentVO() == null){
			return;
		}
		
		init(bxvo.getParentVO());

		//��ͷ
		fmtHeadVO(bxvo.getParentVO());
		// ҵ����Ϣ
		for (BXBusItemVO busi : bxvo.getBxBusItemVOS() == null ? new BXBusItemVO[0] : bxvo.getBxBusItemVOS()) {
			fmtBusiItemVO(busi);
		}
		// ������Ϣ
		for (BxcontrastVO busi : bxvo.getContrastVO() == null ? new BxcontrastVO[0] : bxvo.getContrastVO()) {
			fmtContrastItemVO(busi);
		}
	}
	/**
	 * ����������
	 * @param linkqryVO ��������
	 */
	public void dealLinkQuerydigit(BxDetailLinkQueryVO linkqryVO) {
		init(linkqryVO);
		if(linkqryVO == null){
			throwException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0008")/*@res "������Ϣ����Ϊ��"*/);
		}
		format(linkqryVO);
	}

	//��ʼ��
	private void init(SuperVO headvo) {
		String bzbm = (String) headvo.getAttributeValue(JKBXHeaderVO.BZBM);
		//����� �ֶ� bzbm�����ֱ��룩/pk_currtype ����
		bzbm  = StringUtils.isEmpty(bzbm)? (String) headvo.getAttributeValue(BxDetailLinkQueryVO.PK_CURRTYPE) : bzbm;

		ori.digit = Currency.getCurrDigit(bzbm);

		// TODO û�жԻ��ʷ������д���
		try {
			String pk_org = (String) headvo.getAttributeValue(JKBXHeaderVO.PK_ORG);
			String pk = Currency.getLocalCurrPK(pk_org);
			curr.digit = Currency.getCurrDigit(pk);
			rate.digit = Currency.getRateDigit(pk_org, bzbm, pk);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}

		try {
			String pk_group = (String) headvo.getAttributeValue(JKBXHeaderVO.PK_GROUP);
			String pk = Currency.getGroupLocalCurrPK(pk_group);
			grcurr.digit = Currency.getCurrDigit(pk);
			grrate.digit = Currency.getRateDigit(pk_group,  bzbm, pk);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}

		try {
			String pk = Currency.getGlobalCurrPk(null);
			glcurr.digit = Currency.getCurrDigit(pk);
			glrate.digit = Currency.getRateDigit(IOrgConst.GLOBEORG, bzbm, pk);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}


	// �����ͷ��Ϣ����
	private void fmtHeadVO(JKBXHeaderVO head) {
		if(head == null){
			throwException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0009")/*@res "��ͷ����Ϊ��"*/);
		}
		format(head);
	}

	// ����ҵ����Ϣ����
	private void fmtBusiItemVO(BXBusItemVO item){
		if(item == null){
			throwException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0010")/*@res "����ҵ����Ϣ����Ϊ��"*/);
		}
		format(item);
	}
	// ������������𾫶�
	private void fmtContrastItemVO(BxcontrastVO item){
		if(item == null){
			throwException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0011")/*@res "����������Ϣ����Ϊ��"*/);
		}
		format(item);
	}

	//�쳣����
	private void throwException(String msg) throws BusinessRuntimeException{
		throw new BusinessRuntimeException(msg);
	}

	private void format(SuperVO vo) {
		Set<String> keySet = field2digit.keySet();
		for(String attr : vo.getAttributeNames()){
			if(! keySet.contains(attr)) continue;
			UFDouble value = (UFDouble) vo.getAttributeValue(attr);
			DigitObj obj = field2digit.get(attr);
			vo.setAttributeValue(attr, value == null ? null : value.setScale(obj.digit, UFDouble.ROUND_HALF_UP));
		}
	}


}