package nc.ui.erm.action;

import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.bs.erm.sharerule.ShareRuleDataConvert;
import nc.bs.erm.sharerule.ShareruleConst;
import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.pf.pub.PfDataCache;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.erm.billpub.view.RapidShareDialog;
import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.BillTabbedPane;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.fieldcontrast.FieldcontrastVO;
import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.erm.sharerule.ShareConvRuleVO;
import nc.vo.erm.sharerule.ShareruleObjVO;
import nc.vo.erm.sharerule.ShareruleVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFDouble;

public abstract class RapidShareAbstractAction extends NCAction {

    private static final long serialVersionUID = -4055834802753783082L;

    private BillManageModel model;
    private BillForm editor;
    private UFDouble totalAmount;
    private String currPage;
    private int selectRow;
    private int shareType;
    private UFDouble shareAmount;
    private String refPK;
    private SuperVO selectvo;
    private String djlx;
    private String parentDjlx;
    private String bzbm;
    private AggshareruleVO aggvo;
    
    private static final int SHARE_TYPE_WHOLE = 2;

    private static final int SHARE_TYPE_PART = 1;

    private static final int SHARE_TYPE_UNKONW = 0;

    public RapidShareAbstractAction() {
        super();
        this.setBtnName(ErmActionConst.getRapidShareName());
        this.setCode(ErmActionConst.RAPIDSHARE);
    }
    
    private void validate() throws BusinessException {
        boolean validate = false;
        String billtype = getDjlx();
        BilltypeVO typeVO = PfDataCache.getBillType(billtype);
        if (typeVO != null && typeVO.getParentbilltype() != null && 
                typeVO.getParentbilltype().equals(getParentDjlx())) {
            validate = true;
        }
        if (billtype.equals(BXConstans.BILLTYPECODE_RETURNBILL)) {
            throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0153")/*@res "还款单不能进行快速分摊！"*/);
        }
        
        if (!validate) {
            typeVO = PfDataCache.getBillType(getParentDjlx());
            typeVO.getBilltypenameOfCurrLang();
            throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("201100_0",null,"0201100-0036",null,
                    new String[]{typeVO.getBilltypenameOfCurrLang()})/*@res "单据类型不是{0}，不能进行快速分摊！"*/);
        }
        validateCShareCondition();
    }
    
    @Override
    public void doAction(ActionEvent e) throws Exception {
        initDataBefore();
        validate();
        boolean inited = initData();
        if (!inited)
            return;
        RapidShareDialog dialog = new RapidShareDialog(model, shareAmount, refPK);
        
//        dialog.getValidateService().addValidator(new Validator() {
//
//            private static final long serialVersionUID = 1L;
//
//            @Override
//            public ValidationFailure validate(Object obj) {
//                ValidationFailure failure = null;
//                boolean fieldExist = shareObjExistInCardPanel((AggshareruleVO)obj);
//                if (!fieldExist)
//                    failure = new ValidationFailure(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0151")/*@res "分摊规则的部分分摊对象没有显示，请修改单据模板或改选其他分摊规则！"*/);
//                return failure;
//            }
//            
//        });
        
        // 显示对话框
        dialog.showModal();

        if (dialog.getResult() == nc.ui.pub.beans.UIDialog.ID_OK) {
            aggvo = dialog.getAggvo();
            if (aggvo == null) {
                return;
            }
            handleCostShareBillItem(aggvo);
            ShareConvRuleVO scvo = new ShareConvRuleVO();
            scvo.setAggSRule(aggvo);
            scvo.setBillType(getDjlx());
            scvo.setSharevo(selectvo);
            scvo.setShareMoney(shareAmount);
            scvo.setBusitype(BXConstans.CSHARE_PAGE);
            scvo.setParentBillType(getParentDjlx());
            scvo.setPk_group(getModel().getContext().getPk_group());
            scvo.setPk_org(getModel().getContext().getPk_org());
            SuperVO[] vos = ShareRuleDataConvert.getDataConvertVOS(scvo);
            switch (shareType) {
            case SHARE_TYPE_UNKONW:
                    break;
            case SHARE_TYPE_PART:
                    shareByPart(vos);
                    break;
            case SHARE_TYPE_WHOLE:
                    shareByWhole(vos);
                    break;
            }
        }
        
    }
    
    /**
     * 分摊
     * @param aggvo
     * @return
     */
