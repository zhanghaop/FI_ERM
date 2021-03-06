package nc.impl.erm.report;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.pub.ErmReportUtil;
import nc.bs.erm.sql.ErmBaseSqlCreator;
import nc.bs.erm.sql.LoanDetailSQLCreator;
import nc.bs.erm.util.ErUtil;
import nc.bs.logging.Logger;
import nc.itf.erm.pub.ILoanDetailBO;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.script.statement.select.PlainSelect;
import nc.pub.smart.smartprovider.LoanDetailDataProvider;
import nc.utils.fipub.FipubSqlExecuter;
import nc.utils.fipub.ReportMultiVersionSetter;
import nc.utils.fipub.SmartProcessor;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.rs.MemoryResultSet;
import nc.vo.pubapp.pattern.pub.MapList;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * 报销管理帐表查询，借款明细账查询接口实现类。
 * </p>
 * 
 * @version V6.0
 * @since V6.0 创建时间：2010-12-7 下午02:34:59
 */
public class LoanDetailBOImpl extends FipubSqlExecuter implements ILoanDetailBO {

	@Override
    public DataSet queryLoanDetail(ReportQueryCondVO queryVO, SmartContext context) throws SmartException {
		setContext(context);
		DataSet resultDataSet = new DataSet();

		/***************************************************************/
		/********************** 屏蔽语义模型定义时的校验 ********************/

		if (queryVO == null) {
			// 定义语义模型时，会执行到这里进行校验，且queryVO为空
			try {
				resultDataSet.setMetaData(new LoanDetailDataProvider()
						.provideMetaData(null));
			} catch (SmartException e) {
				// 这里永远不会抛出异常，故吃掉异常也不会造成影响
			}
			return resultDataSet;
		}

		/****************************************************************/

		try {
	        PlainSelect select = (PlainSelect)context.getAttribute("key_current_plain_select");
	        select.setWhere(null);
			ErmBaseSqlCreator sqlCreator = new LoanDetailSQLCreator();

			// 设置查询对象VO的内容
			sqlCreator.setParams(queryVO);

			String[] arrangeSqls = sqlCreator.getArrangeSqls();
			String resultSql = sqlCreator.getResultSql();
			String[] dropTableSqls = sqlCreator.getDropTableSqls();

			// 构建临时表数据
			for (String sql : arrangeSqls) {
				executeUpdate(sql);
			}

			// 从临时表取得结果
			MemoryResultSet resultSet = (MemoryResultSet) executeQuery(
					resultSql, getResultProcessor());

			// 删除临时表
			for (String sql : dropTableSqls) {
				executeUpdate(sql);
			}

            //转换币种
			ErUtil.convertCurrtype(resultSet, queryVO);
			
			// 插入【币种】名称
			PubCommonReportMethod.insertNameColumn(resultSet, IPubReportConstants.CURRTYPE, "pk_currtype", "currtype");
			// 插入【交易类型】名称
            PubCommonReportMethod.insertNameColumn(resultSet,
                    IPubReportConstants.BILLTYPE, "pk_billtype", "billtype",
                    200);

			// 设置返回结果元数据
			resultDataSet.setMetaData(SmartProcessor.getMetaData(resultSet));

			Object[][] datas = getDatas(resultSet, queryVO);
			queryVO.setQueryDetail(true);
			datas = new ReportMultiVersionSetter(resultSet.getMetaData0(), queryVO).setOrg(datas, "djrq");
			int dateIndex = resultSet.getMetaData0().getNameIndex("djrq");
			PubCommonReportMethod.convert2ClientTime(datas, context, dateIndex);

			PubCommonReportMethod.setVSeq(datas, resultDataSet.getMetaData().getIndex(IPubReportConstants.ORDER_MANAGE_VSEQ));

			// 设置返回结果数据集
			resultDataSet.setDatas(datas);
			ErmReportUtil.processDataSet(context, resultDataSet);
		} catch (Exception e) {
			String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0053")/*@res "明细账查询出错！"*/;
			Logger.error(errMsg, e);
			throw new SmartException(errMsg, e);
		}

		return resultDataSet;
	}

