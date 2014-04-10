package nc.ui.arap.bx;

/**
 * @author twei
 *
 * nc.ui.arap.bx.BXBillMainPanelQC
 * 
 * 借款报销期初单据入口
 */
public class BXBillMainPanelQC extends BXBillMainPanelMN {

	private static final long serialVersionUID = 1435169125141340435L;

	public BXBillMainPanelQC() {
		getBxParam().setIsQc(true);
		initialize();
	}
}
