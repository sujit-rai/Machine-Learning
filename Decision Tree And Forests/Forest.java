import java.util.*;
import java.io.*;
import java.math.*;

class ftree{
	ftree left;
	ftree right;
	label node;
	double probability;
	List<Integer> attributes_left;
	Vector<Integer> indexes;
	int splitattribute;
	String splitclass;
	int depth;
}

class label{
	int value;
	int instances;
	int depth;
}

class Forest{
	
	
	static Vector<int[]>  table= new Vector<int[]>();
	static List<Integer> arr = new ArrayList<Integer>();
	static int terminal_nodes = 0;
	static ftree root;
	public static void main(String args[]){
		String featurefile = "/home/sujit/ML/FinalFeatures.txt";
		String tablefile = "/home/sujit/ML/tableindexes.txt";
		PrintWriter writer = null;
		try(BufferedReader br = new BufferedReader(new FileReader(featurefile))){
			String line;
			while((line=br.readLine())!=null){
				int index = Integer.parseInt(line);
				arr.add(index);
			}

		} catch(IOException e){}
		Random rand = new Random(); 
		try(BufferedReader br = new BufferedReader(new FileReader(tablefile))){
				String line;
				String tokens;
				int noise_counter = 0;
				writer = new PrintWriter("finaltable.txt","UTF-8");
				while((line=br.readLine())!=null){
					tokens = line.substring(1,line.length()-1);
					String[] rowelements = tokens.split(", ");
					//System.out.println(Arrays.toString(rowelements));
					if(rowelements.length == 1)
						continue;
					int[] row = new int[arr.size()+1];
					for(int i = 1;i<rowelements.length;i++){
						String[] elements = rowelements[i].split(":");
						int index = arr.indexOf(Integer.parseInt(elements[0]));
						int freq = Integer.parseInt(elements[2]);
						row[index] = freq;
					}
					if(noise_counter >0){
						noise_counter--;
						if(Integer.parseInt(rowelements[0])>=7)
							row[arr.size()] = 0;
						else
							row[arr.size()] = 1;
					}
					else{
						if(Integer.parseInt(rowelements[0])>=7)
							row[arr.size()] = 1;
						else
							row[arr.size()] = 0;
					}
				
					//System.out.println(row);
					writer.println(Arrays.toString(row));
					table.add(row);
				}
		} catch(IOException e){}
		for(int s = 0;s<20;s++){
			terminal_nodes = 0;
			
			
			System.out.println(table.size());
			System.out.println(arr.size());
		

			writer.close();
			root = null;
			root = new ftree();
			root.depth = 0;
			
			root.indexes = new Vector<Integer>();
			root.attributes_left = new ArrayList<Integer>();
			root.splitclass = "";
			for(int i=0;i<300;i++){
				int value = rand.nextInt(arr.size());
				root.attributes_left.add(arr.get(value));
			}
			for(int i=0;i<table.size();i++)
				root.indexes.add(i);
			System.out.println("root indexes size : "+root.indexes.size());
			System.out.println("table size : "+table.size());
			System.out.println("root attributes left : "+root.attributes_left.size());
			ID3(root);
			System.out.println("Number of Terminal Nodes : "+terminal_nodes);
			System.out.println("decision ftree is built completely");
			//System.out.println("store decision ftree ?");
			//Scanner sc = new Scanner(System.in);
			//sc.nextLine();
			String rules = formrules(root,"");
			String rules_file_name = "rules"+s+".txt";
			try {
				writer = new PrintWriter(rules_file_name,"UTF-8");
				writer.println(rules);
				writer.close();
			} catch (IOException e){}



		}
		
		// PrintWriter writer = null;
		// try(BufferedReader br = new BufferedReader(new FileReader(tablefile))){
		// 	String line,tokens;
		// 	writer = new PrintWriter("tablebool.txt","UTF-8");

		// 	Vector<boolean[]> table = new Vector<boolean[]>();
		// 	while((line=br.readLine())!=null){
		// 		tokens = line.substring(1,line.length()-1);
		// 		String[] rowelements = tokens.split(", ");
		// 		System.out.println(rowelements);
		// 		boolean[] row = new boolean[arr.size()+1];
		// 		for(int i = 1;i<rowelements.length;i++){
		// 			row[arr.indexOf(Integer.parseInt(rowelements[i]))] = true;
		// 		}
		// 		if(Integer.parseInt(rowelements[0])>=7)
		// 			row[0] = true;
		// 		else
		// 			row[1] = false;
		// 		System.out.println(Arrays.toString(row));
		// 		table.add(row);
		// 		writer.println(Arrays.toString(row));
		// 	}
		// 	writer.close();

		// } catch(IOException e){}

		
	}

