package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.AbstractFunclet;
import nc.itf.erm.accruedexpense.IErmAccruedBillQueryPrivate;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.link.GenerateParameter;

public class AccMergePfAction extends NCAction {

	private static final long serialVersionUID = 1L;

	private BillForm editor;

	private BillManageModel model;

	public AccMergePfAction() {
		setCode("Bill");
		setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0076")/*
																									 * @
																									 * res
																									 * "制单"
																									 */);

	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		Object[] vos = (Object[]) getModel().getSelectedOperaDatas();

		if (vos == null || vos.length == 0)
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000358")/*
																														 * @
																														 * res
																														 * "请选中单据后再进行制单的操作!"
																														 */);

		AggAccruedBillVO[] selectedvos = Arrays.asList(vos).toArray(new AggAccruedBillVO[0]);
		validate(selectedvos);

		Map<String, List<String>> temp = new HashMap<String, List<String>>();

		try {
			checkTs(selectedvos);
			for (AggAccruedBillVO vo : selectedvos) {
				AccruedVO head = vo.getParentVO();
				if (!temp.containsKey((head.getPk_tradetype()))) {
					temp.put(head.getPk_tradetype(), new ArrayList<String>());
				}
				temp.get(head.getPk_tradetype()).add(head.getPk_accrued_bill());// ehp2要加上凭证环节
			}
			String[][] datas = new String[temp.size()][];
			int idx = 0;
			for (String key : temp.keySet()) {
				String[] item = new String[temp.get(key).size() + 1];
				int i = 0;
				item[i++] = key;
				for (String pk : temp.get(key)) {
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
			AbstractFunclet toftPanel = (AbstractFunclet) getModel().getContext().getEntranceUI();
			// 来源方查目标方
			// 采用反射的方法，制单处理，但是会计平台更改接口，要注意此处不自动更改，这点要注意
			Class<?> c = Class.forName("nc.ui.pub.link.DesBillGenerator");
			Object o = c.newInstance();
			Method m = o.getClass().getMethod("generateDesBill",
					new Class[] { AbstractFunclet.class, String[][].class, GenerateParameter.class });
			m.invoke(o, new Object[] { toftPanel, datas, null });

		} catch (Exception ex) {
			if (ex instanceof java.lang.reflect.InvocationTargetException) {
				ExceptionHandler.handleException((Exception) ((java.lang.reflect.InvocationTargetException) ex)
						.getTargetException());
			} else {
				ExceptionHandler.handleException(ex);
			}
		}
	}

	private void checkTs(AggAccruedBillVO[] selectedvos) throws BusinessException {
		HashMap<String, List<AggAccruedBillVO>> map = new HashMap<String, List<AggAccruedBillVO>>();
		for (AggAccruedBillVO vo : selectedvos) {
			String key = vo.getParentVO().getPk_billtype();
			if (map.containsKey(key)) {
				List<AggAccruedBillVO> list = map.get(key);
				list.add(vo);
			} else {
				ArrayList<AggAccruedBillVO> list = new ArrayList<AggAccruedBillVO>();
				list.add(vo);
				map.put(key, list);
			}
		}

		Set<String> djdls = map.keySet();

		for (String djdl : djdls) {
			List<String> selectedPK = new ArrayList<String>();
			for (AggAccruedBillVO vo : map.get(djdl)) {
				selectedPK.add(vo.getParentVO().getPk_accrued_bill());
			}
			Map<String, UFDateTime> ts = null;
			try {
				ts = NCLocator.getInstance().lookup(IErmAccruedBillQueryPrivate.class)
						.getTsMapByPK(selectedPK, AccruedVO.getDefaultTableName(), AccruedVO.PK_ACCRUED_BILL);
			} catch (Exception e2) {
				throw ExceptionHandler.createException(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000359")/*
																										 * @
																										 * res
																										 * "并发异常，数据已经更新，请重新查询数据后操作"
																										 */, e2);
			}

			for (AggAccruedBillVO vo : map.get(djdl)) {
				if (!vo.getParentVO().getTs().equals(ts.get(vo.getParentVO().getPk_accrued_bill()))) {
					throw ExceptionHandler.createException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"2011", "UPP2011-000359")/*
													 * @res
													 * "并发异常，数据已经更新，请重新查询数据后操作"
													 */);
				}
			}
		}
	}

	private void validate(AggAccruedBillVO[] selectedvos) throws BusinessException {
		// 校验单据是否是审核状态
		// 币种一致
		String bzbm = null;

		for (AggAccruedBillVO vo : selectedvos) {

			AccruedVO parentVO = vo.getParentVO();

			// 为生效的单据不能进行制单
			if ((parentVO.getBillstatus() != ErmAccruedBillConst.BILLSTATUS_APPROVED)
					|| !Integer.valueOf(ErmAccruedBillConst.EFFECTSTATUS_VALID).equals(parentVO.getEffectstatus()))
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000356")/*
										 * @res "选中的单据未生效，不能进行制单的操作!"
										 */);
			if (bzbm != null && !bzbm.equals(parentVO.getPk_currtype()))
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000357")/*
										 * @res "选中的单据包括不同的币种，不能进行制单的操作!"
										 */);
			if (bzbm == null)
				bzbm = parentVO.getPk_currtype();
		}

	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	@Override
	protected boolean isActionEnable() {
		AggAccruedBillVO selectedData = (AggAccruedBillVO) getModel().getSelectedData();
		if ((selectedData != null && (selectedData.getParentVO().getBillstatus()).equals(ErmAccruedBillConst.BILLSTATUS_APPROVED))) {
			return true;
		}

		return false;
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

}
