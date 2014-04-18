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
import nc.itf.erm.prv.IArapCommonPrivate;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRadioButton;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
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
*          Excel�ļ��ĵ��빦��
*
*/
public class ImportExcelDialog extends UIDialog implements ActionListener{

	/**
	 * �汾����
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
	
	IArapCommonPrivate service = null;
	Map<String,Collection<SuperVO>> valuesMap = null;
	
	private static String OFFICE03 = ".xls";
	private static String OFFICE07 = ".xlsx";
	class ColumnAttr{
		String name;
		String type;
	}
	private static Map<Integer,ColumnAttr> colsMap=new HashMap<Integer,ColumnAttr>();


	private final BillCardPanel panel ;

	ArrayList<ReimRulerVO> reimrules;

	Sheet sheet = null;
	/**
	 * This method initializes
	 *
	 */
	public ImportExcelDialog(Container c,BillCardPanel panel) {
		super(c);
		this.panel = panel;
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
		this.setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000460")/*@res "����������Ի���"*/);
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
			BnOK.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000446")/*@res "ȷ��"*/);
			BnOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						//У�����ݺϷ���
						if(getUITextField().getText()== null || getUITextField().getText().equals("")){
							MessageDialog.showWarningDlg(ImportExcelDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "��ʾ"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000447")/*@res "Excel�ļ�����Ϊ�գ���ѡ��Excel�ļ���"*/);
							return;
						}
						if(getUIComboBox().getItemCount()== 0 && getUIComboBox().getSelectedItem() == null){
							MessageDialog.showWarningDlg(ImportExcelDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "��ʾ"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000461")/*@res "��ѡ��Ҫ�����ҳǩ!"*/);
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
							MessageDialog.showErrorDlg(ImportExcelDialog.this,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000380")/*@res "����"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000453")/*@res "���ļ�ʱ���ִ��󣬿������ļ���ʽ����������ڱ�ʹ�á�"*/);
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
			UILabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000445")/*@res "��ѡ��EXCEL�ļ�"*/);
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
			UILabel1.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000462")/*@res "��ѡ��Ҫ�����ҳǩ"*/);
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
			UILabel2.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000463")/*@res "�����׼"*/);
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
			UILabel3.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000464")/*@res "���뷽ʽ"*/);
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
			RBUseName.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000465")/*@res "������"*/);
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
			RBUseCode.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000466")/*@res "������"*/);
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
			RBIncrement.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000467")/*@res "����"*/);
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
			RBCover.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000468")/*@res "����"*/);
			RBCover.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
		}
		return RBCover;
	}
	/**
	 * @author
	 *
	 * ��excel���е���
	 * @param
	 * @return
	 */
	public ReimRulerVO[] importFromExcel() {

		if(sheet == null)
		sheet = wb.getSheet(String.valueOf(getUIComboBox().getSelectdItemValue()));
		int rowNum = sheet.getLastRowNum();
		
		//�ж�EXCEL��ͷ�����Ƿ�����涨�ĸ�ʽ
		Row row = sheet.getRow(0);
		BillItem[] headShowItems = panel.getBodyShowItems();
		if(headShowItems==null){
			MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000380")/*@res "����"*/, "û�п��Ե�����У�");
			return null;
		}
		for(int j= 0 ; j < row.getLastCellNum();j++){
			Cell cell = row.getCell((short) j);
			if(cell == null){
				MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000380")/*@res "����"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000470",null,new String[]{String.valueOf(j+1)})/*@res "EXCEL��i���Ժ�Ӧ������:"*/);
				return null;
			}
			for(BillItem item:headShowItems){
				//�Զ�������������@def1...����Ҫȥ����׺���ж�
				String columnName = cell.toString();
				String splitname = columnName;
				if(columnName.contains("@"))
					splitname = columnName.substring(0,columnName.indexOf('@'));
				ColumnAttr ca = new ColumnAttr();
				if(splitname.equals(item.getName())){
					ca.name = item.getKey();
					if(item.getDataType() == IBillItem.MONEY || item.getDataType() == IBillItem.DECIMAL){
						ca.type = "Money";
					}
					else if (item.getDataType() != IBillItem.UFREF || item.getMetaDataProperty() == null
						 ||item.getMetaDataProperty().isRefAttribute())
						ca.type = "String";
					else
						ca.type = item.getMetaDataProperty().getRefBusinessEntity().getFullClassName();
					colsMap.put(j, ca);
					break;
				}
			}
		}
		reimrules = new ArrayList<ReimRulerVO>(rowNum);
		for(int i=1;i<=rowNum;i++){
			row = sheet.getRow(i);
			if(row == null){
				MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000473")/*@res "����"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000474")/*@res "�����ڣ�"*/+(i+1)+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000475")/*@res "��Ϊ�գ�����"*/);
				continue;
			}
			ReimRulerVO reimrule = getReimRulerVO(i,row);
			if(reimrule != null)
				reimrules.add(reimrule);
		}
		return reimrules.toArray(new ReimRulerVO[reimrules.size()]);
	}
	
	
	private IArapCommonPrivate getService(){
		if(service ==  null)
			service = NCLocator.getInstance().lookup(IArapCommonPrivate.class);
		return service;
	}
	
	private Collection<SuperVO> getValuesMap(String name,@SuppressWarnings("rawtypes") Class claz) throws BusinessException{
		if(valuesMap ==  null)
			valuesMap = new HashMap<String,Collection<SuperVO>>();
		if(valuesMap.get(name) == null){
			valuesMap.put(name, getService().getVOs(claz, "", false));
		}
		return valuesMap.get(name);
	}
	
	//���ݵ�i�е�ֵ���ñ���VO
	@SuppressWarnings("rawtypes")
	private ReimRulerVO getReimRulerVO(int i,Row row){
		ReimRulerVO reimrule = new ReimRulerVO();
		for(int j=0;j<row.getLastCellNum();j++){
			Cell cell = row.getCell((short) j);
			if(cell == null)
				continue;
			Object cellValue = null;
			cellValue = parseValue(cell,cellValue);
			if(colsMap.get(j)==null)
				continue;
			String defcode = colsMap.get(j).name;
			try {
				if(colsMap.get(j).type.equals("Money")){
					UFDouble amount = UFDouble.ZERO_DBL;
					if(!cellValue.toString().trim().equals("")){
						amount = new UFDouble(Double.parseDouble(cellValue.toString().trim()));
					} 
					reimrule.setAttributeValue(defcode, amount);
					reimrule.setAttributeValue(defcode+"_name", amount.toString());
				}
				//String����ֱ�Ӹ�ֵ����
				else if(colsMap.get(j).type.equals("String")){
					reimrule.setAttributeValue(defcode, cellValue.toString().trim());
					reimrule.setAttributeValue(defcode+"_name", cellValue.toString().trim());
				}
				else{
					//����������Ҫ�����ݿ�������pk
					Class claz = Class.forName(colsMap.get(j).type);
					String bdpk = null;
					Collection<SuperVO> vos = getValuesMap(defcode,claz);
					if(vos!=null){
						Iterator<SuperVO> itVos = vos.iterator();
							while(itVos.hasNext()){
								SuperVO currVO = itVos.next();
								if(getRBUseName().isSelected()){
									if(currVO.getAttributeValue("name").equals(cellValue.toString().trim())){
										bdpk = currVO.getPrimaryKey();
									}

								} else if(currVO.getAttributeValue("code").equals(cellValue.toString().trim())){
									bdpk = currVO.getPrimaryKey();
								}
							}
					}
					reimrule.setAttributeValue(defcode, bdpk);
					reimrule.setAttributeValue(defcode+"_name", cellValue.toString().trim());
				}
			} catch (Exception e) {
				Debug.error(e.getMessage(),e);
				MessageDialog.showErrorDlg(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000380")/*@res "����"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000480",null,new String[]{String.valueOf((i+1)),String.valueOf((j+1))})/*@res "EXCEL���ݸ�ʽ�ڣ�i��,j�������⣺\n"*/+e.getMessage());
			}
		}
		return reimrule;
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