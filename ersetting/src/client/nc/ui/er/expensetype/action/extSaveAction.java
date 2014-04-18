package nc.ui.er.expensetype.action;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import nc.ui.uif2.actions.batch.BatchSaveAction;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.pub.BusinessException;

public class extSaveAction extends BatchSaveAction {

	/**
	 * @see 费用类型设置，动作脚本
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
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0002")/*@res "需要录入名称项和编码项"*/);
			}
			if(name == null || "".equals(name)){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0003")/*@res "需要录入名称项"*/);
			}
			if(code == null || "".equals(code)){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0004")/*@res "需要录入编码项"*/);
			}
			if(codeSet.contains(code)){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0005")/*@res "编码重复，请重新输入！"*/);
			}
			if(nameSet.contains(name)){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0006")/*@res "名称重复，请重新输入！"*/);
			}
			codeSet.add(code);
			nameSet.add(name);
	    }
	}
}


