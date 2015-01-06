package nc.ui.erm.billpub.view.eventhandler;

import java.util.ArrayList;
import java.util.List;

import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.fipub.exception.ExceptionHandler;
/**
 * 初始化不同单位归属的字段
 * @author wangled
 *
 */
public class HeadAfterEditUtil {
	
	private ErmBillBillForm editor = null;
	public HeadAfterEditUtil(ErmBillBillForm editor) {
		super();
		this.editor = editor;
	}
	

	private BillCardPanel getBillCardPanel() {
		return editor.getBillCardPanel();
	}
	
	public String getHeadItemStrValue(String itemKey) {
		BillItem headItem = getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? null : (String) headItem.getValueObject();
	}
	
	/**
	 * 初始化不同单位归属的字段
	 */
	public  void initPayentityItems(boolean isEdit) {
		initItemsBelong(editor.getOrgRefFields(JKBXHeaderVO.PK_ORG), editor.getAllOrgRefFields(), JKBXHeaderVO.PK_ORG, null,isEdit);
	}

	public  void initUseEntityItems(boolean isEdit) {
		initItemsBelong(editor.getOrgRefFields(JKBXHeaderVO.DWBM), editor.getAllOrgRefFields(), JKBXHeaderVO.DWBM, null,isEdit);
	}

	public  void initCostentityItems(boolean isEdit) {
		initItemsBelong(editor.getOrgRefFields(JKBXHeaderVO.FYDWBM), editor.getAllOrgRefFields(), JKBXHeaderVO.FYDWBM, null,isEdit);
	}
	
	public void initPayorgentityItems(boolean isEdit){
		initItemsBelong(editor.getOrgRefFields(JKBXHeaderVO.PK_PAYORG), editor.getAllOrgRefFields(), JKBXHeaderVO.PK_PAYORG, null, isEdit);
	}
	
	/**
	 * 分别处理属于借款报销单位、费用承担单位和借款人单位
	 * @param costentity_billitems
	 * @param allitems
	 * @param key
	 * @param fydwbm:包括pk_org;fydwbm;dwbm
	 * @param isEdit
	 */
	public void initItemsBelong(List<String> costentity_billitems,
			List<String> allitems, String key, Object fydwbm, boolean isEdit) {
		if (fydwbm == null){
			fydwbm = getHeadValue(key);
		}
		String fyPkCorp = fydwbm == null ? null : fydwbm.toString();
		for (String item : costentity_billitems) {
			if (item.equals(key)) {
				continue;
			}

			BillItem[] headItems = getItemsById(item);
			if (headItems == null)
				continue;

			for (BillItem headItem : headItems) {
				if (headItem == null)
					continue;
				String refType = headItem.getRefType();
				if (refType != null && !refType.equals("") && headItem.getComponent() != null
						&& headItem.getComponent() instanceof UIRefPane) {
					try {
						UIRefPane ref = (UIRefPane) headItem.getComponent();
						// 如果单据模板上设置这个字段是不可以编辑，或者字段所参照的对应组织字段为空时
						// 除去常用单据集团级
						boolean isInitGroup = false;
						isInitGroup = ((ErmBillBillManageModel) editor.getModel()).getContext().getNodeCode()
								.equals(BXConstans.BXINIT_NODECODE_G);
						if (!isInitGroup && (fyPkCorp == null || fyPkCorp.equals("")) || !headItem.isEnabled()) {
							ref.setEnabled(false);
							// 将组织所关联的字段设置为空
						} else {
							if (!ref.isEnabled()) {
								ref.setEnabled(true);
							}
						}
						
						if((fyPkCorp == null || fyPkCorp.equals(""))){
							if (!JKBXHeaderVO.ZY.equals(headItem.getKey())) {
								headItem.setValue(null);
							}
						}
						
						AbstractRefModel model = ref.getRefModel();
						if (model != null) {
							/** 用来设置卡片表头字段的参照过滤 */
							model.setPk_org((String) fyPkCorp);
						}
						if (isEdit) {
							if (headItem.getPos() == IBillItem.HEAD) {
								if (!JKBXHeaderVO.ZY.equals(headItem.getKey())) {
									headItem.setValue(null);
								}
							} else if (headItem.getPos() == IBillItem.BODY) {
								String tableCode = headItem.getTableCode();
								int rowCount = getBillCardPanel().getBillModel(tableCode).getRowCount();
								for (int i = 0; i < rowCount; i++) {
									getBillCardPanel().setBodyValueAt(null, i, headItem.getKey(), tableCode);
								}
							}
						}
					} catch (ClassCastException e) {
						ExceptionHandler.consume(e);
					}
				}
			}
		}
		
		if (!isEdit) {//在onAdd和onEdit时，初始化bodyItem的过滤
			String[] tables = getBillCardPanel().getBillData().getBodyTableCodes();
			for (String tab : tables) {
				//过滤掉分摊页签
				if(tab != null && tab.equals(BXConstans.CSHARE_PAGE)){
					continue;
				}
				
				BillItem[] bodyItems = getBillCardPanel().getBillData()
						.getBodyShowItems(tab);
				if (bodyItems == null)
					continue;
				List<BillItem> list = new ArrayList<BillItem>();
				for (BillItem bodyItem : bodyItems) {
					// 属于本单位的字段
					boolean flag = costentity_billitems.contains(bodyItem.getKey())
							|| (bodyItem.getIDColName() != null && costentity_billitems.contains(bodyItem
									.getIDColName()));
					
					// 包涵3个单位的字段
					boolean fflag = allitems.contains(bodyItem.getKey())
							|| (bodyItem.getIDColName() != null && allitems.contains(bodyItem.getIDColName()));
					if (flag || (key.equals(JKBXHeaderVO.PK_ORG) && !fflag)) {
						list.add(bodyItem);
					} 
//					else if (flag) {
//						list.add(bodyItem);
//					}
				}
				initAllitemsToCurrcorp(list.toArray(new BillItem[] {}),
						fyPkCorp);
			}
		}
	}
	/**
	 * 设置参照类型的Pk_org:(DWBM,FYDWBM,PK_ORG)
	 * @param headItems
	 * @param pk_org
	 */
	private void initAllitemsToCurrcorp(BillItem[] headItems, String pk_org) {
		boolean isInitGroup = false;
		isInitGroup = ((ErmBillBillManageModel) editor.getModel()).getContext().getNodeCode()
				.equals(BXConstans.BXINIT_NODECODE_G);
		for (BillItem headItem : headItems) {
			String refType = headItem.getRefType();
			if (headItem.getKey().equals(JKBXHeaderVO.DWBM)
					|| headItem.getKey().equals(JKBXHeaderVO.FYDWBM)
					|| headItem.getKey().equals(JKBXHeaderVO.PK_ORG)) {
				continue;
			}
			if (refType != null && !refType.equals("")
					&& headItem.getComponent() != null
					&& headItem.getComponent() instanceof UIRefPane) {
				try {
					UIRefPane ref = (UIRefPane) headItem.getComponent();
					AbstractRefModel refModel = ref.getRefModel();
					if (refModel == null)
						continue;
					if (pk_org == null && !isInitGroup) {
						ref.setEnabled(false);
						ref.setValue(null);
					} else if (ref.getPk_corp() == null
							|| !ref.getPk_corp().equals(pk_org)) {
						ref.setPk_org(pk_org);
						ref.setValue(null);
						ref.setEnabled(true);
					}
				} catch (ClassCastException e) {
					ExceptionHandler.consume(e);
				}
			}
		}
	}
	
