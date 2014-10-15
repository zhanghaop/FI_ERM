package nc.ui.erm.costshare.ui;

import java.awt.Container;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.querytemplate.IQueryTemplateTotalVOProcessor;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.filtereditor.IFilterEditorFactory;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.querytemplate.meta.IFilterMeta;
import nc.ui.querytemplate.normalpanel.INormalQueryPanel;
import nc.ui.querytemplate.valueeditor.IFieldValueElementEditor;
import nc.ui.querytemplate.valueeditor.IFieldValueElementEditorFactory;
import nc.ui.querytemplate.valueeditor.RefElementEditor;
import nc.ui.querytemplate.valueeditor.UIRefpaneCreator;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.query.QueryConditionVO;
import nc.vo.pub.query.QueryTempletTotalVO;
import nc.vo.querytemplate.TemplateInfo;

/**
 * @author luolch
 * 
 * 报销查询对话框（新查询模板实现）
 * 
 * nc.ui.arap.bx.BxQueryDLG
 */
public class CostShareQueryDLG extends QueryConditionDLG {

	private static final long serialVersionUID = 1L;

	/**
	 * @param parent
	 * @param normalPnl
	 * @param ti
	 * @param title
	 * @param jkNode
	 *            是否只处理借款节点 ，管理节点为false
	 * @param bxNode
	 *            是否只处理报销节点 ，管理节点为false
	 */
	public CostShareQueryDLG(Container parent, INormalQueryPanel normalPnl,
			TemplateInfo ti, String title, 
			String nodecode) {

		super(parent, normalPnl, ti, title);

		initBxQueryDialog(parent, nodecode);

		try {
			getQryCondEditor().initUIData();
		} catch (Exception e) {
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
	}


	private void initBxQueryDialog(Container parent,  final String nodecode) {

		registerQueryTemplateTotalVOProceeor(new IQueryTemplateTotalVOProcessor() {
			public void processQueryTempletTotalVO(QueryTempletTotalVO totalVO) {


				QueryConditionVO[] conds = totalVO.getConditionVOs();

				for (int i = 0; i < conds.length; i++) {
					String fieldCode = conds[i].getFieldCode();
					if ((CostShareVO.PK_ORG).equals(fieldCode)) {
						conds[i].setIfDefault(UFBoolean.TRUE);
						conds[i].setValue(getPk_corp());
					}

					if ((CostShareVO.BILLDATE).equals(fieldCode)) {
						UFDate date = WorkbenchEnvironment.getInstance().getBusiDate();
						UFDate dateBefore = date.getDateBefore(30);
						conds[i].setValue(dateBefore + "," + date);
						conds[i].setIfMust(UFBoolean.TRUE);
					}
					
				}

			}
		});

		// 注册单据类型参照
		registerFieldValueEelementEditorFactory(new IFieldValueElementEditorFactory() {
			public IFieldValueElementEditor createFieldValueElementEditor(
					FilterMeta meta) {
				if (meta.getFieldCode().endsWith(CostShareVO.SZXMID)) {
					UIRefPane refPane = new UIRefpaneCreator(getQueryContext())
							.createUIRefPane(meta);
					refPane.setWhereString("( pk_corp='" + refPane.getPk_corp()
							+ "') and  isexpensepro = 'Y'");
					return new RefElementEditor(refPane, meta.getReturnType());
				}

				// 增加是否显示封存按钮
				else if (CostShareVO.DEPTID.equals(meta.getFieldCode())) {
					UIRefPane refPane = new UIRefpaneCreator(getQueryContext())
							.createUIRefPane(meta);
					refPane.setDisabledDataButtonShow(true);
					return new RefElementEditor(refPane, meta.getReturnType());
				}

				else if (CostShareVO.JOBID.equals(meta.getFieldCode())) {
					UIRefPane refPane = new UIRefpaneCreator(getQueryContext())
							.createUIRefPane(meta);
					refPane.setDisabledDataButtonShow(true);
					return new RefElementEditor(refPane, meta.getReturnType());
				}

				else if (CostShareVO.JSFS.equals(meta.getFieldCode())) {
					UIRefPane refPane = new UIRefpaneCreator(getQueryContext())
							.createUIRefPane(meta);
					refPane.setDisabledDataButtonShow(true);
					return new RefElementEditor(refPane, meta.getReturnType());
				}

				else if (CostShareVO.FKYHZH.equals(meta.getFieldCode())) {
					UIRefPane refPane = new UIRefpaneCreator(getQueryContext())
							.createUIRefPane(meta);
					refPane.setDisabledDataButtonShow(true);
					return new RefElementEditor(refPane, meta.getReturnType());
				} else if (CostShareVO.SZXMID.equals(meta.getFieldCode())) {
					UIRefPane refPane = new UIRefpaneCreator(getQueryContext())
							.createUIRefPane(meta);
					refPane.setDisabledDataButtonShow(true);
					return new RefElementEditor(refPane, meta.getReturnType());
				}  else if (CostShareVO.BZBM.equals(meta.getFieldCode())) {
					UIRefPane refPane = new UIRefpaneCreator(getQueryContext())
							.createUIRefPane(meta);
					refPane.setDisabledDataButtonShow(true);
					return new RefElementEditor(refPane, meta.getReturnType());
				}

				

				return null;
			}
		});

		getQueryContext().getFilterEditorManager().registerFilterEditorFactory(
				new IFilterEditorFactory() {
					public IFilterEditor createFilterEditor(IFilterMeta meta) {
						return null;
					}
				});
	}

	protected String getPk_corp() {
		return getTempInfo().getPk_Org();
	}

}