package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Action;

import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.logging.Logger;
import nc.funcnode.ui.AbstractFunclet;
import nc.ui.erm.costshare.common.ArrayClassConvertUtil;
import nc.ui.erm.costshare.ui.CostShareToftPanel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.link.GenerateParameter;

/**
 * @author luolch
 *
 * 制单活动
 *
 */
public class VoucherAction extends NCAction {
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	public VoucherAction() {
		super();
		this.setBtnName(ErmActionConst.getVoucherName());
		this.setCode(ErmActionConst.VOUCHER);
		putValue(Action.SHORT_DESCRIPTION,getBtnName());
	}
	public void doAction(ActionEvent e) throws Exception {
		mergePf();
	}

	private void validate(AggCostShareVO[] selectedvos) throws BusinessException {
		// 校验单据是否是审核状态
		// 币种一致
		String bzbm = null;

		for (AggCostShareVO vo : selectedvos) {

			CostShareVO parentVO = (CostShareVO) vo.getParentVO();

			if (parentVO.getBillstatus() != BXStatusConst.DJZT_Sign)
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000356")/*@res "选中的单据未生效，不能进行制单的操作!"*/);
			if (bzbm != null && !bzbm.equals(parentVO.getBzbm()))
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000357")/*@res "选中的单据包括不同的币种，不能进行制单的操作!"*/);
			if (bzbm == null)
				bzbm = parentVO.getBzbm();
		}

	}

	public void mergePf() throws BusinessException {
		Object[] aggDatas =  getModel().getSelectedOperaDatas();
		AggCostShareVO[] selectedvos = ArrayClassConvertUtil.convert(aggDatas, AggCostShareVO.class);
		if (selectedvos == null || selectedvos.length == 0)
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000358")/*@res "请选中单据后再进行制单的操作!"*/);
		validate( selectedvos);

		Map<String, List<String>> tempMap = new HashMap<String, List<String>>();
		List<String> selectedPK = new ArrayList<String>();

		for (AggCostShareVO vo : selectedvos) {
			selectedPK.add(vo.getParentVO().getPrimaryKey());
		}
		for (AggCostShareVO vo : selectedvos) {
			CostShareVO head = (CostShareVO) vo.getParentVO();
			
			if (!tempMap.containsKey((head.getPk_billtype()))) {
				tempMap.put(head.getPk_billtype(), new ArrayList<String>());
			}
			tempMap.get(head.getPk_billtype()).add(head.getPk_costshare());
		}
		String[][] datas = new String[tempMap.size()][];
		int idx = 0;
		Iterator<Entry<String, List<String>>> iter = tempMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, List<String>>  entry = (Map.Entry<String, List<String>>) iter.next();
			String key = entry.getKey();
			List<String> val = entry.getValue();
			String[] item = new String[val.size() + 1];
			int i = 0;
			item[i++] = key;
			for (String pk : tempMap.get(key)) {
				item[i++] = pk;
			}
			datas[idx++] = item;
		}
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < datas.length; i++) {
			for (int j = 0; j < datas[i].length; j++) {
				sb.append(" ").append(datas[i][j]).append(" ");
			}
		}
		
		Map<String,String> jkMap=new HashMap<String, String>();
		
		for (AggCostShareVO vo : selectedvos) {
			String key = vo.getParentVO().getPrimaryKey();
			jkMap.put(key, ((CostShareVO)vo.getParentVO()).getTs().toString());
		}
		CostShareToftPanel toftPanel = (CostShareToftPanel) getModel().getContext().getEntranceUI();
		try {
			Class<?> c = Class.forName("nc.ui.pub.link.DesBillGenerator");
			Object o = c.newInstance();
			Method m = o.getClass().getMethod("generateDesBill",new Class[] { AbstractFunclet.class, String[][].class,GenerateParameter.class });
			m.invoke(o, new Object[] {(AbstractFunclet) toftPanel, datas, null });
		}catch (Exception e) {
			if(e instanceof java.lang.reflect.InvocationTargetException){
				ExceptionHandler.handleException((Exception)((java.lang.reflect.InvocationTargetException) e).getTargetException());
			}
			Logger.error(e.getMessage(), e);
		}
	}
	@Override
	protected boolean isActionEnable() {
		Object[] datas = getModel().getSelectedOperaDatas();
		if (datas!=null) {
			for (int i = 0; i < datas.length; i++) {
				 CostShareVO vo = ((CostShareVO)((AggCostShareVO)datas[i]).getParentVO());
				 if (vo.getBillstatus() == BXStatusConst.DJZT_TempSaved) {
						return false;
					}
			}
		}else if(getModel().getSelectedData()!=null) {
			CostShareVO vo = ((CostShareVO)((AggCostShareVO)getModel().getSelectedData()).getParentVO());
			 if (vo.getBillstatus() == BXStatusConst.DJZT_TempSaved) {
					return false;
				}
		}else {
			return false;
		}
		return model.getUiState()== UIState.NOT_EDIT;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}
	public BillManageModel getModel() {
		return model;
	}


}