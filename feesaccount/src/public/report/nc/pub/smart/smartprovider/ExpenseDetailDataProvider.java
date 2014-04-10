package nc.pub.smart.smartprovider;

import java.sql.Types;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.pub.IExpenseDetailBO;
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
 * TODO �������������ϸ�����������ṩ�ߡ�
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
public class ExpenseDetailDataProvider extends AbsReportDataProvider{

	private static final long serialVersionUID = 1L;

	/** �ֶ����� */
	private static final String[] CAPTIONS = {

		    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0070")/*@res "����PK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0071")/*@res "ҵ��ԪPK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0072")/*@res "ҵ��ԪCODE"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000396")/*@res "ҵ��Ԫ"*/,

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0002313")/*@res "����"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0078")/*@res "��������PK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0000807")/*@res "��������"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0073")/*@res "����PK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001755")/*@res "����"*/,

			 nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0002185")/*@res "ժҪ"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0079")/*@res "����PK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000144")/*@res "���ݱ��"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0000240")/*@res "����ڼ�"*/,

		     nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0074")/*@res "�������ԭ��"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0075")/*@res "��������"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0076")/*@res "�������ű���"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0077")/*@res "�������ȫ�ֱ���"*/,
		     
		     nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0112")/*@res "���"*/

	};

	/** �ֶα��� */
	private static final String[] FLDNAMES = {

	    "pk_group",      //��������
	    "pk_org",        //ҵ��Ԫ����
	    "code_org",      //ҵ��Ԫ����
	    "org",           //ҵ��Ԫ

	    "djrq",          //����
	    "pk_billtype",   //������������
	    "billtype",	     //��������
	    "pk_currtype",   //��������
	    "currtype",      //����

	    "zy",			 //ժҪ
	    "pk_jkbx",       //��������
	    "djbh",          //���ݱ��
	    "kjqj",          //����ڼ�

		"exp_ori",        //�������ԭ��
		"exp_loc",        //��������
		"gr_exp_loc",     //�������ű���
		"gl_exp_loc",     //�������ȫ�ֱ���,
		IPubReportConstants.ORDER_MANAGE_VSEQ // ���
	};

	/** �ֶ����� */
	private static final int[] DBCOLUMNTYPES = {
		Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,

		Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,Types.VARCHAR,

		Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,

		Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL,
		
		Types.INTEGER };

	/** �ֶγ��� */
	private static final int[] PRECISIONS = {
			20, 20, 50, 50,
			19, 20, 50, 20, 50,
			200,20, 50, 50,
			28, 28, 28, 28,
			20
			};

	/** �ֶξ��� */
	private static final int[] SCALES = {
		0, 0, 0, 0,
		0, 0, 0, 0, 0,
		0, 0, 0, 0,
		8, 8, 8, 8,
		0
		};


	protected DataSet execute(Object obj, SmartContext context) throws SmartException {
		ReportQueryCondVO queryVO = obj == null ? null : (ReportQueryCondVO) obj;
		return NCLocator.getInstance().lookup(IExpenseDetailBO.class).queryExpenseDetail(queryVO, context);
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






//	public MetaData provideMetaData(SmartContext context) throws SmartException {
//		MetaData metaData = new MetaData();
//		// ���ö�̬��ѯ����
//		metaData.addField(SmartProcessor.getSmartQueryObj().toArray(new Field[0]));
//
//		Field fld = null;
//		// ����
//		fld = new Field();
//		fld.setCaption("����");
//		fld.setDbColumnType(Types.VARCHAR);
//		fld.setFldname("djrq"); // ����
//		fld.setPrecision(50);
//		fld.setScale(2);
//		metaData.addField(fld);
//
//		// ��������PK
//		fld = new Field();
//		fld.setCaption("��������PK");
//		fld.setDbColumnType(Types.VARCHAR);
//		fld.setFldname("pk_billtype"); // ��������PK
//		fld.setPrecision(50);
//		fld.setScale(2);
//		metaData.addField(fld);
//
//		// ��������
//		fld = new Field();
//		fld.setCaption("��������");
//		fld.setDbColumnType(Types.VARCHAR);
//		fld.setFldname("billtype"); // ��������
//		fld.setPrecision(50);
//		fld.setScale(2);
//		metaData.addField(fld);
//
//		// ����PK
//		fld = new Field();
//		fld.setCaption("����PK");
//		fld.setDbColumnType(Types.VARCHAR);
//		fld.setFldname("pk_currtype"); // ����PK
//		fld.setPrecision(20);
//		fld.setScale(0);
//		metaData.addField(fld);
//
//		fld = new Field();
//		fld.setCaption("����");
//		fld.setDbColumnType(Types.VARCHAR);
//		fld.setFldname("currtype"); // ����
//		fld.setPrecision(50);
//		fld.setScale(0);
//		metaData.addField(fld);
//
//		// ������֯
//		fld = new Field();
//		fld.setCaption("������֯PK");
//		fld.setDbColumnType(Types.VARCHAR);
//		fld.setFldname("pk_org"); // ������֯
//		fld.setPrecision(50);
//		fld.setScale(2);
//		metaData.addField(fld);
//		// ������֯
//		fld = new Field();
//		fld.setCaption("������֯");
//		fld.setDbColumnType(Types.VARCHAR);
//		fld.setFldname("org"); // ������֯
//		fld.setPrecision(50);
//		fld.setScale(2);
//		metaData.addField(fld);
//
//		// ժҪ
//		fld = new Field();
//		fld.setCaption("ժҪ");
//		fld.setDbColumnType(Types.VARCHAR);
//		fld.setFldname("zy"); // ժҪ
//		fld.setPrecision(50);
//		fld.setScale(2);
//		metaData.addField(fld);
//
//		// ����PK
//		fld = new Field();
//		fld.setCaption("����PK");
//		fld.setDbColumnType(Types.VARCHAR);
//		fld.setFldname("pk_jkbx"); // ����PK
//		fld.setPrecision(50);
//		fld.setScale(0);
//		metaData.addField(fld);
//
//		// ���ݱ��
//		fld = new Field();
//		fld.setCaption("���ݱ��");
//		fld.setDbColumnType(Types.VARCHAR);
//		fld.setFldname("billno"); // ���ݱ��
//		fld.setPrecision(50);
//		fld.setScale(0);
//		metaData.addField(fld);
//
//		// ����Ϊ��̬����ֶ�
//		fld = new Field();
//		fld.setCaption("�������ҽ��");
//		fld.setDbColumnType(Types.DECIMAL);
//		fld.setFldname("bbje");  //�������ҽ��
//		fld.setPrecision(31);
//		fld.setScale(8);
//		metaData.addField(fld);
//
//		fld = new Field();
//		fld.setCaption("����ԭ�ҽ��");
//		fld.setDbColumnType(Types.DECIMAL);
//		fld.setFldname("ybje"); //����ԭ�ҽ��
//		fld.setPrecision(31);
//		fld.setScale(8);
//		metaData.addField(fld);
//
//		fld = new Field();
//		fld.setCaption("����ڼ�");
//		fld.setDbColumnType(Types.VARCHAR);
//		fld.setFldname("kjqj"); // ����ڼ�
//		fld.setPrecision(10);
//		fld.setScale(0);
//		metaData.addField(fld);
//
//		fld = new Field();
//		fld.setCaption("������");
//		fld.setDbColumnType(Types.VARCHAR);
//		fld.setFldname("kjnd"); // ������
//		fld.setPrecision(10);
//		fld.setScale(0);
//		metaData.addField(fld);
//
//		/*��Ҫ�������ݼ�������**/
//
//		return metaData;
//
//	}
}