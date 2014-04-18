package nc.ui.er.reimrule;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.ButtonGroup;

import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.lang.UFDouble;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author 　liansg
 *          文件导出功能
 */
public class ExportExcelDialog extends UIDialog {
	/**
	 * 序列版本
	 */
	private static final long serialVersionUID = 971337863828039312L;
	private UIPanel UIPanel = null;
	private UIPanel UIPanel1 = null;
	private UIPanel UIPanel2 = null;
	private UIButton BnOK = null;
	private UIButton BnCancel = null;
	private UILabel UILabel = null;
	private UITextField UITextField = null;
	private UIButton UIButton = null;
	private UIFileChooser FileChooser = null;
	private UICheckBox UICheckBox = null;
	private UICheckBox UICheckBox1 = null;
	private UITextField TSheetName = null;
	private UILabel UILabel1 = null;
	private UILabel UILabel2 = null;
	private UIComboBox UIComboBox = null;
	private File excelfile = null;
	private transient InputStream is = null;
	private transient OutputStream os = null;
	private Workbook wb = null;
	private static String OFFICE03 = ".xls";
	private static String OFFICE07 = ".xlsx";

	private final BillCardPanel panel;
	
	public ExportExcelDialog(Container c,BillCardPanel panel) {
		super(c);
		this.panel = panel;
		initialize();
	}
	/**
	 * This method initializes this
	 *
	 */
	private void initialize() {
		this.setSize(new java.awt.Dimension(515, 147));
		this.setContentPane(getUIPanel());
		this.setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000442")/*@res "报销管理导出对话框"*/);
		this.setLocationRelativeTo(getUIPanel());
		ButtonGroup bg = new ButtonGroup();
		bg.add(getUICheckBox());
		bg.add(getUICheckBox1());
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
			UILabel2 = new UILabel();
			UILabel2.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000443")/*@res "要覆盖的页签"*/);
			UILabel1 = new UILabel();
			UILabel1.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000444")/*@res "新增加的页签名称"*/);
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setAlignment(java.awt.FlowLayout.LEFT);
			UILabel = new UILabel();
			UILabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000445")/*@res "请选择EXCEL文件"*/);
			UIPanel1 = new UIPanel();
			UIPanel1.setLayout(flowLayout1);
			UIPanel1.add(UILabel, null);
			UIPanel1.add(getUITextField(), null);
			UIPanel1.add(getUIButton(), null);
			UIPanel1.add(getUICheckBox(), null);
			UIPanel1.add(UILabel2, null);
			UIPanel1.add(getUIComboBox(), null);
			UIPanel1.add(getUICheckBox1(), null);
			UIPanel1.add(UILabel1, null);
			UIPanel1.add(getTSheetName(), null);
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
					if(getUITextField().getText() == null || getUITextField().getText().equals("")){
						MessageDialog.showWarningDlg(ExportExcelDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "提示"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000447")/*@res "Excel文件不能为空，请选择Excel文件！"*/);
						return;
					}
					if (!getUICheckBox().isSelected()) {
						String sheetname = getTSheetName().getText();
						if (sheetname == null || sheetname.trim().length() == 0) {
							MessageDialog.showHintDlg(ExportExcelDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "提示"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000448")/*@res "请输入一个页签的名字。"*/);
							return;
						}
						for (int i = 0; i < getUIComboBox().getItemCount(); i++) {
							if (sheetname.equals(getUIComboBox().getItemAt(i))) {
								MessageDialog.showHintDlg(ExportExcelDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "提示"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000449")/*@res "输入的页签名字与已有的页签重复，请更换。"*/);
								return;
							}
						}
						for(int j = 0; j < getUIComboBox().getItemCount(); j++){
							if(sheetname.equalsIgnoreCase(getUIComboBox().getItemAt(j).toString())){
								MessageDialog.showHintDlg(ExportExcelDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "提示"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000450")/*@res "存在相同名字的页签，请更换。"*/);
								return;
							}
						}
					}

					String currentBodyTableCode = panel.getCurrentBodyTableCode();
					ReimRulerVO[] reimRuleVos = (ReimRulerVO[]) panel.getBillData().getBodyValueVOs(currentBodyTableCode, ReimRulerVO.class.getName());
					if(reimRuleVos==null || reimRuleVos.length==0){
						MessageDialog.showHintDlg(ExportExcelDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "提示"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000451")/*@res "选中的报销标准中没有具体的值,导出已取消!"*/);
						return;
					}
					exportToExcel();
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
			UIButton.setPreferredSize(new java.awt.Dimension(40, 20));
			UIButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (getFileChooser().showOpenDialog(ExportExcelDialog.this) == UIFileChooser.APPROVE_OPTION) {
						String filepath = getFileChooser().getSelectedFile().getPath();
						if(!filepath.endsWith(OFFICE03) && !filepath.endsWith(OFFICE07))
							filepath += OFFICE07; 
						getUITextField().setText(filepath);
						try {
							if(getFileChooser().getSelectedFile().exists())
								excelfile = getFileChooser().getSelectedFile();
							else{
								XSSFWorkbook workbook = new XSSFWorkbook();
								XSSFSheet sheet = workbook.createSheet();
								for(int i=0;i<10;i++){
									XSSFRow row = sheet.createRow(i);
									for(int j=0;j<10;j++){
										XSSFCell cell = row.createCell(j);
										cell.setCellType(XSSFCell.CELL_TYPE_STRING);
										cell.setCellValue("");
									}
								}
								FileOutputStream out = new FileOutputStream(filepath);
								workbook.write(out);
								out.flush();
								out.close();
								excelfile = new File(filepath);
							}
							is = new FileInputStream(excelfile);
							if(excelfile.getPath().endsWith(OFFICE03)){
								wb = new HSSFWorkbook(is);
							}else if(excelfile.getPath().endsWith(OFFICE07)){
								wb = new XSSFWorkbook(is);
							}
							getUIComboBox().removeAllItems();
							for (int i = 0; i < wb.getNumberOfSheets(); i++) {
								getUIComboBox().addItem(wb.getSheetName(i));
							}
						} catch (IOException eex) {
							MessageDialog.showErrorDlg(ExportExcelDialog.this,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000380")/*@res "错误"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000453")/*@res "打开文件时出现错误，可能是文件格式出错或者正在被使用。"*/);
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
	 * This method initializes UICheckBox
	 *
	 * @return nc.ui.pub.beans.UICheckBox
	 */
	private UICheckBox getUICheckBox() {
		if (UICheckBox == null) {
			UICheckBox = new UICheckBox();
			UICheckBox.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000454")/*@res "覆盖原来的内容"*/);
			UICheckBox.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			UICheckBox.setSelected(true);
			UICheckBox.setPreferredSize(new java.awt.Dimension(120, 22));
			UICheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if (getUICheckBox().isSelected()) {
						getUIComboBox().setEnabled(true);
					} else {
						getUIComboBox().setEnabled(false);
					}
				}
			});
		}
		return UICheckBox;
	}

	/**
	 * This method initializes UICheckBox1
	 *
	 * @return nc.ui.pub.beans.UICheckBox
	 */
	private UICheckBox getUICheckBox1() {
		if (UICheckBox1 == null) {
			UICheckBox1 = new UICheckBox();
			UICheckBox1.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000455")/*@res "增加新的页签"*/);
			UICheckBox1.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			UICheckBox1.setPreferredSize(new java.awt.Dimension(120, 22));
			UICheckBox1.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if (getUICheckBox1().isSelected()) {
						getTSheetName().setEnabled(true);
					} else {
						getTSheetName().setEnabled(false);
					}
				}
			});
		}
		return UICheckBox1;
	}

	/**
	 * This method initializes TSheetName
	 *
	 * @return nc.ui.pub.beans.UITextField
	 */
	private UITextField getTSheetName() {
		if (TSheetName == null) {
			TSheetName = new UITextField();
			TSheetName.setPreferredSize(new java.awt.Dimension(200, 22));
			TSheetName.setEnabled(false);
		}
		return TSheetName;
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

	private void exportToExcel() {
		if(excelfile == null){
			MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "提示"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000456")/*@res "请选择目标Excel文件！"*/);
		}
		try{
			is = new FileInputStream(excelfile);
			if(excelfile.getPath().endsWith(OFFICE03)){
				wb = new HSSFWorkbook(is);
			}else if(excelfile.getPath().endsWith(OFFICE07)){
				wb = new XSSFWorkbook(is);
			}
			Sheet sheet = null;
			//getUICheckBox().isSelected() 为true 覆盖 否则 新建标签
			if(getUICheckBox().isSelected())
				sheet = wb.getSheet(String.valueOf(getUIComboBox().getSelectdItemValue()));
			else
				sheet = wb.createSheet(getTSheetName().getText());
			if(sheet.getLastRowNum()!=0){

				for (int i = 0; i<=sheet.getLastRowNum();i++){
					if(sheet.getRow(i)!=null)
						sheet.removeRow(sheet.getRow(i));
				}
			}
			//在excel文件中导入表头信息
			Row row0 = sheet.createRow(0);
			BillItem[] headShowItems = panel.getBodyShowItems();
			BillModel model = panel.getBillModel();

			for(int i = 0;i < headShowItems.length;i++){
				row0.createCell((short) i);
			}
			for(int j =0;j< headShowItems.length;j++){
				Cell cell = row0.getCell((short) j);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (j < headShowItems.length) {
					 String reimruleKey =  headShowItems[j].getKey();
					 if(reimruleKey.contains("def")){
						 if(reimruleKey.contains("_name")){
							 String defKey = reimruleKey.substring(0,reimruleKey.indexOf("_"));
//							 cell.setCellValue(new HSSFRichTextString(headShowItems[j].getName().toString()+"@"+defKey));
							 cell.setCellValue(headShowItems[j].getName().toString()+"@"+defKey);
						 }else {
//							 cell.setCellValue(new HSSFRichTextString(headShowItems[j].getName().toString()+"@"+reimruleKey));
							 cell.setCellValue(headShowItems[j].getName().toString()+"@"+reimruleKey);
						 }
					 } else{
//    					cell.setCellValue(new HSSFRichTextString(headShowItems[j].getName().toString()));
    					cell.setCellValue(headShowItems[j].getName().toString());
					 }
				}
			}
			int NumOfColumns = panel.getBillTable().getColumnCount();
			if(NumOfColumns<=0){
				MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "提示"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000457")/*@res "不存在数据。"*/);
			} else{
				int NumOfRows = panel.getBillTable().getRowCount();
				if(NumOfRows <=0){
					MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "提示"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000457")/*@res "不存在数据。"*/);
				}else {
//					Row[] row = new HSSFRow[NumOfRows+1];
//					Cell[] cellbody = new HSSFCell[headShowItems.length];
					Row row = null;
					Cell cellbody = null;
					for(int k=0;k<NumOfRows;k++){
						row = sheet.createRow(k+1);
						for(int j =0;j< headShowItems.length;j++){
						 cellbody = row.createCell((short)j);
						 if(model.getValueAt(k, headShowItems[j].getKey())!=null &&model.getValueAt(k, headShowItems[j].getKey()).toString().length()>0){
							 if(headShowItems[j].getDataType()==headShowItems[j].DECIMAL ||headShowItems[j].getDataType()==headShowItems[j].INTEGER){
								 cellbody.setCellType(Cell.CELL_TYPE_NUMERIC);
								 cellbody.setCellValue(new UFDouble(model.getValueAt(k, headShowItems[j].getKey()).toString()).doubleValue());
							 }else{
								 cellbody.setCellType(Cell.CELL_TYPE_STRING);
								 cellbody.setCellValue(model.getValueAt(k, headShowItems[j].getKey()).toString());
							 }
						 }
						 else{
							 cellbody.setCellType(Cell.CELL_TYPE_STRING);
					    	 cellbody.setCellValue("");
						 }

						}
					}
				}

			}
			os = new FileOutputStream(excelfile);
			wb.write(os);
			MessageDialog.showHintDlg(this, "",nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000458")/*@res "导出完成"*/);
		} catch(IOException e) {
			ExceptionHandler.consume(e);
			MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "提示"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000459")/*@res "导出失败，可能是文件格式错误或者目标文件正在被使用。"*/);
		}finally{
			try {
				os.close();
				is.close();
			} catch (IOException e) {
				ExceptionHandler.consume(e);
				MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "提示"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000459")/*@res "导出失败，可能是文件格式错误或者目标文件正在被使用。"*/);
			}
		}

	}
}