package nc.ui.arap.bx.loancontrol;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.border.TitledBorder;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.prv.IArapCommonPrivate;
import nc.ui.er.djlx.BillTypeRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.common.CommonCard;
import nc.ui.erm.common.CommonModel;
import nc.ui.erm.common.CommonUI;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRadioButton;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.ref.IFilterStrategy;
import nc.vo.ep.bx.LoanControlModeDefVO;
import nc.vo.ep.bx.LoanControlModeVO;
import nc.vo.ep.bx.LoanControlSchemaVO;
import nc.vo.ep.bx.LoanControlVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

/**
 * @author twei
 *
 * nc.ui.arap.bx.loancontrol.LoanControlCard
 *
 * 借款控制设置卡片界面
 *
 * @see CommonCard
 */
public class LoanControlCard extends CommonCard implements ValueChangedListener {

	private CommonUI parentUI;

	@Override
	public CommonUI getParentUI() {
		return parentUI;
	}

	@Override
	public void setParentUI(CommonUI parentUI) {
		this.parentUI = parentUI;
	}

	private static final String FIELD = "@field@"; // @jve:decl-index=0:

	private static final long serialVersionUID = -7673593385591793835L;

	private UIPanel jPanel = null;

	private UIPanel jPanel1 = null;

	private UIPanel jPanel2 = null;

	private UITextField jTextField = null;

	private UITextField jTextField1 = null;

	private UILabel jLabel = null;

	private UILabel jLabel1 = null;

	private UILabel jLabel2 = null;

	private final List<UIRadioButton> controlAttribute_Comp = new ArrayList<UIRadioButton>();

	private final List<List<JComponent>> controlMode_Comp = new ArrayList<List<JComponent>>();

	private final List<String> controlAttribute_Value = new ArrayList<String>();

	private final List<String> controlMode_Value = new ArrayList<String>();

	private UILabel jLabel3 = null;

	private UIRadioButton jRadioButton3 = null;

	private UIRadioButton jRadioButton4 = null;

	private UIRefPane ivjtOrg;

	private UIRefPane ivjtdjlx;

	private UIRefPane ivjtjsfs;

	private UILabel jLabelOrg = null;

	private UILabel jLabel6 = null;

	private UILabel jLabel7 = null;

	private UIPanel jPanel3 = null;

	private UIPanel jPanel4 = null;

	private UIPanel jPanel5 = null;

	private UIPanel jPanel6 = null;

	private UILabel jLabe3;

	private UIRefPane ivjtbz;

	private UICheckBox ivjUIcheckBB;



	private UIRefPane getRefbzbm()  {

		if (ivjtbz == null) {
			ivjtbz = new UIRefPane();
			ivjtbz.setName("ivjtbz");
			ivjtbz.setRefNodeName("币种档案"); /*-=notranslate=-*/
			ivjtbz.setMultiSelectedEnabled(false);
			ivjtbz.setFocusable(false);
			ivjtbz.addValueChangedListener(this);
		}
		return ivjtbz;

	}

	private UICheckBox getIvjUIcheckBB() {
		if(ivjUIcheckBB==null){
			ivjUIcheckBB=new UICheckBox();
			ivjUIcheckBB.setName("uivjUIcheckBB");
//			ivjUIcheckBB.setSize(500, 163);
			ivjUIcheckBB.setPreferredSize(new Dimension(200,20));
//			ivjUIcheckBB.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000034")/*@res "按本位币控制"*/);
			// 集团级控制规则节点也按组织本币控制
//			if(isGroup()){
//				ivjUIcheckBB.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0022")/*@res "按集团本币控制"*/);
//			} else {
				ivjUIcheckBB.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0023")/*@res "按组织本币控制"*/);
//			}
		}
		return ivjUIcheckBB;
	}

	public LoanControlCard() {
	}

	private boolean isGroup() {
		return ((LoanControlMailPanel)getParentUI()).isGroup();
	}

