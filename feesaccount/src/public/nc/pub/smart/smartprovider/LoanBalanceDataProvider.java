package nc.pub.smart.smartprovider;

import java.sql.Types;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.pub.ILoanBalanceBO;
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
 * TODO 报销管理借款余额表语义数据提供者。
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>
 *
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-11-22 上午09:42:13
 */
public class LoanBalanceDataProvider extends AbsReportDataProvider{

	private static final long serialVersionUID = 1L;

	/** 字段名称 */
	private static final String[] CAPTIONS = {

	 nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0070")/*@res "集团PK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0071")/*@res "业务单元PK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0072")/*@res "业务单元CODE"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000396")/*@res "业务单元"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0073")/*@res "币种PK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001755")/*@res "币种"*/,

	 nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0086")/*@res "期初原币余额"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0087")/*@res "期初本币余额"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0088")/*@res "期初集团本币余额"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0089")/*@res "期初全局本币余额"*/,

	 nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0090")/*@res "本期借款原币"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0091")/*@res "本期借款本币"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0092")/*@res "本期借款集团本币"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0093")/*@res "本期借款全局本币"*/,

	 nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0094")/*@res "本期还款原币"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0095")/*@res "本期还款本币"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0096")/*@res "本期还款集团本币"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0097")/*@res "本期还款全局本币"*/,

	 nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0098")/*@res "借款余额"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0099")/*@res "借款本币余额"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0100")/*@res "借款集团本币余额"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0101")/*@res "借款全局本币余额"*/,
	 
	 nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0112")/*@res "序号"*/

	};

	/** 字段编码 */
	private static final String[] FLDNAMES = {
			"pk_group", // 集团主键
			"pk_org", // 业务单元主键
			"code_org", // 业务单元编码
			"org", // 业务单元
			"pk_currtype", // 币种主键
			"currtype", // 币种

			"init_ori", // 期初原币
			"init_loc", // 期初本币
			"gr_init_loc", // 期初集团本币
			"gl_init_loc", // 期初集团本币

			"jk_ori", // 本期借款原币
			"jk_loc", // 本期借款本币
			"gr_jk_loc", // 本期借款集团本币
			"gl_jk_loc",// 本期借款全局本币

			"hk_ori", // 本期还款原币
			"hk_loc", // 本期还款本币
			"gr_hk_loc", // 本期还款集团本币"
			"gl_hk_loc", // 本期还款全局本币"

			"bal_ori", // 借款余额原币
			"bal_loc", // 借款本币余额
			"gr_bal_loc", // 借款集团本币余额
			"gl_bal_loc", // 借款全局本币余额
			IPubReportConstants.ORDER_MANAGE_VSEQ // 序号

	};

	/** 字段类型 */
	private static final int[] DBCOLUMNTYPES = {
			Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,

			Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL,

			Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL,

			Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL,

			Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL,
			
			Types.INTEGER

	};

	/** 字段长度 */
	private static final int[] PRECISIONS = {

	20, 20, 50, 50, 20, 50,

	30, 30, 30, 30,

	30, 30, 30, 30,

	30, 30, 30, 30,

	30, 30, 30, 30,
	
	20

	};

	/** 字段精度 */
	private static final int[] SCALES = {

	0, 0, 0, 0, 0, 0,

	8, 8, 8, 8,

	8, 8, 8, 8,

	8, 8, 8, 8,

	8, 8, 8, 8,
	
	0

	};



	protected DataSet execute(Object obj, SmartContext context) throws SmartException {
		ReportQueryCondVO queryVO = obj == null ? null : (ReportQueryCondVO) obj;
		return NCLocator.getInstance().lookup(ILoanBalanceBO.class).queryLoanBalance(queryVO, context);
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