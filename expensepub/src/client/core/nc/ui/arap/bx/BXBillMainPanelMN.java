package nc.ui.arap.bx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.impl.er.proxy.ProxyDjlx;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.er.util.BXUiUtil;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.uif2.IFunNodeClosingListener;
import nc.vo.cmp.settlement.NodeType;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.uif2.LoginContext;

/**
 * @author twei
 *
 * nc.ui.arap.bx.BXBillMainPanelMN
 * 
 * 借款报销管理节点入口
 */
public class BXBillMainPanelMN extends BXBillMainPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3348016054785023120L;
	
	
	public BXBillMainPanelMN() {
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see nc.ui.arap.bx.BXBillMainPanel#initUi()
	 * 初始化界面
	 */
	@Override
	protected void initUi() {
		add(getListContentPanel(), "LIST");
		setCurrentpage(BillWorkPageConst.LISTPAGE);
	}
	
	/* (non-Javadoc)
	 * @see nc.ui.arap.bx.BXBillMainPanel#loadBillListTemplate()
	 */
	@Override
	protected void loadBillListTemplate() {
		getBillListPanel().setListData(this.getListData());
		getTempletEventAgent().initBillListPane(getBillListPanel());
	}
	
	@Override
	protected DjLXVO[] getDjlxvosByNodeCode() {
		String group = WorkbenchEnvironment.getInstance().getGroupVO().getPrimaryKey();

		DjLXVO[] djLXVOs = new DjLXVO[] {};
		try {
			djLXVOs = ProxyDjlx.getIArapBillTypePublic().queryByWhereStr(" dr=0  and djdl in ('jk','bx') and pk_group='" + group +"'");

		} catch (Exception e) {
			handleException(e);
		}
		return djLXVOs;
	}
	
	/**
	 * v6.1新增  缓存当前交易类型的查询对话框
	 */
	private Map<String,QueryConditionDLG> qryDlgMap;
	
	private Map<String, QueryConditionDLG> getQryDlgMap() {
		if(qryDlgMap==null){
			qryDlgMap = new HashMap<String, QueryConditionDLG>();
		}
		return qryDlgMap;
	}
	
	private QueryConditionDLG getQryDlg(String djlxbm){
		if (getQryDlgMap().get(djlxbm) == null) {
			TemplateInfo tempinfo = new TemplateInfo();
			tempinfo.setPk_Org(BXUiUtil.getPK_group());
			tempinfo.setCurrentCorpPk(BXUiUtil.getPK_group());
			tempinfo.setFunNode(getNodeCode());
			tempinfo.setUserid(BXUiUtil.getPk_user());
			QueryConditionDLG dlg = new BxQueryDLG(this, null, tempinfo,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("common","UC000-0002782")/* @res "查询条件" */, getNodeCode(),getNodeCode(),djlxbm);
			getQryDlgMap().put(djlxbm, dlg);
		}
		return getQryDlgMap().get(djlxbm);
	}

	@Override
	public QueryConditionDLG getQryDlg() {
//		if (queryDialog == null || queryDialog.getParent()==null) {
//
//			TemplateInfo tempinfo = new TemplateInfo();
//			tempinfo.setPk_Org(BXUiUtil.getPK_group());			
//			tempinfo.setCurrentCorpPk(BXUiUtil.getPK_group());
//			tempinfo.setFunNode(getNodeCode());
//			tempinfo.setUserid(BXUiUtil.getPk_user());
//			tempinfo.setNodekey(getCache().getCurrentDjlxbm());
//			
//			queryDialog = new BxQueryDLG(this, null, tempinfo,
//					nc.ui.ml.NCLangRes.getInstance().getStrByID("common",
//							"UC000-0002782")/*
//											 * @res "查询条件"
//											 */, getNodeCode());
//		}
//		return queryDialog;
		
//begin--added by chendya v6.1修改
		return getQryDlg(null);
//--end		
		
	}

	
	public List<Action> getActions() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public IFunNodeClosingListener getCloseListener() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<Action> getEditActions() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public JComponent getExComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setLoginContext(LoginContext context) {
		// TODO Auto-generated method stub
		
	}

	
	public void setNodeType(NodeType nodeType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Action> getActions(String[] actionnames) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Action> getActions(String nodetype) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Action> getEditActions(String nodetype) {
		// TODO Auto-generated method stub
		return null;
	}

}
