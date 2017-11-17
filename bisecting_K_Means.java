
package assignment2;

/**
 *
 * @author Ruchika Salwan
 * @author Annu Joshi
 */
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import weka.filters.unsupervised.instance.RemovePercentage;
 import weka.core.Instances;
import weka.clusterers.*;
 import java.io.BufferedReader;
import weka.clusterers.ClusterEvaluation;
 import java.io.FileReader;
 import java.io.FileNotFoundException;

public class bisecting_K_Means {

public static BufferedReader readDataFile(String filename) {
BufferedReader inputReader = null;

try {
inputReader = new BufferedReader(new FileReader(filename));
} catch (FileNotFoundException ex) {
System.err.println("File not found: " + filename);
}

return inputReader;
}


    
public static void main(String[] args) throws Exception {

	 
		BufferedReader datafile = readDataFile("E:/msc/datamining/pendigits.arff"); 
		Instances data = new Instances(datafile);
                   SimpleKMeans kmeans = new SimpleKMeans(); 
                int count_iter=0,K=10;
                
              //accesing private data member of SimpleKMeans 
                Field f = SimpleKMeans.class.getDeclaredField("m_squaredErrors");
                f.setAccessible(true);
         
                Instances[] temp1=new Instances[6];
                double [][]temp2=new double[3][2];
                
                for (int p = 0; p < 6 ; p++) {
	         temp1[p] = new Instances(data, 0);
                   }
                
                 List SSEList = new ArrayList();
                 List cList=new ArrayList();
                 List SList=new ArrayList();
                 
                //removing the class attribute
             
                Remove remove;
                remove = new Remove();
                remove.setAttributeIndices("last");
                remove.setInvertSelection(false);
                remove.setInputFormat(data);
                Instances data1 = Filter.useFilter(data, remove);
                cList.add(new Instances(data1));
                
                
                
                int j,index=0,clist_index=0;
                while(cList.size()!=K ){
                
                count_iter=0;
                j=0;
                SSEList.clear();
                
                while((count_iter)!=3){
                String []options=weka.core.Utils.splitOptions("-I 10");
                kmeans.setOptions(options);
                kmeans.setSeed((int) (Math.random()*100));
                kmeans.setPreserveInstancesOrder(true);
                data1=(Instances) cList.get(clist_index);
                kmeans.buildClusterer(data1);
  
                 int [] assignments= kmeans.getAssignments();
               
                 index=0;
                  for(int clusternum:assignments){
                   if(clusternum==0)
                       temp1[j].add(data1.instance(index));
                   else
                       temp1[j+1].add(data1.instance(index));
                   index++;
                  }
                    j+=2;
                 SSEList.add(new Double(kmeans.getSquaredError()));
                 double [] sse_arr=(double [])f.get(kmeans);
                 temp2[count_iter]=sse_arr;
                 count_iter++;
                 
               }//innerwhile

              int minIndex = SSEList.indexOf(Collections.min(SSEList));
              
              cList.remove(clist_index);
              
              if(cList.size()>0)
                SList.remove(clist_index);
              
               
              switch(minIndex)
              {
                case 0:cList.add(new Instances(temp1[0]));
                      cList.add(new Instances(temp1[1])); 
                      SList.add(temp2[0][0]);
                      SList.add(temp2[0][1]);
                       break;
                case 1:cList.add(new Instances(temp1[2]));
                       cList.add(new Instances(temp1[3]));
                       SList.add(temp2[1][0]);
                       SList.add(temp2[1][0]);
                      break;
                case 2:cList.add(new Instances(temp1[4]));
                       cList.add(new Instances(temp1[5]));
                       SList.add(temp2[2][0]);
                       SList.add(temp2[2][1]);
                      break;
                    
              }
         
              //Gets the cluster index to be spilt
              clist_index = SList.indexOf(Collections.max(SList));
                  
		
        }//outerwhile


   System.out.println("SList"+SList); 
   
   double sum=0;
   for(int k=0;k<K;k++)
       sum+=(double)SList.get(k);
   
     System.out.println("TotalSSE"+sum); 
}
}

