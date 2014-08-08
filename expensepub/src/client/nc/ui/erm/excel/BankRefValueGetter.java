package nc.ui.erm.excel;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import nc.bs.framework.common.NCLocator;
import nc.md.data.access.DASFacade;
import nc.md.innerservice.IMetaDataQueryService;
import nc.md.model.IBusinessEntity;
import nc.md.model.MetaDataException;
import nc.ui.pub.bill.BillItem;
import nc.ui.trade.excelimport.convertor.DefaultRefValueGetter;
import nc.vo.bd.meta.IBDObject;
import nc.vo.fipub.exception.ExceptionHandler;

/**
 * 银行账户子户导出Getter 银行账户子户导出时应导出对应银行账户的CODE 或name
 * 
 * @author chenshuaia
 * 
 */
public class BankRefValueGetter extends DefaultRefValueGetter {
	private String[] fields = null;

	@Override
	public Map<String, Object[]> getRefValue(BillItem billItem, List<String> pkList) {
		IBusinessEntity entity = billItem.getMetaDataProperty().getRefBusinessEntity();
		if (entity == null) {
			return null;
		}

		if (CollectionUtils.isEmpty(pkList)) {
			return null;
		}
		Map<String, Object[]> valueMap = DASFacade.getAttributeValues(entity, pkList.toArray(new String[0]), new String[] { "pk_bankaccbas" });
		try {
			IBusinessEntity bankAccEntrity = NCLocator.getInstance().lookup(IMetaDataQueryService.class).getBusinessEntityByName("uap", "bankaccount");
			fields = getPkCodeNameFields(bankAccEntrity);
			//这里认为参照只是单选
			valueMap = DASFacade.getAttributeValues(bankAccEntrity, new String[] { (String) valueMap.get("pk_bankaccbas")[0] }, fields);
			valueMap.put("pk_bankaccbas", pkList.toArray());
		} catch (MetaDataException e) {
			ExceptionHandler.consume(e);
		}

		return valueMap;
	}

	private String[] getPkCodeNameFields(IBusinessEntity entity) {
		Map<String, String> bizInterfaceMapInfo = entity.getBizInterfaceMapInfo(IBDObject.class.getName());
		String[] fields = new String[3];
		fields[0] = bizInterfaceMapInfo.get("id");
		fields[1] = bizInterfaceMapInfo.get("code");
		fields[2] = bizInterfaceMapInfo.get("name");
		return fields;
	}

	@Override
	public String[] getPkCodeAndNameFieldName() {
		return fields;
	}
}
