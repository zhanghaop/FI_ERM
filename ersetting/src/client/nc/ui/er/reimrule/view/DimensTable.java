package nc.ui.er.reimrule.view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.Action;

import nc.bs.erm.util.MDPropertyRefPane;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.er.reimtype.IReimTypeService;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IBean;
import nc.md.model.MetaDataException;
import nc.ui.bd.pub.BDOrgPanel;
import nc.ui.bd.ref.RefInfoHelper;
import nc.ui.er.reimrule.ReimRuleUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.components.IComponentWithActions;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.bd.ref.RefInfoVO;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;

public class DimensTable extends BatchBillTable implements IComponentWithActions {

	private static final long serialVersionUID = 5732479916714526380L;

	private int DEFNO = 20;
	private String DEF = "DEF";
	private String djlx = null;
	private List<Action> actions = null;
	// ����ѡ��������
	UIComboBox dtRefPane = null;
	private BDOrgPanel orgPanel = null;
	// �������Ͳ���map
	private Map<String, RefInfoVO[]> map = new HashMap<String, RefInfoVO[]>();

	// ������Ԫ����ѡ��Ի���
	private MDPropertyRefPane bxBillRefPane = null;
	/* ������Ԫ����ID */
	private static String bx_beanid = "d9b9f860-4dc7-47fa-a7d5-7a5d91f39290";

	// ��Ԫ����ѡ��Ի���
	private MDPropertyRefPane jkBillRefPane = null;
	/* ��Ԫ����ID */
	private static String jk_beanid = "e0499b58-c604-48a6-825b-9a7e4d6dacca";

