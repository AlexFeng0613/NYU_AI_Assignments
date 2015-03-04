import java.util.Comparator;


public class ComparatorByAlphabet implements Comparator<Object> {

	public int compare(Object arg0, Object arg1) {
		int result = 0;
		Cluster c1 = (Cluster)arg0;
		Cluster c2 = (Cluster)arg1;
		if(c1.commonContentiveWordsSet.size()==0 && c2.commonContentiveWordsSet.size()==0)	 return 0;
		else if(c1.commonContentiveWordsSet.size()==0 && c2.commonContentiveWordsSet.size()!=0) return 1;
		else if(c1.commonContentiveWordsSet.size()!=0 && c2.commonContentiveWordsSet.size()==0)	return -1;
		else{
			String s1 = c1.commonContentiveWordsSet.first();
			String s2 = c2.commonContentiveWordsSet.first();
			for(int i=0;i<Math.min(s1.length(), s2.length());i++){

				if(s1.charAt(i)>s2.charAt(i)){
					result = 1;
					break;
				}
				else if(s1.charAt(i)<s2.charAt(i)){
					result = -1;
					break;
				}
				else{};
			}
		}
		
		return result;
	}
}
