import java.util.*;
import java.io.*;

class Preprocessing{
	public static void main(String args[]){

		String FILENAME = System.getProperty("user.dir")+"/aclImdb/imdbEr.txt";
		String feat = System.getProperty("user.dir")+"/aclImdb/train/labeledBow.feat";
		List<Integer> arr = new ArrayList<Integer>();
		List<Integer> arrnew = new ArrayList<Integer>();
		List<String> indexrating = new ArrayList<String>();
		Vector<Vector<String>>  table= new Vector<Vector<String>>();
		int index = 0,pos=0,neg=0;
		String indexratingrow;
		PrintWriter writer = null,writer1 = null,writer2=null;
		try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
    			String line;
    			writer = new PrintWriter("indexrating.txt","UTF-8");
    			writer1 = new PrintWriter("Featureindexes0_5.txt","UTF-8");
    			while ((line = br.readLine()) != null) {
       				float rating = Float.parseFloat(line);
				
				if(rating>=0.5 || rating <= -0.5){
					arr.add(index);
					indexratingrow = index + " "+ rating + " "+find(index);
					writer.println(indexratingrow);
					writer1.println(index);
					indexrating.add(indexratingrow);
					System.out.println(indexratingrow);
				}
				index++;
    			}
    			writer.close();
    			writer1.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		try{
			writer = new PrintWriter("table.txt","UTF-8");
			writer1 = new PrintWriter("tableindexes.txt","UTF-8");
			writer2 = new PrintWriter("FinalFeatures.txt","UTF-8");
		} catch (IOException e){}		
			int tablerow = 0;
			try(BufferedReader rw = new BufferedReader(new FileReader(feat))) {
				String line;
				
				while((line = rw.readLine())!=null){
					String[] tokens = line.split(" ");
					Vector<String> row = new Vector<>();
					Vector<String> wordrow = new Vector<>();
					if(Integer.parseInt(tokens[0])>=7&&pos<500){
						row.add(tokens[0]);
						wordrow.add(tokens[0]);

						for(int j = 1;j<tokens.length;j++){
							String[] idnf = tokens[j].split(":");
							int arrindex;
							if((arrindex = arr.indexOf(Integer.parseInt(idnf[0])))!=-1){
								if(!arrnew.contains(Integer.parseInt(idnf[0]))){
									arrnew.add(Integer.parseInt(idnf[0]));
									writer2.println(idnf[0]);
								}
									
								indexratingrow = indexrating.get(arrindex);
								String[] rowsplit = indexratingrow.split(" ");
								System.out.println(rowsplit[1]);
								// if(Float.parseFloat(rowsplit[1])>1){
									row.add(""+idnf[0]+":"+rowsplit[1]+":"+idnf[1]);
									wordrow.add(find(Integer.parseInt(idnf[0]))+" "+rowsplit[1]);
								// }
																
							}
						}
						table.add(row);
						pos++;
						System.out.print(tokens[0]+"   "+"");
						System.out.println(++tablerow);
						System.out.println(row);
						writer1.println(row);
						writer.println(wordrow);
						


					}
					else if(Integer.parseInt(tokens[0])<=4&&neg<500){
						row.add(tokens[0]);
						wordrow.add(tokens[0]);

						for(int j = 1;j<tokens.length;j++){
							String[] idnf = tokens[j].split(":");
							int arrindex;
							if((arrindex = arr.indexOf(Integer.parseInt(idnf[0])))!=-1){
								if(!arrnew.contains(Integer.parseInt(idnf[0]))){
									arrnew.add(Integer.parseInt(idnf[0]));
									writer2.println(idnf[0]);
								}
									
								indexratingrow = indexrating.get(arrindex);
								String[] rowsplit = indexratingrow.split(" ");
								System.out.println(rowsplit[1]);
								// if(Float.parseFloat(rowsplit[1])<-1){
									row.add(""+idnf[0]+":"+rowsplit[1]+":"+idnf[1]);
									wordrow.add(find(Integer.parseInt(idnf[0]))+" "+rowsplit[1]);
								// }
								
							}
						}
						table.add(row);
						neg++;
						System.out.print(tokens[0]+"   "+"");
						System.out.println(++tablerow);
						System.out.println(row);
						writer1.println(row);
						writer.println(wordrow);
						


					}
					else
						continue;
				}
			} catch (IOException e){}
			writer.close();
			writer1.close();
			writer2.close();
		System.out.println(arr.size());
		System.out.println(arrnew.size());
	}

	public static String find(int id){
		String vocabfile = "/home/sujit/ML/aclImdb/imdb.vocab";
		int index = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(vocabfile))) {
    			String line;
    			while ((line = br.readLine()) != null) {
       				
				if(index==id)
					return line;
				index++;
    			}
		} catch (IOException e){
			e.printStackTrace();
		}
		return null;
	}
}
