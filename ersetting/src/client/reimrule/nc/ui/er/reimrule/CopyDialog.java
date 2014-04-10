/**
 *
 */
package nc.ui.er.reimrule;

import java.awt.Container;

import javax.swing.border.EtchedBorder;

import nc.ui.er.basepub.AbstractOKCancelDlg;
import nc.ui.pf.pub.TranstypeRefModel;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;

class CopyDialog extends AbstractOKCancelDlg{

	private final ReimRuleUI ruleUI;
	
	
	@Override
	@SuppressWarnings("deprecation")
	protected boolean onBoOK() {
		String corp = getCorpRef().getRefPK();
		String djlx = getDjlxRef().getRefPK();
		if(corp==null || djlx==null){
			this.ruleUI.showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000439")/*@res "请选择目标公司和交易类型!"*/);
			return false;
		}
		return true;
	}
	public CopyDialog(ReimRuleUI ruleUI, Container parent) {
		super(parent);
		this.ruleUI = ruleUI;
	}
	private static final long serialVersionUID = 1L;
	private UIPanel contentPanel;
	private UIRefPane corpRef;
	private UIRefPane djlxRef;
	private UILabel corpText;
	private UILabel djlxText;
	@Override
	protected UIPanel getNorthPanel() {
		if (contentPanel == null) {
			try {
				contentPanel = new UIPanel();
				contentPanel.setName("contentPanel");
				contentPanel.setPreferredSize(new java.awt.Dimension(330, 140));
				contentPanel.setBorder(new EtchedBorder());
				contentPanel.setLayout(null);
				contentPanel.add(getCorpText());
				contentPanel.add(getCorpRef());
				contentPanel.add(getDjlxText());
				contentPanel.add(getDjlxRef());
			} catch (Throwable ex) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(ex);;
			}
		}
		return contentPanel;
	}
	@Override
	protected int initDlgWidth() {
		return 330;
	}
	@Override
	protected int initDlgHigh() {
		return 160;
	}
	public UILabel getCorpText() {
		if (corpText == null) {
			corpText = new UILabel();
			corpText.setBounds(10, 12, 150, 22);
			//FIXME 多语言改动
//			corpText.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000440")/*@res "复制到目标公司"*/);
			corpText.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0020")/*@res "复制到目标组织"*/);
		}
		return corpText;
	}
	public UILabel getDjlxText() {
		if (djlxText == null) {
			djlxText = new UILabel();
			djlxText.setBounds(10, 42, 150, 22);
			djlxText.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000441")/*@res "复制到目标交易类型"*/);
		}
		return djlxText;
	}
	public UIRefPane getCorpRef() {
		if(corpRef==null){
			corpRef = new UIRefPane();
			corpRef.setBounds(150, 12, 160, 22);
			corpRef.setRefNodeName("财务组织");

		}
		return corpRef;
	}
	public UIRefPane getDjlxRef() {
		if(djlxRef==null){
			djlxRef = new UIRefPane();
			djlxRef.setBounds(150, 42,160, 22);
			//此处注意两点 影响因素交易类型的名字中间多了个空格 并且这里setWhere需要在model上设置
			djlxRef.setRefNodeName("影响因素交 易类型");
			TranstypeRefModel refmodele = (TranstypeRefModel) djlxRef.getRefModel();
			refmodele.setWhere(" parentbilltype in ('263X','264X')");
//			djlxRef.setWhereString(" parentbilltype in ('263X','264X')");

		}
		return djlxRef;
	}
}