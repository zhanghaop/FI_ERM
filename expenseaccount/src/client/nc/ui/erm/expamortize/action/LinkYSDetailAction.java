package nc.ui.erm.expamortize.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.expamortize.control.ExpamortizeYsControlVO;
import nc.bs.framework.common.NCLocator;
import nc.itf.tb.control.ILinkQuery;
import nc.pubitf.erm.expamortize.IExpAmortizeinfoQuery;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;
import nc.view.tb.control.NtbParamVOChooser;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.expamortize.ExpamtDetailVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.erm.verifynew.BusinessShowException;
import nc.vo.tb.obj.NtbParamVO;

/**
 * 联查摊销预算
 * 
 * @author wangled
 * 
 */
@SuppressWarnings({ "serial" })
public class LinkYSDetailAction extends NCAction {
	private BillManageModel model;

	public LinkYSDetailAction() {
		super();
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0074")/*
																										 * @
																										 * res
																										 * "联查预算"
																										 */);
		setCode("LinkYSDetail");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		ExpamtinfoVO selectvo = (ExpamtinfoVO) getModel().getSelectedData();
		Object[] datas = new ExpamtinfoVO[] { selectvo };
		datas = new ExpamtinfoVO[] { selectvo };

		List<ExpamortizeYsControlVO> item = new ArrayList<ExpamortizeYsControlVO>();
		for (int i = 0; i < datas.length; i++) {
			ExpamtinfoVO vo = (ExpamtinfoVO) datas[i];
			String pk_expamtinfo = vo.getPk_expamtinfo();
			ExpamtDetailVO[] dtailvos = getExpAmortizeinfoQuery().queryAllDetailVOs(pk_expamtinfo);
			for (int j = 0; j < dtailvos.length; j++) {
				ExpamortizeYsControlVO cscontrolvo = new ExpamortizeYsControlVO(vo, (ExpamtDetailVO) dtailvos[j]);
				item.add(cscontrolvo);
			}
		}
		
		YsControlVO[] controlVos = new YsControlVO[item.size()];
		for (int i = 0; i < item.size(); i ++) {
			YsControlVO vo = new YsControlVO();
			vo.setItems(new IFYControl[] { item.get(i) });
			controlVos[i] = vo;
		}

		try {
			NtbParamVO[] vos = ((ILinkQuery) NCLocator.getInstance().lookup(ILinkQuery.class.getName())).getLinkDatas(controlVos);
			// FIXME 需要使用反射
			NtbParamVOChooser chooser = new NtbParamVOChooser(getModel().getContext().getEntranceUI(), nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030102", "UPP2006030102-000430")/**
			 * @res
			 *      "预算执行情况"
			 */
			);
			if (null == vos || vos.length == 0) {
				throw new BusinessShowException(nc.ui.ml.NCLangRes.getInstance().getStrByID("2008", "UPP2008-000021"));
			}
			chooser.setParamVOs(vos);
			chooser.showModal();
		} catch (Exception e1) {
			throw ExceptionHandler.handleException(this.getClass(), e1);
		}
	}

	public IExpAmortizeinfoQuery getExpAmortizeinfoQuery() {
		return NCLocator.getInstance().lookup(IExpAmortizeinfoQuery.class);
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null && model.getUiState() == UIState.NOT_EDIT;
	}

}