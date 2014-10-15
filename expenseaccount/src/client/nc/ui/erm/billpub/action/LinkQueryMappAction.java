package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.actions.LinkQueryAction;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXVO;

/**
 * 联查费用申请单
 * @author wangled
 */
@SuppressWarnings("restriction")
public class LinkQueryMappAction  extends LinkQueryAction
{

    private static final long serialVersionUID = 1L;
    
    @Override
    public void doAction(ActionEvent e) throws Exception
    {
        JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
        if(selectedData!=null){
            setBillType(selectedData.getParentVO().getDjlxbm());
            super.doAction(e);
        }
    }
    
	@Override
	protected boolean isActionEnable() {
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if (selectedData == null
				|| selectedData.getParentVO().getDjzt() == BXStatusConst.DJZT_Invalid) {
			return false;
		}

		return true;
	}
}