//    private boolean shareObjExistInCardPanel(AggshareruleVO aggvo) {
//        boolean fieldExist = false;
//        BillCardPanel billCardPanel = getEditor().getBillCardPanel();
//        BillItem[] items = billCardPanel.getBillModel(BXConstans.CSHARE_PAGE).getBodyItems();
//        if (items != null) {
//            ShareruleObjVO[] shareruleObjs = (ShareruleObjVO[])aggvo.getTableVO(aggvo.getTableCodes()[0]);
//            for (ShareruleObjVO shareruleObj : shareruleObjs) {
//                fieldExist = false;
//                for (BillItem item : items) {
//                    if (shareruleObj.getFieldcode().equals(item.getKey()) && item.isShow()) {
//                        fieldExist = true;
//                        break;
//                    }
//                }
//                if (!fieldExist) {
//                    break;
//                }
//            }
//        }
//        return fieldExist;
//    }
    
    private void handleCostShareBillItem(AggshareruleVO aggvo) throws BusinessException {
        BillCardPanel billCardPanel = getEditor().getBillCardPanel();
        BillItem[] items = billCardPanel.getBillModel(BXConstans.CSHARE_PAGE).getBodyItems();
        if (items != null) {

            FieldcontrastVO qryVO = new FieldcontrastVO();
            qryVO.setApp_scene(ErmBillFieldContrastCache.FieldContrast_SCENE_SHARERULEField);
            qryVO.setSrc_billtype(getDjlx());
            qryVO.setSrc_busitype(BXConstans.CSHARE_PAGE);
            qryVO.setPk_group(getModel().getContext().getPk_group());
            qryVO.setPk_org(getModel().getContext().getPk_org());
            
            ShareruleObjVO[] shareruleObjs = (ShareruleObjVO[])aggvo.getTableVO(aggvo.getTableCodes()[0]);
            boolean fieldExist = false;
            for (ShareruleObjVO shareruleObj : shareruleObjs) {
                fieldExist = false;
                String objectField = ShareRuleDataConvert.getSrcFieldCode(qryVO, shareruleObj.getFieldcode());
                for (BillItem item : items) {
                    if (objectField.equals(item.getKey()) && item.isShow()) {
                      fieldExist = true;
                      break;
                  }
                }
                if (!fieldExist) {
                    break;
                }
            }
            if (!fieldExist)
                throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0151")/*@res "分摊规则的部分分摊对象没有显示，请修改单据模板或改选其他分摊规则！"*/);
        }
    }
    
    private void shareByWhole(SuperVO[] vos) throws ValidationException {
        beforeShareByWhole(vos);
        setTabbedPaneSelected(BXConstans.CSHARE_PAGE);
        SuperVO[] updatevos = fillCShareDetailVOs(vos);

        BillCardPanel billCardPanel = getEditor().getBillCardPanel();
        BillModel billModel = billCardPanel
                .getBillModel(BXConstans.CSHARE_PAGE);
        BillScrollPane sp = billCardPanel.getBodyPanel(BXConstans.CSHARE_PAGE);
        int rowCount = billCardPanel.getRowCount();
        for (int nRow = 0; nRow < rowCount; nRow++) {
            sp.getTable().changeSelection(0, 0, false, false);
            billCardPanel.delLine();
        }

        for (int i = 0; i < updatevos.length; i++) {
            billCardPanel.addLine(BXConstans.CSHARE_PAGE);
        }
        for (int i = 0; i < updatevos.length; i++) {
            billModel.setBodyRowVO(updatevos[i], i);
        }

        billModel.loadLoadRelationItemValue();
        for (int i = 0; i < updatevos.length; i++) {
            ErmForCShareUiUtil.setRateAndAmount(i, billCardPanel);
        }
        
        matchData(updatevos, billCardPanel);
    }
    

	/**
	 * 过滤没有权限的数据（例如，收支项目1不在组织1下，则不设置收支项目1）
	 * 
	 * @param updatevos
	 * @param billCardPanel
	 */
	private void matchData(SuperVO[] updatevos, BillCardPanel billCardPanel) {
		for (int i = 0; i < updatevos.length; i++) {
			BillItem[] items = billCardPanel.getBillModel(BXConstans.CSHARE_PAGE).getBodyItems();
			for (BillItem item : items) {
				Object itemVal = updatevos[i].getAttributeValue(item.getKey());
				if (itemVal == null)
					continue;
				if (item.getComponent() instanceof UIRefPane) {
					UIRefPane refPane = (UIRefPane) item.getComponent();
					AbstractRefModel refModel = refPane.getRefModel();
					if (refModel != null && !(refModel instanceof nc.ui.org.ref.OrgBaseTreeDefaultRefModel)) {
						String pkOrg = (String) updatevos[i].getAttributeValue("assume_org");
						if (StringUtil.isEmpty(pkOrg)) {
							pkOrg = (String) getHeadValue("assume_org");
						}
						refModel.setPk_org(pkOrg);
						refModel.setMatchPkWithWherePart(true);
						refModel.setPKMatch(true);
						@SuppressWarnings("rawtypes")
						Vector vec = refModel.matchPkData(itemVal.toString());
						if (vec == null || vec.isEmpty()) {
							updatevos[i].setAttributeValue(item.getKey(), null);

							int row = 0;
							if (shareType == SHARE_TYPE_PART) {
								row = selectRow + i;
							} else {
								row = i;
							}
							billCardPanel.getBillModel(BXConstans.CSHARE_PAGE).setValueAt(null, row, item.getKey());
						}
					}
				}
			}
		}
	}
    
    protected abstract void beforeShareByWhole(SuperVO[] vos);
    
    @SuppressWarnings("deprecation")
    protected void shareByPart(SuperVO[] vos) throws ValidationException {
        SuperVO[] updatevos = fillCShareDetailVOs(vos);
        BillCardPanel billCardPanel = getEditor().getBillCardPanel();
        BillModel billModel = billCardPanel
                .getBillModel(BXConstans.CSHARE_PAGE);
        billModel.clearRowData(selectRow, null);
        if (billModel.getRowState(selectRow) == BillModel.NORMAL)
            billModel.setRowState(selectRow, BillModel.MODIFICATION);
        getEditor().getBillCardPanel().copyLine();
        for (int i = 1; i < updatevos.length; i++) {
            billCardPanel.pasteLine(BXConstans.CSHARE_PAGE);
        }

        int lastIndex = updatevos.length - 1;
        for (int i = 0; i < updatevos.length; i++) {
        	int curRow = selectRow + i;
            if (i < lastIndex) {
                updatevos[i].setAttributeValue(updatevos[i].getPKFieldName(),
                        null);
            }
            billModel.setBodyRowVO(updatevos[i], curRow);
        }
        billModel.loadLoadRelationItemValue();
        for (int i = 0; i < updatevos.length; i++) {//重新计算本币金额
            ErmForCShareUiUtil.setRateAndAmount(selectRow + i, billCardPanel);
        }
        matchData(updatevos, billCardPanel);
    }
    
    private static final double ONE_HUNDRED = 100D;
    
    protected abstract void validateCShareCondition() throws BusinessException;
    
    private SuperVO[] fillCShareDetailVOs(SuperVO[] vos) throws ValidationException {
        String fydwbmHead = (String)getHeadValue("fydwbm");
        boolean bodyOrgShow = isBodyItemVisible("assume_org");
//        boolean bodyDeptShow = isBodyItemVisible("assume_dept");
        
        ShareruleVO shareruleVo = (ShareruleVO) aggvo.getParentVO();
        
        UFDouble sum;
        if (totalAmount.compareTo(shareAmount) == 0) {
            sum = new UFDouble(ONE_HUNDRED);
        } else {
            sum = shareAmount.div(totalAmount).multiply(ONE_HUNDRED);
        }
        sum = sum.setScale(2, BigDecimal.ROUND_HALF_UP);
        int nPos = 0;
        for (SuperVO vo : vos) {
            UFDouble tmpAmount = (UFDouble) vo.getAttributeValue("assume_amount");
            UFDouble tmpRatio = tmpAmount.div(totalAmount).multiply(100);
            tmpRatio = tmpRatio.setScale(2, BigDecimal.ROUND_HALF_UP);
            
            UFDouble tmpSum = sum;
            tmpSum = tmpSum.setScale(2, BigDecimal.ROUND_HALF_UP);
            sum = sum.sub(tmpRatio);
            
            if (ShareruleConst.SRuletype_Money != shareruleVo.getRule_type().intValue()) {
                //不够补齐
                if (sum.compareTo(UFDouble.ZERO_DBL) <= 0) {
                    sum = UFDouble.ZERO_DBL;
                    tmpRatio = tmpSum;
                }
                //如果是最后一个，比例加上
                if ((nPos == vos.length - 1)
                        && (UFDouble.ZERO_DBL.compareTo(sum) != 0)) {
                    tmpRatio = tmpRatio.add(sum);
                }
            }
            
            if (UFDouble.ZERO_DBL.compareTo(tmpAmount) == 0) {
                tmpRatio = UFDouble.ZERO_DBL;
            }
            
			// 重新换算比例
			vo.setAttributeValue("share_ratio", tmpRatio);
			// 设置集团默认值（集团为必填项）
			vo.setAttributeValue("pk_group", getHeadValue(CostShareVO.PK_GROUP));
			if (vo.getAttributeValue("assume_org") == null) {
				vo.setAttributeValue("assume_org", getHeadValue("fydwbm"));
			}
			if (vo.getAttributeValue(CShareDetailVO.BZBM) == null) {
				vo.setAttributeValue(CShareDetailVO.BZBM, getBzbm());
			}
			
            Map<String, String> fieldMap = getHeadToBodyFieldMap();
			String assumeOrg = (String) vo.getAttributeValue(CShareDetailVO.ASSUME_ORG);
			if (bodyOrgShow && assumeOrg != null && assumeOrg.equals(fydwbmHead)) {// 根据表头带入默认值
				for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
				    Object objBodyVal = vo.getAttributeValue(entry.getValue());
					if (objBodyVal == null && isBodyItemVisible(entry.getValue().toString())) {
					    Object obj = getHeadValue(entry.getKey());
					    if (obj != null) {
	                        vo.setAttributeValue(entry.getValue(), obj);
					    }
					}
				}
			} else {
			    for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
			        if (!isShareObject(entry.getValue())) {
	                    vo.setAttributeValue(entry.getValue(), null);
			        }
                }
			}
            nPos++;
        }
        // 报销单快速分摊后，如果是拉分摊申请单后，需要和申请单相关联
        Boolean ismashare = getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.ISMASHARE) == null ? false
        		: (Boolean) getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.ISMASHARE).getValueObject();
		if (ismashare) {
			Object pk_item = getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ITEM).getValueObject();
			for (SuperVO vo : vos) {
				if (vo instanceof CShareDetailVO) {
					vo.setAttributeValue(CShareDetailVO.PK_ITEM, pk_item);
				}
			}
		}
        return vos;
    }
    
    private boolean isShareObject(String field) {
        if (aggvo == null) {
            return false;
        }
        boolean isShareObj = false;
        ShareruleObjVO[] shareruleObjs = (ShareruleObjVO[])aggvo.getTableVO(aggvo.getTableCodes()[0]);
        for (ShareruleObjVO shareruleObj : shareruleObjs) {
            if (shareruleObj.getFieldcode().equals(field)) {
                isShareObj = true;
                break;
            }
        }
        return isShareObj;
    }
    
	private Map<String, String> getHeadToBodyFieldMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(JKBXHeaderVO.FYDEPTID, CShareDetailVO.ASSUME_DEPT);
		map.put(JKBXHeaderVO.SZXMID, CShareDetailVO.PK_IOBSCLASS);
		map.put(JKBXHeaderVO.PK_PCORG, CShareDetailVO.PK_PCORG);
		map.put(CostShareVO.BX_PCORG, CShareDetailVO.PK_PCORG);
		map.put(JKBXHeaderVO.PK_RESACOSTCENTER, CShareDetailVO.PK_RESACOSTCENTER);
		map.put(JKBXHeaderVO.JOBID, CShareDetailVO.JOBID);
		map.put(JKBXHeaderVO.PROJECTTASK, CShareDetailVO.PROJECTTASK);
		map.put(JKBXHeaderVO.PK_CHECKELE, CShareDetailVO.PK_CHECKELE);
		map.put(JKBXHeaderVO.CUSTOMER, CShareDetailVO.CUSTOMER);
		map.put(JKBXHeaderVO.HBBM, CShareDetailVO.HBBM);
		//增加产品线和品牌
		map.put(JKBXHeaderVO.PK_PROLINE, CShareDetailVO.PK_PROLINE);
		map.put(JKBXHeaderVO.PK_BRAND, CShareDetailVO.PK_BRAND);
		return map;
	}

    protected abstract void initDataBefore() throws ValidationException;
    
    protected boolean initData() throws BusinessException {
        // 当当前页签为分摊页签,且分摊页签中有选中行,则认为为部分分摊1,否则为整单分摊2,0为特殊情况
        BillCardPanel billCardPanel = getEditor().getBillCardPanel();
        String bzbm = (String)billCardPanel.getHeadItem("bzbm").getItemEditor().getValue();
        setBzbm(bzbm);
        String currPage =getEditor().getBillCardPanel().getCurrentBodyTableCode();
        setCurrPage(currPage);
        int selectRowCount = billCardPanel.getBodyPanel(currPage).getTable().getSelectedRowCount();
        if (selectRowCount > 1) {
            throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0146")/*@res "不支持多行分摊，请选择一行进行分摊！"*/);
        }
        int selectRow = billCardPanel.getBodyPanel(currPage).getTable().getSelectedRow();
        setSelectRow(selectRow);
        BillModel billModel = billCardPanel
                .getBillModel(BXConstans.CSHARE_PAGE);

        int cshare_selectRow = billCardPanel
                .getBodyPanel(BXConstans.CSHARE_PAGE).getTable()
                .getSelectedRow();

        if (currPage.equals(BXConstans.CSHARE_PAGE) && selectRow > -1) {//
            initDataByPart();
            setShareType(SHARE_TYPE_PART);
        } else if ((!currPage.equals(BXConstans.CSHARE_PAGE) && cshare_selectRow > -1)
                || // 分摊页签有数据,当前页签却为业务页签,则询问
                (billModel.getRowCount() > 0 && cshare_selectRow == -1)) {
            int result = MessageDialog.showOkCancelDlg(getEditor(), null, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0004")/*@res "分摊页签录有数据，是否仍整单分摊。"*/);
            if (result == MessageDialog.ID_OK) {
                initDataByWhole();
                setShareType(SHARE_TYPE_WHOLE);
            } else {
                // setShareType(0);
                return false;
            }
        } else {// 整单分摊
            initDataByWhole();
            setShareType(SHARE_TYPE_WHOLE);
        }
        return true;
    }

    protected void initDataByPart() throws BusinessException {
        SuperVO selectvo = (SuperVO) getEditor().getBillCardPanel().getBillModel(getCurrPage()).getBodyValueRowVO(getSelectRow(),
                CShareDetailVO.class.getName());
        setSelectvo(selectvo);
        UFDouble shareAmount = (UFDouble) selectvo.getAttributeValue("assume_amount");
        if (shareAmount == null || shareAmount.compareTo(UFDouble.ZERO_DBL) == 0) {
            throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0003")/*@res "分摊金额不能为0"*/);
        }
        setShareAmount(shareAmount);
        String refPK = (String) selectvo.getAttributeValue("assume_org");
        setRefPK(refPK);
    }
    
    protected abstract void initDataByWhole() throws ValidationException;
    
    /**
     * 设置指定表体页签显示
     *
     * @param tableCode
     */
    private void setTabbedPaneSelected(String tableCode) {
        BillTabbedPane tabPane = getEditor().getBillCardPanel().getBodyTabbedPane();
        int index = tabPane.indexOfComponent(getEditor().getBillCardPanel().getBodyPanel(tableCode));

        if (index >= 0) {
            getEditor().getBillCardPanel().getBodyTabbedPane().setSelectedIndex(index);
        }
    }
    
    protected void setHeadValue(String key, Object value) {
        if (getEditor().getBillCardPanel().getHeadItem(key) != null) {
            getEditor().getBillCardPanel().getHeadItem(key).setValue(value);
        }
    }
    
    private Object getHeadValue(String key) {
        BillItem headItem = getEditor().getBillCardPanel().getHeadItem(key);
        if (headItem == null) {
            headItem = getEditor().getBillCardPanel().getTailItem(key);
        }
        if (headItem == null) {
            return null;
        }
        return headItem.getValueObject();
    }
    
	private boolean isBodyItemVisible(String key) {
        BillItem item = getEditor().getBillCardPanel().getBodyItem(key);
        if (item == null) {
            return false;
        }
        return item.isShow();
    }
    
    
    public BillManageModel getModel() {
        return model;
    }
    
    public void setModel(BillManageModel model) {
        this.model = model;
        this.model.addAppEventListener(this);
    }
    
    public BillForm getEditor() {
        return editor;
    }
    
    public void setEditor(BillForm editor) {
        this.editor = editor;
    }
    
    public UFDouble getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(UFDouble totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrPage() {
        return currPage;
    }

    public void setCurrPage(String currPage) {
        this.currPage = currPage;
    }

    public int getSelectRow() {
        return selectRow;
    }

    public void setSelectRow(int selectRow) {
        this.selectRow = selectRow;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }

    public UFDouble getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(UFDouble shareAmount) {
        this.shareAmount = shareAmount;
    }

    public String getRefPK() {
        return refPK;
    }

    public void setRefPK(String refPK) {
        this.refPK = refPK;
    }

    public SuperVO getSelectvo() {
        return selectvo;
    }

    public void setSelectvo(SuperVO selectvo) {
        this.selectvo = selectvo;
    }

    public String getDjlx() {
        return djlx;
    }

    public void setDjlx(String djlx) {
        this.djlx = djlx;
    }

    public String getParentDjlx() {
        return parentDjlx;
    }

    public void setParentDjlx(String parentDjlx) {
        this.parentDjlx = parentDjlx;
    }

    public String getBzbm() {
        return bzbm;
    }

    public void setBzbm(String bzbm) {
        this.bzbm = bzbm;
    }
    
}
