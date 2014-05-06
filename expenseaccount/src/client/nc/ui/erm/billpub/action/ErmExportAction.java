package nc.ui.erm.billpub.action;

import java.util.Arrays;

import nc.ui.uif2.excelimport.ExportAction;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXVO;
/**
 * 费用导出按钮
 * @author wangled
 *
 */
public class ErmExportAction extends ExportAction {
	
	private static final long serialVersionUID = 8521058526065481398L;

	@Override
	protected boolean isActionEnable() {
		boolean flag = false ;
		Object[] selectedOperaDatas = ((BillManageModel)getModel()).getSelectedOperaDatas();
		if(selectedOperaDatas != null){
			JKBXVO[] selectedData = Arrays.asList(selectedOperaDatas).toArray(new JKBXVO[0]);
			for(JKBXVO jkbxvo : selectedData ){
				if(jkbxvo.getParentVO().getPk_item()== null
						&& jkbxvo.getParentVO().getDjzt().intValue()!=BXStatusConst.DJZT_Invalid){//不是拉单的借款报销单
					flag = true ;
				}
			}
		}
		return flag;
	}
}
