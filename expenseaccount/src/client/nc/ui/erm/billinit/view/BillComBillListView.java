package nc.ui.erm.billinit.view;

import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.org.IOrgUnitPubService;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.erm.view.ERMBillListView;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillTableCellRenderer;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.MultiLangText;

/**
 * 常用单据列表界面
 * @author wangled
 * @version V6.0
 * @since V6.0 创建时间：2013-2-20 下午02:50:06
 */
public class BillComBillListView extends ERMBillListView
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public void initUI() {
        super.initUI();
        String nodeCode = getModel().getContext().getNodeCode();
        if(nodeCode.equals(BXConstans.BXINIT_NODECODE_G)){
            getBillListPanel().hideHeadTableCol("setorg");
        }else if(nodeCode.equals(BXConstans.BXINIT_NODECODE_U)){
            getBillListPanel().hideHeadTableCol("pk_group");
          
            //设置组织显示控制
            setSetOrgValue();
        }
        
    	/** 为特殊的字段设置渲染器（单据类型、事由）*/
		resetSpecialItemCellRender();
        
    }
	/**
	 * 设置组织显示控制
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	private void setSetOrgValue() {
		String name = getBillListPanel().getBillListData().getHeadItem("setorg").getName();
		getBillListPanel().getHeadTable().getColumn(name).setCellRenderer(new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Object obj = null;
				Boolean isInitGroup = (Boolean) getBillListPanel().getHeadBillModel().getValueAt(row, JKBXHeaderVO.ISINITGROUP);
				if (isInitGroup != null && isInitGroup.booleanValue()) {
					obj = getBillListPanel().getHeadBillModel().getValueAt(row, JKBXHeaderVO.PK_GROUP);
				} else {
					obj = getBillListPanel().getHeadBillModel().getValueAt(row, JKBXHeaderVO.PK_ORG);
				}
				
				if(obj != null){
					String name = null;
					try {
						OrgVO[] orgs = NCLocator.getInstance().lookup(IOrgUnitPubService.class).getOrgs(new String[]{(String) obj}, new String[]{});
						name = getNameByMuti(orgs[0]);
					} catch (BusinessException e) {
						throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0144"));
					}
					
					
					setValue(name);
				}
				
				return this;
			}
		});
	}
    public String getNameByMuti(SuperVO vo) {
		MultiLangText multLangText = new MultiLangText();
		switch (multLangText.getCurrLangIndex()) {
			case 0:
				return (String) vo.getAttributeValue("name");
			case 1:
				return (String) vo.getAttributeValue("name2");
			case 2:
				return (String) vo.getAttributeValue("name3");
			case 3:
				return (String) vo.getAttributeValue("name4");
			case 4:
				return (String) vo.getAttributeValue("name5");
			case 5:
				return (String) vo.getAttributeValue("name6");
		}
		return null;
	}
    @Override
    protected void synchronizeDataFromModel()
    {

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
            getBillListPanelValueSetter().setBodyData(billListPanel,
                    getModel().getSelectedData());

            setHeadTableHighLightByModelSelection();

            setCheckBoxMultiUnstate();
        }
    }
    
    @Override
    public void showMeUp()
    {
        super.showMeUp();
        synchronizeDataFromModel();
    }
    
    private void resetSpecialItemCellRender() {
		try {
			BillItem djbmItem = getBillListPanel().getBillListData().getHeadItem(JKBXHeaderVO.DJLXBM);
			if(djbmItem!=null && djbmItem.isShow()){
				String djlxbm = djbmItem.getName();
				getBillListPanel().getHeadTable().getColumn(djlxbm).setCellRenderer(new BillTableCellRenderer() {
					private static final long serialVersionUID = -7709616533529134473L;
					@SuppressWarnings("unchecked")
					@Override
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
						super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
						List<JKBXVO> data = getModel().getData();
						if(data != null && data.size() != 0){
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
			if(headItem!=null && headItem.isShow()){
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
