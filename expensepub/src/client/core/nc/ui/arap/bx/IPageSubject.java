package nc.ui.arap.bx;

/**
 * ���۲������
 * @author chendya
 */
public interface IPageSubject {

	public  void addObserver(IPageObserver o);
	
	public  void deleteObserver(IPageObserver o); 
	
	public  void notifyObservers(Object object);
}
