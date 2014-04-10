package nc.ui.er.reimrule;

/**
*
*/


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ButtonGroup;

import nc.bs.framework.common.NCLocator;
import nc.itf.bd.currtype.ICurrtypeQuery;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.erm.prv.IArapCommonPrivate;
import nc.itf.org.IDeptQryService;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRadioButton;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.bill.BillItem;
import nc.vo.bd.currtype.CurrtypeVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.er.reimtype.ReimTypeHeaderVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.logging.Debug;
import nc.vo.org.DeptVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



/**
* @author  liansg
*          Excel文件的导入功能
*
*/
public class ImportExcelDialog extends UIDialog implements ActionListener{

	/**
	 * 版本序列
	 */
	private static final long serialVersionUID = 1L;

	private UIPanel UIPanel = null;

	private UIPanel UIPanel1 = null;

	private UIPanel UIPanel2 = null;

	private UIButton BnOK = null;

	private UIButton BnCancel = null;

	private UITextField UITextField = null;

	private UIButton UIButton = null;

	private UIFileChooser FileChooser = null;

	private UIComboBox UIComboBox = null;

	private UIPanel UIPanel3 = null;

	private UIPanel UIPanel4 = null;

	private UIPanel UIPanel5 = null;

	private UILabel UILabel = null;

	private UILabel UILabel1 = null;

	private UILabel UILabel2 = null;

	private UIPanel UIPanel6 = null;

	private UILabel UILabel3 = null;

	private UIRadioButton RBUseName = null;

	private UIRadioButton RBUseCode = null;

	private UIRadioButton RBIncrement = null;

	private UIRadioButton RBCover = null;

	private Workbook wb = null;

	private InputStream is = null;
	private static String OFFICE03 = ".xls";
	private static String OFFICE07 = ".xlsx";

	private static Map<Integer,String> colsMap=new HashMap<Integer,String>();


	private final ReimRuleUI ruleUI ;

	ArrayList<ReimRuleVO> reimrules;

