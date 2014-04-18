package nc.ui.arap.bx.proxyuser;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.funcnode.ui.action.AbstractNCAction;
import nc.itf.arap.prv.IproxyUserBillPrivate;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.ToftPanel;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.actions.ActionInfo;
import nc.ui.uif2.actions.ActionRegistry;
import nc.vo.ep.bx.SqdlrVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;

/**
 * ��Ȩ�����û�����������
 *
 * @author wdh 20081016
 *
 */
public class ProxyUserManagePanel extends ToftPanel {

	private ButtonObject[] buttonObjs;


	private String pk_psndoc = null;

	// Ȩ��ҵ��ί����
	// private RoleManageBusinessDelegator m_roleBsDelegator = null;

	// 5

	private BillListPanel m_ListPanel;

	// private BillListPanel m_ListPanel;

	private ButtonObject m_EditBtn = null;

	private ButtonObject m_AddLineBtn = null;

	private ButtonObject m_DelLineBtn = null;

	private ButtonObject m_SaveBtn = null;

	private ButtonObject m_refresh = null;

	private ButtonObject m_cancel = null;

	private IproxyUserBillPrivate pubpri = null;
	
	private String status = null;
	
	
	private List<SqdlrVO> delVOs = new ArrayList<SqdlrVO>();
	

	
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ProxyUserManagePanel() {
		super();
		initialize();
	}

	private void initialize() {
		setName("proxyusermanagepanel");
		setSize(774, 419);
		this.add(getBillListPanel(), "Center");
		intiBtn();
		try {
			getProxyUser();
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
			showErrorMessage(e.getMessage());
		}

	}

	private void intiBtn() {

		if (m_EditBtn == null)
			m_EditBtn = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000055")/*@res "�޸�"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000055")/*@res "�޸�"*/,5,"�޸�");	/*-=notranslate=-*/
		if (m_AddLineBtn == null)
			m_AddLineBtn = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000057")/*@res "����û�"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000057")/*@res "����û�"*/,5,"����û�");	/*-=notranslate=-*/
		if (m_DelLineBtn == null)
			m_DelLineBtn = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000059")/*@res "ɾ���û�"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000059")/*@res "ɾ���û�"*/,5,"ɾ���û�");	/*-=notranslate=-*/
		if (m_SaveBtn == null)
			m_SaveBtn = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000061")/*@res "����"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000061")/*@res "����"*/,5,"����");	/*-=notranslate=-*/
		if (m_refresh == null)
			m_refresh = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000063")/*@res "ˢ��"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000063")/*@res "ˢ��"*/,5,"ˢ��");	/*-=notranslate=-*/
		if(m_cancel == null)
			m_cancel = new ButtonObject(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000064")/*@res "ȡ��"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000064")/*@res "ȡ��"*/,5,"ȡ��");	/*-=notranslate=-*/
		buttonObjs = new ButtonObject[] { m_EditBtn, m_AddLineBtn,
				m_DelLineBtn, m_SaveBtn, m_refresh, m_cancel};
		setButtons(buttonObjs);
		initActions();
		setStatus(0);
	}
	
	//��ʼ����ť��ݼ���
	private void initActions() {
		List<Action> menuActions = getMenuActions();
		for(Action menu:menuActions){
			if(menu instanceof AbstractNCAction){
				AbstractNCAction action=(AbstractNCAction) menu;
				ActionInfo info = ActionRegistry.getActionInfo(action.getCode());
				if(info!=null){
					action.setCode(info.getCode());
					action.putValue(Action.ACCELERATOR_KEY, info.getKeyStroke());
					action.putValue(Action.SHORT_DESCRIPTION, info.getShort_description());
					action.putValue(Action.SMALL_ICON, info.getIcon());
				}
			}
		}
		setMenuActions(menuActions);
	}

	private BillListPanel getBillListPanel() {
		if (m_ListPanel == null) {
			m_ListPanel = new BillListPanel();
			m_ListPanel.loadTemplet("20110007", null, 
					WorkbenchEnvironment.getInstance().getLoginUser().getPrimaryKey(), 
					WorkbenchEnvironment.getInstance().getGroupVO().getPrimaryKey());

		}
		return m_ListPanel;
	}

	private String getOperatorUser() throws BusinessException {
		if(pk_psndoc == null){
			String cid = WorkbenchEnvironment.getInstance().getLoginUser().getPrimaryKey();
			try {
				pk_psndoc = NCLocator.getInstance().lookup(nc.pubitf.rbac.IUserPubService.class).queryPsndocByUserid(cid);
			} catch (ComponentException e2) {
				ExceptionHandler.consume(e2);
			} catch (BusinessException e2) {
				ExceptionHandler.consume(e2);
				throw e2;
			}
			if (pk_psndoc == null)
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000065")/*@res "����Ա��Ӧ��ҵ��ԱΪ�գ��˽ڵ㲻���ã�"*/);
		}
		return pk_psndoc;
	}

	/**
	 * ���ص�ǰ�û����õĴ����û�
	 *
	 * @param billpanel
	 */
	private void getProxyUser()  throws BusinessException {
		
		
		SqdlrVO[] vos = null; // �����û�vo����
		IproxyUserBillPrivate pubpri = getPubpri();
		try {
			// �����ݿ��еõ������û��б�
			String yuy = getOperatorUser();
			vos = pubpri.getProxyUser(yuy);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
			throw new BusinessException(e.getMessage());
		}
		if (vos != null)
			getBillListPanel().setBodyValueVO(vos);
		BillModel bm = getBillListPanel().getBodyBillModel();
		int rowCount = bm.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			bm.setRowState(i, VOStatus.UNCHANGED);
		}
		getBillListPanel().getBodyBillModel().execLoadFormula();

	}

	@Override
	public String getTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000066")/*@res "��Ȩ����"*/;
	}

	@Override
	public void onButtonClicked(ButtonObject bo) {
		try{
			if (m_AddLineBtn == bo) {
				SqdlrVO vo = new SqdlrVO();
				vo.setPk_org(WorkbenchEnvironment.getInstance().getGroupVO().getPrimaryKey());
				vo.setPk_user(getOperatorUser());
				vo.setType(1);
				getBillListPanel().getBodyBillModel().addLine();
				int rows = getBillListPanel().getBodyTable().getRowCount();
				getBillListPanel().getBodyBillModel().setBodyRowVO(vo, rows-1);
				status = "New";
			} else if (m_EditBtn == bo) {
				status="Edit";
				setStatus(1);
			} else if (m_DelLineBtn == bo) {
				status="Del";
				
				delVOs = delete();
				
			} else if (m_SaveBtn == bo) {
				save(delVOs);				
			} else if (m_refresh == bo) {
				getProxyUser();
			}
//			else if (m_selectAll == bo) {
//				selectStatus(true);
//			} else if (m_cancelAll == bo) {
//				selectStatus(false);
//			}
			else if (m_cancel == bo) {
				getProxyUser();
				setStatus(0);
			}
		}catch (BusinessException e){
			ExceptionHandler.consume(e);
			showErrorMessage(e.getMessage());
		}
	}

