package nc.ui.arap.bx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillData;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.editor.IUserdefitemPreparator;
import nc.ui.uif2.editor.UserdefQueryParam;
import nc.vo.bd.userdefrule.UserdefitemVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.bill.IMetaDataProperty;
import nc.vo.pub.bill.MetaDataPropertyFactory;

import org.apache.commons.lang.StringUtils;

public class BXUserdefitemContainerPreparator implements IBillData,
		IUserdefitemPreparator {

	private BXUserDefItemContainer container;
	private List<UserdefQueryParam> params; // UserDefItemContainer�����ж����ֵ(�������rulecode��Ԫ����ʵ��ȫ��spacename.entityname)
	private List<BillItem> refBillItems;

	public BXUserDefItemContainer getContainer() {
		return container;
	}

	public List<UserdefQueryParam> getParams() {
		return params;
	}

	@Override
	public List<BillItem> getRefBillItems() {
		if (refBillItems == null) {
			refBillItems = new ArrayList<BillItem>();
		}
		return refBillItems;
	}

	@Override
	public void prepareBillData(BillData billdata) {
		if (getParams() != null && getParams().size() > 0) {
			for (UserdefQueryParam param : getParams()) {
				UserdefitemVO[] items = null;
				String ruleCode = param.getRulecode();
				if (StringUtils.isNotEmpty(ruleCode)) {
					items = getContainer().getUserdefitemVOsByUserdefruleCode(
							ruleCode);
				} else {
					items = getContainer().getUserdefitemVOsByMDClassFullName(param.getRulecode());
				}
				
				if(items != null && items.length > 0) {
					if(billdata.isMeataDataTemplate()) {
						if (param.getPos() == IBillItem.BODY) {
							resetBodyMataDataByDef(billdata, param.getTabcode(),
									param.getPrefix(), items);
						} else {
							resetHeadTailMetaDataByDef(billdata, param.getPrefix(),
									items);
						}
					} else {
						if (param.getPos() == IBillItem.BODY) {
							resetBodyItemsByDef(billdata, param.getTabcode(), param
									.getPrefix(), items);
						} else {
							resetHeadTailItemByDef(billdata, param.getPrefix(),
									items);
						}
					}
				}
			}
		}
	}

	private void processBillItem(BillItem item, BillData billdata, String tabcode) {
		if (item.getDataType() == IBillItem.UFREF) {
			getRefBillItems().add(item);
		}
		
		if(StringUtil.isEmptyWithTrim(tabcode))
			return ;
		
		if(item.getDataType() == IBillItem.UFREF || item.getDataType() == IBillItem.COMBO)
		{
			BillModel billModel  = billdata.getBillModel(tabcode);
			if(billModel == null)
				return ;
			billModel.addBodyItemIndex(item.getKey() + IBillItem.ID_SUFFIX, billModel.getBodyColByKey(item.getKey()));
		}
	}

	/**
	 * �����û����������������ñ���BillItem.
	 * 
	 * @param data
	 * @param tabcode
	 * @param prefix
	 * @param defs
	 */
	private void resetBodyItemsByDef(BillData data, String tabcode,
			String prefix, UserdefitemVO[] defs) {
		if (data == null || StringUtils.isBlank(prefix) || defs == null)
			return;
		if (StringUtils.isBlank(tabcode))
			tabcode = data.getBodyBaseTableCodes()[0];
		for (int i = 0; i < defs.length; i++) {
			int index = defs[i].getPropindex();
			String key = prefix + index;
			BillItem item = data.getBodyItem(tabcode, key);
			if (item != null) {
				item.setIsDef(true);
				IMetaDataProperty prop = getMetaDataProperty(item, defs[i]);
				if(prop == null)
					continue;
				item.setName(prop.getName());
				item.setLength(prop.getInputLength());
				item.setDataType(prop.getDataType());
				item.setRefType(prop.getRefType());
				if(item.getRefTypeSet() != null)
					item.getRefTypeSet().setReturnCode(false);
				//item.setShow(true);
				processBillItem(item, data, tabcode);
				
				//��������
				modifyShowName(data,defs[i],item,prop);
			}
		}
	}

	/**
	 * �����û����������������ñ���Ԫ��������.
	 * 
	 * @param data
	 * @param tabcode
	 * @param prefix
	 * @param defs
	 */
	private void resetBodyMataDataByDef(BillData data, String tabcode,
			String prefix, UserdefitemVO[] defs) {
		
		if (data == null || StringUtils.isBlank(prefix) || defs == null)
			return;
		if (StringUtils.isBlank(tabcode))
			tabcode = data.getBodyBaseTableCodes()[0];
		
		for (int i = 0; i < defs.length; i++) {

			UserdefitemVO currentUserDef = defs[i];
			int index = currentUserDef.getPropindex();
			String key = prefix + index;
			BillItem item = data.getBodyItem(tabcode, key);
			if (item != null) {
				IMetaDataProperty prop = getMetaDataProperty(item, currentUserDef);
				if(prop == null)
					continue;
				item.setIsDef(true);
				item.setMetaDataProperty(prop);
				//item.setShow(true);
				if(item.getRefTypeSet() != null)
					item.getRefTypeSet().setReturnCode(false);
				processBillItem(item, data, tabcode);
				
				//��������
				modifyShowName(data,defs[i],item,prop);
			} 
		}
	}
	
	private IMetaDataProperty getMetaDataProperty(BillItem item, UserdefitemVO currentUserDef)
	{
		try {
			IMetaDataProperty prop = MetaDataPropertyFactory
					.creatMetaDataUserDefPropertyByDefItemVO(item
							.getMetaDataProperty(), currentUserDef);
			return prop;
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
			return null;
		}
	}

	private void resetHeadTailItemByDef(BillData data, String prefix,
			UserdefitemVO[] defs) {
		if (data == null || StringUtils.isBlank(prefix) || defs == null)
			return;
		for (int i = 0; i < defs.length; i++) {
			int index = defs[i].getPropindex();
			String key = prefix + index;
			BillItem item = data.getHeadTailItem(key);
			if (item != null) {
				IMetaDataProperty prop = getMetaDataProperty(item, defs[i]);
				if(prop == null)
					continue;
				item.setIsDef(true);
				item.setName(prop.getName());
				item.setLength(prop.getInputLength());
				item.setDataType(prop.getDataType());
				item.setRefType(prop.getRefType());
				//item.setShow(true);
				processBillItem(item, data, null);
				
				//��������
				modifyShowName(data,defs[i],item,prop);
			}
		} 
	}

	private void resetHeadTailMetaDataByDef(BillData data, String prefix,
			UserdefitemVO[] defs) {
		if (data == null || StringUtils.isBlank(prefix) || defs == null)
			return;
		for (int i = 0; i < defs.length; i++) {
			int index = defs[i].getPropindex();
			String key = prefix + index;
			BillItem item = data.getHeadTailItem(key);
			if (item != null) {
				item.setIsDef(true);
				IMetaDataProperty prop = getMetaDataProperty(item, defs[i]);
				if(prop == null)
					continue;
				item.setMetaDataProperty(prop);
				//item.setShow(true);
				processBillItem(item, data, null);
				
				//��������
				modifyShowName(data,defs[i],item,prop);
			}
		}
	}
	
	/**
	 * @author chendya
	 * �����Զ�������ʾ����
	 * @param data
	 * @param defs
	 * @param item
	 */
	private void modifyShowName(BillData data,UserdefitemVO defs,BillItem item,IMetaDataProperty prop){
		//begin--V6������޸� modified by chendya �Ǳ���Ĳ����������⴦�� 
		if ((!(item.getPos() == IBillItem.BODY)) && item.getDataType() == IBillItem.BOOLEAN) {
			item.setName("");
			return;
		}
		
		//ģ����ʾ����
		String tplShowName = getTplShowName(data.getBillTempletVO(),item);
		
		//�Զ���������
		String userDefShowname = defs.getShowname();

		//Ԫ��������
		String metaShowname = prop.getShowName();
		
		//��������
		item.setName(tplShowName!=null?tplShowName:(userDefShowname!=null?userDefShowname:metaShowname));
	}
	
	/**
	 * ���浥��ģ��vo����ʾ������
	 */
	Map<String,String> map = new HashMap<String,String>(); 
	
	/**
	 * ���ص���ģ����ʾ����
	 * @param billTempletVO
	 * @param item
	 * @return
	 */
	private String getTplShowName(BillTempletVO billTempletVO,BillItem item){
		final String key = item.getKey();
		BillTempletBodyVO[] bodyVOs = billTempletVO.getBodyVO();
		for (int i = 0; i < bodyVOs.length; i++) {
			if (key.equals(bodyVOs[i].getItemkey()) && !map.containsKey(key)) {
				map.put(key, bodyVOs[i].getDefaultshowname());
			}
		}
		return map.get(key);
	}

	public void setContainer(BXUserDefItemContainer container) {
		this.container = container;
	}

	public void setParams(List<UserdefQueryParam> params) {
		this.params = params;
	}

	@Override
	public void setPkorgForRefItems(String pk_org) {
		for(BillItem item : getRefBillItems()) {
			UIRefPane refPane = (UIRefPane) item.getComponent();
			if(refPane != null) {
				refPane.setPk_org(pk_org);
			}
		}
	}
}

