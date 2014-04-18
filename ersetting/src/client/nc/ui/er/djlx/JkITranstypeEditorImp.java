package nc.ui.er.djlx;

import nc.vo.er.djlx.DjLXVO;

public class JkITranstypeEditorImp extends BxITranstypeEditorImp {
	protected String getDjdl() {
		return "jk";
	}
	public String getNodecode() {
		return "20110005";
	}
	protected void inittemplate() {
		super.inittemplate();
		try{
			getcardpanel().getBillCardPanelDj().getHeadItem("isbankrecive").setShow(false);
		}catch(Exception e){
		}
	}
	protected void initdefalutvalue(DjLXVO head) {
		head.setUsesystem(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000369")/*@res "报销管理"*/);

	}
	protected String getDjlxPanelClassName() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

		return "nc.ui.er.djlx.DjlxPanel";
	}
}