	/**
	 * 功能：将内存结果集转化为二维表<br>
	 *
	 * @param mrs 内存结果集<br>
	 * @param queryVO 查询条件VO<br>
	 * @return 内存结果集对应的二维表<br>
	 * @throws SQLException<br>
	 */
	@SuppressWarnings("unchecked")
	private Object[][] getDatas(MemoryResultSet mrs, ReportQueryCondVO queryVO) throws SQLException {
		ArrayList<List<Object>> dataRowList = mrs.getResultArrayList();
		int rnIndex = mrs.getColumnIndex("rn");
        int zyColIndex = mrs.getColumnIndex("zy") - 1;
        String initZy = IErmReportConstants.getConst_Brief();

		if (dataRowList == null || dataRowList.size() == 0) {
			return new Object[0][0];
		} else {
			int pk_bill = mrs.getColumnIndex("pk_jkbx") - 1;
			int pk_jkd = mrs.getColumnIndex("pk_contrastjk") - 1;
			int pk_qry0 = mrs.getColumnIndex("qryobj0pk") - 1;

			MapList<String, List<Object>> bxdMap = new MapList<String, List<Object>>();
			for (List<Object> rowData : dataRowList) {
				if (rowData.get(pk_jkd) != null) {
					bxdMap.put(rowData.get(pk_jkd).toString(), rowData);
				}
                if (rowData.get(zyColIndex) != null
                        && rowData.get(zyColIndex).equals("init__period")) {
                    rowData.set(zyColIndex, initZy);
                }
			}

			ArrayList<List<Object>> newdataRowList = new ArrayList<List<Object>>();
			for (List<Object> rowData : dataRowList) {
				if (rowData.get(pk_jkd) == null) {
					newdataRowList.add(rowData);
					if (rowData.get(pk_bill) != null) {// 合计,期初数据主键字段为空
						List<List<Object>> bxds = bxdMap.remove(rowData.get(pk_bill).toString());
						if (bxds != null) {
							newdataRowList.addAll(bxds);
						}
					}
				}
			}

			if (bxdMap.keySet().size() > 0) {
				// 如果报销单(还款单)冲过期初借款，会走这里
				MapList<String, List<Object>> bxdMapList = new MapList<String, List<Object>>();
				for (String key : bxdMap.keySet()) {
					// 对冲期初借款的报销单重组
					List<List<Object>> list = bxdMap.get(key);
					for (List<Object> row : list) {
						bxdMapList.put(row.get(pk_qry0).toString(), row);
					}
				}

//				dataRowList = new ArrayList<List<Object>>();
//				for (List<Object> row : newdataRowList) {
//					dataRowList.add(row);
//					if (Integer.parseInt(row.get(rnIndex).toString()) == 0) {
//						List<List<Object>> rest = bxdMapList.get(row.get(pk_qry0).toString());
//						if (rest != null && rest.size() > 0) {
//							dataRowList.addAll(rest);
//						}
//					}
//				}
			} else {
//				dataRowList = newdataRowList;
			}
		}

		if (!IPubReportConstants.ORG_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
			String[] targetFields = new String[] { "jk_loc", "hk_loc", "bal_loc" };
			String[] formulas = null;
			if (IPubReportConstants.GROUP_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// 集团本币
				formulas = new String[] { "jk_loc->gr_jk_loc", "hk_loc->gr_hk_loc", "bal_loc->gr_bal_loc" };
			} else if (IPubReportConstants.GLOBLE_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// 全局本币
				formulas = new String[] { "jk_loc->gl_jk_loc", "hk_loc->gl_hk_loc", "bal_loc->gl_bal_loc" };
			}
			mrs.setColumnByFormulate_type(targetFields, formulas);
		}
		Object datas[][] = new Object[dataRowList.size()][mrs.getMetaData().getColumnCount()];

		List<Integer> qryObjIndex = new ArrayList<Integer>();
		List<Integer> qryObjNameIndex = new ArrayList<Integer>();
		List<Integer> totalCol = new ArrayList<Integer>(); // 小计列
        List<Integer> totalCol2 = new ArrayList<Integer>(); // 小计列
		int orgIndex = mrs.getColumnIndex("org");
		int currtypeIndex = mrs.getColumnIndex("currtype");
		int tallydateIndex = mrs.getColumnIndex("djrq");
		int oriIndex = mrs.getColumnIndex("bal_ori");
		int locIndex = mrs.getColumnIndex("bal_loc");
		int grlocIndex = mrs.getColumnIndex("gr_bal_loc");
		int gllocIndex = mrs.getColumnIndex("gl_bal_loc");
//		int briefIndex = mrs.getColumnIndex("zy");
		for (int i = 0; i < queryVO.getQryObjs().size(); i++) {
			qryObjIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i));
			qryObjNameIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i + "code"));
			totalCol.add(qryObjIndex.get(i));
            totalCol2.add(qryObjNameIndex.get(i));
		}
		totalCol.add(currtypeIndex);
        totalCol2.add(orgIndex);

        boolean isCurrtype = false;
        String[] amount_ori = new String[] {"jk_ori", "hk_ori", "bal_ori"};
        int[] currtypeCount = new int[amount_ori.length];
        int[] orgCurrtypeCount = new int[amount_ori.length];
        int[] amount_ori_pos = new int[amount_ori.length];
        for (int nPos = 0; nPos < amount_ori.length; nPos++) {
            amount_ori_pos[nPos] = mrs.getColumnIndex(amount_ori[nPos]);
        }
		
        Map<String, Map<String, String>> countMap = new HashMap<String, Map<String, String>>();
        Map<String, String> totalCurrtype = new HashMap<String, String>();
        
		Object[] dataRow = new Object[0];
		int rn = -1;
		boolean isObj = false;
        boolean isMultiOrg = queryVO.getPk_orgs().length > 1;
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

            //记录币种个数
            if (dataRow[currtypeIndex - 1] != null) {
                totalCurrtype.put((String)dataRow[currtypeIndex - 1], null);
            }
			if (rn >= SmartProcessor.MAX_ROW) {
				// 处理合计行
				if (rn == SmartProcessor.MAX_ROW) {
					// rn == SmartProcessor.MAX_ROW：本日小计
					dataRow[tallydateIndex - 1] = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0055")/*@res "本日小计"*/;
					// 日小计期末余额=上一行期末余额
					dataRow[oriIndex - 1] = datas[i - 1][oriIndex - 1];
					dataRow[locIndex - 1] = datas[i - 1][locIndex - 1];
					dataRow[grlocIndex - 1] = datas[i - 1][grlocIndex - 1];
					dataRow[gllocIndex - 1] = datas[i - 1][gllocIndex - 1];
					int j = qryObjIndex.size() - 1;
					for (; j >= 0; j--) {
						dataRow[qryObjIndex.get(j) - 1] = "";
						dataRow[qryObjNameIndex.get(j) - 1] = "";
					}
					dataRow[currtypeIndex - 1] = "";
					dataRow[orgIndex - 1] = "";
				} else {
					int k = totalCol.size() - 1;
					int curRowQryObjIndex = 0;
					for (; k >= 0; k--) {
						if (dataRow[totalCol.get(k) - 1] != null && !"".equals(dataRow[totalCol.get(k) - 1])) {
							if (k == totalCol.size() - 1) {
								// 币种小计
								dataRow[currtypeIndex - 1] = dataRow[currtypeIndex - 1] + IErmReportConstants.getConst_Sub_Total(); // 小计
								
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
								// 查询对象小计
								dataRow[qryObjIndex.get(k) - 1] = dataRow[qryObjIndex.get(k) - 1] + IErmReportConstants.getConst_Sub_Total(); // 小计
//								dataRow[qryObjNameIndex.get(k) - 1] = dataRow[qryObjNameIndex.get(k) - 1] + IErmReportConstants.getConst_Sub_Total(); // 小计
								curRowQryObjIndex = qryObjNameIndex.get(k);
                                isCurrtype = false;
							}
							isObj = true;
							break;
						}
					}

					for (k--; k >= 0; k--) {
						dataRow[qryObjIndex.get(k) - 1] = "";
						dataRow[qryObjNameIndex.get(k) - 1] = "";
					}

					if (!isObj&& isMultiOrg && (dataRow[orgIndex - 1] == null || dataRow[orgIndex - 1].toString().length() == 0)) {
//						dataRow[briefIndex - 1] = IErmReportConstants.getCONST_ALL_TOTAL(); // 总计
						
						if (!isCurrtype) {
                            // 多币种清空金额字段信息
                            for (int nPos = 0; nPos < amount_ori.length; nPos++) {
                                if (totalCurrtype != null && totalCurrtype.keySet().size() > 1) {
                                    dataRow[amount_ori_pos[nPos] - 1] = BigDecimal.ZERO;
                                }
                            }
                        }
						
						dataRow[orgIndex - 1] = IErmReportConstants.getCONST_ALL_TOTAL(); // 总计
						// 多组织、多币种清空金额字段信息
//						if (!isCurrtype) {
//	                        // 多币种清空金额字段信息
//	                        for (int nPos = 0; nPos < amount_ori.length; nPos++) {
//	                            if (currtypeCount[nPos] > 1) {
//	                                dataRow[amount_ori_pos[nPos] - 1] = BigDecimal.ZERO;
//	                            }
//	                        }
//	                    }
					} else if (!isObj) {
					    String org = (String)dataRow[orgIndex - 1];
						dataRow[orgIndex - 1] = dataRow[orgIndex - 1] + IErmReportConstants.getCONST_AGG_TOTAL(); // 合计
						dataRow[tallydateIndex - 1] = "";
						if (!isCurrtype) {
                            // 多币种清空金额字段信息
			                Map<String, String> currtypeMap = countMap.get(org); 
						    for (int nPos = 0; nPos < amount_ori.length; nPos++) {
                                if (orgCurrtypeCount[nPos] > 1 && currtypeMap.keySet().size() > 1) {
                                    dataRow[amount_ori_pos[nPos] - 1] = BigDecimal.ZERO;
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
                        // 多币种清空金额字段信息
                        for (int nPos = 0; nPos < amount_ori.length; nPos++) {
                            if (currtypeCount[nPos] > 1 && currtypeMap != null && currtypeMap.keySet().size() > 1) {
                                dataRow[amount_ori_pos[nPos] - 1] = BigDecimal.ZERO;
                            }
                        }
                    }
				}
			} else if (rn >= 1 && i > 0) {
				// 处理非合计行(不包含期初)
				if(datas[i - 1][oriIndex - 1]==null){
					datas[i - 1][oriIndex - 1]=0;
				}
				if(datas[i - 1][locIndex - 1]==null){
					datas[i - 1][locIndex - 1]=0;
				}
				if(datas[i - 1][grlocIndex - 1]==null){
					datas[i - 1][grlocIndex - 1]=0;
				}
				if(datas[i - 1][gllocIndex - 1]==null){
					datas[i - 1][gllocIndex - 1]=0;
				}
				dataRow[oriIndex - 1] = ((BigDecimal) datas[i - 1][oriIndex - 1]).add((BigDecimal) dataRow[oriIndex - 1]);
				dataRow[locIndex - 1] = ((BigDecimal) datas[i - 1][locIndex - 1]).add((BigDecimal) dataRow[locIndex - 1]);
				dataRow[grlocIndex - 1] = ((BigDecimal) datas[i - 1][grlocIndex - 1]).add((BigDecimal) dataRow[grlocIndex - 1]);
				dataRow[gllocIndex - 1] = ((BigDecimal) datas[i - 1][gllocIndex - 1]).add((BigDecimal) dataRow[gllocIndex - 1]);

			}
			if (dataRow[oriIndex - 1] == null || BigDecimal.ZERO.equals(dataRow[oriIndex - 1])) {
			    dataRow[locIndex - 1] = BigDecimal.ZERO;
			}
			datas[i] = dataRow;
		}

		return datas;
	}

	@Override
	public Object queryLoanDetailResultSet(ReportQueryCondVO queryVO,
			SmartContext context, ResultSetProcessor processor)
			throws BusinessException {
		try {
			setContext(context);
			ErmBaseSqlCreator sqlCreator = new LoanDetailSQLCreator();
			// 设置查询对象VO的内容
			sqlCreator.setParams(queryVO);

			String[] arrangeSqls = sqlCreator.getArrangeSqls();
			String resultSql = sqlCreator.getResultSql();
			String[] dropTableSqls = sqlCreator.getDropTableSqls();

			// 构建临时表数据
			for (String sql : arrangeSqls) {
				executeUpdate(sql);
			}

			// 从临时表取得结果
			Object result = executeQuery(resultSql, processor);

			// 删除临时表
			for (String sql : dropTableSqls) {
				executeUpdate(sql);
			}

			return result;
		} catch (SQLException e) {
			throw ExceptionHandler.handleException(e);
		}
	}

}
