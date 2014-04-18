package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.sharerule.ShareRuleDataConvert;
import nc.bs.erm.sharerule.ShareruleConst;
import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.erm.matterapp.common.ErmForMatterAppUtil;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.erm.sharerule.rapidshare.RapidShareDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.BillTabbedPane;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.erm.fieldcontrast.FieldcontrastVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.erm.sharerule.ShareConvRuleVO;
import nc.vo.erm.sharerule.ShareruleObjVO;
import nc.vo.erm.sharerule.ShareruleVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDouble;
/**
 * 申请单快速分摊
 * @author wangled
 *
 */

public class RapidShareMattAction extends NCAction{

	private static final long serialVersionUID = 1L;
	private BillForm editor; 
	private String djlx;
    private String parentDjlx;
    private String bzbm;
    private String currPage;
    private BillManageModel model;
    private int selectRow;
    private SuperVO selectvo;
    private UFDouble shareAmount;
    private String refPK;
    private int shareType;
    private UFDouble totalAmount;
    
    private static final int SHARE_TYPE_WHOLE = 2;

    private static final int SHARE_TYPE_PART = 1;

    private static final int SHARE_TYPE_UNKONW = 0;
    
    private static final double ONE_HUNDRED = 100D;
    
    public RapidShareMattAction() {
        super();
        this.setBtnName(ErmActionConst.getRapidShareName());
        this.setCode(ErmActionConst.RAPIDSHARE);
    }
    
	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = getEditor().getBillCardPanel();
		billCardPanel.stopEditing();
        setMattDjlx(billCardPanel);
        
        //初始化数据
        initData(billCardPanel);
        
        RapidShareDialog dialog = new RapidShareDialog(model, shareAmount, refPK);
        if (dialog.showModal() == nc.ui.pub.beans.UIDialog.ID_OK) {
            AggshareruleVO aggvo = dialog.getAggvo();
            if (aggvo == null) {
                return;
            }
            handleMatterBillItem(aggvo);
            ShareConvRuleVO scvo = new ShareConvRuleVO();
            scvo.setAggSRule(aggvo);
            scvo.setBillType(getDjlx());
            scvo.setSharevo(selectvo);
            scvo.setShareMoney(shareAmount);
            scvo.setBusitype(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
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
			//对于申请单按金额分摊特殊处理，重新设置表头的合计金额，并重新计算表体的比例
			if (((ShareruleVO) scvo.getAggSRule().getParentVO()).getRule_type() == ShareruleConst.SRuletype_Money) {
				UFDouble totalAmount = UFDouble.ZERO_DBL;
				if(shareType == SHARE_TYPE_WHOLE){
					for(SuperVO vo : vos){
						totalAmount = totalAmount.add(((MtAppDetailVO)vo).getOrig_amount());
					}
				}else if(shareType == SHARE_TYPE_PART){
					totalAmount = this.totalAmount.sub(shareAmount);
					for(SuperVO vo : vos){
						totalAmount = totalAmount.add(((MtAppDetailVO)vo).getOrig_amount());
					}
				}
				//重新设置表头金额
				billCardPanel.getHeadItem(MatterAppVO.ORIG_AMOUNT).setValue(totalAmount);
				
				//重新设置表头本币金额和最大报销金额
				((MatterAppMNBillForm) getEditor()).resetHeadAmounts();
				
				// 重新计算表体的比例
				MatterAppUiUtil.setBodyShareRatio(getEditor().getBillCardPanel());
				
				// 计算表体本币等联动金额
				int rowCount = getEditor().getBillCardPanel().getBillModel(
						ErmMatterAppConst.MatterApp_MDCODE_DETAIL)
						.getRowCount();
				for (int i = 0; i < rowCount; i++) {
					((MatterAppMNBillForm) getEditor()).resetCardBodyRate(i);
					((MatterAppMNBillForm) getEditor()).resetCardBodyAmount(i);
				}
				((MatterAppMNBillForm) getEditor()).resetBodyMaxAmount();
			}
        } 
	}
	
	
	
