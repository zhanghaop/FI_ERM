package nc.ui.arap.bx;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JPanel;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.itf.arap.pub.IBxUIControl;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * @author twei
 *
 *  nc.ui.arap.bx.ContrastDialog
 *
 *  ����Ի���, ʹ���б�ģ��ʵ��
 */
public class ContrastDialog extends UIDialog implements java.awt.event.ActionListener, nc.ui.pub.beans.ValueChangedListener {

	private static final long serialVersionUID = 3022537308787517645L;

	private UIPanel queryPanel;
	private UILabel loanuserlabel;
	private UILabel loanfromtimelabel;
	private UILabel loantotimelabel;
	private UIRefPane loanusertext;
	private UIRefPane loanfromtimetext;
	private UIRefPane loantotimetext;

	private UIButton  loanquerybtn;

	private UIButton btnConfirm;

	private UIButton btnCancel;

	private JKBXVO bxvo;

	private String nodecode;

	private String pkCorp;

	private JPanel ivjUIDialogContentPane;

	private BXBillListPanel listPanel;

	private UIPanel buttonPanel;

	private BXBillMainPanel mainPanel;

	//������ߵ���
	private UFDouble getBxje(){
		 JKBXVO bxvo2 = getBxvo();
		 return bxvo2.getParentVO().getYbje();
	}
	private UFDouble getContrastTotal(){
		 List<BxcontrastVO> contrastData = getContrastData();
		 UFDouble total=new UFDouble(0);
		 if(contrastData!=null){
			 for(BxcontrastVO vo:contrastData){
				 total=total.add(vo.getCjkybje());
			 }
		 }
		 return total;
	}
	protected UFDouble getHkTotal() {
		 List<BxcontrastVO> contrastData = getContrastData();
		 UFDouble total=new UFDouble(0);
		 if(contrastData!=null){
			 for(BxcontrastVO vo:contrastData){
				 total=total.add(vo.getHkybje());
			 }
		 }
		 return total;
	}
	/**
	 * @see ���س���ģ��
	 */
	private void loadBillListTemplate() {
		
		getListPanel().loadTemplet(nodecode, null, new BxParam().getPk_user(),BXUiUtil.getPK_group(), "CJK");
	}

