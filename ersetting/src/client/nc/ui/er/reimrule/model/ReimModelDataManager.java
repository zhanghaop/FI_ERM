package nc.ui.er.reimrule.model;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.er.reimtype.IReimTypeService;
import nc.ui.bd.pub.BDOrgPanel;
import nc.ui.er.reimrule.ReimRuleUtil;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

public class ReimModelDataManager implements IAppModelDataManager,
		IPaginationModelListener, AppEventListener {
	/**
	 * @author shiwla
	 */
	private BatchBillTableModel model = null;
	private BDOrgPanel orgPanel = null;
	private IExceptionHandler exceptionHandler = null;

	public BatchBillTableModel getModel() {
		return model;
	}

	public void setModel(BatchBillTableModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	public void onDataReady() {
//		paginationDelegator.onDataReady();
	}

	@Override
	public void onStructChanged() {

	}

	@Override
	public void handleEvent(AppEvent event) {
//		paginationDelegator.handleEvent(event);
	}


	public void initModel(String djlxbm) {
		List<SuperVO> vos = ReimRuleUtil.getDataMapDim().get(djlxbm);
		getModel().initModel(vos.toArray(new SuperVO[0]));
		getModel().setUiState(UIState.EDIT);
	}

	@Override
	public void initModel() {
		String djlxbm = getOrgPanel().getRefPane().getUITextField().getValue().toString();
		if(djlxbm == null)
			getModel().initModel(null);
		else{
			List<SuperVO> vos = null;
			String[] str = djlxbm.split(";");
			getModel().getContext().setPk_org(str[1]);
			if(str[2].equals("true")){
				vos = ReimRuleUtil.getDataMapDim().get(str[0]);
				//如果未设置维度，则进行初始化
				if(vos==null || vos.size()==0){
					if(vos==null)
						vos = new ArrayList<SuperVO>();
					try {
						List<ReimRuleDimVO> vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
								.queryReimDim("2631", "GLOBLE00000000000000", "~");
						for(ReimRuleDimVO dimvo:vodims){
							dimvo.setPk_billtype(str[0]);
							dimvo.setPk_org(str[1]);
							vos.add(dimvo);
						}
					}catch (BusinessException ex) {
						ExceptionHandler.consume(ex);
					}
				}
				getModel().initModel(vos.toArray(new SuperVO[0]));
				getModel().getContext().setPk_org(str[1]);
			}
			else{
				String centControlItem = null;
				List<SuperVO> reimruledim=ReimRuleUtil.getDataMapDim().get(str[0]);
				if (reimruledim!=null && reimruledim.size()>0) {
					for(SuperVO vo:reimruledim)
					{
						if(((ReimRuleDimVO)vo).getControlflag().booleanValue())
						{
							centControlItem=((ReimRuleDimVO)vo).getCorrespondingitem();
							break;
						}
					}
				}
				getModel().clearState();
				vos = ReimRuleUtil.getDataMapRule().get(str[0]);
				if(centControlItem!=null && vos!=null){
					List<SuperVO> vos1 = new ArrayList<SuperVO>();
					StringBuilder cents = new StringBuilder();
					for(SuperVO vo:vos){
						if(!cents.toString().contains((String) vo.getAttributeValue(centControlItem))){
							vos1.add(vo);
							cents.append((String) vo.getAttributeValue(centControlItem));
						}
					}
					getModel().initModel(vos1.toArray(new SuperVO[0]));
					getModel().getContext().setPk_org(str[1]);
				}
				else
					getModel().initModel(null);
			}
			
		}
		getModel().setUiState(UIState.EDIT);
	}

	public BDOrgPanel getOrgPanel() {
		return orgPanel;
	}

	public void setOrgPanel(BDOrgPanel orgPanel) {
		this.orgPanel = orgPanel;
	}
	
}
