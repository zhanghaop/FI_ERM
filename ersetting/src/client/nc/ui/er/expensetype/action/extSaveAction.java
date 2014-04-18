package nc.ui.er.expensetype.action;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import nc.ui.uif2.actions.batch.BatchSaveAction;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.pub.BusinessException;

public class extSaveAction extends BatchSaveAction {

	/**
	 * @see �����������ã������ű�
	 * @author liansg
	 *
	 */
	private static final long serialVersionUID = 1L;


	public void doAction(ActionEvent e) throws Exception {
		beforeSave();
	    super.doAction(e);
	}

	private void beforeSave() throws BusinessException {
		getEditor().getBillCardPanel().stopEditing();
	    Object[] extVOs = this.getModel().getRows().toArray();
	    Set<String> codeSet = new HashSet<String>();
	    Set<String> nameSet = new HashSet<String>();
	    for(Object svo:extVOs){
	    	final String name= (String) ((ExpenseTypeVO)svo).getAttributeValue("name");
			final String code=(String) ((ExpenseTypeVO)svo).getAttributeValue("code");
			if((name == null || "".equals(name)) && (code == null || "".equals(code))){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0002")/*@res "��Ҫ¼��������ͱ�����"*/);
			}
			if(name == null || "".equals(name)){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0003")/*@res "��Ҫ¼��������"*/);
			}
			if(code == null || "".equals(code)){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0004")/*@res "��Ҫ¼�������"*/);
			}
			if(codeSet.contains(code)){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0005")/*@res "�����ظ������������룡"*/);
			}
			if(nameSet.contains(name)){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0006")/*@res "�����ظ������������룡"*/);
			}
			codeSet.add(code);
			nameSet.add(name);
	    }
	}
}


