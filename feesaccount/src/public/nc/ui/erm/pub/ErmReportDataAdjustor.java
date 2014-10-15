package nc.ui.erm.pub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Logger;
import nc.itf.fi.pub.Currency;
import nc.itf.fipub.report.IPubReportConstants;
import nc.itf.fipub.report.IReportQueryCond;
import nc.itf.iufo.freereport.extend.IAreaCondition;
import nc.itf.org.IOrgConst;
import nc.pub.smart.context.ISmartContextKey;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.metadata.Field;
import nc.pubimpl.fipub.subscribe.ReportVarPool;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.uapbd.CurrencyRateUtilHelper;
import nc.utils.fipub.FipubReportDataAdjustor;
import nc.vo.erm.expenseaccount.ExpenseBalVO;
import nc.vo.erm.pub.IErmReportAnalyzeConstants;
import nc.vo.fipub.report.AggReportInitializeVO;
import nc.vo.fipub.report.FipubBaseQueryCondition;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.fipub.report.ReportInitializeVO;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

import com.ufida.dataset.IContext;
import com.ufida.iufo.table.exarea.ExtendAreaCell;
import com.ufida.iufo.table.exarea.ExtendAreaModel;
import com.ufida.report.anareport.FreeReportContextKey;
import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufida.report.anareport.model.AnaRepField;
import com.ufida.report.anareport.model.AnaReportModel;
import com.ufida.report.crosstable.CrossTableModel;
import com.ufida.report.free.plugin.param.ReportVariable;
import com.ufida.report.free.plugin.param.ReportVariables;
import com.ufsoft.report.IufoFormat;
import com.ufsoft.report.constant.DefaultSetting;
import com.ufsoft.table.AreaPosition;
import com.ufsoft.table.Cell;
import com.ufsoft.table.CellPosition;
import com.ufsoft.table.CellsModel;
import com.ufsoft.table.TableStyle;
import com.ufsoft.table.format.CellAlign;
import com.ufsoft.table.format.CellFont;
import com.ufsoft.table.format.CellLines;
import com.ufsoft.table.format.DefaultDataFormat;
import com.ufsoft.table.format.ICellLine;
import com.ufsoft.table.format.IFormat;
import com.ufsoft.table.header.Header;
import com.ufsoft.table.header.HeaderModel;

public class ErmReportDataAdjustor extends FipubReportDataAdjustor {

    public ErmReportDataAdjustor(IReportQueryCond queryCond) {
        super(queryCond);
    }
    
    private Map<String, String> parseCurrtype(IContext context, DataSet ds) {
        Map<String, String> pkCurrtypeMap = new LinkedHashMap<String, String>();
        int currtypeIndex = ds.getMetaData().getIndex("pk_currtype");
        if (currtypeIndex >= 0) {
            for (int nRow = 0; nRow < ds.getCount(); nRow++) {
                Object currtype = ds.getData(nRow, currtypeIndex);
                if (currtype != null) {
                    pkCurrtypeMap.put(currtype.toString(), null);
                }
            }
        }
        
        ReportQueryCondVO reportQueryCondVO = getQueryCond(context);
        if (reportQueryCondVO != null && reportQueryCondVO.getRepInitContext() != null) {
            AggReportInitializeVO aggReportInitializeVO = reportQueryCondVO.getRepInitContext(); 
            boolean beForeignCurrency = IPubReportConstants.ACCOUNT_FORMAT_FOREIGN
            .equals(((ReportInitializeVO) aggReportInitializeVO.getParentVO()).getReportformat());
            if (beForeignCurrency) {
                pkCurrtypeMap.clear();
            }
        }
        
        if (pkCurrtypeMap.keySet().size() == 0 && reportQueryCondVO != null) {
            //获取组织本位币
            reportQueryCondVO = getQueryCond(context);
            String localCurrType = reportQueryCondVO.getLocalCurrencyType();
            String[] pkOrgs = null;
            if ("org_local".equals(localCurrType)) {
                pkOrgs = reportQueryCondVO.getPk_orgs();
            } else if ("group_local".equals(localCurrType)) {
                LoginContext loginContext = (LoginContext)context.getAttribute("key_private_context");
                pkOrgs = new String[] {loginContext.getPk_group()};
            } else {
                pkOrgs = new String[] {IOrgConst.GLOBEORG};
            }
            if (pkOrgs != null && pkOrgs.length > 0) {
                for (String pkOrg : pkOrgs) {
                    String pkCurrtype = CurrencyRateUtilHelper.getInstance().getLocalCurrtypeByOrgID(pkOrg);
                    if (pkCurrtype != null) {
                        pkCurrtypeMap.put(pkCurrtype, null);
                    }
                }                
            }
        }
        return pkCurrtypeMap;
    }
    
