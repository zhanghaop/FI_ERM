package nc.ui.erm.budgetlink.listener;

import java.util.List;

import nc.ui.pub.bill.BillListPanel;
import nc.vo.ep.bx.BxDetailLinkQueryVO;
import nc.vo.pub.lang.UFDouble;


/**
 * 实现币种精度的显示
 *
 */
public class ErmBillListView extends nc.ui.pubapp.uif2app.view.BillListView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6438635216839544867L;

	/**
	 * 同步ListView模型和BillListPanel模型
	 */
	protected void synchronizeDataFromModel() {
		String[] keys = new String[] { BxDetailLinkQueryVO.ORI, BxDetailLinkQueryVO.LOC, BxDetailLinkQueryVO.GR_LOC,
				BxDetailLinkQueryVO.GL_LOC };
		BillListPanel bcp = getBillListPanel();
		List data = getModel().getData();
		for (Object object : data) {
			BxDetailLinkQueryVO vo = (BxDetailLinkQueryVO) object;
			for (String key : keys) {
				UFDouble ufDouble = (UFDouble)vo.getAttributeValue(key);
				if(bcp.getHeadItem(key) != null){
					bcp.getHeadItem(key).setDecimalDigits(ufDouble == null ? 2 : ufDouble.getPower());
				}
			}
		}
		super.synchronizeDataFromModel();
	}
	
}
