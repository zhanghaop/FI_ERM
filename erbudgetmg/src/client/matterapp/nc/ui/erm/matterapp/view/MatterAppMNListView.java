package nc.ui.erm.matterapp.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;

import nc.bs.erm.common.ErmBillConst;
import nc.bs.framework.core.util.ObjectCreator;
import nc.bs.logging.Logger;
import nc.itf.erm.extendconfig.ErmExtendconfigCache;
import nc.ui.erm.extendtab.AbstractErmExtendList;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.erm.view.ERMBillListView;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.BillMouseEnent;
import nc.ui.pub.bill.BillTableCellRenderer;
import nc.ui.pub.bill.BillTableMouseListener;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.ToftPanelAdaptor;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.extendconfig.ErmExtendConfigVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.MultiLangUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletVO;

public class MatterAppMNListView extends ERMBillListView implements BillTableMouseListener {
	private static final long serialVersionUID = 1L;

	private Map<String, BillTabVO> tabInfo = new HashMap<String, BillTabVO>();

	private List<BillTabVO> tabvolist = new ArrayList<BillTabVO>();

	private List<AbstractErmExtendList> tabpanelist =
	      new ArrayList<AbstractErmExtendList>();


	public MatterAppMNListView() {
		super();
	}

	@Override
	public void initUI() {
		super.initUI();

		billListPanel.addMouseListener(new BillTableMouseListener() {
			@Override
			public void mouse_doubleclick(BillMouseEnent e) {
				if (e.getPos() == BillItem.HEAD)
					onHeadMouseDBClick(e);
				else if (e.getPos() == BillItem.BODY)
					onBodyMouseDBClick(e);

			}
		});
		/**
		 * 单据编号增加超链接
		 */
		BillItem item = billListPanel.getHeadItem(MatterAppVO.BILLNO);
		item.addBillItemHyperlinkListener(getLinklistener());

		MatterAppUiUtil.addDigitListenerToListpanel(this.getBillListPanel());

		// 设置交易类型名称、事由的显示
		resetSpecialItemCellRender();

	}

	@Override
	protected void processErmBillListData(BillListData bld) {
		super.processErmBillListData(bld);
		// 处理扩展页签
		this.dealExtendTab(bld);
	}

	@Override
	protected void processBillInfo(BillTempletVO template) {
		super.processBillInfo(template);
		if (!this.tabvolist.isEmpty()) {
			for (int i = 0; i < this.tabvolist.size(); i++) {
				AbstractErmExtendList scrollPane = this.tabpanelist.get(i);
			      
				BillTabVO extendtab = this.tabvolist.get(i);
				scrollPane.setTableModel(this.getBillListPanel().getBillListData().getBodyBillModel(extendtab.getTabcode()));
				
				this.getBillListPanel()
						.getBodyTabbedPane()
						.addScrollPane(extendtab,
								scrollPane);

			}
		}
	}
	  
	private void dealExtendTab(BillListData bld) {
		String pk_group = this.getModel().getContext().getPk_group();
		ToftPanelAdaptor toftPanel = (ToftPanelAdaptor) this.getModel()
				.getContext().getEntranceUI();
		String ma_tradetype = toftPanel.getParameter("transtype");
		if (ma_tradetype == null) {
			ma_tradetype = ErmBillConst.MatterApp_base_tradeType;
		}
		ErmExtendConfigVO[] extendConfigVO = null;
		try {
			extendConfigVO = ErmExtendconfigCache.getInstance()
					.getErmExtendConfigVOs(pk_group, ma_tradetype);
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
		if (extendConfigVO == null || extendConfigVO.length == 0) {
			return;
		}
		
		String suffix = MultiLangUtil.getCurrentLangSeqSuffix();
		// 增加表体页签
		for (ErmExtendConfigVO tabvo : extendConfigVO) {
			if (StringUtil.isEmpty(tabvo.getCardclass())) {
				continue;
			}
			String busi_tabname = (String) tabvo.getAttributeValue("busi_tabname" + suffix);
			if(StringUtil.isEmpty(busi_tabname) ){
				busi_tabname = (String) tabvo.getAttributeValue("busi_tabname");
			}
			BillTabVO btvo = new BillTabVO();
			btvo.setPos(Integer.valueOf(IBillItem.BODY));
			btvo.setTabcode(tabvo.getBusi_tabcode());
			btvo.setTabname(busi_tabname);
			btvo.setMetadataclass(tabvo.getMetadataclass()); 

			this.tabInfo.put(tabvo.getBusi_tabcode(), btvo);
			// 通过反射获得扩展页签
			AbstractErmExtendList scrollPanes = MatterAppMNListView
					.createInstance(tabvo.getListclass(), tabvo.getBusi_sys());
			scrollPanes.setModel(this.getModel());
			scrollPanes.setListView(this);
			scrollPanes.setTableCode(tabvo.getBusi_tabcode());
			scrollPanes.setTableName(tabvo.getBusi_tabname());
			scrollPanes.initUI();
			
			// 扩展页签加入到billdata
			bld.setBillModel(btvo.getTabcode(), scrollPanes.getTableModel());
			
			this.tabvolist.add(btvo);
		    this.tabpanelist.add(scrollPanes);

		}
	}

	private static AbstractErmExtendList createInstance(String className,
			String busiSysCode) {

		AbstractErmExtendList instance = null;
		try {
			if (StringUtil.isEmptyWithTrim(busiSysCode)) {
				instance = (AbstractErmExtendList) ObjectCreator
						.newInstance(className);
			} else {
				instance = (AbstractErmExtendList) ObjectCreator.newInstance(
						busiSysCode, className);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(
					"cann't create instance. ClassName: "
							+ className
							+ ",devModuleCode:"
							+ busiSysCode
							+ ". Please check register info in table er_extendconfig");
		}
		return instance;
	}

	/**
	 * 设置交易类型名称
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	@SuppressWarnings("serial")
	private void resetSpecialItemCellRender() {
		try {
			String name = getBillListPanel().getBillListData().getHeadItem(MatterAppVO.PK_TRADETYPE).getName();
			getBillListPanel().getHeadTable().getColumn(name).setCellRenderer(new BillTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					@SuppressWarnings("rawtypes")
					List data = getModel().getData();
					if(data != null){
						if(row < data.size()){
							AggMatterAppVO vo = (AggMatterAppVO) data.get(row);
							if(vo != null){
								setValue(ErUiUtil.getDjlxNameMultiLang((vo.getParentVO().getPk_tradetype())));
							}
						}
					}
					return this;
				}
			});
			
			String reasonName = getBillListPanel().getBillListData().getHeadItem(MatterAppVO.REASON).getName();
			
			getBillListPanel().getHeadTable().getColumn(reasonName).setCellRenderer(new BillTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					@SuppressWarnings("rawtypes")
					List data = getModel().getData();
					if(data != null){
						if(row < data.size()){
							AggMatterAppVO vo = (AggMatterAppVO) data.get(row);
							if(vo != null){
								setValue((String)vo.getParentVO().getReason());
							}
						}
					}
					return this;
				}
			});
		} catch (IllegalArgumentException e) {
			ExceptionHandler.consume(e);
		}
	}

  @Override
  protected void handleSelectionChanged() {
    super.handleSelectionChanged();
    if (!this.tabpanelist.isEmpty()) {
      for (AbstractErmExtendList tabpane : this.tabpanelist) {
        tabpane.setValue(this.getModel().getSelectedData());
      }
    }
  }
}
