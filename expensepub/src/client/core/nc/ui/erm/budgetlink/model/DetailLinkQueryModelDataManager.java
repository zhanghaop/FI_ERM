package nc.ui.erm.budgetlink.model;

import java.util.ArrayList;
import java.util.List;

import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.uif2.model.IAppModelDataManagerEx;

import nc.vo.ep.bx.BxDetailLinkQueryVO;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.uif2.LoginContext;

@SuppressWarnings("restriction")
public class DetailLinkQueryModelDataManager implements IAppModelDataManagerEx {

	private LoginContext context = null;
	private BillListView listView = null;
	private ReportQueryCondVO queryCondVO = null;

	private List<BxDetailLinkQueryVO> resultVOList = new ArrayList<BxDetailLinkQueryVO>();

	public void setShowSealDataFlag(boolean showSealDataFlag) {

	}

	public void initModel() {
	}

	public void initModelBySqlWhere(String sqlWhere) {

	}

	public void refresh() {

	}

	public List<BxDetailLinkQueryVO> getResultVOList() {
		return resultVOList;
	}

	public void setResultVOList(List<BxDetailLinkQueryVO> resultVOList) {
		this.resultVOList = resultVOList;
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

	public ReportQueryCondVO getQueryCondVO() {
		return queryCondVO;
	}

	public void setQueryCondVO(ReportQueryCondVO queryCondVO) {
		this.queryCondVO = queryCondVO;
	}

}

// /:~