	public BXBillListPanel getListPanel() {
		if (listPanel == null) {
			try {
				listPanel = new BXBillListPanel();
				listPanel.setName("LIST");

				loadBillListTemplate();

				listPanel.getParentListPanel().setTotalRowShow(true);
				nc.vo.pub.bill.BillRendererVO voCell = new nc.vo.pub.bill.BillRendererVO();
				voCell.setShowThMark(true);
				voCell.setShowZeroLikeNull(true);
				listPanel.getChildListPanel().setShowFlags(voCell);
				listPanel.getParentListPanel().setShowFlags(voCell);
				listPanel.setEnabled(true);

				listPanel.getHeadTable().getActionMap().clear();
				listPanel.addHeadEditListener(new BillEditListener(){

					public void afterEdit(BillEditEvent e) {
						String errormsg = null;
						if(e.getKey().equals(JKBXHeaderVO.SELECTED)){
							
							if(((Boolean)e.getValue()).booleanValue()){
								String jkdpk_corp = getListPanel().getHeadColumnValue("pk_org", e.getRow()).toString();
								String bxdpk_corp = bxvo.getParentVO().getPk_org();
								getListPanel().getHeadItem(JKBXHeaderVO.CJKYBJE).setEnabled(true);
								getListPanel().getHeadItem(JKBXHeaderVO.HKYBJE).setEnabled(true);
								try{
									String bbpk = Currency.getOrgLocalCurrPK(jkdpk_corp);
									String bbpk2 = Currency.getOrgLocalCurrPK(bxdpk_corp);
									if( !VOUtils.simpleEquals(bbpk2, bbpk)){
										throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000372")/*@res "���ŵ��ݵ����ڹ�˾��λ�Ҳ�ͬ�����ܽ��г����������ѡ����������"*/);
									}
								}catch (BusinessException e1) {
									
									BXUiUtil.showUif2DetailMessage(ContrastDialog.this, e1.getMessage(), e1.getMessage());
									//mainPanel.showWarningMessage(e1.getMessage());
									getListPanel().setHeadColumnValue(JKBXHeaderVO.SELECTED,e.getRow(),new Boolean(false));
								}

								UFDouble ybye = (UFDouble) getListPanel().getHeadColumnValue(JKBXHeaderVO.YBYE, e.getRow());
								UFDouble bxybje = bxvo.getParentVO().getYbje();

								if(bxvo.getParentVO().isXeBill()){
									getListPanel().setHeadColumnValue("zpje",e.getRow(),bxybje);
									getListPanel().setHeadColumnValue(JKBXHeaderVO.CJKYBJE,e.getRow(),new UFDouble(0));
									getListPanel().setHeadColumnValue(BxcontrastVO.HKYBJE,e.getRow(),new UFDouble(0));
								}else{
									getListPanel().setHeadColumnValue(JKBXHeaderVO.CJKYBJE,e.getRow(),ybye);
									getListPanel().setHeadColumnValue(BxcontrastVO.HKYBJE,e.getRow(),new UFDouble(0));
								}

							}else{
								getListPanel().setHeadColumnValue(JKBXHeaderVO.CJKYBJE,e.getRow(),new UFDouble(0));
								getListPanel().setHeadColumnValue(BxcontrastVO.HKYBJE,e.getRow(),new UFDouble(0));
								getListPanel().getHeadItem(JKBXHeaderVO.CJKYBJE).setEnabled(false);
								getListPanel().getHeadItem(JKBXHeaderVO.HKYBJE).setEnabled(false);

							}
							resetHkje();
						}else if(e.getKey().equals(JKBXHeaderVO.CJKYBJE)){
							UFDouble ybye = (UFDouble) getListPanel().getHeadColumnValue(JKBXHeaderVO.YBYE, e.getRow());
							UFDouble cjkybje = (UFDouble) getListPanel().getHeadColumnValue(JKBXHeaderVO.CJKYBJE, e.getRow());
							if(cjkybje.compareTo(ybye)>0){
								errormsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000177")/*@res "������ܴ��ڽ����!"*/;
								BXUiUtil.showUif2DetailMessage(ContrastDialog.this, errormsg, errormsg);
								//mainPanel.showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000177")/*@res "������ܴ��ڽ����!"*/);
								getListPanel().setHeadColumnValue(JKBXHeaderVO.CJKYBJE,e.getRow(),ybye);
							} else if(cjkybje.compareTo(new UFDouble(0))<0){
								errormsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000178")/*@res "��������������!"*/;
								BXUiUtil.showUif2DetailMessage(ContrastDialog.this, errormsg, errormsg);
								//mainPanel.showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000178")/*@res "��������������!"*/);
								getListPanel().setHeadColumnValue(JKBXHeaderVO.CJKYBJE,e.getRow(),ybye);
							} else {
								resetHkje();
							}

						}else if(e.getKey().equals(JKBXHeaderVO.HKYBJE)){
							UFDouble hkje = (UFDouble) getListPanel().getHeadColumnValue(JKBXHeaderVO.HKYBJE, e.getRow());
							if(hkje.compareTo(new UFDouble(0))<0){
								
								errormsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000373")/*@res "�������С����!"*/;
								BXUiUtil.showUif2DetailMessage(ContrastDialog.this, errormsg, errormsg);
								//mainPanel.showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000373")/*@res "�������С����!"*/);
								getListPanel().setHeadColumnValue(JKBXHeaderVO.HKYBJE,e.getRow(),0);
							}
						}

//added by chendya ����ϼ��н��
						setTotalRowMoney();
//--end
					}
					public void bodyRowChange(BillEditEvent e) {
						//Do nothing
					}
				});
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}

		return listPanel;
	}

	/**
	 * @author chendya
	 * ��Ҫ�ϼƵ��ֶ�
	 * @return
	 */
	public String[] getTotalMoneyKey(){

		return new String[]{BxcontrastVO.YBJE,BxcontrastVO.YBYE,BxcontrastVO.BBJE,
							BxcontrastVO.CJKBBJE,BxcontrastVO.CJKYBJE,
							BxcontrastVO.FYYBJE,BxcontrastVO.HKYBJE,
							BxcontrastVO.GLOBALBBJE,BxcontrastVO.GLOBALCJKBBJE,
							BxcontrastVO.GLOBALFYBBJE};
	}
	/**
	 * @author chendya
	 *����ϼ�
	 */
	public void setTotalRowMoney(){
		//��ȥ�ϼ���
		int rowCount = getListPanel().getHeadBillModel().getRowCount();
		final String[] fields = getTotalMoneyKey();
		BxcontrastVO totalRowVO = new BxcontrastVO();
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < fields.length; j++) {
				UFDouble value = (UFDouble)getListPanel().getHeadColumnValue(fields[j],i);
				if(value==null||value.toString().length()==0){
					continue;
				}
				if(totalRowVO.getAttributeValue(fields[j])==null){
					int decimalDigits = getListPanel().getHeadItem(fields[j]).getDecimalDigits();
					totalRowVO.setAttributeValue(fields[j], new UFDouble(0,decimalDigits));
				}
				UFDouble sumVal = (UFDouble)totalRowVO.getAttributeValue(fields[j]);
				sumVal = sumVal.add(value);
				totalRowVO.setAttributeValue(fields[j], sumVal);
			}
		}
		BillItem[] items = getListPanel().getHeadBillModel().getBodyItems();
		for (int i = 0; i < items.length; i++) {
			if(!items[i].isShow()){
				continue;
			}
			getListPanel().getHeadBillModel().getTotalTableModel().setValueAt(totalRowVO.getAttributeValue(items[i].getKey()), 0, i);
		}
	}

