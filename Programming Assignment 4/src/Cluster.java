import java.util.*;

public class Cluster
{
	TreeSet<String> commonContentiveWordsSet = new TreeSet<String>();
	TreeSet<String> contentiveWordsSet = new TreeSet<String>();
	ArrayList<Snippet> snippetsSet = new ArrayList<Snippet>();
	
	public Cluster()
	{
		
	}
	
	public void addContentiveCommonWord(String word)
	{
		// TODO Auto-generated method stub
		commonContentiveWordsSet.add(word);
	}
	
	public void addContentWord(String word)
	{
		// TODO Auto-generated method stub
		contentiveWordsSet.add(word);
	}
	
	public void addSnippet(Snippet s)
	{
		// TODO Auto-generated method stub
		snippetsSet.add(s);
	}
}
