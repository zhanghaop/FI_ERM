package nc.ui.erm.billpub.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.sharerule.ShareruleConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.bs.uif2.validation.Validator;
import nc.pubitf.erm.sharerule.IErShareruleQuery;
import nc.ui.er.ref.ShareruleRefModel;
import nc.ui.erm.sharerule.validator.RatioValidator;
import nc.ui.erm.sharerule.validator.ShareruleValidator;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.fipub.BillCardPanelTool;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.erm.sharerule.ShareruleDataVO;
import nc.vo.erm.sharerule.ShareruleObjVO;
import nc.vo.erm.sharerule.ShareruleVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

@SuppressWarnings("restriction")
public class RapidShareDialog extends UIDialog implements ValueChangedListener {

	private static final long serialVersionUID = 1L;

	private UIPanel contentPanel = null;
	private UIPanel btnPanel = null;
	private UIPanel topPanel = null;
	private UIPanel moneyUI = null;
	private UITextField moneyField = null;
	private UIPanel sRuleUI = null;
	private UIRefPane sRuleRefPane = null;
	private UIButton btnOK = null;
	private UIButton btnCancel = null;
	private BillCardPanel listPanel;
	private AbstractAppModel model;
	private UFDouble shareAmount = UFDouble.ZERO_DBL;
	private AggshareruleVO aggvo = null;
	private String refOrg;
	private DefaultValidationService validateService = new DefaultValidationService();

	private transient IExceptionHandler exceptionHanler = new DefaultExceptionHanler();

	private static final List<String> ruleTypeFields = new ArrayList<String>();
	static {
		ruleTypeFields.add(ShareruleDataVO.SHARE_RATIO);
		ruleTypeFields.add(ShareruleDataVO.ASSUME_AMOUNT);
	}
	
	public DefaultValidationService getValidateService() {
        return validateService;
    }

    @SuppressWarnings("deprecation")
	public RapidShareDialog(AbstractAppModel model, UFDouble shareAmount, String refOrg) {
		super();
		this.model = model;
		this.refOrg = refOrg;
		this.shareAmount = shareAmount;
		List<Validator> validators = new ArrayList<Validator>(2);
		validators.add(new ShareruleValidator());
		validators.add(new RatioValidator());
		validateService.setValidators(validators);
		initialize();
	}

	private void initialize() {
		this.setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0025")/*@res "选择分摊规则"*/);
		this.setSize(new Dimension(542, 349));
		this.setResizable(true);
		this.setContentPane(getContentPanel());
	}

