package nc.ui.erm.budgetlink.listener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListSelectionModel;

import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.FuncletInitData;
import nc.itf.erm.ntb.IBugetLinkBO;
import nc.itf.fipub.report.IPubReportConstants;
import nc.pub.smart.data.DataSet;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.uif2.IFuncNodeInitDataListener;
import nc.vo.ep.bx.BxDetailLinkQueryVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.fipub.report.ReportInitializeItemVO;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;
import nc.vo.tb.obj.NtbParamVO;
import nc.vo.uif2.LoginContext;

@SuppressWarnings("restriction")
public class LinkQueryInitDataListener implements IFuncNodeInitDataListener {

	private LoginContext context = null;

	private BillListView listView = null;

	public void initData(FuncletInitData data) {
		if (data == null || data.getInitData() == null) {
			adjustDetailColumns(null, null);
			return;
		}

		Object initParam = data.getInitData();
		List<BxDetailLinkQueryVO> resultVOs = null;
		try {
			ArrayList<?> paramList = (ArrayList<?>) initParam;
			NtbParamVO ntbParamVO = (NtbParamVO) paramList.get(0);
			resultVOs = getLinkQueryErmBillDatas(ntbParamVO);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
		if (resultVOs != null) {
			getListView().getModel().initModel(resultVOs.toArray());
		}
	}

	private List<BxDetailLinkQueryVO> getLinkQueryErmBillDatas(NtbParamVO ntbParamVO) throws BusinessException {
		adjustBillColumns(ntbParamVO);
		return NCLocator.getInstance().lookup(IBugetLinkBO.class).getLinkDatas(ntbParamVO);
	}

	private void adjustBillColumns(NtbParamVO ntbParamVO) {
		BillListPanel blp = getListView().getBillListPanel();
		BillListData billListData = blp.getBillListData();
		BillItem[] headItems = billListData.getHeadItems();
		int length = ntbParamVO.getTypeDim().length;
		int i = 0;
		for (BillItem item : headItems) {
			if (item.getKey().contains(BxDetailLinkQueryVO.QRYOBJ_PREFIX) && !item.getKey().endsWith("pk")) {
				if (i < length) {
					item.setShow(true);
					item.setName(ntbParamVO.getTypeDim()[i]);
				} else {
					item.setShow(false);
				}
				i++;
			}
		}
		blp.setListData(billListData);
		blp.getParentListPanel().getTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

	public BillListView getListView() {
		return listView;
	}

	public void setListView(BillListView listView) {
		this.listView = listView;
	}

	private void adjustDetailColumns(ReportQueryCondVO qryCondVO, DataSet datas) {
		BillListPanel billListPanel = getListView().getBillListPanel();

		List<String> hiddenColList = new ArrayList<String>();
		hiddenColList.add(BxDetailLinkQueryVO.QRYOBJ0);
		hiddenColList.add(BxDetailLinkQueryVO.QRYOBJ1);
		hiddenColList.add(BxDetailLinkQueryVO.QRYOBJ2);
		hiddenColList.add(BxDetailLinkQueryVO.QRYOBJ3);
		hiddenColList.add(BxDetailLinkQueryVO.QRYOBJ4);
		hiddenColList.add(BxDetailLinkQueryVO.EFFECTDATE);

		// 显示指定列
		for (String key : hiddenColList) {
			billListPanel.showHeadTableCol(key);
		}

		int qryObjCount = qryCondVO == null || datas == null || datas.getDatas().length == 0 ? 0 : qryCondVO
				.getQryObjs().size();
		for (int i = 0; i < qryObjCount; i++) {
			hiddenColList.remove(IPubReportConstants.QRY_OBJ_PREFIX + i);
		}

		// 隐藏指定列
		for (String key : hiddenColList) {
			billListPanel.hideHeadTableCol(key);
		}

		if (qryCondVO != null) {
			// 修改查询对象列标题
			ReportInitializeItemVO[] bodyVOs = (ReportInitializeItemVO[]) qryCondVO.getRepInitContext().getChildrenVO();
			BillListData billListData = billListPanel.getBillListData();
			BillItem billItem = null;
			for (int i = 0; i < qryObjCount; i++) {
				billItem = billListData.getHeadItem(IPubReportConstants.QRY_OBJ_PREFIX + i);
				billItem.setName(bodyVOs[i].getDsp_objname());
			}

			billListPanel.setListData(billListData);
		}

		// 禁止排序功能
		billListPanel.getHeadTable().setSortEnabled(false);
	}

}

// /:~
