package nc.impl.erm.fieldcontrast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.fieldcontrast.IFieldContrastQryService;
import nc.itf.org.IOrgConst;
import nc.itf.uap.billtemplate.IBillTemplateQry;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.ui.pub.bill.IBillItem;
import nc.vo.erm.fieldcontrast.FieldcontrastVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletHeadVO;
import nc.vo.pub.bill.BillTempletVO;

public class FieldContrastQryServiceImpl implements IFieldContrastQryService {

	@Override
	public FieldcontrastVO[] qryVOs(String pk_org, int app_scene,
			String src_billtype) throws BusinessException {
		String where = " pk_org = ? and app_scene=?";
		SQLParameter params = new SQLParameter();
		params.addParam(pk_org);
		params.addParam(app_scene);
		if (!StringUtil.isEmpty(src_billtype)) {
			where += " and src_billtype = ?";
			params.addParam(src_billtype);
		}
		BaseDAO dao = new BaseDAO();
		@SuppressWarnings("unchecked")
		Collection<FieldcontrastVO> c = dao.retrieveByClause(
				FieldcontrastVO.class, where, params);
		if (c != null && !c.isEmpty()) {
			return c.toArray(new FieldcontrastVO[0]);
		}
		return null;
	}

	@Override
	public FieldcontrastVO[] qryPredataVOs() throws BusinessException {
		// 待预置的数据为全局配置的费用控制规则维度对照和分摊规则对照
		String where = " pk_org = '"
				+ IOrgConst.GLOBEORG
				+ "' and app_scene in ("
				+ ErmBillFieldContrastCache.FieldContrast_SCENE_MatterAppCtrlField
				+ ","+ErmBillFieldContrastCache.FieldContrast_SCENE_SHARERULEField
				+ ")";
		BaseDAO dao = new BaseDAO();
		@SuppressWarnings("unchecked")
		Collection<FieldcontrastVO> c = dao.retrieveByClause(
				FieldcontrastVO.class, where);
		if (c != null && !c.isEmpty()) {
			return c.toArray(new FieldcontrastVO[0]);
		}
		return new FieldcontrastVO[0];
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getUserDefItemUseInfo() throws BusinessException {
		BaseDAO dao = new BaseDAO();
		List<String> result = (List<String>) dao.executeQuery("select pk_billtemplet from PUB_BILLTEMPLET where metadataclass = 'erm.bxzb'", new ResultSetProcessor() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				List<String> temp = new ArrayList<String>();
				while (rs.next()) {
					temp.add(rs.getString(1));
				}
				return temp;
			}

		});

		List<String> headZyxList = new ArrayList<String>();
		List<String> headUseableZyxList = new ArrayList<String>();// 可用表头自定义项
		for (int i = 1; i <= 30; i++) {
			headZyxList.add("zyx" + i);
		}
		headUseableZyxList.addAll(headZyxList);

		List<String> bodyItemList = new ArrayList<String>();
		List<String> bodyUseableItemList = new ArrayList<String>();// 可用表体自定义项
		for (int i = 1; i <= 50; i++) {
			bodyItemList.add("defitem" + i);
		}
		bodyUseableItemList.addAll(bodyItemList);

		Map<String, List<String>> usedItemMap = new HashMap<String, List<String>>();
		if (result != null && result.size() > 0) {
			for (String id : result) {
				BillTempletVO templateVo = NCLocator.getInstance().lookup(IBillTemplateQry.class).findTempletData(id);
				BillTempletHeadVO headVo = templateVo.getHeadVO();
				BillTempletBodyVO[] bodyVos = templateVo.getBodyVO();
				if (bodyVos != null && bodyVos.length > 0) {
					for (BillTempletBodyVO bodyVo : bodyVos) {
						if (bodyVo.getMetaDataPropertyAdpter() != null && bodyVo.getMetaDataPropertyAdpter().getDataType() == IBillItem.USERDEFITEM) {
							if (bodyVo.getReftype() != null) {
								if (headZyxList.contains(bodyVo.getItemkey())) {
									headUseableZyxList.remove(bodyVo.getItemkey());
									if (usedItemMap.get(bodyVo.getItemkey()) == null) {
										List<String> itemKeyList = new ArrayList<String>();
										itemKeyList.add(headVo.getBillTempletName());
									} else {
										usedItemMap.get(bodyVo.getItemkey()).add(headVo.getBillTempletName());
									}
								}

								if (bodyItemList.contains(bodyVo.getItemkey())) {
									bodyUseableItemList.remove(bodyVo.getItemkey());
									if (usedItemMap.get(bodyVo.getItemkey()) == null) {
										List<String> itemKeyList = new ArrayList<String>();
										itemKeyList.add(headVo.getPkBillTypeCode() + "_" + headVo.getBillTempletName() + "_" + headVo.getBillTempletCaption());
										usedItemMap.put(bodyVo.getItemkey(), itemKeyList);
									} else {
										usedItemMap.get(bodyVo.getItemkey()).add(headVo.getPkBillTypeCode() + "_" + headVo.getBillTempletName() + "_" + headVo.getBillTempletCaption());
									}
								}
							}
						}
					}
				}
			}
		}

		StringBuffer resultInfoBuffer = new StringBuffer();
		resultInfoBuffer.append("报销单可用表头自定义项：\n");
		for (String headZyx : headUseableZyxList) {
			resultInfoBuffer.append(headZyx + ",");
		}

		resultInfoBuffer.append("\n 报销单可用表体自定义项：\n");
		for (String bodyItem : bodyUseableItemList) {
			resultInfoBuffer.append(bodyItem + ",");
		}

		resultInfoBuffer.append("\n\n 报销单被单据模板引用的自定义项：\n");

		for (Map.Entry<String, List<String>> entry : usedItemMap.entrySet()) {
			resultInfoBuffer.append(entry.getKey() + "：\n");
			for (String templateName : entry.getValue()) {
				resultInfoBuffer.append(templateName + "\n");
			}
		}

		return resultInfoBuffer.toString();
	}
}
