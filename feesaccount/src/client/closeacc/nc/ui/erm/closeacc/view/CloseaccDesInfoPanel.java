package nc.ui.erm.closeacc.view;

import java.awt.Dimension;

import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITextArea;
import nc.ui.pub.beans.util.ColumnLayout;

public class CloseaccDesInfoPanel extends UIPanel{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	UITextArea desInfoComp;

	public void initUI(){
	    nc.vo.ml.Language language = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getCurrLanguage();
	    boolean chnLang = false;
	    if ("tradchn".equals(language.getCode()) || "simpchn".equals(language.getCode())) {
	        chnLang = true;
	    }
		this.setLayout(new ColumnLayout(ColumnLayout.TOP, 1, 1, false, false));
		this.add(new UILabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0012")/*@res "������ʾ��Ϣ"*/));
		desInfoComp = new UITextArea();
		if (!chnLang) {
	        desInfoComp.setLineWrap(true);
		}
		desInfoComp.setPreferredSize(new Dimension(220,100));
		this.add(desInfoComp);
		desInfoComp.setEnabled(false);
		this.add(new UILabel(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0013")/*@res "��ע"*/));
		UITextArea expComp = new UITextArea();
		expComp.setPreferredSize((new Dimension(220,150)));
		this.add(expComp);
		if (!chnLang) {
	        expComp.setLineWrap(true);
		}
		expComp.setText(getExp());
        expComp.setEnabled(false);

	}

	private String getExp() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0014")/*@res "(1)�ڳ��رպ���ܽ���\n(2)��ֹ���´�̯����ȫ��̯������ܽ���\n(3)�Ѿ����˵��ڼ䲻���ٽ����κ�ҵ��\n��"*/;
	}

	public void setDesinfo(String desinfo){
		desInfoComp.setText(desinfo);
	}
}