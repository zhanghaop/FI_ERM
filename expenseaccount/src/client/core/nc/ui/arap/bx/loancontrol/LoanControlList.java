package nc.ui.arap.bx.loancontrol;

import java.util.Vector;

import nc.bs.logging.Logger;
import nc.ui.erm.common.CommonList;
import nc.ui.erm.common.CommonUI;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.vo.erm.util.FormulaUtil;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

/**
 * @author twei
 *
 * nc.ui.arap.bx.loancontrol.LoanControlList
 *
 * �����������б����
 *
 * @see CommonList
 */
public class LoanControlList extends CommonList {

	private CommonUI parentUI;

	public CommonUI getParentUI() {
		return parentUI;
	}

	public void setParentUI(CommonUI parentUI) {
		this.parentUI = parentUI;
	}

	private static final long serialVersionUID = -6733112674085716293L;

	public LoanControlList() {
		setName("LoanControlList");
	}

	@Override
	public Vector<String> getHeader() {
		Vector<String> header =new Vector<String>();
		header.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000051")/*@res "����"*/);
		header.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000045")/*@res "����"*/);
		header.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000043")/*@res "���ƶ���"*/);
		header.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000042")/*@res "��������"*/);
		header.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000046")/*@res "����"*/);
//		if(!isGroup()){
			header.add(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0010")/*@res "������֯"*/);
//		}

		return header;
	}

	protected boolean isGroup() {
		return ((LoanControlMailPanel)getParentUI()).isGroup();
	}

	@Override
	public Vector<String> getHeaderColumns() {

		Vector<String> header =new Vector<String>();
		header.add("paracode");
		header.add("paraname");
		header.add("attributename");
		header.add("controlstylename");
		header.add("currency:" + FormulaUtil.GETCOLVALUE + "-nc.vo.bd.currtype.CurrtypeVO-name");
		header.add("pk_org:" + FormulaUtil.GETCOLVALUE + "-nc.vo.org.FinanceOrgVO-name");

		return header;
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		try {
			parentUI.initDataOrg();
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		parentUI.init();

	}


}