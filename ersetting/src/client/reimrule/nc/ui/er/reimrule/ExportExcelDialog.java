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
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.er.reimrule.ReimRuleUI;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.lang.UFDouble;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author ��liansg
 *          �ļ���������
 */
public class ExportExcelDialog extends UIDialog {
	/**
	 * ���а汾
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

//	private File file;  //
	private final ReimRuleUI ruleUI;
	/**
	 * This method initializes
	 *
	 */ 
	public ExportExcelDialog(Container c,ReimRuleUI ruleui) {
		super(c);
		this.ruleUI = ruleui;
		initialize();
	}
	/**
	 * This method initializes this
	 *
	 */
	private void initialize() {
		this.setSize(new java.awt.Dimension(515, 147));
		this.setContentPane(getUIPanel());
		this.setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000442")/*@res "�����������Ի���"*/);
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
			UILabel2.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000443")/*@res "Ҫ���ǵ�ҳǩ"*/);
			UILabel1 = new UILabel();
			UILabel1.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000444")/*@res "�����ӵ�ҳǩ����"*/);
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setAlignment(java.awt.FlowLayout.LEFT);
			UILabel = new UILabel();
			UILabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000445")/*@res "��ѡ��EXCEL�ļ�"*/);
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
			BnOK.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000446")/*@res "ȷ��"*/);
			BnOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(getUITextField().getText() == null || getUITextField().getText().equals("")){
						MessageDialog.showWarningDlg(ExportExcelDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "��ʾ"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000447")/*@res "Excel�ļ�����Ϊ�գ���ѡ��Excel�ļ���"*/);
						return;
					}
					if (!getUICheckBox().isSelected()) {
						String sheetname = getTSheetName().getText();
						if (sheetname == null || sheetname.trim().length() == 0) {
							MessageDialog.showHintDlg(ExportExcelDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "��ʾ"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000448")/*@res "������һ��ҳǩ�����֡�"*/);
							return;
						}
						for (int i = 0; i < getUIComboBox().getItemCount(); i++) {
							if (sheetname.equals(getUIComboBox().getItemAt(i))) {
								MessageDialog.showHintDlg(ExportExcelDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "��ʾ"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000449")/*@res "�����ҳǩ���������е�ҳǩ�ظ����������"*/);
								return;
							}
						}
						for(int j = 0; j < getUIComboBox().getItemCount(); j++){
							if(sheetname.equalsIgnoreCase(getUIComboBox().getItemAt(j).toString())){
								MessageDialog.showHintDlg(ExportExcelDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "��ʾ"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000450")/*@res "������ͬ���ֵ�ҳǩ���������"*/);
								return;
							}
						}
					}

					String currentBodyTableCode = ruleUI.getBillCardPanel().getCurrentBodyTableCode();
					ReimRuleVO[] reimRuleVos = (ReimRuleVO[]) ruleUI.getBillCardPanel().getBillData().getBodyValueVOs(currentBodyTableCode, ReimRuleVO.class.getName());
					if(reimRuleVos==null || reimRuleVos.length==0){
						MessageDialog.showHintDlg(ExportExcelDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "��ʾ"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000451")/*@res "ѡ�еı�����׼��û�о����ֵ,������ȡ��!"*/);
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
			BnCancel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000064")/*@res "ȡ��"*/);
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
			UIButton.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000452")/*@res "���"*/);
			UIButton.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			UIButton.setPreferredSize(new java.awt.Dimension(40, 20));
			UIButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (getFileChooser().showOpenDialog(ExportExcelDialog.this) == UIFileChooser.APPROVE_OPTION) {
						getUITextField().setText(getFileChooser().getSelectedFile().getPath());
						excelfile = getFileChooser().getSelectedFile();
						try {
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
							MessageDialog.showErrorDlg(ExportExcelDialog.this,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000380")/*@res "����"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000453")/*@res "���ļ�ʱ���ִ��󣬿������ļ���ʽ����������ڱ�ʹ�á�"*/);
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
			UICheckBox.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000454")/*@res "����ԭ��������"*/);
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
			UICheckBox1.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000455")/*@res "�����µ�ҳǩ"*/);
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

		excelfile = getFileChooser().getSelectedFile();
		if(excelfile == null){
			MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "��ʾ"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000456")/*@res "��ѡ��Ŀ��Excel�ļ���"*/);
		}
		try{
			is = new FileInputStream(excelfile);
			if(excelfile.getPath().endsWith(OFFICE03)){
				wb = new HSSFWorkbook(is);
			}else if(excelfile.getPath().endsWith(OFFICE07)){
				wb = new XSSFWorkbook(is);
			}
			Sheet sheet = null;
			//getUICheckBox().isSelected() Ϊtrue ���� ���� �½���ǩ
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
			//��excel�ļ��е����ͷ��Ϣ
			Row row0 = sheet.createRow(0);
			BillItem[] headShowItems = ruleUI.getBillCardPanel().getBodyShowItems();
			BillModel model = ruleUI.getBillCardPanel().getBillModel();

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
			int NumOfColumns = ruleUI.getBillCardPanel().getBillTable().getColumnCount();
			if(NumOfColumns<=0){
				MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "��ʾ"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000457")/*@res "���������ݡ�"*/);
			} else{
				int NumOfRows = ruleUI.getBillCardPanel().getBillTable().getRowCount();
				if(NumOfRows <=0){
					MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "��ʾ"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000457")/*@res "���������ݡ�"*/);
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
			MessageDialog.showHintDlg(this, "",nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000458")/*@res "�������"*/);
		} catch(IOException e) {
			ExceptionHandler.consume(e);
			MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "��ʾ"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000459")/*@res "����ʧ�ܣ��������ļ���ʽ�������Ŀ���ļ����ڱ�ʹ�á�"*/);
		}finally{
			try {
				os.close();
				is.close();
			} catch (IOException e) {
				ExceptionHandler.consume(e);
				MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "��ʾ"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000459")/*@res "����ʧ�ܣ��������ļ���ʽ�������Ŀ���ļ����ڱ�ʹ�á�"*/);
			}
		}

	}
}