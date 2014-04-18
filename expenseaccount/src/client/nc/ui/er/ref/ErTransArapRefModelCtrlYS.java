package nc.ui.er.ref;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.para.IParaEditComponentCtrl;

public class ErTransArapRefModelCtrlYS implements IParaEditComponentCtrl {

	public ErTransArapRefModelCtrlYS() {
	}

	public void initComponentProp(Object component, Object parameter) {
		if (((UIRefPane) component).getRefModel() != null) {
			(((UIRefPane) component).getRefModel()).setWherePart(" and arap_djlx.djdl='ys'");
		}
	}
}