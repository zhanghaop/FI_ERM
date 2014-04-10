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
	private String[] m_sTitle = {nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UC000-0000239")/*@res "����·�"*/, nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UC000-0000240")/*@res "����ڼ�"*/, nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000053")/*@res "�ڼ�״̬"*/, nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UC000-0004045")/*@res "ѡ���־"*/};
	private String m_sAccount = nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000054")/*@res "�ѽ���"*/;
	private String m_sDisAccount = nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000055")/*@res "δ����"*/;
	private String m_sDisEnabled = nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000056")/*@res "δ����"*/;
	private Vector<Vector<Object>> m_vData = new Vector<Vector<Object>>(); /*��ʾ����*/
	private int m_selRow = -1;
	private String m_sYearOfFirDisAccMon = null;/*��һ��δ���������ڵ����*/
	private String m_sCopeOfFirDisAccMon = null;/*��һ��δ���������ڵ��ڼ�*/
	private String nodeCode = null;
	
	public String getNodeCode() {
			return nodeCode;
		}
		public void setNodeCode(String nodeCode) {
			this.nodeCode = nodeCode;
		}
	/**
	 * ReckoningBegPanel ������ע�⡣
	 */
	public ReckoningBegPanel() {
		super();
		initialize();
	}
	/**
	 * ReckoningBegPanel ������ע�⡣
	 * @param p0 java.awt.LayoutManager
	 */
	public ReckoningBegPanel(java.awt.LayoutManager p0) {
		super(p0);
	}
	/**
	 * ReckoningBegPanel ������ע�⡣
	 * @param p0 java.awt.LayoutManager
	 * @param p1 boolean
	 */
	public ReckoningBegPanel(java.awt.LayoutManager p0, boolean p1) {
		super(p0, p1);
	}
	/**
	 * ReckoningBegPanel ������ע�⡣
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
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-11-30 11:35:02)
	 * @return java.lang.String
	 */
	public String getCopeOfFDM() {
		return m_sCopeOfFirDisAccMon;
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-11-17 13:58:48)
	 * @return java.util.Vector
	 */
	public Vector<Vector<Object>> getData() {
		return m_vData;
	}
	/**
	 * ���� getPeriodTable ����ֵ��
	 * @return nc.ui.pub.beans.UITablePane
	 */
	/* ���棺�˷������������ɡ� */
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
	 * ��Ҫ���ܣ�ȡ���û�ѡ�еĽ����µ���Ϣ
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-8-17 18:03:21)
	 * ����޸����ڣ�(2001-8-17 18:03:21)
	 * @author��wyan
	 * @return java.lang.String[]
	 */
	public FilterCondVO getSelectedData() throws DataValidateException{
	
		if(m_selRow == -1){
			throw new DataValidateException(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000057")/*@res "������ѡ��һ���ڼ������ĩ����"*/);
		}
	
		FilterCondVO voCond = new FilterCondVO();
		NCTableModel model = (NCTableModel) getPeriodTable().getTable().getModel();
		String period = model.getValueAt(m_selRow, 0).toString();
		String cope = model.getValueAt(m_selRow, 1).toString();
		String state = model.getValueAt(m_selRow, 2).toString().trim();
		if(state.equals(m_sDisEnabled)){
			throw new DataValidateException(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000058")/*@res "���ܶ�δ���õĻ���ڼ���в�����"*/);
		}
	
		int index = period.indexOf(".");
		String ReckoningYear = period.substring(0, index); /*ѡ���ڼ�*/
		String ReckoningQj = period.substring(index + 1); /*ѡ���ڼ��������*/
		index = cope.indexOf("-----");
		String BegDate = cope.substring(0, index); /*ѡ���ڼ���ʼ����*/
		String EndDate = cope.substring(index + 5); /*ѡ���ڼ��������*/
	
		voCond.setQj(ReckoningQj);
		voCond.setYear(ReckoningYear);
		voCond.setBegDate(BegDate);
		voCond.setEndDate(EndDate);
		if (state.equals(m_sAccount)) {
			state = "Y";
		} else {
			state = "N";
		}
		voCond.setPeriodSate(state); /*�����Ƿ���˻���δ����*/
	
		return voCond;
	}
	/**
	 * ��Ҫ���ܣ���ʱ����
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-8-21 10:20:32)
	 * ����޸����ڣ�(2001-8-21 10:20:32)
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getTransactState() {
		return null;
	}
	/**
	 * ���� UILabel1 ����ֵ��
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UILabel getUILabel1() {
		if (ivjUILabel1 == null) {
			try {
				ivjUILabel1 = new nc.ui.pub.beans.UILabel();
				ivjUILabel1.setName("UILabel1");
				ivjUILabel1.setText("");
				ivjUILabel1.setMaximumSize(new java.awt.Dimension(200, 200));
				ivjUILabel1.setForeground(java.awt.Color.black);
				ivjUILabel1.setILabelType(0/** JavaĬ��(�Զ���)*/);
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
	 * ���� UILabel11 ����ֵ��
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UILabel getUILabel11() {
		if (ivjUILabel11 == null) {
			try {
				ivjUILabel11 = new nc.ui.pub.beans.UILabel();
				ivjUILabel11.setName("UILabel11");
				ivjUILabel11.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000059")/*@res "ע:"*/);
				ivjUILabel11.setBounds(6, 0, 30, 21);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel11;
	}
	/**
	 * ���� UILabel12 ����ֵ��
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private UITextArea getUILabel12() {
		if (ivjUILabel12 == null) {
			try {
				ivjUILabel12 = new UITextArea();
				ivjUILabel12.setName("UILabel12");
				ivjUILabel12.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000060")/*@res "��ĩ���˺�,���ܽ����κδ���!,"*/);
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
	 * ���� UIPanel1 ����ֵ��
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* ���棺�˷������������ɡ� */
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
	 * ���� UIPanel3 ����ֵ��
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* ���棺�˷������������ɡ� */
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
	 * ���� UIPanel4 ����ֵ��
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* ���棺�˷������������ɡ� */
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
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-11-30 11:35:02)
	 * @return java.lang.String
	 */
	public String getYearOfFDM() {
		return m_sYearOfFirDisAccMon;
	}
	/**
	 * ÿ�������׳��쳣ʱ������
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable e) {
	
		ExceptionHandler.consume(e);
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-11-17 16:23:36)
	 */
	private void initConnection() {
	    getPeriodTable().getTable().getModel().addTableModelListener(this);
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-11-16 13:53:00)
	 */
	private void initData() {
	
		Vector<Vector<Object>> vResult = new Vector<Vector<Object>>();
		boolean findAccMonth = false; /*�ҵ�������*/
		boolean findEnabledMonth = false; /*�ҵ�������*/
		try {
			SystemInfoVO sysInfo = getTransactUI().getSysInfo();
			String curYear = sysInfo.getCurNd();
			String pk_org = sysInfo.getPk_org();
			String pk_group = sysInfo.getCurDwbm();
			String prodID = sysInfo.getProdID();
			//���ݼ����������롢ģ������ѯ�����ڼ�
			String[] enableInfo = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).queryEnabledPeriod(pk_group, prodID);
	
			if(enableInfo == null || pk_org == null) {
				return ;
			}
			String enabledYear = enableInfo[0].substring(0, 4); /*������*/
			String enabledMonth = enableInfo[0].substring(5, 7); /*������*/
	
			//FIXME �������õ���57��uap�Ľӿڡ�uap���ܻ���µĽӿڣ�Ҳ������Ҫ�Լ�д�ýӿ�
	//		String[] accountInfo = FipubTools.querySettledPeriod("0001","FI");
			String[] accountInfo = NCLocator.getInstance().lookup(ICloseAccountService.class).getCloseAccountInfo(getNodeCode(), pk_org);
			String accYear = accountInfo[0]; /*��ǰ������*/
			String accMonth = accountInfo[1]; /*��ǰ������*/
			/*��һ��δ������*/
			FirstNotClosedAccountMonthVO vo = AccountInfo.getFirstNotClosedAccountMonth(new String[]{enabledYear,enabledMonth},new String[]{accYear,accMonth},sysInfo);
			String year = vo.getNotAccYear();
			String month = vo.getNotAccMonth();
	//		findAccMonth = vo.getIsAccounted();
	
	
			/*�û���½������������(Ҫ����δ������)*/
			if (enabledYear.equals(curYear)) {
				AccountCalendar ac = AccountCalendar.getInstance();
				ac.set(curYear);
	
				AccperiodmonthVO[] months = ac.getMonthVOsOfCurrentYear();
				
				//��������ͽ����¾�Ϊ�գ�˵��δ���� ������ findAccMonth = true�������Ժ��״̬��Ϊδ���� 
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
			/*�û���½�겻��������(���ñ���δ���õ���)*/
			else {
				/*����û���½�����Ѿ����˵����һ���ڼ�������ȵĺ���*/
				/*���Ƿ��ҵ����������ó�Ϊ���Ա�֤�û���ǰ��½����ڼ�ȫ����ʾ��δ����*/
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
						/*����û���½�겻���Ѿ����˵����һ���ڼ��������*/
						/*�ж��û���½����ڼ��Ƿ��ǽ����ڼ��ʱ���Ҫ��������ڼ�ļ��*/
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
			setYearOfFDM(year); /*��һ��δ�������������*/
			setCopeOfFDM(month); /*��һ��δ�����������ڼ�*/
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);
		}
		setData(vResult);
	}
	/**
	 * ��ʼ���ࡣ
	 */
	/* ���棺�˷������������ɡ� */
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
	 * ��Ҫ���ܣ���ʼ�����ڼ�չ�ֽ���
	 * ��Ҫ�㷨��
	 * �쳣������
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
		javax.swing.table.TableColumn col1 = getPeriodTable().getTable().getColumn(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UC000-0000239")/*@res "����·�"*/);
		javax.swing.table.TableColumn col2 = getPeriodTable().getTable().getColumn(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UC000-0000240")/*@res "����ڼ�"*/);
		javax.swing.table.TableColumn col3 = getPeriodTable().getTable().getColumn(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000053")/*@res "�ڼ�״̬"*/);
		javax.swing.table.TableColumn col4 = getPeriodTable().getTable().getColumn(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UC000-0004045")/*@res "ѡ���־"*/);
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
	 * �ı�����ڼ����ѡ���ڼ�Ľ���״̬��
	 * �������ڣ�(2001-11-30 17:20:10)
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
				
				String enabledYear = enableInfo[0].substring(0, 4); /*������*/
				String enabledMonth = enableInfo[0].substring(5, 7); /*������*/
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
			setYearOfFDM(year); /*��һ��δ�������������*/
			setCopeOfFDM(month); /*��һ��δ�����������ڼ�*/
		}
	  catch(Exception e)
	  {
		  ExceptionHandler.consume(e);
	  }
		
	
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-11-30 11:37:13)
	 * @param cope java.lang.String
	 */
	private void setCopeOfFDM(String cope) {
	    m_sCopeOfFirDisAccMon = cope;
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-11-17 13:58:23)
	 * @param vdata java.util.Vector
	 */
	public void setData(Vector<Vector<Object>> vdata) {
	    m_vData = vdata;
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-11-30 11:37:56)
	 * @param year java.lang.String
	 */
	private void setYearOfFDM(String year) {
	    m_sYearOfFirDisAccMon = year;
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-11-17 16:23:03)
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