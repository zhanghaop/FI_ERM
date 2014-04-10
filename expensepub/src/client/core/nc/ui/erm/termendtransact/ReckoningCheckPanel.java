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
	private int m_iCheckCount = -1;/*检查步数*/
/**
 * ReckoingCheckPanel 构造子注解。
 */
public ReckoningCheckPanel() {
	super();
	initialize();
}
/**
 * ReckoingCheckPanel 构造子注解。
 * @param p0 java.awt.LayoutManager
 */
public ReckoningCheckPanel(java.awt.LayoutManager p0) {
	super(p0);
}
/**
 * ReckoingCheckPanel 构造子注解。
 * @param p0 java.awt.LayoutManager
 * @param p1 boolean
 */
public ReckoningCheckPanel(java.awt.LayoutManager p0, boolean p1) {
	super(p0, p1);
}
/**
 * ReckoingCheckPanel 构造子注解。
 * @param p0 boolean
 */
public ReckoningCheckPanel(boolean p0) {
	super(p0);
}
/**
 * 返回 OtherIsAllToVouchid 特性值。
 * @return nc.ui.pub.beans.UILabel
 */
/* 警告：此方法将重新生成。 */
private nc.ui.pub.beans.UILabel getAgiotageIsCalculate() {
	if (ivjAgiotageIsCalculate == null) {
		try {
			ivjAgiotageIsCalculate = new nc.ui.pub.beans.UILabel();
			ivjAgiotageIsCalculate.setName("AgiotageIsCalculate");
			ivjAgiotageIsCalculate.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000063")/*@res "截止到本月单据是否全部计算汇兑损益"*/);
			ivjAgiotageIsCalculate.setForeground(java.awt.Color.black);
			ivjAgiotageIsCalculate.setILabelType(0/** Java默认(自定义)*/);
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
 * 此处插入方法说明。
 * 创建日期：(2001-12-1 13:07:10)
 * @return boolean
 */
public int getCheckCount() {
	return m_iCheckCount;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-20 22:33:19)
 * 最后修改日期：(2001-8-20 22:33:19)
 * @author：wyan
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
 * 返回 CheckStepPanel 特性值。
 * @return nc.ui.pub.beans.UIPanel
 */
/* 警告：此方法将重新生成。 */
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
 * 返回 CheckStepPanelGridLayout 特性值。
 * @return java.awt.GridLayout
 */
/* 警告：此方法将重新生成。 */
private java.awt.GridLayout getCheckStepPanelGridLayout() {
	java.awt.GridLayout ivjCheckStepPanelGridLayout = null;
	try {
		/* 创建部件 */
		ivjCheckStepPanelGridLayout = new java.awt.GridLayout();
		ivjCheckStepPanelGridLayout.setRows(5);
		ivjCheckStepPanelGridLayout.setHgap(50);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjCheckStepPanelGridLayout;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-21 16:46:11)
 * 最后修改日期：(2001-8-21 16:46:11)
 * @author：wyan
 * @return nc.vo.arap.termendtransact.FilterCondVO
 */
public FilterCondVO getCondVO() {
	return m_vCondVO;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-11-19 13:48:48)
 * @return nc.vo.arap.agiotage.AgiotageVO
 */
public AgiotageVO getCurrencyInfo() {
    return m_voCurrency;
}
/**
 * 返回 DocIsAllAccount 特性值。
 * @return nc.ui.pub.beans.UILabel
 */
/* 警告：此方法将重新生成。 */
private nc.ui.pub.beans.UILabel getDocIsAllAccount() {
	if (ivjDocIsAllAccount == null) {
		try {
			ivjDocIsAllAccount = new nc.ui.pub.beans.UILabel();
			ivjDocIsAllAccount.setName("DocIsAllAccount");
			ivjDocIsAllAccount.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000064")/*@res "截止到本月单据是否全部生效"*/);
			ivjDocIsAllAccount.setForeground(java.awt.Color.black);
			ivjDocIsAllAccount.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjDocIsAllAccount.setILabelType(0/** Java默认(自定义)*/);
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
 * 返回 DocIsAllToVocher 特性值。
 * @return nc.ui.pub.beans.UILabel
 */
/* 警告：此方法将重新生成。 */
private nc.ui.pub.beans.UILabel getDocIsAllToVocher() {
	if (ivjDocIsAllToVocher == null) {
		try {
			ivjDocIsAllToVocher = new nc.ui.pub.beans.UILabel();
			ivjDocIsAllToVocher.setName("DocIsAllToVocher");
			ivjDocIsAllToVocher.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000065")/*@res "截止到本月单据是否全部生成会计凭证"*/);
			ivjDocIsAllToVocher.setForeground(java.awt.Color.black);
			ivjDocIsAllToVocher.setILabelType(0/** Java默认(自定义)*/);
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
 * 返回 UILabel2 特性值。
 * @return nc.ui.pub.beans.UILabel
 */
/* 警告：此方法将重新生成。 */
private nc.ui.pub.beans.UILabel getFirstLabel() {
	if (ivjFirstLabel == null) {
		try {
			ivjFirstLabel = new nc.ui.pub.beans.UILabel();
			ivjFirstLabel.setName("FirstLabel");
			ivjFirstLabel.setText("(1)");
			ivjFirstLabel.setForeground(java.awt.Color.black);
			ivjFirstLabel.setILabelType(0/** Java默认(自定义)*/);
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
 * 返回 FourthLabel 特性值。
 * @return nc.ui.pub.beans.UILabel
 */
/* 警告：此方法将重新生成。 */
private nc.ui.pub.beans.UILabel getFourthLabel() {
	if (ivjFourthLabel == null) {
		try {
			ivjFourthLabel = new nc.ui.pub.beans.UILabel();
			ivjFourthLabel.setName("FourthLabel");
			ivjFourthLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjFourthLabel.setText("(4)");
			ivjFourthLabel.setForeground(java.awt.Color.black);
			ivjFourthLabel.setILabelType(0/** Java默认(自定义)*/);
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
 * 返回 UILabel3 特性值。
 * @return nc.ui.pub.beans.UILabel
 */
/* 警告：此方法将重新生成。 */
private nc.ui.pub.beans.UILabel getSecondLabel() {
	if (ivjSecondLabel == null) {
		try {
			ivjSecondLabel = new nc.ui.pub.beans.UILabel();
			ivjSecondLabel.setName("SecondLabel");
			ivjSecondLabel.setText("(2)");
			ivjSecondLabel.setForeground(java.awt.Color.black);
			ivjSecondLabel.setILabelType(0/** Java默认(自定义)*/);
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
 * 返回 SkdIsAllVerify 特性值。
 * @return nc.ui.pub.beans.UILabel
 */
/* 警告：此方法将重新生成。 */
private nc.ui.pub.beans.UILabel getSFkdIsAllVerify() {
	if (ivjSFkdIsAllVerify == null) {
		try {
			ivjSFkdIsAllVerify = new nc.ui.pub.beans.UILabel();
			ivjSFkdIsAllVerify.setName("SFkdIsAllVerify");
			ivjSFkdIsAllVerify.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000066")/*@res "截止到本月收款单是否全部核销"*/);
			ivjSFkdIsAllVerify.setForeground(java.awt.Color.black);
			ivjSFkdIsAllVerify.setILabelType(0/** Java默认(自定义)*/);
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
 * 返回 UILabel4 特性值。
 * @return nc.ui.pub.beans.UILabel
 */
/* 警告：此方法将重新生成。 */
private nc.ui.pub.beans.UILabel getThirdLabel() {
	if (ivjThirdLabel == null) {
		try {
			ivjThirdLabel = new nc.ui.pub.beans.UILabel();
			ivjThirdLabel.setName("ThirdLabel");
			ivjThirdLabel.setText("(3)");
			ivjThirdLabel.setForeground(java.awt.Color.black);
			ivjThirdLabel.setILabelType(0/** Java默认(自定义)*/);
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
 * 返回 UIPanel1 特性值。
 * @return nc.ui.pub.beans.UIPanel
 */
/* 警告：此方法将重新生成。 */
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
 * 返回 UIPanel1GridLayout 特性值。
 * @return java.awt.GridLayout
 */
/* 警告：此方法将重新生成。 */
private java.awt.GridLayout getUIPanel1GridLayout() {
	java.awt.GridLayout ivjUIPanel1GridLayout = null;
	try {
		/* 创建部件 */
		ivjUIPanel1GridLayout = new java.awt.GridLayout();
		ivjUIPanel1GridLayout.setRows(5);
		ivjUIPanel1GridLayout.setHgap(50);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjUIPanel1GridLayout;
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
 * 主要功能：初始化检查界面
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-16 18:15:15)
 * 最后修改日期：(2001-8-16 18:15:15)
 * @author：wyan
 */
public void initPanel() {
    onChangeView();
}

/**
 * 主要功能：根据用户的选择来确定检查几项，不要求检查的置灰
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-9-21 14:29:27)
 * 最后修改日期：(2001-9-21 14:29:27)
 * @author：wyan
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
			getSFkdIsAllVerify().setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000067")/*@res "截止到本月付款单是否全部核销"*/);
		}
		if (getCondVO().getSfbz().equals("Bzzx")) {
			getSFkdIsAllVerify().setName(nc.ui.ml.NCLangRes.getInstance().getStrByID("200604","UPP200604-000068")/*@res "截止到本月收(付)款单是否全部核销"*/);
		}
		setCheckCount(count);

	} catch (Exception ex) {
		ExceptionHandler.consume(ex);
	}
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-9-21 13:39:01)
 * 最后修改日期：(2001-9-21 13:39:01)
 * @author：wyan
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
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-21 16:37:45)
 * 最后修改日期：(2001-8-21 16:37:45)
 * @author：wyan
 * @param vo nc.vo.arap.termendtransact.FilterCondVO
 */
public void setCheckCond(FilterCondVO vo) {
	m_vCondVO = vo;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-12-1 13:07:39)
 */
private void setCheckCount(int count) {
	m_iCheckCount = count;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-11-19 13:47:59)
 * @param vo nc.vo.arap.agiotage.AgiotageVO
 */
public void setCurrencyInfo(AgiotageVO vo) {
    m_voCurrency = vo;
}
}