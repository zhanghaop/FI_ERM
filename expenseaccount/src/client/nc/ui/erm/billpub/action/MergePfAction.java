package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.funcnode.ui.AbstractFunclet;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.ui.erm.view.ErmToftPanel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.link.GenerateParameter;

public class MergePfAction extends NCAction {

	private static final long serialVersionUID = 1L;

	private BillManageModel model;
	private BillForm editor;

	public MergePfAction() {
		super();
		setCode("Bill");
		setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0076")/*
																									 * @
																									 * res
																									 * "�Ƶ�"
																									 */);

	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		Object[] vos = (Object[]) getModel().getSelectedOperaDatas();

		if (vos == null || vos.length == 0)
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000358")/*
																														 * @
																														 * res
																														 * "��ѡ�е��ݺ��ٽ����Ƶ��Ĳ���!"
																														 */);

		JKBXVO[] selectedvos = Arrays.asList(vos).toArray(new JKBXVO[0]);
		validate(selectedvos);

		Map<String, List<String>> temp = new HashMap<String, List<String>>();
//		List<String> selectedPK = new ArrayList<String>();
//
//		for (JKBXVO vo : selectedvos) {
//			selectedPK.add(vo.getParentVO().getPk_jkbx());
//		}

		try {
			checkTs(selectedvos);
			for (JKBXVO vo : selectedvos) {
				JKBXHeaderVO head = vo.getParentVO();
				if (!temp.containsKey((head.getDjlxbm()))) {
					temp.put(head.getDjlxbm(), new ArrayList<String>());
				}
				temp.get(head.getDjlxbm()).add(head.getPk_jkbx()+"_"+head.getVouchertag());//ehp2Ҫ����ƾ֤����
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
			ErmToftPanel toftPanel = (ErmToftPanel) getModel().getContext().getEntranceUI();
			// ��Դ����Ŀ�귽
			// ���÷���ķ������Ƶ��������ǻ��ƽ̨���Ľӿڣ�Ҫע��˴����Զ����ģ����Ҫע��
			Class<?> c = Class.forName("nc.ui.pub.link.DesBillGenerator");
			Object o = c.newInstance();
			Method m = o.getClass().getMethod("generateDesBill",
					new Class[] { AbstractFunclet.class, String[][].class, GenerateParameter.class });
			m.invoke(o, new Object[] { toftPanel, datas, null });

		} catch (Exception ex) {
			if (ex instanceof java.lang.reflect.InvocationTargetException) {
				ExceptionHandler.handleException((Exception) ((java.lang.reflect.InvocationTargetException) ex)
						.getTargetException());
			}
			Logger.error(ex.getMessage(), ex);
		}
	}

	private void checkTs(JKBXVO[] selectedvos) throws BusinessException {
		HashMap<String, List<JKBXVO>> map = new HashMap<String, List<JKBXVO>>();
		for (JKBXVO vo : selectedvos) {
			String key = vo.getParentVO().getDjdl();
			if (map.containsKey(key)) {
				List<JKBXVO> list = map.get(key);
				list.add(vo);
			} else {
				ArrayList<JKBXVO> list = new ArrayList<JKBXVO>();
				list.add(vo);
				map.put(key, list);
			}
		}

		Set<String> djdls = map.keySet();

		for (String djdl : djdls) {
			List<String> selectedPK = new ArrayList<String>();
			for (JKBXVO vo : map.get(djdl)) {
				selectedPK.add(vo.getParentVO().getPk_jkbx());
			}
			Map<String, String> ts = null;
			try {
				ts = NCLocator
						.getInstance()
						.lookup(IBXBillPrivate.class)
						.getTsByPrimaryKey(selectedPK.toArray(new String[] {}),
								djdl.equals(BXConstans.BX_DJDL) ? BXConstans.BX_TABLENAME : BXConstans.JK_TABLENAME,
								new BXHeaderVO().getPKFieldName());
			} catch (Exception e2) {
				throw ExceptionHandler.createException(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000359")/*
																										 * @
																										 * res
																										 * "�����쳣�������Ѿ����£������²�ѯ���ݺ����"
																										 */, e2);
			}

			for (JKBXVO vo : map.get(djdl)) {
				if (!vo.getParentVO().getTs().toString().equals(ts.get(vo.getParentVO().getPk_jkbx()))) {
					throw ExceptionHandler.createException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"2011", "UPP2011-000359")/*
													 * @res
													 * "�����쳣�������Ѿ����£������²�ѯ���ݺ����"
													 */);
				}
			}
		}
	}

	private void validate(JKBXVO[] selectedvos) throws BusinessException {
		// У�鵥���Ƿ������״̬
		// ����һ��
		String bzbm = null;

		for (JKBXVO vo : selectedvos) {

			JKBXHeaderVO parentVO = vo.getParentVO();

			// Ϊ��Ч�ĵ��ݲ��ܽ����Ƶ�
			if ((parentVO.getDjzt() != BXStatusConst.DJZT_Sign && parentVO.getDjzt() != BXStatusConst.DJZT_Verified)
					|| !Integer.valueOf(BXStatusConst.SXBZ_VALID).equals(parentVO.getSxbz()))
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000356")/*
										 * @res "ѡ�еĵ���δ��Ч�����ܽ����Ƶ��Ĳ���!"
										 */);
			if (bzbm != null && !bzbm.equals(parentVO.getBzbm()))
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000357")/*
										 * @res "ѡ�еĵ��ݰ�����ͬ�ı��֣����ܽ����Ƶ��Ĳ���!"
										 */);
			if (bzbm == null)
				bzbm = parentVO.getBzbm();
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
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if ((selectedData != null && selectedData.getParentVO().getDjzt() != BXStatusConst.DJZT_Invalid && (selectedData.getParentVO().getDjzt()).equals(BXStatusConst.DJZT_Sign))) {
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
