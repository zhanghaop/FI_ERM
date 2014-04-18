package nc.vo.er.pub;
/**
 * filled m_array with BddataVOs
 * if m_fieldAttribute =PK getFieldValue(m_fieldAttribute) ruturn BddataVO.getPK()
 * 
 * */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import nc.vo.bd.accessor.IBDData;
public class QryObjList<E> implements List<E>,Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6550071893342594704L;
	private List<E> m_array = new ArrayList<E>();
    @Override
	public String toString(){
    	
    	StringBuffer str = new StringBuffer();
    	for(int i = 0; i < m_array.size(); i++){
    		Object obj = m_array.get(i);
    		if(obj != null){
    			if(i>0){
    				str.append(",");
    			}
    			str.append(obj.toString());
    		}
    	}
		return str.toString();
    }
    public void setArray(List<E> array){
        m_array = array;
    }
    public List<E> getArray(){
        if(m_array == null){
            m_array = new ArrayList<E>(0);
        }
        return m_array;
    }


    public  String getFieldValue(IBDData bddataVO){
        String value = null;
        int fieldAttribute = getFieldAttribute();
        if(fieldAttribute == QryObjList.PK){
            value = bddataVO.getPk();
        }else if(fieldAttribute == QryObjList.NAME){
            value = bddataVO!=null?bddataVO.getName().toString():null;
        }else if(fieldAttribute == QryObjList.CODE){
            value = bddataVO.getCode();
        }
        return value;
    }
    private int m_fieldAttribute = QryObjList.PK;
    public int getFieldAttribute(){
        return m_fieldAttribute;
    }
    public void setFieldAttribue(int fieldAttribute){    
        m_fieldAttribute = fieldAttribute;
    }
    public int getFieldAttribue(){    
        return m_fieldAttribute;
    }
    public  static final int PK = 0 ;
    public  static final int NAME = 1;
    public static final int CODE = 2;

    /* ���� Javadoc��
     * @see java.util.List#size()
     */
    public int size() {
        // TODO �Զ����ɷ������
        return getArray().size();
    }

    /* ���� Javadoc��
     * @see java.util.List#clear()
     */
    public void clear() {
        // TODO �Զ����ɷ������
        getArray().clear();
        
    }

    /* ���� Javadoc��
     * @see java.util.List#isEmpty()
     */
    public boolean isEmpty() {
        // TODO �Զ����ɷ������
        return getArray().isEmpty();
    }

    /* ���� Javadoc��
     * @see java.util.List#toArray()
     */
    public Object[] toArray() {
        // TODO �Զ����ɷ������
        return getArray().toArray();
    }

    /* ���� Javadoc��
     * @see java.util.List#get(int)
     */
    public E get(int arg0) {
        // TODO �Զ����ɷ������
        return getArray().get(arg0);
    }

    /* ���� Javadoc��
     * @see java.util.List#remove(int)
     */
    public E remove(int arg0) {
        // TODO �Զ����ɷ������
        return getArray().remove(arg0);
    }

    /* ���� Javadoc��
     * @see java.util.List#add(int, java.lang.Object)
     */
    public void add(int arg0, E arg1) {
        // TODO �Զ����ɷ������
        getArray().add(arg0, arg1);
    }

    /* ���� Javadoc��
     * @see java.util.List#indexOf(java.lang.Object)
     */
    public int indexOf(Object arg0) {
        // TODO �Զ����ɷ������
        return getArray().indexOf(arg0);
    }

    /* ���� Javadoc��
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    public int lastIndexOf(Object arg0) {
        // TODO �Զ����ɷ������
        return getArray().lastIndexOf(arg0);
    }

    /* ���� Javadoc��
     * @see java.util.List#add(java.lang.Object)
     */
    public boolean add(E arg0) {
        // TODO �Զ����ɷ������
        return getArray().add(arg0);
    }

    /* ���� Javadoc��
     * @see java.util.List#contains(java.lang.Object)
     */
    public boolean contains(Object arg0) {
        // TODO �Զ����ɷ������
        return getArray().contains(arg0);
    }

    /* ���� Javadoc��
     * @see java.util.List#remove(java.lang.Object)
     */
    public boolean remove(Object arg0) {
        // TODO �Զ����ɷ������
        return getArray().remove(arg0);
    }

    /* ���� Javadoc��
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    @SuppressWarnings("unchecked")
	public boolean addAll(int arg0, Collection arg1) {
        // TODO �Զ����ɷ������
        return getArray().addAll(arg0, arg1);
    }

    /* ���� Javadoc��
     * @see java.util.List#addAll(java.util.Collection)
     */
    @SuppressWarnings("unchecked")
	public boolean addAll(Collection arg0) {
        // TODO �Զ����ɷ������
        return getArray().addAll(arg0);
    }

    /* ���� Javadoc��
     * @see java.util.List#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection arg0) {
        // TODO �Զ����ɷ������
        return getArray().containsAll(arg0);
    }

    /* ���� Javadoc��
     * @see java.util.List#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection arg0) {
        // TODO �Զ����ɷ������
        return getArray().removeAll(arg0);
    }

    /* ���� Javadoc��
     * @see java.util.List#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection arg0) {
        // TODO �Զ����ɷ������
        return getArray().retainAll(arg0);
    }

    /* ���� Javadoc��
     * @see java.util.List#iterator()
     */
    public Iterator<E> iterator() {
        // TODO �Զ����ɷ������
        return getArray().iterator();
    }

    /* ���� Javadoc��
     * @see java.util.List#subList(int, int)
     */
    public List<E> subList(int arg0, int arg1) {
        // TODO �Զ����ɷ������
        return getArray().subList(arg0, arg1);
    }

    /* ���� Javadoc��
     * @see java.util.List#listIterator()
     */
    public ListIterator<E> listIterator() {
        // TODO �Զ����ɷ������
        return getArray().listIterator();
    }

    /* ���� Javadoc��
     * @see java.util.List#listIterator(int)
     */
    public ListIterator<E> listIterator(int arg0) {
        // TODO �Զ����ɷ������
        return getArray().listIterator(arg0);
    }

    /* ���� Javadoc��
     * @see java.util.List#set(int, java.lang.Object)
     */
    public E set(int arg0, E arg1) {
        // TODO �Զ����ɷ������
        return getArray().set(arg0, arg1);
    }

    /* ���� Javadoc��
     * @see java.util.List#toArray(java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
	public Object[] toArray(Object[] arg0) {
        // TODO �Զ����ɷ������
        return getArray().toArray(arg0);
    }

}