package nc.ui.erm.expamortize.action;

import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

import nc.bs.erm.expamortize.ExpAmoritizeConst;
import nc.bs.logging.Logger;
import nc.funcnode.ui.AbstractFunclet;
import nc.ui.erm.costshare.common.ArrayClassConvertUtil;
import nc.ui.erm.expamortize.model.ExpamorizeManageModel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ToftPanelAdaptor;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.link.GenerateParameter;
/**
 *
 * @author wangled
 *
 */
@SuppressWarnings("serial")
public class VoucherAction extends NCAction {
	private BillManageModel model;

	public VoucherAction() {
		super();
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0076")/*@res "制单"*/);
		this.setCode("Voucher");
	}
	public BillManageModel getModel() {
		return model;
	}
	public void setModel(BillManageModel model) {
		this.model = model;
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
		mergePf();
	}
	
	public void mergePf() throws Exception{
		Object[] selectedDatas =  getModel().getSelectedOperaDatas();
		ExpamtinfoVO[] selectedvos = ArrayClassConvertUtil.convert(selectedDatas, ExpamtinfoVO.class);

		validate(selectedvos);
		
		String[][] datas = new String[1][];
		datas[0] = new String[selectedvos.length + 1];
		datas[0][0] = ExpAmoritizeConst.Expamoritize_BILLTYPE;

		for (int i = 0; i < selectedvos.length; i ++) {
			datas[0][i + 1] = selectedvos[i].getPrimaryKey() + "_" + ((ExpamorizeManageModel) getModel()).getPeriod();
		}
		
		Class<?> c = Class.forName("nc.ui.pub.link.DesBillGenerator");
		Object o = c.newInstance();
		Method m = o.getClass().getMethod("generateDesBill",
				new Class[] { AbstractFunclet.class, String[][].class, GenerateParameter.class });
		ToftPanelAdaptor toftPanel = (ToftPanelAdaptor) getModel().getContext().getEntranceUI();
		
		try {
			m.invoke(o, new Object[] { (AbstractFunclet) toftPanel, datas, null });
		} catch (Exception ex) {
			if (ex instanceof java.lang.reflect.InvocationTargetException) {
				ExceptionHandler.handleException((Exception) ((java.lang.reflect.InvocationTargetException) ex)
						.getTargetException());
			}
			Logger.error(ex.getMessage(), ex);
		}
	}
	
	private void validate(ExpamtinfoVO[] selectedvos) throws BusinessException {
		if (selectedvos == null || selectedvos.length == 0){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000358")/*@res "请选中单据后再进行制单的操作!"*/);
		}
		
		
		for(ExpamtinfoVO infoVo : selectedvos){
			if(infoVo.getAmt_status().equals(UFBoolean.FALSE)){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
						getStrByID("upp2012v575_0","0upp2012V575-0120")/*@res ""选中的单据未摊销，不能进行制单动作""*/);
			}
		}
		
		// 币种一致
		String bzbm = null;

		for (ExpamtinfoVO vo : selectedvos) {
			if (bzbm != null && !bzbm.equals(vo.getBzbm())){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000357")/* @res "选中的单据包括不同的币种，不能进行制单的操作!" */);
			}
			if (bzbm == null){
				bzbm = vo.getBzbm();
			}
		}

	}
}