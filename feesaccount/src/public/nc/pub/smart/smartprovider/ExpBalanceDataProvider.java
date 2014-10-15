package nc.pub.smart.smartprovider;

import java.sql.Types;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.pub.IExpBalanceBO;
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
 * TODO ����������û��ܱ����������ṩ�ߡ�
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * <br><br>
 *
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-11-22 ����09:42:13
 */
public class ExpBalanceDataProvider extends AbsReportDataProvider{

	private static final long serialVersionUID = 1L;
	public static final String PREFIX_GR = "gr_";//����
	public static final String PREFIX_GL = "gl_";//ȫ��
	public static final String SUFFIX_ORI = "_ori";//ԭ��
	public static final String SUFFIX_LOC = "_loc";//����


	private static final String[] CAPTIONS = {

		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0070")/*@res "����PK"*/, 
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0071")/*@res "ҵ��ԪPK"*/, 
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0072")/*@res "ҵ��ԪCODE"*/, 
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000396")/*@res "ҵ��Ԫ"*/, 
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0073")/*@res "����PK"*/, 
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001755")/*@res "����"*/,

		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0074")/*@res "�������ԭ��"*/, 
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0075")/*@res "��������"*/, 
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0076")/*@res "�������ű���"*/, 
		nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0077")/*@res "�������ȫ�ֱ���"*/,

        nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
                "feesaccount_0", "02011001-0112") /* @res "���" */
		};

	/** �ֶα��� */
	private static final String[] FLDNAMES = {

			"pk_group", // ��������
			"pk_org", // ҵ��Ԫ����
			"code_org", // ҵ��Ԫ����
			"org", // ҵ��Ԫ
			"pk_currtype", // ��������
			"currtype", // ����

			"assume_amount"+SUFFIX_ORI,        //�������ԭ��
			"org_amount"+SUFFIX_LOC,        //��������
			PREFIX_GR+"group_amount"+SUFFIX_LOC,     //�������ű���
			PREFIX_GL+"global_amount"+SUFFIX_LOC,     //�������ȫ�ֱ���
            IPubReportConstants.ORDER_MANAGE_VSEQ// ���
	};

	/** �ֶ����� */
	private static final int[] DBCOLUMNTYPES = {
		Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, 
		Types.VARCHAR, Types.VARCHAR,Types.DECIMAL, Types.DECIMAL, 
		Types.DECIMAL, Types.DECIMAL,Types.INTEGER };

	/** �ֶγ��� */
	private static final int[] PRECISIONS = {
			20, 20, 50, 50, 20, 50,
			28, 28, 28, 28, 20 };

	/** �ֶξ��� */
	private static final int[] SCALES = {
		0, 0, 0, 0, 0, 0,
		8, 8, 8, 8, 0 };


	protected DataSet execute(Object obj, SmartContext context) throws SmartException {
		ReportQueryCondVO queryVO = obj == null ? null : (ReportQueryCondVO) obj;
		return NCLocator.getInstance().lookup(IExpBalanceBO.class).queryExpenseBalance(queryVO, context);
	}

	public MetaData provideMetaData(SmartContext context) throws SmartException {
		MetaData metaData = new MetaData();
		// ���ö�̬��ѯ����
		metaData.addField(SmartProcessor.getSmartQueryObj().toArray(new Field[0]));

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