
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Ishaan Thakker
 * This class is the RoutingTable which holds the information of all the RoutingTableRows
 */
 public class RoutingTable extends Thread implements Serializable{
        
        HashMap<String, ArrayList<RoutingTableRow>> configs = new HashMap<>();
        String local_ip = "";
        public RoutingTable(String local_ip){
            this.local_ip = local_ip;
        }
		
		//This function adds entry of a row in RoutingTable
        public void addEntry(RoutingTableRow rtr){
            
           if(!configs.containsKey(local_ip)){
             
               ArrayList<RoutingTableRow> r = new ArrayList<>();
               r.add(new RoutingTableRow(rtr.getDestination(), rtr.getHop(), rtr.getCost()));
               configs.put(local_ip, r);
           }else if(configs.get(local_ip).size()>2){
               System.out.println("Can be connected to only 2 routers");
               return;
           }else{
               ArrayList<RoutingTableRow> r = configs.get(local_ip);
               r.add(new RoutingTableRow(rtr.getDestination(), rtr.getHop(), rtr.getCost()));
               configs.put(local_ip, r);
               
           }
            
        }
		/*
		 * This function performs CIDR over IP.
		 * @param none
		 * @return String value of the IP in CIDR format
		 *
		*/
        static public String getIpPrefix(String ip){
			String subnetmask = "255.255.255.0";
			String subnets[] = subnetmask.split("\\.");
			String ipPrefix[] = new String[4];
			
			String a = Integer.toBinaryString(Integer.parseInt(subnets[0]));
			String b = Integer.toBinaryString(Integer.parseInt(subnets[1]));
			String c = Integer.toBinaryString(Integer.parseInt(subnets[2]));
			String d = Integer.toBinaryString(Integer.parseInt(subnets[3]));
			int count = 0;
			if(!a.equals("0")){
				count+= a.length();
			}
			if(!b.equals("0")){
				count+= b.length();
			}
			if(!c.equals("0")){
				count+=c.length();
			}
			if(!d.equals("0")){
				count+=d.length();
			}
			String prefix = ip+"/"+count;
				return prefix;
			}
			
			/**
			 * This function prints the Routing Table
			 * @return none
			 * @param none
			 */
			synchronized public void printRoutingTable(){
            Iterator it = configs.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
				System.out.println("**********************Routing Table*********************");                
                ArrayList<RoutingTableRow> row = configs.get(local_ip);
                for(RoutingTableRow r: row){
                    if(!r.getFailed()){
		    	System.out.println();
		    	System.out.println();
		    	System.out.println("Routing Information: ");
		    	System.out.println("Source: "+getIpPrefix(pair.getKey()+""));
		    	System.out.println("Destination: "+getIpPrefix(r.getDestination()));
		    	System.out.println("Hop: "+getIpPrefix(r.getHop()));
		    	System.out.println("Cost: "+r.getCost());
				}
            }
          }
           
        }
        
    }

