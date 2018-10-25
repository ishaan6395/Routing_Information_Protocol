/**
 *@author: Ishaan Thakker
 *Program: To implement Distance Vector Routing
*/
import java.util.*;
import java.net.*;
import java.io.*;
public class router2 extends Thread{
    
	//Initialize Variables
    static String local_ip = "";
    static ArrayList<String> ip_addresses = new ArrayList<>();
    static ArrayList<Integer> ports = new ArrayList<>();
    static ArrayList<Integer> ports2 = new ArrayList<>();
    static RoutingTable rt;
    static String subnetmask = "255.255.255.0";    
    static ArrayList<String> neighbours = new ArrayList<>();
   
    /**
	 * This is the main method
	 * @param File name in String format
	 * @return none
	 */
	public static void main(String args[]) throws Exception{
        
		Scanner sc = new Scanner(System.in);
        File f = new File(args[0]);
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        local_ip = InetAddress.getLocalHost().toString().split("/")[1];
        ArrayList<RoutingTableRow> rtr = new ArrayList<>();
        int count = 0;
	
		router2 thisrouter = new router2();
		
		// to limit the number of routers to 2
        while(line != null){
            count++;
			if(count>2){
			System.out.println("A router can have only two neighbours at max. Please change the configuration file");
			System.exit(0);

			}
            String[] content = line.split(",");
            if(!ip_addresses.contains(content[0])){
                ip_addresses.add(content[0]);
            }
			String dest0 = thisrouter.getIpPrefix(content[0]); 
            String dest = (content[0]);
            double cost = Double.parseDouble(content[1]);
            rtr.add(new RoutingTableRow(dest, dest, cost));
            ports.add(Integer.parseInt(content[2]));
			ports2.add(Integer.parseInt(content[3]));
            line = br.readLine();
			neighbours.add(content[0]);
        }
        
       rt = new RoutingTable(local_ip);
        for(RoutingTableRow r: rtr){
            rt.addEntry(r);
        }
       System.out.println(ip_addresses); 
        for(int idx = 0; idx<ip_addresses.size(); idx++){
            new Sender(ip_addresses.get(idx), ports.get(idx), ports2.get(idx)).start();
	    new Receiver(ip_addresses.get(idx), ports.get(idx), ports2.get(idx)).start();           
       	    System.out.println("The ip address is :"+ip_addresses.get(idx));
       	}
        thisrouter.start();
        
    }

    public String getIpPrefix(String ip){
		String octets[] = ip.split("\\.");
		String subnets[] = subnetmask.split("\\.");
		String ipPrefix[] = new String[4];
		
		ipPrefix[0] = Integer.toString(Integer.parseInt(octets[0])&Integer.parseInt(subnets[0]));
		ipPrefix[1] = Integer.toString(Integer.parseInt(octets[1])&Integer.parseInt(subnets[1]));
		ipPrefix[2] = Integer.toString(Integer.parseInt(octets[2])&Integer.parseInt(subnets[2]));
		ipPrefix[3] = Integer.toString(Integer.parseInt(octets[3])&Integer.parseInt(subnets[3]));
		String prefix = ipPrefix[0]+"."+ipPrefix[1]+"."+ipPrefix[2]+"."+ipPrefix[3];
		return prefix;
    }
    public void run(){
		try{
			// Prints the RoutingTable every 1 Second.
			while(true){

				Thread.sleep(1000);
				rt.printRoutingTable();
				}
			}catch(Exception e){
			System.out.println("In main router run method: "+e.getMessage());
			}
    } 
	
	//Sender Class which keeps on forwarding the routing table
    static class Sender extends Thread{
        
