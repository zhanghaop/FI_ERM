package nc.ui.erm.fieldcontrast.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import nc.bs.erm.util.ErmBillTypeUtil;
import nc.ui.pub.beans.RefEditEvent;
import nc.ui.pub.beans.RefEditListener;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
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


public class FieldBilRefPanel extends UIPanel implements ValueChangedListener,ItemListener ,AppEventListener{

	private static final long serialVersionUID = 1L;
	private UIRefPane djlxRef;
	private UILabel jLabel;
	private UILabel bLabel;
	private UIComboBox bcombobox;
	private BatchBillTableModel model;
	public final static String ITEMCHANGED="Item_Changed";
	public final static String CTRLCHANGED="CtrlObj_Changed";
	public final static int CTRLSTATE= -1;//控制维度
	public final static int SHARESTATE= 0;//分摊对象

	public void initUI(){
		FlowLayout flowLayout1 = new FlowLayout();
		flowLayout1.setAlignment(FlowLayout.LEFT);
		bLabel = new UILabel();
		bLabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0007")/*@res "业务场景"*/);
	    bcombobox = new UIComboBox();
	    bcombobox.setPreferredSize(new Dimension(160, bcombobox.getHeight()));
//        bcombobox.addItem(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0008")/*@res "控制维度"*/);
        bcombobox.addItem(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0009")/*@res "分摊对象"*/);
        bcombobox.addItemListener(this);
		jLabel = new UILabel();
		jLabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0010")/*@res "控制对象"*/);
		setLayout(flowLayout1);
		setName("contrastfieldpane");
		add(bLabel);
		add(bcombobox);
		add(jLabel);
		add(getDjlxRef());
		setSize(639, 363);
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
		}
		djlxRef.addRefEditListener(new RefEditListener() {

			@Override
			public boolean beforeEdit(RefEditEvent event) {
				if (getBcombobox().getSelectedIndex() == CTRLSTATE) {// 控制维度
					String insql = ErmBillTypeUtil.getBilltypeRuleWhereSql(null,true);
					String wherePart = insql +"and isnull(islock,'N') ='N' and  ( pk_group='"
									+ getModel().getContext().getPk_group() + "' or pk_org='GLOBLE00000000000000' )";
					djlxRef.getRefModel().setWherePart(wherePart);
//					djlxRef.getRefModel().setWherePart(
//					"  ncbrcode in ('bx','jk') and pk_billtypecode <> '2647' and isnull(islock,'N') ='N' and  ( pk_group='"
//							+ getModel().getContext().getPk_group() + "' or pk_org='GLOBLE00000000000000' )");
				} else if (getBcombobox().getSelectedIndex() == SHARESTATE) {// 分摊对象
					BilltypeRuleVO rulevo = ErmBillTypeUtil.getBilltypeRuleVO(BilltypeRuleVO.FIELDCONTRAST_SHARESTATE);
					String insql = ErmBillTypeUtil.getBilltypeRuleWhereSql(rulevo,true);
					String wheresql = insql +"and isnull(islock,'N') ='N' and  ( pk_group='"
					+ getModel().getContext().getPk_group() + "' or pk_org='GLOBLE00000000000000' )";
					djlxRef.getRefModel().setWherePart(wheresql);
//					djlxRef.getRefModel().setWherePart(
//							"  ncbrcode in ('bx','jk','cs') and pk_billtypecode <> '2647' and isnull(islock,'N') ='N' and  ( pk_group='"
//									+ getModel().getContext().getPk_group() + "' or pk_org='GLOBLE00000000000000' )");
				}
				return true;
			}

		});
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
		if(ItemEvent.SELECTED ==e.getStateChange()){
			getModel().fireEvent(new AppEvent(ITEMCHANGED, this, getBcombobox().getSelectedIndex()));
		}
	}

	@Override
	public void handleEvent(AppEvent event) {
		if(AppEventConst.UISTATE_CHANGED.equals(event.getType())){
			UIStateChangeEvent newState =  (UIStateChangeEvent) event;
			boolean newUIState = !(UIState.EDIT == newState.getNewState()|| UIState.ADD == newState.getNewState());
			getDjlxRef().setEnabled(newUIState);
			getBcombobox().setEnabled(newUIState);
		}
	}
}