
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main 
{
	public static void main(String[] args) throws Exception
	{
		BufferedReader reader;		//read file buffer
		BufferedWriter writer;
		String readInFileLine;		//storing read in lines
		String queryLine = "";	//search query
		String stopWordsList = "./src/stopwords.txt";		//stop words file
		
		TreeSet<String> stopWordsSet = new TreeSet<String>();
		TreeSet<String> queryWordsSet = new TreeSet<String>();
		ArrayList<Snippet> snippetList = new ArrayList<Snippet>();
		ArrayList<Cluster> clusterList = new ArrayList<Cluster>();
		
		System.out.println("Program begins!");
		System.out.println("Step1:  The source page information:");
		System.out.println("Please inout the directory and file name:");
		reader = new BufferedReader(new InputStreamReader(System.in));
		String inputPage = reader.readLine();


		System.out.println("Step2: The outpage page information");
		System.out.println("Please inout the directory and file name:");
		reader = new BufferedReader(new InputStreamReader(System.in));
		String clusteredResultPage = reader.readLine();
		
		System.out.println("Step3: Please refresh the folder for the a new html page appears.");
		System.out.println("The name and path of this out put page is: " + clusteredResultPage);
		
		
		/*read stop words from stop word file*/
		reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(stopWordsList), "UTF-8"));
		readInFileLine = "";
		while((readInFileLine = reader.readLine())!=null)
		{
			String fields[] = readInFileLine.split(" ");
			for(int i = 0 ; i < fields.length ; i++)
			{
				stopWordsSet.add(fields[i]);
			}
		}	

		/*read search query*/
		reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(inputPage), "UTF-8"));
		readInFileLine = reader.readLine();		
		
		//search in the file context
		Pattern queryPattern = Pattern.compile("<TITLE> Query: (.+) </TITLE>");
		Matcher queryMatcher = queryPattern.matcher(readInFileLine);		
		queryMatcher.find();	
		String queries[] = queryMatcher.group(1).split(" ");	
		
		for(int i = 0 ; i < queries.length ; i++)
		{
			queryWordsSet.add(queries[i].toLowerCase());
		}	

		/*read each snippet and write it into a snippet*/
		reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(inputPage), "UTF-8"));
		readInFileLine = "";	
		//one snippet is stored in a string
		String snippetString = "";		
		while((readInFileLine = reader.readLine())!=null)
		{
			//ignore title and query lines
			if(!readInFileLine.startsWith("<TITLE>") && !readInFileLine.startsWith("<H2>")){
				/* 
				 * when read in a null line, and the snippet string is not null
				 * we can know this is a sign of snippet break. Thus 
				 * construct a new snippet with the snippet line.
				*/
				if (readInFileLine.trim().equalsIgnoreCase("")
						&& !snippetString.trim().equalsIgnoreCase("")) {
					Snippet completeSnippet = new Snippet(snippetString);
					snippetList.add(completeSnippet);			
					snippetString = "";	
				}
				/*
				 * if current line is not null or encountered a null line in a snippet
				 * then a snippet is not completed reading in. 
				 * Continue reading.
				*/
				else if(!readInFileLine.trim().equalsIgnoreCase("")
						|| !snippetString.trim().equalsIgnoreCase("")) {
					snippetString += readInFileLine + " ";
				}
				else{	}
			}
		}	

		/*
		 * remove html tags from each snippet
		 * */
		for(int i = 0 ; i < snippetList.size() ; i++)
		{
			snippetString = snippetList.get(i).sourceSnippet;
			//remove tags
			String allTagsRemovedSnippet = snippetString.replaceAll("<A href=\"(.+)\">", "")
					.replaceAll("<p>|</A>", "").replaceAll("\\p{Punct}", "");

			String snippetContents[] = allTagsRemovedSnippet.split(" ");
			ArrayList<String> contentWordList =  new ArrayList<String>();
			
			for(int j = 0 ; j < snippetContents.length ; j++ )
			{
				//remove all stop words and null line, add remaining parts to the snippet
				if(!snippetContents[j].trim().equalsIgnoreCase("")){
					if (!stopWordsSet.contains(snippetContents[j].toLowerCase())
							&& snippetContents[j].length() >= 3
							&& !queryWordsSet.contains(snippetContents[j].toLowerCase())){
						contentWordList.add(snippetContents[j].toLowerCase());
					}
				}
			}
			snippetList.get(i).addRemovedTagSnippet(contentWordList);	
		}
		/*
		 * for each snippet, using Porter stemmer algorithm
		 * */
		//temporary file for storing each snippet's input and output
		String cacheInput = "./src/input.txt";
		String cacheOutput = "./src/output.txt";
		for(int i = 0 ; i < snippetList.size() ; i++)
		{
			writer = new BufferedWriter(new FileWriter(cacheInput));
			for(int j = 0 ; j < snippetList.get(i).tagRemovedSnippet.size() ; j++)
			{
				//System.out.println(snippetList.get(i).tagRemovedSnippet.get(j));
				writer.write(snippetList.get(i).tagRemovedSnippet.get(j)+" ");
			}
			writer.close();
			Stemmer stemmer = new Stemmer();
			stemmer.runner(cacheInput, cacheOutput);
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(cacheOutput), "UTF-8"));
			readInFileLine = reader.readLine();
			snippetList.get(i).addStemmedSnippet(readInFileLine);
		}
		//delete cache files
		File input = new File(cacheInput);
		input.delete();
		File output = new File(cacheOutput);	
		output.delete();
		reader.close();
		
		/*
		 * cluster: scan from the first snippet to the last
		 * */
		for(int i = 0 ; i < snippetList.size() ; i++)
		{
			boolean currentCanConnectToCluster = false;
			// check if one of clusters can be connected to current snippets one by one
			for (int j = 0; j < clusterList.size(); j++) {
				//check if one stemmed word is contained in the snippet list
				for (int k = 0; k < snippetList.get(i).stemmeredWordsSnippet.size(); k++) {
					if (clusterList.get(j).contentiveWordsSet.contains(
							snippetList.get(i).stemmeredWordsSnippet.get(k))) {
						currentCanConnectToCluster = true;
						clusterList.get(j).addSnippet(snippetList.get(i));
						break;
					}
				}
				/*
				 * can be connected to a cluster then add this snippet into corresponding 
				 * cluster and modify the contentive words
				 */
				if (currentCanConnectToCluster) {
					// add common contentive words
					for (int l = 0; l < snippetList.get(i).stemmeredWordsSnippet.size(); l++) {
						if (clusterList.get(j).contentiveWordsSet
								.contains(snippetList.get(i).stemmeredWordsSnippet.get(l))) {
							clusterList.get(j).commonContentiveWordsSet
							.add(snippetList.get(i).stemmeredWordsSnippet.get(l));
						}
					}
					// add new appeared stemmered words into current cluster as contentive words
					for (int l = 0; l < snippetList.get(i).stemmeredWordsSnippet.size(); l++) {
						if (!queryWordsSet.contains(snippetList.get(i).
										stemmeredWordsSnippet.get(l))) {
							clusterList.get(j).contentiveWordsSet
							.add(snippetList.get(i).stemmeredWordsSnippet.get(l));
						}
					}
					break;
				}
			}
			/*
			 * having scanned all clusters and found no one 
			 * can be connected then create a new cluster
			 * */
			if (!currentCanConnectToCluster) {
				Cluster tmpCluster = new Cluster();
				tmpCluster.addSnippet(snippetList.get(i));
				for (int j = 0; j < snippetList.get(i).stemmeredWordsSnippet.size(); j++) {
					if (!queryWordsSet.contains(snippetList.get(i).stemmeredWordsSnippet.get(j))) {
						tmpCluster.contentiveWordsSet.add(snippetList.get(i).stemmeredWordsSnippet.get(j));
					}
				}
				clusterList.add(tmpCluster);
			}
		}
		
		/*
		 * double check clustering
		 * */
		//scanning the snippet from the end to the first,then compare this one with all previous snippets
		for(int i = clusterList.size()-1 ; i > 0 ; i--)
		{
			for(int j = i - 1 ; j >= 0 ; j--)
			{
				boolean haveCommonWordsBetweenCluster = false;
				/*
				 * if find a common word then add it to cluster common words list
				 *  */
				for(String contentiveWords : clusterList.get(i).contentiveWordsSet)
					if(clusterList.get(j).contentiveWordsSet.contains(contentiveWords))
					{
						haveCommonWordsBetweenCluster = true;
						clusterList.get(j).commonContentiveWordsSet.add(contentiveWords);
					}	
				/*
				 * if there are two clusters that have common contentive words then merge them,
				 * then delete the added one from the cluster
				 * */
				if(haveCommonWordsBetweenCluster)
				{
					//add this snipppet
					for(int k = 0 ; k < clusterList.get(i).snippetsSet.size() ; k++)
					{
						clusterList.get(j).snippetsSet.add(clusterList.get(i).snippetsSet.get(k));
					}
					//merge the two clusters' common words into one
					for(String commonContentiveWords : clusterList.get(i).commonContentiveWordsSet)
					{
						clusterList.get(j).commonContentiveWordsSet.add(commonContentiveWords);
					}
					//merge two clusters' contentive words
					for(String contentiveWords : clusterList.get(i).contentiveWordsSet)
					{
						clusterList.get(j).contentiveWordsSet.add(contentiveWords);
					}	
					clusterList.remove(i);
					break;
				}
			}
		}
		
		/*
		 * Sort the cluster by alphabeta
		 * */
		//System.out.println(clusterList.size());
		ComparatorByAlphabet comp = new ComparatorByAlphabet();
		Collections.sort(clusterList,comp);
		
		//write the result into a file
		writer = new BufferedWriter(new FileWriter(clusteredResultPage));
		writer.write("<TITLE> Clusters for " + queryLine + " </TITLE>");
		writer.write("<H2> Clusters for " + queryLine + " </H2>");
		
		for(int i = 0 ; i < clusterList.size() ; i++)
		{
			/*
			 * if a cluster has only one snippet and cannot extract contentive words
			 * then make its title as the index name of this cluster. Search the title in
			 * the context then display it in the common content words area.
			 */
			if(clusterList.get(i).commonContentiveWordsSet.size() == 0 )
			{
				snippetString = clusterList.get(i).snippetsSet.get(0).sourceSnippet;
				//System.out.println(snippetString);
				Pattern titlePattern = Pattern.compile("<A(.+)</A>");
				Matcher titleMatcher = titlePattern.matcher(snippetString);			
				titleMatcher.find();
				String title = titleMatcher.group(0).replaceAll("<A href=\"(.+)\">", "").replace("</A>", "");
				
				writer.newLine();
				writer.write("<H3>");
				writer.write(title);
				writer.write("</H3>");
				writer.newLine();
			}
			/*
			 * write the cluster html tags, followed by the common contentive words and snippet with
			 * html tags
			 */
			else
			{
				writer.newLine();
				writer.write("<H3> ");
				/*
				 * If find a common word from the stemmered common words list
				 * then stop loop, retrive this word's original form and write it into the title
				 * */
				for(String commonWord : clusterList.get(i).commonContentiveWordsSet)
				{
					String snippetContent = "";
					boolean stopLoop = false;
					// Search in the snippet, one word by one
					for(Snippet snippet : clusterList.get(i).snippetsSet)
					{
						for(int j = 0 ; j < snippet.stemmeredWordsSnippet.size() ; j++)
						{
							//find the original form of current common word in the snippet, loop stop
							if(snippet.stemmeredWordsSnippet.get(j).equalsIgnoreCase(commonWord))
							{
								snippetContent = snippet.tagRemovedSnippet.get(j);
								stopLoop = true;
								break;
							}
						}					
						if(stopLoop)
							break;
					}
					writer.write(snippetContent + " ");
				}
				writer.write("</H3>");
				writer.newLine();
			}
			/*
			 * Write source snippet content
			 * */
			for(int j = 0 ; j < clusterList.get(i).snippetsSet.size() ; j++)
			{
				writer.write(clusterList.get(i).snippetsSet.get(j).sourceSnippet);
				writer.newLine();
			}
		}		
		writer.close();
	}
}
