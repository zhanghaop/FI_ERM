package nc.ui.er.expensetype.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.er.expensetype.ref.ControlAreaRefModel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.IAppModelDataManager;

public class ControlAreaOrgPanel extends UIPanel implements
		ValueChangedListener, AppEventListener {

	/**
	 * @author liansg
	 */
	private static final long serialVersionUID = 1L;

	private UIRefPane refPane = null;
	private String labelName = null;// ×¢Éä
	private UILabel label = null;
	private IAppModelDataManager dataManager;// ×¢Éä
	private AbstractUIAppModel model;// ×¢Éä
	public IExceptionHandler exceptionHandler;

	public ControlAreaOrgPanel() {
		setLayout(new BorderLayout());
	}

	public void initUI() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(getLabel());
		add(getRefPane());
		exceptionHandler = new DefaultExceptionHanler(this);
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	protected UILabel getLabel() {
		if (label == null) {
			label = new UILabel();
			label.setText(labelName);
		}
		return label;
	}

	// @Override
	public UIRefPane getRefPane() {
		if (refPane == null) {
			String pk_group = getModel().getContext().getPk_group();
			ControlAreaRefModel refModel = new ControlAreaRefModel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0012")/*@res "¹Ü¿Ø·¶Î§"*/);
			refModel.setControlareaOfGroup(pk_group);
			refPane = new UIRefPane();
			refPane.setPreferredSize(new Dimension(200, 20));
			refPane.setRefModel(refModel);
			refPane.addValueChangedListener(this);
			refPane.setButtonFireEvent(true);
		}
		return refPane;
	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public IAppModelDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IAppModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public void handleEvent(AppEvent event) {
		if (AppEventConst.UISTATE_CHANGED == event.getType()) {
			if (getModel().getUiState() == UIState.ADD
					|| getModel().getUiState() == UIState.EDIT
					|| model.getUiState() == UIState.DISABLE) {
				getRefPane().setEnabled(false);
			} else {
				getRefPane().setEnabled(true);
			}
		}
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		try {
			String pk_org = getRefPane().getRefPK();
			model.getContext().setPk_org(pk_org);
			if (pk_org != null) {
				dataManager.initModel();
			}
		} catch (Exception e) {
			exceptionHandler.handlerExeption(e);
		}
	}

}