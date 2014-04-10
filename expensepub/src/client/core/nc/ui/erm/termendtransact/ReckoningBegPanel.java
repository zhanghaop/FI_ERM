package nc.ui.erm.termendtransact;

import java.awt.Color;
import java.util.Vector;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.termendtransact.ICloseAccountService;
import nc.itf.uap.sf.ICreateCorpQueryService;
import nc.pubitf.accperiod.AccountCalendar;
import nc.ui.pub.beans.UITextArea;
import nc.ui.pub.beans.table.NCTableModel;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.termendtransact.AccountInfo;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.erm.termendtransact.FilterCondVO;
import nc.vo.erm.termendtransact.FirstNotClosedAccountMonthVO;
import nc.vo.erm.termendtransact.SystemInfoVO;
public class ReckoningBegPanel extends nc.ui.pub.beans.UIPanel implements javax.swing.event.TableModelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1035442667054812753L;
	private nc.ui.pub.beans.UILabel ivjUILabel11 = null;
	private UITextArea ivjUILabel12 = null;
	private nc.ui.pub.beans.UIPanel ivjUIPanel1 = null;
	private nc.ui.pub.beans.UIPanel ivjUIPanel4 = null;
	private nc.ui.pub.beans.UITablePane ivjPeriodTable = null;
	private nc.ui.pub.beans.UILabel ivjUILabel1 = null;
	private nc.ui.pub.beans.UIPanel ivjUIPanel3 = null;
	private String[] m_sTitle = {nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UC000-0000239")/*@res "会计月份"*/, nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UC000-0000240")/*@res "会计期间"*/, nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000053")/*@res "期间状态"*/, nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UC000-0004045")/*@res "选择标志"*/};
	private String m_sAccount = nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000054")/*@res "已结账"*/;
	private String m_sDisAccount = nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000055")/*@res "未结账"*/;
	private String m_sDisEnabled = nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000056")/*@res "未启用"*/;
	private Vector<Vector<Object>> m_vData = new Vector<Vector<Object>>(); /*显示数据*/
	private int m_selRow = -1;
	private String m_sYearOfFirDisAccMon = null;/*第一个未结账月所在的年度*/
	private String m_sCopeOfFirDisAccMon = null;/*第一个未结账月所在的期间*/
	private String nodeCode = null;
	
	public String getNodeCode() {
			return nodeCode;
		}
		public void setNodeCode(String nodeCode) {
			this.nodeCode = nodeCode;
		}
	/**
	 * ReckoningBegPanel 构造子注解。
	 */
	public ReckoningBegPanel() {
		super();
		initialize();
	}
	/**
	 * ReckoningBegPanel 构造子注解。
	 * @param p0 java.awt.LayoutManager
	 */
	public ReckoningBegPanel(java.awt.LayoutManager p0) {
		super(p0);
	}
	/**
	 * ReckoningBegPanel 构造子注解。
	 * @param p0 java.awt.LayoutManager
	 * @param p1 boolean
	 */
	public ReckoningBegPanel(java.awt.LayoutManager p0, boolean p1) {
		super(p0, p1);
	}
	/**
	 * ReckoningBegPanel 构造子注解。
	 * @param p0 boolean
	 */
	public ReckoningBegPanel(boolean p0) {
		super(p0);
	}
	public ReckoningBegPanel(TermEndTransactUI transactUI) {
		super();
		this.transactUI=transactUI;
		initialize();
		
	}
	
	private TermEndTransactUI transactUI;
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-30 11:35:02)
	 * @return java.lang.String
	 */
	public String getCopeOfFDM() {
		return m_sCopeOfFirDisAccMon;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-17 13:58:48)
	 * @return java.util.Vector
	 */
	public Vector<Vector<Object>> getData() {
		return m_vData;
	}
	/**
	 * 返回 getPeriodTable 特性值。
	 * @return nc.ui.pub.beans.UITablePane
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITablePane getPeriodTable() {
		if (ivjPeriodTable == null) {
			try {
				ivjPeriodTable = new nc.ui.pub.beans.UITablePane();
				ivjPeriodTable.setName("PeriodTable");
				ivjPeriodTable.setPreferredSize(new java.awt.Dimension(480, 410));
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjPeriodTable;
	}
	/**
	 * 主要功能：取得用户选中的结账月的信息
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-17 18:03:21)
	 * 最后修改日期：(2001-8-17 18:03:21)
	 * @author：wyan
	 * @return java.lang.String[]
	 */
	public FilterCondVO getSelectedData() throws DataValidateException{
	
		if(m_selRow == -1){
			throw new DataValidateException(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000057")/*@res "请至少选择一个期间进行月末处理！"*/);
		}
	
		FilterCondVO voCond = new FilterCondVO();
		NCTableModel model = (NCTableModel) getPeriodTable().getTable().getModel();
		String period = model.getValueAt(m_selRow, 0).toString();
		String cope = model.getValueAt(m_selRow, 1).toString();
		String state = model.getValueAt(m_selRow, 2).toString().trim();
		if(state.equals(m_sDisEnabled)){
			throw new DataValidateException(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000058")/*@res "不能对未启用的会计期间进行操作！"*/);
		}
	
		int index = period.indexOf(".");
		String ReckoningYear = period.substring(0, index); /*选中期间*/
		String ReckoningQj = period.substring(index + 1); /*选中期间所在年度*/
		index = cope.indexOf("-----");
		String BegDate = cope.substring(0, index); /*选中期间起始日期*/
		String EndDate = cope.substring(index + 5); /*选中期间结束日期*/
	
		voCond.setQj(ReckoningQj);
		voCond.setYear(ReckoningYear);
		voCond.setBegDate(BegDate);
		voCond.setEndDate(EndDate);
		if (state.equals(m_sAccount)) {
			state = "Y";
		} else {
			state = "N";
		}
		voCond.setPeriodSate(state); /*本月是否结账或者未结账*/
	
		return voCond;
	}
	/**
	 * 主要功能：暂时作废
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-21 10:20:32)
	 * 最后修改日期：(2001-8-21 10:20:32)
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getTransactState() {
		return null;
	}
	/**
	 * 返回 UILabel1 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabel1() {
		if (ivjUILabel1 == null) {
			try {
				ivjUILabel1 = new nc.ui.pub.beans.UILabel();
				ivjUILabel1.setName("UILabel1");
				ivjUILabel1.setText("");
				ivjUILabel1.setMaximumSize(new java.awt.Dimension(200, 200));
				ivjUILabel1.setForeground(java.awt.Color.black);
				ivjUILabel1.setILabelType(0/** Java默认(自定义)*/);
				ivjUILabel1.setPreferredSize(new java.awt.Dimension(52, 30));
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabel1;
	}
	/**
	 * 返回 UILabel11 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabel11() {
		if (ivjUILabel11 == null) {
			try {
				ivjUILabel11 = new nc.ui.pub.beans.UILabel();
				ivjUILabel11.setName("UILabel11");
				ivjUILabel11.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000059")/*@res "注:"*/);
				ivjUILabel11.setBounds(6, 0, 30, 21);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel11;
	}
	/**
	 * 返回 UILabel12 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private UITextArea getUILabel12() {
		if (ivjUILabel12 == null) {
			try {
				ivjUILabel12 = new UITextArea();
				ivjUILabel12.setName("UILabel12");
				ivjUILabel12.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000060")/*@res "月末结账后,不能进行任何处理!,"*/);
				ivjUILabel12.setForeground(java.awt.Color.black);
				ivjUILabel12.setEditable(false);
				ivjUILabel12.setEnabled(false);
				ivjUILabel12.setBackground(this.getBackground());
				ivjUILabel12.setLineWrap(true);
				ivjUILabel12.setForeground(Color.BLACK);
				ivjUILabel12.setBounds(36, 0, 320, 21);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabel12;
	}
	/**
	 * 返回 UIPanel1 特性值。
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIPanel getUIPanel1() {
		if (ivjUIPanel1 == null) {
			try {
				ivjUIPanel1 = new nc.ui.pub.beans.UIPanel();
				ivjUIPanel1.setName("UIPanel1");
				ivjUIPanel1.setPreferredSize(new java.awt.Dimension(500, 515));
				ivjUIPanel1.setLayout(new java.awt.BorderLayout());
				getUIPanel1().add(getPeriodTable(), "Center");
				getUIPanel1().add(getUILabel1(), "South");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIPanel1;
	}
	/**
	 * 返回 UIPanel3 特性值。
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIPanel getUIPanel3() {
		if (ivjUIPanel3 == null) {
			try {
				ivjUIPanel3 = new nc.ui.pub.beans.UIPanel();
				ivjUIPanel3.setName("UIPanel3");
				ivjUIPanel3.setPreferredSize(new java.awt.Dimension(90, 0));
				ivjUIPanel3.setLayout(null);
				ivjUIPanel3.add(getUILabel12(), null);
				// user code end
				ivjUIPanel3.add(getUILabel11(), null);
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIPanel3;
	}
	/**
	 * 返回 UIPanel4 特性值。
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIPanel getUIPanel4() {
		if (ivjUIPanel4 == null) {
			try {
				ivjUIPanel4 = new nc.ui.pub.beans.UIPanel();
				ivjUIPanel4.setName("UIPanel4");
				ivjUIPanel4.setPreferredSize(new java.awt.Dimension(420, 70));
				ivjUIPanel4.setLayout(new java.awt.BorderLayout());
				ivjUIPanel4.setMinimumSize(new java.awt.Dimension(320, 50));
				ivjUIPanel4.add(getUIPanel3(), java.awt.BorderLayout.CENTER);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIPanel4;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-30 11:35:02)
	 * @return java.lang.String
	 */
	public String getYearOfFDM() {
		return m_sYearOfFirDisAccMon;
	}
	/**
	 * 每当部件抛出异常时被调用
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable e) {
	
		ExceptionHandler.consume(e);
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-17 16:23:36)
	 */
	private void initConnection() {
	    getPeriodTable().getTable().getModel().addTableModelListener(this);
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-16 13:53:00)
	 */
	private void initData() {
	
		Vector<Vector<Object>> vResult = new Vector<Vector<Object>>();
		boolean findAccMonth = false; /*找到结账月*/
		boolean findEnabledMonth = false; /*找到启用月*/
		try {
			SystemInfoVO sysInfo = getTransactUI().getSysInfo();
			String curYear = sysInfo.getCurNd();
			String pk_org = sysInfo.getPk_org();
			String pk_group = sysInfo.getCurDwbm();
			String prodID = sysInfo.getProdID();
			//根据集团主键编码、模块编码查询启用期间
			String[] enableInfo = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).queryEnabledPeriod(pk_group, prodID);
	
			if(enableInfo == null || pk_org == null) {
				return ;
			}
			String enabledYear = enableInfo[0].substring(0, 4); /*启用年*/
			String enabledMonth = enableInfo[0].substring(5, 7); /*启用月*/
	
			//FIXME 这里延用的是57中uap的接口。uap可能会出新的接口，也可能需要自己写该接口
	//		String[] accountInfo = FipubTools.querySettledPeriod("0001","FI");
			String[] accountInfo = NCLocator.getInstance().lookup(ICloseAccountService.class).getCloseAccountInfo(getNodeCode(), pk_org);
			String accYear = accountInfo[0]; /*当前结账年*/
			String accMonth = accountInfo[1]; /*当前结账月*/
			/*第一个未结账月*/
			FirstNotClosedAccountMonthVO vo = AccountInfo.getFirstNotClosedAccountMonth(new String[]{enabledYear,enabledMonth},new String[]{accYear,accMonth},sysInfo);
			String year = vo.getNotAccYear();
			String month = vo.getNotAccMonth();
	//		findAccMonth = vo.getIsAccounted();
	
	
			/*用户登陆年是在启用年(要标明未启用月)*/
			if (enabledYear.equals(curYear)) {
				AccountCalendar ac = AccountCalendar.getInstance();
				ac.set(curYear);
	
				AccperiodmonthVO[] months = ac.getMonthVOsOfCurrentYear();
				
				//若结账年和结账月均为空，说明未结账 ，设置 findAccMonth = true，这样以后的状态均为未结帐 
				if(("".equals(accYear) && "".equals(accMonth)) || (accYear == null && accMonth == null)) {
					findAccMonth = true;
					findEnabledMonth = true;
				}
				
				for (int i = 0; i < months.length; i++) {
					String periodNum = months[i].getYearmth().substring(5,7);
					String begDate = months[i].getBegindate().toString();
					String endDate = months[i].getEnddate().toString();
					Vector<Object> vRow = new Vector<Object>();
					vRow.addElement(curYear + "." + periodNum);
					vRow.addElement(begDate + "-----" + endDate);
					if (!findEnabledMonth) {
						if (periodNum.equals(enabledMonth)) {
							findEnabledMonth = true;
							if (!findAccMonth ) {
								if (curYear.equals(accYear) && periodNum.equals(accMonth)) {
									findAccMonth = true;
								}											
							}
							vRow.addElement(m_sAccount);
						} else {
							vRow.addElement(m_sDisEnabled);
						}
					} else {
						if (!findAccMonth ) {
							if (curYear.equals(accYear) && periodNum.equals(accMonth)) {
								findAccMonth = true;
							}						
							vRow.addElement(m_sAccount);				
						} else {
							vRow.addElement(m_sDisAccount);
						}
					}
					vRow.addElement(new Boolean(false));
					vResult.addElement(vRow);
				}
			}
			/*用户登陆年不在启用年(不用标明未启用的月)*/
			else {
				/*如果用户登陆年在已经结账的最后一个期间所在年度的后面*/
				/*将是否找到结账月设置成为是以保证用户当前登陆年的期间全部显示成未结账*/
				if (accYear==null || accYear.trim().length()==0 || Integer.valueOf(curYear).intValue() > Integer.valueOf(accYear).intValue())
					findAccMonth = true;
				AccountCalendar ac = AccountCalendar.getInstance();
				ac.set(curYear);
	
				AccperiodmonthVO[] months = ac.getMonthVOsOfCurrentYear();
				for (int i = 0; i < months.length; i++) {
					String periodNum = months[i].getYearmth();
					String begDate = months[i].getBegindate().toString();
					String endDate = months[i].getEnddate().toString();
					Vector<Object> vRow = new Vector<Object>();
					vRow.addElement(curYear + "." + periodNum);
					vRow.addElement(begDate + "-----" + endDate);
					if (!findAccMonth) {
						/*如果用户登陆年不在已经结账的最后一个期间所在年度*/
						/*判断用户登陆年的期间是否是结账期间的时候就要加上年和期间的检查*/
						if (curYear.equals(accYear) && periodNum.equals(accMonth)) {
							findAccMonth = true;
							vRow.addElement(m_sAccount);
						} else {
							vRow.addElement(m_sAccount);
						}
					} else {
						vRow.addElement(m_sDisAccount);
					}
					vRow.addElement(new Boolean(false));
					vResult.addElement(vRow);
				}
			}
			setYearOfFDM(year); /*第一个未结账月所在年度*/
			setCopeOfFDM(month); /*第一个未结账月所在期间*/
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);
		}
		setData(vResult);
	}
	/**
	 * 初始化类。
	 */
	/* 警告：此方法将重新生成。 */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("ReckoningBegPanel");
			setLayout(new java.awt.BorderLayout());
			setSize(569, 366);
			add(getUIPanel4(), "South");
			add(getUIPanel1(), "Center");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		initData();
		initTable();
		initConnection();
		// user code end
	}
	/**
	 * 主要功能：开始结账期间展现界面
	 * 主要算法：
	 * 异常描述：
	 */
	private void initTable() {
	
		NCTableModel model = new NCTableModel() {
	
			private static final long serialVersionUID = 4180814489963148999L;
	
			public boolean isCellEditable(int row, int col) {
				Class cls = getColumnClass(col);
				return cls == Boolean.class;
			}
		};
		Vector vTitle = TermEndTransactUI.converToVector(m_sTitle);
		model.setDataVector(getData(), vTitle);
		getPeriodTable().getTable().setModel(model);
		//getPeriodTable().getTable().setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
		getPeriodTable().getTable().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		javax.swing.table.TableColumn col1 = getPeriodTable().getTable().getColumn(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UC000-0000239")/*@res "会计月份"*/);
		javax.swing.table.TableColumn col2 = getPeriodTable().getTable().getColumn(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UC000-0000240")/*@res "会计期间"*/);
		javax.swing.table.TableColumn col3 = getPeriodTable().getTable().getColumn(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000053")/*@res "期间状态"*/);
		javax.swing.table.TableColumn col4 = getPeriodTable().getTable().getColumn(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UC000-0004045")/*@res "选择标志"*/);
		col1.setMinWidth(70);
		col1.setMaxWidth(70);
		col2.setMinWidth(280);
		col2.setMaxWidth(280);
		col3.setMinWidth(70);
		col3.setMaxWidth(70);
		col4.setMinWidth(70);
		col4.setMaxWidth(70);
	
	}
	
	/**
	 * 改变结账期间界面选中期间的结账状态。
	 * 创建日期：(2001-11-30 17:20:10)
	 * @param enableInfo2 
	 * @param accountInfo 
	 */
	public void onChangeStateForOper(String[] accountInfo, String[] enableInfo, String sign) {
		try
		{
			SystemInfoVO sysInfo =  getTransactUI().getSysInfo();
			NCTableModel model = (NCTableModel) getPeriodTable().getTable().getModel();
			String enableYear = enableInfo[0].substring(0, 4);
			String enableMonth = enableInfo[0].substring(5, 7);
	
			
			FirstNotClosedAccountMonthVO vo = AccountInfo.getFirstNotClosedAccountMonth(new String[]{enableYear,enableMonth},accountInfo,sysInfo);
			String year = vo.getNotAccYear();
			String month = vo.getNotAccMonth();
			if (month == null) {
				String pkCorp = sysInfo.getCurDwbm();
				String prodID = sysInfo.getProdID();
				
				if(enableInfo==null)
					enableInfo = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).queryEnabledPeriod(pkCorp, prodID);
				
				String enabledYear = enableInfo[0].substring(0, 4); /*启用年*/
				String enabledMonth = enableInfo[0].substring(5, 7); /*启用月*/
				year = enabledYear;
				month = enabledMonth;
			}
			if (sign.equals("Reckoning")) {
				model.setValueAt(m_sAccount, m_selRow, 2);
				model.setValueAt(new Boolean(false), m_selRow, 3);
			} else {
				model.setValueAt(m_sDisAccount, m_selRow, 2);
				model.setValueAt(new Boolean(false), m_selRow, 3);
			}
			setYearOfFDM(year); /*第一个未结账月所在年度*/
			setCopeOfFDM(month); /*第一个未结账月所在期间*/
		}
	  catch(Exception e)
	  {
		  ExceptionHandler.consume(e);
	  }
		
	
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-30 11:37:13)
	 * @param cope java.lang.String
	 */
	private void setCopeOfFDM(String cope) {
	    m_sCopeOfFirDisAccMon = cope;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-17 13:58:23)
	 * @param vdata java.util.Vector
	 */
	public void setData(Vector<Vector<Object>> vdata) {
	    m_vData = vdata;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-30 11:37:56)
	 * @param year java.lang.String
	 */
	private void setYearOfFDM(String year) {
	    m_sYearOfFirDisAccMon = year;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-17 16:23:03)
	 * @param e javax.swing.event.TableModelEvent
	 */
	public void tableChanged(javax.swing.event.TableModelEvent e) {
	
	    int row = e.getFirstRow();
	    int col = e.getColumn();
	    getPeriodTable().getTable().getModel().removeTableModelListener(this);
	    NCTableModel model = (NCTableModel) getPeriodTable().getTable().getModel();
	    String value = model.getValueAt(row, col).toString();
	    if (col == 3) {
	        if (new Boolean(value).booleanValue()) {
	            m_selRow = row;
	            for (int i = 0; i < model.getRowCount(); i++) {
	                Boolean isSelRow = new Boolean(i == row);
	                model.setValueAt(isSelRow, i, 3);
	            }
	        }
	        if (!new Boolean(value).booleanValue()) {
	            m_selRow = -1;
	        }
	    }
	    getPeriodTable().getTable().getModel().addTableModelListener(this);
	}
	public TermEndTransactUI getTransactUI() {
		return transactUI;
	}
	public void setTransactUI(TermEndTransactUI transactUI) {
		this.transactUI = transactUI;
	}
}