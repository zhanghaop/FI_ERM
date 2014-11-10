package nc.ui.erm.billpub.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;

import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.common.ErmConst;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.funcnode.ui.FuncletInitData;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.fi.pub.Currency;
import nc.ui.arap.bx.listeners.BxYbjeDecimalListener;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.eventhandler.InitCurrencyDecimalListener;
import nc.ui.erm.model.ERMBillManageModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.erm.view.ERMBillListView;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemHyperlinkEvent;
import nc.ui.pub.bill.BillItemHyperlinkListener;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillTableCellRenderer;
import nc.ui.pub.linkoperate.ILinkType;
import nc.ui.uap.sf.SFClientUtil2;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.link.LinkQuery;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

public class ErmBillBillListView extends ERMBillListView {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void initUI() {
		super.initUI();

		BillItem item = getBillListPanel().getHeadItem(JKBXHeaderVO.DJBH);
		item.addBillItemHyperlinkListener(getLinklistener());

		
		ListBillItemHyperlinkListener ll = new ListBillItemHyperlinkListener();
		BillItem pk_item_billno = getBillListPanel().getHeadItem(JKBXHeaderVO.PK_ITEM_BILLNO);
		// 注意还款单没有pk_item_billno字段
		if (pk_item_billno != null) {
			pk_item_billno.addBillItemHyperlinkListener(ll);
		}
		BillItem pk_item_billno2 = getBillListPanel().getBodyItem(BXBusItemVO.FCTNO);
		if (pk_item_billno2 != null) {
			pk_item_billno2.addBillItemHyperlinkListener(ll);
		}

		/** 添加精度监听 **/
		addDecimalListenerToListpanel(getBillListPanel());
		
		/** 为特殊的字段设置渲染器（单据类型、事由）*/
		resetSpecialItemCellRender();
		