    private void setReportVariableCurr(IContext context, DataSet ds,
            AbsAnaReportModel reportModel) {
        ReportVariables varPool = (ReportVariables)context.getAttribute(IPubReportConstants.REPORT_VAR_POOL);
        if (varPool != null) {
            Map<String, String> pkCurrtypeMap = parseCurrtype(context, ds);
            ReportVariable var = varPool.getVariable("currtype");
            if (var == null) {
                var = varPool.getVariable(ExpenseBalVO.PK_CURRTYPE);
            }
            if (var == null) {
                var = varPool.getVariable("currency");
            }
            if (var != null) {
                StringBuilder sbCurrName = new StringBuilder();
                Iterator<Entry<String, String>> iter = pkCurrtypeMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry<String, String> entry = iter.next();
                    if (StringUtils.isEmpty(entry.getKey())) {
                        continue;
                    }
                    try {
                        sbCurrName.append(Currency.getCurrNameByPk(entry.getKey()));
                        sbCurrName.append(",");
                    } catch (BusinessException e) {
                        Logger.error(e.getMessage(), e);
                    }
                }
                if (sbCurrName.length() > 0) {
                    sbCurrName.deleteCharAt(sbCurrName.length() - 1);
                }
                var.setValue(sbCurrName.toString());
            }
        }
    }
    
    private void setReportVariablePeriod(IContext context, DataSet ds,
            AbsAnaReportModel reportModel) {

        //期间>
        ReportVariables varPool = ReportVariables.getInstance(reportModel.getFormatModel());
        ReportVariable var = null;
        var = varPool.getVariable(ReportVarPool.QUERY_PERIOD_TITLE);
        if (var != null) {
            var.setValue(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("arap","1arap0006")/*@res "期间："*/);
        }

        String dateFmt = (String) context.getAttribute(ISmartContextKey.KEY_TIME_FORMAT);
        Object objTimeZone = context.getAttribute(FreeReportContextKey.REPORT_EXEC_TIMEZONE);
        List<String> periodParams = new ArrayList<String>();
        if (queryCond instanceof ReportQueryCondVO && IPubReportConstants.QUERY_MODE_MONTH.equals(((ReportQueryCondVO) queryCond).getQryMode())) {
            // 按月份查询
            ReportQueryCondVO tempVO = (ReportQueryCondVO) queryCond;
            AccountCalendar calendar = AccountCalendar.getInstanceByPk_org(tempVO.getPk_orgs()[0]);
            try {
                if (tempVO.getBeginDate() != null) {
                    calendar.setDate(tempVO.getBeginDate());
                    periodParams.add(calendar.getMonthVO().getYearmth());
                }
                if (tempVO.getEndDate() != null) {
                    calendar.setDate(tempVO.getEndDate());
                    periodParams.add(calendar.getMonthVO().getYearmth());
                }
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
            }
        } else {
            if(objTimeZone != null && objTimeZone instanceof TimeZone){
                TimeZone timeZone = (TimeZone) objTimeZone;
                String sBeginDate = queryCond.getBeginDate() == null ? null : queryCond.getBeginDate().toStdString(timeZone);
                UFDate dBeginDate = null;
                if (sBeginDate != null) {
                    dBeginDate = new UFDate(sBeginDate, timeZone);
                }
                periodParams.add(PubCommonReportMethod.format(dateFmt, dBeginDate));
                
                String sEndDate = queryCond.getEndDate() == null ? null : queryCond.getEndDate().toStdString(timeZone);
                UFDate dEndDate = null;
                if (sEndDate != null) {
                    dEndDate = new UFDate(sEndDate, timeZone);
                }
                periodParams.add(PubCommonReportMethod.format(dateFmt, dEndDate));
            }else{
                periodParams.add(PubCommonReportMethod.format(dateFmt, queryCond.getBeginDate()));
                periodParams.add(PubCommonReportMethod.format(dateFmt, queryCond.getEndDate()));
            }
        }

        String period = null;
        if (queryCond.getBeginDate() != null && queryCond.getEndDate() != null) {
            period = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201008v6_0", "0201008v6-0121", null, periodParams.toArray(new String[0]))/*@res "{0}至{1}" */;
        } else if (queryCond.getBeginDate() != null) {
            period = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201008v6_0", "0201008v6-0122", null, periodParams.toArray(new String[0]))/* @res "{0}以上" */;
        } else if (queryCond.getEndDate() != null) {
            period = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201008v6_0", "0201008v6-0123", null, periodParams.toArray(new String[0]))/* @res "{0}以内" */;
        }

        var = varPool.getVariable(ReportVarPool.QUERY_PERIOD);
        if (var != null) {
            var.setValue(period);
        }
        
        //截止日期
        var = varPool.getVariable(ReportVarPool.QUERY_DEADLINE);
        UFDateTime busiDate = new UFDateTime(InvocationInfoProxy.getInstance().getBizDateTime());
        UFDate busiUFDate = busiDate.getDate();
        UFDate dateLineUFDate = queryCond.getDateline();
        UFDate computeUFDate = null;
        
        if(var != null && queryCond instanceof ReportQueryCondVO && 
                IErmReportAnalyzeConstants.getACC_ANA_PATTERN_FINAL().equals(((ReportQueryCondVO)queryCond).getAnaPattern())){
            computeUFDate = busiUFDate;
        }else{
            if (var != null && dateLineUFDate != null) {
                computeUFDate = dateLineUFDate;
            }
        } 
        String sEndDate = null;
        if(objTimeZone != null && objTimeZone instanceof TimeZone && computeUFDate != null){
            TimeZone timeZone = (TimeZone) objTimeZone;
            String sDate = computeUFDate.toStdString(timeZone);
            UFDate dDate = null;
            if (sDate != null) {
                dDate = new UFDate(sDate, timeZone);
            }
            sEndDate = PubCommonReportMethod.format(dateFmt, dDate);
        }else{
            sEndDate = PubCommonReportMethod.format(dateFmt, computeUFDate);
        }
        if (var != null) {
            var.setValue(sEndDate);
        }
    }

