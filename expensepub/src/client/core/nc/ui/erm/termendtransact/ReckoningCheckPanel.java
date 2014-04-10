package nc.ui.erm.termendtransact;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.itf.erm.termendtransact.ITermEndPrivate;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.termendtransact.AgiotageVO;
import nc.vo.erm.termendtransact.FilterCondVO;
import nc.vo.erm.termendtransact.RemoteTransferVO;
public class ReckoningCheckPanel extends nc.ui.pub.beans.UIPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9029173027272003399L;
	private nc.ui.pub.beans.UIPanel ivjCheckStepPanel = null;
	private nc.ui.pub.beans.UILabel ivjDocIsAllAccount = null;
	private nc.ui.pub.beans.UILabel ivjDocIsAllToVocher = null;
	private FilterCondVO m_vCondVO = null;
	private nc.ui.pub.beans.UIPanel ivjUIPanel1 = null;
	private nc.ui.pub.beans.UILabel ivjFirstLabel = null;
	private nc.ui.pub.beans.UILabel ivjFourthLabel = null;
	private nc.ui.pub.beans.UILabel ivjSecondLabel = null;
	private nc.ui.pub.beans.UILabel ivjThirdLabel = null;
	private nc.ui.pub.beans.UILabel ivjSFkdIsAllVerify = null;
	private nc.ui.pub.beans.UILabel ivjAgiotageIsCalculate = null;
	private AgiotageVO m_voCurrency = null;
	private int m_iCheckCount = -1;/*��鲽��*/
/**
 * ReckoingCheckPanel ������ע�⡣
 */
public ReckoningCheckPanel() {
	super();
	initialize();
}
/**
 * ReckoingCheckPanel ������ע�⡣
 * @param p0 java.awt.LayoutManager
 */
public ReckoningCheckPanel(java.awt.LayoutManager p0) {
	super(p0);
}
/**
 * ReckoingCheckPanel ������ע�⡣
 * @param p0 java.awt.LayoutManager
 * @param p1 boolean
 */
public ReckoningCheckPanel(java.awt.LayoutManager p0, boolean p1) {
	super(p0, p1);
}
/**
 * ReckoingCheckPanel ������ע�⡣
 * @param p0 boolean
 */
public ReckoningCheckPanel(boolean p0) {
	super(p0);
}
/**
 * ���� OtherIsAllToVouchid ����ֵ��
 * @return nc.ui.pub.beans.UILabel
 */