		//期初或是常用单据,隐藏费用申请单字段
		if(((ErmBillBillManageModel)getModel()).isInit()||((ErmBillBillManageModel)getModel()).iscydj()){
			getBillListPanel().hideHeadTableCol("pk_item.billno");
		}
		
	
	}
	/**
	 * 列表界面超链接
	 */
	private class ListBillItemHyperlinkListener implements BillItemHyperlinkListener {
		@Override
		public void hyperlink(BillItemHyperlinkEvent event) {
			if (event != null) {
				if(event.getItem().getKey().equals(JKBXHeaderVO.PK_ITEM)){
					if (getModel().getData() != null) {
						JKBXVO vo = (JKBXVO) getModel().getData().get(event.getRow());
						String pkItem = vo.getParentVO().getPk_item();
						if(!StringUtils.isEmpty(pkItem)){
							LinkQuery linkQuery = new LinkQuery(ErmBillConst.MatterApp_DJDL,new String[] { pkItem });
							FuncletInitData initData = new FuncletInitData();
							initData.setInitData(linkQuery);
							initData.setInitType(ILinkType.LINK_TYPE_QUERY);
							SFClientUtil2.openFuncNodeDialog(getBillListPanel(), BXConstans.MTAMN_NODE, initData, null, false,
									false, null, new String[] { ErmConst.BUSIACTIVE_LINKQUERY });
						}
					}
				}else if(event.getItem().getKey().equals(BXBusItemVO.FCTNO)){
					String pk =(String) event.getValue();
					LinkQuery linkQuery = new LinkQuery("Z4", pk);
					FuncletInitData initData = new FuncletInitData();
					initData.setInitData(linkQuery);
					initData.setInitType(ILinkType.LINK_TYPE_QUERY);
					SFClientUtil2.openFuncNodeDialog(getBillListPanel(), "200401APM", initData, null, false,
							false, null, null);
				}
			}
		}
	}

	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if( AppEventConst.SELECTION_CHANGED == event.getType()){
		    resetTableBodyData(); 
		}  
		if (AppEventConst.DATA_UPDATED == event.getType() || AppEventConst.SELECTED_DATE_CHANGED == event.getType()) {
			synchronizeDataFromModel();
		}
	}

	/**
	 * 表体精度
	 */
	private void resetTableBodyData() {
		/**添加表体精度监听 **/
		JKBXVO zbvo =(JKBXVO) getModel().getSelectedData();
		if(zbvo == null){
			return;
		}
		String[] tables = getBillListPanel().getBillListData().getBodyTableCodes();
		if(tables == null){
			return;
		}
		
		for (String table : tables) {
			BillItem[] bodyItems = getBillListPanel().getBodyBillModel(table).getBodyItems();
			for (BillItem bodyitem : bodyItems) {
				BXUiUtil.resetDecimalDigits(bodyitem, zbvo);
			}

			CircularlyAccessibleValueObject[] voArray = zbvo.getTableVO(table);

			getBillListPanel().setBodyValueVO(table, voArray);
			getBillListPanel().getBodyBillModel(table).loadLoadRelationItemValue();
			getBillListPanel().getBodyBillModel(table).execLoadFormula();
			// 不可编辑
			getBillListPanel().getBodyBillModel(table).setEnabled(false);
		}
	}

	@Override
	protected void synchronizeDataFromModel() {

		Object[] datas = getModel().getData().toArray();

		if (datas == null || datas.length == 0) { // 如果没有数据，则清除
			billListPanel.getHeadBillModel().clearBodyData();
			billListPanel.getBodyBillModel().clearBodyData();
		} else {

			JKBXHeaderVO[] headVos = new JKBXHeaderVO[datas.length];
			for (int i = 0; i < datas.length; i++) {
				headVos[i] = (JKBXHeaderVO) ((JKBXVO) datas[i]).getParentVO();
			}

			getBillListPanelValueSetter()
					.setHeaderDatas(billListPanel, headVos);
			
			if (getModel().getSelectedData() != null) {
				try {
					JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
					if (selectedData.getChildrenVO() == null || selectedData.getChildrenVO().length == 0) {
						boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(selectedData.getParentVO().getPk_group(), selectedData.getParentVO().getDjlxbm(),ErmDjlxConst.BXTYPE_ADJUST);
						if(isAdjust && selectedData.getcShareDetailVo()!=null && selectedData.getcShareDetailVo().length!=0){
							((ErmBillBillManageModel) getModel()).directlyUpdateWithoutFireEvent(selectedData);

						}else{
							List<JKBXVO> jkbxvo = NCLocator
							.getInstance()
							.lookup(IBXBillPrivate.class)
							.queryVOsByPrimaryKeysForNewNode(
									new String[] { selectedData.getParentVO().getPrimaryKey() },
									selectedData.getParentVO().getDjdl(), selectedData.getParentVO().isInit(),
									((ErmBillBillManageModel) getModel()).getDjCondVO());
							if (jkbxvo != null) {
								// 更新model数据
								selectedData = jkbxvo.get(0);
							}
							((ErmBillBillManageModel) getModel()).directlyUpdateWithoutFireEvent(selectedData);
						}
					}

				} catch (Exception e) {
					ExceptionHandler.handleExceptionRuntime(e);
				}
			}
			
			getBillListPanelValueSetter().setBodyData(billListPanel,
					getModel().getSelectedData());

			setHeadTableHighLightByModelSelection();

			setCheckBoxMultiUnstate();
		}
	}
	
	 
	/**
	 * 方法说明：
	 * <p>修改记录：</p>
	 * @param listPanel
	 * @param vo
	 * @see 
	 * @since V6.0
	 */
	public void addDecimalListenerToListpanel(BillListPanel listPanel) {
		// 添加汇率精度监听器
		if (listPanel.getHeadItem(JKBXHeaderVO.BBHL) != null) {
			InitCurrencyDecimalListener listener = new InitCurrencyDecimalListener((ERMBillManageModel) getModel());
			listPanel.getHeadBillModel().addDecimalListener(listener);

		}
		// 添加原币金额币种精度监听器
		String[] targets = null;
		ArrayList<String> values = new ArrayList<String>();
		String[] keys = JKBXHeaderVO.getYbjeField();
		for (String key : keys) {
			if (listPanel.getHeadItem(key) != null) {
				values.add(key);
			}
		}
		targets = new String[values.size()];
		values.toArray(targets);
		BxYbjeDecimalListener listener2 = new BxYbjeDecimalListener();
		listener2.setTarget(targets);
		listPanel.getHeadBillModel().addDecimalListener(listener2);
		// 设置本币金额币种精度
		// String bbjeCurrency = "";
		// int bbkeCurrencyPrecision = 0;// 本币币种精度
		// try {
		// bbjeCurrency =
		// Currency.getOrgLocalCurrPK(WorkbenchEnvironment.getInstance().getGroupVO().getPk_group());
		// bbkeCurrencyPrecision = Currency.getCurrDigit(bbjeCurrency);
		// } catch (Exception e) {
		// }
		// String[] bbjeKeys = JKBXHeaderVO.getBbjeField();
		// for (String key : bbjeKeys) {
		// if (listPanel.getHeadItem(key) != null) {
		// listPanel.getHeadItem(key).setDecimalDigits(bbkeCurrencyPrecision);
		// }
		// }
	}
	
	/**
     * 设置原币精度 设置本币金额精度 设置集团本币精度 设置全局本币精度
     */
    public static void resetDecimalDigits(BillItem item, JKBXVO zbvo) {
        // 设置原币精度
        if (item.getKey().equals(BXBusItemVO.AMOUNT) || (item.getIDColName() != null && item.getIDColName().equals(BXBusItemVO.AMOUNT))
                || item.getKey().equals(BXBusItemVO.YBJE) || item.getKey().equals(BXBusItemVO.HKYBJE) || item.getKey().equals(BXBusItemVO.ZFYBJE)
                || item.getKey().equals(BXBusItemVO.CJKYBJE)|| item.getKey().equals(CShareDetailVO.ASSUME_AMOUNT)) {
            String bzbm = zbvo.getParentVO().getBzbm();
            if (bzbm != null) {
                int precision = 2;
                try {
                    precision = Currency.getCurrDigit(bzbm);
                } catch (Exception e1) {
                    Log.getInstance("BXuiUtil").error(e1);
                }
                item.setDecimalDigits(precision);
            }
        }

        // 设置组织本币精度
        else if (item.getKey().equals(BXBusItemVO.BBJE) || item.getKey().equals(BXBusItemVO.CJKBBJE) || item.getKey().equals(BXBusItemVO.HKBBJE)
                || item.getKey().equals(BXBusItemVO.ZFBBJE)) {
            int bbkeCurrencyPrecision = 0;
            try {
                bbkeCurrencyPrecision = Currency.getCurrDigit(Currency.getOrgLocalCurrPK(zbvo.getParentVO().getPk_org()));
                item.setDecimalDigits(bbkeCurrencyPrecision);
            } catch (BusinessException e1) {
                Log.getInstance("BXuiUtil").error(e1);
            }
        }
        // 设置集团本币精度
        else if (item.getKey().equals(BXBusItemVO.GROUPBBJE) || item.getKey().equals(BXBusItemVO.GROUPCJKBBJE) || item.getKey().equals(BXBusItemVO.GROUPHKBBJE)
                || item.getKey().equals(BXBusItemVO.GROUPZFBBJE)) {
            int GroupbbkeCurrencyPrecision = 0;
            try {
                GroupbbkeCurrencyPrecision = Currency.getCurrDigit(Currency.getGroupCurrpk(zbvo.getParentVO().getPk_group()));
                item.setDecimalDigits(GroupbbkeCurrencyPrecision);
            } catch (BusinessException e1) {
                Log.getInstance("BXuiUtil").error(e1);
            }
        }
        // 设置全局本币精度
        else if (item.getKey().equals(BXBusItemVO.GLOBALBBJE) || item.getKey().equals(BXBusItemVO.GLOBALCJKBBJE) || item.getKey().equals(BXBusItemVO.GLOBALHKBBJE)
                || item.getKey().equals(BXBusItemVO.GLOBALZFBBJE)) {
            int GroupbbkeCurrencyPrecision = 0;
            try {
                GroupbbkeCurrencyPrecision = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));
                item.setDecimalDigits(GroupbbkeCurrencyPrecision);
            } catch (BusinessException e1) {
                Log.getInstance("BXuiUtil").error(e1);
            }
        }
    }
	
	@Override
	protected void handleRowInserted(AppEvent event) {
		super.handleRowInserted(event);
		//同步条码输入的数据
//		synchronizeDataFromModel();//创维修改，列表排序后，新增保存的单据切换为了最后一行的数据，出现问题
	}

	@Override
	public void showMeUp() {
		super.showMeUp();
		synchronizeDataFromModel();
	}
	
	private void resetSpecialItemCellRender() {
		try {
			BillItem djbmItem = getBillListPanel().getBillListData().getHeadItem(JKBXHeaderVO.DJLXMC);
			if(djbmItem.isShow()){
				String djlxbm = djbmItem.getName();
				getBillListPanel().getHeadTable().getColumn(djlxbm).setCellRenderer(new BillTableCellRenderer() {
					private static final long serialVersionUID = -7709616533529134473L;
					@SuppressWarnings("unchecked")
					@Override
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
						super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
						List<JKBXVO> data = getModel().getData();
						if(data != null && data.size() != 0 && row < data.size()){
							JKBXVO vo = (JKBXVO) data.get(row);
							if(vo != null){
								setValue(ErUiUtil.getDjlxNameMultiLang((vo.getParentVO().getDjlxbm())));
							}
						}
						return this;
					}
				});
			}
			

			BillItem headItem = getBillListPanel().getBillListData().getHeadItem(JKBXHeaderVO.ZY);
			if(headItem.isShow()){
				String reasonName = headItem.getName();
				getBillListPanel().getHeadTable().getColumn(reasonName).setCellRenderer(new BillTableCellRenderer() {
					private static final long serialVersionUID = -7709616533529134473L;
					@SuppressWarnings("unchecked")
					@Override
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
						super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
						List<JKBXVO> data = getModel().getData();
						if(data != null && data.size() != 0){
							JKBXVO vo = (JKBXVO) data.get(row);
							if(vo != null){
								setValue(vo.getParentVO().getZy());
							}
						}
						return this;
					}
				});
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}
}