//    public final int[] DEFAULT_LINES_TYPE = new int[] {
//            TableConstant.UNDEFINED, TableConstant.L_SOLID4,
//            TableConstant.UNDEFINED, TableConstant.UNDEFINED,
//            TableConstant.UNDEFINED, TableConstant.UNDEFINED,
//            TableConstant.UNDEFINED, TableConstant.UNDEFINED };
    
    private void formatTitle(int nStartRow, int extRow, AbsAnaReportModel reportModel ) {
        CellsModel formatModel = reportModel.getFormatModel();
        List<List<Cell>> cells = formatModel.getCells(); 
        ExtendAreaCell[] extendAreaCell = reportModel.getExtendAreaModel().getExtendAreaCells();
        int endColumn = extendAreaCell[1].getArea().getEnd().getColumn();
        for (int nRow = nStartRow; nRow < extRow; nRow++) {
            List<Cell> rowList = cells.get(nRow);
            for (int nCol = 0; nCol < endColumn; nCol++) {
                Cell cell = rowList.get(nCol);
                if (cell != null) {
                    ReportVariable var = (ReportVariable)cell.getExtFmt("report_var_info");
                    if (var != null) {
                        Cell left = rowList.get(nCol - 1);
                        left.setValue(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                                "erm_report", "1erm_report0196"));/* @res "业务单元：" */
                    }
                }
            }
        }
    }
    
    private void formatCell(IContext context, DataSet ds,
            AbsAnaReportModel reportModel) {

        CellsModel formatModel = reportModel.getFormatModel();
        ExtendAreaCell[] extendAreaCell = reportModel.getExtendAreaModel().getExtendAreaCells();
        List<List<Cell>> cells = formatModel.getCells(); 
        int extRow = 0;
        int nStartRow = 1;
        if (extendAreaCell.length == 1) {
            extRow = extendAreaCell[0].getArea().getStart().getRow();
        } else {
            nStartRow = extendAreaCell[0].getArea().getEnd().getRow();
            extRow = extendAreaCell[1].getArea().getStart().getRow();
            formatTitle(nStartRow, extRow, reportModel);
        }
        
        HeaderModel headerModel = formatModel.getColumnHeaderModel();
        int nHeaderIndex = 0;
        for (Header header : headerModel.getHeaders()) {
        	//当列被隐藏,即列的长度为0时，对应的列如果有变量字段，则需要合并单元格，达到不被隐藏
            if (header.getSize() == TableStyle.MINHEADER) {
//                int extRow = reportModel.getExtendAreaModel().getExtendAreaCells()[0].getArea().getStart().getRow();
                for (nStartRow = 1; nStartRow < extRow; nStartRow++) {
                    List<Cell> rowList = cells.get(nStartRow);
                    
                    //第一次合并后，cell为null，则不需要合并，解决连续合并造成问题
                    if(rowList.get(nHeaderIndex) == null){
                    	continue;
                    }
                    
                    Cell pre = rowList.get(nHeaderIndex - 1);
                    Cell next = rowList.get(nHeaderIndex + 1);
                    ReportVariable preVar = null;
                    if (pre != null) {
                        preVar = (ReportVariable)pre.getExtFmt("report_var_info");
                    }
                    ReportVariable nextVar = null;
                    if (next != null) {
                        nextVar = (ReportVariable)next.getExtFmt("report_var_info");
                    }
                    
                    if ((pre == null || pre.getValue() == null) && preVar == null) {
                        if (nextVar == null || nextVar.getValue() == null) {
                            //值单元格，没有值时，不处理
                            continue;
                        }

                        AreaPosition areaPosition = AreaPosition.getInstance(nStartRow, nHeaderIndex - 1, 2, 1);
                        AreaPosition toMove = AreaPosition.getInstance(nStartRow, nHeaderIndex , 1, 1);
                        formatModel.moveCells(toMove, CellPosition.getInstance(nStartRow, nHeaderIndex - 1));
                        formatModel.getCombinedAreaModel().combineCell(areaPosition);
                    } else if (next == null || next.getValue() == null) {
                        AreaPosition areaPosition = AreaPosition.getInstance(nStartRow, nHeaderIndex, 2, 1);
                        formatModel.getCombinedAreaModel().combineCell(areaPosition);
                    }
                }
                break;
            }
            nHeaderIndex++;
        }
        
        ReportVariables varPool = (ReportVariables)context.getAttribute(IPubReportConstants.REPORT_VAR_POOL);
        if (varPool != null) {
            Map<String, String> excludeCell = new HashMap<String, String>();
            for (int nPos = 1; nPos <= 5; nPos++) {
                String varName = "query_obj" + nPos + "_title";
                ReportVariable var = varPool.getVariable(varName);
                if (var != null) {
                    AreaPosition area = (AreaPosition)var.getArea();
                    if (area == null) {
                        continue;
                    }
                    CellPosition cellPos = area.getStart();
                    ICellLine cellLine = null;
                    if (var.getValue() == null || 
                            "".equals(var.getValue().toString().trim())) {
                        cellLine = CellLines.getInstance(DefaultSetting.NO_LINES_TYPE, DefaultSetting.NO_LINES_COLOR);
                    } else {
                        excludeCell.put(cellPos.getRow() + "_" + cellPos.getColumn(), null);
                        excludeCell.put(cellPos.getRow() + "_" + (cellPos.getColumn() - 1), null);
                        excludeCell.put(cellPos.getRow() + "_" + (cellPos.getColumn() + 1), null);
                        continue;
                    }
                    Cell cell = reportModel.getFormatModel().getCell(cellPos);
                    if (cell == null) {
                        continue;
                    }
                    IFormat format = cell.getFormat();
                    if (format == null) {
                        format = IufoFormat.getInstance(DefaultDataFormat.getInstance(), CellFont
                                .getInstance(), CellAlign.getInstance(), cellLine);
                    } else {
                        format = IufoFormat.getInstance(format.getDataFormat(), 
                                format.getFont(), format.getAlign(), cellLine);
                    }
                    if (!excludeCell.containsKey(cellPos.getRow() + "_" + cellPos.getColumn())) {
                        CellPosition celPosVal = CellPosition.getInstance(cell.getRow(), cell.getCol() + 1);
                        if (celPosVal != null) {
                            Cell cellVal = reportModel.getFormatModel().getCell(celPosVal);
                            if (cellVal != null &&
                                    !excludeCell.containsKey(celPosVal.getRow() + "_" + celPosVal.getColumn())) {
                                cellVal.setFormat(format);
                            }
                        }
                        cell.setFormat(format);
                    }
                }
            }
        }
    }
    
    @Override
    public DataSet doAdjustDataSet(String areaPK, IContext context, DataSet ds,
            AbsAnaReportModel reportModel) {
        setReportVariableCurr(context, ds, reportModel);
        formatCell(context, ds, reportModel);
        DataSet dataSet = super.doAdjustDataSet(areaPK, context, ds, reportModel);
        setReportVariablePeriod(context, ds, reportModel);
        return dataSet;
    }

    @Override
    public CrossTableModel doAdjustCrossHeader(String areaPK, IContext context,
            CrossTableModel crossTabel, AbsAnaReportModel reportModel) {
        return super.doAdjustCrossHeader(areaPK, context, crossTabel, reportModel);
    }
    
    private ReportQueryCondVO getQueryCond(IContext context) {
        ReportQueryCondVO reportQueryCondVO = (ReportQueryCondVO)context.getAttribute("reportquerycondvo");
        if (reportQueryCondVO == null) {
            FipubBaseQueryCondition qryCon = (FipubBaseQueryCondition)context.getAttribute("freereport_querycondition_innode");
            if (qryCon != null) {
                reportQueryCondVO = (ReportQueryCondVO)qryCon.getQryCondVO();
            }
        }
        return reportQueryCondVO;
    }

    @Override
    public void doAreaAdjust(IContext context, String areaPK,
            IAreaCondition areaCond, AbsAnaReportModel reportModel) {
        super.doAreaAdjust(context, areaPK, areaCond, reportModel);
        
        ReportQueryCondVO reportQueryCondVO = getQueryCond(context);
        if (reportQueryCondVO == null || reportQueryCondVO.getRepInitContext() == null) {
            return;
        }
        if ((Boolean)reportQueryCondVO.getUserObject().get("isPkorgSameAssumeOrg")) {
            ExtendAreaModel exModel = ExtendAreaModel.getInstance(reportModel.getCellsModel());
            CellPosition[] cellPosArr = exModel.getExtendAreaCells()[0].getArea().split();
            for (CellPosition cellPos : cellPosArr) {
                AnaRepField fld = (AnaRepField) exModel.getExtendAreaCells()[0].getCellInfoSet().getExtInfo(cellPos, AnaRepField.EXKEY_FIELDINFO);
                if (fld != null && fld.getFldname().equals("org")) {
                    CellPosition pos = cellPos.getMoveArea(-1, 0);
                    Cell cell = reportModel.getFormatModel().getCell(pos);
                    cell.setValue(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                            "erm_report", "1erm_report0195"));/* @res "业务单元" */
                    break;
                }
            }
        }
        AggReportInitializeVO aggReportInitializeVO = reportQueryCondVO.getRepInitContext(); 
        boolean beForeignCurrency = IPubReportConstants.ACCOUNT_FORMAT_FOREIGN
            .equals(((ReportInitializeVO) aggReportInitializeVO.getParentVO()).getReportformat());
        if (!beForeignCurrency) {

//            String pk_report = (String)context.getAttribute(FreeReportContextKey.REPORT_PK);
//            Boolean bHideField = (Boolean)context.getAttribute(pk_report);
//            if (bHideField == null) {
//                bHideField = Boolean.valueOf(false);
//                context.setAttribute(pk_report, bHideField);
//            }
//            if (!bHideField) {
                int curtypePos = -1;
                ExtendAreaModel exModel = ExtendAreaModel.getInstance(reportModel.getCellsModel());
                CellPosition[] cellPosArr = exModel.getExtendAreaCells()[0].getArea().split();
                for (CellPosition cellPos : cellPosArr) {
                    AnaRepField fld = (AnaRepField) exModel.getExtendAreaCells()[0].getCellInfoSet().getExtInfo(cellPos, AnaRepField.EXKEY_FIELDINFO);
                    if (fld != null && fld.getFldname().equals("currtype")) {
                        curtypePos = cellPos.getColumn();
                        HeaderModel headerModel = reportModel.getCellsModel().getColumnHeaderModel();
                        headerModel.setSize(curtypePos, TableStyle.MINHEADER);
                        headerModel.resetSizeCache();
                        break;
                    }
                }

                ErmLoadUserIndividuation loadUserIndividuation = new ErmLoadUserIndividuation(context, reportModel);
                loadUserIndividuation.loadUserIndiv(curtypePos);
//                bHideField = Boolean.valueOf(true);
//                context.setAttribute(pk_report, bHideField);
//            }
        }
    }
    
    public Integer parseCurrtypeIndex(AbsAnaReportModel anaReportModel, String exAreaPk) {
        //取扩展区域模型
          ExtendAreaModel exAreaModel = ExtendAreaModel.getInstance(anaReportModel.getCellsModel());

          //根据扩展区pk取扩展区
          ExtendAreaCell exAreaCell = exAreaModel.getExAreaByPK(exAreaPk);

          //取扩展区所在区域
          AreaPosition area = exAreaCell.getArea();

          int curtypeIndex = -1;
          for(CellPosition pos : area.split()){
              //取 pos 位置的字段信息
              AnaRepField anaRepField = (AnaRepField) anaReportModel.getCellsModel().
                      getCellIfNullNew(pos.getRow(), pos.getColumn()).getExtFmt(AnaRepField.EXKEY_FIELDINFO);
              Map<String, Object> map = anaReportModel.getExtendAreaModel().getExtendAreaCells()[0].getCellInfoSet().getExtInfo(pos);
              if (map == null) {
                  continue;
              }
              anaRepField = (AnaRepField)map.get(AnaRepField.EXKEY_FIELDINFO);
              if(null == anaRepField){
                  continue ;
              }
              Field fld = anaRepField.getField();
              if ("currtype".equals(fld.getExpression())) {
                  curtypeIndex = pos.getColumn();
                  break;
              }
          }
          return curtypeIndex;
      }

    @Override
    public void doReportAdjust(IContext context, AnaReportModel reportModel) {
        super.doReportAdjust(context, reportModel);
    }

}