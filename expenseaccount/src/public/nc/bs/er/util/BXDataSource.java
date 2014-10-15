package nc.bs.er.util;

import java.util.List;

import nc.md.data.access.NCObject;
import nc.ui.pub.print.IDataSource;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

@SuppressWarnings("serial")
public class BXDataSource implements IDataSource {

	/**
	 * ��ͷǰ׺
	 */
	static String HEAD_PREFIX = "h_";

	/**
	 * ����ǰ׺
	 */
	static String BODY_PREFIX = "b_";

	String billId;

	String billtype;

	String checkman;

	JKBXVO vo;

	public BXDataSource(String billId, String billtype, String checkman,
			List<JKBXVO> voList) throws BusinessException {
		if (voList == null || voList.size() == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0013")/*@res "��ӡ����Դ����Ĳ���VO����Ϊ��"*/);
		}
		vo = voList.get(0);
	}

	public JKBXVO getVo() {
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
		return new String[] { BXBusItemVO.AMOUNT };
	}

	public static String[] getHeadUFDoubleFields() {
		return new String[] { JKBXHeaderVO.TOTAL };
	}

//	/**
//	 * �������辫�Ⱥ��vo
//	 *
//	 * @author chendya
//	 * @param vo
//	 * @return
//	 */
//	private static JKBXVO getResetDigitVO(JKBXVO vo) {
//		//������
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
			//Ӧ���Ե���������ʾ����Ϊ19λ��Ӧ�ø�Ϊ10λ��������,���д������⴦��
			if(value instanceof UFDate){
				return new String[] { ((UFDate)value).toStdString()};
			}else{
				return new String[] { value.toString() };
			}
		}
		return null;
	}

	/**
	 * ��Ԫ����ȡֵ
	 * @param dataVO
	 * @param strFieldName
	 * @return
	 */
	private Object getEntityField(Object dataVO, String strFieldName) {
		NCObject nco = NCObject.newInstance(dataVO);
		return nco.getValueFromOwnObj(strFieldName);
	}

	/**
	 * ȡ����ֵ
	 *
	 * @param itemExpress
	 * @return
	 */
	public String[] getItemValuesByExpressBody(String itemExpress) {
		// ���ȴ�Ԫ����ȡֵ
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
				//begin--added by chendya Ӧ��������������ڣ�����������ʾ����Ӧ����ʾ10λ��������Ҳȷ��ͬ���޸ģ����������⴦��
				if("defitem1".equals(attribute)||"defitem2".equals(attribute)){
					strValues[i] = strValues[i].substring(0,10);
				}
				//--end
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