package nc.ui.er.reimtype.action;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import nc.ui.pub.print.version55.print.output.excel.core.ExcelException;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.batch.BatchSaveAction;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.er.reimtype.ReimTypeVO;

public class retSaveAction extends BatchSaveAction {

	/**
	 * @author liansg
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		//保存时脱离编辑
		getEditor().getBillCardPanel().stopEditing();
		BatchOperateVO operVO = this.getModel().getCurrentSaveObject();
		Object[] addObjs = operVO.getAddObjs();
 		Object[] updObjs = operVO.getUpdObjs();
		Object[] delObjs = operVO.getDelObjs();
		//判断如果更改需要更新数据
	    if(addObjs.length ==0 && updObjs.length ==0 && delObjs.length ==0){
			if(this.getModel().getUiState()==UIState.EDIT){
//				if(updObjs==null || updObjs.length==0)
//					throw new BusinessException(" 请修改数据后，进行保存！");
			}
		}
	    Object[] extVOs = this.getModel().getRows().toArray();
	    Set<String> codeSet = new HashSet<String>();
	    Set<String> nameSet = new HashSet<String>();
	    for(Object svo:extVOs){
	    	String name= (String) ((ReimTypeVO)svo).getAttributeValue("name");
			String code1=(String) ((ReimTypeVO)svo).getAttributeValue("code");
			if((name == null || "".equals(name)) && (code1 == null || "".equals(code1))){
				throw new ExcelException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0002"));
			}
			if(name == null || "".equals(name)){
				throw new ExcelException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0003")/*@res "需要录入名称项"*/);
			}
			if(code1 == null || "".equals(code1)){
				throw new ExcelException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0004")/*@res "需要录入编码项"*/);
			}
			if(codeSet.contains(code1)){
				throw new ExcelException( nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0005")/*@res "编码重复，请重新输入！"*/);
			}
			if(nameSet.contains(name)){
				throw new ExcelException( nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0006")/*@res "名称重复，请重新输入！"*/);
			}
			codeSet.add(code1);
			nameSet.add(name);
	    }
	    super.doAction(e);
	}


}