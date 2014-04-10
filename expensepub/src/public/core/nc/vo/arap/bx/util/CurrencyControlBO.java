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
 * 报销币种格式化类
 * @author liaobx
 *
 */
/**
 * @author liaobx
 *
 */
public final class CurrencyControlBO {

	Map<String , DigitObj> field2digit = new HashMap<String, DigitObj>();

	DigitObj ori = new DigitObj(DigitType.OriCurr);//原币精度
	DigitObj curr = new DigitObj(DigitType.LocalCurr);//本币精度
	DigitObj rate = new DigitObj(DigitType.LocalRate);//汇率精度
	DigitObj grcurr = new DigitObj(DigitType.GroupCurr);//集团本币精度
	DigitObj grrate = new DigitObj(DigitType.GroupRate);//集团汇率精度
	DigitObj glcurr = new DigitObj(DigitType.GlobalCurr);//全局本币精度
	DigitObj glrate = new DigitObj(DigitType.GlobalRate);//全局汇率精度

	{
		field2digit.put(JKBXHeaderVO.YBJE, ori);//原币金额
		field2digit.put(JKBXHeaderVO.TOTAL, ori);//汇总金额
		field2digit.put(JKBXHeaderVO.YBYE, ori);//原币余额
		field2digit.put(JKBXHeaderVO.YJYE, ori);//预计金额

		field2digit.put(JKBXHeaderVO.BBJE, curr);//本币金额
		field2digit.put(JKBXHeaderVO.BBYE, curr);//本币余额
		field2digit.put(JKBXHeaderVO.BBHL, rate);//本币汇率

		field2digit.put(JKBXHeaderVO.GROUPBBJE, grcurr);//集团金额
		field2digit.put(JKBXHeaderVO.GROUPBBYE, grcurr);//集团余额
		field2digit.put(JKBXHeaderVO.GROUPBBHL, grrate);//集团本币汇率

		field2digit.put(JKBXHeaderVO.GLOBALBBJE, glcurr);//全局金额
		field2digit.put(JKBXHeaderVO.GLOBALBBYE, glcurr);//全局余额
		field2digit.put(JKBXHeaderVO.GLOBALBBHL, glrate);//全局本币汇率

		field2digit.put(BXBusItemVO.YBJE, ori);//原币金额
		field2digit.put(BXBusItemVO.CJKYBJE, ori);//冲借款金额
		field2digit.put(BXBusItemVO.ZFYBJE, ori);//支付金额
		field2digit.put(BXBusItemVO.HKYBJE, ori);//还款金额

		field2digit.put(BXBusItemVO.GROUPBBJE, grcurr);//集团本币金额
		field2digit.put(BXBusItemVO.GROUPBBYE, grcurr);//集团本币余额
		field2digit.put(BXBusItemVO.GROUPHKBBJE, grcurr);//集团还款本币金额
		field2digit.put(BXBusItemVO.GROUPZFBBJE, grcurr);//集团支付本币金额
		field2digit.put(BXBusItemVO.GROUPCJKBBJE, grcurr);//集团冲借款本币金额

		field2digit.put(BXBusItemVO.GLOBALBBJE, glcurr);//全局本币金额
		field2digit.put(BXBusItemVO.GLOBALBBYE, glcurr);//全局本币余额
		field2digit.put(BXBusItemVO.GLOBALHKBBJE, glcurr);//全局还款本币金额
		field2digit.put(BXBusItemVO.GLOBALZFBBJE, glcurr);//全局支付本币金额
		field2digit.put(BXBusItemVO.GLOBALCJKBBJE, glcurr);//全局冲借款本币金额


		field2digit.put(BXBusItemVO.AMOUNT, ori);//金额

		field2digit.put(BxcontrastVO.BBJE, curr);//
		field2digit.put(BxcontrastVO.CJKBBJE, curr);//
		field2digit.put(BxcontrastVO.CJKYBJE, curr);//
		field2digit.put(BxcontrastVO.FYYBJE, ori);//
		field2digit.put(BxcontrastVO.HKYBJE, ori);//
		field2digit.put(BxcontrastVO.GROUPFYBBJE,grcurr);//集团费用本币金额
		field2digit.put(BxcontrastVO.GROUPBBJE, grcurr);//
		field2digit.put(BxcontrastVO.GROUPCJKBBJE, grcurr);//集团冲借款本币金额
		field2digit.put(BxcontrastVO.GLOBALFYBBJE, glcurr);//全局费用本币金额
		field2digit.put(BxcontrastVO.GLOBALBBJE, glcurr);//
		field2digit.put(BxcontrastVO.GLOBALCJKBBJE, glcurr);//全局冲借款本币金额


		//支持联查精度
		field2digit.put(BxDetailLinkQueryVO.ORI, ori);//原币金额
		field2digit.put(BxDetailLinkQueryVO.LOC, curr);//本币金额
		field2digit.put(BxDetailLinkQueryVO.GR_LOC, grcurr);//集团金额
		field2digit.put(BxDetailLinkQueryVO.GL_LOC, glcurr);//全局冲借款本币金额
	}
	//精度类型 ，精度值的数据结构
	class DigitObj {
		DigitType type;
		int digit= 2;
		DigitObj(DigitType type) {
			this.type = type;
		}
	}
	//精度类型
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
	 * 处理报销精度
	 * @param bxvo 报销类型
	 */
	public void dealBXVOdigit(JKBXVO bxvo) {
		
		if(bxvo == null || bxvo.getParentVO() == null){
			return;
		}
		
		init(bxvo.getParentVO());

		//表头
		fmtHeadVO(bxvo.getParentVO());
		// 业务信息
		for (BXBusItemVO busi : bxvo.getBxBusItemVOS() == null ? new BXBusItemVO[0] : bxvo.getBxBusItemVOS()) {
			fmtBusiItemVO(busi);
		}
		// 冲销信息
		for (BxcontrastVO busi : bxvo.getContrastVO() == null ? new BxcontrastVO[0] : bxvo.getContrastVO()) {
			fmtContrastItemVO(busi);
		}
	}
	/**
	 * 处理报销精度
	 * @param linkqryVO 报销类型
	 */
	public void dealLinkQuerydigit(BxDetailLinkQueryVO linkqryVO) {
		init(linkqryVO);
		if(linkqryVO == null){
			throwException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0008")/*@res "联查信息不能为空"*/);
		}
		format(linkqryVO);
	}

	//初始化
	private void init(SuperVO headvo) {
		String bzbm = (String) headvo.getAttributeValue(JKBXHeaderVO.BZBM);
		//适配多 字段 bzbm（币种编码）/pk_currtype 币种
		bzbm  = StringUtils.isEmpty(bzbm)? (String) headvo.getAttributeValue(BxDetailLinkQueryVO.PK_CURRTYPE) : bzbm;

		ori.digit = Currency.getCurrDigit(bzbm);

		// TODO 没有对汇率方案进行处理
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


	// 处理表头信息精度
	private void fmtHeadVO(JKBXHeaderVO head) {
		if(head == null){
			throwException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0009")/*@res "表头不能为空"*/);
		}
		format(head);
	}

	// 处理业务信息精度
	private void fmtBusiItemVO(BXBusItemVO item){
		if(item == null){
			throwException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0010")/*@res "处理业务信息不能为空"*/);
		}
		format(item);
	}
	// 处理借款报销冲销金精度
	private void fmtContrastItemVO(BxcontrastVO item){
		if(item == null){
			throwException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0011")/*@res "报销冲销信息不能为空"*/);
		}
		format(item);
	}

	//异常处理
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