	@Override
	public void initUI(){
		try{
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(3);
			gridLayout.setColumns(1);
			this.setLayout(gridLayout);
			this.setSize(new Dimension(639, 563));
			this.add(getUIPanel(), null);
			this.add(getUIPanel1(), null);
			this.add(getUIPanel2(), null);
			getUIPanel().setBorder(new TitledBorder(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000035")/*@res "基本"*/));
			getUIPanel1().setBorder(new TitledBorder(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000036")/*@res "控制方式"*/));
			getUIPanel2().setBorder(new TitledBorder(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000037")/*@res "高级"*/));
		}catch (Exception e) {
			ExceptionHandler.consume(e);
		}

		setName("LoanControlCard");
	}

	@Override
	public void setVO(SuperVO vo) {

		if (vo == null) {
			setCVO();
		} else {
			setCVO();
			setAVO(vo);
		}
	}

	private void setCVO() {

		getUIRadioButton4().setSelected(true);
		getUITextField1().setText(null);
		getUITextField().setText(null);
		for (int i = 0; i < controlAttribute_Value.size(); i++) {
			controlAttribute_Comp.get(i).setSelected(false);
		}

		getRefOrg().getRefModel().setSelectedData(null);
		getRefjsfs().getRefModel().setSelectedData(null);
		getRefdjlx().getRefModel().setSelectedData(null);
		getRefbzbm().getRefModel().setSelectedData(null);

		//列表选择的业务单元传入
		String refPK = parentUI.getListPanel().getRefOrg().getRefPK();

		//默认组织
		final String defaultOrg = BXUiUtil.getBXDefaultOrgUnit();
		getRefOrg().setPK(refPK!=null? refPK : defaultOrg);
		
		getRefjsfs().setValue(null);
		getRefdjlx().setValue(null);
		getRefbzbm().setValue(null);
		getIvjUIcheckBB().setSelected(false);

		for (int i = 0; i < controlMode_Comp.size(); i++) {
			List<JComponent> name2 = controlMode_Comp.get(i);
			for (Iterator<JComponent> iter = name2.iterator(); iter.hasNext();) {
				JComponent element = iter.next();
				if (element instanceof UICheckBox) {
					UICheckBox check = (UICheckBox) element;
					check.setSelected(false);
				}if (element instanceof UITextField) {
					UITextField check = (UITextField) element;
					check.setText(null);
				}
			}

		}
	}

	private void setAVO(SuperVO vo) {
		LoanControlVO controlVO = (LoanControlVO) vo;
		String controlattr = controlVO.getControlattr();
		int controlstyle = controlVO.getControlstyle();
		String paracode = controlVO.getParacode();
		String paraname = controlVO.getParaname();
		List<LoanControlSchemaVO> schemavos = controlVO.getSchemavos();
		List<LoanControlModeVO> modevos = controlVO.getModevos();
		int isBBControl = controlVO.getBbcontrol();

		if(isBBControl==0){
			getIvjUIcheckBB().setSelected(false);
		}else{
			getIvjUIcheckBB().setSelected(true);
		}
        getRefOrg().setPK(controlVO.getPk_org());
		getRefbzbm().setPK(controlVO.getCurrency());

		if(controlstyle==0){
			getUIRadioButton3().setSelected(true);
		}else{
			getUIRadioButton4().setSelected(true);
		}

		getUITextField1().setText(paracode);
		getUITextField().setText(paraname);

		for (int i = 0; i < controlAttribute_Value.size(); i++) {
			if(controlAttribute_Value.get(i).equals(controlattr)){
				controlAttribute_Comp.get(i).setSelected(true);
				break;
			}
		}

		List<String> balatypes=new ArrayList<String>();
		List<String> djlxbms=new ArrayList<String>();
		for (Iterator<LoanControlSchemaVO> iter = schemavos.iterator(); iter.hasNext();) {
			LoanControlSchemaVO schema = iter.next();
			if(schema.getBalatype() != null){
				balatypes.add(schema.getBalatype());
			}
			djlxbms.add(schema.getDjlxbm());
		}

		getRefdjlx().setPKs(djlxbms.toArray(new String[]{}));
		getRefjsfs().setPKs(balatypes.toArray(new String[]{}));

		Map<String,LoanControlModeVO> keyMaps=new HashMap<String, LoanControlModeVO>();
		for (Iterator<LoanControlModeVO> iter = modevos.iterator(); iter.hasNext();) {
			LoanControlModeVO modVO = iter.next();
			keyMaps.put(modVO.getPk_controlmodedef(),modVO);
		}

		for (int i = 0; i < controlMode_Value.size(); i++) {
			String key = controlMode_Value.get(i);
			if(keyMaps.containsKey(key)){
				List<JComponent> comp = controlMode_Comp.get(i);
				((UICheckBox)comp.get(0)).setSelected(true);
				if(comp.size()>1){
					((UITextField)comp.get(1)).setText(keyMaps.get(key).getValue().toString());
				}
			}
		}
	}

	@Override
	public SuperVO getVO() throws BusinessException{
		LoanControlVO controlVO = new LoanControlVO();
		if(!isGroup()){
			controlVO.setPk_org(getRefOrg().getRefPK());
		}else{
			controlVO.setPk_org(null);
		}


		controlVO.setPk_group(BXUiUtil.getPK_group());

		controlVO.setControlstyle(getUIRadioButton4().isSelected() ? 1 : 0);
		controlVO.setBbcontrol(getIvjUIcheckBB().isSelected() ? 1 : 0);
		controlVO.setParacode(getUITextField1().getText());
		controlVO.setParaname(getUITextField().getText());

		for (int i = 0; i < controlAttribute_Comp.size(); i++) {
			if(controlAttribute_Comp.get(i).isSelected()){
				controlVO.setControlattr(controlAttribute_Value.get(i));
				break;
			}
		}

		List<LoanControlSchemaVO> schemavos=new ArrayList<LoanControlSchemaVO>();
		String[] djlxPKs = getRefdjlx().getRefPKs();
		String[] jsfsPKs = getRefjsfs().getRefPKs();
		String pk_org = getRefOrg().getRefPK();
		getRefbzbm().stopEditing();
		String bzbmPK = getRefbzbm().getRefPK();

		
		String name= (String) controlVO.getAttributeValue("paraname");
		String code1=(String) controlVO.getAttributeValue("paracode");
		
		StringBuffer notNullErrMsg = new StringBuffer();
		if((name == null || "".equals(name)) && (code1 == null || "".equals(code1))){
			notNullErrMsg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0024")/*@res "需要录入名称和编码"*/);
		}else if(name == null || "".equals(name)){
			notNullErrMsg.append("\n");
			notNullErrMsg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0025")/*@res "需要录入名称"*/);
		}else if(code1 == null || "".equals(code1)){
			notNullErrMsg.append("\n");
			notNullErrMsg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0026")/*@res "需要录入编码"*/);
		}
		
		IArapCommonPrivate impl = (IArapCommonPrivate) NCLocator.getInstance().lookup(IArapCommonPrivate.class.getName());
		
		//要将集团下的(组织和集团的值)都要查询出来，判断集团和组织、组织和组织、集团和集团之间的编码、名称的关系
		String whereStr="pk_group='"+BXUiUtil.getPK_group()+"'";
		Collection<SuperVO> vos = impl.getVOs(parentUI.getVoClass(), whereStr, true);
	    for(Object svo:vos){
//begin--modified by Chen deyin(chendya@ufida.com.cn)
//Note: 新增保存时才校验编码，名称重复性
//			if(code1.equals(((LoanControlVO) svo).getParacode())){
//				throw new BusinessException("编码重复，请重新输入！");
//			}
//			if(name.equals(((LoanControlVO) svo).getParaname())){
//				throw new BusinessException("名称重复，请重新输入！");
//
//			}
			if(getParentUI().getModel().getStatus() == CommonModel.STATUS_ADD){
				if(code1.equals(((LoanControlVO) svo).getParacode())){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0027")/*@res "编码重复，请重新输入！"*/);
				}
				if(name.equals(((LoanControlVO) svo).getParaname())){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0028")/*@res "名称重复，请重新输入！"*/);

				}
			}
//--end	modified by Chen deyin(chendya@ufida.com.cn)

	    }



		if(djlxPKs==null||djlxPKs.length==0){
			notNullErrMsg.append("\n");
			notNullErrMsg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000039")/*@res "单据类型必须选择"*/);
//			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000039")/*@res "单据类型必须选择"*/);
		}
		if(jsfsPKs==null||jsfsPKs.length==0){
			jsfsPKs=new String[]{""};
		}
        if(!isGroup()){
        	if(pk_org==null||pk_org.length()==0){
        		notNullErrMsg.append("\n");
    			notNullErrMsg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0029")/*@res "所属单位不能为空！"*/);
    		}
        }


		List<LoanControlModeVO> modvos=new ArrayList<LoanControlModeVO>();
		for (int i = 0; i < controlMode_Comp.size(); i++) {
			if(((UICheckBox)controlMode_Comp.get(i).get(0)).isSelected()){

				LoanControlModeVO vo=new LoanControlModeVO();

				vo.setPk_controlmodedef(controlMode_Value.get(i));

				if(controlMode_Comp.get(i).size()>1){

					String text = ((UITextField)controlMode_Comp.get(i).get(1)).getText();

					if(text==null || text.trim().length()==0){
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000040")/*@res "选中的控制方式必须输入相应的取值!"*/);
					}

					text = BXUiUtil.convertToTrueString(text);
					vo.setValue(new Integer(text));
				}
				modvos.add(vo);
			}
		}

		if(modvos.size()==0){
			notNullErrMsg.append("\n");
			notNullErrMsg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000041")/*@res "至少要选择一个控制方式!"*/);
		}

		// 必输项提示统一处理
		if (notNullErrMsg.length() > 0) {
			throw new BusinessException(notNullErrMsg.toString());
		}
		
		for (int i = 0; i < jsfsPKs.length; i++) {
			if (djlxPKs != null && djlxPKs.length > 0) {
				for (int j = 0; j < djlxPKs.length; j++) {
					LoanControlSchemaVO vo = new LoanControlSchemaVO();
					vo.setDjlxbm(djlxPKs[j]);
					vo.setBalatype(jsfsPKs[i]);
					schemavos.add(vo);
				}
			}
		}
		controlVO.setCurrency(bzbmPK);
		controlVO.setSchemavos(schemavos);
		controlVO.setModevos(modvos);

		return controlVO;
	}

	@Override
	public void setEditStatus(boolean b) {

		getUIRadioButton3().setEnabled(b);
		getUIRadioButton4().setEnabled(b);
		getUITextField().setEnabled(b);
		getUITextField1().setEnabled(b);

		getRefOrg().setEnabled(getParentUI().getModel().getStatus()==CommonModel.STATUS_MOD?false:b);
		getRefdjlx().setEnabled(b);
		getRefjsfs().setEnabled(b);
		getRefbzbm().setEnabled(b);
		getIvjUIcheckBB().setEnabled(b);

		for (Iterator<UIRadioButton> iter = controlAttribute_Comp.iterator(); iter.hasNext();) {
			UIRadioButton button = iter.next();
			button.setEnabled(b);
		}

		for (Iterator<List<JComponent>> iter = controlMode_Comp.iterator(); iter.hasNext();) {
			List<JComponent> list = iter.next();
			for (Iterator<JComponent> iterator = list.iterator(); iterator.hasNext();) {
				JComponent element = iterator.next();
				element.setEnabled(b);
			}
		}

	}


	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.UIPanel
	 */
	private UIPanel getUIPanel() {
		if (jPanel == null) {
			GridLayout gridLayout2 = new GridLayout();
			gridLayout2.setRows(4);
			gridLayout2.setColumns(1);
			jLabel3 = new UILabel();
			jLabel3.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000042")/*@res "控制类型"*/);
			jLabel3.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
			jLabel2 = new UILabel();
			jLabel2.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000043")/*@res "控制对象"*/);
			jLabel2.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));

			jLabelOrg = new UILabel();
			jLabelOrg.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0010")/*@res "所属组织"*/);

			jLabel1 = new UILabel();
			jLabel1.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000044")/*@res "编号"*/);
			jLabel = new UILabel();
			jLabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000045")/*@res "名称"*/);
			jLabe3 = new UILabel();
			jLabe3.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000046")/*@res "币种"*/);

			jPanel = new UIPanel();
			jPanel.setLayout(gridLayout2);
			jPanel.setName("jPanel");
			jPanel.setSize(999, 163);


			if(!isGroup()){
				jPanel.add(getUIPanel3(), null);
			}
			jPanel.add(getUIPanel4(), null);
			jPanel.add(getUIPanel5(), null);
			jPanel.add(getUIPanel6(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.UIPanel
	 * @throws BusinessException
	 */
	private UIPanel getUIPanel1() throws BusinessException {
		if (jPanel1 == null) {

			IArapCommonPrivate impl = (IArapCommonPrivate) NCLocator.getInstance().lookup(IArapCommonPrivate.class.getName());
			Collection<SuperVO> vos = impl.getVOs(LoanControlModeDefVO.class, " dr=0 ", true);

			GridLayout gridLayout3 = new GridLayout();
			gridLayout3.setColumns(1);
			gridLayout3.setRows(vos.size());

			jPanel1 = new UIPanel();
			jPanel1.setLayout(gridLayout3);
			jPanel1.setName("jPanel1");
			jPanel1.setSize(639, 263);

			for (Iterator<SuperVO> iter = vos.iterator(); iter.hasNext();) {

				LoanControlModeDefVO vo = (LoanControlModeDefVO) iter.next();

				UIPanel jPanel3 = null;
				List<JComponent> list = new ArrayList<JComponent>();

				FlowLayout flowLayout2 = new FlowLayout();
				flowLayout2.setAlignment(FlowLayout.LEFT);
				jPanel3 = new UIPanel();
				jPanel3.setLayout(flowLayout2);

				String desc = vo.getDescription();
				String[] descs = desc.split(",");

				UICheckBox jCheckBox = new UICheckBox();
				jPanel3.add(jCheckBox, null);
				list.add(jCheckBox);

				for (int i = 0; i < descs.length; i++) {

					if (descs[i].equals(FIELD)) {
						UITextField jTextField2 = new UITextField();
						jTextField2.setPreferredSize(new Dimension(100, 20));
						jTextField2.setTextType("TextInt");
						jPanel3.add(jTextField2, null);
						list.add(jTextField2);
					} else {
						String text = descs[i];
						if (descs[i].indexOf("@") != -1) {
							String[] strings = descs[i].split("@");
							text = NCLangRes4VoTransl.getNCLangRes().getStrByID(strings[0], strings[1]);
						}

						UILabel jLabel5 = new UILabel();
						jLabel5.setText(text);
						jPanel3.add(jLabel5, null);
					}
				}

				controlMode_Comp.add(list);
				controlMode_Value.add(vo.getPrimaryKey());
				jPanel1.add(jPanel3, null);
			}

		}
		return jPanel1;
	}

	/**
	 * This method initializes jPanel2
	 *
	 * @return javax.swing.UIPanel
	 */
	private UIPanel getUIPanel2() {
		if (jPanel2 == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setAlignment(FlowLayout.LEFT);
			jLabel7 = new UILabel();
			jLabel7.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000047")/*@res "结算方式"*/);
			jLabel6 = new UILabel();
			jLabel6.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000048")/*@res "单据类型"*/);
			jPanel2 = new UIPanel();
			jPanel2.setLayout(flowLayout1);
			jPanel2.setName("jPanel2");
			jPanel2.add(jLabel6, null);
			jPanel2.add(getRefdjlx(), null);
			jPanel2.add(jLabel7, null);
			jPanel2.add(getRefjsfs(), null);
			jPanel1.setSize(639, 363);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jTextField
	 *
	 * @return javax.swing.UITextField
	 */
	private UITextField getUITextField() {
		if (jTextField == null) {
			jTextField = new UITextField();
			jTextField.setPreferredSize(new Dimension(130, 20));
			jTextField.setShowMustInputHint(true);
		}
		return jTextField;
	}

	/**
	 * This method initializes jTextField1
	 *
	 * @return javax.swing.UITextField
	 */
	private UITextField getUITextField1() {
		if (jTextField1 == null) {
			jTextField1 = new UITextField();
			jTextField1.setPreferredSize(new Dimension(130, 20));
			jTextField1.setShowMustInputHint(true);

		}
		return jTextField1;
	}

	/**
	 * This method initializes jRadioButton3
	 *
	 * @return javax.swing.UIRadioButton
	 */
	private UIRadioButton getUIRadioButton3() {
		if (jRadioButton3 == null) {
			jRadioButton3 = new UIRadioButton();
			jRadioButton3.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "提示"*/);
		}
		return jRadioButton3;
	}

	/**
	 * This method initializes jRadioButton4
	 *
	 * @return javax.swing.UIRadioButton
	 */
	private UIRadioButton getUIRadioButton4() {
		if (jRadioButton4 == null) {
			jRadioButton4 = new UIRadioButton();
			jRadioButton4.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000050")/*@res "控制"*/);
		}
		return jRadioButton4;
	}

	private UIRefPane getRefdjlx() {
		if (ivjtdjlx == null) {

			ivjtdjlx = new UIRefPane();
			ivjtdjlx.setName("tdjlx");
			ivjtdjlx.setRefNodeName("单据类型"); /*-=notranslate=-*/
			ivjtdjlx.setRefModel(new BillTypeRefModel());
			ivjtdjlx.getRefModel().setWherePart("(pk_billtypecode in('2631','2632') or (pk_billtypecode like '263X-%') )and billtypename is not null and  ( pk_group='"
									+ BXUiUtil.getPK_group() + "' )");
			ivjtdjlx.setMultiSelectedEnabled(true);
			ivjtdjlx.getUITextField().setShowMustInputHint(true);
		}
		return ivjtdjlx;
	}

	private UIRefPane getRefOrg() {
		if (ivjtOrg == null) {
			ivjtOrg = new UIRefPane();
			ivjtOrg.setRefNodeName("财务组织"); /*-=notranslate=-*/
			ivjtOrg.setSize(500, ivjtOrg.getHeight());
			ivjtOrg.setPreferredSize(new Dimension(200, ivjtOrg.getHeight()));
			ivjtOrg.setMultiSelectedEnabled(true);
			// 不支持多集团参照
			ivjtOrg.setMultiCorpRef(false);
			ivjtOrg.addValueChangedListener(this);
			ivjtOrg.setMultiSelectedEnabled(false);
			ivjtOrg.getRefModel().setFilterPks(BXUiUtil.getPermissionOrgs(BXConstans.LOANCTRL_ORG),IFilterStrategy.INSECTION);
			ivjtOrg.getUITextField().setShowMustInputHint(true);
		}
		return ivjtOrg;
	}

	private UIRefPane getRefjsfs() {
		if (ivjtjsfs == null) {
			ivjtjsfs = new UIRefPane();
			ivjtjsfs.setName("tjsfs");
			ivjtjsfs.setRefNodeName("结算方式"); /*-=notranslate=-*/
			ivjtjsfs.setPk_org(getRefOrg().getRefPK());
			ivjtjsfs.setMultiSelectedEnabled(true);
		}
		return ivjtjsfs;
	}
	/**
	 * This method initializes jPanel4
	 *
	 * @return javax.swing.UIPanel
	 */
	private UIPanel getUIPanel3() {
		if (jPanel3 == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			jPanel3 = new UIPanel();
			jPanel3.setLayout(flowLayout);
			jPanel3.add(jLabelOrg, null);
			jPanel3.add(getRefOrg() ,null);
		}
		return jPanel3;
	}
	/**
	 * This method initializes jPanel4
	 *
	 * @return javax.swing.UIPanel
	 */
	private UIPanel getUIPanel4() {
		if (jPanel4 == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			jPanel4 = new UIPanel();
			jPanel4.setLayout(flowLayout);
			jPanel4.add(jLabel1, null);
			jPanel4.add(getUITextField1(), null);
			jPanel4.add(jLabel, null);
			jPanel4.add(getUITextField(), null);
			jPanel4.add(jLabe3, null);
			jPanel4.add(getRefbzbm(), null);
			jPanel4.add(getIvjUIcheckBB(), null);
		}
		return jPanel4;
	}

	/**
	 * This method initializes jPanel5
	 *
	 * @return javax.swing.UIPanel
	 */
	private UIPanel getUIPanel5() {
		if (jPanel5 == null) {
			jPanel5 = new UIPanel();

			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			jPanel5.setLayout(flowLayout);

			jPanel5.add(jLabel2, null);

			LoanControlVO vo = new LoanControlVO();
			Map<String, String> controlAttributeNames = vo.getControlAttributeNames();
			ButtonGroup btgroup = new ButtonGroup();
			for (Iterator<String> iter = controlAttributeNames.keySet().iterator(); iter.hasNext();) {
				String key = iter.next();
				UIRadioButton jRadioButton = new UIRadioButton();
				jRadioButton.setText(controlAttributeNames.get(key));
				jRadioButton.setSelected(true);
				btgroup.add(jRadioButton);
				jPanel5.add(jRadioButton, null);
				controlAttribute_Comp.add(jRadioButton);
				controlAttribute_Value.add(key);
			}

		}
		return jPanel5;
	}

	/**
	 * This method initializes jPanel6
	 *
	 * @return javax.swing.UIPanel
	 */
	private UIPanel getUIPanel6() {
		if (jPanel6 == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);

			jPanel6 = new UIPanel();
			jPanel6.setLayout(flowLayout);

			ButtonGroup btnGroup = new ButtonGroup();
			btnGroup.add(getUIRadioButton3());
			btnGroup.add(getUIRadioButton4());

			jPanel6.add(jLabel3, null);
			jPanel6.add(getUIRadioButton3(), null);
			jPanel6.add(getUIRadioButton4(), null);
		}
		return jPanel6;
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {

		if(getRefbzbm().getValueObj()==null){

		}
	}

} // @jve:decl-index=0:visual-constraint="10,10"