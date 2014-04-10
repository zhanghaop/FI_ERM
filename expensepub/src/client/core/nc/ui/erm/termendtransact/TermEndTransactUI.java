package nc.ui.erm.termendtransact;

import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Logger;
import nc.itf.arap.initbill.IInitBillCloseService;
import nc.itf.erm.service.IErmEJBService;
import nc.itf.erm.termendtransact.ICloseAccountService;
import nc.itf.erm.termendtransact.ITermEndPrivate;
import nc.itf.fi.pub.Currency;
import nc.itf.uap.sf.ICreateCorpQueryService;
import nc.pubitf.accperiod.AccountCalendar;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.util.ErmBtnRes;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ToftPanel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.para.SysInitBO_Client;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.ArapCommonTool;
import nc.vo.erm.service.ServiceVO;
import nc.vo.erm.termendtransact.AccountInfo;
import nc.vo.erm.termendtransact.AgiotageBzVO;
import nc.vo.erm.termendtransact.AgiotageVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.erm.termendtransact.FilterCondVO;
import nc.vo.erm.termendtransact.FirstNotClosedAccountMonthVO;
import nc.vo.erm.termendtransact.RemoteTransferVO;
import nc.vo.erm.termendtransact.ReportVO;
import nc.vo.erm.termendtransact.SystemInfoVO;
import nc.vo.erm.termendtransact.TermEndMsg;
import nc.vo.erm.termendtransact.TermEndVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
public class TermEndTransactUI extends ToftPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = -3038845740036357554L;
	private ButtonObject m_BnLast = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPT200604-000004")/*@res "上一步"*/, "", 2,"上一步");	/*-=notranslate=-*/
	private ButtonObject m_BnNext = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPT200604-000001")/*@res "下一步"*/, "", 2,"下一步");	/*-=notranslate=-*/
	private ButtonObject m_BnReckoning = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPT200604-000002")/*@res "完成结账"*/, "", 2,"完成结账");	/*-=notranslate=-*/
	private ButtonObject m_BnCancelReckoning = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPT200604-000003")/*@res "取消结账"*/, "", 2,"取消结账");	/*-=notranslate=-*/
	private ButtonObject m_BnRefersh = new ButtonObject(nc.ui.ml.NCLangRes.getInstance().getStrByID("common","uarappub-v35-000380")/*@res "刷新"*/, "", 2,"刷新");	/*-=notranslate=-*/
	private ButtonObject[] m_BnGroups =
		{m_BnLast, m_BnNext, m_BnReckoning, m_BnCancelReckoning,m_BnRefersh};
	private String m_sCurState = ""; /*当前所在界面*/
	private java.awt.CardLayout m_Cardlayout = new java.awt.CardLayout();
	private ReckoningBegPanel m_ReckoningBegp = null; /*结账开始*/
	private ReckoningCheckPanel m_ReckoningCheckp = null; /*结账检查*/
	private ReckoningReportPanel m_ReckoningReportp = null; /*结账报告*/
	private nc.ui.pub.beans.UIPanel ivjUIPanel0 = null;
	private nc.ui.pub.beans.UIPanel ivjUIPanel1 = null;
	private nc.ui.pub.beans.UIPanel ivjUIPanel2 = null;
	private nc.ui.pub.beans.UIRefPane ivjOrgPanel = null;
	private nc.ui.pub.beans.UILabel ivjBeginLabel = null;
	private nc.ui.pub.beans.UILabel ivjCheckLabel = null;
	private nc.ui.pub.beans.UILabel ivjFinishLabel = null;
	private nc.ui.pub.beans.UILabel ivjReportLabel = null;
	private nc.ui.pub.beans.UILabel ivjUILabel1 = null;
	private nc.ui.pub.beans.UILabel ivjUILabel2 = null;
	private nc.ui.pub.beans.UILabel ivjUILabel3 = null;
	private nc.ui.pub.beans.UILabel ivjUILabel4 = null;
	private nc.ui.pub.beans.UILabel ivjUILabel5 = null;
	private nc.ui.pub.beans.UIPanel ivjUIPanel6 = null;
	private nc.ui.pub.beans.UIPanel ivjTablePanel = null;
	private nc.ui.pub.beans.UILabel ivjOrgLabel = null;
	private  SystemInfoVO m_voInfo = new SystemInfoVO();/*保存系统信息VO*/
	private AgiotageVO m_voCurrency = new AgiotageVO();/*汇兑损益检查的币种信息*/
	private String m_sYearOfFirDisAccMon = null;/*第一个未结账月所在的年度*/
	private String m_sCopeOfFirDisAccMon = null;/*第一个未结账月所在的期间*/
	private String nodeCode = null;		/*节点编码*/

	public String getNodeCode() {
		return nodeCode;
	}
	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}
		/**
	 * TermEndTransactUI 构造子注解。
	 */
	public TermEndTransactUI() {
		super();
		initialize();
	}
	/**
	 * 对象数组转化成VECTOR。
	 * 创建日期：(2001-5-24 13:11:46)
	 * @author：wyan
	 * @return java.util.Vector
	 * @param sArray java.lang.Object[]
	 */
	public static Vector<Object> converToVector(Object[] objArray) {
		if (objArray == null)
			return null;
		Vector<Object> v = new Vector<Object>(objArray.length);
		for (int i = 0; i < objArray.length; i++) {
			v.addElement(objArray[i]);
		}
		return v;
	}

	/**
	 *
	 * @return
	 */
	private UIRefPane getOrgPanel() {
		if (ivjOrgPanel == null) {

			ivjOrgPanel = new UIRefPane();
			ivjOrgPanel.setName("pkorg");
			ivjOrgPanel.setRefNodeName("财务组织");/*-=nottranslet=-*/
			ivjOrgPanel.setBounds(new Rectangle(100,20));
			ivjOrgPanel.addValueChangedListener(new ValueChangedListener() {
				public void valueChanged(ValueChangedEvent e) {
	//
					m_ReckoningBegp = null;
					initialize();
				}
			});
		}
		return ivjOrgPanel;
	}

	/**
	 * 返回ivjOrgLabel的特征值
	 * @return nc.ui.pub.beans.UILabel
	 */
	private UILabel getOrgLabel() {
		if (ivjOrgLabel == null) {
			ivjOrgLabel = new UILabel();
			ivjOrgLabel.setName("pkorg");
			ivjOrgLabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000006")/*@res "财务组织"*/);
			ivjOrgLabel.setBounds(355, 256, 101, 24);
			ivjOrgLabel.setForeground(java.awt.Color.black);
		}
		return ivjOrgLabel;
	}

	/**
	 * 返回 UILabel11 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getBeginLabel() {
		if (ivjBeginLabel == null) {
			try {
				ivjBeginLabel = new nc.ui.pub.beans.UILabel();
				ivjBeginLabel.setName("BeginLabel");
				ivjBeginLabel.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000070")/*@res "1.开始结账"*/);
				ivjBeginLabel.setForeground(java.awt.Color.black);
				ivjBeginLabel.setILabelType(0/** Java默认(自定义)*/);
				ivjBeginLabel.setFont(new java.awt.Font("dialog", 0, 14));
				ivjBeginLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
				// user code begin {1}
				ivjBeginLabel.setBounds(0, 41, 199, 24);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjBeginLabel;
	}
	/**
	 * 返回 UILabel12 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getCheckLabel() {
		if (ivjCheckLabel == null) {
			try {
				ivjCheckLabel = new nc.ui.pub.beans.UILabel();
				ivjCheckLabel.setName("CheckLabel");
				ivjCheckLabel.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000071")/*@res "2.月末检查"*/);
				ivjCheckLabel.setForeground(java.awt.Color.black);
				ivjCheckLabel.setILabelType(0/** Java默认(自定义)*/);
				ivjCheckLabel.setFont(new java.awt.Font("dialog", 0, 14));
				ivjCheckLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
				// user code begin {1}
				ivjCheckLabel.setBounds(0, 65, 199, 22);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCheckLabel;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-30 11:35:02)
	 * @return java.lang.String
	 */
	private String getCopeOfSel() {
		return m_sCopeOfFirDisAccMon;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-19 13:45:49)
	 * @return nc.vo.arap.agiotage.AgiotageVO
	 */
	public AgiotageVO getCurrencyInfo() {
		return m_voCurrency;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-16 11:40:25)
	 * 最后修改日期：(2001-8-16 11:40:25)
	 * @author：wyan
	 * @return java.lang.String
	 */
	private String getCurState() {
		return m_sCurState;
	}
	/**
	 * 返回 UILabel13 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getFinishLabel() {
		if (ivjFinishLabel == null) {
			try {
				ivjFinishLabel = new nc.ui.pub.beans.UILabel();
				ivjFinishLabel.setName("FinishLabel");
				ivjFinishLabel.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000072")/*@res "4.完成结账"*/);
				ivjFinishLabel.setForeground(java.awt.Color.black);
				ivjFinishLabel.setILabelType(0/** Java默认(自定义)*/);
				ivjFinishLabel.setFont(new java.awt.Font("dialog", 0, 14));
				ivjFinishLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
				ivjFinishLabel.setRequestFocusEnabled(true);
				// user code begin {1}
				ivjFinishLabel.setBounds(0, 109, 200, 22);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjFinishLabel;
	}
	/**
	 * 返回 UILabel1 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getReportLabel() {
		if (ivjReportLabel == null) {
			try {
				ivjReportLabel = new nc.ui.pub.beans.UILabel();
				ivjReportLabel.setName("ReportLabel");
				ivjReportLabel.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000073")/*@res "3.月度工作报告"*/);
				ivjReportLabel.setForeground(java.awt.Color.black);
				ivjReportLabel.setILabelType(0/** Java默认(自定义)*/);
				ivjReportLabel.setFont(new java.awt.Font("dialog", 0, 14));
				ivjReportLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
				// user code begin {1}
				ivjReportLabel.setBounds(0, 87, 199, 22);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjReportLabel;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-16 10:58:02)
	 * 最后修改日期：(2001-8-16 10:58:02)
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getSfbz() {
		return null;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-4 15:54:10)
	 * 最后修改日期：(2001-9-4 15:54:10)
	 * @author：wyan
	 * @return nc.pub.arap.transaction.SystemInfoVO
	 */
	public SystemInfoVO getSysInfo() {
		return m_voInfo;
	}
	/**
	 * 返回 UIPanel3 特性值。
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIPanel getTablePanel() {
		if (ivjTablePanel == null) {
			try {
				ivjTablePanel = new nc.ui.pub.beans.UIPanel();
				ivjTablePanel.setName("TablePanel");
				//ivjTablePanel.setLayout(new java.awt.CardLayout());
				// user code begin {1}
				ivjTablePanel.setLayout(m_Cardlayout);
				//ivjTablePanel.add(getUIScrollPane(), getUIScrollPane().getName());
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTablePanel;
	}
	/**
	 * 子类实现该方法，返回业务界面的标题。
	 * @version (00-6-6 13:33:25)
	 *
	 * @return java.lang.String
	 */
	public String getTitle() {
		return nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000074")/*@res "月末处理"*/;
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
				// user code begin {1}
				ivjUILabel1.setBounds(0, 0, 200, 41);
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
	 * 返回 UILabel2 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabel2() {
		if (ivjUILabel2 == null) {
			try {
				ivjUILabel2 = new nc.ui.pub.beans.UILabel();
				ivjUILabel2.setName("UILabel2");
				ivjUILabel2.setText("");
				ivjUILabel2.setMaximumSize(new java.awt.Dimension(52, 200));
				ivjUILabel2.setForeground(java.awt.Color.black);
				ivjUILabel2.setILabelType(0/** Java默认(自定义)*/);
				ivjUILabel2.setPreferredSize(new java.awt.Dimension(52, 60));
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabel2;
	}
	/**
	 * 返回 UILabel3 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabel3() {
		if (ivjUILabel3 == null) {
			try {
				ivjUILabel3 = new nc.ui.pub.beans.UILabel();
				ivjUILabel3.setName("UILabel3");
				ivjUILabel3.setText("");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabel3;
	}
	/**
	 * 返回 UILabel4 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabel4() {
		if (ivjUILabel4 == null) {
			try {
				ivjUILabel4 = new nc.ui.pub.beans.UILabel();
				ivjUILabel4.setName("UILabel4");
				ivjUILabel4.setPreferredSize(new java.awt.Dimension(32, 22));
				ivjUILabel4.setText("");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabel4;
	}
	/**
	 * 返回 UILabel5 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabel5() {
		if (ivjUILabel5 == null) {
			try {
				ivjUILabel5 = new nc.ui.pub.beans.UILabel();
				ivjUILabel5.setName("UILabel5");
				ivjUILabel5.setPreferredSize(new java.awt.Dimension(12, 22));
				ivjUILabel5.setText("");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUILabel5;
	}

	private nc.ui.pub.beans.UIPanel getUIPanel0() {
		if (ivjUIPanel0 == null) {
			try {
				ivjUIPanel0 = new nc.ui.pub.beans.UIPanel();
				ivjUIPanel0.setName("UIPanel0");
	//			ivjUIPanel0.setPreferredSize(new java.awt.Dimension(130, 30));
	//			ivjUIPanel0.setLayout(new FlowLayout());
	//			ivjUIPanel0.setLayout(new java.awt.BorderLayout());
				UIPanel ad = new UIPanel();
				ad.setLayout(new FlowLayout());
				ad.add(getOrgLabel(),FlowLayout.LEFT);
				ad.add(getOrgPanel());
				ivjUIPanel0.add(ad);
	//			ivjUIPanel0.add(getOrgLabel(), FlowLayout.LEFT);
	//			ivjUIPanel0.add(getOrgPanel());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIPanel0;
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
				ivjUIPanel1.setPreferredSize(new java.awt.Dimension(130, 10));
				ivjUIPanel1.setLayout(new java.awt.BorderLayout());
				getUIPanel1().add(getUIPanel6(), "Center");
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
	 * 返回 UIPanel2 特性值。
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIPanel getUIPanel2() {
		if (ivjUIPanel2 == null) {

				ivjUIPanel2 = new nc.ui.pub.beans.UIPanel();
				ivjUIPanel2.setName("UIPanel2");
				ivjUIPanel2.setLayout(new java.awt.BorderLayout());
				ivjUIPanel2.setPreferredSize(new java.awt.Dimension(644, 419));
				getUIPanel2().add(getUILabel2(), "North");
				getUIPanel2().add(getUILabel3(), "South");
				getUIPanel2().add(getUILabel4(), "East");
				getUIPanel2().add(getUILabel5(), "West");

				getUIPanel2().add(this.getTablePanel(), "Center");
		}
		return ivjUIPanel2;
	}

	/**
	 * 返回 UIPanel6 特性值。
	 * @return nc.ui.pub.beans.UIPanel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIPanel getUIPanel6() {
		if (ivjUIPanel6 == null) {
			try {
				ivjUIPanel6 = new nc.ui.pub.beans.UIPanel();
				ivjUIPanel6.setName("UIPanel6");
	//			ivjUIPanel6.setBorder(new javax.swing.border.CompoundBorder());
				ivjUIPanel6.setLayout(null);
				ivjUIPanel6.add(getUILabel1(), null);
				ivjUIPanel6.add(getBeginLabel(), null);
				ivjUIPanel6.add(getCheckLabel(), null);
				ivjUIPanel6.add(getReportLabel(), null);
				ivjUIPanel6.add(getFinishLabel(), null);
	//			ivjUIPanel6.setBorder(
	//				javax.swing.BorderFactory.createEtchedBorder(getBackground().brighter(), getBackground().darker()));

				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIPanel6;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-30 11:35:02)
	 * @return java.lang.String
	 */
	private String getYearOfSel() {
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
	 * 初始化类。
	 */
	/* 警告：此方法将重新生成。 */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("TermEndTransactUI");
			setLayout(new java.awt.BorderLayout());
			setSize(774, 419);
			add(getUIPanel0(),"North");
			add(getUIPanel2(), "Center");
			add(getUIPanel1(), "West");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}

		// user code begin {2}
		setButtons(m_BnGroups);
		initSysInfo();
		onReckoningBeg();
		// user code end

		if(m_ReckoningBegp == null) {
			m_ReckoningBegp = new ReckoningBegPanel(this);
		}
		m_ReckoningBegp.setNodeCode(getNodeCode());
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-20 16:01:24)
	 * 最后修改日期：(2001-9-20 16:01:24)
	 * @author：wyan
	 */
	private void initSysInfo() {

		try {
	//		String Sfbz = getSfbz(); //收付标志
	//		String sNum = "AR5";
	//		String sNum1 = "AR7";
	//		String sNum2 = "AR8";
	//		String sNum3 = "AR9";
	//		String sNum4 = "AR10";
	////		String sNum6 = "BD302";
	//		String prodID = "AR";
	//		if (Sfbz.equals("Yf")) {
	//			sNum = "AP3";
	//			sNum1 = "AP5";
	//			sNum2 = "AP6";
	//			sNum3 = "AP7";
	//			sNum4 = "AP8";
	//			prodID = "AP";
	//		}
			String sNum1 = BXParamConstant.PARAM_IS_EFFECT_BILL;
			String sNum3 = BXParamConstant.PARAM_GENERATE_VOUCHER;
			String prodID = BXConstans.ERM_PRODUCT_CODE_Lower;
			/**
			 * 报销管理部分目前只考虑两个参数，截止到本月单据是否全部生效            ER9
			 *                             截止到本月单据全部生成会计凭证     ER10
			 *
			 *
			 * 如果有其他需要 增加对应参数
			 * */

			String pk_org = this.getOrgPanel().getRefModel().getPkValue();/*选中的业务单元*/
			String pk_group = BXUiUtil.getPK_group();/*当前集团*/
			String CurRq = BXUiUtil.getBusiDate().toString(); /*当前日期*/
			AccountCalendar ac = AccountCalendar.getInstance();
			ac.setDate(new UFDate(CurRq));
			AccperiodmonthVO month = ac.getMonthVO();
			String QjEnd = month.getEnddate().toString();/*期间的最后时间*/
			String CurNd = ac.getYearVO().getPeriodyear();/*年度*/
			String CurQj = month.getYearmth();/*期间*/
			String CurUser = BXUiUtil.getPk_user();/*当前用户*/

			String mode = null;
			String mode1 = null;
			String mode2 = null;
			String mode3 = null;
			String mode4 = null;

	//		if(pk_org != null) {
	//			/*损益方式：外币，月末*/
	//			mode = SysInitBO_Client.getParaString(pk_org, sNum);
	//			/*截止到本月单据全部审核*/
	//			mode1 = SysInitBO_Client.getParaString(pk_org, sNum1);
	//			/*截止到本月收款单全部核销*/
	//			mode2 = SysInitBO_Client.getParaString(pk_org, sNum2);
	//			/*截止到本月单据全部生成会计凭证*/
	//			mode3 = SysInitBO_Client.getParaString(pk_org, sNum3);
	//			/*本月汇兑损益是否计算*/
	//			mode4 = SysInitBO_Client.getParaString(pk_org, sNum4);
	//		}
			if(pk_org != null){
				 /* (1)截止到本月单据全部审核 */
				 mode1 = SysInitBO_Client.getParaString(pk_org, sNum1); //
				 /* (1)截止到本月单据全部生成会计凭证 */
				 mode3 = SysInitBO_Client.getParaString(pk_org, sNum3); //
			}


			boolean isMonthEnd = false;
			if ("月末计算".equals(mode))	/*-=notranslate=-*/
				isMonthEnd = true;

			/*****MODE1********/
			if ("不检查".equals(mode1))	/*-=notranslate=-*/
				mode1 = null;
			else
				if ("检查但不控制".equals(mode1))	/*-=notranslate=-*/
					mode1 = "check";
				else
					if ("检查并且控制".equals(mode1))	/*-=notranslate=-*/
						mode1 = "control";
			/*****MODE2********/
			if ("不检查".equals(mode2))	/*-=notranslate=-*/
				mode2 = null;
			else
				if ("检查但不控制".equals(mode2))	/*-=notranslate=-*/
					mode2 = "check";
				else
					if ("检查并且控制".equals(mode2))	/*-=notranslate=-*/
						mode2 = "control";
			/*****MODE3********/
			if ("不检查".equals(mode3))	/*-=notranslate=-*/
				mode3 = null;
			else
				if ("检查但不控制".equals(mode3))	/*-=notranslate=-*/
					mode3 = "check";
				else
					if ("检查并且控制".equals(mode3))	/*-=notranslate=-*/
						mode3 = "control";
			/*****MODE5********/
			/*如果汇兑损益方式不是月末结算则不检查*/
			if (isMonthEnd) {
				if ("不检查".equals(mode4))	/*-=notranslate=-*/
					mode4 = null;

				else
					if ("检查但不控制".equals(mode4)) {	/*-=notranslate=-*/
						mode4 = "check";
					} else
						if ("检查并且控制".equals(mode4)) {	/*-=notranslate=-*/
							mode4 = "control";
						}
			} else
				mode4 = null;

	//		m_voInfo.setSfbz(Sfbz);
			m_voInfo.setCurDwbm(pk_group);
			m_voInfo.setPk_org(pk_org);
			m_voInfo.setCurNd(CurNd);
			m_voInfo.setCurQj(CurQj);
			m_voInfo.setCurRq(QjEnd);
			m_voInfo.setCurUser(CurUser);
			m_voInfo.setCheckMode1(mode1);
	//		m_voInfo.setCheckMode2(mode2);
			m_voInfo.setCheckMode3(mode3);
	//		m_voInfo.setCheckMode4(mode4);
			m_voInfo.setProdID(prodID);
	//		m_voInfo.setHsMode(isZFB);


		} catch (Exception ex) {
			ExceptionHandler.consume(ex);
		}
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-16 11:07:33)
	 * 最后修改日期：(2001-8-16 11:07:33)
	 * @author：wyan
	 * @param cardname java.lang.String
	 */
	private void onButtonChange(String cardname) {

		if (cardname.equals("Begin")) {
			getBeginLabel().setEnabled(true);
			getCheckLabel().setEnabled(false);
			getReportLabel().setEnabled(false);
			getFinishLabel().setEnabled(false);
			m_BnLast.setEnabled(false);

			if(m_voInfo.getPk_org() == null) {
				m_BnNext.setEnabled(false);
			} else {
				m_BnNext.setEnabled(true);
			}

			m_BnReckoning.setEnabled(false);
			m_BnCancelReckoning.setEnabled(true);
			m_BnRefersh.setEnabled(true);
		}
		if (cardname.equals("Check")) {
			getBeginLabel().setEnabled(false);
			getCheckLabel().setEnabled(true);
			getReportLabel().setEnabled(false);
			getFinishLabel().setEnabled(false);
			m_BnLast.setEnabled(true);
			m_BnNext.setEnabled(true);
			m_BnReckoning.setEnabled(false);
			m_BnCancelReckoning.setEnabled(false);
			m_BnRefersh.setEnabled(false);
		}
		if (cardname.equals("CheckNone")) {
			getBeginLabel().setEnabled(false);
			getCheckLabel().setEnabled(true);
			getReportLabel().setEnabled(false);
			getFinishLabel().setEnabled(false);
			m_BnLast.setEnabled(true);
			m_BnNext.setEnabled(false);
			m_BnReckoning.setEnabled(true);
			m_BnCancelReckoning.setEnabled(false);
			m_BnRefersh.setEnabled(false);
		}
		if (cardname.equals("Report")) {
			getBeginLabel().setEnabled(false);
			getCheckLabel().setEnabled(false);
			getReportLabel().setEnabled(true);
			getFinishLabel().setEnabled(false);
			m_BnLast.setEnabled(true);
			m_BnNext.setEnabled(false);
			m_BnReckoning.setEnabled(true);
			m_BnCancelReckoning.setEnabled(false);
			m_BnRefersh.setEnabled(false);
		}
		if (cardname.equals("Finish")) {
			getBeginLabel().setEnabled(false);
			getCheckLabel().setEnabled(false);
			getReportLabel().setEnabled(false);
			getFinishLabel().setEnabled(true);
			m_BnLast.setEnabled(true);
			m_BnNext.setEnabled(false);
			m_BnReckoning.setEnabled(false);
			m_BnCancelReckoning.setEnabled(false);
			m_BnRefersh.setEnabled(false);
		}
		if (cardname.equals("UnReckoning")) {
			getBeginLabel().setEnabled(false);
			getCheckLabel().setEnabled(false);
			getReportLabel().setEnabled(true);
			getFinishLabel().setEnabled(false);
			m_BnLast.setEnabled(true);
			m_BnNext.setEnabled(false);
			m_BnReckoning.setEnabled(false);
			m_BnCancelReckoning.setEnabled(false);
			m_BnRefersh.setEnabled(false);
		}
		updateButtons();
	}
	/**
	 * 子类实现该方法，响应按钮事件。
	 * @version (00-6-1 10:32:59)
	 *
	 * @param bo ButtonObject
	 */
	public void onButtonClicked(nc.ui.pub.ButtonObject bo) {
		this.showHintMessage(ErmBtnRes.getBtnRes(bo.getName())!=null?nc.ui.ml.NCLangRes.getInstance().getStrByID("2008",ErmBtnRes.getBtnRes(bo.getName())[0]):"");
		if (bo == m_BnNext) {
			if (getCurState().equals("Begin")) {
				onCheckInit();
				return;
			}
			else if (getCurState().equals("Check")) {
				onReckoningCheck();
				return;
			}

		}
		if (bo == m_BnLast) {
			if (getCurState().equals("Report")) {
				onCheckInit();
				return;
			}
			else if (getCurState().equals("Finish")) {
				onReckoningBeg();
				return;
			}
			else if (getCurState().equals("Check")) {
				onReckoningBeg();
				return;
			}

		}
		if (bo == m_BnReckoning) {
			onReckoning();
		}
		if (bo == m_BnCancelReckoning) {
			onCancelReckoning();
		}
		if(bo==m_BnRefersh){
			onRefersh();
		}
	//	// lhwei 2009.05.22 响应"重建余额表"动作
	//	if( bo==m_rebuildingYE ){
	//		if( new RebuildArapBalanceDlg(this,getSfbz()).showModal() == UIDialog.ID_OK ){
	//
	//		}
	//	}
		this.showHintMessage(ErmBtnRes.getBtnRes(bo.getName())!=null?nc.ui.ml.NCLangRes.getInstance().getStrByID("2008",ErmBtnRes.getBtnRes(bo.getName())[1]):"");
	}
	private void onRefersh(){
		initSysInfo();
		onReckoningBeg();
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-16 11:48:35)
	 * 最后修改日期：(2001-8-16 11:48:35)
	 * @author：wyan
	 */


	private void onCancelReckoning() {
		String dwbm = getSysInfo().getCurDwbm();
		String prodId = getSysInfo().getProdID();
		String pk_org = getSysInfo().getPk_org();


		ReportVO voRep = new ReportVO();
		String[] accountInfo = null;
		String[] enableInfo = null;
		try {

			List<String[]> result = callRemoteService(dwbm, prodId);
			if(accountInfo==null){
				accountInfo=result.get(0);
				if(accountInfo==null){
	//			    accountInfo = FipubTools.querySettledPeriod("0001","FI");
					accountInfo = NCLocator.getInstance().lookup(ICloseAccountService.class).getCloseAccountInfo(getNodeCode(), getSysInfo().getPk_org());
				}
			}
			if(enableInfo==null){
				enableInfo=result.get(1);
				if(enableInfo==null){
				    enableInfo = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).queryEnabledPeriod(dwbm, prodId);
				}
			}

			FilterCondVO voCond = m_ReckoningBegp.getSelectedData();
			setYearOfSel(voCond.getYear());
			setCopeOfSel(voCond.getQj());
			voRep = onPeriodValidate("CancelReckoning",accountInfo,enableInfo,getSysInfo(),result.get(2),result.get(3));
			if (voRep.getState()) {
				SystemInfoVO info = new SystemInfoVO();
				info.setCurNd(getYearOfSel());
				info.setCurQj(getCopeOfSel());
				info.setCurDwbm(dwbm);
				info.setProdID(prodId);
				FirstNotClosedAccountMonthVO vo = AccountInfo.getFirstDisAccountMonth(new String[]{enableInfo[0].substring(0, 4),enableInfo[0].substring(5, 7)},info);
				String year = vo.getNotAccYear();
				String cope = vo.getNotAccMonth();
	//			Proxy.getICreateCorpService().updateSettledPeriod(pk_org, "FI", year, cope);
				NCLocator.getInstance().lookup(ICloseAccountService.class).updateCloseAccountInfo(getNodeCode(), getSysInfo().getPk_org(), year, cope);
				getITermEndPrivate().termEndOperation(prodId,new TermEndVO(dwbm,year,cope),true);

				m_ReckoningBegp.onChangeStateForOper(accountInfo,enableInfo,"CancelReckoning");
				showHintMessage(TermEndMsg.getm_sCancelReckoningSuc());
			} else
				showWarningMessage(voRep.getInfo());

		} catch (DataValidateException ex) {
			showWarningMessage(ex.getMessage());
		} catch (Exception ex) {
			showHintMessage(TermEndMsg.getm_sCancelReckoningFail());
			ExceptionHandler.consume(ex);
		}

	}

	private ITermEndPrivate getITermEndPrivate() throws ComponentException {

	    return ((ITermEndPrivate) NCLocator.getInstance().lookup(ITermEndPrivate.class.getName()));

	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-16 11:07:07)
	 * 最后修改日期：(2001-8-16 11:07:07)
	 * @author：wyan
	 * @param cardname java.lang.String
	 */
	private void onCardLayout(String cardname) {

		if (cardname.equals("Begin")) {
			if (m_ReckoningBegp == null)
				//m_ReckoningBegp = null;
			m_ReckoningBegp = new ReckoningBegPanel(this);
			getTablePanel().add(cardname, m_ReckoningBegp);
			m_Cardlayout.show(getTablePanel(), "Begin");
		}
		if (cardname.equals("Check")) {
			if (m_ReckoningCheckp != null)
				m_ReckoningCheckp = null;
			m_ReckoningCheckp = new ReckoningCheckPanel();
			getTablePanel().add(cardname, m_ReckoningCheckp);
			m_Cardlayout.show(getTablePanel(), "Check");
		}
		if (cardname.equals("Report")) {
			if (m_ReckoningReportp != null)
				m_ReckoningReportp = null;
			m_ReckoningReportp = new ReckoningReportPanel();
			getTablePanel().add(cardname, m_ReckoningReportp);
			m_Cardlayout.show(getTablePanel(), "Report");
		}
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-17 17:51:25)
	 */
	private void onCheckHl() {

		AgiotageVO vo = new AgiotageVO();
		Vector<AgiotageBzVO> vBzData = new Vector<AgiotageBzVO>();


		try {
			String dwbm = getSysInfo().getCurDwbm();
			String sfbz = getSysInfo().getSfbz();
			String calQj = getCopeOfSel();
			String calNd = getYearOfSel();

			String localPk = Currency.getLocalCurrPK(dwbm);
//			String fracPk = CurrParamQuery.getInstance().getFracCurrPK(this.getSysInfo().getCurDwbm());

			vo.setDwbm(dwbm);
			vo.setSfbz(sfbz);
			vo.setBzbm(localPk);
			vo.setLocal(localPk);
	//		vo.setFrac(fracPk);
			/*取得除本币之外的全部币种*/
			Hashtable hBz = vo.getAllBz();

			java.util.Enumeration em = hBz.keys();
			while (em.hasMoreElements()) {
				AgiotageBzVO bzvo = new AgiotageBzVO();
				String bzbm = em.nextElement().toString().trim();
				UFDouble[] hls = null;
				try{
					hls=Currency.getAdjustRateBoth(dwbm,bzbm,null, calNd, calQj);
				}catch(Exception e){
					ExceptionHandler.consume(e);
					continue;
				}
				bzvo.setBzbm(bzbm);
	//			bzvo.setFbhl(hls[0]);
				bzvo.setBbhl(hls[1]);

				if(hls[1]==null||ArapCommonTool.isZero(hls[1])){
	//				bzvo.setState(false);
	//			}else if(Currency.isBlnLocalFrac(dwbm) && ArapCommonTool.isZero(hls[0])){
	//				bzvo.setState(false);
				}else{
					bzvo.setState(true);
				}
				vBzData.addElement(bzvo);
			}
			vo.setSelBzbm(vBzData);
			setCurrencyInfo(vo);

		} catch (Exception ex) {
			ExceptionHandler.consume(ex);

		}
	}

	protected List<String[]> callRemoteService(String dwbm,String prodId) throws BusinessException{
		List<ServiceVO> list=new ArrayList<ServiceVO>();

		// 根据财务组织和产品代号查询模块结账期间。
		ServiceVO callvo1 = new ServiceVO();
		callvo1.setClassname("nc.itf.uap.sf.ICreateCorpQueryService");
		callvo1.setMethodname("querySettledPeriod");
		callvo1.setParamtype(new Class[] { String.class, String.class });
		callvo1.setParam(new Object[] {dwbm,prodId});
		callvo1.getCode();
		list.add(callvo1);

		// 根据财务组织和产品代号查询模块启用期间
		ServiceVO callvo2 = new ServiceVO();
		callvo2.setClassname("nc.itf.uap.sf.ICreateCorpQueryService");
		callvo2.setMethodname("queryEnabledPeriod");
		callvo2.setParamtype(new Class[] { String.class, String.class });
		callvo2.setParam(new Object[] {dwbm,prodId});
		callvo2.getCode();
		list.add(callvo2);

		// 根据财务组织和产品代号查询模块结账期间。
		ServiceVO callvo3 = new ServiceVO();
		callvo3.setClassname("nc.itf.uap.sf.ICreateCorpQueryService");
		callvo3.setMethodname("querySettledPeriod");
		callvo3.setParamtype(new Class[] { String.class, String.class });
		callvo3.setParam(new Object[] {dwbm,"JC"});
		callvo3.getCode();
		list.add(callvo3);

		ServiceVO callvo4 = new ServiceVO();
		callvo4.setClassname("nc.itf.uap.sf.ICreateCorpQueryService");
		callvo4.setMethodname("querySettledPeriod");
		callvo4.setParamtype(new Class[] { String.class, String.class });
		callvo4.setParam(new Object[] {dwbm,"GL"});
		callvo4.getCode();
		list.add(callvo4);


		Map<String,Object> datas= NCLocator.getInstance().lookup(IErmEJBService.class).callBatchEJBService(list.toArray(new ServiceVO[]{}));

		List<String[]> result=new ArrayList<String[]>();
		result.add(0,(String[]) datas.get(callvo1.getCode()));
		result.add(1,(String[]) datas.get(callvo2.getCode()));
		result.add(2,(String[]) datas.get(callvo3.getCode()));
		result.add(3,(String[]) datas.get(callvo4.getCode()));

		return result;

	}

	/**
	 * 初始化
	 */
	private void onCheckInit() {

		ReportVO voRep = new ReportVO();
		try {
			FilterCondVO voCond = m_ReckoningBegp.getSelectedData();
			setYearOfSel(voCond.getYear());
			setCopeOfSel(voCond.getQj());
			voCond.setDwbm(getSysInfo().getCurDwbm());
	//		voCond.setSfbz(getSysInfo().getSfbz());
			voCond.setMode1(getSysInfo().getCheckMode1());
	//		voCond.setMode2(getSysInfo().getCheckMode2());
			voCond.setMode3(getSysInfo().getCheckMode3());
	//		voCond.setMode4(getSysInfo().getCheckMode4());
			voCond.setPk_org(getSysInfo().getPk_org());
	//		String param = "AR_QC000";
			String prodID = "erm";

	//		if(getSfbz().equals("Yf")){
	//			param = "AP_QC000";
	//			prodID = "AP";
	//		}else if(getSfbz().equals("Ys")){
	//			param = "AR_QC000";
	//			prodID = "AR";
	//		}

			String[] accountInfo = null;		//结账信息
			String[] enableInfo = null;			//启用信息
			String pk_org = nc.desktop.ui.WorkbenchEnvironment.getInstance().getLoginUser().getPk_org(); //取得当前登录用户所在的业务单元PK

			List<String[]> result = callRemoteService(getSysInfo().getCurDwbm(), prodID);
	        // 查询
			if(accountInfo==null){
				accountInfo=result.get(0);
				if(accountInfo==null){
					accountInfo = NCLocator.getInstance().lookup(ICloseAccountService.class).getCloseAccountInfo(getNodeCode(), getSysInfo().getPk_org());
	//			    accountInfo = FipubTools.querySettledPeriod("0001",	"FI");
				}
			}
			if(enableInfo==null){
				enableInfo=result.get(1);
				if(enableInfo==null){
				    enableInfo = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).queryEnabledPeriod(pk_org, prodID);
				}
			}
			if(accountInfo==null || enableInfo==null)
				return;

	        String accYear = m_ReckoningBegp.getYearOfFDM(); /*当前结账年*/
	        String accMonth = m_ReckoningBegp.getCopeOfFDM();/*当前结账月*/

			String enabledYear = enableInfo[0].substring(0, 4); /*启用年*/
			String enabledMonth = enableInfo[0].substring(5, 7); /*启用月*/

			if(accYear!=null && accMonth!=null && accYear.equals(enabledYear) && accMonth.equals(enabledMonth) &&
					NCLocator.getInstance().lookup(IInitBillCloseService.class).isInitBillClosed(getNodeCode(),pk_org))
			{
				MessageDialog.showErrorDlg(this,nc.ui.ml.NCLangRes.getInstance().getStrByID("2006","UPP2006-000314")/*@res "错误"*/,nc.ui.ml.NCLangRes.getInstance().getStrByID("2006","UPP2006-000315")/*@res "本系统的期初余额节点未关闭"*/);
				return;
			}

	        //判断是否已关帐
			String period = null;
			if(getCopeOfSel().length()< 7 ) {
				period = getYearOfSel()+"-"+getCopeOfSel();
			} else {
				period = getCopeOfSel();
			}

	//		Map<String,Boolean> res = NCLocator.getInstance().lookup(ICloseAccQryPubServicer.class).isCloseByModuleIdAndPk_org(this.getNodeCode(), this.getSysInfo().getPk_org(), new String[]{period});
	//        if(!res.get(period).booleanValue()) {
	//        	throw new DataValidateException("请先关帐再进行此操作");
	//		}


			/*检查是否符合结账条件*/
			voRep = onPeriodValidate("Reckoning",accountInfo,new String[]{enabledYear,enabledMonth},getSysInfo(),result.get(2),result.get(3));
			if (voRep.getState()) {
				onCardLayout("Check");
				setCurState("Check");
				m_ReckoningCheckp.setCheckCond(voCond); /*将结账条件传给结账检查类*/
				if(getSysInfo().getCheckMode4()!=null)
				{
					if(getSysInfo().getCheckMode4().equalsIgnoreCase("check")
							||getSysInfo().getCheckMode4().equalsIgnoreCase("control")){
						onCheckHl();
					}
				}

				m_ReckoningCheckp.setCurrencyInfo(getCurrencyInfo());
				m_ReckoningCheckp.initPanel();
				int count = m_ReckoningCheckp.getCheckCount();
				/*如果没有要检查的项直接显示结账界面*/
				if (count == 4)
					onButtonChange("CheckNone");
				else
					onButtonChange("Check");
			} else
				showWarningMessage(voRep.getInfo());

			showHintMessage("");
		} catch (DataValidateException ex) {
			ExceptionHandler.consume(ex);
			showWarningMessage(ex.getMessage());
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);
		}
	}
	/**
	 * 检验用户选中的期间是否可以结账或者反结账。
	 * 创建日期：(2010-12-3 10:46:51)
	 * @param strings2
	 * @param strings
	 * @param accountInfo2
	 */
	private ReportVO onPeriodValidate(String sign, String[] accountInfo,String[] enableInfo,SystemInfoVO sysInfo, String[] jc_accountInfo, String[] gl_accountInfo) {
		return null;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-16 11:37:25)
	 * 最后修改日期：(2001-8-16 11:37:25)
	 * @author：wyan
	 */
	private void onReckoning() {
		onReckoningOver();
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-16 11:41:31)
	 * 最后修改日期：(2001-8-16 11:41:31)
	 * @author：wyan
	 */
	private void onReckoningBeg() {
		onCardLayout("Begin");
		onButtonChange("Begin");
		setCurState("Begin");
	}

	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-16 11:39:35)
	 * 最后修改日期：(2001-8-16 11:39:35)
	 * @author：wyan
	 */
		private void onReckoningCheck() {

			Vector vResult = new Vector();
			RemoteTransferVO voRemote = new RemoteTransferVO();
		try {
			showHintMessage(TermEndMsg.getm_sCheckBeg());
			voRemote = m_ReckoningCheckp.onReckoningCheck();/*进行结账检查*/
			vResult = voRemote.getTranData1();
			boolean state = voRemote.getReckoningState();/*得到本月结账状态(是否可以结账)*/
			onCardLayout("Report");/*检查完成后切换到结账报告界面*/
			m_ReckoningReportp.setData(vResult);/*将检查结果传给结账报告类*/
			m_ReckoningReportp.initTable();/*调用方法初始化结账报告界面的TABLE*/
			setCurState("Report");/*标记当前界面标志*/
			if(!state)
			onButtonChange("UnReckoning");/*如果有要控制的检查项没有符合要求，那末本月不可结账*/
			else
			onButtonChange("Report");/*切换主界面的按钮状态*/
			showHintMessage(TermEndMsg.getm_sCheckEnd());
		}
		catch (Exception ex) {
			showHintMessage(TermEndMsg.getm_sCheckFail());
			ExceptionHandler.consume(ex);
		}
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-16 11:42:19)
	 * 最后修改日期：(2001-8-16 11:42:19)
	 * @author：wyan
	 */
	private void onReckoningOver() {

		try {
			String[] accountInfo = null;
			String[] enableInfo = null;
			String dwbm = getSysInfo().getCurDwbm();
			String prodId = getSysInfo().getProdID();
			String pk_org = getSysInfo().getPk_org();

			if(accountInfo==null){
	//		    accountInfo = FipubTools.querySettledPeriod("0001",	"FI");
				accountInfo = NCLocator.getInstance().lookup(ICloseAccountService.class).getCloseAccountInfo(getNodeCode(), getSysInfo().getPk_org());
			}
			if(enableInfo==null){
			    enableInfo = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).queryEnabledPeriod(dwbm, prodId);
			}


			String year = getYearOfSel();
			String cope = getCopeOfSel();

	//		Proxy.getICreateCorpService().updateSettledPeriod(pk_org,"FI",year,cope);
			NCLocator.getInstance().lookup(ICloseAccountService.class).updateCloseAccountInfo(getNodeCode(), getSysInfo().getPk_org(), year, cope);
			getITermEndPrivate().termEndOperation(prodId,new TermEndVO(dwbm,year,cope),false);

			setCurState("Finish");
			onButtonChange("Finish");
			m_ReckoningBegp.onChangeStateForOper(accountInfo,enableInfo,"Reckoning");
			showHintMessage(TermEndMsg.getm_sReckoningSuc());

		} catch (Exception ex) {
			showHintMessage(TermEndMsg.getm_sReckoningFail());
			ExceptionHandler.consume(ex);

		}

	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-30 11:37:13)
	 * @param cope java.lang.String
	 */
	private void setCopeOfSel(String cope) {
		m_sCopeOfFirDisAccMon = cope;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-19 13:44:59)
	 * @param vo nc.vo.arap.agiotage.AgiotageVO
	 */
	public void setCurrencyInfo(AgiotageVO vo) {
	    m_voCurrency = vo;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-16 11:40:49)
	 * 最后修改日期：(2001-8-16 11:40:49)
	 * @author：wyan
	 * @param state java.lang.String
	 */
	private void setCurState(String state) {
		m_sCurState = state;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-30 11:37:56)
	 * @param year java.lang.String
	 */
	private void setYearOfSel(String year) {
		m_sYearOfFirDisAccMon = year;
	}
}