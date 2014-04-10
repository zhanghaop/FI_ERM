package nc.ui.arap.bx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.IBillListData;
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

public class BXUserdefitemContainerListPreparator implements IBillListData,
		IUserdefitemPreparator {
	private BXUserDefItemContainer container;
	private List<UserdefQueryParam> params; // UserDefItemContainer参数中定义的值(规则编码rulecode或元数据实体全名spacename.entityname)
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
	public void prepareBillListData(BillListData bld) {
		if (getParams() != null && getParams().size() > 0) {
			for (UserdefQueryParam param : getParams()) {
				UserdefitemVO[] items = null;
				String ruleCode = param.getRulecode();
				if (StringUtils.isNotEmpty(ruleCode)) {
					items = getContainer().getUserdefitemVOsByUserdefruleCode(
							ruleCode);
				} else {
					items = getContainer().getUserdefitemVOsByMDClassFullName(
							param.getMdfullname());
				}

				if (items != null && items.length > 0) {
					if (bld.isMeataDataTemplate()) {
						if (param.getPos() == IBillItem.BODY) {
							resetBodyMataDataByDef(bld, param.getTabcode(),
									param.getPrefix(), items);
						} else {
							resetHeadMetaDataByDef(bld, param.getPrefix(),
									items);
						}
					} else {
						if (param.getPos() == IBillItem.BODY) {
							resetBodyItemsByDef(bld, param.getTabcode(), param
									.getPrefix(), items);
						} else {
							resetHeadTailItemByDef(bld, param.getPrefix(),
									items);
						}
					}
				}
			}
		}
	}

	private void processBillItem(BillItem item, BillListData listData,
			String tabcode) {

		int dataType = item.getDataType();
		if (dataType == IBillItem.UFREF) {
			getRefBillItems().add(item);
		}
		if (dataType != IBillItem.UFREF && dataType != IBillItem.COMBO)
			return;

		BillModel billModel = null;
		if (!StringUtil.isEmptyWithTrim(tabcode))
			billModel = listData.getBodyBillModel(tabcode);
		else
			billModel = listData.getHeadBillModel();
		if (billModel == null)
			return;

		billModel.addBodyItemIndex(item.getKey() + IBillItem.ID_SUFFIX,
				billModel.getBodyColByKey(item.getKey()));
	}

	/**
	 * 根据用户定义属性重新设置表体BillItem.
	 * 
	 * @param listdata
	 * @param tabcode
	 * @param prefix
	 * @param defs
	 */
	private void resetBodyItemsByDef(BillListData listdata, String tabcode,
			String prefix, UserdefitemVO[] defs) {
		if (listdata == null || StringUtil.isEmptyWithTrim(prefix)
				|| defs == null)
			return;
		if (StringUtils.isBlank(tabcode))
			tabcode = listdata.getBodyBaseTableCodes()[0];
		for (int i = 0; i < defs.length; i++) {
			int index = defs[i].getPropindex();
			String key = prefix + index;
			BillItem item = listdata.getBodyItem(tabcode, key);
			if (item != null) {
				item.setIsDef(true);
				IMetaDataProperty prop = null;
				try {
					prop = MetaDataPropertyFactory
							.creatMetaDataUserDefPropertyByDefItemVO(item
									.getMetaDataProperty(), defs[i]);
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
					continue;
				}
				item.setName(prop.getName());
				item.setLength(prop.getInputLength());
				item.setDataType(prop.getDataType());
				item.setRefType(prop.getRefType());
				// item.setShow(true);
				if (item.getRefTypeSet() != null)
					item.getRefTypeSet().setReturnCode(false);
				processBillItem(item, listdata, tabcode);
			}
		}
	}

	/**
	 * 根据用户定义属性重新设置表体元数据属性.
	 * 
	 * @param listdata
	 * @param tabcode
	 * @param prefix
	 * @param defs
	 */
	private void resetBodyMataDataByDef(BillListData listdata, String tabcode,
			String prefix, UserdefitemVO[] defs) {
		if (listdata == null || StringUtil.isEmptyWithTrim(tabcode)
				|| StringUtil.isEmptyWithTrim(prefix) || defs == null)
			return;
		if (StringUtil.isEmptyWithTrim(tabcode))
			tabcode = listdata.getBodyBaseTableCodes()[0];
		for (int i = 0; i < defs.length; i++) {
			int index = defs[i].getPropindex();
			String key = prefix + index;
			BillItem item = listdata.getBodyItem(tabcode, key);
			if (item != null) {
				item.setIsDef(true);
				IMetaDataProperty prop = null;
				try {
					prop = MetaDataPropertyFactory
							.creatMetaDataUserDefPropertyByDefItemVO(item
									.getMetaDataProperty(), defs[i]);
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
					continue;
				}
				item.setMetaDataProperty(prop);
				// item.setShow(true);
				if (item.getRefTypeSet() != null)
					item.getRefTypeSet().setReturnCode(false);
				processBillItem(item, listdata, tabcode);
				
				//更改名称
				modifyShowName(listdata,defs[i],item,prop);
			}
		}
	}

	/**
	 * 根据用户定义属性重新设置表头BillItem.
	 * 
	 * @param listdata
	 * @param prefix
	 * @param defs
	 */
	private void resetHeadTailItemByDef(BillListData listdata, String prefix,
			UserdefitemVO[] defs) {
		if (listdata == null || StringUtil.isEmptyWithTrim(prefix)
				|| defs == null)
			return;
		for (int i = 0; i < defs.length; i++) {
			int index = defs[i].getPropindex();
			String key = prefix + index;
			BillItem item = listdata.getHeadItem(key);
			if (item != null) {
				item.setIsDef(true);
				IMetaDataProperty prop = null;
				try {
					prop = MetaDataPropertyFactory
							.creatMetaDataUserDefPropertyByDefItemVO(item
									.getMetaDataProperty(), defs[i]);
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
					continue;
				}
				item.setName(prop.getName());
				item.setLength(prop.getInputLength());
				item.setDataType(prop.getDataType());
				item.setRefType(prop.getRefType());
				// item.setShow(true);
				if (item.getRefTypeSet() != null)
					item.getRefTypeSet().setReturnCode(false);
				processBillItem(item, listdata, null);
				
				//更改名称
				modifyShowName(listdata,defs[i],item,prop);
			}
		}
	}

	/**
	 * 根据用户定义属性重新设置表头元数据属性.
	 * 
	 * @param listdata
	 * @param prefix
	 * @param defs
	 */
	private void resetHeadMetaDataByDef(BillListData listdata, String prefix,
			UserdefitemVO[] defs) {
		if (listdata == null || StringUtil.isEmptyWithTrim(prefix)
				|| defs == null)
			return;
		for (int i = 0; i < defs.length; i++) {
			int index = defs[i].getPropindex();
			// String key = usedefatts.get(index).getName();
			String key = prefix + index;
			BillItem item = listdata.getHeadItem(key);
			if (item != null) {
				item.setIsDef(true);
				IMetaDataProperty prop = null;
				try {
					prop = MetaDataPropertyFactory
							.creatMetaDataUserDefPropertyByDefItemVO(item
									.getMetaDataProperty(), defs[i]);
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
					continue;
				}
				item.setMetaDataProperty(prop);
				// item.setShow(true);
				if (item.getRefTypeSet() != null)
					item.getRefTypeSet().setReturnCode(false);
				processBillItem(item, listdata, null);
				
				//更改名称
				modifyShowName(listdata,defs[i],item,prop);
			}
		}
	}
	
	/**
	 * @author chendya
	 * 更改自定义项显示名称
	 * @param data
	 * @param defs
	 * @param item
	 */
	private void modifyShowName(BillListData data,UserdefitemVO defs,BillItem item,IMetaDataProperty prop){
		//模版显示名称
		String tplShowName = getTplShowName(data.getBillTempletVO(),item);
		
		//自定义项名称
		String userDefShowname = defs.getShowname();

		//元数据名称
		String metaShowname = prop.getShowName();
		
		//设置名称
		item.setName(tplShowName!=null?tplShowName:(userDefShowname!=null?userDefShowname:metaShowname));
	}
	
	/**
	 * 缓存单据模版vo中显示的名称
	 */
	Map<String,String> map = new HashMap<String,String>(); 
	
	/**
	 * 返回单据模版显示名称
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
		for (BillItem item : getRefBillItems()) {
			UIRefPane refPane = (UIRefPane) item.getComponent();
			if (refPane != null) {
				refPane.setPk_org(pk_org);
			}
		}
	}
}