	Sheet sheet = null;
	/**
	 * This method initializes
	 *
	 */
	public ImportExcelDialog(Container c,ReimRuleUI ruleui) {
		super(c);
		this.ruleUI = ruleui;
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 */
	private void initialize() {
		this.setSize(new java.awt.Dimension(508, 182));
		this.setLocationRelativeTo(getUIPanel());
		this.setContentPane(getUIPanel());
		this.setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000460")/*@res "报销管理导入对话框"*/);
	}

	/**
	 * This method initializes UIPanel
	 *
	 * @return nc.ui.pub.beans.UIPanel
	 */
	private UIPanel getUIPanel() {
		if (UIPanel == null) {
			UIPanel = new UIPanel();
			UIPanel.setLayout(new BorderLayout());
			UIPanel.add(getUIPanel2(), java.awt.BorderLayout.SOUTH);
			UIPanel.add(getUIPanel1(), java.awt.BorderLayout.CENTER);
		}
		return UIPanel;
	}

	/**
	 * This method initializes UIPanel1
	 *
	 * @return nc.ui.pub.beans.UIPanel
	 */
	private UIPanel getUIPanel1() {
		if (UIPanel1 == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setColumns(1);
			gridLayout.setRows(0);
			UIPanel1 = new UIPanel();
			UIPanel1.setLayout(gridLayout);
			UIPanel1.add(getUIPanel3(), null);
			UIPanel1.add(getUIPanel4(), null);
			UIPanel1.add(getUIPanel5(), null);
			UIPanel1.add(getUIPanel6(), null);
		}
		return UIPanel1;
	}

	/**
	 * This method initializes UIPanel2
	 *
	 * @return nc.ui.pub.beans.UIPanel
	 */
	private UIPanel getUIPanel2() {
		if (UIPanel2 == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(java.awt.FlowLayout.RIGHT);
			UIPanel2 = new UIPanel();
			UIPanel2.setLayout(flowLayout);
			UIPanel2.setPreferredSize(new java.awt.Dimension(10, 30));
			UIPanel2.add(getBnOK(), null);
			UIPanel2.add(getBnCancel(), null);
		}
		return UIPanel2;
	}

	/**
	 * This method initializes BnOK
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	private UIButton getBnOK() {
		if (BnOK == null) {
			BnOK = new UIButton();
			BnOK.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000446")/*@res "确定"*/);
			BnOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						//校验数据合法性
						if(getUITextField().getText()== null || getUITextField().getText().equals("")){
							MessageDialog.showWarningDlg(ImportExcelDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "提示"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000447")/*@res "Excel文件不能为空，请选择Excel文件！"*/);
							return;
						}
						if(getUIComboBox().getItemCount()== 0 && getUIComboBox().getSelectedItem() == null){
							MessageDialog.showWarningDlg(ImportExcelDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "提示"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000461")/*@res "请选择要导入的页签!"*/);
							return ;
						}

					} catch (NumberFormatException ex) {
						ExceptionHandler.consume(ex);
						MessageDialog.showErrorDlg(ImportExcelDialog.this, null, ex.getMessage());
					}
					closeOK();
				}
			});
		}
		return BnOK;
	}


	/**
	 * This method initializes BnCancel
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	private UIButton getBnCancel() {
		if (BnCancel == null) {
			BnCancel = new UIButton();
			BnCancel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000064")/*@res "取消"*/);
			BnCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					closeCancel();
				}
			});
		}
		return BnCancel;
	}

	/**
	 * This method initializes UITextField
	 *
	 * @return nc.ui.pub.beans.UITextField
	 */
	private UITextField getUITextField() {
		if (UITextField == null) {
			UITextField = new UITextField();
			UITextField.setPreferredSize(new java.awt.Dimension(300, 22));
			UITextField.setMaxLength(400);
			UITextField.setEditable(false);
		}
		return UITextField;
	}

	/**
	 * This method initializes UIButton
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	private UIButton getUIButton() {
		if (UIButton == null) {
			UIButton = new UIButton();
			UIButton.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000452")/*@res "浏览"*/);
			UIButton.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			UIButton.setPreferredSize(new java.awt.Dimension(30, 20));
			UIButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (getFileChooser().showOpenDialog(ImportExcelDialog.this) == UIFileChooser.APPROVE_OPTION) {
						getUITextField().setText(getFileChooser().getSelectedFile().getPath());
						File excelfile = getFileChooser().getSelectedFile();
						try {
							is = new FileInputStream(excelfile);
							if(excelfile.getPath().endsWith(OFFICE03)){
								wb = new HSSFWorkbook(is);
							}else if(excelfile.getPath().endsWith(OFFICE07)){
								wb = new XSSFWorkbook(is);
							}
							for (int i = 0; i < wb.getNumberOfSheets(); i++) {
								getUIComboBox().addItem(wb.getSheetName(i));
							}
						} catch (IOException eex) {
							MessageDialog.showErrorDlg(ImportExcelDialog.this,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000380")/*@res "错误"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000453")/*@res "打开文件时出现错误，可能是文件格式出错或者正在被使用。"*/);
						}
					}
				}
			});
		}
		return UIButton;
	}

	/**
	 * This method initializes UIFileChooser
	 *
	 * @return nc.ui.pub.beans.UIFileChooser
	 */
	private UIFileChooser getFileChooser() {
		if (FileChooser == null) {
			FileChooser = new UIFileChooser();
		}
		return FileChooser;
	}

	/**
	 * This method initializes UIComboBox
	 *
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	private UIComboBox getUIComboBox() {
		if (UIComboBox == null) {
			UIComboBox = new UIComboBox();
			UIComboBox.setPreferredSize(new java.awt.Dimension(200, 20));
		}
		return UIComboBox;
	}
	/**
	 * This method initializes UIPanel3
	 *
	 * @return nc.ui.pub.beans.UIPanel
	 */
	private UIPanel getUIPanel3() {
		if (UIPanel3 == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setAlignment(java.awt.FlowLayout.LEFT);
			UILabel = new UILabel();
			UILabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000445")/*@res "请选择EXCEL文件"*/);
			UIPanel3 = new UIPanel();
			UIPanel3.setLayout(flowLayout1);
			UIPanel3.add(UILabel, null);
			UIPanel3.add(getUITextField(), null);
			UIPanel3.add(getUIButton(), null);
		}
		return UIPanel3;
	}

	/**
	 * This method initializes UIPanel4
	 *
	 * @return nc.ui.pub.beans.UIPanel
	 */
	private UIPanel getUIPanel4() {
		if (UIPanel4 == null) {
			FlowLayout flowLayout2 = new FlowLayout();
			flowLayout2.setAlignment(java.awt.FlowLayout.LEFT);
			UILabel1 = new UILabel();
			UILabel1.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000462")/*@res "请选择要导入的页签"*/);
			UIPanel4 = new UIPanel();
			UIPanel4.setLayout(flowLayout2);
			UIPanel4.add(UILabel1, null);
			UIPanel4.add(getUIComboBox(), null);
		}
		return UIPanel4;
	}

	/**
	 * This method initializes UIPanel5
	 *
	 * @return nc.ui.pub.beans.UIPanel
	 */
	private UIPanel getUIPanel5() {
		if (UIPanel5 == null) {
			FlowLayout flowLayout3 = new FlowLayout();
			flowLayout3.setAlignment(java.awt.FlowLayout.LEFT);
			UILabel2 = new UILabel();
			UILabel2.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000463")/*@res "导入标准"*/);
			UIPanel5 = new UIPanel();
			UIPanel5.setLayout(flowLayout3);
			UIPanel5.add(UILabel2, null);
			UIPanel5.add(getRBUseName(), null);
			UIPanel5.add(getRBUseCode(), null);
			ButtonGroup bg = new ButtonGroup();
			bg.add(getRBUseName());
			bg.add(getRBUseCode());
		}
		return UIPanel5;
	}
	/**
	 * This method initializes UIPanel6
	 *
	 * @return nc.ui.pub.beans.UIPanel
	 */
	private UIPanel getUIPanel6() {
		if (UIPanel6 == null) {
			FlowLayout flowLayout4 = new FlowLayout();
			flowLayout4.setAlignment(java.awt.FlowLayout.LEFT);
			UILabel3 = new UILabel();
			UILabel3.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000464")/*@res "导入方式"*/);
			UIPanel6 = new UIPanel();
			UIPanel6.setLayout(flowLayout4);
			UIPanel6.add(UILabel3, null);
			UIPanel6.add(getRBIncrement(), null);
			UIPanel6.add(getRBCover(), null);
			ButtonGroup bg = new ButtonGroup();
			bg.add(getRBIncrement());
			bg.add(getRBCover());
		}
		return UIPanel6;
	}

	/**
	 * This method initializes UIRadioButton
	 *
	 * @return nc.ui.pub.beans.UIRadioButton
	 */
	private UIRadioButton getRBUseName() {
		if (RBUseName == null) {
			RBUseName = new UIRadioButton();
			RBUseName.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000465")/*@res "按名称"*/);
			RBUseName.setSelected(true);
			RBUseName.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
		}
		return RBUseName;
	}

	/**
	 * This method initializes RBUseCode
	 *
	 * @return nc.ui.pub.beans.UIRadioButton
	 */
	private UIRadioButton getRBUseCode() {
		if (RBUseCode == null) {
			RBUseCode = new UIRadioButton();
			RBUseCode.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000466")/*@res "按编码"*/);
			RBUseCode.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
		}
		return RBUseCode;
	}

	/**
	 * This method initializes RBRound
	 *
	 * @return nc.ui.pub.beans.UIRadioButton
	 */
	private UIRadioButton getRBIncrement() {
		if (RBIncrement == null) {
			RBIncrement = new UIRadioButton();
			RBIncrement.setSelected(true);
			RBIncrement.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			RBIncrement.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000467")/*@res "增量"*/);
		}
		return RBIncrement;
	}

	/**
	 * This method initializes RBCut
	 *
	 * @return nc.ui.pub.beans.UIRadioButton
	 */
	private UIRadioButton getRBCover() {
		if (RBCover == null) {
			RBCover = new UIRadioButton();
			RBCover.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000468")/*@res "覆盖"*/);
			RBCover.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
		}
		return RBCover;
	}
	/**
	 * @author
	 *
	 * 从excel表中导入
	 * @param
	 * @return
	 */
	public ReimRuleVO[] importFromExcel() {

		if(sheet == null)
		sheet = wb.getSheet(String.valueOf(getUIComboBox().getSelectdItemValue()));
		int rowNum = sheet.getLastRowNum();
		//判断EXCEL表头标题是否满足规定的格式
		Row row = sheet.getRow(0);
		BillItem[] headShowItems = ruleUI.getBillCardPanel().getBodyShowItems();
		ArrayList<String> list = new ArrayList<String>(headShowItems.length);
		for(int j= 0 ; j < row.getLastCellNum();j++){
//			colsMap.put(j, headShowItems[j].getName());
			String reimruleKey = headShowItems[j].getKey();
			if(reimruleKey.contains("def")){
				 if(reimruleKey.contains("_name")){
					 String defKey = reimruleKey.substring(0, reimruleKey.indexOf("_"));
					 list.add(j, headShowItems[j].getName().toString() +  "@" + defKey);
					 colsMap.put(j, headShowItems[j].getName().toString() +  "@" + defKey);
				 }else{
					 list.add(j, headShowItems[j].getName().toString() + "@" + reimruleKey);
					 colsMap.put(j, headShowItems[j].getName().toString() + "@" + reimruleKey);
				 }
			} else {
				list.add(j, headShowItems[j].getName().toString());
			    colsMap.put(j, headShowItems[j].getName().toString());
			}
		}

		for (int j = 0; j < row.getLastCellNum(); j++) {
			Cell cell = row.getCell((short) j);
			try {
				if(cell != null && !cell.toString().equals(list.get(j))){
					MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000380")/*@res "错误"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000469")/*@res "EXCEL标题格式不满足要求:"*/+cell.toString());
					return null;
				}else if(cell == null){
					MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000380")/*@res "错误"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000470",null,new String[]{String.valueOf(j+1)})/*@res "EXCEL在i列以后不应有数据:"*/);
					return null;
				}
			} catch (RuntimeException e) {
				ExceptionHandler.consume(e);
				MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000380")/*@res "错误"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000472")/*@res "EXCEL数据格式异常:"*/);
				return null;
			}
		}

		reimrules = new ArrayList<ReimRuleVO>(rowNum);

		for(int i=1;i<=rowNum;i++){
			row = sheet.getRow(i);
			if(row == null){
				MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000473")/*@res "警告"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000474")/*@res "数据在："*/+(i+1)+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000475")/*@res "行为空，跳过"*/);
				continue;
			}
			ReimRuleVO reimrule = new ReimRuleVO();
			for(int j=0;j<row.getLastCellNum();j++){
				Cell cell = row.getCell((short) j);

				if(cell == null)continue;
				try {
					Object cellValue = null;
					cellValue = parseValue(cell,cellValue);
					if(colsMap.get(j).equals(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPTcommon-000156")/*@res "费用类型"*/)){
						Collection<SuperVO> expenseType = NCLocator.getInstance().lookup(IArapCommonPrivate.class).getVOs(ExpenseTypeVO.class, "", false);
						if(expenseType!=null){
							Iterator<SuperVO> itExpense = expenseType.iterator();
							while (itExpense.hasNext()) {
								ExpenseTypeVO voexpense = (ExpenseTypeVO) itExpense.next();
								if (getRBUseName().isSelected()) {
									if (voexpense.getName().equals(cellValue))
										reimrule.setPk_expensetype(voexpense.getPk_expensetype());

								} else if (voexpense.getCode().equals(cellValue)) {
									reimrule.setPk_expensetype(voexpense.getPk_expensetype());
								}
							}
						}else
							reimrule.setPk_expensetype("");

					} else if(colsMap.get(j).equals(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000476")/*@res "报销类型"*/)){
						Collection<SuperVO> reimType = NCLocator.getInstance().lookup(IArapCommonPrivate.class).getVOs(ReimTypeHeaderVO.class, "", false);
						if(reimType!=null){
							Iterator<SuperVO> itReimtype = reimType.iterator();
								while(itReimtype.hasNext()){
									ReimTypeHeaderVO voReimtype = (ReimTypeHeaderVO) itReimtype.next();
									if(getRBUseName().isSelected()){
										if(voReimtype.getName().equals(cellValue))
											reimrule.setPk_reimtype(voReimtype.getPk_reimtype());

									} else if(voReimtype.getCode().equals(cellValue)){
											reimrule.setPk_reimtype(voReimtype.getPk_reimtype());
									}
								}
						}else reimrule.setPk_reimtype("");
					}else if(colsMap.get(j).equals(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPTcommon-000007")/*@res "部门"*/)){
						//部门导入目前采用默认集团
						DeptVO condDeptdocVO = new DeptVO();
						String department = cell.toString();
						if(department.contains("."))
							department = department.substring(0,department.lastIndexOf("."));
						condDeptdocVO.setName(department);
						if(condDeptdocVO != null && !condDeptdocVO.getName().equals("")){
							DeptVO[] bookvo = NCLocator.getInstance().lookup(IDeptQryService.class).queryDeptVOSByGroupIDAndClause(BXUiUtil.getPK_group(), "name='" + department + "'");
							if(bookvo!=null && bookvo.length>0 )
							{
								if(getRBUseName().isSelected()){
									if(bookvo[0].getName().equals(department))
										reimrule.setPk_deptid(bookvo[0].getPk_dept());
									else
										reimrule.setPk_deptid("");
								} else if(bookvo[0].getName().equals(cellValue.toString()))
										reimrule.setPk_deptid(bookvo[0].getPk_dept());
							} else reimrule.setPk_deptid("");
						} else reimrule.setPk_deptid("");

					}else if(colsMap.get(j).equals(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000477")/*@res "姓名"*/)){

						String strpsn = cell.toString();
						if (strpsn.contains("."))
							strpsn = strpsn.substring(0,strpsn.lastIndexOf("."));
						PsndocVO[] namevo = NCLocator.getInstance().lookup(IPsndocQueryService.class).queryPsndocVOsByCondition("name ='" + cellValue.toString() +"'");
					
						if(namevo!= null){
							if(getRBUseName().isSelected()){
								if(namevo[0].getName().equals(cellValue.toString()))
									reimrule.setPk_psn(namevo[0].getPk_psndoc());
								else reimrule.setPk_psn("");
							} else if(namevo[0].getCode().equals(cellValue.toString()))
								   reimrule.setPk_psn(namevo[0].getPk_psndoc());
						} else
							reimrule.setPk_psn("");

					}else if(colsMap.get(j).equals(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000046")/*@res "币种"*/)){
						String strCurrtype = cellValue.toString().trim();
						if(!"".equals(strCurrtype)&& strCurrtype!=null){
							CurrtypeVO[] currtypevoByName = NCLocator.getInstance().lookup(ICurrtypeQuery.class).queryAllCurrtypeVO();
							if(getRBUseName().isSelected()){							
								if(currtypevoByName!=null){
									for(CurrtypeVO vo:currtypevoByName){
										if(vo.getName().equals(strCurrtype)){
											reimrule.setPk_currtype(vo.getPk_currtype());
										}
									}
								}else{
									reimrule.setPk_currtype("");
								} 
										
							} else 
							{	
								if(currtypevoByName!=null){
									for(CurrtypeVO vo:currtypevoByName){
										if(vo.getCode().equals(strCurrtype)){
											reimrule.setPk_currtype(vo.getPk_currtype());
										}
									}
								}
								else 
									reimrule.setPk_currtype("");
							}
						}																																											
					}else if(colsMap.get(j).equals(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000478")/*@res "金额"*/)){
						if(!cell.toString().equals("")){
							UFDouble amount = new UFDouble(Double.parseDouble(cell.toString()));
							reimrule.setAmount(amount);
						} else
							reimrule.setAmount(UFDouble.ZERO_DBL);
					}else if(colsMap.get(j).equals(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPTcommon-000199")/*@res "备注"*/)){
						String memo = cell.toString();
						reimrule.setMemo(memo);
					}else if(colsMap.get(j).equals(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000479")/*@res "优先级"*/)){
						String priority = cell.toString();
						if (priority.contains("."))
							priority = priority.substring(0,priority.lastIndexOf("."));
						reimrule.setPriority(Integer.valueOf(priority));
					}else if(colsMap.get(j).contains("def")){ // 部门自定义@def?
						String key=colsMap.get(j);
						String defcode=key.substring(key.indexOf("def"));
//						String bdpk = null;
//						if(getRBUseName().isSelected())
//							bdpk = ruleUI.getDefValueByKey(cell.toString(), defcode, true);
//						else
//							bdpk = ruleUI.getDefValueByKey(cell.toString(), defcode, false);
						String bdpk = cell.toString();
						reimrule.setAttributeValue(defcode, bdpk);
						reimrule.setAttributeValue(defcode+"_name", cell.toString());
					}

				} catch (Exception e) {
					Debug.error(e.getMessage(),e);
					MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000380")/*@res "错误"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000480",null,new String[]{String.valueOf((i+1)),String.valueOf((j+1))})/*@res "EXCEL数据格式在：i行,j列有问题：\n"*/+e.getMessage());
					return null;
				}
			}
			reimrules.add(reimrule);
		}
		return reimrules.toArray(new ReimRuleVO[reimrules.size()]);
	}

	private Object parseValue(Cell cell, Object reimtype) {
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC : {
				double dvalue = cell.getNumericCellValue();
				reimtype = String.valueOf(dvalue);
				break;
			}
			case Cell.CELL_TYPE_STRING : {
				reimtype = cell.getRichStringCellValue();
				reimtype = String.valueOf(cell);
				break;
			}
		}
		return reimtype;
	}
	public Boolean isRBIncrement() {
		if(getRBIncrement().isSelected())
			return true;
		else return false ;
	}
	public void actionPerformed(ActionEvent e) {

	}

}