//	private void selectStatus(boolean b){
//		int rows = getBillListPanel().getBodyTable().getRowCount();
//		if (rows > 0) {
//			for (int i = 0; i < rows; i++) {
//				UICheckBox cbox = (UICheckBox) getBillListPanel()
//						.getBodyTable().prepareEditor(
//								getBillListPanel().getBodyTable().getCellEditor(i, 0), i,
//								0); // �õ���ѡ�����
////				cbox.doClick();
////				cbox.repaint();
//				cbox.setSelected(b);
//			}
//		}
//	}


	private void save(List<SqdlrVO> vo) throws BusinessException {
		CircularlyAccessibleValueObject[] vos = getBillListPanel()
				.getBodyBillModel().getBodyValueVOs(SqdlrVO.class.getName());
		
	
		if(null!=status && status.equals("New")){
		// ��֤
		for (int i = 0; i < vos.length; i++) {
			if (vos[i].getStatus() == VOStatus.NEW) {
				String mis = check((SqdlrVO) vos[i]);
				if(mis != null){
					showErrorMessage(mis);
					return;
				}
			}
		}
		try {
			getPubpri().saveProxyUser((SqdlrVO[]) vos);
			getProxyUser();
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000067")/*@res "�����û�ʧ��"*/);
		}
		
		}else if(null!=status && status.equals("Edit")){
			for (int i = 0; i < vos.length; i++) {
				if (vos[i].getStatus() == VOStatus.UPDATED) {
					String mis = check((SqdlrVO) vos[i]);
					if(mis != null){
						showErrorMessage(mis);
						return;
					}
				}
		}
		try {
			getPubpri().saveProxyUser((SqdlrVO[]) vos);
			getProxyUser();
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000067")/*@res "�����û�ʧ��"*/);
			}
		}
		else if(null!=status && status.equals("Del")){
			deletebody(vo);
		}
		setStatus(0);
	}
	private void deletebody(List<SqdlrVO> vo)  throws BusinessException {
		//ԭ����
		//int rows = getBillListPanel().getBodyTable().getRowCount();
		
		int rows = vo.size();
		
		

		if (rows > 0) {
		
				if (!vo.isEmpty()) {
					try {
						getPubpri().delProxyuser(vo);
					} catch (BusinessException e) {
						ExceptionHandler.consume(e);
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000069")/*@res "ɾ���û�ʧ��"*/);
					}
			}

		}
	}
	private List<SqdlrVO> delete()  throws BusinessException {
		int rows = getBillListPanel().getBodyTable().getRowCount();
		List<SqdlrVO> sqdlrVOs = new ArrayList<SqdlrVO>();
//		String num = null;
		ArrayList<Integer> alnum = new ArrayList<Integer>();
		if (rows > 0) {
			for (int i = 0; i < rows; i++) {
				UICheckBox cbox = (UICheckBox) getBillListPanel()
						.getBodyTable().prepareEditor(
								getBillListPanel().getBodyTable().getCellEditor(i, 0), i,
								0); // �õ���ѡ�����
				if (cbox.isSelected()) {
					SqdlrVO vo = (SqdlrVO) getBillListPanel().getBodyBillModel()
							.getBodyValueRowVO(i, SqdlrVO.class.getName()); // �õ�ѡ���е�voֵ
					sqdlrVOs.add(vo);
					getBillListPanel().getBodyTable().clearSelection();
					
//					if (num == null)
//						num = Integer.toString(i);
//					else
//						num = num + ";" + Integer.toString(i);
					alnum.add(i);
				}
			}
			// ɾ��ѡ�е���
			if (alnum == null || alnum.size()==0)
				showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000068")/*@res "������ѡ��һ�У�"*/);
			else {
				if (!sqdlrVOs.isEmpty()) {
					try {
						
						//getPubpri().delProxyuser(sqdlrVOs);
					} catch (Exception e) {
						ExceptionHandler.consume(e);
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000069")/*@res "ɾ���û�ʧ��"*/);
					}
				}
				delLine(alnum);
			}
			

		}
		return sqdlrVOs;
	}

	/**
	 * ��֤vo
	 * @param vo
	 * @return
	 */
	private String check(SqdlrVO vo) {
		String mis = null;
		if (vo.getPk_operator() == null) {
			mis = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000070")/*@res "����Ա����Ϊ��"*/;
		} else if (vo.getStartdate() == null) {
			mis = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000071")/*@res "��ʼ���ڲ���Ϊ��"*/;
		} else if (vo.getEnddate() == null) {
			mis = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000072")/*@res "�������ڲ���Ϊ��"*/;
		} else if (vo.getBilltype() == null) {
			mis = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000073")/*@res "�������Ͳ���Ϊ��"*/;
		} else if (vo.getStartdate().compareTo(vo.getEnddate()) > 0) {
			mis = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000074")/*@res "��ʼ���ڲ��ܴ��ڽ�������"*/;
		}
		return mis;
	}

	/**
	 * ��BillListPanelɾ����
	 * @param lines
	 */
	private void delLine(ArrayList<Integer> num) {
//		String[] snum = lines.split(";");
//		int[] delline = new int[snum.length];
//		for (int i = 0; i < delline.length; i++) {
//			delline[i] = Integer.parseInt(snum[i]);
//		}
		Integer[]delline = num.toArray(new Integer[0]);
		int[] dline = new int[delline.length];
		for(int i=0;i<delline.length;i++){
			dline[i] = delline[i];
		}
		getBillListPanel().getBodyBillModel().delLine(dline);
	}