        String destination;
		int port_index ;
		int port_2;
        public Sender(String destination, int port_index, int port_2){
         
        this.destination = destination;
	    this.port_index = port_index;
	    this.port_2 = port_2;
        }

	
		
	
		public void run(){
			try{
			HashMap<String, ArrayList<RoutingTableRow>> map = new HashMap<>();
			RoutingTable sendthis = new RoutingTable(local_ip);
			ArrayList<RoutingTableRow> toforwardrow1 = new ArrayList<RoutingTableRow>();

			ArrayList<RoutingTableRow> temp = rt.configs.get(local_ip);
			System.out.println(temp.size());
			for(RoutingTableRow tr: temp){
				if(!tr.getDestination().equals(destination)){
				System.out.println("Here");	
				toforwardrow1.add(new RoutingTableRow(tr.getDestination(), tr.getHop(), tr.getCost()));
				}
					
			}
				
			//System.out.println(toforwardrow1.size());	
			for(RoutingTableRow addthis: toforwardrow1){
				sendthis.addEntry(addthis);
			}
			
			//sendthis.printRoutingTable();
					//Receive a packet for port
					DatagramSocket ds = new DatagramSocket();
					
			while(true){
			Thread.sleep(1000);	
			System.out.println("In sender Thread");
					//Send the Serialized object over network
					ArrayList<RoutingTableRow> split_horizon_list = new ArrayList<>();
			ArrayList<RoutingTableRow> temp_list = rt.configs.get(local_ip);
			for(RoutingTableRow tt: temp_list){
				if(!tt.getHop().equals(destination)){
					split_horizon_list.add(tt);
				}
			}
			RoutingTable tosend = new RoutingTable(local_ip);
			tosend.configs.put(local_ip, split_horizon_list);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					ObjectOutputStream os = new ObjectOutputStream(outputStream);
					os.writeObject(tosend);
					os.flush();
					byte[] Buf= outputStream.toByteArray();
					ByteArrayInputStream in = new ByteArrayInputStream(Buf);
					ObjectInputStream is = new ObjectInputStream(in);
					RoutingTable rt1 = (RoutingTable)is.readObject();
			//System.out.println("The Destination HERE is :"+destination);
					DatagramPacket dp1 = new DatagramPacket(Buf,Buf.length,
							InetAddress.getByName(destination), port_2);
					ds.send(dp1);
			//rt.printRoutingTable();
			}
				}catch(Exception e){
					System.out.println("Sender: "+e.getMessage());
				}
			}
    }
	//Received Class which receives the routing table from neighbours and keeps on updating accordingly
     static class Receiver extends Thread{
		String ipAddr;
		int port_no;
		int port_2;
			public Receiver(String ipAddr, int port_no, int port_2){
			this.ipAddr = ipAddr;
				this.port_no = port_no;
			this.port_2 = port_2;
			}

			public void run(){
			
				try{
								
					DatagramSocket ds = new DatagramSocket(port_2);
					ds.setSoTimeout(20000);
					String s = "";
					byte[] b1 = s.getBytes();
					DatagramPacket temp = new DatagramPacket(b1, b1.length,
							InetAddress.getByName(ipAddr), port_no);
					ds.send(temp);
					while(true){
					//System.out.println("In receiver Thread");
					byte b[] = new byte[1024];
					DatagramPacket dp =  new DatagramPacket(b, b.length);
					ds.receive(dp);
					byte[] data = dp.getData();
					ByteArrayInputStream in = new ByteArrayInputStream(data);
					ObjectInputStream is = new ObjectInputStream(in);
					RoutingTable rt_received = (RoutingTable) is.readObject();
				
					updateTable(rt_received, ipAddr);
					updateForFailed(rt_received, ipAddr);	
	
		
                }
            }catch(Exception e){
				HashMap<String,ArrayList<RoutingTableRow>> host_table = rt.configs;
				ArrayList<RoutingTableRow> hostrows= rt.configs.get(local_ip); 
                for(RoutingTableRow rows: hostrows){
				if(rows.getDestination().equals(ipAddr)){
					rows.setFailed(true);
					rows.setCause(ipAddr);
				}
		}
			host_table.put(local_ip, hostrows);
			rt.configs = host_table;
	
		//System.out.println("Receiver: "+e.getMessage());
            }
        
	}
	