//	�������û�����
	private void resetHkje() {
		UFDouble contrastTotal = getContrastTotal();
		UFDouble bxje = getBxje();
		UFDouble hkje = contrastTotal.compareTo(bxje)>0?contrastTotal.sub(bxje):UFDouble.ZERO_DBL;

		for (int row = 0; row < getListPanel().getHeadBillModel().getRowCount(); row++) {
			if (getListPanel().getHeadBillModel().getValueAt(row,JKBXHeaderVO.SELECTED) == null)
				continue;
			if (((Boolean) (getListPanel().getHeadBillModel().getValueAt(row, JKBXHeaderVO.SELECTED))).booleanValue()) {
				Object value = getListPanel().getHeadColumnValue(BxcontrastVO.CJKYBJE,row);
				UFDouble cjkje=(UFDouble) (value==null?new UFDouble(0):getListPanel().getHeadColumnValue(BxcontrastVO.CJKYBJE,row));
				if(hkje.compareTo(UFDouble.ZERO_DBL)>0){
					if(cjkje.compareTo(hkje)>=0){
						getListPanel().setHeadColumnValue(JKBXHeaderVO.HKYBJE,row,hkje);
						hkje=UFDouble.ZERO_DBL;
					}else{
						getListPanel().setHeadColumnValue(JKBXHeaderVO.HKYBJE,row,cjkje);
						hkje=hkje.sub(cjkje);
					}
				}else{
					getListPanel().setHeadColumnValue(JKBXHeaderVO.HKYBJE,row,UFDouble.ZERO_DBL);
				}
			}
		}
	}


	public ContrastDialog(BXBillMainPanel parent,String nodecode, String pkCorp,JKBXVO bxvo) {
		super(parent);
		setNodecode(nodecode);
		setPkCorp(pkCorp);
//		setPkOrg(pkCorp);
		setBxvo(bxvo);
		this.mainPanel=parent;

		initialize();
	}

	private IBxUIControl getBxUIControl(){
		return NCLocator.getInstance().lookup(IBxUIControl.class);
	}

	/**
	 * @param bxvo
	 * @throws BusinessException
	 *
	 * ��ʼ������
	 */
	public void initData(JKBXVO bxvo) throws BusinessException {

		setBxvo(bxvo);

		if(bxvo.getParentVO().getReceiver()==null && bxvo.getParentVO().getJkbxr()==null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000179")/*@res "��¼�뱨����,�ٽ��г������!"*/);
		}
		if(bxvo.getParentVO().getYbje()==null){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000180")/*@res "��¼�뱨�����,�ٽ��г������!"*/);
		}

		if(!((BXBillCardPanel)mainPanel.getBillCardPanel()).isContrast()){
			BXUiUtil.resetDecimalForContrast(getListPanel(),bxvo.getParentVO().getBzbm());
			try {
				List<JKBXHeaderVO> bxvos = getBxUIControl().getJKD(bxvo.getParentVO(),BXUiUtil.getBusiDate(),null);
				setJkds(bxvos);
			} catch (Exception e) {
				Log.getInstance(this.getClass()).error(e.getMessage(), e);
				throw new BusinessException(e.getMessage(),e);
			}
		}

		initJkds(bxvo, getJkds());

		setTotalRowMoney();
	}

	private void initJkds(JKBXVO bxvo, List<JKBXHeaderVO> bxvos) {
		getListPanel().setHeadValueVos(bxvos);

		//����������Ѿ������˽�,�˴���ʼ��.
		if(((BXBillCardPanel)mainPanel.getBillCardPanel()).isContrast()){ //�Ѿ��ڽ����޸��˳�����Ϣ��ȡ������Ϣ��ʼ������Ի���
			List<BxcontrastVO> contrasts = ((BXBillCardPanel)mainPanel.getBillCardPanel()).getContrasts();
			if(contrasts!=null){
				for (BxcontrastVO head:contrasts) {
					setCjkje(head);	 //���ó�����
				}
			}
		}else{															 //ȡ���ݿ���Ϣ��ʼ������Ի���
			BxcontrastVO[] contrastVO = bxvo.getContrastVO();
			if(contrastVO!=null){
				for (BxcontrastVO head:contrastVO) {
					setCjkje(head);	 //���ó�����
				}
			}
		}
	}

	private List<JKBXHeaderVO> jkds=new ArrayList<JKBXHeaderVO>();

	/**
	 * @param pk_jkd
	 * @param cjkybje
	 *
	 * ���ó�����
	 */
	private void setCjkje(BxcontrastVO vo) {
		String pk_jkd = vo.getPk_jkd();

		if(pk_jkd!=null){
			getListPanel().setHeadColumnValue(JKBXHeaderVO.SELECTED,JKBXHeaderVO.PK_JKBX, pk_jkd,UFBoolean.TRUE);
			getListPanel().setHeadColumnValue("pk_bxcontrast",JKBXHeaderVO.PK_JKBX, pk_jkd, vo.getPk_bxcontrast());

			Object cjkje = getListPanel().getHeadColumnValue(JKBXHeaderVO.CJKYBJE,JKBXHeaderVO.PK_JKBX,pk_jkd);
			if(cjkje!=null)
				getListPanel().setHeadColumnValue(JKBXHeaderVO.CJKYBJE,JKBXHeaderVO.PK_JKBX, pk_jkd, vo.getCjkybje().add((UFDouble) cjkje));
			else
				getListPanel().setHeadColumnValue(JKBXHeaderVO.CJKYBJE,JKBXHeaderVO.PK_JKBX, pk_jkd, vo.getCjkybje());
			//���ÿ��Ա༭ 
			getListPanel().getHeadItem(JKBXHeaderVO.CJKYBJE).setEnabled(true);
			getListPanel().getHeadItem(JKBXHeaderVO.HKYBJE).setEnabled(true);
		}

		if(!StringUtils.isNullWithTrim(vo.getPrimaryKey())){
			Object ybye = getListPanel().getHeadColumnValue(JKBXHeaderVO.YBYE,JKBXHeaderVO.PK_JKBX,pk_jkd);
			if(ybye!=null)
				getListPanel().setHeadColumnValue(JKBXHeaderVO.YBYE,JKBXHeaderVO.PK_JKBX, pk_jkd, vo.ybje.add(new UFDouble(ybye.toString())));
		}

		resetHkje();
	}

	private void initialize() {
		try {
			setName("ContrastDialog");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(837, 490);
			setContentPane(getContentPanel());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		getBtnConfirm().addActionListener(this);
		getBtnCancel().addActionListener(this);
	}

	private nc.ui.pub.beans.UIPanel getQueryPanel() {
		if(queryPanel==null){
			try{
				queryPanel = new nc.ui.pub.beans.UIPanel();
				queryPanel.setName("queryPanel");
				queryPanel.setPreferredSize(new java.awt.Dimension(660, 50));
				queryPanel.setBounds(0,0,600, 50);
				queryPanel.setLayout(null);
				queryPanel.add(getUserlabel());
				queryPanel.add(getusertext());
				queryPanel.add(getfromtimelabel(),getfromtimelabel().getName());
				queryPanel.add(getfromtimetext(),getfromtimetext().getName());
				queryPanel.add(gettotimelabel(),gettotimelabel().getName());
				queryPanel.add(gettotimetext(),gettotimetext().getName());
				queryPanel.add(getBtnQuery(),getBtnQuery().getName());
			}catch(java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return queryPanel;
	}

	private UILabel getUserlabel(){
		if(loanuserlabel==null){
			try{
				loanuserlabel = new nc.ui.pub.beans.UILabel();
				loanuserlabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000249")/*@res "�����"*/);
				loanuserlabel.setBounds(40, 15, 60, 20);
			}catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return loanuserlabel;
	}
	private UIRefPane getusertext(){
		if(loanusertext==null){
			try{
				loanusertext = new nc.ui.pub.beans.UIRefPane();
				loanusertext.setName("loanuser");
				loanusertext.setRefNodeName("��Ա");
				loanusertext.setBounds(95, 15, 100, 20);
				loanusertext.setPk_org(getBxvo().getParentVO().getPk_org());

				//FIXME ������Ȩ����֮�����,�˴��൱���ӹ����������뵥�ݽ����ϵĽ����˵�ȡ�÷�����ͬ������Ӧ��Ҳ��ͬ
				String wherePart = BXUiUtil.getAgentWhereString(getBxvo().getParentVO().getDjlxbm(),BXUiUtil.getPk_user(),BXUiUtil.getSysdate().toString(),getBxvo().getParentVO().getDwbm());
				try {
					String whereStr = loanusertext.getRefModel().getWherePart();
					if(null != whereStr){
						whereStr+=wherePart;
					} else {
						whereStr=" 1=1 "+wherePart;
					}
					loanusertext.setWhereString(whereStr);
				} catch (ClassCastException e) {
				}
			}catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		loanusertext.setPk_org(getBxvo().getParentVO().getPk_org());
		return loanusertext;
	}

	private UILabel getfromtimelabel(){
		if(loanfromtimelabel==null){
			try{
				loanfromtimelabel = new nc.ui.pub.beans.UILabel();
				loanfromtimelabel.setBounds(270, 15, 60, 20);
				loanfromtimelabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000374")/*@res "���ʱ��"*/);
			}catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return loanfromtimelabel;
	}

	private UIRefPane getfromtimetext(){
		if(loanfromtimetext==null){
			try{
				loanfromtimetext = new nc.ui.pub.beans.UIRefPane();
				loanfromtimetext.setName("loanfromtime");
				loanfromtimetext.setRefNodeName("����");
				loanfromtimetext.setBounds(335, 15, 100, 20);
			}catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return loanfromtimetext;
	}
	private UILabel gettotimelabel(){
		if(loantotimelabel==null){
			try{
				loantotimelabel = new nc.ui.pub.beans.UILabel();
				loantotimelabel.setBounds(490, 15, 60, 20);
				loantotimelabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000375")/*@res "��"*/);
			}catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return loantotimelabel;
	}

	private UIRefPane gettotimetext(){
		if(loantotimetext==null){
			try{
				loantotimetext = new nc.ui.pub.beans.UIRefPane();
				loantotimetext.setName("loantotime");
				loantotimetext.setRefNodeName("����");
				loantotimetext.setBounds(527, 15, 100, 20);
			}catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return loantotimetext;
	}
	protected UIButton getBtnQuery() {
		if(loanquerybtn==null){
			loanquerybtn = new nc.ui.pub.beans.UIButton();
			loanquerybtn.setName("querybtn");
			loanquerybtn.setBounds(670, 15, 60, 20);
			loanquerybtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000204")/*@res "��ѯ"*/);
			loanquerybtn.addActionListener(this);

		}
		return loanquerybtn;
	}

	private javax.swing.JPanel getContentPanel() {
		if (ivjUIDialogContentPane == null) {
			try {
				ivjUIDialogContentPane = new javax.swing.JPanel();
				ivjUIDialogContentPane.setName("UIDialogContentPane");
				ivjUIDialogContentPane.setLayout(new java.awt.BorderLayout());
				//�����Ƿ������������˽�����
				boolean para = true;
				try {
//modified by chendya@ufida.com.cn ȡ��֯������
//					para = SysInit.getParaBoolean(BXUiUtil.getDefaultOrgUnit(), BXParamConstant.PARAM_IS_CONTRAST_OTHERS).booleanValue();
					final String pk_org = (String)mainPanel.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
					para = SysInit.getParaBoolean(pk_org, BXParamConstant.PARAM_IS_CONTRAST_OTHERS).booleanValue();
//--end
				} catch (java.lang.Throwable ivjExc) {
				}
				if(para){
					getContentPanel().add(getQueryPanel(), BorderLayout.NORTH);
				}
//				getContentPanel().add(getQueryPanel(), BorderLayout.NORTH);
				getContentPanel().add(getListPanel(), BorderLayout.CENTER);
				getContentPanel().add(getButtonPanel(), BorderLayout.SOUTH);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIDialogContentPane;
	}

	private nc.ui.pub.beans.UIPanel getButtonPanel() {
		if (buttonPanel == null) {
			try {
				buttonPanel = new nc.ui.pub.beans.UIPanel();
				buttonPanel.setName("buttonPanel");
				buttonPanel.setPreferredSize(new java.awt.Dimension(660, 50));
				buttonPanel.setLayout(null);
				buttonPanel.add(getBtnConfirm(), getBtnConfirm().getName());
				buttonPanel.add(getBtnCancel(), getBtnCancel().getName());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return buttonPanel;
	}

	/**
	 * @return��
	 *
	 * ������Ϣѡ��󣬷�����Ϣ�����к���ҵ����
	 */
	public List<BxcontrastVO> getContrastData(){

		List<BxcontrastVO> list=new ArrayList<BxcontrastVO>();

		JKBXHeaderVO head=null;

		for (int row = 0; row < getListPanel().getHeadBillModel().getRowCount(); row++) {
			if (getListPanel().getHeadBillModel().getValueAt(row,JKBXHeaderVO.SELECTED) == null)
				continue;
			if (((Boolean) (getListPanel().getHeadBillModel().getValueAt(row, JKBXHeaderVO.SELECTED))).booleanValue()) {

				AggregatedValueObject billValueVO = getListPanel().getBillValueVO(row,"nc.vo.ep.bx.BXVO","nc.vo.ep.bx.BXHeaderVO","nc.vo.ep.bx.BXBusItemVO");

				head=(JKBXHeaderVO) billValueVO.getParentVO();
				JKBXHeaderVO parentVO = bxvo.getParentVO();

				BxcontrastVO bxcontrastVO = new BxcontrastVO();
				bxcontrastVO.setYbje(head.getCjkybje());
				bxcontrastVO.setCjkybje(head.getCjkybje());
//begin-- V6������޸� modified by chendya 2011-12-05				
//				bxcontrastVO.setCxrq(BXUiUtil.getSysdate());
 				bxcontrastVO.setCxrq(BXUiUtil.getBusiDate());
 //--end				
				bxcontrastVO.setDeptid(head.getDeptid());
				bxcontrastVO.setDjlxbm(head.getDjlxbm());
				bxcontrastVO.setJkbxr(head.getJkbxr());
				bxcontrastVO.setJobid(head.getJobid());
				bxcontrastVO.setPk_bxd(parentVO.getPk_jkbx());
				bxcontrastVO.setPk_org(head.getPk_org());
				bxcontrastVO.setPk_jkd(head.getPk_jkbx());
				bxcontrastVO.setSxbz(BXStatusConst.SXBZ_NO);
				bxcontrastVO.setSxrq(null);
				bxcontrastVO.setSzxmid(head.getSzxmid());
				bxcontrastVO.setTs(head.getTs());
				bxcontrastVO.setHkybje(head.getHkybje()==null?UFDouble.ZERO_DBL:head.getHkybje());

				bxcontrastVO.setFyybje(bxcontrastVO.getCjkybje().sub(bxcontrastVO.getHkybje()));
//				bxcontrastVO.setFybbje(bxcontrastVO.getCjkybje().sub(bxcontrastVO.getHkybje()==null?UFDouble.ZERO_DBL:bxcontrastVO.getHkybje()));
				bxcontrastVO.setPk_bxcontrast(getListPanel().getHeadColumnValue(BxcontrastVO.PK_BXCONTRAST, row)==null?null:getListPanel().getHeadColumnValue(BxcontrastVO.PK_BXCONTRAST, row).toString());

				bxcontrastVO.setBxdjbh(parentVO.getDjbh());
				bxcontrastVO.setJkdjbh(head.getDjbh());
				list.add(bxcontrastVO);
			}
		}
		return list;
	}

	public ContrastDialog(java.awt.Frame owner) {
		super(owner);
	}

	private nc.ui.pub.beans.UIButton getBtnCancel() {
		if (btnCancel == null) {
			try {
				btnCancel = new nc.ui.pub.beans.UIButton();
				btnCancel.setName("BnCancel");
				btnCancel.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030201", "UC001-0000008")/* @res "ȡ��" */);
				btnCancel.setBounds(435, 15, 70, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnCancel;
	}

	private nc.ui.pub.beans.UIButton getBtnConfirm() {
		if (btnConfirm == null) {
			try {
				btnConfirm = new nc.ui.pub.beans.UIButton();
				btnConfirm.setName("BnConfirm");
				btnConfirm.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030201", "UC001-0000044")/* @res "ȷ��" */);
				btnConfirm.setBounds(355, 15, 70, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnConfirm;
	}

	@Override
	public String getTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000182")/*@res "����"*/;
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(getBtnConfirm())) {

			String errormsg = validateData();
			if(errormsg!=null){
				//mainPanel.showErrorMessage(errormsg);
				BXUiUtil.showUif2DetailMessage(this, null, errormsg);
				return;
			}
			closeOK();
			destroy();
		} else if (e.getSource().equals(getBtnCancel())) {
			closeCancel();
			destroy();
		}else if (e.getSource().equals(getBtnQuery())) {
			onBoquery();
		}
	}

	private String validateData() {
		UFDouble contrastTotal = getContrastTotal();
		UFDouble hkTotal = getHkTotal();
		UFDouble bxje = getBxje();

		if(contrastTotal.compareTo(bxje)>0){ //������ڱ������
			if(hkTotal.compareTo(contrastTotal.sub(bxje))!=0){
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000376")/*@res "������Ϣ���󣺺ϼƻ�����Ӧ���ڱ����ܽ��-�����ܽ��"*/;
			}
		}else{
			if(hkTotal.compareTo(new UFDouble(0))>0){
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000377")/*@res "������Ϣ���󣺳�����С�ڱ����ܽ����ܽ��л���"*/;
			}
		}
		return null;
	}

	private List<JKBXHeaderVO> getJkdsByQuery() throws BusinessException{
		List<JKBXHeaderVO> jkds=new ArrayList<JKBXHeaderVO>();
		String usertext = getusertext().getRefPK();
		UFDate fromtime = getfromtimetext().getDateBegin();
		UFDate totime = gettotimetext().getDateEnd();
		String errormsg = null;
		if(fromtime!=null&&fromtime.compareTo(totime) > 0){
			
			errormsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0044")/*@res "��ʼ���ڲ��ܴ��ڽ������ڣ�����������!"*/;
			BXUiUtil.showUif2DetailMessage(this, errormsg, errormsg);
			//mainPanel.showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0044")/*@res "��ʼ���ڲ��ܴ��ڽ������ڣ�����������!"*/);
		}
		if(usertext==null ||usertext.trim().equals("")){
			errormsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000378")/*@res "����ѡ����������"*/;
			BXUiUtil.showUif2DetailMessage(this, errormsg, errormsg);
			//mainPanel.showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000378")/*@res "����ѡ����������"*/);
		}
		else{
			String sql=" zb.jkbxr='"+usertext+"'";
			if(totime!=null && !"".equals(totime))
				sql+=" and zb.djrq<='"+totime+"'";
			if(fromtime!=null && !"".equals(fromtime))
				sql+=" and zb.djrq>='"+fromtime+"'";

			jkds = getBxUIControl().getJKD(bxvo.getParentVO(),BXUiUtil.getSysdate(),sql);
		}
		return jkds;
	}

	private void onBoquery() {
		Collection<JKBXHeaderVO> queryContrastVO=null;
		try {
			queryContrastVO = getJkdsByQuery();
		} catch (BusinessException e) {
			throw new BusinessRuntimeException(e.getMessage(),e);
		}
		if(queryContrastVO!=null){
			for(JKBXHeaderVO svo:queryContrastVO){
				boolean include=false;
				String newprimaryKey = svo.getPrimaryKey();
				for(SuperVO supervo:getJkds()){
					String primaryKey = supervo.getPrimaryKey();
					if(newprimaryKey.equals(primaryKey)){
						include=true;
					}
				}
				if(!include){
					getJkds().add(svo);
				}
			}
            //modified 2009-10-12
			//initJkds(getBxvo(),getJkds());
			initJkds(getBxvo(),(List<JKBXHeaderVO>) queryContrastVO);
		}
	}

	public void valueChanged(ValueChangedEvent event) {
	}

	private void handleException(java.lang.Throwable e) {
		ExceptionHandler.consume(e);;
	}

	public JKBXVO getBxvo() {
		return bxvo;
	}

	public void setBxvo(JKBXVO bxvo) {
		this.bxvo = bxvo;
	}

	public String getNodecode() {
		return nodecode;
	}

	public void setNodecode(String nodecode) {
		this.nodecode = nodecode;
	}

	public String getPkCorp() {
		return pkCorp;
	}

	public void setPkCorp(String pkCorp) {
		this.pkCorp = pkCorp;
	}

	public List<JKBXHeaderVO> getJkds() {
		return jkds;
	}

	public void setJkds(List<JKBXHeaderVO> bxvos) {
		this.jkds = bxvos;
	}

}