/* ���棺�˷������������ɡ� */
private nc.ui.pub.beans.UILabel getAgiotageIsCalculate() {
	if (ivjAgiotageIsCalculate == null) {
		try {
			ivjAgiotageIsCalculate = new nc.ui.pub.beans.UILabel();
			ivjAgiotageIsCalculate.setName("AgiotageIsCalculate");
			ivjAgiotageIsCalculate.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000063")/*@res "��ֹ�����µ����Ƿ�ȫ������������"*/);
			ivjAgiotageIsCalculate.setForeground(java.awt.Color.black);
			ivjAgiotageIsCalculate.setILabelType(0/** JavaĬ��(�Զ���)*/);
			ivjAgiotageIsCalculate.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAgiotageIsCalculate.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAgiotageIsCalculate;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-12-1 13:07:10)
 * @return boolean
 */
public int getCheckCount() {
	return m_iCheckCount;
}
/**
 * ��Ҫ���ܣ�
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-8-20 22:33:19)
 * ����޸����ڣ�(2001-8-20 22:33:19)
 * @author��wyan
 * @param step java.lang.String
 */
public boolean getCheckStep(String step) {
/*	if(step.equals("step1")){
		return getCheckStep1Sel().isSelected();
	}
	else if(step.equals("step2")){
		return getCheckStep2Sel().isSelected();
	}
	else if(step.equals("step3")){
		return getCheckStep3Sel().isSelected();
	}
	else if(step.equals("step4")){
		return getCheckStep4Sel().isSelected();
	}
	else if(step.equals("step5")){
		return getCheckStep5Sel().isSelected();
	}
	else if(step.equals("step6")){
		return getCheckStep6Sel().isSelected();
	}
	*/
	return false;
}
/**
 * ���� CheckStepPanel ����ֵ��
 * @return nc.ui.pub.beans.UIPanel
 */
/* ���棺�˷������������ɡ� */
private nc.ui.pub.beans.UIPanel getCheckStepPanel() {
	if (ivjCheckStepPanel == null) {
		try {
			ivjCheckStepPanel = new nc.ui.pub.beans.UIPanel();
			ivjCheckStepPanel.setName("CheckStepPanel");
			ivjCheckStepPanel.setPreferredSize(new java.awt.Dimension(40, 100));
			ivjCheckStepPanel.setLayout(getCheckStepPanelGridLayout());
			ivjCheckStepPanel.setMinimumSize(new java.awt.Dimension(100, 220));
			getCheckStepPanel().add(getDocIsAllAccount(), getDocIsAllAccount().getName());
			getCheckStepPanel().add(getSFkdIsAllVerify(), getSFkdIsAllVerify().getName());
			getCheckStepPanel().add(getDocIsAllToVocher(), getDocIsAllToVocher().getName());
			getCheckStepPanel().add(getAgiotageIsCalculate(), getAgiotageIsCalculate().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCheckStepPanel;
}
/**
 * ���� CheckStepPanelGridLayout ����ֵ��
 * @return java.awt.GridLayout
 */
/* ���棺�˷������������ɡ� */
private java.awt.GridLayout getCheckStepPanelGridLayout() {
	java.awt.GridLayout ivjCheckStepPanelGridLayout = null;
	try {
		/* �������� */
		ivjCheckStepPanelGridLayout = new java.awt.GridLayout();
		ivjCheckStepPanelGridLayout.setRows(5);
		ivjCheckStepPanelGridLayout.setHgap(50);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjCheckStepPanelGridLayout;
}
/**
 * ��Ҫ���ܣ�
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-8-21 16:46:11)
 * ����޸����ڣ�(2001-8-21 16:46:11)
 * @author��wyan
 * @return nc.vo.arap.termendtransact.FilterCondVO
 */
public FilterCondVO getCondVO() {
	return m_vCondVO;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-11-19 13:48:48)
 * @return nc.vo.arap.agiotage.AgiotageVO
 */
public AgiotageVO getCurrencyInfo() {
    return m_voCurrency;
}
/**
 * ���� DocIsAllAccount ����ֵ��
 * @return nc.ui.pub.beans.UILabel
 */
/* ���棺�˷������������ɡ� */
private nc.ui.pub.beans.UILabel getDocIsAllAccount() {
	if (ivjDocIsAllAccount == null) {
		try {
			ivjDocIsAllAccount = new nc.ui.pub.beans.UILabel();
			ivjDocIsAllAccount.setName("DocIsAllAccount");
			ivjDocIsAllAccount.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000064")/*@res "��ֹ�����µ����Ƿ�ȫ����Ч"*/);
			ivjDocIsAllAccount.setForeground(java.awt.Color.black);
			ivjDocIsAllAccount.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjDocIsAllAccount.setILabelType(0/** JavaĬ��(�Զ���)*/);
			ivjDocIsAllAccount.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDocIsAllAccount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDocIsAllAccount;
}
/**
 * ���� DocIsAllToVocher ����ֵ��
 * @return nc.ui.pub.beans.UILabel
 */
/* ���棺�˷������������ɡ� */
private nc.ui.pub.beans.UILabel getDocIsAllToVocher() {
	if (ivjDocIsAllToVocher == null) {
		try {
			ivjDocIsAllToVocher = new nc.ui.pub.beans.UILabel();
			ivjDocIsAllToVocher.setName("DocIsAllToVocher");
			ivjDocIsAllToVocher.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000065")/*@res "��ֹ�����µ����Ƿ�ȫ�����ɻ��ƾ֤"*/);
			ivjDocIsAllToVocher.setForeground(java.awt.Color.black);
			ivjDocIsAllToVocher.setILabelType(0/** JavaĬ��(�Զ���)*/);
			ivjDocIsAllToVocher.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDocIsAllToVocher.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDocIsAllToVocher;
}
/**
 * ���� UILabel2 ����ֵ��
 * @return nc.ui.pub.beans.UILabel
 */
/* ���棺�˷������������ɡ� */
private nc.ui.pub.beans.UILabel getFirstLabel() {
	if (ivjFirstLabel == null) {
		try {
			ivjFirstLabel = new nc.ui.pub.beans.UILabel();
			ivjFirstLabel.setName("FirstLabel");
			ivjFirstLabel.setText("(1)");
			ivjFirstLabel.setForeground(java.awt.Color.black);
			ivjFirstLabel.setILabelType(0/** JavaĬ��(�Զ���)*/);
			ivjFirstLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjFirstLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFirstLabel;
}
/**
 * ���� FourthLabel ����ֵ��
 * @return nc.ui.pub.beans.UILabel
 */
/* ���棺�˷������������ɡ� */
private nc.ui.pub.beans.UILabel getFourthLabel() {
	if (ivjFourthLabel == null) {
		try {
			ivjFourthLabel = new nc.ui.pub.beans.UILabel();
			ivjFourthLabel.setName("FourthLabel");
			ivjFourthLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjFourthLabel.setText("(4)");
			ivjFourthLabel.setForeground(java.awt.Color.black);
			ivjFourthLabel.setILabelType(0/** JavaĬ��(�Զ���)*/);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFourthLabel;
}
/**
 * ���� UILabel3 ����ֵ��
 * @return nc.ui.pub.beans.UILabel
 */
/* ���棺�˷������������ɡ� */
private nc.ui.pub.beans.UILabel getSecondLabel() {
	if (ivjSecondLabel == null) {
		try {
			ivjSecondLabel = new nc.ui.pub.beans.UILabel();
			ivjSecondLabel.setName("SecondLabel");
			ivjSecondLabel.setText("(2)");
			ivjSecondLabel.setForeground(java.awt.Color.black);
			ivjSecondLabel.setILabelType(0/** JavaĬ��(�Զ���)*/);
			ivjSecondLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSecondLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSecondLabel;
}
/**
 * ���� SkdIsAllVerify ����ֵ��
 * @return nc.ui.pub.beans.UILabel
 */
/* ���棺�˷������������ɡ� */
private nc.ui.pub.beans.UILabel getSFkdIsAllVerify() {
	if (ivjSFkdIsAllVerify == null) {
		try {
			ivjSFkdIsAllVerify = new nc.ui.pub.beans.UILabel();
			ivjSFkdIsAllVerify.setName("SFkdIsAllVerify");
			ivjSFkdIsAllVerify.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000066")/*@res "��ֹ�������տ�Ƿ�ȫ������"*/);
			ivjSFkdIsAllVerify.setForeground(java.awt.Color.black);
			ivjSFkdIsAllVerify.setILabelType(0/** JavaĬ��(�Զ���)*/);
			ivjSFkdIsAllVerify.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSFkdIsAllVerify.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSFkdIsAllVerify;
}
/**
 * ���� UILabel4 ����ֵ��
 * @return nc.ui.pub.beans.UILabel
 */
/* ���棺�˷������������ɡ� */
private nc.ui.pub.beans.UILabel getThirdLabel() {
	if (ivjThirdLabel == null) {
		try {
			ivjThirdLabel = new nc.ui.pub.beans.UILabel();
			ivjThirdLabel.setName("ThirdLabel");
			ivjThirdLabel.setText("(3)");
			ivjThirdLabel.setForeground(java.awt.Color.black);
			ivjThirdLabel.setILabelType(0/** JavaĬ��(�Զ���)*/);
			ivjThirdLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjThirdLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjThirdLabel;
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
			ivjUIPanel1.setPreferredSize(new java.awt.Dimension(80, 60));
			ivjUIPanel1.setLayout(getUIPanel1GridLayout());
			getUIPanel1().add(getFirstLabel(), getFirstLabel().getName());
			getUIPanel1().add(getSecondLabel(), getSecondLabel().getName());
			getUIPanel1().add(getThirdLabel(), getThirdLabel().getName());
			getUIPanel1().add(getFourthLabel(), getFourthLabel().getName());
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
 * ���� UIPanel1GridLayout ����ֵ��
 * @return java.awt.GridLayout
 */
/* ���棺�˷������������ɡ� */
private java.awt.GridLayout getUIPanel1GridLayout() {
	java.awt.GridLayout ivjUIPanel1GridLayout = null;
	try {
		/* �������� */
		ivjUIPanel1GridLayout = new java.awt.GridLayout();
		ivjUIPanel1GridLayout.setRows(5);
		ivjUIPanel1GridLayout.setHgap(50);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjUIPanel1GridLayout;
}
/**
 * ÿ�������׳��쳣ʱ������
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable e) {

	ExceptionHandler.consume(e);
}
/**
 * ��ʼ���ࡣ
 */
/* ���棺�˷������������ɡ� */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ReckoingCheckPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(514, 322);
		add(getCheckStepPanel(), "Center");
		add(getUIPanel1(), "West");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * ��Ҫ���ܣ���ʼ��������
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-8-16 18:15:15)
 * ����޸����ڣ�(2001-8-16 18:15:15)
 * @author��wyan
 */
public void initPanel() {
    onChangeView();
}

/**
 * ��Ҫ���ܣ������û���ѡ����ȷ����鼸���Ҫ������û�
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-9-21 14:29:27)
 * ����޸����ڣ�(2001-9-21 14:29:27)
 * @author��wyan
 */
private void onChangeView() {

	int count = 0;
	try {
		if (getCondVO().getMode1() == null) {
			getDocIsAllAccount().setEnabled(false);
			getFirstLabel().setEnabled(false);
			count++;
		}
		if (getCondVO().getMode2() == null) {
			getSFkdIsAllVerify().setEnabled(false);
			getSecondLabel().setEnabled(false);
			count++;
		}
		if (getCondVO().getMode3() == null) {
			getDocIsAllToVocher().setEnabled(false);
			getThirdLabel().setEnabled(false);
			count++;
		}
		if (getCondVO().getMode4() == null) {
			getAgiotageIsCalculate().setEnabled(false);
			getFourthLabel().setEnabled(false);
			count++;
		}
		if (getCondVO().getSfbz().equals("Yf")) {
			getSFkdIsAllVerify().setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000067")/*@res "��ֹ�����¸���Ƿ�ȫ������"*/);
		}
		if (getCondVO().getSfbz().equals("Bzzx")) {
			getSFkdIsAllVerify().setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000068")/*@res "��ֹ��������(��)��Ƿ�ȫ������"*/);
		}
		setCheckCount(count);

	} catch (Exception ex) {
		ExceptionHandler.consume(ex);
	}
}
/**
 * ��Ҫ���ܣ�
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-9-21 13:39:01)
 * ����޸����ڣ�(2001-9-21 13:39:01)
 * @author��wyan
 * @return nc.pub.arap.transaction.RemoteTransferVO
 */
public RemoteTransferVO onReckoningCheck() throws Exception {

    RemoteTransferVO voRemote = new RemoteTransferVO();
    try {
        voRemote = getITermEndPrivate().onReckoningCheck(getCondVO(), getCurrencyInfo());
    } catch (Exception ex) {
        throw ex;
    }
    return voRemote;
}

private ITermEndPrivate getITermEndPrivate() throws ComponentException {

    return ((ITermEndPrivate) NCLocator.getInstance().lookup(ITermEndPrivate.class.getName()));

}
/**
 * ��Ҫ���ܣ�
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-8-21 16:37:45)
 * ����޸����ڣ�(2001-8-21 16:37:45)
 * @author��wyan
 * @param vo nc.vo.arap.termendtransact.FilterCondVO
 */
public void setCheckCond(FilterCondVO vo) {
	m_vCondVO = vo;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-12-1 13:07:39)
 */
private void setCheckCount(int count) {
	m_iCheckCount = count;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-11-19 13:47:59)
 * @param vo nc.vo.arap.agiotage.AgiotageVO
 */
public void setCurrencyInfo(AgiotageVO vo) {
    m_voCurrency = vo;
}
}