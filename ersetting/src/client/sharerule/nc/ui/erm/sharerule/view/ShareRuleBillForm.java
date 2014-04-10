package nc.ui.erm.sharerule.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bs.erm.sharerule.ShareruleConst;
import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.MDPropertyRefPane;
import nc.bs.logging.Logger;
import nc.itf.org.IOrgConst;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IAttribute;
import nc.md.model.IBean;
import nc.md.model.MetaDataException;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.erm.sharerule.validator.ShareruleValidator;
import nc.ui.erm.view.ERMUserdefitemContainerPreparator;
import nc.ui.fipub.BillCardPanelTool;
import nc.ui.pmpub.ref.WBSDefaultRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.itemeditors.UFRefBillItemEditor;
import nc.ui.resa.refmodel.CostCenterTreeRefModel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.UIStateChangeEvent;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.erm.sharerule.ShareruleDataVO;
import nc.vo.erm.sharerule.ShareruleObjVO;
import nc.vo.erm.sharerule.ShareruleVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author shengqy
 *
 */
@SuppressWarnings("restriction")
public class ShareRuleBillForm extends BillForm implements BillEditListener, BillEditListener2 {

	private static final long serialVersionUID = 1L;
	private MDPropertyRefPane refPane;

	private static final List<String> ruleTypeFields = new ArrayList<String>();
	static {
		ruleTypeFields.add(ShareruleDataVO.SHARE_RATIO);
		ruleTypeFields.add(ShareruleDataVO.ASSUME_AMOUNT);
	}

	@Override
    protected void processBillData(BillData data) {
		super.processBillData(data);
		BillItem objItem = data.getHeadItem(ShareruleVO.RULEOBJ_NAME);
		UFRefBillItemEditor editor = new UFRefBillItemEditor(objItem);
		editor.setComponent(getRefPane());
		objItem.setDataType(IBillItem.UFREF);
		objItem.setItemEditor(editor);
	}

	@Override
	public void initUI() {
		super.initUI();

		getBillCardPanel().addEditListener(this);
		getBillCardPanel().addBodyEditListener2(this);
		// 表体为单页签时,仍显示页签名
		getBillCardPanel().getBodyTabbedPane().setSingleTabVisible(true);
		setRefMultiSelected(true);
		BillScrollPane bsp = getBillCardPanel().getBodyPanel("rule_data");
		bsp.setAutoAddLine(true);
	}

	private void setRefMultiSelected(boolean flag) {
		BillItem[] items = billCardPanel.getBodyItems();
		for (BillItem item : items) {
			if (item.getComponent() instanceof UIRefPane) {
				UIRefPane ref = (UIRefPane) item.getComponent();
                ref.setMultiSelectedEnabled(flag);
			}
		}
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		if (e.getKey().equals(ShareruleVO.RULEOBJ_NAME)) {// 分摊对象编辑后处理
            LoginContext context = getModel().getContext();
            switch (context.getNodeType()) {
            case GROUP_NODE:
                context.setPk_org(context.getPk_group());
                break;
            }
            linkBodyByRuleObj();
		} else if (e.getKey().equals(ShareruleVO.RULE_TYPE)) {// 分摊方式编辑后处理
			linkBodyByRuleType();
		}
		if (e.getPos() == IBillItem.BODY) {
			if (ShareruleDataVO.ASSUME_ORG.equals(e.getKey())) {
				IConstEnum orgObj = (IConstEnum) billCardPanel.getBillModel().getValueObjectAt(e.getRow(),
						ShareruleDataVO.ASSUME_ORG);
				setRefWhere(orgObj == null ? getModel().getContext().getPk_org() : orgObj.getValue().toString());
				clearColData(e.getRow());
			} else if (ShareruleDataVO.PK_PCORG.equals(e.getKey())) {
			    // 利润中心为空，需要清空核算要素
                IConstEnum obj = (IConstEnum) billCardPanel.getBillModel().getValueObjectAt(e.getRow(),
                        ShareruleDataVO.PK_PCORG);
			    if (obj == null) {
	                billCardPanel.getBillModel().setValueAt(null, e.getRow(), ShareruleDataVO.PK_CHECKELE);
			    }
			} else if (ShareruleDataVO.JOBID.equals(e.getKey())) {
                // 项目，需要清空项目任务
                IConstEnum obj = (IConstEnum) billCardPanel.getBillModel().getValueObjectAt(e.getRow(),
                        ShareruleDataVO.JOBID);
                if (obj != null && !obj.getValue().equals(e.getOldValue())) {
                    billCardPanel.getBillModel().setValueAt(null, e.getRow(), ShareruleDataVO.PROJECTTASK);
                }
            }
			afterBodySelect(e);
		}
	}

