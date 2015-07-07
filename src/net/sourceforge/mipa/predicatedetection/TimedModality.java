package net.sourceforge.mipa.predicatedetection;

import org.apache.log4j.Logger;


public class TimedModality extends Composite{

    private static final long serialVersionUID = 2618026284487183097L;

    private NodeType operator;
    
    private String op;
    
    private String bound;
    
    private static Logger logger = Logger.getLogger(TimedModality.class);
    
    public TimedModality(NodeType type, String name) {
        super(type, name);
        // TODO Auto-generated constructor stub
    }
    
    public TimedModality(NodeType type, String name, String operator, String op, String bound) {
        super(type, name);
        setOperator(operator);
        this.op = op;
        this.bound = bound;
        // TODO Auto-generated constructor stub
    }

    public void setOperator(String operator) {
        if(operator.equals(NodeType.CONJUNCTION.toString().toLowerCase())) {
            this.operator = NodeType.CONJUNCTION;
        }
        else {
            System.out.println("Operator "+ operator +" not defined!");
            logger.error("Operator "+ operator +" not defined!");
        }
    }
    
    public void setOperator(NodeType operator) {
        this.operator = operator;
    }

    public NodeType getOperator() {
        return operator;
    }

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getBound() {
		return bound;
	}

	public void setBound(String bound) {
		this.bound = bound;
	}
}
