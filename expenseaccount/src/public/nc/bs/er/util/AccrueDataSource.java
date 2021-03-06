package nc.bs.er.util;

import java.util.List;

import nc.md.data.access.NCObject;
import nc.ui.pub.print.IDataSource;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

public class AccrueDataSource implements IDataSource{

	/**
	 * 表头前缀
	 */
	static String HEAD_PREFIX = "h_";

	/**
	 * 表体前缀
	 */
	static String BODY_PREFIX = "b_";

	String billId;

	String billtype;

	String checkman;

	AggAccruedBillVO vo;

	public AccrueDataSource(String billId, String billtype, String checkman,
			List<AggAccruedBillVO> voList) throws BusinessException {
		if (voList == null || voList.size() == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0013")/*@res "打印数据源传入的参数VO不能为空"*/);
		}
		vo = voList.get(0);
	}

	public AggAccruedBillVO getVo() {
//		return convertBX2JK(vo);
		return vo;
	}

//	private BXVO convertBX2JK(BXVO bxvo) {
//		if (bxvo.getParentVO().djdl.equals(BXConstans.JK_DJDL)) {
//			JKHeaderVO jkhead = new JKHeaderVO();
//			BXHeaderVO head = bxvo.getParentVO();
//			String[] attributeNames = head.getAttributeNames();
//			for (String attr : attributeNames) {
//				jkhead.setAttributeValue(attr, head.getAttributeValue(attr));
//			}
//			bxvo.setParentVO(jkhead);
//			BXBusItemVO[] bxBusItemVOS = bxvo.getBxBusItemVOS();
//			for (int i = 0; i < bxBusItemVOS.length; i++) {
//				bxBusItemVOS[i].setTablecode(BXConstans.BUS_PAGE_JK);
//			}
//			bxvo.setChildrenVO(bxBusItemVOS);
//		}
//		return getResetDigitVO(bxvo);
//	}

	public static String[] getBodyUFDoubleFields() {
		return new String[] {  AccruedDetailVO.ORG_AMOUNT };
	}

	public static String[] getHeadUFDoubleFields() {
		return new String[] { AccruedVO.ORG_AMOUNT };
	}

//	/**
//	 * 返回重设精度后的vo
//	 *
//	 * @author chendya
//	 * @param vo
//	 * @return
//	 */
//	private static JKBXVO getResetDigitVO(JKBXVO vo) {
//		//处理精度
//		new CurrencyControlBO().dealBXVOdigit(vo);
//		return vo;
//	}

	@Override
	public String[] getItemValuesByExpress(String express) {
		if (express.startsWith(BODY_PREFIX)) {
			express = express.replace(BODY_PREFIX, "");
		}
		if(express.indexOf(".") > 0){
			return getItemValuesByExpressBody(express);
		}
		if (express.startsWith(HEAD_PREFIX)) {
			express = express.replace(HEAD_PREFIX, "");
		}
		Object value = getVo().getParentVO().getAttributeValue(express);
		if (value != null) {
			if(value instanceof UFDate){
				return new String[] { ((UFDate)value).toStdString()};
			}else{
				return new String[] { value.toString() };
			}
		}
		return null;
	}

	/**
	 * 从元数据取值
	 * @param dataVO
	 * @param strFieldName
	 * @return
	 */
	private Object getEntityField(Object dataVO, String strFieldName) {
		NCObject nco = NCObject.newInstance(dataVO);
		return nco.getValueFromOwnObj(strFieldName);
	}

	/**
	 * 取表体值
	 *
	 * @param itemExpress
	 * @return
	 */
	public String[] getItemValuesByExpressBody(String itemExpress) {
		// 优先从元数据取值
		Object value = getEntityField(getVo(), itemExpress);
		itemExpress = itemExpress.replace(".", "#");
		String attribute = itemExpress.split("#")[1];
		if (value == null) {
			return null;
		} else if (value instanceof Object[]) {
			Object[] values = (Object[]) value;
			String[] strValues = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				if (values[i] == null) {
					continue;
				}
				strValues[i] = values[i].toString();
				if("defitem1".equals(attribute)||"defitem2".equals(attribute)){
					strValues[i] = strValues[i].substring(0,10);
				}
			}
			return strValues;
		} else {
			return new String[] { value.toString() };
		}
	}

	@Override
	public String[] getAllDataItemExpress() {

		return null;
	}

	@Override
	public String[] getAllDataItemNames() {
		return null;
	}

	@Override
	public String[] getDependentItemExpressByExpress(String arg0) {
		return null;
	}

	@Override
	public String getModuleName() {
		return null;
	}

	@Override
	public boolean isNumber(String arg0) {
		return false;
	}

}
