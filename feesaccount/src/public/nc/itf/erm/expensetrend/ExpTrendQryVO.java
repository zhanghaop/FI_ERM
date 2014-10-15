package nc.itf.erm.expensetrend;

import java.io.Serializable;

import nc.vo.bd.inoutbusiclass.InoutBusiClassVO;
import nc.vo.erm.expenseaccount.ExpenseBalVO;
import nc.vo.erm.pub.ErmBaseQueryCondition;
import nc.vo.fipub.report.QryObj;
import nc.vo.org.DeptVO;
import nc.vo.resa.costcenter.CostCenterVO;

public class ExpTrendQryVO implements Serializable{

	private static final long serialVersionUID = 1L;
	private QryObj qryobj;
	private String sqlWhere;
	private nc.pub.smart.data.DataSet dataSet;
	
	private ErmBaseQueryCondition ermBaseQueryCondition;

	public ErmBaseQueryCondition getErmBaseQueryCondition() {
        return ermBaseQueryCondition;
    }
    public void setErmBaseQueryCondition(ErmBaseQueryCondition ermBaseQueryCondition) {
        this.ermBaseQueryCondition = ermBaseQueryCondition;
    }
    public void setSqlWhere(String sqlWhere) {
		this.sqlWhere = sqlWhere;
	}
	public String getSqlWhere() {
		return sqlWhere;
	}
	public void setQryobj(QryObj qryobj) {
		this.qryobj = qryobj;
	}
	public QryObj getQryobj() {
		return qryobj;
	}
	
	public void convertQryObjVO(String reportcode){
		qryobj = new QryObj();
		//收支项目
		if("erm_exptrend".equals(reportcode)){
			qryobj.setOriginFld(ExpenseBalVO.PK_IOBSCLASS);
			qryobj.setBd_pkField(InoutBusiClassVO.PK_INOUTBUSICLASS);
			qryobj.setBd_nameField(InoutBusiClassVO.NAME);
			qryobj.setBd_codeField(InoutBusiClassVO.CODE);
			qryobj.setBd_table(InoutBusiClassVO.getDefaultTableName());
		}else if("erm_exptrenddept".equals(reportcode)){
		//部门	
			qryobj.setOriginFld(ExpenseBalVO.ASSUME_DEPT);
			qryobj.setBd_pkField(DeptVO.PK_DEPT);
			qryobj.setBd_nameField(DeptVO.NAME);
			qryobj.setBd_codeField(DeptVO.CODE);
			qryobj.setBd_table(DeptVO.getDefaultTableName());
		}else if("erm_exptrendcorp".equals(reportcode)){
		//成本中心	
			qryobj.setOriginFld(ExpenseBalVO.PK_RESACOSTCENTER);
			qryobj.setBd_pkField(CostCenterVO.PK_COSTCENTER);
			qryobj.setBd_nameField(CostCenterVO.CCNAME);
			qryobj.setBd_codeField(CostCenterVO.CCCODE);
			qryobj.setBd_table(CostCenterVO.getDefaultTableName());
		}
	}
	public void setDataSet(nc.pub.smart.data.DataSet dataSet) {
		this.dataSet = dataSet;
	}
	public nc.pub.smart.data.DataSet getDataSet() {
		return dataSet;
	}

}