    @Override
    public boolean beforeEdit(BillEditEvent e) {
        boolean canEdit = true;
        if (e.getPos() == IBillItem.BODY) {
            if (!ShareruleDataVO.SHARE_RATIO.equals(e.getKey()) &&
                    !ShareruleDataVO.ASSUME_AMOUNT.equals(e.getKey()) &&
                    !ShareruleDataVO.ASSUME_ORG.equals(e.getKey())) {
                // 核算要素
                if (ShareruleDataVO.PK_CHECKELE.equals(e.getKey())) {
                    canEdit = handleCheckele(e);
                } else if (ShareruleDataVO.PROJECTTASK.equals(e.getKey())) {
                    // 项目任务
                    handleProjectTask(e);
                } else {
                    canEdit = handleBodyItemBeforeEdit(e);
                }
            }
        }
        return canEdit;
    }
    
    private boolean handleBodyItemBeforeEdit(BillEditEvent e) {
        boolean canEdit = true;
        BillItem assumeOrgItem = billCardPanel.getBodyItem(ShareruleDataVO.ASSUME_ORG);
        if (assumeOrgItem.isShow()) {
//            String pkOrgContext = getModel().getContext().getPk_org();
            IConstEnum orgObj =
                (IConstEnum)billCardPanel.getBillModel().getValueObjectAt(e.getRow(),
                        ShareruleDataVO.ASSUME_ORG);
            if (orgObj == null) {
                canEdit = false;
                ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0028")/*@res "请先选择承担单位！"*/,
                        getModel().getContext());
                billCardPanel.getBillModel().setValueAt(null, e.getRow(), e.getKey());
            }else {
//                getModel().getContext().setPk_org(orgObj.getValue().toString());
                setRefWhere(orgObj.getValue().toString());
            }
        }else {
            setRefWhere(null);
        }
        return canEdit;
    }
    
    private void handleProjectTask(BillEditEvent e) {
        // 项目
        BillItem jobid = billCardPanel.getBodyItem(ShareruleDataVO.JOBID);
        // 项目任务
        BillItem projectTaskItem = billCardPanel.getBodyItem(ShareruleDataVO.PROJECTTASK);
        UIRefPane projectTaskRefPane = (UIRefPane)projectTaskItem.getComponent();
        String projectVal = null;
        if (jobid.isShow()) {
            IConstEnum jobVal = (IConstEnum) billCardPanel.getBillModel()
                    .getValueObjectAt(e.getRow(), ShareruleDataVO.JOBID);
            if (jobVal != null) {
                projectVal = jobVal.getValue().toString();
            }
        }
        WBSDefaultRefModel wbsModel = (WBSDefaultRefModel)projectTaskRefPane.getRefModel();
        wbsModel.setProjectId(projectVal);
    }
    
    private boolean handleCheckele(BillEditEvent e) {
        boolean canEdit = true;
        // 利润中心
        BillItem pcOrgItem = billCardPanel.getBodyItem(ShareruleDataVO.PK_PCORG);
        // 核算要素
        BillItem checkeleItem = billCardPanel.getBodyItem(ShareruleDataVO.PK_CHECKELE);
        UIRefPane checkeleRefPane = (UIRefPane)checkeleItem.getComponent();
        if (pcOrgItem.isShow()) {
            IConstEnum orgObj = (IConstEnum) billCardPanel.getBillModel()
                    .getValueObjectAt(e.getRow(), ShareruleDataVO.PK_PCORG);
            if (orgObj == null) {
                canEdit = false;

                ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0037")/*@res "请先选择利润中心！"*/,
                        getModel().getContext());
                billCardPanel.getBillModel().setValueAt(null, e.getRow(),
                        e.getKey());
            } else {
                checkeleRefPane.setPk_org(orgObj.getValue().toString());
            }
        } else {
            canEdit = false;
            ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0038")/*@res "没有利润中心不能参照核算要素，请选择利润中心作为分摊对象！"*/,
                    getModel().getContext());
            billCardPanel.getBillModel().setValueAt(null, e.getRow(),
                    e.getKey());
        }
        return canEdit;
    }

    private void clearColData(int row) {
        BillItem[] items = billCardPanel.getBodyShowItems();
        for (BillItem item : items) {
            if (ShareruleDataVO.ASSUME_ORG.equals(item.getKey()))
                continue;
            if (!(item.getComponent() instanceof UIRefPane))
                continue;
            UIRefPane ref = (UIRefPane) item.getComponent();
            if (ref.getRefModel() != null) {
                billCardPanel.getBillModel().setValueAt(null, row, item.getKey());
            }
        }
    }

	private void linkBodyByRuleObj() {
		// 取得参照中选择的数据编码
		Map<String, String> map = getRefPane().getDialog().getSelecteddatas();
		List<String> ruleobjcodes = new ArrayList<String>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			ruleobjcodes.add(entry.getKey());
		}
		// 表体显示分摊对象字段
		showRuleObjField(ruleobjcodes);
	}

	private void linkBodyByRuleType() {
		// 得到分摊方式
		Object obj = billCardPanel.getHeadItem(ShareruleVO.RULE_TYPE).getValueObject();
		int ruletype = Integer.parseInt(obj.toString());
		// 表体显示分摊方式字段
		showRuleTypeFiled(ruletype);
		setEditStatus();
	}

	private void afterBodySelect(BillEditEvent e) {
		BillCardPanel cardPanel = getBillCardPanel();
		BillItem item = getBillCardPanel().getBodyItem(e.getKey());
		if (item.getComponent() instanceof UIRefPane) {
			UIRefPane refPane = (UIRefPane) item.getComponent();
			if (refPane.getRefModel() != null) {
				AbstractRefModel refmodel = ((UIRefPane) item.getComponent()).getRefModel();
				String[] refValues = refmodel.getPkValues();
				if (refValues != null && refValues.length > 1) {// 多选
					Object[] showValue = null;
					if (refPane.isReturnCode()) {
						showValue = refPane.getRefCodes();
					} else {
						String showNameField = ((UIRefPane) item.getComponent()).getRefModel()
								.getRefShowNameField();
						showValue = refmodel.getValues(showNameField);
					}
					cardPanel.copyLine();// 复制改行
					// 粘贴下面行
					for (int i = 0; i < refValues.length; i++) {
						String value = refValues[i];
						String name = (String) showValue[i];
						if (i != refValues.length - 1) {
							cardPanel.pasteLine();// 粘贴行,粘贴行会将新行放入当前行
							cardPanel.setBodyValueAt(null, e.getRow() + i, ShareruleDataVO.PK_CSHARE_DETAIL);// 将比例与pk设置为null
							cardPanel.getBillModel().setRowState(e.getRow() + i, BillModel.ADD);
						}

						cardPanel.setBodyValueAt(new DefaultConstEnum(value, name), e.getRow() + i, e.getKey());
						if (ShareruleDataVO.ASSUME_ORG.equals(e.getKey())) {// 费用单位在多选时，要将部门和项目、比例置空
							cardPanel.setBodyValueAt(null, e.getRow() + i, ShareruleDataVO.ASSUME_DEPT);
							cardPanel.setBodyValueAt(null, e.getRow() + i, ShareruleDataVO.PK_IOBSCLASS);
						}
					}
				}
			}
		}
	}

	private String getRuleTypeField(int ruletype) {
		String field = null;
		switch (ruletype) {
		case ShareruleConst.SRuletype_Average:
			break;
		case ShareruleConst.SRuletype_Ratio:
			field = ShareruleDataVO.SHARE_RATIO;
			break;
		case ShareruleConst.SRuletype_Money:
			field = ShareruleDataVO.ASSUME_AMOUNT;
			break;
		default:
			break;
		}
		return field;
	}

	private void setEditStatus() {
	    int rowCount = billCardPanel.getBillModel().getRowCount();
	    int rowStatus = -1;
	    BillModel billModel = billCardPanel.getBillModel();
	    for (int nPos = 0; nPos < rowCount; nPos++) {
	        rowStatus = billModel.getRowState(nPos);
	        if (rowStatus == BillModel.ADD ||
	                rowStatus == BillModel.DELETE ||
	                rowStatus == BillModel.MODIFICATION)
	            continue;
	        else
	            billModel.setRowState(nPos, BillModel.MODIFICATION);
	    }
	}

	private void showRuleTypeFiled(int ruletype) {
		for (String field : ruleTypeFields) {
			billCardPanel.getBodyItem(field).setShow(false);
			//置空数据
			for (int nPos = 0; nPos < billCardPanel.getRowCount(); nPos++) {
			    billCardPanel.setBodyValueAt(null, nPos, field);
			}
		}
		String ruleTypeField = getRuleTypeField(ruletype);
		if (!StringUtil.isEmpty(ruleTypeField)) {
			billCardPanel.getBodyItem(ruleTypeField).setShow(true);
		}
		// 刷新
		BillCardPanelTool.setBillData(billCardPanel, billCardPanel.getBillData());
	}

	private void showRuleObjField(List<String> ruleobjcodes) {
		// 先将表体除分摊比例和分摊金额字段的其他字段隐藏
		BillItem[] items = billCardPanel.getBodyItems();
		LoginContext context = getModel().getContext();

		for (BillItem item : items) {
			if (ruleTypeFields.contains(item.getMetaDataAccessPath())) {
				continue;
			}
			if (ruleobjcodes.contains(item.getMetaDataAccessPath())) {
				item.setShow(true);
				if (item.getComponent() instanceof UIRefPane) {
                    UIRefPane ref = (UIRefPane) item.getComponent();
                    ref.setMultiSelectedEnabled(true);
				    if ("assume_org".equals(item.getMetaDataAccessPath()) ||
				            "assume_org".equals(item.getKey())) {
				        continue;
				    }
				    if (ref.getRefModel() != null) {
					    setFilter(ref.getRefModel(),context.getPk_org());
					}
				}
			} else {
				item.setShow(false);
			}
		}
		// 刷新
		BillCardPanelTool.setBillData(billCardPanel, billCardPanel.getBillData());
	}

	private void setRefWhere(String pk_org) {
        BillItem[] items = billCardPanel.getBodyShowItems();
        LoginContext context = getModel().getContext();
        
        if(pk_org == null){
        	pk_org = context.getPk_org();
        }

        for (BillItem item : items) {
            if (ShareruleDataVO.ASSUME_ORG.equals(item.getKey()))
                continue;
            if (!(item.getComponent() instanceof UIRefPane))
                continue;
            UIRefPane ref = (UIRefPane) item.getComponent();
            if (ref.getRefModel() != null) {
                if (ref.getRefModel() instanceof nc.ui.resa.refmodel.FactorRefModel) {
                    // 核算要素不过滤
                    continue;
                } else {
                    setFilter(ref.getRefModel(), pk_org);
                }
            }
        }
	}

	private void setFilter(AbstractRefModel refModel, String pk_org) {
        String pkGroup = getModel().getContext().getPk_group();
        setFilter(refModel, pkGroup, pk_org);
	}

	private void setFilter(AbstractRefModel refModel, String pkGroup, String pkOrg) {
		if (refModel instanceof nc.ui.org.ref.LiabilityCenterDefaultRefModel
				|| refModel instanceof nc.ui.pmpub.ref.WBSDefaultRefModel
				|| refModel instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel) {
			String sWhere = parseWhere(refModel, pkGroup, pkOrg);
			refModel.setWherePart(sWhere, false);
		} else if (refModel instanceof CostCenterTreeRefModel) {
			String sWhere = parseWhere(refModel, pkGroup, pkOrg);
			refModel.setWherePart(sWhere, false);
			refModel.setPk_org(pkOrg);
		} else if (refModel instanceof nc.ui.resa.refmodel.FactorRefModel) {
			// 核算要素根据利润中心过滤，不响应财务组织的过滤
		} else {
			NODE_TYPE nodeType = ErUtil.getNodeTypeByPk_groupAndPk_org(pkGroup, pkOrg);
			switch (nodeType) {
				case GROUP_NODE:
					refModel.setPk_org(pkGroup);
					break;
				case ORG_NODE:
					refModel.setPk_org(pkOrg);
					break;
			}
		}
	}

	private String parseWhere(AbstractRefModel refModel, String pkGroup, String pkOrg) {
	    String orgFieldName = "pk_org";
        String groupFieldName = "pk_group";
        if (refModel instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel ||
        		refModel instanceof CostCenterTreeRefModel) {
            orgFieldName = "pk_financeorg";
        }

        String[] pks = null;
        StringBuilder sbWhere = new StringBuilder();
        NODE_TYPE nodeType = ErUtil.getNodeTypeByPk_groupAndPk_org(pkGroup, pkOrg);
        switch (nodeType) {
        case GROUP_NODE:
            if (refModel instanceof nc.ui.org.ref.FinanceOrgDefaultRefTreeModel) {
                sbWhere.append(groupFieldName).append(" = '").append(pkGroup).append("'");
            } else {
                pks = new String[2];
                pks[0] = IOrgConst.GLOBEORG;
                pks[1] = pkGroup;
            }
            break;
        case ORG_NODE:
            pks = new String[3];
            pks[0] = IOrgConst.GLOBEORG;
            pks[1] = pkGroup;
            pks[2] = pkOrg;
            break;
        }
        try {
            if (pks != null) {
                String sWhere = SqlUtils.getInStr(orgFieldName, pks, false);
                sbWhere.append(sWhere);
            }
        } catch (BusinessException e) {
            Logger.error(e.getMessage(), e);
        }
        return sbWhere.toString();
	}

	private void cardChange(List<String> ruleobjcodes, Integer ruletype) {
		String ruleTypeField = getRuleTypeField(ruletype);
		if (!StringUtil.isEmpty(ruleTypeField)) {
			ruleobjcodes.add(ruleTypeField);
		}
		// 先将表体除分摊比例和分摊金额字段的其他字段隐藏
		BillItem[] items = billCardPanel.getBodyItems();
        LoginContext context = getModel().getContext();
		for (BillItem item : items) {
			if (ruleobjcodes.contains(item.getMetaDataAccessPath())) {
				item.setShow(true);
				if (item.getComponent() instanceof UIRefPane) {
					UIRefPane ref = (UIRefPane) item.getComponent();
					if (ref.getRefModel() != null) {
                        setFilter(ref.getRefModel(),context.getPk_org());
					}
				}
			} else {
				item.setShow(false);
			}
		}
		// 刷新
		BillCardPanelTool.setBillData(billCardPanel, billCardPanel.getBillData());
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {

	}

	public MDPropertyRefPane getRefPane() {
		if (refPane == null) {
			refPane = new MDPropertyRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0009")/*@res "分摊对象"*/, "cf21a746-807a-4cf8-911f-f397529ee06e") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onButtonClicked() {
					if (billCardPanel.getRowCount() != 0) {
						ShowStatusBarMsgUtil.showErrorMsg("", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0029")/*@res "表体有数据,请先清空"*/, getModel().getContext());
						return;
					}
					super.onButtonClicked();
				}
			};
			refPane.setEnabled(true);
			refPane.setEditable(false);
			refPane.setAutoCheck(true);
		}
		return refPane;
	}

	private long showroottime = 0;
	
	private boolean isShowroot() { 
	    boolean bShow = false;
	    if (System.currentTimeMillis() - showroottime > 500)
	        bShow = true;
	    else 
	        bShow = false;
	    showroottime = System.currentTimeMillis();
	    return bShow;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if (AppEventConst.SELECTION_CHANGED == event.getType()) {
			AggshareruleVO aggvo = (AggshareruleVO) getModel().getSelectedData();
			if (aggvo == null) {
			    if (isShowroot()) {
	                showRoot();
			    }
			} else {
				// 主表VO
				ShareruleVO srulevo = (ShareruleVO) aggvo.getParentVO();
				// 分摊对象VOS
				ShareruleObjVO[] sruleobjvos = (ShareruleObjVO[]) aggvo.getTableVO(aggvo.getTableCodes()[0]);
				List<String> ruleobjcodes = new ArrayList<String>();
                IBean bean = null;
                try {
                    bean = MDBaseQueryFacade.getInstance().getBeanByID(ShareruleValidator.MD_ID_SHARERULE);
                } catch (MetaDataException e) {
                    Logger.error(e.getMessage(), e);
                }
                IAttribute att;
				Vector vec = new Vector();
				for (ShareruleObjVO sruleobj : sruleobjvos) {
				    ruleobjcodes.add(sruleobj.getFieldcode());
				    Vector row = new Vector();
				    row.add(sruleobj.getFieldcode());
                    att = bean.getAttributeByName(sruleobj.getFieldcode());
				    if (att == null || att.getDisplayName() == null) {
	                    row.add(sruleobj.getFieldname());
				    } else {
                        row.add(att.getDisplayName());
				    }
				    vec.add(row);
                }
				getRefPane().getRefModel().setSelectedData(vec);
				cardChange(ruleobjcodes, srulevo.getRule_type());
				getBillCardPanel().getBillModel().loadLoadRelationItemValue();
			}
		}
		if (event instanceof UIStateChangeEvent) {
		    UIStateChangeEvent newEvent = (UIStateChangeEvent)event;
		    if (UIState.ADD.equals(newEvent.getNewState())) {
		        getRefPane().getRefPK();
		        linkBodyByRuleType();
		    }
		}
	}

	private void showRoot() {
		BillItem[] items = getBillCardPanel().getBodyItems();
		for (BillItem item : items) {
			item.setShow(false);
		}
		getBillCardPanel().getBodyItem(ShareruleDataVO.SHARE_RATIO).setShow(true);
		
		getRefPane().getRefModel().setSelectedData(null);
		
		//加载用户定义属性
		new ERMUserdefitemContainerPreparator(getModel().getContext(),this).prepareBillData(getBillCardPanel().getBillData());
		// 刷新
		BillCardPanelTool.setBillData(getBillCardPanel(), getBillCardPanel().getBillData());
	}

    @Override
    protected int[] getDelLineIndex(String bodyTabcode) {
        if (!"rule_data".equals(bodyTabcode))
            return null;
        BillItem[] showItems = getBillCardPanel().getBodyShowItems();
        int colCount = showItems.length;
        int rowCount = getBillCardPanel().getRowCount();
        BillCardPanel cardPanel = getBillCardPanel();
        List<Integer> emptyRowList = new ArrayList<Integer>();
        boolean emptyRow = true;
        for (int nRowPos = 0; nRowPos < rowCount; nRowPos++) {
            for (int nColPos = 0; nColPos < colCount; nColPos++) {
                Object objVal =
                    cardPanel.getBodyValueAt(nRowPos, showItems[nColPos].getKey());
                if (objVal != null) {
                    emptyRow = false;
                    break;
                }
            }
            if (emptyRow)
                emptyRowList.add(Integer.valueOf(nRowPos));
            else
                emptyRow = true;
        }
        Integer[] emptyRowArray = null;
        if (!emptyRowList.isEmpty()) {
            emptyRowArray = new Integer[emptyRowList.size()];
            emptyRowArray = emptyRowList.toArray(emptyRowArray);
        }
        return ArrayUtils.toPrimitive(emptyRowArray);
    }

    public Object getValidateValue() {
        getBillCardPanel().stopEditing();
        return this.billCardPanel.getBillData().getBillObjectByMetaData();
    }

}