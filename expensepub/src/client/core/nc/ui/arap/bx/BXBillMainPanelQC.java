package nc.ui.arap.bx;

/**
 * @author twei
 *
 * nc.ui.arap.bx.BXBillMainPanelQC
 * 
 * �����ڳ��������
 */
public class BXBillMainPanelQC extends BXBillMainPanelMN {

	private static final long serialVersionUID = 1435169125141340435L;

	public BXBillMainPanelQC() {
		getBxParam().setIsQc(true);
		initialize();
	}
}