	/**
	 * 将这些字段为表头表体均有的字段，设置到表体的每个页签中
	 * @param item
	 * @return
	 */
	protected BillItem[] getItemsById(String item) {
		if (item.equals(JKBXHeaderVO.SZXMID) || item.equals(JKBXHeaderVO.JOBID)
				|| item.equals(JKBXHeaderVO.CASHPROJ)
				|| item.equals(JKBXHeaderVO.PROJECTTASK)
				|| item.equals(JKBXHeaderVO.PK_CHECKELE)
				|| item.equals(JKBXHeaderVO.PK_RESACOSTCENTER)
				|| item.equals(JKBXHeaderVO.PK_PCORG)
				|| item.equals(JKBXHeaderVO.PK_PCORG_V)
				|| item.startsWith("defitem")) {
			String[] tables = getBillCardPanel().getBillData().getBodyTableCodes();
			List<BillItem> results = new ArrayList<BillItem>();
			if (getBillCardPanel().getHeadItem(item) != null) {
				results.add(getBillCardPanel().getHeadItem(item));
			}
			for (String tab : tables) {
				BillItem[] bodyItems = getBillCardPanel().getBillData().getBodyItemsForTable(tab);
				if (bodyItems == null){
					continue;
				}
				for (BillItem key : bodyItems) {
					if (key.getKey().equals(item) || key.getIDColName() != null && key.getIDColName().equals(item)) {
						results.add(key);
					}
				}
			}
			return results.toArray(new BillItem[] {});
		}
		return new BillItem[] {getBillCardPanel().getHeadItem(item) };
	}
	
	protected Object getHeadValue(String key) {
		BillItem headItem = getBillCardPanel().getHeadItem(key);
		if (headItem == null) {
			headItem = getBillCardPanel().getTailItem(key);
		}
		if (headItem == null) {
			return null;
		}
		return headItem.getValueObject();
	}
	
	protected void setHeadValue(String key, Object value) {
		if (getBillCardPanel().getHeadItem(key) != null) {
			getBillCardPanel().getHeadItem(key).setValue(value);
		}
	}

}
