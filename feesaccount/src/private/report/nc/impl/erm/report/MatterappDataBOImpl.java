package nc.impl.erm.report;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.sql.MatterappSQLCreator;
import nc.bs.logging.Logger;
import nc.itf.erm.pub.IMatterappDataBO;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.jdbc.framework.util.DBConsts;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.smartprovider.MatterappDataProvider;
import nc.utils.fipub.FipubSqlExecuter;
import nc.utils.fipub.SmartProcessor;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.rs.MemoryResultSet;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * ���������ѯ�����������ѯ�ӿ�ʵ���ࡣ
 * </p>
 *
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-12-7 ����02:34:59
 */
public class MatterappDataBOImpl extends FipubSqlExecuter implements IMatterappDataBO {

	@Override
    public DataSet queryMatterappData(ReportQueryCondVO queryVO,
			SmartContext context) throws SmartException {
		setContext(context);
		DataSet resultDataSet = new DataSet();

		/***************************************************************/
		/********************** ��������ģ�Ͷ���ʱ��У�� ********************/
		if (queryVO == null) {
			// ��������ģ��ʱ����ִ�е��������У�飬��queryVOΪ��
			try {
				resultDataSet.setMetaData(new MatterappDataProvider()
						.provideMetaData(null));
			} catch (SmartException e) {
				// ������Զ�����׳��쳣���ʳԵ��쳣Ҳ�������Ӱ��
			}
			return resultDataSet;
		}

		/****************************************************************/

		try {
			MatterappSQLCreator sqlCreator = new MatterappSQLCreator();
			// ���ò�ѯ����VO������
			sqlCreator.setParams(queryVO);

			String[] arrangeSqls = sqlCreator.getArrangeSqls();
			String resultSql = sqlCreator.getResultSql();

			// ������ʱ������
			for (String sql : arrangeSqls) {
				executeUpdate(sql);
			}

			// ����ʱ��ȡ�ý��
			MemoryResultSet resultSet = (MemoryResultSet) executeQuery(resultSql, getResultProcessor());


			// ���롾���֡�����
			PubCommonReportMethod.insertNameColumn(resultSet, IPubReportConstants.CURRTYPE, "pk_currtype", "currtype");
			// ���롾�������͡�����
            PubCommonReportMethod.insertNameColumn(resultSet,
                    IPubReportConstants.BILLTYPE, "pk_billtype", "billtype",
                    200);

			if(getBaseDAO().getDBType()==DBConsts.DB2){
				// ���÷��ؽ��Ԫ����
				resultDataSet.setMetaData(SmartProcessor.getMetaDataForDB2(resultSet));
			}else {
				resultDataSet.setMetaData(SmartProcessor.getMetaData(resultSet));
			}

			Object[][] datas = getDatas(resultSet, queryVO);
			queryVO.setQueryDetail(true);
//			datas = new ReportMultiVersionSetter(resultSet.getMetaData0(), queryVO).setOrg(datas, MtAppDetailVO.BILLDATE );
			int dateIndex = resultSet.getMetaData0().getNameIndex(MtAppDetailVO.BILLDATE );
			PubCommonReportMethod.convert2ClientTime(datas, context, dateIndex);
            PubCommonReportMethod.setVSeq(datas, resultDataSet.getMetaData().getIndex(IPubReportConstants.ORDER_MANAGE_VSEQ));

			// ���÷��ؽ�����ݼ�
			resultDataSet.setDatas(datas);
		} catch (Exception e) {
			String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0029")/*@res "���������ѯ����"*/;
			Logger.error(errMsg, e);
			throw new SmartException(errMsg, e);
		}

		return resultDataSet;

	}

