package nc.ui.arap.bx.print;


import java.util.List;

import nc.ui.pub.print.IMetaDataDataSource;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.HierachicalDataAppModel;

public class MetaDataAllDatasSource implements IMetaDataDataSource {

	/**
		 * 
		 */
	private static final long serialVersionUID = -6708685729328036658L;

	private AbstractUIAppModel model;

	private Object[] objs = null;

	public MetaDataAllDatasSource() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getMDObjects() {
		if (getModel() instanceof HierachicalDataAppModel)
			objs = ((HierachicalDataAppModel) getModel()).getAllDatas();
		else if (getModel() instanceof BatchBillTableModel) {
			List datas = ((BatchBillTableModel) getModel()).getRows();
			objs = datas.toArray();
		} else if (getModel() instanceof BillManageModel) {
			List datas = ((BillManageModel) getModel()).getData();
			objs = datas.toArray();
		}
		return objs;
	}

	@Override
	public String[] getAllDataItemExpress() {
		return null;
	}

	@Override
	public String[] getAllDataItemNames() {
		return null;
	}

	@Override
	public String[] getDependentItemExpressByExpress(String itemExpress) {
		return null;
	}

	@Override
	public String[] getItemValuesByExpress(String itemExpress) {
		return null;
	}

	@Override
	public String getModuleName() {
		return null;
	}

	@Override
	public boolean isNumber(String itemExpress) {
		return false;
	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
	}
}
