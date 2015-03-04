
import java.util.*;

public class Snippet 
{

	public String sourceSnippet;
	public ArrayList<String> tagRemovedSnippet;
	public ArrayList<String> stemmeredWordsSnippet;
	

	public Snippet()
	{
		sourceSnippet = "";
		tagRemovedSnippet = new ArrayList<String>();
		stemmeredWordsSnippet = new ArrayList<String>();
	}
	
	public Snippet(String s)
	{
		// TODO Auto-generated method stub
		sourceSnippet = s;
		tagRemovedSnippet = new ArrayList<String>();
		stemmeredWordsSnippet = new ArrayList<String>();
	}
	
	public void addStemmedSnippet(String snippetString)
	{
		// TODO Auto-generated method stub
		String snippetWords[] = snippetString.split(" ");
		for(int i = 0 ; i < snippetWords.length ; ++i)
		{
			stemmeredWordsSnippet.add(snippetWords[i]);
		}
	}
	
	public void addRemovedTagSnippet(ArrayList<String> words)
	{	
		// TODO Auto-generated method stub
		for(int i = 0; i < words.size() ; ++i)
		{
			tagRemovedSnippet.add(words.get(i));
		}
		
	}
}
