package nc.pub.smart.smartprovider;

import java.sql.Types;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.accountageana.ILoanAccountAgeAnalyzeBO;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.metadata.Field;
import nc.pub.smart.metadata.MetaData;
import nc.smart.fipub.AbsReportDataProvider;
import nc.utils.fipub.SmartProcessor;
import nc.vo.fipub.report.ReportQueryCondVO;

/**
 * 借款账龄分析语义提供者<br>
 *
 * @author liansg<br>
 * @since V60 2010-12-14<br>
 */
public class LoanAgeAnalyzeDataProvider extends AbsReportDataProvider {
	private static final long serialVersionUID = 1L;

	/** 字段名称 */
	private static final String[] CAPTIONS = {

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0070")/*@res "集团PK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0071")/*@res "业务单元PK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0072")/*@res "业务单元CODE"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000396")/*@res "业务单元"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0073")/*@res "币种PK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001755")/*@res "币种"*/,

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001819")/*@res "序列号"*/,

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0080")/*@res "借款账龄原币"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0081")/*@res "借款账龄本币"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0082")/*@res "借款账龄集团本币"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0083")/*@res "借款账龄全局本币"*/, // 借款账龄

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0084")/*@res "借款账龄ID"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0085")/*@res "借款账龄"*/ // 借款账龄描述
	};

	/** 字段编码 */
	private static final String[] FLDNAMES = {
			"pk_group", // 集团主键
			"pk_org", // 业务单元主键
			"code_org", // 业务单元编码
			"org", // 业务单元
			"pk_currtype", // 币种主键
			"currtype", // 币种

			"vseq", // 序列号

			"accage_ori", // 借款账龄原币
			"accage_loc", // 借款账龄本币
			"gr_accage_loc", // 借款账龄集团本币
			"gl_accage_loc", // 借款账龄全局本币

			"accageid", // 借款账龄ID
			"accage" // 借款账龄
	};

	/** 字段类型 */
	private static final int[] DBCOLUMNTYPES = {

			Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,

			Types.INTEGER,

			Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL,

			Types.INTEGER, Types.VARCHAR
	};

	/** 字段长度 */
	private static final int[] PRECISIONS = {
		20, 20, 50, 50, 20, 50,

		10,

		30, 30, 30, 30,

		10, 10 };

	/** 字段精度 */
	private static final int[] SCALES = {
			0, 0, 0, 0, 0, 0,

			0,

			8, 8, 8, 8,

			0, 0
	};

	protected DataSet execute(Object obj, SmartContext context) throws SmartException {
		ReportQueryCondVO queryVO = obj == null ? null : (ReportQueryCondVO) obj;
		if (queryVO != null) {
			queryVO.setQueryDetail(false);
		}
		return NCLocator.getInstance().lookup(ILoanAccountAgeAnalyzeBO.class).accountAgeQuery(queryVO, context);
	}


	public MetaData provideMetaData(SmartContext context) throws SmartException {
		MetaData metaData = new MetaData();

		// 设置动态查询对象
		metaData.addField(SmartProcessor.getSmartQueryObj().toArray(new Field[0]));

		// 设置其他查询字段
		Field fld = null;
		for (int i = 0; i < FLDNAMES.length; i++) {
			fld = new Field();
			fld.setCaption(CAPTIONS[i]);
			fld.setFldname(FLDNAMES[i]);
			fld.setDbColumnType(DBCOLUMNTYPES[i]);
			fld.setPrecision(PRECISIONS[i]);
			fld.setScale(SCALES[i]);
			metaData.addField(fld);
		}

		return metaData;
	}

}

