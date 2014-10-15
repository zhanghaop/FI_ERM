package nc.ui.erm.matterapp.actions.print;

import java.util.HashMap;
import java.util.Map;

import nc.ui.arap.bx.print.ERMPrintDigitUtil;
import nc.ui.bd.pub.actions.print.MetaDataAllDatasSource;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.BusinessException;

public class MatterAppMetaDataAllDatasSource extends MetaDataAllDatasSource {
	private static final long serialVersionUID = 1L;

	@Override
	public Object[] getMDObjects() {
		Object[] result = super.getMDObjects();
		Map<String, String[]> fieldsMap = new HashMap<String, String[]>();

		fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_MONEY, new String[] { MatterAppVO.ORIG_AMOUNT,
				MatterAppVO.REST_AMOUNT, MatterAppVO.EXE_AMOUNT, MatterAppVO.PRE_AMOUNT });
		fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_LOCAL, new String[] { MatterAppVO.ORG_AMOUNT,
				MatterAppVO.ORG_PRE_AMOUNT, MatterAppVO.ORG_REST_AMOUNT, MatterAppVO.ORG_EXE_AMOUNT });

		try {
			result = ERMPrintDigitUtil.getDatas(result, fieldsMap, MatterAppVO.PK_ORG, MatterAppVO.PK_CURRTYPE);
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
