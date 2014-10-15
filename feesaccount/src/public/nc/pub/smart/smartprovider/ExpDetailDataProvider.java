package nc.pub.smart.smartprovider;

import java.sql.Types;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.pub.IExpDetailBO;
import nc.itf.fipub.report.IPubReportConstants;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.metadata.Field;
import nc.pub.smart.metadata.MetaData;
import nc.smart.fipub.AbsReportDataProvider;
import nc.utils.fipub.SmartProcessor;
import nc.vo.fipub.report.ReportQueryCondVO;

/**
 * <p>
 *  报销管理费用明细账语义数据提供者。
 * </p>
 * 
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li> <br>
 * <br>
 * 
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-11-22 上午09:42:13
 */
public class ExpDetailDataProvider extends AbsReportDataProvider {

	private static final long serialVersionUID = 1L;
	public static final String PREFIX_GR = "gr_";//集团
	public static final String PREFIX_GL = "gl_";//全局
	public static final String SUFFIX_ORI = "_ori";//原币
	public static final String SUFFIX_LOC = "_loc";//本币

	/** 字段名称 */
	private static final String[] CAPTIONS = {

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0070")/* @res "集团PK" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0071")/* @res "业务单元PK" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0072")/* @res "业务单元CODE" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UCMD1-000396")/* @res "业务单元" */,

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC000-0002313")/* @res "日期" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0078")/* @res "单据类型PK" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC000-0000807")/* @res "单据类型" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0073")/* @res "币种PK" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC000-0001755")/* @res "币种" */,

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC000-0002185")/* @res "摘要" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0079")/* @res "单据PK" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UCMD1-000144")/* @res "单据编号" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
					"UC000-0000240")/* @res "会计期间" */,

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0074")/* @res "报销金额原币" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0075")/* @res "报销金额本币" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0076")/* @res "报销金额集团本币" */,
			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0077")/* @res "报销金额全局本币" */,

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"feesaccount_0", "02011001-0112") /* @res "序号" */

	};

	/** 字段编码 */
	private static final String[] FLDNAMES = {

	"pk_group", // 集团主键
			"pk_org", // 业务单元主键
			"code_org", // 业务单元编码
			"org", // 业务单元

			"billdate", // 日期
			"pk_billtype", // 单据类型主键
			"billtype", // 单据类型
			"pk_currtype", // 币种主键
			"currtype", // 币种

			"reason", // 摘要
			"src_id", // 单据主键
			"src_billno", // 单据编号
			"accperiod", // 会计期间

			"assume_amount"+SUFFIX_ORI, // 报销金额原币
			"org_amount"+SUFFIX_LOC, // 报销金额本币
			PREFIX_GR+"group_amount"+SUFFIX_LOC, // 报销金额集团本币
			PREFIX_GL+"global_amount"+SUFFIX_LOC, // 报销金额全局本币,
			IPubReportConstants.ORDER_MANAGE_VSEQ// 序号
	};

	/** 字段类型 */
	private static final int[] DBCOLUMNTYPES = { Types.VARCHAR, Types.VARCHAR,
			Types.VARCHAR, Types.VARCHAR,

			Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
			Types.VARCHAR,

			Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,

			Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL,

			Types.INTEGER };

	/** 字段长度 */
	private static final int[] PRECISIONS = { 20, 20, 50, 50, 19, 20, 120, 20,
			50, 200, 20, 50, 50, 28, 28, 28, 28, 20 };

	/** 字段精度 */
	private static final int[] SCALES = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 8, 8, 8, 8, 0 };

	protected DataSet execute(Object obj, SmartContext context)
			throws SmartException {
		ReportQueryCondVO queryVO = obj == null ? null
				: (ReportQueryCondVO) obj;
		return NCLocator.getInstance().lookup(IExpDetailBO.class)
				.queryExpenseDetail(queryVO, context);
	}

	public MetaData provideMetaData(SmartContext context) throws SmartException {
		MetaData metaData = new MetaData();
		// 设置动态查询对象
		metaData.addField(SmartProcessor.getSmartQueryObj().toArray(
				new Field[0]));

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