	private UIPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new UIPanel();
			contentPanel.setLayout(new BorderLayout());
			contentPanel.add(getTopPanel(), BorderLayout.NORTH);
			contentPanel.add(getMainPane(), BorderLayout.CENTER);
			contentPanel.add(getBtnPanel(), BorderLayout.SOUTH);
		}
		return contentPanel;
	}

	private UIPanel getBtnPanel() {
		if (btnPanel == null) {
			btnPanel = new UIPanel();
			btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			// btnPanel.setPreferredSize(new Dimension(10, 30));
			btnPanel.add(getBtnOK());
			btnPanel.add(getBtnCancel());
		}
		return btnPanel;
	}

	private UIPanel getMainPane() {
		return getBillCardPanel();
	}

	private UIPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new UIPanel();
			topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			topPanel.add(getMoneyUI());
			topPanel.add(getSRuleUI());
		}
		return topPanel;
	}

	private UIPanel getMoneyUI() {
		if (moneyUI == null) {
			moneyUI = new UIPanel();
			moneyUI.add(new UILabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0026")/*@res "分摊金额"*/));
			moneyUI.add(getMoneyField());
		}
		return moneyUI;
	}

	private UIPanel getSRuleUI() {
		if (sRuleUI == null) {
			sRuleUI = new UIPanel();
			sRuleUI.add(new UILabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0027")/*@res "分摊规则"*/));
			sRuleUI.add(getSRuleRefPane());

		}
		return sRuleUI;
	}

	private UITextField getMoneyField() {
		if (moneyField == null) {
			moneyField = new UITextField();
            moneyField.setPreferredSize(new Dimension(130, 20));
            if (shareAmount != null) {
                moneyField.setValue(shareAmount.toString());
            }
			moneyField.setEditable(false);
		}
		return moneyField;
	}

	private UIRefPane getSRuleRefPane() {
		if (sRuleRefPane == null) {
			sRuleRefPane = new UIRefPane();
			sRuleRefPane.setLocation(578, 458);
			sRuleRefPane.setIsCustomDefined(true);
			sRuleRefPane.setVisible(true);
			sRuleRefPane.setRefNodeName("分摊规则");
			sRuleRefPane.setRefModel(new ShareruleRefModel());
			sRuleRefPane.getRefModel().setWherePart(
					"  ( pk_group='" + ErUiUtil.getPK_group() + "' and pk_org in ('" + refOrg + "','"
							+ ErUiUtil.getPK_group() + "'))");
			sRuleRefPane.addValueChangedListener(this);
		}
		return sRuleRefPane;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
	}

	/**
	 * This method initializes btnOK
	 *
	 * @return nc.ui.pub.beans.UIButton+
	 *
	 */
	private UIButton getBtnOK() {
		if (btnOK == null) {
			btnOK = new UIButton();
			btnOK.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC001-0000044")/*
																										 * @res
																										 * "确定"
																										 */);
			btnOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					closeOK();
				}
			});
		}
		return btnOK;
	}

	/**
	 * This method initializes btnCancel
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	private UIButton getBtnCancel() {
		if (btnCancel == null) {
			btnCancel = new UIButton();
			btnCancel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("common", "UC001-0000008")/*
														 * @res "取消"
														 */);
			btnCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					closeCancel();
				}
			});
		}
		return btnCancel;
	}

	private nc.ui.pub.bill.BillCardPanel getBillCardPanel() {
		if (listPanel == null) {
			listPanel = new BillCardPanel();
			listPanel.setName("LIST");
			loadBillListTemplate();
		}

		return listPanel;
	}

	private void loadBillListTemplate() {
		String nodecode = BXConstans.BXMNG_NODECODE;
		String loginUser = getModel().getContext().getPk_loginUser();
		String pk_group = getModel().getContext().getPk_group();
		listPanel.loadTemplet(nodecode, null, loginUser, pk_group, "SRchoose");
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		if (event.getSource() == sRuleRefPane) {
			String pk_sharerule = sRuleRefPane.getRefPK();
			IErShareruleQuery iq = (IErShareruleQuery) NCLocator.getInstance()
					.lookup(IErShareruleQuery.class);
			try {
				aggvo = iq.queryByPK(pk_sharerule);
				if (aggvo == null) {
					return;
				}
				// 主表VO
				ShareruleVO srulevo = (ShareruleVO) aggvo.getParentVO();
				// 分摊对象VOS
				ShareruleObjVO[] sruleobjvos = (ShareruleObjVO[]) aggvo.getTableVO(aggvo.getTableCodes()[0]);
				List<String> ruleobjcodes = new ArrayList<String>();
				for (ShareruleObjVO vo : sruleobjvos) {
					ruleobjcodes.add(vo.getFieldcode());
				}
				getBillCardPanel().getBodyPanel().setAutoAddLine(true);
				showRuleObjField(ruleobjcodes);
				showRuleTypeFiled(srulevo.getRule_type());
				// 设置表体分摊规则数据
				ShareruleDataVO[] shareruledatavos = (ShareruleDataVO[]) aggvo.getTableVO(aggvo
						.getTableCodes()[1]);
				getBillCardPanel().getBillModel().clearBodyData();
				if (shareruledatavos != null && shareruledatavos.length > 0) {
					for (int i = 0; i < shareruledatavos.length; i++) {
						getBillCardPanel().addLine();
					}
					getBillCardPanel().getBillModel().setBodyRowObjectByMetaData(shareruledatavos, 0);
				}
                getBillCardPanel().getBodyPanel().setAutoAddLine(false);
                getBillCardPanel().setBodyMenuShow(false);
			} catch (BusinessException e) {
				exceptionHanler.handlerExeption(e);
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

	private void showRuleTypeFiled(int ruletype) {
		for (String field : ruleTypeFields) {
			getBillCardPanel().getBodyItem(field).setShow(false);
		}
		String ruleTypeField = getRuleTypeField(ruletype);
		if (!StringUtil.isEmpty(ruleTypeField)) {
			getBillCardPanel().getBodyItem(ruleTypeField).setShow(true);
		}
		// 刷新
		BillCardPanelTool.setBillData(getBillCardPanel(), getBillCardPanel().getBillData());
	}

	private void showRuleObjField(List<String> ruleobjcodes) {
		// 先将表体除分摊比例和分摊金额字段的其他字段隐藏
		BillItem[] items = getBillCardPanel().getBodyItems();
		for (BillItem item : items) {
			if (ruleTypeFields.contains(item.getMetaDataAccessPath())) {
				continue;
			}
			if (ruleobjcodes.contains(item.getMetaDataAccessPath())) {
				item.setShow(true);
				if (item.getComponent() instanceof UIRefPane) {
					UIRefPane ref = (UIRefPane) item.getComponent();
					if (ref.getRefModel() != null) {
						ref.getRefModel().setPk_org(getModel().getContext().getPk_org());
						ref.getRefModel().setPk_group(getModel().getContext().getPk_group());
					}
				}
				item.setEnabled(false);
			} else {
				item.setShow(false);
			}
		}
		// 刷新
		BillCardPanelTool.setBillData(getBillCardPanel(), getBillCardPanel().getBillData());
	}

	public AggshareruleVO getAggvo() {
		return aggvo;
	}

	@Override
    public void closeOK() {
	    ShareruleDataVO[] srcShareruleDataVOArray =
            (ShareruleDataVO[])this.listPanel.getBillData().getBillObjectByMetaData();
	    if (aggvo == null) {
            MessageDialog.showHintDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0028")/*@res "提示"*/, 
                    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0154")/*@res "分摊规则不允许为空"*/);
            return;
	    }
        aggvo.setTableVO(aggvo.getTableCodes()[1], srcShareruleDataVOArray);
        try {
            validateService.validate(aggvo);
        } catch (ValidationException e) {
            MessageDialog.showHintDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0028")/*@res "提示"*/, e.getMessage());
            return;
        }
        super.closeOK();
    }

    public void setAggvo(AggshareruleVO aggvo) {
		this.aggvo = aggvo;
	}
}