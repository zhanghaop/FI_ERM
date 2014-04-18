package nc.ui.erm.pub;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.vo.iufo.freereport.FreeRepUserIndivVO;
import nc.vo.iufo.freereport.FreeReportBox;

import com.ufida.dataset.IContext;
import com.ufida.iufo.table.exarea.ExCellInfoSet;
import com.ufida.iufo.table.exarea.ExtendAreaCell;
import com.ufida.iufo.table.exarea.ExtendAreaModel;
import com.ufida.report.anareport.FreePrivateContextKey;
import com.ufida.report.anareport.FreeReportContextKey;
import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufida.report.anareport.model.AnaRepField;
import com.ufida.report.free.userdef.FrAreaFieldWidths;
import com.ufida.report.free.userdef.FrReportUserDimInfo;
import com.ufsoft.table.AreaPosition;
import com.ufsoft.table.CellPosition;
import com.ufsoft.table.PrintSetModel;
import com.ufsoft.table.TableStyle;
import com.ufsoft.table.event.HeaderEvent;
import com.ufsoft.table.event.PropertyChangeEventAdapter;
import com.ufsoft.table.header.Header;
import com.ufsoft.table.header.HeaderModel;
import com.ufsoft.table.print.PrintSet;

public class ErmLoadUserIndividuation {
    /**
     * 上下文参数
     * @since ncv63
     */
    private IContext context = null;
    
    /**
     * 分析模型基类
     * @since ncv63
     */
    private AbsAnaReportModel anaReportModel = null ;
    
    public ErmLoadUserIndividuation(IContext context ,AbsAnaReportModel anaReportModel) {
        this.context = context ;
        this.anaReportModel = anaReportModel ;
    }
    
    /**
     * 加载用户个性化设置信息
     * @param cellsModel 表格模型
     */
    public void loadUserIndiv(int hideFldIndex){
        //报表pk
        String pk_report = (String)context.getAttribute(FreeReportContextKey.REPORT_PK);
        //登录用户pk
//        String pk_user = (String)context.getAttribute(FreeReportContextKey.LOGIN_USER_ID);
        //获取用户个性化设置信息
        FreeRepUserIndivVO[] userIndivVOs = null;
        FreeReportBox box = (FreeReportBox) context.getAttribute(
                FreePrivateContextKey.REPORT_FREEREPORTBOX);
        if (box != null) {
            Map<String, FreeRepUserIndivVO[]> userIndivVOsMap = box.getUserIndivVOsMap();
            userIndivVOs = userIndivVOsMap.get(pk_report);
        }
        if(userIndivVOs == null){
//            try {
//                userIndivVOs = getUserIndivQryService().queryUserIndivs(pk_report, pk_user);
//            } catch (UFOSrvException e) {
//                AppDebug.error(e);
//            }
            return;
        }
        
        if(null == userIndivVOs || 0 == userIndivVOs.length ){
            return ;
        }
        FreeRepUserIndivVO userIndivVO = userIndivVOs[0];
        FrReportUserDimInfo userDimInfo =(FrReportUserDimInfo) userIndivVO.getUserindiv();
        if(null == userDimInfo){
            return ;
        }
        //对扩展区中字段列宽进行设置
        loadFieldWidths(userDimInfo, hideFldIndex);
        
        //对打印设置信息进行加载
        loadPrintSet(userDimInfo);
    }
    
    
    