	public static String formrules(ftree node,String rules){
		if(node.left == null)
			return rules + node.splitattribute+ "=" + node.splitclass+","+node.probability+","+node.depth + "t"+ node.node.value +" | "+node.node.depth+":"+node.node.instances+ "\n";
		else if(node.depth == 0)
			rules = "";
		else
			rules = rules + node.splitattribute + "=" + node.splitclass +","+node.probability+","+node.depth+ "&";
		return formrules(node.left,rules) + formrules(node.right,rules);

	}

	public static int splitattribute(ftree node){
		return 0;
	}

	public static void ID3(ftree node){


		int table_pos=0,table_neg = 0,indexes_size;
		indexes_size = node.indexes.size();
		for(int k=0;k<node.indexes.size();k++){

			int review = table.get(node.indexes.get(k))[arr.size()];
			if(review == 1){
				table_pos++;
			}
			else
				table_neg++;


		}

		node.probability = ((double)table_pos)/((double) (table_pos + table_neg));

		//all positive create label positive,return

		System.out.println("total positive labels : "+table_pos);
		if(table_neg == 0){
			label lpos = new label();
			lpos.value = 1;
			lpos.instances = indexes_size;
			lpos.depth = node.depth + 1;
			node.node = lpos;
			node.left = null;
			node.right = null;
			terminal_nodes++;
			return;
		}

		//all negative create label negative, return

		else if(table_pos == 0){
			label lneg = new label();
			lneg.value = 0;
			lneg.instances = indexes_size;
			lneg.depth = node.depth + 1;
			node.node = lneg;
			node.left = null;
			node.right = null;
			terminal_nodes++;
			return;
		}

		

		//if attribute empty, make label using majority vote, return

		if(node.attributes_left.size()==0 || node.probability > 0.75 || node.probability <0.25){
			terminal_nodes++;
			if(table_pos>table_neg){
				label lpos = new label();
				lpos.value = 1;
				node.node = lpos;
				return;
			}
			else{
				label lneg = new label();
				lneg.value = 0;
				node.node = lneg;
				return;
			}
		}

		//find split attribute
		//calculate frequency 

		int[][] frequency_table = new int[3][node.attributes_left.size()];
		int maxig_attr_index = 0,classindex=0,classvalue=0;
		double maxig_attr_value = 0;
		PrintWriter writer1 = null;
		//try{
				//writer1 = new PrintWriter("ig.txt","UTF-8");
		for(int i=0;i<node.attributes_left.size();i++){
			List<Integer> discfreq = new ArrayList<Integer>();
			List<Integer> pos = new ArrayList<Integer>();
			List<Integer> neg = new ArrayList<Integer>();
			//writer1.println("attribute index : "+ i);
			
				for(int j=0;j<node.indexes.size();j++){
				int[] instance = table.get(node.indexes.get(j));
				int attributeindex = arr.indexOf(node.attributes_left.get(i));
				int freq = instance[attributeindex];
				int review = instance[arr.size()];
				if(discfreq.contains(freq)){
					int freqindex = discfreq.indexOf(freq);
					if(review==1)
						pos.set(freqindex, pos.get(freqindex)+1);
					else
						neg.set(freqindex, neg.get(freqindex)+1);
				}
				else{
					discfreq.add(freq);
					if(review==1){
						pos.add(1);
						neg.add(0);
					}						
					else{
						neg.add(1);
						pos.add(0);
					}
				}


				// frequency_table[0][i] = frequency_table[0][i] + freq;
				// frequency_table[1][i] = frequency_table[1][i] + ((review == 1)?1:0);
				// frequency_table[2][i] = frequency_table[2][i] + ((review == 0)?1:0);
				
			}
			//System.out.println(discfreq);
			//System.out.println(pos);
			//System.out.println(neg);

			//sort
			List<Integer> discfreq_s = new ArrayList<Integer>();
			List<Integer> pos_s = new ArrayList<Integer>();
			List<Integer> neg_s = new ArrayList<Integer>();
			
			while(discfreq.size()>0){
				int min = 0;
				for(int j=1;j<discfreq.size();j++){
					if(discfreq.get(j)<discfreq.get(min))
						min = j;
				}
				discfreq_s.add(discfreq.get(min));
				pos_s.add(pos.get(min));
				neg_s.add(neg.get(min));
				discfreq.remove(min);
				pos.remove(min);
				neg.remove(min);
			}
			//System.out.println(discfreq_s);
			//System.out.println(pos_s);
			//System.out.println(neg_s);

			//writer1.println(discfreq_s);
			//writer1.println(pos_s);
			//writer1.println(neg_s);

			//calculate total entropy

			Double positives=0.0,negatives=0.0,total=0.0;
			for(int k=0;k<pos_s.size();k++){
				positives += pos_s.get(k);
			}
			for(int k=0;k<neg_s.size();k++){
				negatives += neg_s.get(k);
			}
			total = positives + negatives;

			//System.out.println(positives+" "+negatives+" "+total);
			//writer1.println("positives : "+positives+" negatives : "+negatives);
			Double prp,prn,entropy;
			prp = positives/total;
			prn = negatives/total;

			entropy = -(prp*(Math.log(prp)/Math.log(2)))-(prn*(Math.log(prn)/Math.log(2)));

			//System.out.println("entropy : "+entropy);
			//writer1.println("entropy : "+entropy);

			//calculate information gain
			//brute force
			double[] ig = new double[pos_s.size()];
			int maxig = 0;
			for(int k=1;k<pos_s.size();k++){
				double leftclass=0.0,rightclass=0.0;
				double posleft=0.0,negleft=0.0,posright=0.0,negright=0.0;
				for(int j=0;j<k;j++){
					leftclass += pos_s.get(j) + neg_s.get(j);
					posleft += pos_s.get(j);
					negleft += neg_s.get(j);
				}
				for(int j=k;j<pos_s.size();j++){
					rightclass += pos_s.get(j) + neg_s.get(j);
					posright += pos_s.get(j);
					negright += neg_s.get(j);
				}
				Double entropyleft=0.0,entropyright=0.0;
				//System.out.println("attribute : " + i);
				//writer1.println("attribute : " + i);
				//System.out.println(leftclass+" "+posleft+" "+posright+" "+rightclass+" "+total);
				//writer1.println(leftclass+" "+posleft+" "+posright+" "+rightclass+" "+total);
				if(posleft==0.0 || negleft==0.0 )
					entropyleft = 0.0;
				else
					entropyleft = -((posleft/leftclass)*(Math.log(posleft/leftclass)/Math.log(2)))-((negleft/leftclass)*(Math.log(negleft/leftclass)/Math.log(2)));
				if(posright==0.0 || negright ==0.0)
					entropyright = 0.0;
				else
					entropyright = -((posright/rightclass)*(Math.log(posright/rightclass)/Math.log(2)))-((negright/rightclass)*(Math.log(negright/rightclass)/Math.log(2)));
				//System.out.println("entropyleft : "+entropyleft+" entropyright : "+entropyright);	
				//writer1.println("entropyleft : "+entropyleft+" entropyright : "+entropyright);	
				ig[k-1] = entropy - (((leftclass/total)*entropyleft) + ((rightclass/total)*entropyright));
				//System.out.println(ig[k-1]);
				//writer1.println(ig[k-1]);
				if(ig[k-1]>ig[maxig])
					maxig = k-1;
			}
			//System.out.println(Arrays.toString(ig));
			//writer1.println(Arrays.toString(ig));
			//System.out.println(maxig);
			//writer1.println(maxig);
			

			if(ig[maxig] > maxig_attr_value){
				maxig_attr_value = ig[maxig];
				maxig_attr_index = i;
				classindex = maxig;
				classvalue = discfreq_s.get(maxig);
			}

			//break;
			


		}

		//}catch(IOException e){}

		System.out.println(maxig_attr_value);
		System.out.println(maxig_attr_index);
		System.out.println(classindex);
		System.out.println(classvalue);
		
		System.out.println("depth : "+node.depth);
		//writer1.close();
		
		

		//create 2 nodes 

		int splitattribute = node.attributes_left.get(maxig_attr_index);
		System.out.println(node.attributes_left.get(maxig_attr_index));

		Vector<Integer> left_indexes = new Vector<Integer>();
		Vector<Integer> right_indexes = new Vector<Integer>();

		for(int i=0;i<node.indexes.size();i++){
			int[] instance = table.get(node.indexes.get(i));
			int attributeindex = arr.indexOf(node.attributes_left.get(maxig_attr_index));
			int freq = instance[attributeindex];
			if(freq<=classvalue){
				left_indexes.add(node.indexes.get(i));
			}
			else
				right_indexes.add(node.indexes.get(i));

		}
		node.attributes_left.remove(maxig_attr_index);
		List<Integer> left_attributes_left = new ArrayList<Integer>();
		List<Integer> right_attributes_left = new ArrayList<Integer>();
		for(int i=0;i<node.attributes_left.size();i++){
			left_attributes_left.add(node.attributes_left.get(i));
			right_attributes_left.add(node.attributes_left.get(i));
		}

		ftree left = new ftree();
		left.indexes = left_indexes;
		left.attributes_left = left_attributes_left;
		left.splitattribute = splitattribute;
		left.splitclass = "l"+classvalue;
		left.depth = node.depth+1;

		ftree right = new ftree();
		right.indexes = right_indexes;
		right.attributes_left = right_attributes_left;
		right.splitattribute = splitattribute;
		right.splitclass = "r"+classvalue;
		right.depth = node.depth+1;


		
		//set value for both nodes and assign to parent node.

		node.left = left;
		node.right = right;

		

		//call ID3 on left node
		Scanner sc = new Scanner(System.in);
		System.out.println("enter to continue, indexsize : ");
		System.out.println(left.indexes.size()+"\n");
		//sc.nextLine();
		ID3(node.left);
		//call ID3 on right node
		System.out.println("enter to continue, indexsize : ");
		System.out.println(right.indexes.size()+"\n");
		//
		ID3(node.right);
	}

}