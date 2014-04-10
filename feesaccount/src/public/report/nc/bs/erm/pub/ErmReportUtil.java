package nc.bs.erm.pub;

import java.util.ArrayList;
import java.util.List;

import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.metadata.MetaData;
import nc.utils.fipub.SmartProcessor;
import nc.vo.fipub.report.ReportInitializeVO;
import nc.vo.fipub.report.ReportQueryCondVO;

import com.ufida.dataset.IContext;
import com.ufida.report.anareport.FreeReportContextKey;
import com.ufida.report.anareport.base.BaseQueryCondition;

public class ErmReportUtil {

    public static void processDataSet(IContext context, DataSet ds) {
        processDataSet(context, ds, REPEAT_STEP_DEFAULT);
    }
    
    public static final int REPEAT_STEP_DEFAULT = 1;
    
    public static final int REPEAT_STEP_TWO = 2;
    
    public static void processDataSet(IContext context, DataSet ds, int repeatStep) {
        Object[][] objVals = ds.getDatas();
        if (objVals == null || objVals.length == 0) {
            return;
        }
        BaseQueryCondition qryCondition = (BaseQueryCondition) context
                .getAttribute(FreeReportContextKey.KEY_IQUERYCONDITION);
        Object userQueryCond = qryCondition == null ? null : qryCondition
                .getUserObject();
        ReportQueryCondVO queryVO = null;
        if (userQueryCond instanceof ReportQueryCondVO) {
            queryVO = (ReportQueryCondVO) userQueryCond;
        }
        if (queryVO == null) {
            return;
        }
        
        boolean codeFormat = false;
        if (queryVO.getRepInitContext() != null) {
            String showFormat = ((ReportInitializeVO)queryVO.getRepInitContext().getParentVO()).getShowformat();
            if (IPubReportConstants.SHOW_FORMAT_CODE.equals(showFormat)) {
                codeFormat = true;
            }
        }
        
        boolean isMultiOrg = false;
        if (queryVO.getPk_orgs() == null) {
            if (queryVO.getUserObject() != null) {
                String[] orgs = (String[])queryVO.getUserObject().get("pk_org");
                if (orgs != null && orgs.length > 1 && orgs[1] != null && 
                        orgs[1].indexOf(",") > 0) {
                    isMultiOrg = true;
                }
            }
        } else {
            isMultiOrg = queryVO.getPk_orgs().length > 1;
        }
//        boolean isCurrtype = false;
        MetaData metaData = ds.getMetaData();
        int currtypeIndex = metaData.getIndex("currtype");
        int rnIndex = metaData.getIndex("rn");
        int pkorgIndex = metaData.getIndex("pk_org");
        List<Integer> qryObjIndex = new ArrayList<Integer>();
        List<Integer> qryObjCodeIndex = new ArrayList<Integer>();
        List<Integer> totalCol = new ArrayList<Integer>(); // 小计列
        for (int i = 0; i < queryVO.getQryObjs().size(); i++) {
            qryObjIndex.add(metaData
                    .getIndex(IPubReportConstants.QRY_OBJ_PREFIX + i));
            qryObjCodeIndex.add(metaData
                    .getIndex(IPubReportConstants.QRY_OBJ_PREFIX + i + "code"));
            totalCol.add(qryObjCodeIndex.get(i));
        }
        totalCol.add(currtypeIndex);
        int rn = -1;
        boolean isObj = false;
        int nRows = objVals.length;
        int nValidateRows = 0;
        boolean bOnlyOneRow = true;
        for (int nRow = 0; nRow < nRows; nRow++) {
            Object[] dataRow = objVals[nRow];
            rn = Integer.parseInt(dataRow[rnIndex].toString());
            isObj = false;
            if (rn > SmartProcessor.MAX_ROW) {
                // 处理合计行
                int k = totalCol.size() - 1;
                for (; k >= 0; k--) {
                    if (dataRow[totalCol.get(k)] != null && !"".equals(dataRow[totalCol.get(k)])) {
                        if (k == totalCol.size() - 1) {
                            // 币种
//                          bOnlyOneRow = onlyOneRow(nRow, totalCol.get(k), objVals, rnIndex, repeatStep);
                        } else {
                            // 查询对象
//                            isCurrtype = false;
//                            bOnlyOneRow = onlyOneRow(nRow, totalCol.get(k), objVals, rnIndex, repeatStep);
                            
                            if (codeFormat) {
                                dataRow[qryObjCodeIndex.get(k)] = dataRow[qryObjCodeIndex.get(k)] + IErmReportConstants.getConst_Sub_Total(); // 小计
                              }
                        }
                        isObj = true;
                        break;
                    }
                }
                
                if (!isObj && isMultiOrg) {
                    // 多组织
                    bOnlyOneRow = onlyOneRow(nRow, pkorgIndex, objVals, rnIndex, repeatStep);
                } else if (!isObj) {
                    // 单组织
                    bOnlyOneRow = onlyOneRow(nRow, pkorgIndex, objVals, rnIndex, repeatStep);
                } else {
                    // 查询对象
                    bOnlyOneRow = false;
                }
                if (bOnlyOneRow) {
                    objVals[nRow] = null;
                } else {
                    nValidateRows++;
                }
            } else {
                nValidateRows++;
            }
        }
        
        Object[][] dataNew = new Object[nValidateRows][];
        int nRowNew = 0;
        for (int nRow = 0; nRow < nRows; nRow++) {
            Object[] dataRow = objVals[nRow];
            if (dataRow != null) {
                dataNew[nRowNew++] = dataRow;
            }
        }
        ds.setDatas(dataNew);
    }
    
    private static boolean onlyOneRow(int curRowIndex, int compareCol, Object[][] objVals, int rnIndex, int repeatStep) {
        int rn = -1;
        int preRowIndex = curRowIndex - 1;
        Object[] curDataRow = objVals[curRowIndex];
        String curRowVal = (String)curDataRow[compareCol];
        int rowCount = 0;
        while (preRowIndex >= 0) {
            Object[] preDataRow = objVals[preRowIndex];
            if (preDataRow != null && preDataRow[rnIndex] != null) {
                rn = Integer.parseInt(preDataRow[rnIndex].toString());
                if (rn < SmartProcessor.MAX_ROW) {
                    String preRowVal = (String)preDataRow[compareCol];
                    if (curRowVal == null || curRowVal.equals(preRowVal)) {
                        rowCount++;
                    } else {
                        break;
                    }
                    if (rowCount > repeatStep) {
                        break;
                    }
                }
            }
            preRowIndex--;
        }
        return rowCount <= repeatStep;
    }
}