	@SuppressWarnings("unchecked")
	private Object[][] getDatas(MemoryResultSet mrs, ReportQueryCondVO queryVO) throws SQLException {
		boolean isMultiOrg = queryVO.getPk_orgs().length > 1;

		ArrayList<List<Object>> dataRowList = mrs.getResultArrayList();

		if (dataRowList == null || dataRowList.size() == 0) {
			return new Object[0][0];
		}

		if (!IPubReportConstants.ORG_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
			//���
			String[] targetFields = new String[] { MtAppDetailVO.ORG_AMOUNT+MatterappDataProvider.SUFFIX_LOC };
			//���
			String[] ytargetFields = new String[] { MtAppDetailVO.ORG_REST_AMOUNT+MatterappDataProvider.SUFFIX_LOC };
			//ִ����
            String[] ztargetFields = new String[] { MtAppDetailVO.ORG_EXE_AMOUNT
                    + MatterappDataProvider.SUFFIX_LOC };
			String[] formulas = null;
			String[] yformulas = null;
			String[] zformulas = null;
			if (IPubReportConstants.GROUP_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// ���ű���
				formulas = new String[] { MtAppDetailVO.ORG_AMOUNT+MatterappDataProvider.SUFFIX_LOC+
						"->"+MatterappDataProvider.PREFIX_GR+MtAppDetailVO.GROUP_AMOUNT+MatterappDataProvider.SUFFIX_LOC };
				yformulas = new String[] { MtAppDetailVO.ORG_REST_AMOUNT+MatterappDataProvider.SUFFIX_LOC+
						"->"+MatterappDataProvider.PREFIX_GR+MtAppDetailVO.GROUP_REST_AMOUNT+MatterappDataProvider.SUFFIX_LOC };
				zformulas = new String[] { MtAppDetailVO.ORG_EXE_AMOUNT+MatterappDataProvider.SUFFIX_LOC+
						"->"+MatterappDataProvider.PREFIX_GR+MtAppDetailVO.GROUP_EXE_AMOUNT+MatterappDataProvider.SUFFIX_LOC };
			} else if (IPubReportConstants.GLOBLE_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// ȫ�ֱ���
				formulas = new String[] { MtAppDetailVO.ORG_AMOUNT+MatterappDataProvider.SUFFIX_LOC+
						"->"+MatterappDataProvider.PREFIX_GL+MtAppDetailVO.GLOBAL_AMOUNT
						+MatterappDataProvider.SUFFIX_LOC };
				yformulas = new String[] { MtAppDetailVO.ORG_REST_AMOUNT+MatterappDataProvider.SUFFIX_LOC+
						"->"+MatterappDataProvider.PREFIX_GL+MtAppDetailVO.GLOBAL_REST_AMOUNT
						+MatterappDataProvider.SUFFIX_LOC };
				zformulas = new String[] { MtAppDetailVO.ORG_EXE_AMOUNT+MatterappDataProvider.SUFFIX_LOC+
						"->"+MatterappDataProvider.PREFIX_GL+MtAppDetailVO.GLOBAL_EXE_AMOUNT
						+MatterappDataProvider.SUFFIX_LOC };
			}
			mrs.setColumnByFormulate_type(targetFields, formulas);
			mrs.setColumnByFormulate_type(ytargetFields, yformulas);
			mrs.setColumnByFormulate_type(ztargetFields, zformulas);
		}

		Object datas[][] = new Object[dataRowList.size()][mrs.getMetaData().getColumnCount()];

        int rnIndex = mrs.getColumnIndex("rn");
		int orgIndex = mrs.getColumnIndex("org");
//		int currtypeIndex = mrs.getColumnIndex("pk_currtype");
        int currtypeIndex = mrs.getColumnIndex("currtype");
        
		int closeStatus = mrs.getColumnIndex(MtAppDetailVO.CLOSE_STATUS);
		int tallydateIndex = mrs.getColumnIndex("billdate");
		List<Integer> qryObjIndex = new ArrayList<Integer>();
		List<Integer> qryObjNameIndex = new ArrayList<Integer>();
		List<Integer> totalCol = new ArrayList<Integer>(); // С����
        List<Integer> totalCol2 = new ArrayList<Integer>(); // С����
		for (int i = 0; i < queryVO.getQryObjs().size(); i++) {
			qryObjIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i));
			qryObjNameIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i + "code"));
            totalCol2.add(qryObjNameIndex.get(i));
            totalCol.add(qryObjIndex.get(i));
		}
        totalCol.add(currtypeIndex);
        totalCol2.add(orgIndex);

        Map<String, Map<String, String>> countMap = new HashMap<String, Map<String, String>>();

        Object[] dataRow = null;
        int rn = -1;
        boolean isObj = false;

        boolean isCurrtype = false;
        String[] amount_ori = new String[] {"orig_amount_ori", "exe_amount_ori", "rest_amount_ori"};
        int[] currtypeCount = new int[amount_ori.length];
        int[] orgCurrtypeCount = new int[amount_ori.length];
        int[] amount_ori_pos = new int[amount_ori.length];
        for (int nPos = 0; nPos < amount_ori.length; nPos++) {
            amount_ori_pos[nPos] = mrs.getColumnIndex(amount_ori[nPos]);
        }
        
		for (int i = 0; i < dataRowList.size(); i++) {
			dataRow = dataRowList.get(i).toArray();
			rn = Integer.parseInt(dataRow[rnIndex - 1].toString());
            isObj = false;
            
            for (int nPos = 0; nPos < totalCol2.size(); nPos++) {
                String key = (String)dataRow[totalCol2.get(nPos) - 1];
                String currtype = (String)dataRow[currtypeIndex - 1];
                if (StringUtils.isEmpty(key)) {
                    continue;
                }
                Map<String, String> currtypeMap = countMap.get(key); 
                if (currtypeMap == null) {
                    currtypeMap = new HashMap<String, String>();
                    countMap.put(key, currtypeMap);
                }
                if (StringUtils.isNotEmpty(currtype)) {
                    currtypeMap.put(currtype, null);
                }
            }
            
            if (rn >= SmartProcessor.MAX_ROW) {
                // ����ϼ���
                if (rn == SmartProcessor.MAX_ROW) {
                } else {
                    int k = totalCol.size() - 1;
                    int curRowQryObjIndex = 0;
                    for (; k >= 0; k--) {
                        if (dataRow[totalCol.get(k) - 1] != null && !"".equals(dataRow[totalCol.get(k) - 1])) {
                            if (k == totalCol.size() - 1) {
                                // ����С��
                                dataRow[currtypeIndex - 1] = dataRow[currtypeIndex - 1]
                                        + IErmReportConstants.getConst_Sub_Total(); // С��
                                if (!isCurrtype) {
                                    for (int nPos = 0; nPos < orgCurrtypeCount.length; nPos++) {
                                        currtypeCount[nPos] = 0;
                                    }
                                }
                                for (int nPos = 0; nPos < orgCurrtypeCount.length; nPos++) {
                                    if (dataRow[amount_ori_pos[nPos] - 1] != null && 
                                            BigDecimal.ZERO.compareTo((BigDecimal)dataRow[amount_ori_pos[nPos] - 1]) != 0) {
                                        currtypeCount[nPos]++;
                                        orgCurrtypeCount[nPos]++;
                                    }
                                }
                                isCurrtype = true;
                            } else {
                                // ��ѯ����С��
                                dataRow[qryObjIndex.get(k) - 1] = dataRow[qryObjIndex.get(k) - 1]
                                        + IErmReportConstants.getConst_Sub_Total(); // С��
//                              dataRow[qryObjNameIndex.get(k) - 1] = dataRow[qryObjNameIndex.get(k) - 1]
//                                      + IErmReportConstants.getConst_Sub_Total(); // С��
                                isCurrtype = false;
                                curRowQryObjIndex = qryObjNameIndex.get(k);
                            }
                            isObj = true;
                            break;
                        }
                    }
                    
                    for (k--; k >= 0; k--) {
                        dataRow[qryObjIndex.get(k) - 1] = "";
                        dataRow[qryObjNameIndex.get(k) - 1] = "";
                    }

                    if (!isObj && isMultiOrg && (dataRow[orgIndex - 1] == null
                            || dataRow[orgIndex - 1].toString().length() == 0)) {
//                        dataRow[briefIndex - 1] = IErmReportConstants.CONST_ALL_TOTAL; // �ܼ�
                        dataRow[orgIndex - 1] = "";
                        // ����֯���������ս���ֶ���Ϣ
                        if (!isCurrtype) {
                            // �������ս���ֶ���Ϣ
                            for (int nPos = 0; nPos < amount_ori.length; nPos++) {
                                if (currtypeCount[nPos] > 1) {
                                    dataRow[amount_ori_pos[nPos] - 1] = null;
                                }
                            }
                        }
                    } else if (!isObj) {
                        String org = (String)dataRow[orgIndex - 1];
                        dataRow[orgIndex - 1] = dataRow[orgIndex - 1] + IErmReportConstants.getCONST_AGG_TOTAL(); // �ϼ�
                        dataRow[tallydateIndex - 1] = "";
                        if (!isCurrtype) {
                            // �������ս���ֶ���Ϣ
                            Map<String, String> currtypeMap = countMap.get(org); 
                            for (int nPos = 0; nPos < amount_ori.length; nPos++) {
                                if (orgCurrtypeCount[nPos] > 1 && currtypeMap.keySet().size() > 1) {
                                    dataRow[amount_ori_pos[nPos] - 1] = null;
                                }
                                orgCurrtypeCount[nPos] = 0;
                            }
                        }
                    } else {
                        dataRow[orgIndex - 1] = "";
                    }
                    if (!isCurrtype && curRowQryObjIndex > 0) {
                        String curRowQryObj = (String)dataRow[curRowQryObjIndex - 1];
                        Map<String, String> currtypeMap = countMap.get(curRowQryObj); 
                        // �������ս���ֶ���Ϣ
                        for (int nPos = 0; nPos < amount_ori.length; nPos++) {
                            if (currtypeCount[nPos] > 1 && currtypeMap != null && currtypeMap.keySet().size() > 1) {
                                dataRow[amount_ori_pos[nPos] - 1] = null;
                            }
                        }
                    }
                }
            }
            
			datas[i] = dataRow;
			if((datas[i][closeStatus-1])!=null){
				int closeInt = Integer.parseInt((String)datas[i][closeStatus-1]);
				if(closeInt==2){
					datas[i][closeStatus-1] = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0030")/*@res "δ�ر�"*/;
				}else if(closeInt==1){
					datas[i][closeStatus-1] = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0","0201109-0031")/*@res "�ѹر�"*/;
				}
			}
		}

		return datas;
	}

}