package nc.ui.arap.bx.actions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.funcnode.ui.AbstractFunclet;
import nc.ui.glpub.IUiPanel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.link.GenerateParameter;

/**
 * <p>
 * TODO 接口/类功能说明 制单，调用会计平台
 * </p>
 * 
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li> <br>
 * <br>
 * 
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-10-25 下午07:47:34
 */
public class MergePfAct extends BXDefaultAction {

	private void validate(JKBXVO[] selectedvos) throws BusinessException {
		// 校验单据是否是审核状态
		// 币种一致
		String bzbm = null;

		for (JKBXVO vo : selectedvos) {

			JKBXHeaderVO parentVO = vo.getParentVO();
			
			//为生效的单据不能进行制单
			if ((parentVO.getDjzt() != BXStatusConst.DJZT_Sign
					&& parentVO.getDjzt() != BXStatusConst.DJZT_Verified) 
					|| !Integer.valueOf(BXStatusConst.SXBZ_VALID).equals(parentVO.getSxbz()))
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000356")/*
															 * @res
															 * "选中的单据未生效，不能进行制单的操作!"
															 */);
			if (bzbm != null && !bzbm.equals(parentVO.getBzbm()))
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000357")/*
															 * @res
															 * "选中的单据包括不同的币种，不能进行制单的操作!"
															 */);
			if (bzbm == null)
				bzbm = parentVO.getBzbm();
		}

	}

	/**
	 * 制单动作
	 * @throws BusinessException
	 */
	public void mergePf() throws BusinessException {

		JKBXVO[] selectedvos = getSelBxvos();

		if (selectedvos == null || selectedvos.length == 0)
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011", "UPP2011-000358")/*
														 * @res
														 * "请选中单据后再进行制单的操作!"
														 */);
		validate(selectedvos);

		Map<String, List<String>> temp = new HashMap<String, List<String>>();
		List<String> selectedPK = new ArrayList<String>();

		for (JKBXVO vo : selectedvos) {
			selectedPK.add(vo.getParentVO().getPk_jkbx());
		}

		try {
			checkTs(selectedvos);
			for (JKBXVO vo : selectedvos) {
				JKBXHeaderVO head = vo.getParentVO();
				if (!temp.containsKey((head.getDjlxbm()))) {
					temp.put(head.getDjlxbm(), new ArrayList<String>());
				}
				temp.get(head.getDjlxbm()).add(head.getPk_jkbx());
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
			try {
				// 来源方查目标方
				//采用反射的方法，制单处理，但是会计平台更改接口，要注意此处不自动更改，这点要注意
				Class<?> c = Class.forName("nc.ui.pub.link.DesBillGenerator");
				Object o = c.newInstance();
				Method m = o.getClass().getMethod("generateDesBill",new Class[] { AbstractFunclet.class, String[][].class,GenerateParameter.class });
				m.invoke(o, new Object[] {(AbstractFunclet) (IUiPanel) this.getParent(), datas, null });
			} catch (Exception e) {
				throw ExceptionHandler.createException(e);
			}
		} catch (Exception ex) {
			throw ExceptionHandler.createException(ex);
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
				ts = getIBXBillPrivate().getTsByPrimaryKey(
						selectedPK.toArray(new String[] {}),
						djdl.equals(BXConstans.BX_DJDL) ? BXConstans.BX_TABLENAME
								: BXConstans.JK_TABLENAME,
						new BXHeaderVO().getPKFieldName());
			} catch (Exception e2) {
				throw ExceptionHandler.createException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011", "UPP2011-000359")/*
																			 * @res
																			 * "并发异常，数据已经更新，请重新查询数据后操作"
																			 */, e2);
			}

			for (JKBXVO vo : map.get(djdl)) {
				if (!vo.getParentVO().getTs().toString().equals(
						ts.get(vo.getParentVO().getPk_jkbx()))) {
					throw ExceptionHandler.createException(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("2011", "UPP2011-000359")/*
																				 * @res
																				 * "并发异常，数据已经更新，请重新查询数据后操作"
																				 */);
				}
			}
		}
	}

}