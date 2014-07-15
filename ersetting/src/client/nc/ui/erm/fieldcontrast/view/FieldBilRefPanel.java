package nc.ui.erm.fieldcontrast.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import nc.bs.erm.util.ErmBillTypeUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.erm.fieldcontrast.IFieldContrastQryService;
import nc.ui.pub.beans.RefEditEvent;
import nc.ui.pub.beans.RefEditListener;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIDialogFactory;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextAreaScrollPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.UIState;
import nc.ui.uif2.UIStateChangeEvent;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.vo.bx.pub.ref.BXBilltypeRefModel;
import nc.vo.ep.bx.BilltypeRuleVO;

public class FieldBilRefPanel extends UIPanel implements ValueChangedListener, ItemListener, AppEventListener {

	private static final long serialVersionUID = 1L;
	private UIRefPane djlxRef;
	private UILabel jLabel;
	private UILabel bLabel;
	private UIComboBox bcombobox;
	private BatchBillTableModel model;
	public final static String ITEMCHANGED = "Item_Changed";
	public final static String CTRLCHANGED = "CtrlObj_Changed";
	public final static int CTRLSTATE = -1;// 控制维度
	public final static int SHARESTATE = 0;// 分摊对象
	public final static int BUDGETSTATE = 1;// 预算字段

	public void initUI() {
		FlowLayout flowLayout1 = new FlowLayout();
		flowLayout1.setAlignment(FlowLayout.LEFT);
		bLabel = new UILabel();
		bLabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0007")/*
																										 * @
																										 * res
																										 * "业务场景"
																										 */);
		bcombobox = new UIComboBox();
		bcombobox.setPreferredSize(new Dimension(160, bcombobox.getHeight()));
		// bcombobox.addItem(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0008")/*@res
		// "控制维度"*/);
		bcombobox.addItem(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0009")/*
																											 * @
																											 * res
																											 * "分摊对象"
																											 */);
//		bcombobox.addItem("预算维度对照");
		bcombobox.addItemListener(this);
		jLabel = new UILabel();
		jLabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0010")/*
																										 * @
																										 * res
																										 * "控制对象"
																										 */);
		setLayout(flowLayout1);
		setName("contrastfieldpane");
		add(bLabel);
		add(bcombobox);
		add(jLabel);
		add(getDjlxRef());
		setSize(639, 363);

		// 附加隐藏功能，用于查看报销单自定义项被使用情况
		bLabel.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				int clickCount = e.getClickCount();
				Logger.error("+++++++++clickCount++++++++:" + clickCount);
				if (clickCount > 3) {
					try {
						UIDialog dlg = UIDialogFactory.newDialogInstance(UIDialog.class, FieldBilRefPanel.this,
								nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("voucherclient1_0", "02002005-0133")/*
																														 * @
																														 * res
																														 * "系统信息"
																														 */);
						dlg.getContentPane().setLayout(new BorderLayout());
						dlg.setResizable(true);
						UITextAreaScrollPane textArea = new UITextAreaScrollPane();
						dlg.getContentPane().add(textArea);

						IFieldContrastQryService qrySer = NCLocator.getInstance().lookup(IFieldContrastQryService.class);

						textArea.setText(qrySer.getUserDefItemUseInfo());
						dlg.showModal();
					} catch (Exception ex) {
						Logger.error(ex.getMessage(), ex);
					}
				}
			}
		});
	}

	public UIRefPane getDjlxRef() {
		if (this.djlxRef == null) {
			this.djlxRef = new UIRefPane();
			this.djlxRef.setName("trantsyperef");
			this.djlxRef.setLocation(578, 458);
			this.djlxRef.setIsCustomDefined(true);
			this.djlxRef.setVisible(true);
			this.djlxRef.setRefModel(new BXBilltypeRefModel());
			this.djlxRef.setPreferredSize(new Dimension(160, djlxRef.getHeight()));
			djlxRef.addValueChangedListener(this);
			
			djlxRef.addRefEditListener(new RefEditListener() {
				@Override
				public boolean beforeEdit(RefEditEvent event) {
					if (getBcombobox().getSelectedIndex() == CTRLSTATE) {// 控制维度
						String insql = ErmBillTypeUtil.getBilltypeRuleWhereSql(null, true);
						String wherePart = insql + "and isnull(islock,'N') ='N' and  ( pk_group='" + getModel().getContext().getPk_group() + "' or pk_org='GLOBLE00000000000000' )";
						djlxRef.getRefModel().setWherePart(wherePart);
					} else if (getBcombobox().getSelectedIndex() == SHARESTATE) {// 分摊对象
						BilltypeRuleVO rulevo = ErmBillTypeUtil.getBilltypeRuleVO(BilltypeRuleVO.FIELDCONTRAST_SHARESTATE);
						String insql = ErmBillTypeUtil.getBilltypeRuleWhereSql(rulevo, true);
						String wheresql = insql + "and isnull(islock,'N') ='N' and  ( pk_group='" + getModel().getContext().getPk_group() + "' or pk_org='GLOBLE00000000000000' )";
						djlxRef.getRefModel().setWherePart(wheresql);
					} else if (getBcombobox().getSelectedIndex() == BUDGETSTATE) {// 预算控制
						String wheresql = " pk_billtypecode in (select distinct(src_billtype) from ER_FIELDCONTRAST where ER_FIELDCONTRAST.APP_SCENE = 1) and  ( pk_group='"
								+ getModel().getContext().getPk_group() + "' or pk_org='GLOBLE00000000000000' )";
						djlxRef.getRefModel().setWherePart(wheresql);
					}
					return true;
				}

			});
		}
		
		return djlxRef;
	}

	public void setBcombobox(UIComboBox bcombobox) {
		this.bcombobox = bcombobox;
	}

	public UIComboBox getBcombobox() {
		return bcombobox;
	}

	public void setModel(BatchBillTableModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public BatchBillTableModel getModel() {
		return model;
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		getModel().fireEvent(new AppEvent(CTRLCHANGED, this, event.getNewValue()));
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (ItemEvent.SELECTED == e.getStateChange()) {
			getModel().fireEvent(new AppEvent(ITEMCHANGED, this, getBcombobox().getSelectedIndex()));
		}
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (AppEventConst.UISTATE_CHANGED.equals(event.getType())) {
			UIStateChangeEvent newState = (UIStateChangeEvent) event;
			boolean newUIState = !(UIState.EDIT == newState.getNewState() || UIState.ADD == newState.getNewState());
			getDjlxRef().setEnabled(newUIState);
			getBcombobox().setEnabled(newUIState);
		}
	}
}