//	private ButtonObject getM_AddLineBtn() {
//		return m_AddLineBtn;
//	}

	public void setStatus(int status) {
		if (status == 0) { //��ʼ״̬
			m_EditBtn.setEnabled(true);
			m_AddLineBtn.setEnabled(false);
			m_DelLineBtn.setEnabled(false);
			m_SaveBtn.setEnabled(false);
			m_refresh.setEnabled(true);
//			m_selectAll.setEnabled(true);
//			m_cancelAll.setEnabled(true);
			m_cancel.setEnabled(false);
			getBillListPanel().setEnabled(false);
			getBillListPanel().getBodyItem("selectflag").setEnabled(true);
		}
		if (status == 1) {  //�༭״̬
			m_EditBtn.setEnabled(false);
			m_AddLineBtn.setEnabled(true);
			m_DelLineBtn.setEnabled(true);
			m_SaveBtn.setEnabled(true);
			m_refresh.setEnabled(false);
//			m_selectAll.setEnabled(false);
//			m_cancelAll.setEnabled(false);
			m_cancel.setEnabled(true);
			m_ListPanel.setEnabled(true);
			getBillListPanel().setEnabled(true);
		}
		updateButtons();
	}

	public IproxyUserBillPrivate getPubpri() {
		if(pubpri == null )
			pubpri = NCLocator.getInstance().lookup(IproxyUserBillPrivate.class);
		return pubpri;
	}



}