	/**
	 * 初始化数据
	 * @param billCardPanel
	 * @throws BusinessException
	 */
	private void initData(BillCardPanel billCardPanel)throws BusinessException {
		String currPage =billCardPanel.getCurrentBodyTableCode();
		if(!ErmMatterAppConst.MatterApp_MDCODE_DETAIL.equals(currPage)){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0162")/*
			 * @res
			 * "当前页签不支持快速分摊"
			 */);
			
		}
		setCurrPage(currPage);
		String bzbm = (String)billCardPanel.getHeadItem(MatterAppVO.PK_CURRTYPE).getItemEditor().getValue();
		setBzbm(bzbm);
		int selectRowCount = billCardPanel.getBodyPanel(currPage).getTable().getSelectedRowCount();
		if (selectRowCount > 1) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0146")/*
																		 * @res
																		 * "不支持多行分摊，请选择一行进行分摊！"
																		 */);
		}
		
		int selectRow = billCardPanel.getBodyPanel(currPage).getTable().getSelectedRow();
        setSelectRow(selectRow);
       
        //如果表头可编辑，没有表体或选择的表体没有金额，按整单分摊
        boolean isAllshare=false;
        BillItem headItem = billCardPanel.getHeadItem(MatterAppVO.ORIG_AMOUNT);
        if(UIState.ADD == getModel().getUiState()){
        	int rowCount = billCardPanel.getBillModel(currPage).getRowCount();
        	if(rowCount==0 && headItem.isEdit()){
        		isAllshare=true;
        	}else {
        		int orig_amountCol = billCardPanel.getBodyColByKey(currPage, MtAppDetailVO.ORIG_AMOUNT);
        		UFDouble orig_amount = (UFDouble) billCardPanel.getBillModel().getTotalTableModel().getValueAt(0, orig_amountCol);
        		if(orig_amount==null || orig_amount.compareTo(UFDouble.ZERO_DBL)==0 && headItem.isEdit()){
        			isAllshare=true;
        		}else{
        			isAllshare=false;
        		}
        	}
        }else{
        	if(selectRow == -1){
        		isAllshare=true;
        	}
        }
  
        if (currPage.equals(ErmMatterAppConst.MatterApp_MDCODE_DETAIL) && !isAllshare) {
        	//部分分摊
            initDataByPart();
            setShareType(SHARE_TYPE_PART);
        }else if(currPage.equals(ErmMatterAppConst.MatterApp_MDCODE_DETAIL) && isAllshare){
        	//整单分摊
        	initDataByWhole();
            setShareType(SHARE_TYPE_WHOLE);
        }
	}
	
	/**
	 * 初始化部分分摊
	 * @throws BusinessException
	 */
	protected void initDataByPart() throws BusinessException {
		 MtAppDetailVO selectvo = (MtAppDetailVO) getEditor().getBillCardPanel().getBillModel(getCurrPage()).getBodyValueRowVO(getSelectRow(),
	        		MtAppDetailVO.class.getName());
	        setSelectvo(selectvo);
	        UFDouble shareAmount = (UFDouble) selectvo.getOrig_amount();
	        if (shareAmount == null || shareAmount.compareTo(UFDouble.ZERO_DBL) <= 0) {
	            throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","2201BTP-0012")/*@res "表体的分摊金额应大于0!"*/);
	        }
	        setShareAmount(shareAmount);
	        UFDouble totalAmount = (UFDouble) getEditor().getBillCardPanel().getHeadItem(MatterAppVO.ORIG_AMOUNT).getValueObject();
	        setTotalAmount(totalAmount);
	        String refPK = (String) selectvo.getAssume_org();
	        setRefPK(refPK);
	}
	
	/**
	 * 初始化整单分摊
	 * @throws BusinessException
	 */
    protected void initDataByWhole() throws BusinessException {
        BillCardPanel billCardPanel = getEditor().getBillCardPanel();
        int nDecimalDigit = billCardPanel.getHeadItem(MatterAppVO.ORIG_AMOUNT).getDecimalDigits();
        UFDouble shareAmount = (UFDouble) billCardPanel.getHeadItem(MatterAppVO.ORIG_AMOUNT).getValueObject();
        if (shareAmount == null || shareAmount.compareTo(UFDouble.ZERO_DBL) <= 0) {
            throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","2201BTP-0014")/*@res "表头的金额应大于0!"*/);
        }
        shareAmount = shareAmount.setScale(nDecimalDigit, BigDecimal.ROUND_HALF_UP);
        setShareAmount(shareAmount);
        setTotalAmount(shareAmount);
        String refPK = (String) billCardPanel.getHeadItem(MatterAppVO.PK_ORG).getValueObject();
        setRefPK(refPK);
        setSelectvo(new MtAppDetailVO());
    }
	
	
	/**
	 * 部分快速分摊 
	 * @param vos
	 * @throws ValidationException
	 */
    @SuppressWarnings("deprecation")
	protected void shareByPart(SuperVO[] vos) throws ValidationException {
        SuperVO[] updatevos = fillMattDetailVOs(vos);
        BillCardPanel billCardPanel = getEditor().getBillCardPanel();
        BillModel billModel = billCardPanel
                .getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
        billModel.clearRowData(selectRow, null);
        if (billModel.getRowState(selectRow) == BillModel.NORMAL)
            billModel.setRowState(selectRow, BillModel.MODIFICATION);
        getEditor().getBillCardPanel().copyLine();
        for (int i = 1; i < updatevos.length; i++) {
            billCardPanel.pasteLine(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
        }

        int lastIndex = updatevos.length - 1;
		for (int i = 0; i < updatevos.length; i++) {
			int curRow = selectRow + i;
			if (i < lastIndex) {
				updatevos[i].setAttributeValue(updatevos[i].getPKFieldName(), null);
			}

			billModel.setBodyRowVO(updatevos[i], curRow);

			// 如果是利润中心为空，则根据部门带成本中心和利润中心
			if (updatevos[i].getAttributeValue(MtAppDetailVO.PK_PCORG) == null) {
				ErmForMatterAppUtil.setCostCenter(curRow, getEditor().getBillCardPanel());
			}
		}
        billModel.loadLoadRelationItemValue();
        //重新计算本币金额
        for (int i = selectRow; i < updatevos.length+selectRow; i++) {
        	 ((MatterAppMNBillForm) getEditor()).resetCardBodyRate(i);
        	 ((MatterAppMNBillForm)getEditor()).resetCardBodyAmount(i);
        }
        
        //重新计算表体最大金额
        ((MatterAppMNBillForm)getEditor()).resetBodyMaxAmount();
        
		matchData(updatevos, billCardPanel);
	}
    
    
//    /**
//	 * 是否设置了百分比
//	 * 
//	 * @param pk_group
//	 * @param pk_currtype
//	 * @param total
//	 */
//	private UFBoolean isPercentage(BillCardPanel billCardPanel) {
//		// 交易类型
//		if(percentage==null){
//			String pk_tradetype = (String) billCardPanel.getHeadItem(MatterAppVO.PK_TRADETYPE).getValueObject();
//			String pk_group = (String) billCardPanel.getHeadItem(MatterAppVO.PK_GROUP).getValueObject();
//			DjLXVO djlx = null;
//			try {
//				djlx = ErmDjlxCache.getInstance().getDjlxVO(pk_group, pk_tradetype);
//			} catch (BusinessException e) {
//				this.exceptionHandler.handlerExeption(e);
//			}
//			if (djlx == null) {
//				this.exceptionHandler.handlerExeption(new BusinessException("交易类型不存在，请检查数据：" + pk_tradetype));
//			}
//			
//			// 根据交易类型上设置的允许报销百分比，计算最大允许报销值
//			UFDouble bx_percentage = djlx.getBx_percentage();
//			this.percentage=bx_percentage == null ? UFBoolean.FALSE : UFBoolean.TRUE;
//		}
//		
//		return percentage;
//	}
    
    /**
     * 整单快速分摊
     * @param vos
     * @throws ValidationException
     */
    private void shareByWhole(SuperVO[] vos) throws ValidationException {
        setTabbedPaneSelected(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
        SuperVO[] updatevos = fillMattDetailVOs(vos);

        BillCardPanel billCardPanel = getEditor().getBillCardPanel();
        BillModel billModel = billCardPanel
                .getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
        BillScrollPane sp = billCardPanel.getBodyPanel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
        int rowCount = billCardPanel.getRowCount();
        for (int nRow = 0; nRow < rowCount; nRow++) {
            sp.getTable().changeSelection(0, 0, false, false);
            billCardPanel.delLine();
        }

        for (int i = 0; i < updatevos.length; i++) {
            billCardPanel.addLine(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
        }
        
		for (int i = 0; i < updatevos.length; i++) {
			billModel.setBodyRowVO(updatevos[i], i);

			// 如果是利润中心为空，则根据部门带成本中心和利润中心
			if (updatevos[i].getAttributeValue(MtAppDetailVO.PK_PCORG) == null) {
				ErmForMatterAppUtil.setCostCenter(i, getEditor().getBillCardPanel());
			}
		}

		billModel.loadLoadRelationItemValue();
		for (int i = 0; i < updatevos.length; i++) {
			((MatterAppMNBillForm) getEditor()).resetCardBodyRate(i);
			((MatterAppMNBillForm) getEditor()).resetCardBodyAmount(i);
		}

		// 重新最大金额
		((MatterAppMNBillForm) getEditor()).resetBodyMaxAmount();

		matchData(updatevos, billCardPanel);
	}
    
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
    
    /**
     * 补齐申请单的表体
     * @param vos
     * @return
     * @throws ValidationException
     */
    private SuperVO[] fillMattDetailVOs(SuperVO[] vos) throws ValidationException {
        String fydwbmHead = (String)getHeadValue(MatterAppVO.PK_ORG);
        UFDouble sum;
        if (totalAmount.compareTo(shareAmount) == 0) {
            sum = new UFDouble(ONE_HUNDRED);
        } else {
            sum = shareAmount.div(totalAmount).multiply(ONE_HUNDRED);
        }
        sum = sum.setScale(2, BigDecimal.ROUND_HALF_UP);
        for (SuperVO vo : vos) {
            UFDouble tmpAmount = (UFDouble) vo.getAttributeValue(MtAppDetailVO.ORIG_AMOUNT);
            UFDouble tmpRatio = tmpAmount.div(totalAmount).multiply(100);
            tmpRatio = tmpRatio.setScale(2, BigDecimal.ROUND_HALF_UP);
            
            UFDouble tmpSum = sum;
            tmpSum = tmpSum.setScale(2, BigDecimal.ROUND_HALF_UP);
            sum = sum.sub(tmpRatio);
            //不够补齐
            
            if (sum.compareTo(UFDouble.ZERO_DBL) <= 0) {
                sum = UFDouble.ZERO_DBL;
                tmpRatio = tmpSum;
            } 
            
            //如果是最后一个并且金额加上
//            if ((nPos == vos.length - 1)
//                    && (UFDouble.ZERO_DBL.compareTo(sum) != 0)) {
//                tmpRatio = tmpRatio.add(sum);
//            }
            
            if (UFDouble.ZERO_DBL.compareTo(tmpAmount) == 0) {
                tmpRatio = UFDouble.ZERO_DBL;
            }
            
			// 重新换算比例
			vo.setAttributeValue(MtAppDetailVO.SHARE_RATIO, tmpRatio);
			// 设置集团默认值（集团为必填项）
			vo.setAttributeValue(MtAppDetailVO.PK_GROUP, getHeadValue(MatterAppVO.PK_GROUP));
			if (vo.getAttributeValue(MtAppDetailVO.ASSUME_ORG) == null) {
				vo.setAttributeValue(MtAppDetailVO.ASSUME_ORG, getHeadValue(MatterAppVO.PK_ORG));
			}
			if (vo.getAttributeValue(MtAppDetailVO.PK_CURRTYPE) == null) {
				vo.setAttributeValue(MtAppDetailVO.PK_CURRTYPE, getBzbm());
			}

			String assumeOrg = (String) vo.getAttributeValue(MtAppDetailVO.ASSUME_ORG);
			if (assumeOrg != null && assumeOrg.equals(fydwbmHead)) {// 根据表头带入默认值
				Map<String, String> fieldMap = getHeadToBodyFieldMap();
				for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
					if (vo.getAttributeValue(entry.getValue()) == null) {
						vo.setAttributeValue(entry.getValue(), getHeadValue(entry.getKey()));
					}
				}
			}
        }
        return vos;
    }
    
	private Map<String, String> getHeadToBodyFieldMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(MatterAppVO.APPLY_DEPT, MtAppDetailVO.ASSUME_DEPT);
		return map;
	}
	


	/**
	 * 设置交易类型和单据类型
	 * @param billCardPanel
	 */
	private void setMattDjlx(BillCardPanel billCardPanel) {
		String djlx = (String)billCardPanel.getHeadItem(MatterAppVO.PK_TRADETYPE).getValueObject();
        setDjlx(djlx);
        setParentDjlx(ErmMatterAppConst.MatterApp_BILLTYPE);
	}
	
	
    private void handleMatterBillItem(AggshareruleVO aggvo) throws BusinessException {
        BillCardPanel billCardPanel = getEditor().getBillCardPanel();
        BillItem[] items = billCardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL).getBodyItems();
        if (items != null) {
        	FieldcontrastVO qryVO = new FieldcontrastVO();
            qryVO.setApp_scene(ErmBillFieldContrastCache.FieldContrast_SCENE_SHARERULEField);
            qryVO.setSrc_billtype(getDjlx());
            qryVO.setSrc_busitype(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
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
                throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","2201BTP-0013")/*@res "分摊规则的部分分摊对象没有显示，请修改单据模板或改选其他分摊规则！"*/);
        }
    }
	
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void matchData(SuperVO[] updatevos, BillCardPanel billCardPanel) {
		for (int i = 0; i < updatevos.length; i++) {
			BillItem[] items = billCardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL).getBodyItems();
			for (BillItem item : items) {
				Object itemVal = updatevos[i].getAttributeValue(item.getKey());
				if (itemVal == null)
					continue;
				if (item.getComponent() instanceof UIRefPane) {
					UIRefPane refPane = (UIRefPane) item.getComponent();
					AbstractRefModel refModel = refPane.getRefModel();
					if (refModel != null && !(refModel instanceof nc.ui.org.ref.OrgBaseTreeDefaultRefModel)) {
						if(MtAppDetailVO.PK_RESACOSTCENTER.equals(item.getKey()) || 
								MtAppDetailVO.PK_CHECKELE.equals(item.getKey())){
							String pcorg = (String) updatevos[i].getAttributeValue(MtAppDetailVO.PK_PCORG);
							refModel.setPk_org(pcorg);
							if(MtAppDetailVO.PK_RESACOSTCENTER.equals(item.getKey())){
								refModel.setWherePart("pk_profitcenter= '"+pcorg+"'");
							}
						
						}else{
							String pkOrg = (String) updatevos[i].getAttributeValue(MtAppDetailVO.ASSUME_ORG);
							if (StringUtil.isEmpty(pkOrg)) {
								pkOrg = (String) getHeadValue(MatterAppVO.PK_ORG);
							}
							refModel.setPk_org(pkOrg);
						}
						refModel.setMatchPkWithWherePart(true);
						refModel.setPKMatch(true);
						Vector vec = refModel.matchPkData(itemVal.toString());
						if (vec == null || vec.isEmpty()) {
							updatevos[i].setAttributeValue(item.getKey(), null);

							int row = 0;
							if (shareType == SHARE_TYPE_PART) {
								row = selectRow + i;
							} else {
								row = i;
							}
							billCardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL).setValueAt(null, row, item.getKey());
						}
					}
				}
			}
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
    

	
    public BillForm getEditor() {
        return editor;
    }
    
    public void setEditor(BillForm editor) {
        this.editor = editor;
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

	public String getCurrPage() {
		return currPage;
	}

	public void setCurrPage(String currPage) {
		this.currPage = currPage;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public int getSelectRow() {
		return selectRow;
	}

	public void setSelectRow(int selectRow) {
		this.selectRow = selectRow;
	}

	public SuperVO getSelectvo() {
		return selectvo;
	}

	public void setSelectvo(SuperVO selectvo) {
		this.selectvo = selectvo;
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

	public int getShareType() {
		return shareType;
	}

	public void setShareType(int shareType) {
		this.shareType = shareType;
	}

	public UFDouble getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(UFDouble totalAmount) {
		this.totalAmount = totalAmount;
	}

}