	// ��������Ӧ��Ԫ���ݱ༭��
	public MDPropertyRefPane getBXBillRefPane() {
		if (bxBillRefPane == null) {
			bxBillRefPane = new MDPropertyRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0011")/*
																																	 * @
																																	 * res
																																	 * "��Դ�����ֶ�"
																																	 */, bx_beanid, null);
		}
		return bxBillRefPane;
	}

	// ����Ӧ��Ԫ���ݱ༭��
	public MDPropertyRefPane getJKBillRefPane() {
		if (jkBillRefPane == null) {
			jkBillRefPane = new MDPropertyRefPane(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0", "0201100-0011")/*
																																	 * @
																																	 * res
																																	 * "��Դ�����ֶ�"
																																	 */, jk_beanid, null);
		}

		return jkBillRefPane;
	}

	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if (event.getType() == AppEventConst.MODEL_INITIALIZED) {
			// ���ݵ�ǰ�ĵ����������õ��ݶ�Ӧ��Ӧ�ò��ձ��������ǽ�
			djlx = getOrgPanel().getRefPane().getUITextField().getValue().toString();
			// ���ݶ�Ӧ��༭��
			if (djlx == null)
				return;
			if (djlx.startsWith("263"))
				getBillCardPanel().getBodyPanel().setTableCellEditor(ReimRuleDimVO.BILLREF, new BillCellEditor(getJKBillRefPane()));
			else
				getBillCardPanel().getBodyPanel().setTableCellEditor(ReimRuleDimVO.BILLREF, new BillCellEditor(getBXBillRefPane()));
		}
	}

	public void initUI() {
		super.initUI();
		// ����ѡ��������༭��
		getBillCardPanel().getBodyPanel().setTableCellEditor(ReimRuleDimVO.REFERENTIAL, new BillCellEditor(getDtRefPane()));
	}

	public ReimRuleDimVO[] checkReimDims(ReimRuleDimVO[] reimDimVOs, String pk_billtype, String pk_group, String pk_org) throws BusinessException {
		List<ReimRuleDimVO> returnVOs = new ArrayList<ReimRuleDimVO>();
		List<String> keys = new ArrayList<String>();
		int currow = 0;
		int controlflags = 0;
		for (ReimRuleDimVO dim : reimDimVOs) {
			currow++;
			if (dim.getDisplayname() == null && dim.getDatatype() == null && dim.getReferential() == null && dim.getBillref() == null && dim.getShowflag().equals(UFBoolean.FALSE)
					&& dim.getControlflag().equals(UFBoolean.FALSE)) {
				continue;
			}
			String displayname = dim.getDisplayname();
			if (displayname == null) {
				// throw new BusinessException("ά�����ñ�����д��ʾ����(��" + currow +
				// "��)������ʧ��!");
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2",
						"22011rr-000048", null, new String[] { String.valueOf(currow) })/*
																						 * @
																						 * res
																						 * "ά�����ñ�����д��ʾ����(��{0}��)������ʧ��!"
																						 */);
			} else {
				if (keys.contains(displayname)) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2",
							"22011rr-000049")/**
					 * @* res*"ά�������а���������ͬ�ļ�¼������ʧ��!"
					 */
					);
				} else {
					keys.add(displayname);
				}
			}
			if (dim.getDatatype() == null) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2",
						"22011rr-000050", null, new String[] { String.valueOf(currow) })/*
						 * @
						 * res
						 * "ά�����ñ�����д��������(��{0}��)������ʧ��!"
						 */);
			}
			if (dim.getControlflag().booleanValue())
				controlflags++;
			if (controlflags > 1) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2",
						"22011rr-000051")/**
				 * @* res*"���Ŀ��������ֻ����һ�"
				 */
				);
			}
			dim.setPk_billtype(pk_billtype);
			dim.setPk_group(pk_group);
			dim.setPk_org(pk_org);
			dim.setOrders(currow);
			returnVOs.add(dim);
		}
		if (controlflags == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2",
					"22011rr-000052")/**
			 * @* res*"���Ŀ��������ѡ��һ���ѡ��"
			 */
			);
		}
		return returnVOs.toArray(new ReimRuleDimVO[0]);
	}

	public List<SuperVO> Save() throws Exception {
		super.beforeSave();
		String currentBodyTableCode = getBillCardPanel().getCurrentBodyTableCode();
		ReimRuleDimVO[] reimDimVOs = (ReimRuleDimVO[]) getBillCardPanel().getBillData().getBodyValueVOs(currentBodyTableCode, ReimRuleDimVO.class.getName());

		String[] str = djlx.split(";");
		// ���ά��ֵ�������뵥�����͡���֯���Ӧ��
		ReimRuleDimVO[] returnvo = checkReimDims(reimDimVOs, str[0], getModel().getContext().getPk_group(), str[1]);
		// ����
		List<ReimRuleDimVO> returnVos = NCLocator.getInstance().lookup(IReimTypeService.class).saveReimDim(str[0], getModel().getContext().getPk_group(), str[1], returnvo);
		List<SuperVO> list = new ArrayList<SuperVO>();
		list.addAll(returnVos);
		ReimRuleUtil.putDim(str[0], list);
		return list;
	}

	// ����ѡ��������༭��
	private UIComboBox getDtRefPane() {
		if (dtRefPane == null) {
			dtRefPane = new UIComboBox();
			dtRefPane.setPreferredSize(new Dimension(150, 22));
		}
		return dtRefPane;
	}

	public void addRef(BillEditEvent e) {
		Object value = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.CORRESPONDINGITEM);
		if (!nc.vo.arap.utils.StringUtil.isNullWithTrim((String) value))
			return;
		/**
		 * Ԥ���߸�Ĭ�ϵı�׼�����������ʾ��������Щֵʱ����Ӧ����ģ���ϵ���һ��
		 */
		String[] names = new String[] {
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000053")/**
				 * @*
				 * res*"��������"
				 */
				,

				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000054")/**
				 * @*
				 * res*"��������"
				 */
				,

				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000055")/**
				 * @*
				 * res*"����"
				 */
				,

				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000056")/**
				 * @*
				 * res*"ְλ"
				 */
				,

				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000057")/**
				 * @*
				 * res*"����"
				 */
				,

				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000058")/**
				 * @*
				 * res*"���"
				 */
				,

				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000059") /**
				 * @*
				 * res*"��ע"
				 */

		};
		String[] items = new String[] { "PK_EXPENSETYPE", "PK_REIMTYPE", "PK_DEPTID", "PK_POSITION", "PK_CURRTYPE", "AMOUNT", "MEMO" };
		// �жϽ����ϵ���ʾ�����Ƿ�������Ԥ�Ʒ�Χ�ڣ������ڵ���ģ��Ķ�Ӧ���������
		String displayname = (String) e.getValue();
		boolean flag = false;
		for (int j = 0; j < names.length && flag == false; j++) {
			if (displayname.equals(names[j])) {
				getBillCardPanel().setBodyValueAt(items[j], e.getRow(), ReimRuleDimVO.CORRESPONDINGITEM);
				flag = true;
			}
		}
		// �������Ԥ�Ʒ�Χ�ڣ�����Ҫȫ�����ж�Ӧ�ö�Ӧ�ĸ��Զ�����
		if (flag == false) {
			String currentBodyTableCode = getBillCardPanel().getCurrentBodyTableCode();
			CircularlyAccessibleValueObject[] bodyValueVOs = getBillCardPanel().getBillData().getBodyValueVOs(currentBodyTableCode, ReimRuleDimVO.class.getName());
			ReimRuleDimVO[] reimDimVOs = (ReimRuleDimVO[]) bodyValueVOs;
			List<String> list = new ArrayList<String>();
			for (ReimRuleDimVO rule : reimDimVOs) {
				list.add(rule.getCorrespondingitem());
			}
			for (int i = 0; i < DEFNO && flag == false; i++) {
				String tmp = DEF + (i + 1);
				if (!list.contains(tmp)) {
					getBillCardPanel().setBodyValueAt(tmp, e.getRow(), ReimRuleDimVO.CORRESPONDINGITEM);
					flag = true;
				}
			}
		}
		// ���Ҳ�����Զ������У������
		if (flag == false) {
			MessageDialog.showHintDlg(null,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000060")/**
					 * @*
					 * res*"�е���������"
					 */
					, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000061")/**
					 * @*
					 * res*"�Զ����е���������Ϊ20!"
					 */);
		}
	}

	private void fillDiaplayName(BillEditEvent e) {
		String entityid = null;
		if (e.getSource() != null && (e.getSource() instanceof BillCellEditor)) {
			java.awt.Component component = ((BillCellEditor) e.getSource()).getComponent();
			if (component != null && (component instanceof UIRefPane))
				entityid = ((UIRefPane) component).getRefPK();
		}
		if (entityid == null || entityid.trim().length() == 0)
			return;
		IBean bean = null;
		try {
			bean = MDBaseQueryFacade.getInstance().getBeanByID(entityid);
		} catch (MetaDataException e1) {
			// ����ѡ���ʼ��ʧ�ܣ���������ֻ��¼��־
			MessageDialog.showHintDlg(null, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000062")/**
					 * @*
					 * res*"�������ʹ�"
					 */
					, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000063")/**
					 * @*
					 * res*"�������� ����!"
					 */);
			Logger.error(e1.getMessage(), e1);
		}
		Object name = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.DISPLAYNAME);
		if (name == null && bean != null) {
			getBillCardPanel().setBodyValueAt(bean.getDisplayName(), e.getRow(), ReimRuleDimVO.DISPLAYNAME);
			addRef(e);
		}
		// Object value = getBillCardPanel().getBodyItem(e.getTableCode(),
		// e.getKey()).getValueObject();
		// setDataStyle(e.getRow(),entityid);
		getBillCardPanel().setBodyValueAt(null, e.getRow(), ReimRuleDimVO.DATATYPENAME);
		getBillCardPanel().setBodyValueAt(null, e.getRow(), ReimRuleDimVO.BEANNAME);
		if (entityid.equals("BS000010000100001031") || entityid.equals("BS000010000100001052"))
			getBillCardPanel().setBodyValueAt(2, e.getRow(), ReimRuleDimVO.DATATYPENAME);
		if (bean != null) {
			String beanName = bean.getName();
			getBillCardPanel().setBodyValueAt(bean.getDisplayName(), e.getRow(), ReimRuleDimVO.DATATYPENAME);
			getBillCardPanel().setBodyValueAt(beanName, e.getRow(), ReimRuleDimVO.BEANNAME);
			RefInfoVO[] infoVOs = map.get(beanName);
			if (infoVOs == null || infoVOs.length == 0) {
				infoVOs = RefInfoHelper.getInstance().getRefinfoVOs(beanName);
				if (infoVOs != null && infoVOs.length > 0) {
					map.put(beanName, infoVOs);
				}
			}
		}
	}

	private void initDtInfoData(BillEditEvent e) {
		// ���ԭ��ֵ
		getDtRefPane().removeAllItems();
		Object beanname = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.BEANNAME);
		if (beanname == null)
			return;
		String value = beanname.toString();
		// Object value = getBillCardPanel().getBodyItem(e.getTableCode(),
		// e.getKey()).getValueObject();
		RefInfoVO[] infoVOs = map.get(value);
		if (infoVOs == null || infoVOs.length == 0) {
			infoVOs = RefInfoHelper.getInstance().getRefinfoVOs(value);
			if (infoVOs != null && infoVOs.length > 0) {
				map.put(value, infoVOs);
			}
		}
		if (infoVOs != null && infoVOs.length > 0) {
			getDtRefPane().addItems(infoVOs);
		}
	}

	public boolean beforeEdit(BillEditEvent e) {
		Object name = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.DISPLAYNAME);
		if (name != null && (name.toString().equals("����") || name.toString().equals("���"))) {
			MessageDialog.showHintDlg(null, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000064")/**
					 * @*
					 * res*"���������"
					 */
					, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_2", "22011rr-000065")/**
					 * @*
					 * res*"���������в��������!"
					 */);
		}
		// ����ѡ����
		if (e.getKey().equals(ReimRuleDimVO.REFERENTIAL)) {
			initDtInfoData(e);
			return true;
		} else if (e.getKey().equals(ReimRuleDimVO.BILLREF)) {
			MDPropertyRefPane mdref = null;
			Vector<Object> vecSelectedData = new Vector<Object>();
			mdref = djlx.startsWith("263") ? getJKBillRefPane() : getBXBillRefPane();
			String showCode = null;
			String showName = null;
			if (getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.BILLREFCODE) != null)
				showCode = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.BILLREFCODE).toString();
			if (getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.BILLREF) != null)
				showName = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.BILLREF).toString();
			if (mdref != null) {
				if (showCode != null && showName != null) {
					String[] codeArray = showCode.split(",");
					String[] nameArray = showName.split(",");
					for (int nPos = 0; nPos < codeArray.length; nPos++) {
						Vector<String> row = new Vector<String>();
						row.add(codeArray[nPos]);
						row.add(nameArray[nPos]);
						vecSelectedData.add(row);
					}
				}
				mdref.getRefModel().setSelectedData(vecSelectedData);
				mdref.getDialog().getEntityTree().setSelectionPath(null);
			}
		}
		return true;
	}

	/**
	 * ʵ�ֱ༭����߼�
	 * 
	 * @param e
	 */
	@SuppressWarnings("unchecked")
	protected void doAfterEdit(BillEditEvent e) {
		super.doAfterEdit(e);
		Object value = getBillCardPanel().getBodyValueAt(e.getRow(), e.getKey());
		// ��ʾ�����У��༭�����Ҫд���Ӧ��
		if (e.getKey().equals(ReimRuleDimVO.DISPLAYNAME)) {
			if (value == null)
				return;
			addRef(e);
		}
		// ���������У��༭�����Ҫ�����������͵�ֵ��DATATYPENAME��
		else if (e.getKey().equals(ReimRuleDimVO.DATATYPE)) {
			fillDiaplayName(e);
		}
		// ����ѡ����
		else if (e.getKey().equals(ReimRuleDimVO.REFERENTIAL)) {
			if (value == null)
				return;
			getBillCardPanel().setBodyValueAt(((RefInfoVO) value).getName(), e.getRow(), ReimRuleDimVO.DATATYPENAME);
		}
		// ���༭��Ϊ���ݶ�Ӧ���У��༭�����Ҫд�뵥�ݶ�Ӧ����룬�Ա㱨����׼���õ�����ĵ�����
		else if (e.getKey().equals(ReimRuleDimVO.BILLREF)) {
			Object name = getBillCardPanel().getBodyValueAt(e.getRow(), ReimRuleDimVO.DISPLAYNAME);
			if (name.toString().equals("����")) {
				getBillCardPanel().setBodyValueAt("����", e.getRow(), ReimRuleDimVO.BILLREF);
				getBillCardPanel().setBodyValueAt("bzbm", e.getRow(), ReimRuleDimVO.BILLREFCODE);
			} else {
				getBillCardPanel().setBodyValueAt(null, e.getRow(), ReimRuleDimVO.BILLREFCODE);
				Vector vec;
				if (djlx.startsWith("263"))
					vec = getJKBillRefPane().getRefModel().getSelectedData();
				else
					vec = getBXBillRefPane().getRefModel().getSelectedData();
				if (vec != null) {
					for (Object object : vec) {
						String fieldcode = ((Vector) object).get(0).toString();
						getBillCardPanel().setBodyValueAt(fieldcode, e.getRow(), ReimRuleDimVO.BILLREFCODE);
					}
				}
			}
		}
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public BDOrgPanel getOrgPanel() {
		return orgPanel;
	}

	public void setOrgPanel(BDOrgPanel orgPanel) {
		this.orgPanel = orgPanel;
	}
}