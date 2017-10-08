import java.util.*;
import java.io.*;

class tree{
	tree left;
	tree right;
	double probability;
	int attr;
	int lcond;
	int rcond;
	label lnode;
	label rnode;
}

class label{
	int value;
}

class Testtree{
	static String rules;
	public static void main(String args[]){
		String file = args.length>0?args[0]:"test";
		System.out.println(System.getProperty("user.dir"));
		String rulefile = System.getProperty("user.dir") + "/rules.txt";
		String featfile = System.getProperty("user.dir") + "/aclImdb/" + file + "/labeledBow.feat";
		String featurefile = System.getProperty("user.dir")+"/FinalFeatures.txt";
		List<Integer> arr = new ArrayList<Integer>();
		int nodes = 0;
		PrintWriter writer = null;
		tree root=null,temp=null;

		try(BufferedReader br = new BufferedReader(new FileReader(featurefile))){
			String line;
			while((line=br.readLine())!=null){
				int index = Integer.parseInt(line);
				arr.add(index);
			}

		} catch(IOException e){}

		int[] splitattr = new int[arr.size()];
		for(int i = 0;i<arr.size();i++){
			splitattr[i] = 0;
		}

		try(BufferedReader br = new BufferedReader(new FileReader(rulefile))){
			String line;
			tree node = new tree();
			node.left = null;
			node.right = null;
			root = node;
			nodes++;
			while((line=br.readLine())!=null){
				if(line.length()==0)
					continue;
				node = root;
				String[] tokens = line.split("&");
				// System.out.println(line);
				for(int i = 0;i<tokens.length-1;i++){
					String[] split = tokens[i].split("=");
					String[] attr = split[1].split(",");
					int attr_value = Integer.parseInt(split[0]);
					System.out.println(arr.indexOf(attr_value));
					System.out.println(arr.size());
					
					double probability = Double.parseDouble(attr[1]);
					if(attr[0].charAt(0) == 'l' && node.left == null){
						node.lcond = Integer.parseInt(attr[0].substring(1));
						node.left = new tree();
						node.attr = attr_value;
						node.probability = probability;
						node.left.left = null;
						node.left.right = null;
						nodes++;
						node = node.left;
						if(splitattr[arr.indexOf(attr_value)] == 0)
							splitattr[arr.indexOf(attr_value)] = 1;
						splitattr[arr.indexOf(attr_value)] = splitattr[arr.indexOf(attr_value)] + 1;
					}
					else if (attr[0].charAt(0)=='r'&& node.right ==null){
						node.rcond = Integer.parseInt(attr[0].substring(1));
						node.right = new tree();
						node.attr = attr_value;
						node.probability = probability;
						node.right.right = null;
						node.right.left = null;
						node = node.right;
						nodes++;
						if(splitattr[arr.indexOf(attr_value)] == 0)
							splitattr[arr.indexOf(attr_value)] = 1;
						splitattr[arr.indexOf(attr_value)] = splitattr[arr.indexOf(attr_value)] + 1;
					}
					else if(attr[0].charAt(0)=='r'&&node.right !=null){
						node = node.right;
					}
					else
						node = node.left;
				}
				// System.out.println(Arrays.toString(tokens));
				// System.out.println(tokens[tokens.length-1]);
				String[] split = tokens[tokens.length-1].split("=");
				System.out.println(Arrays.toString(split));
				String[] attr = split[1].split(",");
				String[] lvalue = attr[2].split("t");
				int attr_value = Integer.parseInt(split[0]);
				if(splitattr[arr.indexOf(attr_value)] == 0)
					splitattr[arr.indexOf(attr_value)] = 1;
				splitattr[arr.indexOf(attr_value)] = splitattr[arr.indexOf(attr_value)] + 1;
				double probability = Double.parseDouble(attr[1]);

				if(attr[0].charAt(0) == 'l'){
					node.lcond = Integer.parseInt(attr[0].substring(1));
					node.probability = probability;
					node.attr = attr_value;
					node.lnode = new label();
					node.lnode.value = Integer.parseInt(""+lvalue[1].charAt(0));
					nodes ++;
				}

				else if (attr[0].charAt(0) == 'r'){
					node.rcond = Integer.parseInt(attr[0].substring(1));
					node.probability = probability;
					node.attr = attr_value;
					node.rnode = new label();
					node.rnode.value = Integer.parseInt(""+lvalue[1].charAt(0));
					nodes ++;
				}
				
			}
		} catch(IOException e){}
		temp = root;
		preorder(temp);

		// formrules(temp);
		
		// try {
		// 	writer = new PrintWriter("formedtreerules.txt","UTF-8");
		// 	writer.println(rules);
		// 	writer.close();
		// 	return;
		// } catch (IOException e){}

		

		// TEST

		

		try(BufferedReader br = new BufferedReader(new FileReader(featfile))){
			String line;
			int total=0,correct=0,elsecases=0;
			while((line=br.readLine())!=null){
				int treelabel;
				String[] tokens = line.split(" ");
				int[] freq = new int[arr.size()];
				total ++;
				System.out.println("total : "+total);
				int rating = (Integer.parseInt(tokens[0])>=7)?1:0;
				for(int i=1;i<tokens.length;i++){
					String[] idnf = tokens[i].split(":");
					if(arr.contains(Integer.parseInt(idnf[0]))){
						freq[arr.indexOf(Integer.parseInt(idnf[0]))] = Integer.parseInt(idnf[1]);
					}
				}
				temp = root;
				while(temp!=null){
					//if index is present then go to left or right
					if(arr.contains(temp.attr)){
						if(freq[arr.indexOf(temp.attr)]<=temp.lcond){
							if(temp.left!=null){
								temp = temp.left;
								continue;
							}
							treelabel = temp.lnode.value;
							//System.out.println("left treelabel : "+treelabel);
							//System.out.println("rating : "+rating);
						}
						else{
							if(temp.right!=null){
								temp = temp.right;
								continue;
							}							
							treelabel = temp.rnode.value;
							//System.out.println("right treelabel : "+treelabel);
							//System.out.println("rating : "+rating);
						}
					}
					//else return label with max prob
					else{
						treelabel = (temp.probability>=0.5)?1:0;
						elsecases++;
						//System.out.println("inside else : "+treelabel);
						
					}

					//if label is correct increase correct
					if(treelabel == rating){
						correct++;
						System.out.println("correct : "+correct);
						//System.out.println("found");
						System.out.println("accurracy : "+((double)correct/(double)total));
						System.out.println("elsecase : "+elsecases);
					}
					temp = null;
					//calculate accuracy and print
					
				}
			}

		} catch(IOException e){}

		for(int j=0;j<5;j++){
			int max = 0;
			for(int i = 1;i<arr.size();i++){
				if(splitattr[i]>splitattr[max]){
					max = i;
				}
							
			}
			System.out.println("splitattr : "+arr.get(max));
			splitattr[max] = 0;
		}

		System.out.println("number of nodes : "+nodes);


		
	}

	public static void formrules(tree node){

		rules = rules +" "+ node.attr;
		if(node.left == null){
			rules = rules + " " + node.attr + " "+ node.lnode.value + "\n";
			return;
		}
		else if(node.right == null){
			rules = rules + " " + node.attr + " " + node.rnode.value + "\n";
			return;
		}
		formrules(node.left);
		formrules(node.right);
	}

	public static void preorder(tree root){
		
	}
}