    /**
     * 加载字段列宽信息
     * @param userDimInfo 个性化信息
     * @param cellsModel  表格模型
     * @modify by zhujhc 20130513 补充添加图表列宽信息(目前也只处理只有一个图表的情况)
     * @since ncv63
     */
    private void loadFieldWidths(FrReportUserDimInfo userDimInfo, int hideFldIndex){
        List<FrAreaFieldWidths> areaWidthsList = getAreaWidthsList(userDimInfo);
        if(null == areaWidthsList || areaWidthsList.size() == 0){
            return ;
        }
        Map<String,Integer> fieldPosMap = parseFieldIndexMap(areaWidthsList.get(0));
        for(FrAreaFieldWidths areaWidths : areaWidthsList){
            if(null == areaWidths){
                continue ;
            }
            //取列宽信息
            HashMap<String,Integer> fieldWidths = areaWidths.getFieldWidths();
            if(fieldWidths == null) {
                continue;
            }
            //取扩展区pk
            String exAreaPk = areaWidths.getAreaPK();

            //取扩展区域模型
            ExtendAreaModel exAreaModel = ExtendAreaModel.getInstance(anaReportModel.getCellsModel());
            if(null == exAreaModel){
                return ;
            }
            //根据扩展区pk取扩展区
            ExtendAreaCell exAreaCell = exAreaModel.getExAreaByPK(exAreaPk);
            if(null == exAreaCell){
                continue ;
            }
            //取扩展区所在区域
            AreaPosition area = exAreaCell.getArea();
            if(null == area){
                continue ;
            }

            HeaderModel headerModel = anaReportModel.getCellsModel().getColumnHeaderModel();
            Iterator<Entry<String, Integer>> iter = fieldPosMap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, Integer> entry = iter.next();
                Integer width = fieldWidths.get(entry.getKey());
                if (hideFldIndex == entry.getValue()) {
                    headerModel.setSize(entry.getValue(), TableStyle.MINHEADER);
                } else {
                    headerModel.setSize(entry.getValue(), width);
                }
            }
            
//            Header[] headersOri = anaReportModel.getCellsModel().getColumnHeaderModel().getHeaders();
//            Header[] headersNew = null;
//            if (hideFldIndex >= 0) {
//                headersNew = new Header[headersOri.length - 1];
//            } else {
//                headersNew = headersOri;
//            }
//            
//            for (int nPos = 0, nPosNew = 0; nPos < headersOri.length; nPos++) {
//                if (nPos == hideFldIndex) {
//                    setSize(anaReportModel.getCellsModel().getColumnHeaderModel(), nPos, 0);
//                    nPosNew++;
//                    continue;
//                }
//                headersNew[nPosNew] = headersOri[nPos];
//                if (headersNew[nPosNew].isUserDefSize()) {
//                    setSize(anaReportModel.getCellsModel().getColumnHeaderModel(), nPosNew, headersNew[nPosNew].getSize());
//                }
//                nPosNew++;
//            }
        }
    }
    
    private Map<String, Integer> parseFieldIndexMap(FrAreaFieldWidths areaWidths) {
        HashMap<String,Integer> fieldWidths = areaWidths.getFieldWidths();
        HashMap<String,Integer> fieldPosMap = new HashMap<String,Integer>();
        int fieldPos;
        ExtendAreaModel exModel = ExtendAreaModel.getInstance(anaReportModel.getCellsModel());
        if (exModel.getExtendAreaCells() != null && exModel.getExtendAreaCells().length > 0) {
            CellPosition[] cellPosArr = exModel.getExtendAreaCells()[0].getArea().split();
            ExCellInfoSet exCellInfoSet = exModel.getExtendAreaCells()[0].getCellInfoSet();
            for (CellPosition cellPos : cellPosArr) {
                AnaRepField fld = (AnaRepField) exCellInfoSet.getExtInfo(cellPos, AnaRepField.EXKEY_FIELDINFO);
                if (fld != null && fieldWidths.containsKey(fld.getFldname())) {
                    fieldPos = cellPos.getColumn();
                    fieldPosMap.put(fld.getFldname(), fieldPos);
                    if (fieldPosMap.size() == fieldWidths.size()) {
                        break;
                    }
                }
            }            
        }
        return fieldPosMap;
    }
    
    public void setSize(HeaderModel headerModel, int index, int size) {

        Header h = headerModel.getHeader(index);

        Header hh = Header.getInstance(h.getValue(), size, h.getFormat(), true);
        headerModel.setHeader(index, hh);

        // liuyy, size事件。
        PropertyChangeEventAdapter evt = new PropertyChangeEventAdapter(
                new Header[] { hh }, HeaderEvent.HEADER_SIZE_PROPERTY, null,
                null);
        headerModel.fireHeaderPropertyChanged(evt);
    }
    
    /**
     * 获得用户个性化设置信息列宽信息的list
     * @param userDimInfo 用户个性化设置信息
     * @return list 
     */
    private List<FrAreaFieldWidths> getAreaWidthsList(FrReportUserDimInfo userDimInfo){
        List<FrAreaFieldWidths> list = new LinkedList<FrAreaFieldWidths>();
        FrAreaFieldWidths[] areaWidths = userDimInfo.getAreaWidths();
        FrAreaFieldWidths areaFieldWidth = userDimInfo.getFieldWidth();
        if(null != areaWidths && areaWidths.length != 0){
            list = Arrays.asList(areaWidths);
            if(null != areaFieldWidth){
                list.add(0, areaFieldWidth);
            }
        }else{
            if(null != areaFieldWidth){
                list.add(areaFieldWidth);
            }
        }
        return list ;
    }
    
    /**
     * 加载打印设置信息
     * @param userDimInfo 个性化信息
     * @param cellsModel  表格模型
     * @since ncv63
     */
    private void loadPrintSet(FrReportUserDimInfo userDimInfo){
        PrintSet printSet = userDimInfo.getPrintSet();
        if(null == printSet){
            return ;
        }
        //获得打印设置模型
        PrintSetModel printSetModel = PrintSetModel.getInstance(anaReportModel.getCellsModel());
        if(null == printSetModel){
            return ;
        }
        //设置打印信息
        printSetModel.setPrintSet(printSet);
    }
    
    /**
     * 得到用户个性化设置信息的Service
     * 
     * @return IFreeRepUserIndivService
     */
//    private IFreeRepUserIndivQryService getUserIndivQryService() {
//        return NCLocator.getInstance().lookup(IFreeRepUserIndivQryService.class);
//    }
}
