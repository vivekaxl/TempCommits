//public class Snippet {
//	public static void main(String[] args) {
//		if(cn == null){
//				String driver = "com.mysql.jdbc.Driver"; 
//				Class.forName(driver);
//				dbHost = "jdbc:mysql://"+dbHost+a;
//				cn = DriverManager.getConnection(dbHost,dbUser,dbPassword);
//				System.out.println("test");
//				}
//	}
//}
//class abc(){
//	static void test(){
//	RiWordnet wordnet = new RiWordnet(null);
//		
//			String pos;
//		
//		List<String> listStart = FindSubstring(wordnet,start);
//			List<String> listEnd = FindSubstring(wordnet, end);
//			List<Float> distance = new ArrayList<Float>();
//			for(String s:listStart)
//				for(String e:listEnd){
//					pos = wordnet.getBestPos(s);
//					dist = 1- wordnet.getDistance(s,e,pos);
//					distance.add(dist);
//				}
//			return Collections.max(distance);
//	}
//}

//import java.io.BufferedReader;
//import java.util.ArrayList;
//import java.util.List;
//		 				class abc{
//		 				public void test(){
//		 					RiWordnet wordnet = new RiWordnet(null);
//		 				
//		 					String pos;
//		 				
//		 				List<String> listStart = FindSubstring(wordnet,start);
//		 					List<String> listEnd = FindSubstring(wordnet, end);
//		 					List<Float> distance = new ArrayList<Float>();
//		 					for(String s:listStart)
//		 						for(String e:listEnd){
//		 							pos = wordnet.getBestPos(s);
//		 							dist = 1- wordnet.getDistance(s,e,pos);
//		 							distance.add(dist);
//		 						}
//		 					return Collections.max(distance);
//		 				}
//		 				}