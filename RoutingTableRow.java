
import java.io.Serializable;

/**
 *
 * @author Ishaan Thakker
 * This is the class which stores information related to destination, hop, cost in a row
 *
 */
    public class RoutingTableRow implements Serializable{
        String destination, hop, prefix, type, cause;
        double cost;
		boolean failed;
		boolean old;
        public RoutingTableRow() {
        }
		//Constructor Class to initialize the attributes
        public RoutingTableRow(String destination, String hop, double cost) {
            this.destination = destination;
            this.hop = hop;
            this.cost = cost;
			this.prefix = prefix;
			this.failed = false;
			this.type = "normal";
			this.old = false;
			this.cause = "";
        }
		public String getCause(){
		return cause;
		}
		public void setCause(String cause){
			this.cause = cause;
		}
		public boolean getOld(){
			return old;
		}

		public void setOld(boolean old){
			this.old = old;
		}
		public boolean getFailed(){
			return failed;
		}
		public void setFailed(boolean failed){
			this.failed = failed;
		}
		public String getType(){
			return type;
		}
		public void setType(String type){
			this.type = type;
		}
		public String getPrefix(){
			return prefix;
		}

		public void serPrefix(String prefix){
			this.prefix = prefix;
		}
        public String getDestination() {
            return destination;
        }

        public String getHop() {
            return hop;
        }

        public double getCost() {
            return cost;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public void setHop(String hop) {
            this.hop = hop;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }
        
    }

