package nc.ui.erm.accruedexpense.actions;

import java.util.HashMap;
import java.util.Map;

import nc.ui.arap.bx.print.ERMPrintDigitUtil;
import nc.ui.bd.pub.actions.print.MetaDataAllDatasSource;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.pub.BusinessException;

public class AccruedPrintMetaDataMultDataSource extends MetaDataAllDatasSource {
	private static final long serialVersionUID = 1L;

	@Override
	public Object[] getMDObjects() {
		Object[] result = super.getMDObjects();

		Map<String, String[]> fieldsMap = new HashMap<String, String[]>();

		fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_MONEY, new String[] { AccruedVO.AMOUNT,
				AccruedVO.REST_AMOUNT, AccruedVO.VERIFY_AMOUNT, AccruedVO.PREDICT_REST_AMOUNT });
		fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_LOCAL, new String[] { AccruedVO.ORG_AMOUNT,
				AccruedVO.ORG_REST_AMOUNT, AccruedVO.ORG_VERIFY_AMOUNT });

		try {
			result = ERMPrintDigitUtil.getDatas(result, fieldsMap, AccruedVO.PK_ORG, AccruedVO.PK_CURRTYPE);
		} catch (NumberFormatException e) {
			ExceptionHandler.consume(e);
		} catch (IllegalArgumentException e) {
			ExceptionHandler.consume(e);
		} catch (IllegalAccessException e) {
			ExceptionHandler.consume(e);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return result;
	}
}