		/**
		 *This function will update the Routers detecting the Failed Node
		*/
      public void updateForFailed(RoutingTable rt_received, String ipAddr){
	
	      
		     HashMap<String, ArrayList<RoutingTableRow>> host_put = new HashMap<>();
		ArrayList<RoutingTableRow> host_rows = rt.configs.get(local_ip);
		ArrayList<RoutingTableRow> received_rows = rt_received.configs.get(ipAddr);
		System.out.println(received_rows.size());
		for(RoutingTableRow hr: host_rows){
			for(RoutingTableRow rr: received_rows){
				if(rr.getDestination().equals(hr.getDestination())){
					if(rr.getFailed()){
						if((rr.getCause().equals(rr.getHop()))){
							hr.setFailed(true);
							
						}
					}
				}
			}
		}

		host_put.put(local_ip, host_rows);
		rt.configs = host_put;
     }

	/*
	 * This table will update the route if any better route is available to the router
	*/
     synchronized public void updateTable(RoutingTable rt_received, String ipAddr){
	 //Getting Routing Table information for current host
	 
		HashMap<String,Double> host_ips = new HashMap<>();
		HashMap<String, Double> incoming_ips = new HashMap<>();
		boolean update = false;
        HashMap<String, ArrayList<RoutingTableRow>> host_configs = rt.configs;
        Iterator host_it = host_configs.entrySet().iterator();
        while(host_it.hasNext()){
                 Map.Entry pair = (Map.Entry)host_it.next();
                 ArrayList<RoutingTableRow> host_row = host_configs.get(local_ip);
                 for(RoutingTableRow hostr: host_row){
				if(!hostr.getDestination().equals(""))
                         host_ips.put(hostr.getDestination(), hostr.getCost());
                 }
		}
                //Ending extraction of information
        
				//Computations for cost and new path
 
			HashMap<String, ArrayList<RoutingTableRow>> configs = rt_received.configs;
			Iterator it = configs.entrySet().iterator();
			while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                ArrayList<RoutingTableRow> row = configs.get(ipAddr);
			for(RoutingTableRow r: row){
				if(!r.getDestination().equals(""))
				incoming_ips.put(r.getDestination(), r.getCost());
			}
          }
         //Computation end
	 
		//Iterate for HashMap of incoming_ips and costs

		 Iterator it2 = incoming_ips.entrySet().iterator();
		 while(it2.hasNext()){
			Map.Entry pair = (Map.Entry)it2.next();
			if(!pair.getKey().equals(local_ip)){
				// If host does not contains key add it in the routing table and add the cost
				if(!host_ips.containsKey(pair.getKey())){
					rt.addEntry(new RoutingTableRow((String)pair.getKey(), ipAddr, (double)pair.getValue() + host_ips.get(ipAddr)));
					update = true;
				}
				//check if this cost is less than current cost or not.
				else{
				
					double new_cost = (double)pair.getValue() + host_ips.get(ipAddr);
					double current_cost = host_ips.get(pair.getKey());
					if(new_cost< current_cost){
						ArrayList<RoutingTableRow> temp_row = rt.configs.get(local_ip);
						int ind = -1;
						ArrayList<RoutingTableRow> new_list = new ArrayList<>();
						for(RoutingTableRow tempr: temp_row){
							RoutingTableRow forold= new RoutingTableRow(tempr.getDestination(), tempr.getHop(), tempr.getCost());
							
							if(tempr.getDestination().equals((String)pair.getKey())){
								new_list.add(new RoutingTableRow((String)pair.getKey(), ipAddr, new_cost));								
							}else{
								new_list.add(new RoutingTableRow(tempr.getDestination(), tempr.getHop(), tempr.getCost()));
							}
						}

						rt.configs.put(local_ip, new_list);
						update=  true;
				}
				
			}	
		
		}
	 }
		 if(update)
			System.out.println("This is a Triggered Update");


		}

    }

    

}

